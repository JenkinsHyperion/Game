package Input;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class InputController {

	ArrayList< KeyBindingAbstract > keysListening = new ArrayList<>();
	ArrayList< KeyBindingAbstract > keysHeld = new ArrayList<>();
	
	KeyBindingAbstract tempInputKey;
	
	public InputController(){
		
	}
	
	/** Add specified user-defined KeyBinding to be listened for.  
	 * 
	 * @param inputKey
	 */
	public void addInputKey( KeyBindingAbstract inputKey ){
		
		inputKey.setIndexListened( keysListening.size() ); 
		keysListening.add( inputKey );
		
	}
	
	
	public void keyPressed( KeyEvent e ){
		
		for ( int i = 0 ; i < keysListening.size() ; i++ ){ 
			
			if ( keysListening.get(i).getKeyCode() == e.getKeyCode() ){ // If key is being pressed
				
				keysListening.get(i).onPressed(); // trigger pressed event for that key
				
				keysListening.get(i).setIndexHeld( keysHeld.size() ); // add this key to keys being held
				keysHeld.add( keysListening.get(i) );
				
				removeFromListening( keysListening.get(i) ); // stop listening for this key while it's being held
				
			}
			
		}
		
	}
	
	public void keyReleased( KeyEvent e ){
		
		for ( int i = 0 ; i < keysHeld.size() ; i++ ){
			
			if ( keysHeld.get(i).getKeyCode() == e.getKeyCode() ){
				
				keysHeld.get(i).onReleased();
				
				keysHeld.get(i).setIndexListened( keysListening.size() ); //same as above but inverted
				keysListening.add( keysHeld.get(i) );
				
				removeFromHeld( keysHeld.get(i) );
				
			}
			
		}
		
	}
	
	public void runHeld(){
		
		for ( int i = 0 ; i < keysHeld.size() ; i++ ){

				keysHeld.get(i).onHeld();
		}
	}
	
	private void removeFromListening( KeyBindingAbstract key ){
		
		keysListening.remove( key.getIndexListened() );
		
		for ( int i = key.getIndexListened() ; i < keysListening.size() ; i++ ){
			keysListening.get(i).shiftListenedIndex();
		}
		
	}
	
	private void removeFromHeld( KeyBindingAbstract key ){
		
		keysHeld.remove( key.getIndexHeld() );
		
		for ( int i = key.getIndexHeld() ; i < keysHeld.size() ; i++ ){
			keysHeld.get(i).shiftHeldIndex();
		}
		
	}
	
	
}
