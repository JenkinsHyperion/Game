package testEntities;

import java.awt.Color;
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
import utility.DoubleLinkedList;
import utility.ListNodeTicket;

public class PlantPlayer extends Player {

	private final ClimbingState climbing = new ClimbingState();
	private final MovingState movingState = new MovingState();
	private final StandingState standing = new StandingState();
	private final FallingState falling = new FallingState();
	private State currentState = falling;
	private State bufferState = standing;
	
	private boolean canStartClimb = true;
	
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
			
			@Override
			public void onHeld() {
				currentState.holdingLeft();
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
			@Override
			public void onHeld() {
				currentState.holdingRight();
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
			@Override
			public void onHeld() {
				currentState.holdingJump();
			}
		});
		this.inputController.createKeyBinding(KeyEvent.VK_UP, new KeyCommand(){
			@Override
			public void onPressed() {
				currentState.onUp();
			}
			@Override
			public void onReleased() {
				currentState.offUp();
			}
			@Override
			public void onHeld() {
				currentState.holdingUp();
			}
		});
		this.inputController.createKeyBinding(KeyEvent.VK_DOWN, new KeyCommand(){
			@Override
			public void onPressed() {
				currentState.onDown();
			}
			@Override
			public void onReleased() {
				currentState.offDown();
			}
			@Override
			public void onHeld() {
				currentState.holdingDown();
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
	
	public void debugDraw(  MovingCamera cam , Graphics2D g2 ){
		this.currentState.debugDraw(cam, g2);
	}
	
	public void debugCollisions( MovingCamera cam , Graphics2D g2 ){
		for( int i = 0 ; i < this.getColliderComposite().getCollisions().length ; ++i ){
			Collision collision = this.getColliderComposite().getCollisions()[i];
			g2.drawString( collision.toString(), 10, 400+(i*15));
		}
	}

	public static class ClingCollision extends CollisionBuilder<PlantPlayer,PlantTwigSegment>{

		@Override
		public Collision createVisualCollision(PlantPlayer player, Collider playerCollider, PlantTwigSegment plantSegment, Collider collider2, VisualCollisionCheck check, RenderingEngine engine) {
			
			return new Collision.CustomType<PlantPlayer, PlantTwigSegment>( player , playerCollider , plantSegment, collider2 ){

				private ListNodeTicket segmentOnPlayer;
				private Vector clingVector;
				
				@Override
				public void updateCollision() {
					isComplete = !check.check(collidingPrimary, collidingSecondary);
				}
				
				@Override
				public void updateVisualCollision(MovingCamera camera, Graphics2D gOverlay) {
					isComplete = !check.check(collidingPrimary, collidingSecondary,camera,gOverlay);
					gOverlay.setColor(Color.RED);
					gOverlay.drawString("Test drawing" , 300,300);
				}

				@Override
				protected void initializeCollision() {
					
					Axis[] axes = check.getAxisCollector().getSeparatingAxes(playerCollider, entityPrimary.getPosition(), playerCollider.getBoundary(), 
							collider2, entitySecondary.getPosition(), collider2.getBoundary() );

					clingVector = axes[0].getNearFeatureSecondary().getNormal().unitVector();
					
					segmentOnPlayer = player.climbing.addSegment( plantSegment , clingVector );
					
					if ( player.canStartClimb ){		
						
						player.changeState(player.climbing);
					}
					
				}

				@Override
				public void completeCollision() {
					segmentOnPlayer.removeSelfFromList();
				}

			};
		}	
		
		@Override
		public String toString() {
			return "PlantPlayer: PLAYER ON PLANT STEM";
		}
	}
	
	private class Event extends CollisionEvent{
		@Override
		public void run(BoundaryFeature source, BoundaryFeature collidingWith, Vector separation) {
			changeState(bufferState);
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
		this.inputController.runHeld();
	}
	
	private abstract class State implements Runnable{
		public void onChange(){}
		public void onLeavingState(){}
		public void onLeft(){}
		public void holdingLeft() {}
		public void offLeft(){}
		public void onRight(){}
		public void holdingRight() {}
		public void offRight(){}
		public void onUp(){}
		public void holdingUp() {}
		public void offUp(){}
		public void onDown(){}
		public void holdingDown() {}
		public void offDown(){}
		
		public void onJump(){}
		public void holdingJump() {}
		public void offJump(){}
		
		public void debugDraw( MovingCamera cam , Graphics2D g2){
			
			g2.drawString("Player "+getAngularComposite().getOrientationVector().normalLeft().angleFromVector() , 800, 400);
			g2.drawString("Gravity "+ gravity.toVector().angleFromVector() , 800, 415);
			g2.drawString("Difference "+(float) Vector.angleBetweenVectors( getAngularComposite().getOrientationVector(), gravity.toVector()) , 800, 430);
		}
	}
	
	private class ClimbingState extends State{

		private Force clingNormal;
		private VelocityVector currentCling;
		private VelocityVector climbVelocity;
		private byte leftRight;
		private Vector jumpVelocity;
		
		private byte slidingGripPercent = 100;
		
		private DoubleLinkedList<PlantTwigSegment> stemsInRange = new DoubleLinkedList<PlantTwigSegment>();
		private PlantTwigSegment currentStem;
		private int stemHeight = 0;
		
		@Override
		public void debugDraw(MovingCamera cam, Graphics2D g2) {
			int i = 400;
			while(stemsInRange.hasNext()){
				PlantTwigSegment stem = stemsInRange.get();
				Vector absAngle = stem.getAngularComposite().getOrientationVector().normalLeft();
				//g2.drawString("Stem "+ Vector.angleBetweenVectors( absAngle , gravity.toVector().inverse() ) , 800, i);
				g2.drawString("Stem "+ Vector.angleBetweenVectors(absAngle, gravity.toVector()) , 700, i);
				i = i + 15;
			}
			
			g2.drawString(" Velocities "+getTranslationComposite().debugNumberVelocities()+" "+ currentCling.getVector().angleFromVector() , 700, 385);
		}
		
		public ListNodeTicket addSegment( PlantTwigSegment stem, Vector clingVector ){

			if ( stemsInRange.size() == 0 ){
				currentStem = stem;
			}
			return stemsInRange.add( stem );
		}
		
		public void onChange(){
			canStartClimb = false; //already climbing
			
			jumpVelocity = PlantPlayer.this.getTranslationComposite().getVelocityVector();
			clingNormal = PlantPlayer.this.getTranslationComposite().registerNormalForce(gravity.toVector().inverse());
			currentCling = PlantPlayer.this.getTranslationComposite().registerVelocityVector( Vector.zeroVector );			
			climbVelocity = PlantPlayer.this.getTranslationComposite().registerVelocityVector( Vector.zeroVector );

			PlantPlayer.this.getTranslationComposite().halt();
		}
		
		@Override
		public void run() {

			clingNormal.setVector(gravity.toVector().inverse().multiply( slidingGripPercent/100.0 ));
			
			final Vector distance = PlantPlayer.this.getRelativeTranslationalVectorOf(currentStem);
			final Vector attachDirection = distance.projectedOver( currentStem.getOrientationVector() );

			currentCling.setVector( attachDirection.multiply(0.1) );
			
			//check to see if player has left current stem
			
			Point relativePlayerPosition = currentStem.getFullRelativePositionOf(PlantPlayer.this);
			stemHeight = -relativePlayerPosition.y;
			
			if ( stemHeight < 0 ){
				if ( currentStem.previousSegment != null ){ //FIXME make a method get next and get prev IN PLANT STEM that returns self
					currentStem = currentStem.previousSegment;
				}
			}
			else if ( stemHeight > 80 ){
				if ( currentStem.nextSegment.length != 0 ){	// instead of null
					currentStem = currentStem.nextSegment[0];
				}
			}
			
		}
		
		@Override
		public void onLeavingState() { //remove all velocities when done with this state
			canStartClimb = true;
			PlantPlayer.this.getTranslationComposite().removeNormalForce(clingNormal);
			PlantPlayer.this.getTranslationComposite().removeVelocityVector(currentCling);
			PlantPlayer.this.getTranslationComposite().removeVelocityVector(climbVelocity);
		}
		
		@Override
		public void onUp() {
			climbVelocity.setVector( currentStem.getOrientationVector().normalRight().multiply(3) );
			slidingGripPercent=100;
		}
		@Override
		public void holdingUp() {
			onUp();
		}
		@Override
		public void offUp() {
			climbVelocity.setVector( Vector.zeroVector );
		}
		@Override
		public void onDown() {
			//climbVelocity.setVector( currentStem.getOrientationVector().normalLeft().multiply(3) );
			slidingGripPercent = 60;
		}
		@Override
		public void offDown() {
			//climbVelocity.setVector( Vector.zeroVector );
			slidingGripPercent = 100;
			getTranslationComposite().halt();
		}
		
		@Override
		public void onRight() { leftRight = -1; }
		@Override
		public void onLeft() { leftRight = 1; }
		@Override
		public void holdingRight() { leftRight = -1; }
		@Override
		public void holdingLeft() { leftRight = 1; }
		@Override
		public void offRight() { leftRight = 0; }
		@Override
		public void offLeft() { leftRight = 0; }
		
		@Override
		public void onJump() {
			getTranslationComposite().addVelocity( 
					gravity.toVector().inverse().unitVector().multiply(5) .add(
							gravity.toVector().normalLeft().multiply(leftRight*10)
							));
			changeState(falling);
			bufferState = movingState;
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
		@Override public void onLeft(){}
		@Override public void offLeft(){ movingState.setCoasting(); }
		@Override public void onRight(){}
		@Override public void offRight(){ movingState.setCoasting(); }
		@Override public void onJump(){
			canStartClimb = false;
		}
		@Override public void holdingJump(){
			canStartClimb = false;
		}
		@Override public void offJump(){
			canStartClimb = true;
		}
		
	}
	
	private class MovingState extends State{
		
		protected byte leftRight;
		
		private final Runnable accelerating = new Runnable(){
			public void run(){
				
				movementForce.setVector( gravity.toVector().normalLeft().unitVector().multiply(0.1*leftRight) );
			}
		};
		
		private final Runnable coasting = new Runnable(){
			public void run(){}
		};
		
		private Runnable currentMovementState = accelerating;
		
		protected void setCoasting(){  currentMovementState = coasting;  }
		
		@Override
		public void onChange() {
			
		}
		
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
			getTranslationComposite().addVelocity( gravity.toVector().inverse().unitVector().multiply(5) );
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
