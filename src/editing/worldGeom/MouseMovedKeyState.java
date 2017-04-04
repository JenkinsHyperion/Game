package editing.worldGeom;

import java.awt.event.MouseEvent;

/**
 * KeyState is mostly intended to handle mouseMoved() events since
 * the InputController isn't equipped to handle events with no associated button.
 * @author Dave
 *
 */
public interface MouseMovedKeyState {
	
	public abstract void mouseMoved(MouseEvent e);

}
