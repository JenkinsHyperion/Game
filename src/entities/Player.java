package entities;


import Input.InputController;

//import javax.swing.Action;
//import javax.swing.Timer;

import engine.BoardAbstract;
import entityComposites.EntityStatic;

public class Player extends EntityStatic {
	
    public Player(int x, int y) {
        super(x, y);

		name = "Player"+count;
        initPlayer();
    }

    private void initPlayer() {
        
    }

    //INPUT CONTROL
    
    public InputController inputController = new InputController("Player controller");

    public String toString() {
		return String.format(name);
	}
    
}