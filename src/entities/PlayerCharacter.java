package entities;


import java.awt.event.KeyEvent;

import Input.KeyBindingAbstract;

//import javax.swing.Action;
//import javax.swing.Timer;

import animation.*;
import engine.Board;
import entityComposites.Collidable;
import physics.Collision;
import physics.Vector;
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
	
	private boolean climbing = false;
    
    private final SpriteAnimated RUN_RIGHT = new SpriteAnimated(
    		new AnimationEnhanced(LoadAnimation.buildAnimation(16, 0, 75, "RunRight.png") , 2 ),
    		this , spriteOffsetX , spriteOffsetY );
    private final SpriteAnimated RUN_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(16, 0, 75, "Run_75px.png") , 2 ),
    		this , spriteOffsetX , spriteOffsetY );
    
    private final SpriteAnimated SPRINT_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(10, 0, 75, "SprintLeft2.png") , 3 ),
    		this , spriteOffsetX , spriteOffsetY );
    private final SpriteAnimated SPRINT_RIGHT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(10, 0, 75, "SprintRight2.png") , 3 ),
    		this , spriteOffsetX , spriteOffsetY ); 
    
    private final SpriteAnimated IDLE_RIGHT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(2, 0, 75, "IdleLeft.png") , 18 ),
    		this , spriteOffsetX , spriteOffsetY );
    
    //
    private final SpriteAnimated IDLE_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(2, 0, 75, "IdleLeft.png") , 18 ),
    		this , spriteOffsetX , spriteOffsetY );
    
    private final SpriteAnimated CLIMB_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.getAnimation(21, 0, 40,64 , "spritesFramesFinal.png") , 2 ),
    		this , spriteOffsetX , spriteOffsetY );
    private final SpriteAnimated CLIMB_RIGHT = new SpriteAnimated(
    		new Animation(LoadAnimation.getAnimation(21, 1, 40,64 , "spritesFramesFinal.png") , 2 ),
    		this , spriteOffsetX , spriteOffsetY );
    
    private SpriteAnimated JUMP_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(2, 5, 32, "player_sheet.png") , 18 ),
    		this , spriteOffsetX , spriteOffsetY );

    //private PlayerState climbingRight= new EntityState("climbing_left",CLIMB_RIGHT);
    //private PlayerState climbingLeft= new EntityState("climbing_left",CLIMB_LEFT);
    
    private final PlayerState running= new Running( "Running" , RUN_RIGHT , RUN_LEFT);
    private final PlayerState sprintingRight= new Sprinting("sprinting_right",SPRINT_RIGHT , SPRINT_LEFT );
    private final PlayerState standing = new Standing("standing", IDLE_RIGHT , IDLE_LEFT);
    private final PlayerState runningTurn = new RunningTurn("turning", IDLE_RIGHT , IDLE_LEFT);
    private final PlayerState sprintingTurn = new SprintingTurn("skidding", IDLE_RIGHT , IDLE_LEFT);
    private final PlayerState wallSliding = new WallSliding("WallSliding", IDLE_RIGHT , IDLE_LEFT);
    //private PlayerState jumpingLeft= new EntityState("jumping_left",JUMP_LEFT);
    
    private final PlayerState fallingLeft = new Falling( "Falling" , JUMP_LEFT , JUMP_LEFT );
    
    private PlayerState playerState = fallingLeft;
    private PlayerState playerStateBuffer = standing;
    
    private PlayerDirection playerDirection = new PlayerDirection();
    
    private final CollisionEvent onSideCollision = new SideCollisionEvent();

    public PlayerCharacter(int x, int y , Board currentBoard) {
        super(x, y, currentBoard);
        
        RUN_RIGHT.getAnimation().setReverse();
        SPRINT_RIGHT.getAnimation().setReverse();

		name = "Player"+count;
        board = currentBoard;
        initPlayer();
    }

    private void initPlayer() {
        
        loadAnimatedSprite(IDLE_LEFT); 
        //setAngle(0);
        //setAccY( 0.2f ); // Force initialize gravity (temporary)
        ((Collidable) collisionType).addForce( new Vector( 0 , 0.2 ) );
        
        CollisionEvent floorCollisionEvent = new DefaultCollisionEvent(  );
        CollisionEvent[] eventList = new CollisionEvent[]{
        		floorCollisionEvent, //top
        		onSideCollision,
        		new FloorCollisionEvent(),
        		onSideCollision
        };
        
        ((Collidable) getCollisionType() ).setLeavingCollisionEvent( new OnLeavingCollision() );
        // Find better method for casting
        
        Boundary boundarytemp =  new Boundary.EnhancedBox( 24,76 ,-12,-38, eventList , (Collidable) this.collisionType );
        //Boundary boundarytemp =  new Boundary.Box( 24,76 ,-12,-38, (Collidable) this.collisionType );
		((Collidable) collisionType).setBoundary( boundarytemp ); 
		storedBounds = new Boundary.Box(24,76 ,-12,-38, (Collidable) this.collisionType );   //OPTIMIZE move to child RotationalCollidable that can store boundary 
		boundarytemp = null;
		floorCollisionEvent = null;
		eventList = null;
		
		
		
		this.inputController.addInputKey( new UpKey( KeyEvent.VK_W ) );
		this.inputController.addInputKey( new LeftKey( KeyEvent.VK_A ) );
		this.inputController.addInputKey( new DownKey( KeyEvent.VK_S ) );
		this.inputController.addInputKey( new RightKey( KeyEvent.VK_D ) );
		
		this.inputController.addInputKey( new JumpKey( KeyEvent.VK_SPACE ) );
		this.inputController.addInputKey( new ModKey( KeyEvent.VK_SHIFT ) );
		
    }
    
  
    
    @Override
    public void updatePosition() {// SPLIT INTO GENERAL UPDATE BETWEEN INPUT AND POSITION
    	super.updatePosition();
    	playerState.updateState();
    	
    	//TESTING FORCES
    	this.accX = (float) ((Collidable)collisionType).sumOfForces().getX();
    	this.accY = (float) ((Collidable)collisionType).sumOfForces().getY();
    	
    	
    }   
    
    private void changePlayerState( PlayerState state ){
    	
    	System.out.println("Changing state to "+state.getName());
    	
		playerState = state;
		
		entitySprite = playerDirection.getDirectionalSprite(state) ;
		
		((SpriteAnimated) playerDirection.getDirectionalSprite(state) ).getAnimation().start();
		
		//inputController.runReleased();

		inputController.runHeld();
		
		playerState.uponChange();
  	
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
		}
		@Override
		public String toString() {
			return "Floor";
		}
    }
    
    private class SideCollisionEvent extends CollisionEvent{
	
		@Override
		public void run( BoundaryFeature source , BoundaryFeature collidingWith ) {
			
				changePlayerState( wallSliding );
		}
		
		@Override
		public String toString() {
			return "Side";
		}
    }
    
    
    
    public String getPlayerStateName() {
        return playerState.getName();
    }
    
    public EntityState getPlayerState() {
        return playerState;
    }
    
    private void setStateBuffer( PlayerState state){
    	playerStateBuffer = state;
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
			setDY(-5);
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
    	
    	private abstract class RunState{ public void update(){} } //CONSIDER SEPERATE IDLERUN STATE
    	
    	private class Accelerating extends RunState{ 
    		
    		public void update(){ dx = playerDirection.normalize( 3 ); }
    	}
    	
    	private class Neutral extends RunState{ public void update(){/*nothing*/} }
    	
    	private final RunState idle = new Neutral();
    	private final RunState accelerate = new Accelerating();
    	private RunState state = accelerate;

		public Running( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft);
		}
		
		@Override
		public void uponChange() {
			
		}
		
		@Override
		public void updateState() {
			state.update();
			
			//((AnimationEnhanced) RUN_RIGHT.getAnimation()).updateLinkedSpeed((int)-dx, 2, 20, 0, 3);
			
			if (dx == 0){
				changePlayerState(standing);
			}
		}
		
		@Override
		public void onJump() {
			setDY(-5);
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
    	public void uponChange() {
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
			setDY(-5);
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
    	public void uponChange() {
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
    	
    	private final float hangAccelerationUp = 0.1f;
    	private final float hangAccelerationForward = 0.02f;
    	private final float hangAccelerationBackward = 0.02f;
    	
		public Falling( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft);
		}
		
		@Override
		public void onUp() {
			
		}
		
		@Override
		public void holdingJump() {
			accY = hangAccelerationUp;
		}
		
		@Override
		public void offJump() {
			accY=0.2f;
		}
		
		@Override
		public void holdingForward() {
			playerStateBuffer = running;
			accX=(float)playerDirection.normalize( hangAccelerationForward );
		}
		
		@Override
		public void offForward() {
			playerStateBuffer = standing;
			accX=0;
		}
		
		@Override
		public void holdingBackward() {
			playerStateBuffer = standing;
			accX=(float)playerDirection.normalize( -hangAccelerationBackward );
		}
		
		@Override
		public void offBackward() {
			playerStateBuffer = standing;
			accX=0;
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
		public void uponChange() {
			clinging = false;
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
    
    
    
    /* #############################################################################
     * 			KEYBINDINGS  -  MOVE ELSEWHERE TO SOME CONTROLLER CONFIG
 	 * #############################################################################
     */

    
    private class UpKey extends KeyBindingAbstract{
    	
		protected UpKey(int keycode) {
			super(keycode);
		}

		@Override
		public void onPressed() { playerState.onUp(); }

		@Override
		public void onReleased() { playerState.offUp(); }

		@Override
		public void onHeld(){ playerState.holdingUp(); }
		
    }
    
    private class DownKey extends KeyBindingAbstract{
    	
		protected DownKey(int keycode) {
			super(keycode);
		}

		@Override
		public void onPressed() { playerState.onDown(); }

		@Override
		public void onReleased() { playerState.offDown(); }

		@Override
		public void onHeld(){ playerState.holdingDown(); }
		
    }
    
    private class RightKey extends KeyBindingAbstract{
    	
		protected RightKey(int keycode) {
			super(keycode);
		}

		@Override
		public void onPressed() { playerState.onRight(playerDirection); }

		@Override
		public void onReleased() { playerState.offRight(playerDirection); }

		@Override
		public void onHeld(){ playerState.holdingRight(playerDirection); }
		
		@Override
		public String toString() {
			return "Right";
		}
		
    }
    
    private class LeftKey extends KeyBindingAbstract{
    	
		protected LeftKey(int keycode) {
			super(keycode);
		}

		@Override
		public void onPressed() { playerState.onLeft(playerDirection); }

		@Override
		public void onReleased() { playerState.offLeft(playerDirection); }

		@Override
		public void onHeld(){ playerState.holdingLeft(playerDirection); }
		
    }
    
    
    private class JumpKey extends KeyBindingAbstract{
    	
		protected JumpKey(int keycode) {
			super(keycode);
		}

		@Override
		public void onPressed() { playerState.onJump(); }

		@Override
		public void onReleased() { playerState.offJump(); }

		@Override
		public void onHeld(){ playerState.holdingJump(); }
		
    }
    
    private class ModKey extends KeyBindingAbstract{
    	
		protected ModKey(int keycode) {
			super(keycode);
		}

		@Override
		public void onPressed() { playerState.onShift(); }

		@Override
		public void onReleased() { playerState.offShift(); }

		@Override
		public void onHeld(){ playerState.holdingShift(); }
		
    }
    
    
}