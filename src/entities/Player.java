package entities;


import java.awt.event.KeyEvent;

//import javax.swing.Action;
//import javax.swing.Timer;

import engine.Board;

public class Player extends EntityRotationalDynamic {

	protected transient Board board;

    public Player(int x, int y , Board currentBoard) {
        super(x, y);

		name = "Player"+count;
        board = currentBoard;
        initPlayer();
    }

    private void initPlayer() {
        
    }

    //INPUT CONTROL
    
    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }
    
    @Override
    public void updatePosition() {//Override friction forces while running 
    	super.updatePosition();
    	

    }   
    

    public String toString() {
		return String.format(name);
	}
    
}