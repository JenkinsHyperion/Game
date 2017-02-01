package Input;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class InputController {

	ArrayList< KeyBindingAbstract > keysListening = new ArrayList<>();
	ArrayList< KeyBindingAbstract > keysHeld = new ArrayList<>();
	
	KeyBindingAbstract tempInputKey;
	
	public InputController(){ 
		
	}
	
	/** Add specified user-defined KeyBindingAbstract to be listened for.  
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
				
				KeyBindingAbstract key = keysListening.get(i);
				
				key.setIndexHeld( keysHeld.size() ); // add this key to keys being held
				keysHeld.add( key );
				
				removeFromListening( key ); // stop listening for this key while it's being held
				
				key.onPressed(); // trigger pressed event for that key
				
			}
			
		}
		
	}
	
	public void keyReleased( KeyEvent e ){
		
		for ( int i = 0 ; i < keysHeld.size() ; i++ ){
			
			if ( keysHeld.get(i).getKeyCode() == e.getKeyCode() ){
				
				KeyBindingAbstract key = keysHeld.get(i);
				
				key.setIndexListened( keysListening.size() ); //same as above but inverted
				keysListening.add( key );
				
				removeFromHeld( key );
				
				key.onReleased();
				
			}
			
		}
		
	}
	
	public void runHeld(){
		
		for ( int i = 0 ; i < keysHeld.size() ; i++ ){

				keysHeld.get(i).onHeld();
		}
	}
	
	public void runReleased(){
		
		for ( int i = 0 ; i < keysListening.size() ; i++ ){

			keysListening.get(i).onReleased();
		}
	}
	
	public void debugReleased(){
		
		for ( int i = 0 ; i < keysListening.size() ; i++ ){

			System.out.println(keysListening.get(i) + "is released");
		}
	}
	
	public void debugHeld(){
		
		for ( int i = 0 ; i < keysHeld.size() ; i++ ){

			System.out.println(keysHeld.get(i) + "is held");
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
