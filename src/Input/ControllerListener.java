package Input;

import com.studiohartman.jamepad.ControllerState;

public interface ControllerListener {

	public void buttonPressed( ControllerEvent e );
	public void buttonReleased( ControllerEvent e );
	
	public abstract boolean controllerIsConnected( ControllerState currentState );
	public abstract void getControllerEvents( ControllerState currentState, ControllerState lastState );
	
}
