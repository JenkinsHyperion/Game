package entities;


import java.awt.event.KeyEvent;

//import javax.swing.Action;
//import javax.swing.Timer;

import animation.*;
import engine.Board;
import testEntities.Bullet;
import testEntities.Particle;

public class PlayerSquare extends Player {

	//private static Action enterAction;
	
	
	//private Timer keytimer;
	
	private boolean keypressA = false;
	private boolean keypressD = false;
	private boolean keypressUP = false;
	private boolean keypressS = false;

	

    public PlayerSquare(int x, int y) {
        super(x, y);

		name = "Player"+count;
        
        initPlayer();
    }

    private void initPlayer() {
        
        //setBoundingBox(14,0,4,32);
        setBoundingBox(4,0,24,32);
        loadSprite("bullet"); 
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
    }
    
    @Override
    public void updatePosition() {//Override friction forces while running 

    	
    	if (keypressA ){
    		if (isColliding) {
    			dx = -1;
    		}
		
    	}
    	if (keypressD ){ 
    		if (isColliding) {
    			dx = 1; 
    		}
	
    	}
    	
		if (keypressUP && isColliding){
				dy = -1;
		}
    	
    	
    	dx += accX;
    	dy += accY;
    	
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