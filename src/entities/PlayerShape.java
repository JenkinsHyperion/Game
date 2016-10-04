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
	
	private boolean keypressA = false;
	private boolean keypressD = false;
	private boolean keypressUP = false;
	private boolean keypressS = false;
	private boolean keypressE = false;
	private boolean keypressQ = false;

	private Animation IDLE_LEFT = new Animation(LoadAnimation.buildAnimation(4, 0, 14, "bullet.png") , 4 ); 

    public PlayerShape(int x, int y , Board currentBoard) {
        super(x, y , currentBoard);

		name = "Player"+count;
        
        initPlayer();
    }

    private void initPlayer() {
        
        //setBoundingBox(14,0,4,32);
        setBoundingBox(-12,-40,24,80);
        loadAnimatedSprite(IDLE_LEFT,-7,-7);
        //setAccY(0.1f); //override gravity
        IDLE_LEFT.start();
    }
	
    //INPUT CONTROL
    
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SHIFT) {
            	
            	board.player = new PlayerCharacter(board.ICRAFT_X , board.ICRAFT_Y, board);
        	
        }

        if (key == KeyEvent.VK_A && !keypressA) {
        	keypressA = true; 
        	//x=x-1;
        }

        if (key == KeyEvent.VK_D && !keypressD) {
        	keypressD = true;
        	//x=x+1;
        }

        if (key == KeyEvent.VK_SPACE && !keypressUP) { //JUMP
        	keypressUP = true;
        	//y=y-1;
        }

        if (key == KeyEvent.VK_S) {
            keypressS = true;
            //y=y+1;
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
        	angularVelocity=0;
        }
        
        if (key == KeyEvent.VK_E) {
        	keypressE = false;
        	angularVelocity=0;
        }
    }
    
    @Override
    public void updatePosition() {//Override friction forces while running 
    	super.updatePosition();

    	
    	
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
			angularVelocity=1f;
    	}	 
    	else if (keypressE){
    		angularVelocity=-1f;
    	}	
    	

    	



	

    }   
    
    public String getPlayerStateName() {
        return "Square";
    }


    
}