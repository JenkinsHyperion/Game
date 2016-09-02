package entities;


import java.awt.Shape;
import java.awt.event.KeyEvent;

//import javax.swing.Action;
//import javax.swing.Timer;

import animation.*;
import engine.Board;
import testEntities.Bullet;
import testEntities.Particle;

public class PlayerShape extends Player {

	//private static Action enterAction;
	
	
	//private Timer keytimer;
	
	private boolean keypressA = false;
	private boolean keypressD = false;
	private boolean keypressUP = false;
	private boolean keypressS = false;
	private boolean keypressE = false;
	private boolean keypressQ = false;

	private Animation IDLE_LEFT = new Animation(LoadAnimation.getAnimation(4, 0, 14, "bullet") , 4 ); 

    public PlayerShape(int x, int y) {
        super(x, y);

		name = "Player"+count;
        
        initPlayer();
    }

    private void initPlayer() {
        
        //setBoundingBox(14,0,4,32);
        setBoundingBox(-12,-40,24,80);
        loadAnimatedSprite(IDLE_LEFT,-7,-7);
        setAccY(0); //override gravity
        IDLE_LEFT.start();
    }
	
    //INPUT CONTROL
    
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SHIFT) {

        	
        }

        if (key == KeyEvent.VK_A && !keypressA) {
        	keypressA = true; 
        }

        if (key == KeyEvent.VK_D && !keypressD) {
        	keypressD = true;
            
        }

        if (key == KeyEvent.VK_SPACE && !keypressUP) { //JUMP
        	keypressUP = true;
            
        }

        if (key == KeyEvent.VK_S) {
            keypressS = true;
        }
        
        if (key == KeyEvent.VK_Q && !keypressQ) {
        	keypressQ = true; 
        }
        
        if (key == KeyEvent.VK_E && !keypressE) {
        	keypressE = true; 
        }
    }

    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

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
        
        if (key == KeyEvent.VK_Q) {
        	keypressQ = false;
        }
        
        if (key == KeyEvent.VK_E) {
        	keypressE = false;
        }
    }
    
    @Override
    public void updatePosition() {//Override friction forces while running 

    	
    	if (keypressA ){
    		dx = -2;
		
    	}
    	else if (keypressD ){ 
    			dx = 2; 
    	}
    	
    	else if (keypressUP){
				dy = -2;
		}
		
    	else if (keypressS){
			dy = 2;
    	}
    	else {dx=0; dy=0;}
    	
    	if (keypressQ){
			angle--;
        	if (angle>36){angle=-36;} //constrain range from -180 to 180 degrees for convenience
        	else if (angle<-36){angle=36;}
    	}	 
    	else if (keypressE){
    		angle++;
    		if (angle>36){angle=-36;}
        	else if (angle<-36){angle=36;}
    	}	
    	

    	setAngle(angle * ((2*Math.PI)/72) );
    	
    	//dx += accX;
    	//dy += accY;
    	
    	x = x+dx;
    	y = y+dy;

    	
		if (dx>2){
			dx=2;
		}
		else if (dx<-2){
			dx=-2;
		}		

    }   
    
    public String getPlayerStateName() {
        return "Square";
    }


    
}