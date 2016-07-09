package entities;


import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.Timer;

import animation.*;
import animation.AnimationState;
import animation.LoadAnimation;
import engine.Board;
import testEntities.Bullet;
import testEntities.Particle;

public class Player extends EntityDynamic {

	private static Action enterAction;
	
	
	private Timer keytimer;
	
	private boolean keypressA = false;
	private boolean keypressD = false;
	private boolean keypressUP = false;
	
	private boolean climbing = false;
    
    private AnimationEnhanced RUN_RIGHT = new AnimationEnhanced(LoadAnimation.getAnimation(8, 1, 32, "player_sheet") , 2 ); 	
    private Animation RUN_LEFT = new Animation(LoadAnimation.getAnimation(8, 0, 32, "player_sheet") , 2 ); 
    private Animation IDLE_RIGHT = new Animation(LoadAnimation.getAnimation(2, 2, 32, "player_sheet") , 18 ); 
    private Animation IDLE_LEFT = new Animation(LoadAnimation.getAnimation(2, 3, 32, "player_sheet") , 18 );
    
    private Animation CLIMB_LEFT = new Animation(LoadAnimation.getAnimation(21, 0, 40,64 , "spritesFramesFinal") , 3 );
    
    private Animation JUMP_LEFT = new Animation(LoadAnimation.getAnimation(2, 5, 32, "player_sheet") , 18 ); 

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

        initPlayer();
    }

    private void initPlayer() {
        
        setBoundingBox(8,0,12,32);
        loadAnimatedSprite(IDLE_LEFT);
        setAccY((float) 0.1); // Force initialize gravity (temporary)
    }


	
    //INPUT CONTROL
    
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SPACE) {

        	//Generate particle and add to current Board's object list
        	
        	if (playerState == idlingLeft){ // Player can only shoot from certain states.
        		// This really should be a method canShoot() that can be set by changing states. Then you'd be able to shoot
        		//from multiple states.
        		Board.BoardAccess.spawnDynamicEntity( new Bullet(getX(),getY(),-2,0) );
        	}
        	else if (playerState == idlingRight){
        		Board.BoardAccess.spawnDynamicEntity( new Bullet(getX(),getY(),2,0) );
        	}
        	
        }

        if (key == KeyEvent.VK_LEFT && !keypressA) {
        	keypressA = true;
        }

        if (key == KeyEvent.VK_RIGHT && !keypressD) {
        	keypressD = true;
            
        }

        if (key == KeyEvent.VK_UP && !keypressUP) { //JUMP
        	keypressUP = true;
        	
    		if (keypressUP){
    			if (dy == 0){
    				dy = -2.5f;
    			}
        	
    			//Zero all x acceleration when leaving ground. Should be handled in its own collision class.
    			//accX = 0;
        	
    			//setState(jumpingLeft);
    			//setStateBuffer(idlingRight);
    		}

            
        }

        if (key == KeyEvent.VK_DOWN) {
            //dy = 1;
        }
    }

    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT && keypressA) {
        	keypressA = false;
        	
        	//test dust particles. Can go in own method if we enjoy every collision having particles/sparks.
        	if (getDY()==0 && getDX()==-2){ //Check to see if on ground. Should check state once states are stable, not DY.
        		Board.BoardAccess.spawnDynamicEntity( new Particle( getX()+16,getY()+30,-2, -0.75f) );
        		Board.BoardAccess.spawnDynamicEntity( new Particle( getX()+16,getY()+30,-0.9f, -0.5f) );
        		Board.BoardAccess.spawnDynamicEntity( new Particle( getX()+16,getY()+30,-2, -0.25f) );
        	}
        	
        }

        if (key == KeyEvent.VK_RIGHT && keypressD) {
        	keypressD = false;
        	
        	//test making dust particles
        	if (getDY()==0 && getDX()==2){ //Check to see if on ground
        		Board.BoardAccess.spawnDynamicEntity( new Particle( getX()+20,getY()+30,2, -0.5f) );
        		Board.BoardAccess.spawnDynamicEntity( new Particle( getX()+20,getY()+30,1.5f, -0.5f) );
        		Board.BoardAccess.spawnDynamicEntity( new Particle( getX()+20,getY()+30,2, -0.25f) );
        	}
        }

        if (key == KeyEvent.VK_UP && keypressUP) { 
        	keypressUP = false;
        }

        if (key == KeyEvent.VK_DOWN) {
        	
        }
    }
    
    @Override
    public void updatePosition() {//Override friction forces while running 
    	
        //TESTING update enhanced run animation
        RUN_RIGHT.updateSpeed((int) getDX(), 0, 2, 2, 10);
    	
    	if (keypressA && dy==0 ){
    		//dx = -2;
    		accX = (float) -0.2;
    				
    	}
    	if (keypressD && dy==0){
    		//dx = 2;
    		accX = (float) 0.2;
    		
    	}
    	
    	
    	dx += accX;
    	dy += accY;
    	
    	x = Math.round(x+dx);
    	y = Math.round(y+dy);

    	
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
		else {
			setDX(0);
		}
		

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
    

    public void setClimb(int frame){
    	playerState = climbingLeft;
    	getObjectGraphic().setSprite(climbingLeft.getAnimation());
    	playerState.getAnimation().reset();
    	playerState.getAnimation().start(frame);

    	climbing = true;
    }
    
    public void finishClimb(){
    	playerState = idlingLeft;
    	climbing = false;
    }
    
    public boolean isClimbing(){

    	return climbing;
    }
    
    
}