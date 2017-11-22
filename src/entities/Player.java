package entities;


import Input.InputManager;
import Input.InputManagerController;
import Input.InputManagerMouseKeyboard;

//import javax.swing.Action;
//import javax.swing.Timer;

import engine.BoardAbstract;
import entityComposites.EntityStatic;

public class Player extends EntityStatic {
	
    public InputManager currentInputManager; //TODO restrict access
    public InputManagerMouseKeyboard mouseAndKeyInputManager;
    public InputManagerController controllerInputManager;

    public Player(int x, int y) {
        super(x, y);

		name = "Player"+count;
        initPlayer();
    }

    private void initPlayer() {
        
    }

    public String toString() {
		return String.format(name);
	}
    
}