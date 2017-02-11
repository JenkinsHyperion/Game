package editing.worldGeom;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import Input.*;

public abstract class WorldGeomMode extends InputController implements MouseInputController{

	public abstract void render(Graphics g);
	@Override
	public abstract void mousePressed(MouseEvent e);
	
	@Override
	public abstract void mouseDragged(MouseEvent e);
	@Override
	public abstract void mouseMoved(MouseEvent e);
	@Override
	public abstract void mouseReleased(MouseEvent e);

}
