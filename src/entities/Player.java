package entities;


import Input.InputController;

//import javax.swing.Action;
//import javax.swing.Timer;

import engine.BoardAbstract;

public class Player extends EntityRotationalDynamic {

	protected transient BoardAbstract board;
	
    public Player(int x, int y , BoardAbstract testBoard) {
        super(x, y);

		name = "Player"+count;
        board = testBoard;
        initPlayer();
    }

    private void initPlayer() {
        
    }

    //INPUT CONTROL
    
    public InputController inputController = new InputController();
    
    @Override
    public void updatePosition() {//Override friction forces while running 
    	super.updatePosition();
    	

    }   
    

    public String toString() {
		return String.format(name);
	}
    
}