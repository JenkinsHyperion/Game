package Input;

import java.awt.event.MouseEvent;

import javax.swing.event.*;
import javax.swing.event.MouseInputListener;

public interface MouseInputController {
	
	public void mousePressed(MouseEvent e);
	public void mouseDragged(MouseEvent e);
	public void mouseMoved(MouseEvent e);
	public void mouseReleased(MouseEvent e);

}
