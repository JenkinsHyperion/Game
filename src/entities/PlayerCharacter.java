package entities;


import java.awt.event.KeyEvent;

import Input.KeyBinding;

//import javax.swing.Action;
//import javax.swing.Timer;

import animation.*;
import engine.Board;
import entityComposites.Collidable;
import misc.CollisionEvent;
import misc.DefaultCollisionEvent;
import misc.EntityState;
import misc.PlayerState;
import misc.Direction.Direction;
import physics.Boundary;
import sprites.Sprite;
import sprites.SpriteAnimated;

public class PlayerCharacter extends Player {
	
	private final int spriteOffsetX=-75;
	private final int spriteOffsetY=-75;
	
	private boolean climbing = false;
    
    private final SpriteAnimated RUN_RIGHT = new SpriteAnimated(
    		new AnimationEnhanced(LoadAnimation.buildAnimation(16, 0, 75, "RunRight.png") , 2 ,spriteOffsetX,spriteOffsetY ),
    		this );
    private final SpriteAnimated RUN_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(16, 0, 75, "Run_75px.png") , 2 ,spriteOffsetX,spriteOffsetY),
    		this);
    
    private final SpriteAnimated SPRINT_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(10, 0, 75, "SprintLeft2.png") , 3 ,spriteOffsetX,spriteOffsetY),
    		this);
    private final SpriteAnimated SPRINT_RIGHT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(10, 0, 75, "SprintRight2.png") , 3 ,spriteOffsetX,spriteOffsetY),
    		this); 
    
    private final SpriteAnimated IDLE_RIGHT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(1, 0, 75, "IdleLeft.png") , 18 ,spriteOffsetX,spriteOffsetY),
    		this);
    private final SpriteAnimated IDLE_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(1, 0, 75, "IdleLeft.png") , 18 ,spriteOffsetX,spriteOffsetY),
    		this);
    
    private final SpriteAnimated CLIMB_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.getAnimation(21, 0, 40,64 , "spritesFramesFinal.png") , 2 ),
    		this);
    private final SpriteAnimated CLIMB_RIGHT = new SpriteAnimated(
    		new Animation(LoadAnimation.getAnimation(21, 1, 40,64 , "spritesFramesFinal.png") , 2 , -9, 0),
    		this);
    
    private SpriteAnimated JUMP_LEFT = new SpriteAnimated(
    		new Animation(LoadAnimation.buildAnimation(2, 5, 32, "player_sheet.png") , 18 ),
    		this);

    //private PlayerState climbingRight= new EntityState("climbing_left",CLIMB_RIGHT);
    //private PlayerState climbingLeft= new EntityState("climbing_left",CLIMB_LEFT);
    
    private final PlayerState running= new Running( "Running" , RUN_RIGHT , RUN_LEFT);
    private final PlayerState sprintingRight= new Sprinting("sprinting_right",SPRINT_RIGHT , SPRINT_LEFT );
    private final PlayerState standing = new Standing("standing", IDLE_RIGHT , IDLE_LEFT);
    //private PlayerState jumpingLeft= new EntityState("jumping_left",JUMP_LEFT);
    
    private final PlayerState fallingLeft = new Falling( "Falling" , JUMP_LEFT , JUMP_LEFT );
    
    private PlayerState playerState = running;
    private PlayerState playerStateBuffer = running;
    
    private Direction playerDirection = new Direction();
    
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
        this.getEntitySprite().setOffset(12, 38); 
        //setAngle(0);
        setAccY( 0.2f ); // Force initialize gravity (temporary)

        CollisionEvent floorCollisionEvent = new DefaultCollisionEvent( (Collidable) this.collisionType );
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
		storedBounds = boundarytemp;
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
    	playerState.update();
    	
    	//inputController.runHeld();
    	
    }   
    
    private void changePlayerState( PlayerState state ){
    	
		playerState = state;
		
		entitySprite = state.getSpriteRight() ;
		
		((SpriteAnimated) state.getSpriteRight() ).getAnimation().start();
		
		inputController.runHeld();
		  	
    }
    
    private void changeDirection(){
    	
    	playerDirection.reveseDirection();
    	
    	
    }

    private class OnLeavingCollision extends CollisionEvent{
		@Override
		public void run() {
			changePlayerState( fallingLeft );
		}	
    }
    
    private class FloorCollisionEvent extends CollisionEvent {
		@Override
		public void run() {
			
			playerState.onCollision();
		}
    }
    
    private class SideCollisionEvent extends CollisionEvent{
    	
    	protected SideCollisionEvent(){
    		this.name = "SIDE COLLISION";
    	}
    	
		@Override
		public void run( ) {
			
			dx = -1;
			
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

		public Running( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft);
		}
		
		@Override
		public void update() {
			dx = playerDirection.normalize(3);
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
		public void offForward() {
			changePlayerState( standing );
		}
		
    	
    }
    
    
    private class Sprinting extends PlayerState{ 

		public Sprinting( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft);
		}

		@Override
		public void update() {
			dx = playerDirection.normalize(5);
		}
		
		@Override
		public void offShift() {
			changePlayerState( running );
		}
    	
    }
    
    
    private class Falling extends PlayerState{

		public Falling( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft);
		}
		
		@Override
		public void holdingForward() {
			playerStateBuffer = running;
		}
		
		@Override
		public void offForward() {
			playerStateBuffer = standing;
		}
		
		@Override
		public void onCollision() {
			
			changePlayerState(playerStateBuffer);
		}
    	
    }
    

    
    private class WallSliding extends PlayerState{

		public WallSliding( String name , Sprite spriteRight , Sprite spriteLeft) {
			super( name , spriteRight , spriteLeft );
		}
		
    	
    } 
    
    
    
    /* #############################################################################
     * 			KEYBINDINGS  -  MOVE ELSEWHERE TO SOME CONTROLLER CONFIG
 	 * #############################################################################
     */

    
    private class UpKey extends KeyBinding{
    	
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
    
    private class DownKey extends KeyBinding{
    	
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
    
    private class RightKey extends KeyBinding{
    	
		protected RightKey(int keycode) {
			super(keycode);
		}

		@Override
		public void onPressed() { playerState.onRight(playerDirection); }

		@Override
		public void onReleased() { playerState.offRight(playerDirection); }

		@Override
		public void onHeld(){ playerState.holdingRight(playerDirection); }
		
    }
    
    private class LeftKey extends KeyBinding{
    	
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
    
    
    private class JumpKey extends KeyBinding{
    	
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
    
    private class ModKey extends KeyBinding{
    	
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