package entities;


import java.awt.event.KeyEvent;

//import javax.swing.Action;
//import javax.swing.Timer;

import animation.*;
import engine.Board;
import entityComposites.Collidable;
import misc.CollisionEvent;
import misc.DefaultCollisionEvent;
import physics.Boundary;

public class PlayerCharacter extends Player {

	//private static Action enterAction;
	
	//private Timer keytimer;
	
	private boolean keypressA = false;
	private boolean keypressD = false;
	private boolean keypressUP = false;
	private boolean keypressS = false;
	private boolean keypressSHIFT = false;
	
	private final int spriteOffsetX=-38;
	private final int spriteOffsetY=-38;
	
	private boolean climbing = false;
    
    private AnimationEnhanced RUN_RIGHT = new AnimationEnhanced(LoadAnimation.buildAnimation(16, 0, 75, "RunRight.png") , 2 ,spriteOffsetX,spriteOffsetY ); 
    private Animation RUN_LEFT = new Animation(LoadAnimation.buildAnimation(16, 0, 75, "Run_75px.png") , 2 ,spriteOffsetX,spriteOffsetY); 
    
    private Animation SPRINT_LEFT = new Animation(LoadAnimation.buildAnimation(10, 0, 75, "SprintLeft2.png") , 3 ,spriteOffsetX,spriteOffsetY); 
    private Animation SPRINT_RIGHT = new Animation(LoadAnimation.buildAnimation(10, 0, 75, "SprintRight2.png") , 3 ,spriteOffsetX,spriteOffsetY);  
    
    private Animation IDLE_RIGHT = new Animation(LoadAnimation.buildAnimation(1, 2, 32, "player_sheet.png") , 18 ,spriteOffsetX,spriteOffsetY); 
    private Animation IDLE_LEFT = new Animation(LoadAnimation.buildAnimation(1, 0, 75, "IdleLeft.png") , 18 ,spriteOffsetX,spriteOffsetY);
    
    private Animation CLIMB_LEFT = new Animation(LoadAnimation.getAnimation(21, 0, 40,64 , "spritesFramesFinal.png") , 2 );
    private Animation CLIMB_RIGHT = new Animation(LoadAnimation.getAnimation(21, 1, 40,64 , "spritesFramesFinal.png") , 2 , -9, 0);
    
    private Animation JUMP_LEFT = new Animation(LoadAnimation.buildAnimation(2, 5, 32, "player_sheet.png") , 18 ); 

    private EntityState climbingRight= new EntityState("climbing_left",CLIMB_RIGHT);
    private EntityState climbingLeft= new EntityState("climbing_left",CLIMB_LEFT);
    
    private EntityState runningRight= new EntityState("running_right",RUN_RIGHT);
    private EntityState runningLeft= new EntityState("running_left",RUN_LEFT);
    private EntityState sprintingLeft= new EntityState("sprinting_left",SPRINT_LEFT);
    private EntityState sprintingRight= new EntityState("sprinting_right",SPRINT_RIGHT);
    private EntityState idlingRight= new EntityState("idle_right",IDLE_RIGHT);
    private EntityState idlingLeft= new EntityState("idle_left",IDLE_LEFT);
    private EntityState jumpingLeft= new EntityState("jumping_left",JUMP_LEFT);
    
    private EntityState playerState = idlingLeft;
    private EntityState playerStateBuffer = idlingLeft;
    
    private CollisionEvent onSideCollision = new SideCollisionEvent();

    public PlayerCharacter(int x, int y , Board currentBoard) {
        super(x, y, currentBoard);
        
        RUN_RIGHT.setReverse();
        SPRINT_RIGHT.setReverse();

		name = "Player"+count;
        board = currentBoard;
        initPlayer();
    }

    private void initPlayer() {
        
        loadAnimatedSprite(IDLE_LEFT); 
        this.getEntitySprite().setOffset(12, 38); 
        //setAngle(0);
        setAccY( 0.2f ); // Force initialize gravity (temporary)

        CollisionEvent defaultEvent = new DefaultCollisionEvent( (Collidable) this.collisionType );
        CollisionEvent[] eventList = new CollisionEvent[]{
        		defaultEvent, //top
        		onSideCollision,
        		defaultEvent,
        		onSideCollision
        };
        
        Boundary boundarytemp =  new Boundary.EnhancedBox( 24,76 ,-12,-38, eventList , (Collidable) this.collisionType );
        //Boundary boundarytemp =  new Boundary.Box( 24,76 ,-12,-38, (Collidable) this.collisionType );
		((Collidable) collisionType).setBoundary( boundarytemp ); 
		storedBounds = boundarytemp;
		boundarytemp = null;
		defaultEvent = null;
		eventList = null;
		
    }


	
    //INPUT CONTROL
    
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_1) {
        	
        	board.player = new PlayerShape(board.ICRAFT_X , board.ICRAFT_Y, board);

        }
        
        if (key == KeyEvent.VK_SHIFT && !keypressSHIFT ) {
        	keypressSHIFT = true; 

        }

        if (key == KeyEvent.VK_A && !keypressA) {
        	keypressA = true; 
        
        
        }

        if (key == KeyEvent.VK_D && !keypressD) {
        	keypressD = true;
        }

        if (key == KeyEvent.VK_SPACE && !keypressUP) { //JUMP
        	keypressUP = true;
        	if (isColliding){
        		dy -= 4;
        	}
        }

        if (key == KeyEvent.VK_S) {
            keypressS = true;
        }
    }

    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SHIFT && keypressSHIFT) {
        	keypressSHIFT = false;
        	
        }
        
        if (key == KeyEvent.VK_A && keypressA) {
        	keypressA = false;
        	
        }

        if (key == KeyEvent.VK_D && keypressD) {
        	keypressD = false;
        	

        }

        if (key == KeyEvent.VK_SPACE && keypressUP) { 
        	keypressUP = false;
        }

        if (key == KeyEvent.VK_S) {
        	keypressS = false;
        }
    }
    
    @Override
    public void updatePosition() {//Override friction forces while running 
    	super.updatePosition();
        //TESTING update enhanced run animation
        //RUN_RIGHT.updateSpeed((int) getDX(), 0, 2, 2, 10); //move to rendering
        //
        
    	//OPTIMIZE ALL THIS KEYPRESSING INTO ITS OWN ACTION CLASS
        
    	if (keypressA && isColliding ){
    		//applyAccelerationX( -0.1f );
    		if (keypressSHIFT){ //OPTIMIZE THIS TO EVENTS RATHER THAN CRAPPY CHECKS
    			dx = -5.5f;
    			setState(sprintingLeft);
    		}
    		else {
    			setState(runningLeft);
    			dx = -3;
    		}
    	}
    	else if (keypressD && isColliding){ 
    		//applyAccelerationX( 0.1f );
    		if (keypressSHIFT){
    			dx = 5.5f;
    			setState(sprintingRight);
    		}
    		else {
    			setState(runningRight);
    			dx = 3;
    		}
    	}
    	else{
    		
    		setState(idlingLeft);
    		
    	}


    }   
    

    
    private class SideCollisionEvent extends CollisionEvent{
    	
    	protected SideCollisionEvent(){
    		this.name = "SIDE COLLISION";
    	}
    	
		@Override
		public void run( ) {
			
			//System.out.println("SIDESIDE");
			dx = -1;
			
		}
    }
    
    
    
    public String getPlayerStateName() {
        return playerState.getName();
    }
    
    public EntityState getPlayerState() {
        return playerState;
    }

    public void setState( EntityState state){
    	playerState = state;
    	getEntitySprite().setSprite(state.getAnimation());
    	playerState.getAnimation().start(); //Check for redundant calls to Animation.start() method
    }
    
    
    private void setStateBuffer( EntityState state){
    	playerStateBuffer = state;
    }
    

    public void setClimb(int frame, boolean right){

    	
    	if (right){
        	playerState = climbingRight;
        	getEntitySprite().setSprite(climbingRight.getAnimation());
    	}
    	else 
    	{
	    	playerState = climbingLeft;
	    	getEntitySprite().setSprite(climbingLeft.getAnimation());
    	}
    	
    	playerState.getAnimation().reset();
    	playerState.getAnimation().setFrame(frame); 
    	climbing = true;
    }
    
    public void finishClimb(){
    	playerState = idlingLeft;
    	climbing = false;
    }
    
    public boolean isClimbing(){

    	return climbing;
    }
    public String toString() {
		return String.format(name);
	}
    
}