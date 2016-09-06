package entities;


import java.awt.event.KeyEvent;

//import javax.swing.Action;
//import javax.swing.Timer;

import animation.*;
import engine.Board;
import testEntities.Bullet;
import testEntities.Particle;

public class Player extends EntityRotationalDynamic {

	//private static Action enterAction;
	
	
	//private Timer keytimer;
	
	private boolean keypressA = false;
	private boolean keypressD = false;
	private boolean keypressUP = false;
	private boolean keypressS = false;
	
	private boolean climbing = false;
    
    private AnimationEnhanced RUN_RIGHT = new AnimationEnhanced(LoadAnimation.getAnimation(8, 1, 32, "player_sheet") , 2 ); 	
    private Animation RUN_LEFT = new Animation(LoadAnimation.getAnimation(8, 0, 32, "player_sheet") , 2 ); 
    private Animation IDLE_RIGHT = new Animation(LoadAnimation.getAnimation(2, 2, 32, "player_sheet") , 18 ); 
    private Animation IDLE_LEFT = new Animation(LoadAnimation.getAnimation(2, 3, 32, "player_sheet") , 18 );
    
    private Animation CLIMB_LEFT = new Animation(LoadAnimation.getAnimation(21, 0, 40,64 , "spritesFramesFinal") , 2 );
    private Animation CLIMB_RIGHT = new Animation(LoadAnimation.getAnimation(21, 1, 40,64 , "spritesFramesFinal") , 2 , -9, 0);
    
    private Animation JUMP_LEFT = new Animation(LoadAnimation.getAnimation(2, 5, 32, "player_sheet") , 18 ); 

    private AnimationState climbingRight= new AnimationState("climbing_left",CLIMB_RIGHT);
    private AnimationState climbingLeft= new AnimationState("climbing_left",CLIMB_LEFT);
    
    private AnimationState runningRight= new AnimationState("running_right",RUN_RIGHT);
    private AnimationState runningLeft= new AnimationState("running_left",RUN_LEFT);
    private AnimationState idlingRight= new AnimationState("idle_right",IDLE_RIGHT);
    private AnimationState idlingLeft= new AnimationState("idle_left",IDLE_LEFT);
    private AnimationState jumpingLeft= new AnimationState("jumping_left",JUMP_LEFT);
    
    private AnimationState playerState = idlingLeft;
    private AnimationState playerStateBuffer = idlingLeft;

    public Player(int x, int y) {
        super(x, y);

		name = "Player"+count;
        
        initPlayer();
    }

    private void initPlayer() {
        
        setBoundingBox(-12,-16,24,32);
        loadAnimatedSprite(IDLE_LEFT); 
        //setAngle(0);
        //setAccY( 0.1f ); // Force initialize gravity (temporary)
    }


	
    //INPUT CONTROL
    
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SHIFT) {

        	/*//Generate particle and add to current Board's object list
        	
        	if (playerState == idlingLeft){ // Player can only shoot from certain states.
        		// This really should be a method canShoot() that can be set by changing states. Then you'd be able to shoot
        		//from multiple states.
        		Board.BoardAccess.spawnDynamicEntity( new Bullet(getX(),getY(),-2,0) );
        	}
        	else if (playerState == idlingRight){
        		Board.BoardAccess.spawnDynamicEntity( new Bullet(getX(),getY(),2,0) );
        	}*/
        	
        }

        if (key == KeyEvent.VK_A && !keypressA) {
        	keypressA = true; 
        }

        if (key == KeyEvent.VK_D && !keypressD) {
        	keypressD = true;
            
        }

        if (key == KeyEvent.VK_SPACE && !keypressUP) { //JUMP
        	dy = -2.5f;
        	keypressUP = true;
            
        }

        if (key == KeyEvent.VK_S) {
            keypressS = true;
        }
    }

    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_A && keypressA) {
        	keypressA = false;
        	
        	//test dust particles. Can go in own method if we enjoy every collision having particles/sparks.
        	if (getDY()==0 && getDX()==-2){ //Check to see if on ground. Should check state once states are stable, not DY.
        	}
        	
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
    	
        //TESTING update enhanced run animation
        RUN_RIGHT.updateSpeed((int) getDX(), 0, 2, 2, 10);
        //
        dx += accX;
    	dy += accY;
    	
    	x = x+dx;
    	y = y+dy;
        
    	//setAngle(0);
        
    	/*if (keypressA && !climbing ){
    		if (isColliding) {
    			accX = -0.1f ; 
    		}

    				
    	}
    	if (keypressD && !climbing){ 
    		if (isColliding) {
    			accX = 0.1f ; 
    		}

    		
    	}
    	
		if (keypressUP && isColliding && !climbing){
				dy -= 2.5f;
		}

    	

    	
		if (dx>2){
			dx=2;
		}
		else if (dx<-2){
			dx=-2;
		}
		
		
		if (!climbing){
			if (keypressA){
				setState(runningLeft);
				setStateBuffer(idlingLeft);
			}
			else if (keypressD){
				setState(runningRight);
				setStateBuffer(idlingRight);
			}
			else
				setState(playerStateBuffer);
			
		}
		else { //climbing
			
			if (keypressA || keypressD) {
				playerState.getAnimation().start(); //climb when holding jump
			}
			
			if (keypressS){
				
				//abort climb
				playerState = idlingRight;
	        	getObjectGraphic().setSprite(idlingRight.getAnimation());
				
			}
			
		}*/
		
    	if (keypressA ){
    		//accX = -0.2f ; 
    		dx = -1;
		
    	}
    	else if (keypressD ){ 
    		//accX = 0.2f ; 
    		dx = 1; 
    	}
    	
    	else if (keypressUP){
    		//dy = -2.5f;
		}
		
    	else if (keypressS){
			dy = 2;
    	}
    	//else {dx=0; dy=0;}
    	

    }   
    
    public String getPlayerStateName() {
        return playerState.getName();
    }
    
    public AnimationState getPlayerState() {
        return playerState;
    }

    public void setState( AnimationState state){
    	playerState = state;
    	getObjectGraphic().setSprite(state.getAnimation());
    	playerState.getAnimation().start(); //Check for redundant calls to Animation.start() method
    }
    
    
    private void setStateBuffer( AnimationState state){
    	playerStateBuffer = state;
    }
    

    public void setClimb(int frame, boolean right){

    	
    	if (right){
        	playerState = climbingRight;
        	getObjectGraphic().setSprite(climbingRight.getAnimation());
    	}
    	else 
    	{
	    	playerState = climbingLeft;
	    	getObjectGraphic().setSprite(climbingLeft.getAnimation());
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