package testEntities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;


import javax.swing.text.ChangedCharSetException;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;


import Input.KeyCommand;
import engine.BoardAbstract;
import engine.MovingCamera;
import engine.TestBoard;
import entities.Player;
import entityComposites.Collider;
import entityComposites.EntityStatic;
import entityComposites.TranslationComposite;
import entityComposites.TranslationComposite.VelocityVector;
import misc.CollisionEvent;
import misc.MovementBehavior;
import physics.Boundary;
import physics.BoundaryCircular;
import physics.BoundaryFeature;
import physics.BoundaryPolygonal;
import physics.BoundarySingular;
import physics.Collision;
import physics.CollisionDispatcher;
import physics.CollisionRigidDynamicStatic;
import physics.Force;
import physics.SeparatingAxisCollector.Axis;
import physics.Vector;
import physics.VisualCollisionCheck;
import sprites.RenderingEngine;
import sprites.Sprite;
import testEntities.PlantSegment.SeedFruit;
import utility.DoubleLinkedList;
import utility.ListNodeTicket;

public class PlantPlayer extends Player {
	
	private final static Sprite.Stillframe playerSprite = new Sprite.Stillframe("box.png", Sprite.CENTERED);
	
	private TestBoard board;
	private MovingCamera camera;

	private final ClimbingState climbing = new ClimbingState();
	private final MovingState movingState = new MovingState();
	private final StandingState standing = new StandingState();
	private final FallingState falling = new FallingState();
	private State currentState = falling;
	private GroundState bufferState = standing;
	
	private boolean canStartClimb = true;
	
	private Vector inputVectorRight = new Vector(0,0);
	private Vector inputVectorLeft = new Vector(0,0);
	private Vector inputVectorUp = new Vector(0,0);
	private Vector inputVectorDown = new Vector(0,0);
	
	private Vector leftStickInputVector = new Vector(0,0);
	
	private Force movementForce;
	private byte hasFriction = 0;
	
	private Point playerCameraFocus = new Point(0,0);
	private Double playerCameraZoomOut = 1.0;
	
	private NullContext nullContext = new NullContext();
	private InteractionEvent currentInteractionContext = nullContext;

	private InventoryItem currentHeldItem = new NothingHeld();
	
	public ControllerManager controllers;

	public PlantPlayer(int x, int y, TestBoard board ) {
		super(x, y);
		
		this.camera = board.getCamera();
		this.board = board;
    	controllers = new ControllerManager();
    	controllers.initSDLGamepad();
		this.addGraphicTo( playerSprite , true);
		
		Boundary boundary = new BoundarySingular( new Event() );
		Boundary boundary2 = new BoundaryCircular( 40 , new Event() );
		Boundary boundary3 = new BoundaryPolygonal.Box( 200,200,-100,-100 );
		
		this.addInitialColliderTo( boundary2 );
		this.addTranslationTo();
		
		this.addAngularComposite();
		
		this.movementForce = this.getTranslationComposite().addForce( new Vector(0,0) );
		
		
		this.inputController.createKeyBinding(KeyEvent.VK_E, new KeyCommand(){	//ACTION EVENT KEY
			
			@Override
			public void onPressed() {
				currentInteractionContext.activate();
			}
		});
		
		this.inputController.createKeyBinding(KeyEvent.VK_LEFT, new KeyCommand(){
			@Override
			public void onPressed() {
				currentState.onLeft();
				inputVectorLeft = PlantPlayer.this.getOrientationVector().normalRight();
			}
			@Override
			public void onReleased() {
				currentState.offLeft();
				inputVectorLeft = Vector.zeroVector;
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
				inputVectorRight = PlantPlayer.this.getOrientationVector().normalLeft();
			}
			@Override
			public void onReleased() {
				currentState.offRight();
				inputVectorRight = Vector.zeroVector;
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
				inputVectorUp = PlantPlayer.this.getOrientationVector().inverse();
			}
			@Override
			public void onReleased() {
				currentState.offUp();
				inputVectorUp = Vector.zeroVector;
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
				inputVectorDown = PlantPlayer.this.getOrientationVector();
			}
			@Override
			public void onReleased() {
				currentState.offDown();
				inputVectorDown = Vector.zeroVector;
			}
			@Override
			public void onHeld() {
				currentState.holdingDown();
			}
		});
		
		this.getColliderComposite().setLeavingCollisionEvent( new CollisionEvent(){
			@Override
			public void run(Collider partner, BoundaryFeature source, BoundaryFeature collidingWith, Vector normal) {
				movementForce.setVector(Vector.zeroVector);
				changeState(falling);
			}
		});
		Thread testControllerThread = new Thread(new Runnable() {

			ControllerState currState;
			ControllerState oldState =  controllers.getState(0);

			//a = new Object();

    		@Override
    		public void run() {
    			//Print a message when the "A" button is pressed. Exit if the "B" button is pressed 
    			//or the controller disconnects.
    			while(true) {
    				
    				
    				currState = controllers.getState(0);
    				double playerAngle =Math.toRadians( -currState.leftStickAngle - getAngularComposite().getAngleInDegrees() );
    				leftStickInputVector.set(Math.cos(playerAngle), Math.sin(playerAngle));
    				leftStickInputVector = leftStickInputVector.multiply(currState.leftStickMagnitude );
    				if(!currState.isConnected) {
    					break;
    				}
    				if(currState.dpadLeft && !oldState.dpadLeft ) {
    					currentState.onLeft();
    				}
    				if(!currState.dpadLeft && oldState.dpadLeft) {
    					currentState.offLeft();
    					System.err.println("calling offLeft()");
    				} 
    			
    				if(currState.dpadRight) {
    					currentState.onRight();
    				}
    				
    				oldState = currState;
    			}
    		}

    	});
    	testControllerThread.start();
    	
    	
    	givePlayerItem( new Fruit01() );
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		this.currentState.run();
		
		double playerAbsoluteAngle = translationComposite.getNetGravityVector().normalLeft().angleFromVectorInRadians();
		this.getAngularComposite().setAngleInRadians( playerAbsoluteAngle );
	}
	
	public Point getPlayerCameraFocus(){
		return playerCameraFocus;
	}
	
	public Double getPlayerLookZoom(){
		return playerCameraZoomOut;
	}
	
	public void debugDraw(  MovingCamera cam , Graphics2D g2 ){
		
		this.currentState.debugDraw(cam, g2);
		
		this.currentInteractionContext.drawReadout(cam, g2);
	}
	
	public void debugCollisions( MovingCamera cam , Graphics2D g2 ){
		for( int i = 0 ; i < this.getColliderComposite().getCollisions().length ; ++i ){
			Collision collision = this.getColliderComposite().getCollisions()[i];
			g2.drawString( collision.toString(), 10, 400+(i*15));
		}
	}

	/*##########################################################################################################################################################
	 * 		PLAYER COLLISION EVENTS
	 *##########################################################################################################################################################
	 */
	
	public static class GroundCollision extends CollisionDispatcher<PlantPlayer,EntityStatic>{
		
		@Override
		public Collision createVisualCollision(PlantPlayer player, Collider collider1, EntityStatic entity2,
				Collider collider2, VisualCollisionCheck check, RenderingEngine engine) {
			
			return new CollisionRigidDynamicStatic(collider1, collider2, check.getAxisCollector() ){

				private Force frictionForce;
				
				@Override
				protected void initializeCollision() {

					this.frictionForce = player.getTranslationComposite().addForce( new Vector( 0 , 0 ) );
					
					player.movingState.setNormalForce(this.normalForce);
					player.standing.setNormalForce(this.normalForce);
					player.changeState(player.bufferState); 
				}
				
				@Override
				public void updateBehavior(Vector unitNormal, Vector tangentalVelocity) {
					frictionForce.setVector(tangentalVelocity.inverse().multiply(0.02));
					
				}
				
				@Override
				protected void completeCollision() {
					player.getTranslationComposite().unregisterForce(frictionForce);
				}
			};
		}
	}

	public static class PlayerOnStem extends CollisionDispatcher<PlantPlayer,PlantSegment>{

		@Override
		public Collision createVisualCollision(PlantPlayer player, Collider playerCollider, PlantSegment plantSegment, Collider collider2, VisualCollisionCheck check, RenderingEngine engine) {
			
			return new Collision.CustomType<PlantPlayer, PlantSegment>( player , playerCollider , plantSegment, collider2 ){

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
				protected void internalInitializeCollision() {
					
					Axis[] axes = check.getAxisCollector().getSeparatingAxes(playerCollider, entityPrimary.getPosition(), playerCollider.getBoundary(), 
							collider2, entitySecondary.getPosition(), collider2.getBoundary() );

					clingVector = axes[0].getNearFeatureSecondary().getNormal().unitVector();
					
					segmentOnPlayer = player.climbing.addSegment( plantSegment , clingVector );
					
					if ( player.canStartClimb ){		
						
						player.changeState(player.climbing);
					}
					else{
						System.err.println("Stem rejecting player, can climb = "+player.canStartClimb);
					}
					
				}

				@Override
				public void internalCompleteCollision() {
					segmentOnPlayer.removeSelfFromList();
				}

			};
		}	
		
		@Override
		public String toString() {
			return "PlantPlayer: PLAYER ON PLANT STEM";
		}
	}
	
	
	public static class FruitInRange extends CollisionDispatcher<PlantPlayer, SeedFruit>{

		@Override
		public Collision createVisualCollision(PlantPlayer player, Collider collider1, SeedFruit fruit,
				Collider collider2, VisualCollisionCheck check, RenderingEngine engine) {
			
			return new Collision.CustomType<PlantPlayer, SeedFruit>(player, collider1, fruit, collider2) {

				Point readoutPosition;
				
				@Override
				protected void internalInitializeCollision() {
					
					readoutPosition = new Point(300,300);
					InteractionEvent pickup = player.new InteractionEvent(readoutPosition){

						@Override
						protected void activate() {

							player.givePlayerItem( player.new Fruit01() );
							
							isComplete = true;
							fruit.disable();
						}

						@Override
						protected void drawReadout(MovingCamera cam, Graphics2D g2) {
							g2.setFont( contextFont );
							g2.setColor( Color.WHITE );
							g2.drawString( "Pick" , cam.getRelativeX(readoutPosition.x), cam.getRelativeY(readoutPosition.y) );
							g2.setFont( defaultFont );
						}

					};

					player.currentInteractionContext = pickup;
				}

				@Override
				public void updateCollision() {
					isComplete = !check.check(collidingPrimary, collidingSecondary);
					
					readoutPosition.setLocation(fruit.getPosition());
				}

				@Override
				public void updateVisualCollision(MovingCamera camera, Graphics2D gOverlay) {
					isComplete = !check.check(collidingPrimary, collidingSecondary, camera, gOverlay);
					
				}

				@Override
				public void internalCompleteCollision() {

					player.resetContext();
				}
			};
		}
	}
	
	
	private class Event extends CollisionEvent{
		@Override
		public void run(Collider partner, BoundaryFeature source, BoundaryFeature collidingWith, Vector separation) {
			//changeState(bufferState);
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
		public void onRight(){ }
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
			

		}
	}
	
	private abstract class GroundState extends State{

		protected Force normal;
		
		protected void setNormalForce(Force normal){
			this.normal = normal;
		}
	}
	
	private class ClimbingState extends State{

		private Force clingNormal;
		private VelocityVector currentCling;
		private VelocityVector climbVelocity;
		
		private byte leftRight;
		private Vector jumpVelocity;
		
		private byte slidingGripPercent = 100;
		
		private DoubleLinkedList<PlantSegment> stemsInRange = new DoubleLinkedList<PlantSegment>();
		private PlantSegment currentStem;
		private int stemHeight = 0;
		
		@Override
		public void debugDraw(MovingCamera cam, Graphics2D g2) {
			int i = 400;
			/*while(stemsInRange.hasNext()){
				PlantSegment stem = stemsInRange.get();
				Vector absAngle = stem.getAngularComposite().getOrientationVector().normalLeft();
				//g2.drawString("Stem "+ Vector.angleBetweenVectors( absAngle , gravity.toVector().inverse() ) , 800, i);
				g2.drawString("Stem "+ Vector.angleBetweenVectors(absAngle, gravity.toVector()) , 700, i);
				i = i + 15;
			}*/
			leftStickInputVector.set( inputVectorDown.add(inputVectorLeft).add(inputVectorRight).add(inputVectorUp) );
			
			g2.drawString("InputVector "+leftStickInputVector, 800, 400);
			
			cam.drawLineInWorld( leftStickInputVector.multiply(200).toLine( new Point(300,300) ) , g2);
			
			//g2.drawString(" Velocities "+getTranslationComposite().debugNumberVelocities()+" "+ currentCling.getVector().angleFromVector() , 700, 385);
		}
		
		public ListNodeTicket addSegment( PlantSegment stem, Vector clingVector ){

			if ( stemsInRange.size() == 0 ){
				currentStem = stem;
			}
			return stemsInRange.add( stem );
		}
		
		public void onChange(){
			canStartClimb = false; //already climbing
			
			jumpVelocity = PlantPlayer.this.getTranslationComposite().getVelocityVector();
			clingNormal = PlantPlayer.this.getTranslationComposite().registerNormalForce(translationComposite.getNetGravityVector().inverse());
			currentCling = PlantPlayer.this.getTranslationComposite().registerVelocityVector( Vector.zeroVector );			
			climbVelocity = PlantPlayer.this.getTranslationComposite().registerVelocityVector( Vector.zeroVector );

			PlantPlayer.this.getTranslationComposite().halt();
		}
		
		@Override
		public void run() {

			clingNormal.setVector(translationComposite.getNetGravityVector().inverse().multiply( slidingGripPercent/100.0 ));
			
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
				if ( currentStem.nextSegments.length != 0 ){	// instead of null
					
					if ( currentStem.nextSegments.length > 1 ){ //check for branches OPTIMIZE polymorph intoa  plantsegment method that takes playerVelocity as refernece
						
						//Vector absInputVector = inputVectorDown.add(inputVectorLeft).add(inputVectorRight).add(inputVectorUp);
						Vector absInputVector = leftStickInputVector;
						
						Vector forkCWDirection = currentStem.nextSegments[0].getOrientationVector().normalRight();
						Vector forkCCWDirection = currentStem.nextSegments[1].getOrientationVector().normalRight();
						
						if ( absInputVector.dotProduct(forkCWDirection) > absInputVector.dotProduct(forkCCWDirection)){
							
							currentStem = currentStem.nextSegments[0];
						}
						else{
							currentStem = currentStem.nextSegments[1];
						}
					}
					else{
						currentStem = currentStem.nextSegments[0];
					}
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
		public void onRight() { 
			leftRight = -1; 
			}
		@Override
		public void onLeft() { 
			leftRight = 1; 
			}
		@Override
		public void holdingRight() { leftRight = -1; }
		@Override
		public void holdingLeft() { leftRight = 1; }
		@Override
		public void offRight() { 
			leftRight = 0; 
			}
		@Override
		public void offLeft() { 
			leftRight = 0; 
			}
		
		@Override
		public void onJump() {
			getTranslationComposite().addVelocity( 
					translationComposite.getNetGravityVector().inverse().unitVector().multiply(5) .add(
							translationComposite.getNetGravityVector().normalLeft().multiply(leftRight*10)
							));
			changeState(falling);
			bufferState = movingState;
		}

	}
	
	private class StandingState extends GroundState{
		
		@Override
		public void onLeavingState() {
			//playerCameraFocus.setLocation(0,0); camera.zoomInFull();
		}
		
		@Override
		public void run() {}
		@Override
		public void onLeft() {
			changeState(movingState);
			movingState.currentMovementState = movingState.accelerating;
			movingState.leftRight = 1;
		}
		@Override
		public void onRight() {
			changeState(movingState);
			movingState.currentMovementState = movingState.accelerating;
			movingState.leftRight = -1;
		}
		@Override
		public void onJump() {
			getTranslationComposite().addVelocity( normal.toVector().unitVector().multiply(5) );
			bufferState = movingState;
		}
		@Override
		public void onUp() { playerCameraFocus.setLocation(0,-250); camera.zoomOutFull();}
		@Override
		public void offUp() { playerCameraFocus.setLocation(0,0); camera.zoomInFull();}
		@Override
		public void onDown() { currentInteractionContext = new DropContext(); }
		@Override
		public void offDown() { resetContext(); }
		
		@Override public void offLeft(){};
		@Override public void offRight(){};
		@Override public void offJump(){};
	}
	
	private class FallingState extends State{
		
		@Override
		public void onChange() {
			currentInteractionContext = nullContext;
		}
		
		@Override
		public void onLeavingState() {
			currentInteractionContext = nullContext;
		}
		
		@Override
		public void run() {}
		@Override public void onLeft(){ 
			movingState.currentMovementState = movingState.accelerating; 
			movingState.leftRight = 1;
		}
		@Override public void offLeft(){ movingState.setCoasting(); }
		@Override public void onRight(){ 
			movingState.currentMovementState = movingState.accelerating; 
			movingState.leftRight = -1;
			}
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
	
	private class MovingState extends GroundState{
		
		protected byte leftRight;
		
		private final Runnable accelerating = new Runnable(){
			public void run(){
				
				movementForce.setVector( normal.toVector().inverse().normalLeft().unitVector().multiply(0.1*leftRight) );
			}
		};
		
		private final Runnable coasting = new Runnable(){
			public void run(){
				if ( getDX()*getDX() + getDY()*getDY() < 1 ){	//Enter standing state when velocity drops below a certain speed
					changeState(standing);
				}
			}
		};
		
		private Runnable currentMovementState = accelerating;
		
		protected void setCoasting(){  currentMovementState = coasting;  }
		
		@Override
		public void onChange() {
			canStartClimb = false;
			hasFriction = 1;
		}
		
		@Override
		public void onLeavingState() {
			canStartClimb = true;
			hasFriction = 0;
		}
		
		@Override
		public void run() {
			currentMovementState.run();
		}
		@Override
		public void offRight() {
			movementForce.setVector(0,0);
			currentMovementState = coasting;
			hasFriction = 1;
		}
		@Override
		public void offLeft() {
			movementForce.setVector(0,0);
			currentMovementState = coasting;
			hasFriction = 1;
		}
		@Override
		public void onJump() {
			getTranslationComposite().addVelocity( normal.toVector().unitVector().multiply(5) );
			bufferState = movingState;
		}
		
		@Override public void onLeft(){
			if ( leftRight < 0 )
				leftRight = 1;
			
			currentMovementState = accelerating;
			hasFriction = 0;
		};
		@Override public void onRight(){
			if( leftRight > 0 )
				leftRight = -1;

			currentMovementState = accelerating;
			hasFriction = 0;
		};
		
		//############# CAMERA LOOK UP AND DOWN METHODS ####################
		@Override
		public void onUp() { 
			playerCameraFocus.setLocation(0,-250); 
			camera.zoomOutFull();
			}
		@Override
		public void offUp() { playerCameraFocus.setLocation(0,0); camera.zoomInFull();}
		@Override
		public void onDown() { 
			playerCameraFocus.setLocation(0,250); 
			camera.zoomOutFull();
			}
		@Override
		public void offDown() { playerCameraFocus.setLocation(0,0); camera.zoomInFull();}
		//##################################################################
	}

	
	private void givePlayerItem( InventoryItem item ){
		item.debugSetGraphic();
		currentHeldItem = item;
	}
	
	
	private abstract class InventoryItem{
		
		protected abstract void drawHeldItem(Graphics2D g2);
		
		protected abstract void debugSetGraphic();
		
		protected abstract void dropItemEvent();
		
		protected abstract String getName();
		
		protected abstract String getDropContext();
	}
	
	private class NothingHeld extends InventoryItem{

		protected void drawHeldItem( Graphics2D g2 ){
			
		}
		
		protected void debugSetGraphic(){
			getGraphicComposite().setSprite( playerSprite );
		}
		
		protected String getName(){
			return "Nothing";
		}
		
		protected void dropItemEvent(){}
		
		@Override
		protected String getDropContext() {
			return "Nothing";
		}
	}
	
	private class Fruit01 extends InventoryItem{
		
		private final Sprite.Stillframe fruit01Sprite = new Sprite.Stillframe("Prototypes/fruittest_01.png",Sprite.CENTERED);
		
		protected void drawHeldItem( Graphics2D g2 ){
			
		}
		
		protected void debugSetGraphic(){
			getGraphicComposite().setSprite(fruit01Sprite);
			getGraphicComposite().setGraphicAngle(Math.PI/2);
		}
		
		protected void dropItemEvent(){
			board.spawnNewTree( translationComposite.getNetGravityVector().normalLeft() );
		}
		
		protected String getName(){
			return "Fruit01";
		}
		
		@Override
		protected String getDropContext() {
			return "Plant seed";
		}
	}
	
	
	protected void resetContext(){
		this.currentInteractionContext = nullContext;
	}
	
	private abstract class InteractionEvent{
		
		protected Font defaultFont = new Font( Font.SANS_SERIF , Font.PLAIN, 12) ;
		protected Font contextFont = new Font( Font.SANS_SERIF , Font.PLAIN, 30) ;

		protected Point readoutPosition;
		
		protected InteractionEvent( Point readoutPosition ){
			this.readoutPosition = readoutPosition;
		}
		
		protected abstract void activate();
		
		protected abstract void drawReadout(MovingCamera cam, Graphics2D g2);
		
	}
	
	private class DropContext extends InteractionEvent{

		protected DropContext() {
			super( new Point(200,200));
		}

		@Override
		protected void activate() {
			
			currentHeldItem.dropItemEvent();
			
			currentHeldItem = new NothingHeld();
			getGraphicComposite().setSprite(playerSprite);
		}
		
		@Override

		public void drawReadout( MovingCamera cam, Graphics2D g2 ){	
			g2.setFont( contextFont );
			g2.setColor( Color.WHITE );
			g2.drawString( currentHeldItem.getDropContext() , readoutPosition.x, readoutPosition.y );
			g2.setFont( defaultFont );
		}
		
	}
	
	private class NullContext extends InteractionEvent{
		
		protected NullContext() {
			super( new Point( 100,100 ));
		}
		
		@Override
		protected void activate(){ /*DO NOTHING*/}

		@Override
		protected void drawReadout(MovingCamera cam, Graphics2D g2) {
			
		}

	}
	
}
