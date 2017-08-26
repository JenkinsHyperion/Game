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
import entityComposites.*;
import physics.Force;
import physics.Side;
import physics.Vector;
import physics.BoundaryCorner;
import physics.Boundary;
import physics.BoundaryCircular;
import physics.BoundaryFeature;
import misc.CollisionEvent;
import misc.DefaultCollisionEvent;
import misc.EntityState;
import misc.PlayerState;
import misc.PlayerDirection.PlayerDirection;
import physics.BoundaryPolygonal;
import physics.BoundarySingular;
import physics.BoundaryVertex;
import sprites.Sprite;
import sprites.SpriteAnimated;

public class PlayerCharacter extends Player {
	
	private final int spriteOffsetX=-35;
	private final int spriteOffsetY=-35;
	
	private Force movementForce ;
	private Force gravity ;
	
	private TranslationComposite translation = CompositeFactory.addTranslationTo(this);
	private AngularComposite angular = this.getAngularComposite();
	
	private Side ground;
	
    private final SpriteAnimated RUN_RIGHT = new SpriteAnimated( "RunRight.png", spriteOffsetX , spriteOffsetY,
    		16, 0, 75, 75, 6 );
    		
    private final SpriteAnimated RUN_LEFT = new SpriteAnimated( "Run_75px.png" , spriteOffsetX , spriteOffsetY,
    		16, 0, 75, 75 ,6 );
    
    private final SpriteAnimated SPRINT_LEFT = new SpriteAnimated( "SprintLeft2.png" , spriteOffsetX , spriteOffsetY,
    		10, 0, 75, 75 , 3);
    private final SpriteAnimated SPRINT_RIGHT = new SpriteAnimated( "SprintRight2.png" , spriteOffsetX , spriteOffsetY,
    		10, 0, 75, 75 , 3);
    
    private final SpriteAnimated IDLE_RIGHT = new SpriteAnimated( "IdleLeft.png" , spriteOffsetX , spriteOffsetY,
    		2, 0, 75, 75, 60);
    
    //
    private final SpriteAnimated IDLE_LEFT = new SpriteAnimated("IdleLeft.png" , spriteOffsetX , spriteOffsetY,
    		2, 0, 75, 75 , 60);
    
    private final SpriteAnimated CLIMB_LEFT = new SpriteAnimated( "climbSpritesheet.png" , spriteOffsetX-40 , spriteOffsetY-55,
    		18, 0, 100,165,3);
    
    private final SpriteAnimated CLIMB_RIGHT = new SpriteAnimated( "climbSpritesheet.png", spriteOffsetX , spriteOffsetY,
    		18, 0, 100,165,2);
    
    private SpriteAnimated JUMP_LEFT = new SpriteAnimated( "player_sheet.png" , spriteOffsetX , spriteOffsetY ,
    		2, 5, 32 , 32 ,16);

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
        super(x, y);
        
        RUN_RIGHT.getAnimation().setReverse();
        SPRINT_RIGHT.getAnimation().setReverse();

		name = "Player"+count;
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
       // Collider collisionMesh = new Collider( this );
        //setCollisionComposite( collisionMesh );
        // Find better method for casting
        
        Boundary boundarytemp =  new BoundaryPolygonal.EnhancedBox( 24,76 ,-12,-38, eventList );
        Boundary boundarytemp2 = new BoundarySingular();
        Boundary boundarytemp3 = new BoundaryCircular(40,this);
		
		/*for ( BoundaryVertex corner : boundarytemp.getCornersVertex() ){
			corner.setCollisionEvent( cornerCollision );
		}*/
        
		CompositeFactory.addColliderTo( this , boundarytemp );
		
        this.getColliderComposite().setLeavingCollisionEvent( new OnLeavingCollision() );
		
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
		this.gravity = this.translation.addForce( new Vector( 0 , 0.2 ) );
		this.movementForce = this.translation.addForce( new Vector( 0 , 0 ) );
		
    }
    
  
    
    @Override
    public void updateComposite() {// SPLIT INTO GENERAL UPDATE BETWEEN INPUT AND POSITION
    	super.updateComposite();
    	playerState.updateState();
 
    	//this.applyAllForces();
    	
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
		public void run( BoundaryFeature source , BoundaryFeature collidingWith, Vector separation ) {
			changePlayerState( fallingLeft );
			
		}	
    }
    
    private class FloorCollisionEvent extends CollisionEvent {

		@Override
		public void run( BoundaryFeature source , BoundaryFeature collidingWith, Vector separation ) {
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
		public void run( BoundaryFeature source , BoundaryFeature collidingWith, Vector separation ) {
			
			
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
			translation.addVelocity(ground.getSlopeVector().normalRight().unitVector().multiply(5));
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
    			
    			double runningForce = 0.1f * ( 3 - Math.abs(translation.getDX()) ) ;
    			
    			if (runningForce < 0){ runningForce = 0;}
    			
    			Vector groundVector = ground.getSlopeVector().unitVector();
    			
    			translation.setDX( playerDirection.normalize( 2 ) );
    			
    			//movementForce.setVector( groundVector.multiply(  playerDirection.normalize( 0.1 + runningForce  ) ) );
    			
    		}
    	}
    	
    	private class Neutral extends RunState{ 
    		@Override
    		public void update(){
    			movementForce.setVector(0,0);
    			if ( Math.abs(translation.getDX()) < 0.1 ){
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
			translation.addVelocity(ground.getSlopeVector().normalRight().unitVector().multiply(5));
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
			translation.setDX( playerDirection.normalize(5) );
		}
		
		@Override
		public void onJump() {
			translation.setDY(-3);
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
			adjustUpForce = translation.addForce( new Vector( 0 , 0 ) );
			adjustForwardForce = translation.addForce( new Vector( 0 , 0 ) );
			adjustBackwardForce = translation.addForce( new Vector( 0 , 0 ) );
		}
		
		@Override
		public void onEnteringState() {
			translation.setAccX( 0 );
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
			//this.adjustUpForce.setVector( orientation.unitVector().normalRight().multiply( hangAccelerationUp) );
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
			//this.adjustForwardForce.setVector( orientation.unitVector().multiply( playerDirection.normalize(hangAccelerationForward) ) );
		}
		
		@Override
		public void offForward() {
			playerStateBuffer = standing;
			this.adjustForwardForce.setVector(0,0);
		}
		
		@Override
		public void holdingBackward() {
			playerStateBuffer = standing;
			//this.adjustBackwardForce.setVector( orientation.unitVector().inverse().multiply( playerDirection.normalize(hangAccelerationBackward) ) );
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
				translation.setDY( 0.5 );
			}
		}
		
		@Override
		public void onJump() { // wall jump reverse direction 
			

			changeDirection();
			translation.setDX( playerDirection.normalize(2) );
			translation.setDY( translation.getDY()-5 );
			
		}
		
		@Override
		public void holdingForward() {
			clinging = true;
		}
		
		@Override
		public void onDown() {
			translation.setAccY( 0.2f );
			clinging = false;
		}
    	
    } 
    
    
    private class WallClimbing extends PlayerState{

		public WallClimbing(String name, Sprite spriteRight, Sprite spriteLeft) {
			super(name, spriteRight, spriteLeft);
		}
    	
		@Override
		public void onEnteringState() {
			translation.halt();
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

    
    private class UpKey extends KeyCommand{

		@Override
		public void onPressed() { playerState.onUp(); }

		@Override
		public void onReleased() { playerState.offUp(); }

		@Override
		public void onHeld(){ playerState.holdingUp(); }
		
    }
    
    private class DownKey extends KeyCommand{

		@Override
		public void onPressed() { playerState.onDown(); }

		@Override
		public void onReleased() { playerState.offDown(); }

		@Override
		public void onHeld(){ playerState.holdingDown(); }
		
    }
    
    private class RightKey extends KeyCommand{
    	

		@Override
		public void onPressed() { playerState.onRight(playerDirection); }

		@Override
		public void onReleased() { playerState.offRight(playerDirection); }

		@Override
		public void onHeld(){ playerState.holdingRight(playerDirection); }
		
    }
    
    private class LeftKey extends KeyCommand{
    	

		@Override
		public void onPressed() { playerState.onLeft(playerDirection); }

		@Override
		public void onReleased() { playerState.offLeft(playerDirection); }

		@Override
		public void onHeld(){ playerState.holdingLeft(playerDirection); }
		
    }
    
    
    private class JumpKey extends KeyCommand{
    	

		@Override
		public void onPressed() { playerState.onJump(); }

		@Override
		public void onReleased() { playerState.offJump(); }

		@Override
		public void onHeld(){ playerState.holdingJump(); }
		
    }
    
    private class ModKey extends KeyCommand{

		@Override
		public void onPressed() { playerState.onShift(); }

		@Override
		public void onReleased() { playerState.offShift(); }

		@Override
		public void onHeld(){ playerState.holdingShift(); }
		
    }
    
    private class ClickTest extends MouseCommand{

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