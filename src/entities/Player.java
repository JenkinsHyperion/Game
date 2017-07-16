package entities;


import Input.InputController;

//import javax.swing.Action;
//import javax.swing.Timer;

import engine.BoardAbstract;
import entityComposites.EntityStatic;

public class Player extends EntityStatic {

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
    
    public InputController inputController = new InputController("Player controller");

    @Override
    public void updateComposite() {
    	super.updateComposite();
    }

    public String toString() {
		return String.format(name);
	}
    
}