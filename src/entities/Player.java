package entities;


import java.awt.event.KeyEvent;

//import javax.swing.Action;
//import javax.swing.Timer;

import animation.*;
import engine.Board;
import sun.util.resources.cldr.ur.CurrencyNames_ur;
import testEntities.Bullet;
import testEntities.Particle;

public class Player extends EntityRotationalDynamic {

	protected Board board;

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