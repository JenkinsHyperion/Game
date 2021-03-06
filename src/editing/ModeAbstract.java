package editing;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import Input.*;
import editing.worldGeom.MouseMovedKeyState;

public abstract class ModeAbstract {
	protected InputManagerMouseKeyboard inputController;
	protected MouseMovedKeyState mouseMovedKeyState;
	protected MouseCommand cameraPanMode;
	protected String modeName = "unnamedMode";
	//protected CameraMode cameraMode;
	/*public abstract void mouseInput(MouseEvent m);
	public abstract void keyInput(KeyEvent e);
	*/
	
	public abstract void mousePressed(MouseEvent e);
	public abstract void mouseDragged(MouseEvent e);	
	public void mouseMoved(MouseEvent e){}
	public abstract void mouseReleased(MouseEvent e);
	public void mouseWheelScrolled(MouseWheelEvent e){}
	public abstract void keyPressed(KeyEvent e);
	public abstract void keyReleased(KeyEvent e);
	public abstract void render(Graphics g);
	public String getModeName() {
		return modeName;
	}
	public void debugDrawController(Graphics g){
		this.inputController.debugPrintInputList(800, 200, g);
	}

}
