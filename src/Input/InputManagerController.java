package Input;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class InputManagerController extends InputManager implements ControllerListener{

	private static final float LEFT_ANALOG_STICK_CUTOFF = 0.05f; //Minimum magnitude of analog stick before counting as zero
	private static final float RIGHT_ANALOG_STICK_CUTOFF = 0.05f;
	
	ArrayList< ControllerButtonBinding > buttonsListening = new ArrayList<>();
	ArrayList< ControllerButtonBinding > buttonsHeld = new ArrayList<>();
	
	ArrayList< AnalogStickBinding > leftAnalogStickListening = new ArrayList<>();
	ArrayList< AnalogStickBinding > leftAnalogStickHeld = new ArrayList<>();
	
	public InputManagerController() {
		name = "Controller Input Manager";
	}
	
	@Override
	public boolean controllerIsConnected( ControllerState currentState ){
		return currentState.isConnected;
	}
	@Override
	public void getControllerEvents( ControllerState currentState, ControllerState lastState ){
		
	//ANALOG STICKS
		//if ( currentState.leftStickMagnitude > LEFT_ANALOG_STICK_CUTOFF ){

		if ( leftAnalogStickListening.size() == 1 ){
			if ( currentState.leftStickMagnitude > LEFT_ANALOG_STICK_CUTOFF ){
				leftAnalogStickListening.get(0).onMoved(currentState.leftStickAngle, currentState.leftStickX,currentState.leftStickY, currentState.leftStickMagnitude);
				leftStickTilted();
			}
		}
		if ( leftAnalogStickHeld.size() == 1 ){
			leftAnalogStickHeld.get(0).updatePosition( currentState.leftStickAngle, currentState.leftStickMagnitude);
			leftAnalogStickHeld.get(0).onTilted(currentState.leftStickAngle, currentState.leftStickX,currentState.leftStickY, currentState.leftStickMagnitude);
			
			if ( currentState.leftStickMagnitude < LEFT_ANALOG_STICK_CUTOFF ){
				leftAnalogStickHeld.get(0).onReturned();
				leftStickReturned();
			}

		}
		
	//LEFT
		if(currentState.dpadLeft && !lastState.dpadLeft ) {
			buttonPressed( new ControllerEvent( ControllerEvent.VC_LEFT ) );
		}
		else if(!currentState.dpadLeft && lastState.dpadLeft) {
			buttonReleased( new ControllerEvent( ControllerEvent.VC_LEFT ) );
		} 
	//RIGHT	
		if(currentState.dpadRight && !lastState.dpadRight ) {
			buttonPressed( new ControllerEvent( ControllerEvent.VC_RIGHT ) );
		}
		else if(!currentState.dpadRight && lastState.dpadRight) {
			buttonReleased( new ControllerEvent( ControllerEvent.VC_RIGHT ) );
		} 
	//UP
		if(currentState.dpadUp && !lastState.dpadUp ) {
			buttonPressed( new ControllerEvent( ControllerEvent.VC_UP ) );
		}
		else if(!currentState.dpadUp && lastState.dpadUp) {
			buttonReleased( new ControllerEvent( ControllerEvent.VC_UP ) );
		} 
	//DOWN
		if(currentState.dpadDown && !lastState.dpadDown ) {
			buttonPressed( new ControllerEvent( ControllerEvent.VC_DOWN ) );
		}
		else if(!currentState.dpadDown && lastState.dpadDown) {
			buttonReleased( new ControllerEvent( ControllerEvent.VC_DOWN ) );
		} 
	//A
		if(currentState.a && !lastState.a ) {
			buttonPressed( new ControllerEvent( ControllerEvent.VC_A ) );
		}
		else if(!currentState.a && lastState.a) {
			buttonReleased( new ControllerEvent( ControllerEvent.VC_A ) );
		} 
	//B
		if(currentState.b && !lastState.b ) {
			buttonPressed( new ControllerEvent( ControllerEvent.VC_B ) );
		}
		else if(!currentState.b && lastState.b) {
			buttonReleased( new ControllerEvent( ControllerEvent.VC_B ) );
		} 

	}

	private boolean noDuplicateBindingExists( ControllerButtonBinding newBinding ){
		for ( ControllerButtonBinding binding : buttonsListening ){
			if ( newBinding.getCode() == binding.getCode() ){
				return false;
			}
		}
		return true;
	}
	
	public void createButtonBinding( int inputKey , KeyCommand command ){
		
		ControllerButtonBinding binding = new ControllerButtonBinding( inputKey , command );
		
		if ( noDuplicateBindingExists(binding) ){
			binding.setIndexListened( buttonsListening.size() ); 
			buttonsListening.add( binding );
		}
		else{
			binding = null;
		}
	}
	
	@Override
	public void buttonPressed(ControllerEvent e) {
		
		for ( int i = 0 ; i < buttonsListening.size() ; i++ ){ 
			
			ControllerButtonBinding button = buttonsListening.get(i);
			
			if ( buttonsListening.get(i).keyMatch( e ) && buttonsListening.get(i).keyMatch( e )  ){ // If key is being pressed

				pressButton(button);
				
			}
			
		}
	}
	
	@Override
	public void buttonReleased(ControllerEvent e) {
		
		for ( int i = 0 ; i < buttonsHeld.size() ; i++ ){ 
			
			ControllerButtonBinding button = buttonsHeld.get(i);
			
			if ( buttonsHeld.get(i).keyMatch( e ) && buttonsHeld.get(i).keyMatch( e )  ){ // If key is being pressed

				releaseButton(button);
				
			}
			
		}
	}
	
	protected void releaseButton( ControllerButtonBinding button){
		
		button.setIndexListened( buttonsListening.size() ); 
		buttonsListening.add( button );
		removeFromHeld( button, buttonsHeld);
		button.onReleased();
	}

	protected void pressButton( ControllerButtonBinding button){
		
		button.setIndexHeld( buttonsHeld.size() ); // add this key to keys being held
		buttonsHeld.add( button );
		removeFromListening( button, buttonsListening ); // stop listening for this key while it's being held
		button.onPressed(); // trigger pressed event for that key
	}
	

	public void createLeftAnalogStickEvent( AnalogStickBinding stickBinding ){
		
		leftAnalogStickListening.add(stickBinding);
	}
	
	
	public void debugPrintInputList( int x, int y ,Graphics g){
    	
    	g.setColor(Color.GRAY);
    	g.drawString(name, x, y);
    	

    	//g.drawString("Left Stick Angle: "+leftAnalogStickBinding.stickMagnitude+" "+leftAnalogStickBinding.stickAngle, x , y+15);
    	if (leftAnalogStickHeld.size() == 1)
    		g.drawString("Left Stick Angle: "+leftAnalogStickHeld.get(0).stickAngle, x , y+15);
    	
    	g.drawString("Held:                 Listening: ", x, y+15+15);
    	
    	int line;
    	
    	for ( line = 0 ; line < this.buttonsHeld.size() ; line++ ) {
	    	g.drawString( buttonsHeld.get(line).toString() , x , y+30+15+(10*line) );
	    }	
    	
    	for ( line = 0 ; line < this.buttonsListening.size() ; line++ ) {
	    	g.drawString( buttonsListening.get(line).toString() , x+80 , y+30+15+(10*line) );
	    }	

    	
    	

    }

	private void leftStickTilted(){
		
		leftAnalogStickHeld.add(leftAnalogStickListening.get(0));
		leftAnalogStickListening.clear();
	}
	private void leftStickReturned(){
		leftAnalogStickListening.add(leftAnalogStickHeld.get(0));
		leftAnalogStickHeld.clear();
		leftAnalogStickListening.get(0).cutoffToZero();
	}
	
	@Override
	public void runHeld(){
		
		for ( int i = 0 ; i < buttonsHeld.size() ; i++ ){

			buttonsHeld.get(i).onHeld();
		}
	}
	
	//CONVENIENCE METHODS
	
	public void mapKeyboardBindingToControllerButton( InputManagerMouseKeyboard keyboardManager, int buttonCode, int keyCode ){
		mapKeyboardBindingToControllerButton(keyboardManager, buttonCode, 0, keyCode);
	}
	
	public void mapKeyboardBindingToControllerButton( InputManagerMouseKeyboard keyboardManager, int buttonCode, int modKeyCode, int keyCode ){
		
		KeyCommand extractedCommand = keyboardManager.extractKeyCommandFromKeyboardManager(keyCode);
		createButtonBinding( buttonCode, extractedCommand );
	}
	
	//automatically copies directional commands from keyboard arrow keys to controller d-pad
	public void mapKeyboardDirectionalBindingsToControllerDpad( InputManagerMouseKeyboard keyboardManager ){
		
		KeyCommand[] directionalCommandsFromKeyboard = keyboardManager.extractDirectionalKeyCommands(); //left, right, up, down
		
		createButtonBinding( ControllerEvent.VC_LEFT, directionalCommandsFromKeyboard[0] );
		createButtonBinding( ControllerEvent.VC_RIGHT, directionalCommandsFromKeyboard[1] );
		createButtonBinding( ControllerEvent.VC_UP, directionalCommandsFromKeyboard[2] );
		createButtonBinding( ControllerEvent.VC_DOWN, directionalCommandsFromKeyboard[3] );
	}
}
