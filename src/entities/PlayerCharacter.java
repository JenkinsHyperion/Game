package entities;


import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import Input.KeyCommand;
import Input.MouseCommand;

//import javax.swing.Action;
//import javax.swing.Timer;

import animation.*;
import engine.BoardAbstract;
import entityComposites.Collider;
import entityComposites.Collider;
import entityComposites.CompositeFactory;
import entityComposites.GraphicComposite;
import physics.Force;
import physics.Side;
import physics.Vector;
import physics.BoundaryVertex;
import physics.BoundaryFeature;
import misc.CollisionEvent;
import misc.DefaultCollisionEvent;
import misc.EntityState;
import misc.PlayerState;
import misc.PlayerDirection.PlayerDirection;
import physics.Boundary;
import sprites.Sprite;
import sprites.SpriteAnimated;

public class PlayerCharacter extends Player {
	
	private final int spriteOffsetX=-35;
	private final int spriteOffsetY=-35;
	
	private Force movementForce ;
	private Force gravity ;
	
	private Side ground;
	
    
    private final SpriteAnimated RUN_RIGHT = new SpriteAnimated(
    		new AnimationEnhanced(LoadAnimation.buildAnimation(16, 0, 75, "RunRight.png") , 2 ),
    		spriteOffsetX , spriteOffsetY ); 
    private final SpriteAnimated RUN_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(16, 0, 75, "Run_75px.png") , 2 ),
    		spriteOffsetX , spriteOffsetY );
    
    private final SpriteAnimated SPRINT_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(10, 0, 75, "SprintLeft2.png") , 3 ),
    		spriteOffsetX , spriteOffsetY );
    private final SpriteAnimated SPRINT_RIGHT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(10, 0, 75, "SprintRight2.png") , 3 ),
    		spriteOffsetX , spriteOffsetY ); 
    
    private final SpriteAnimated IDLE_RIGHT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(2, 0, 75, "IdleLeft.png") , 18 ),
    		spriteOffsetX , spriteOffsetY );
    
    //
    private final SpriteAnimated IDLE_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(2, 0, 75, "IdleLeft.png") , 18 ),
    		spriteOffsetX , spriteOffsetY );
    
    private final SpriteAnimated CLIMB_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.getAnimation(18, 0, 100,165 , "climbSpritesheet.png") , 3 ),
    		spriteOffsetX-40 , spriteOffsetY-55 );
    private final SpriteAnimated CLIMB_RIGHT = new SpriteAnimated(
    		new Animation(LoadAnimation.getAnimation(18, 0, 100,165 , "climbSpritesheet.png") , 2 ),
    		spriteOffsetX , spriteOffsetY );
    
    private SpriteAnimated JUMP_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(2, 5, 32, "player_sheet.png") , 18 ),
    		spriteOffsetX , spriteOffsetY );

    //private PlayerState climbingRight= new EntityState("climbing_left",CLIMB_RIGHT);
    //private PlayerState climbingLeft= new EntityState("climbing_left",CLIMB_LEFT);
    
    private final PlayerState running= new Running( "Running" , RUN_RIGHT , RUN_LEFT); //FIX ME REFERNCING ERROR
    private final PlayerState sprintingRight= new Sprinting("sprinting_right",SPRINT_RIGHT , SPRINT_LEFT );
    private final PlayerState standing = new Standing("standing", IDLE_RIGHT , IDLE_LEFT);
    private final PlayerState runningTurn = new RunningTurn("turning", IDLE_RIGHT , IDLE_LEFT);
    private final PlayerState sprintingTurn = new SprintingTurn("skidding", IDLE_RIGHT , IDLE_LEFT);
    private final PlayerState wallSliding = new WallSliding("WallSliding", IDLE_RIGHT , IDLE_LEFT);
    //private PlayerState jumpingLeft= new EntityState("jumping_left",JUMP_LEFT);
    private final PlayerState climbing = new WallClimbing( "WallClimbing" , CLIMB_RIGHT , CLIMB_LEFT );
    private final PlayerState fallingLeft = new Falling( "Falling" , IDLE_RIGHT , IDLE_LEFT );
    
    private PlayerState playerState = fallingLeft;
    private PlayerState playerStateBuffer = standing;
    
    private PlayerDirection playerDirection = new PlayerDirection();
    
    private final CollisionEvent onSideCollision = new SideCollisionEvent();

    public PlayerCharacter(int x, int y , BoardAbstract testBoard) {
        super(x, y, testBoard);
        
        RUN_RIGHT.getAnimation().setReverse();
        SPRINT_RIGHT.getAnimation().setReverse();

		name = "Player"+count;
        board = testBoard;
        initPlayer();
    }

    private void initPlayer() {
        
        // MANUAL SPRITE COMPOSITE
        //SpriteComposite spirteComp = new SpriteComposite( IDLE_LEFT , this);
        //this.setSpriteType(spirteComp);
        CompositeFactory.addGraphicTo( this , IDLE_LEFT);

        // COLLIDER COMPOSITE
        // Making many custom events for player contexts
        CollisionEvent floorCollisionEvent = new DefaultCollisionEvent(  );
        CollisionEvent[] eventList = new CollisionEvent[]{
        		floorCollisionEvent, //top
        		onSideCollision,
        		new FloorCollisionEvent(),
        		onSideCollision
        };
        // Creating actual composite
        Collider collisionMesh = new Collider( this );
        setCollisionComposite( collisionMesh );
        collisionMesh.setLeavingCollisionEvent( new OnLeavingCollision() );
        // Find better method for casting
        
        Boundary boundarytemp =  new Boundary.EnhancedBox( 24,76 ,-12,-38, eventList );
        //Boundary boundarytemp =  new Boundary.Box( 24,76 ,-12,-38, (Collidable) this.collisionType );
        
        CollisionEvent cornerCollision = new CollisionEvent(){
			
			@Override 
			public void run(BoundaryFeature source, BoundaryFeature collidingWith) {
			
				//SNAPPING ANGLE TO SIDE 
				ground = (Side) collidingWith;
				

				BoundaryVertex corner = ((BoundaryVertex)source);
				
				Vector sub = corner.getStartingSide().getSlopeVector().subtract( corner.getEndingSide().getSlopeVector() );
				
				Vector slope1 = corner.getStartingSide().getSlopeVector();
				Vector slope2 = corner.getEndingSide().getSlopeVector();
				
				BoundaryVertex rawCorner = getColliderComposite().getBoundary().getRawVertex( corner.getID() );

				Vector slopeGround = ground.getSlopeVector();

				
				double dist1 = slope1.unitVector().dotProduct(ground.getSlopeVector());
				double dist2 = slope2.unitVector().dotProduct(ground.getSlopeVector());
	
				double ground = slopeGround.calculateAngleFromVector();
							
					if ( Math.abs(dist2) > Math.abs(dist1) ){ //only works for rectangles
						//Counterclockwise side is leaning at a closer angle to the surface
						double rawSide2 = storedBounds.getRawSide( corner.getEndingSide().getID() ).getSlopeVector().absSlope().calculateAngleFromVector();
						//System.out.println("snapping CCW to "+Math.toDegrees(ground) );
						//System.out.println("snapping CCW to "+Math.toDegrees(rawSide2) );
						PlayerCharacter.this.setAngleInRadians( ground + rawSide2 );
					}
					else{
						//Clockwise side is leaning at a closer angle to the surface
						double rawSide1 = storedBounds.getRawSide( corner.getStartingSide().getID() ).getSlopeVector().absSlope().calculateAngleFromVector();
						//System.out.println("snapping CW to "+Math.toDegrees(ground));
						//System.out.println("snapping CW to "+Math.toDegrees(rawSide1) );
						PlayerCharacter.this.setAngleInRadians( ground + rawSide1 );
					}
			 
			}
		};
		
		for ( BoundaryVertex corner : boundarytemp.getCornersVertex() ){
			corner.setCollisionEvent( cornerCollision );
		}
        
		((Collider) collisionType).setBoundary( boundarytemp ); 
		storedBounds = new Boundary.Box(24,76 ,-12,-38 );   //OPTIMIZE move to child RotationalCollidable that can store boundary 

		
		//dispose of all the temp stuff
		boundarytemp = null;
		floorCollisionEvent = null;
		eventList = null;
		
		// PLAYER INPUT 
		this.inputController.createKeyBinding( KeyEvent.VK_W , new UpKey() );
		this.inputController.createKeyBinding( KeyEvent.VK_A , new LeftKey() ) ;
		this.inputController.createKeyBinding( KeyEvent.VK_S , new DownKey() ) ;
		this.inputController.createKeyBinding( KeyEvent.VK_D , new RightKey()) ;
		this.inputController.createKeyBinding( KeyEvent.VK_SPACE , new JumpKey() ) ;
		this.inputController.createKeyBinding( KeyEvent.VK_SHIFT , new ModKey() ) ;
		
		this.inputController.createMouseBinding(MouseEvent.ALT_MASK , MouseEvent.BUTTON1, new ClickTest() );
		
		// ADD FORCES // TO BE MOVED TO EXTERNAL BOARD AND OR FEILDS
		this.gravity = addForce( new Vector( 0 , 0.2 ) );
		this.movementForce = addForce( new Vector( 0 , 0 ) );
		
    }
    
  
    
    @Override
    public void updatePosition() {// SPLIT INTO GENERAL UPDATE BETWEEN INPUT AND POSITION
    	super.updatePosition();
    	playerState.updateState();
    	
    	//TESTING FORCES
    	//this.accX = (float) this.sumOfForces().getX();
    	//this.accY = (float) this.sumOfForces().getY();
    	this.applyAllForces();
    	
    }   
    
    private void changePlayerState( PlayerState state ){
    	
    	playerState.onLeavingState();
    	
		playerState = state;
		
		Sprite sprite = playerDirection.getDirectionalSprite(state);
		
		sprite.getAnimation().start();
		
		this.getGraphicComposite().setSprite( sprite );
		
		//inputController.runReleased();

		inputController.runHeld();
		
		playerState.onEnteringState();
  	
    }
    
    private void changeDirection(){
    	
    	playerDirection.reveseDirection(); 
    	
    	
    }

    private class OnLeavingCollision extends CollisionEvent{
		@Override
		public void run( BoundaryFeature source , BoundaryFeature collidingWith ) {
			changePlayerState( fallingLeft );
			
		}	
    }
    
    private class FloorCollisionEvent extends CollisionEvent {

		@Override
		public void run( BoundaryFeature source , BoundaryFeature collidingWith ) {
			playerState.onCollision();
			changePlayerState( playerStateBuffer );
			if ( collidingWith.debugIsSide() ){
				ground = (Side)collidingWith;
			}
			else{
				
			}
				
		}
		@Override
		public String toString() {
			return "Floor Event";
		}
    }
    
    private class SideCollisionEvent extends CollisionEvent{
	
		@Override
		public void run( BoundaryFeature source , BoundaryFeature collidingWith ) {
			
			
			//CLIMBING MECHANIC
			/*
				
				
				Vector AB = new Vector( source.getP1() , projected1 );
				Vector AC = new Vector( source.getP1() , source.getP2() );
				
				double segment = AC.dotProduct(AB);
				
				if ( segment > 0 && segment < AC.dotProduct(AC) ){ 
				
					Point translate = new Point( (int)(getX() + AB.getX()) , (int)(getY() + AB.getY()) );
					halt();
					setPos( translate );
					
					changePlayerState( climbing );
				}
				else
					changePlayerState( wallSliding );*/
				changePlayerState( wallSliding );
		}
		
		@Override
		public String toString() {
			return "Side Collision Event";
		}
    }
    
    
    
    public String getPlayerStateName() {
        return playerState.getName();
    }
    
    public EntityState getPlayerState() {
        return playerState;
    }
    
    protected void onCollisionCompletion(){
    	
    }

    public String toString() {
		return String.format(name);
	}
    
    public String printState(){
    	return playerState.getName() + " " + playerDirection;
    }
    
    public String printBufferState(){
    	return playerStateBuffer.getName() + " " + playerDirection;
    }
    
    
    
    private class Standing extends PlayerState{ 

		public Standing( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft);
		}

		@Override
		public void onJump() {
			//setDY(-5);
			addVelocity(ground.getSlopeVector().normal().inverse().unitVector().multiply(5));
			playerStateBuffer = running;
			//changePlayerState(fallingLeft); //later to be jum;ping
		}

		@Override
		public void onForward() {
			super.onForward();
			changePlayerState( running );
		}

		@Override
		public void onBackward() {
			super.onBackward();
			changeDirection();
			changePlayerState( running );
		}

    	
    }
    
    
    private class Running extends PlayerState{ 
    	
    	private abstract class RunState{ public abstract void update(); } //CONSIDER SEPERATE IDLERUN STATE
    	
    	private class Accelerating extends RunState{ 
    		@Override
    		public void update(){ 
    			
    			float runningForce = 0.1f * ( 3 - Math.abs(dx) ) ;
    			
    			if (runningForce < 0){ runningForce = 0;}
    			
    			Vector groundVector = ground.getSlopeVector().unitVector();
    			
    			//movementForce.setVector(
    			//	getOrientationVector().multiply(  playerDirection.normalize( 0.1 + runningForce  )  ) 
    			//);
    			
    			movementForce.setVector( groundVector.multiply(  playerDirection.normalize( 0.1 + runningForce  ) ) );
    			
    		}
    	}
    	
    	private class Neutral extends RunState{ 
    		@Override
    		public void update(){
    			movementForce.setVector(0,0);
    			if ( Math.abs(dx) < 0.1 ){
    				changePlayerState(standing);
    			}
    		} 
    	}
    	
    	private final RunState idle = new Neutral();
    	private final RunState accelerate = new Accelerating();
    	private RunState state = accelerate;

		public Running( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft);
		}
		
		@Override
		public void onLeavingState() {
			movementForce.setVector(0,0);
		}
		
		@Override
		public void onEnteringState() {
			//state = accelerate;
		}
		
		@Override
		public void updateState() {
			
			state.update();
			//((AnimationEnhanced) RUN_RIGHT.getAnimation()).updateLinkedSpeed((int)-dx, 2, 20, 0, 3);
			
		}
		
		@Override
		public void onJump() {
			addVelocity(ground.getSlopeVector().normal().inverse().unitVector().multiply(5));
			playerStateBuffer = running;
			changePlayerState(fallingLeft); //later to be jum;ping
			movementForce.setVector(0,0);
		}
		
		@Override
		public void holdingShift() {
			changePlayerState(sprintingRight);
		}
		
		@Override
		public void holdingForward() {
			state = accelerate;
		}
		
		@Override
		public void offForward() {
			state = idle;
			//System.out.println(" RELEASED RUN ");
			//inputController.debugReleased();
		}
		
		@Override
		public void onBackward() {
			changePlayerState( runningTurn );
		}
		
    	
    }
    
    private class RunningTurn extends PlayerState{
    	
    	private int counter = 10; //Duration of turn in frames
    	
    	public RunningTurn( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft );
    	}
    	
    	@Override
    	public void onEnteringState() {
    		counter = 10;
    	}
    	
    	@Override
    	public void updateState() {
    		
    		if (counter < 0){
    			changeDirection();
    			changePlayerState(running);
    		}
    		else 
    			counter--;

    	}
    	
    }
    
    private class Sprinting extends PlayerState{ 

		public Sprinting( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft);
		}

		@Override
		public void updateState() {
			dx = playerDirection.normalize(5);
		}
		
		@Override
		public void onJump() {
			setDY(-3);
		}
		
		@Override
		public void offShift() {
			changePlayerState( running );
		}
		
		@Override
		public void offForward() {
			changePlayerState( running );
		}
    	
		@Override
		public void holdingBackward() {
			changePlayerState( sprintingTurn );
		}
		
    }
    
    private class SprintingTurn extends PlayerState{
    	
    	private int counter = 20; //Duration of turn in frames
    	
    	public SprintingTurn( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft );
    	}
    	
    	@Override
    	public void onEnteringState() {
    		counter = 20;
    	}
    	
    	@Override
    	public void updateState() {
    		
    		if (counter < 0){
    			changeDirection();
    			changePlayerState( sprintingRight );  
    		}
    		else 
    			counter--;

    	}
    	
    }
    
    
    private class Falling extends PlayerState{
    	
    	private final float hangAccelerationUp = -0.08f;
    	private final float hangAccelerationForward = 0.05f; 
    	private final float hangAccelerationBackward = 0.05f;
    	
    	private Force adjustUpForce;
    	private Force adjustForwardForce;
    	private Force adjustBackwardForce;
    	
		public Falling( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft);
			adjustUpForce = addForce( new Vector( 0 , 0 ) );
			adjustForwardForce = addForce( new Vector( 0 , 0 ) );
			adjustBackwardForce = addForce( new Vector( 0 , 0 ) );
		}
		
		@Override
		public void onEnteringState() {
			accX = 0;
			movementForce.setVector( 0, 0);
		}
		
		@Override
		public void onLeavingState() {
			this.adjustBackwardForce.setVector( 0 , 0 );
			this.adjustForwardForce.setVector( 0 , 0 );
			this.adjustUpForce.setVector( 0 , 0 );
		}
		
		@Override
		public void updateState() {
			//System.out.println(getAngle()+"angles");
			//addAngle( -getAngle()/100 );

		}
		
		@Override
		public void onUp() {
			
		}
		
		@Override
		public void holdingJump() {
			this.adjustUpForce.setVector( orientation.unitVector().normal().multiply( hangAccelerationUp) );
		}
		
		@Override
		public void offJump() {
			this.adjustUpForce.setVector( 0 , 0 );
		}
		
		@Override
		public void onForward() {
			playerStateBuffer = running;
		}
		@Override
		
		public void holdingForward() {
			playerStateBuffer = running;
			this.adjustForwardForce.setVector( orientation.unitVector().multiply( playerDirection.normalize(hangAccelerationForward) ) );
		}
		
		@Override
		public void offForward() {
			playerStateBuffer = standing;
			this.adjustForwardForce.setVector(0,0);
		}
		
		@Override
		public void holdingBackward() {
			playerStateBuffer = standing;
			this.adjustBackwardForce.setVector( orientation.unitVector().inverse().multiply( playerDirection.normalize(hangAccelerationBackward) ) );
		}
		
		@Override
		public void offBackward() {
			playerStateBuffer = standing;
			this.adjustBackwardForce.setVector(0,0);
		}
		
		@Override
		public void onCollision() {
			
			changePlayerState(playerStateBuffer);
		}
    	
    }
    
    private class WallSliding extends PlayerState{

    	private boolean clinging = false;
    	
		public WallSliding( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft );
			
		}
		
		@Override
		public void onEnteringState() {
			clinging = false;
			playerStateBuffer = standing;
		}
		
		@Override
		public void onLeavingState() {

		}
		
		@Override
		public void updateState() {
			if (clinging){
				dy = 0.5f;
			}
		}
		
		@Override
		public void onJump() { // wall jump reverse direction 
			

			changeDirection();
			dx = playerDirection.normalize(2);
			dy = dy-5;
			
		}
		
		@Override
		public void holdingForward() {
			clinging = true;
		}
		
		@Override
		public void onDown() {
			accY = 0.2f;
			clinging = false;
		}
    	
    } 
    
    
    private class WallClimbing extends PlayerState{

		public WallClimbing(String name, Sprite spriteRight, Sprite spriteLeft) {
			super(name, spriteRight, spriteLeft);
		}
    	
		@Override
		public void onEnteringState() {
			halt();
			gravity.setVector(0,0);
		}
		
		@Override
		public void onLeavingState() {
			gravity.setVector(0,0.2);
			
		}
		
    	
    }
    
    
    /* #############################################################################
     * 			KEYBINDINGS  -  MOVE ELSEWHERE TO SOME CONTROLLER CONFIG
 	 * #############################################################################
     */

    
    private class UpKey implements KeyCommand{

		@Override
		public void onPressed() { playerState.onUp(); }

		@Override
		public void onReleased() { playerState.offUp(); }

		@Override
		public void onHeld(){ playerState.holdingUp(); }
		
    }
    
    private class DownKey implements KeyCommand{

		@Override
		public void onPressed() { playerState.onDown(); }

		@Override
		public void onReleased() { playerState.offDown(); }

		@Override
		public void onHeld(){ playerState.holdingDown(); }
		
    }
    
    private class RightKey implements KeyCommand{
    	

		@Override
		public void onPressed() { playerState.onRight(playerDirection); }

		@Override
		public void onReleased() { playerState.offRight(playerDirection); }

		@Override
		public void onHeld(){ playerState.holdingRight(playerDirection); }
		
    }
    
    private class LeftKey implements KeyCommand{
    	

		@Override
		public void onPressed() { playerState.onLeft(playerDirection); }

		@Override
		public void onReleased() { playerState.offLeft(playerDirection); }

		@Override
		public void onHeld(){ playerState.holdingLeft(playerDirection); }
		
    }
    
    
    private class JumpKey implements KeyCommand{
    	

		@Override
		public void onPressed() { playerState.onJump(); }

		@Override
		public void onReleased() { playerState.offJump(); }

		@Override
		public void onHeld(){ playerState.holdingJump(); }
		
    }
    
    private class ModKey implements KeyCommand{

		@Override
		public void onPressed() { playerState.onShift(); }

		@Override
		public void onReleased() { playerState.offShift(); }

		@Override
		public void onHeld(){ playerState.holdingShift(); }
		
    }
    
    private class ClickTest implements MouseCommand{

		@Override
		public void mousePressed() {
			playerState.onLeft(playerDirection);
		}
		@Override
		public void mouseReleased() {
			playerState.offLeft(playerDirection);
		}
		@Override
		public void mouseDragged() {
			// TODO Auto-generated method stub
		}

		
    }
    
    
}