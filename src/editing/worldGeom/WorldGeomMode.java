package editing.worldGeom;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import Input.*;

public abstract class WorldGeomMode {
	protected InputController inputController;
	protected KeyState keyState;
	/*public abstract void mouseInput(MouseEvent m);
	public abstract void keyInput(KeyEvent e);
	*/
	public abstract void mousePressed(MouseEvent e);
	public abstract void mouseDragged(MouseEvent e);	
	public abstract void mouseMoved(MouseEvent e);
	public abstract void mouseReleased(MouseEvent e);
	public abstract void render(Graphics g);
	
}
