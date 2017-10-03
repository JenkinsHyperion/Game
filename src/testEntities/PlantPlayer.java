package testEntities;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import Input.KeyCommand;
import engine.MovingCamera;
import entities.Player;
import entityComposites.Collider;
import entityComposites.EntityStatic;
import entityComposites.TranslationComposite;
import entityComposites.TranslationComposite.VelocityVector;
import misc.CollisionEvent;
import physics.Boundary;
import physics.BoundaryCircular;
import physics.BoundaryFeature;
import physics.BoundaryPolygonal;
import physics.BoundarySingular;
import physics.Collision;
import physics.CollisionBuilder;
import physics.Force;
import physics.SeparatingAxisCollector.Axis;
import physics.Vector;
import physics.VisualCollisionCheck;
import sprites.RenderingEngine;
import sprites.Sprite;

public class PlantPlayer extends Player {

	private final ClimbingState climbing = new ClimbingState();
	private final MovingState movingState = new MovingState();
	private final StandingState standing = new StandingState();
	private final FallingState falling = new FallingState();
	private State currentState = falling;
	private State bufferState = standing;
	
	private boolean isClimbing = false;
	
	private Force movementForce;
	
	private Force gravity;
	
	public PlantPlayer(int x, int y ) {
		super(x, y);
		
		this.addGraphicTo( new Sprite.Stillframe("box.png", Sprite.CENTERED) );
		
		Boundary boundary = new BoundarySingular( new Event() );
		Boundary boundary2 = new BoundaryCircular( 40 , new Event() );
		Boundary boundary3 = new BoundaryPolygonal.Box( 200,200,-100,-100 );
		
		this.addInitialColliderTo( boundary2 );
		this.addTranslationTo();
		
		this.addAngularComposite();
		
		this.movementForce = this.getTranslationComposite().addForce( new Vector(0,0) );
		
		this.inputController.createKeyBinding(KeyEvent.VK_LEFT, new KeyCommand(){
			@Override
			public void onPressed() {
				currentState.onLeft();
			}
			@Override
			public void onReleased() {
				currentState.offLeft();
			}
		});
		this.inputController.createKeyBinding(KeyEvent.VK_RIGHT, new KeyCommand(){
			@Override
			public void onPressed() {
				currentState.onRight();
			}
			@Override
			public void onReleased() {
				currentState.offRight();
			}
		});
		this.inputController.createKeyBinding(KeyEvent.VK_SPACE, new KeyCommand(){
			@Override
			public void onPressed() {
				currentState.onJump();
			}
			@Override
			public void onReleased() {
				currentState.offJump();
			}
		});
		
		this.getColliderComposite().setLeavingCollisionEvent( new CollisionEvent(){
			@Override
			public void run(BoundaryFeature source, BoundaryFeature collidingWith, Vector normal) {
				movementForce.setVector(Vector.zeroVector);
				changeState(falling);
			}
		});
		
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		this.currentState.run();
	}
	
	public void setGravity( Force gravity){
		this.gravity = gravity;
	}

	public static class ClingCollision extends CollisionBuilder<PlantPlayer,PlantTwigSegment>{

		@Override
		public Collision createVisualCollision(PlantPlayer player, Collider playerCollider, PlantTwigSegment plantSegment, Collider collider2, VisualCollisionCheck check, RenderingEngine engine) {
			
			return new Collision.CustomType<PlantPlayer, PlantTwigSegment>( player , playerCollider , plantSegment, collider2 ){

				private TranslationComposite trans = player.getTranslationComposite();
				private VelocityVector clingVelocity = trans.addVelocityVector(Vector.zeroVector);

				@Override
				public void updateVisualCollision(MovingCamera camera, Graphics2D gOverlay) {
					
					isComplete = !check.check(collidingPrimary, collidingSecondary);

					//Axis[] axes = check.getAxisCollector().getSeparatingAxes(entityPrimary, entityPrimary.getPosition(), playerCollider.getBoundary(), 
					//		entitySecondary, entitySecondary.getPosition(), collider2.getBoundary() );
					
					//clingVelocity.setVector( axes[0].getNearFeaturePrimary().getNormal().unitVector().multiply(0.1) );

				}

				@Override
				protected void initializeCollision() {
					
					Axis[] axes = check.getAxisCollector().getSeparatingAxes(entityPrimary, entityPrimary.getPosition(), playerCollider.getBoundary(), 
							entitySecondary, entitySecondary.getPosition(), collider2.getBoundary() );
					
					if ( axes[0].getNearFeatureSecondary().debugIsSide() ){

						if ( !player.isClimbing ){
							
							player.climbing.setSegment( plantSegment , axes[0].getNearPointSecondary() );
							
							player.changeState(player.climbing);
							plantSegment.deactivateOrganism();
						}
					}
				}

				@Override
				public void completeCollision() {
					
					trans.removeVelocityVector(clingVelocity);
					//trans = null;
				}
			};

		}			
	}
	
	private class Event extends CollisionEvent{
		@Override
		public void run(BoundaryFeature source, BoundaryFeature collidingWith, Vector separation) {
			changeState(bufferState);
			System.out.println("HITTING GROUND");
		}
		@Override
		public String toString() {
			return "Hit Something Event";
		}
	}
	
	private void changeState( State state ){
		System.err.println("CHANGE STATE "+currentState+" TO "+state);
		this.currentState.onLeavingState();
		this.currentState = state;
		state.onChange();
	}
	
	private abstract class State implements Runnable{
		public void onChange(){}
		public void onLeavingState(){}
		public void onLeft(){}
		public void offLeft(){}
		public void onRight(){}
		public void offRight(){}
		public void onJump(){}
		public void offJump(){}
	}
	
	private class ClimbingState extends State{

		private Force clingNormal;
		PlantTwigSegment segment;
		Point2D attatchPoint;
		
		public void setSegment( PlantTwigSegment stem, Point2D attatchPoint ){
			this.segment = stem;
			this.attatchPoint = attatchPoint;
		}
		
		public void onChange(){
			isClimbing = true;
			clingNormal = PlantPlayer.this.getTranslationComposite().addNormalForce(gravity.getVector().inverse());
			
			PlantPlayer.this.setPos( attatchPoint );
			
			
			PlantPlayer.this.getTranslationComposite().halt();
		}
		
		@Override
		public void run() {
			clingNormal.setVector(gravity.getVector().inverse());
		}
		
		@Override
		public void onLeavingState() {
			isClimbing = false;
			PlantPlayer.this.getTranslationComposite().removeNormalForce(clingNormal);
		}

	}
	
	private class StandingState extends State{
		@Override
		public void run() {}
		@Override
		public void onLeft() {
			changeState(movingState);
			movingState.leftRight = 1;
		}
		@Override
		public void onRight() {
			changeState(movingState);
			movingState.leftRight = -1;
		}
		@Override
		public void onJump() {
			//movementForce.setVector( gravity.getVector().unitVector().multiply(-1) );
			//trans.addVelocity( gravity.getVector().unitVector().multiply(-5) );
		}
		@Override public void offLeft(){};
		@Override public void offRight(){};
		@Override public void offJump(){};
	}
	
	private class FallingState extends State{
		@Override
		public void run() {}
		@Override public void onLeft(){};
		@Override public void offLeft(){ }
		@Override public void onRight(){};
		@Override public void offRight(){ }
		@Override public void onJump(){};
		@Override public void offJump(){};
		
	}
	
	private class MovingState extends State{
		
		protected byte leftRight;
		
		private final Runnable accelerating = new Runnable(){
			public void run(){
				
				movementForce.setVector( gravity.getVector().normalLeft().unitVector().multiply(0.1*leftRight) );
			}
		};
		
		private final Runnable coasting = new Runnable(){
			public void run(){}
		};
		
		private Runnable currentMovementState = accelerating;
		
		@Override
		public void run() {
			currentMovementState.run();
		}
		@Override
		public void offRight() {
			movementForce.setVector(0,0);
			currentMovementState = coasting;
		}
		@Override
		public void offLeft() {
			movementForce.setVector(0,0);
			currentMovementState = coasting;
		}
		@Override
		public void onJump() {
			getTranslationComposite().addVelocity( gravity.getVector().inverse().unitVector().multiply(5) );
			bufferState = movingState;
		}
		
		@Override public void onLeft(){
			if ( leftRight < 0 )
				leftRight = 1;
			
			currentMovementState = accelerating;
		};
		@Override public void onRight(){
			if( leftRight > 0 )
				leftRight = -1;

			currentMovementState = accelerating;
		};
		@Override public void offJump(){};
	}
	
}
