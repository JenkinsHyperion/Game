package editing;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Input.*;
import editing.worldGeom.KeyState;

public abstract class ModeAbstract {
	protected InputController inputController;
	protected KeyState keyState;
	protected MouseCommand cameraPanMode;
	//protected CameraMode cameraMode;
	/*public abstract void mouseInput(MouseEvent m);
	public abstract void keyInput(KeyEvent e);
	*/
	public abstract void mousePressed(MouseEvent e);
	public abstract void mouseDragged(MouseEvent e);	
	public abstract void mouseMoved(MouseEvent e);
	public abstract void mouseReleased(MouseEvent e);
	public abstract void keyPressed(KeyEvent e);
	public abstract void keyReleased(KeyEvent e);
	public abstract void render(Graphics g);
}
