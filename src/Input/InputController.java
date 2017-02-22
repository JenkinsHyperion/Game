package Input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class InputController {

	ArrayList< KeyBinding > keysListening = new ArrayList<>();
	ArrayList< KeyBinding > keysHeld = new ArrayList<>();
	
	ArrayList< MouseBinding > mouseListening = new ArrayList<>();
	ArrayList< MouseBinding > mouseHeld = new ArrayList<>();
	
	/**
	 * 
	 */
	public InputController(){ 
	}
	
	/** Binds input key to user-defined command class implementing KeyCommand.
	 * 
	 * @param inputKey
	 * @param command
	 */
	public void createKeyBinding( int inputKey , KeyCommand command ){
		
		KeyBinding binding = new KeyBinding( inputKey , command );
		binding.setIndexListened( keysListening.size() ); 
		keysListening.add( binding );
	}
	
	/** Binds input key plus modifier key (such as shift) to user-defined command class implementing KeyCommand.
	 *PLEASE NOTE that the modifier keyCode is the MODIFIER MASK and not the raw key.
	 * EX: KeyEvent.SHIFT_MASK and not KeyEvent.VK_SHIFT
	 * @param modifierMask
	 * @param inputKey
	 * @param command
	 */
	public void createKeyBinding( int modifierMask , int inputKey , KeyCommand command ){
		
		KeyBinding binding = new KeyBinding( modifierMask , inputKey , command );
		binding.setIndexListened( keysListening.size() ); 
		keysListening.add( binding );
	}
	
	/**Binds input mouse button to user-defined command class implementing MouseCommand.
	 * 
	 * @param mouseButton
	 * @param command
	 */
	public void createMouseBinding( int mouseButton , MouseCommand command ){
		MouseBinding mouseBinding = new MouseBinding( mouseButton , command );
		mouseBinding.setIndexListened( mouseListening.size() ); 
		mouseListening.add( mouseBinding );
	}
	
	/** Binds input mouse button plus modifier key (such as shift) to user-defined command class implementing MouseCommand.
	 *PLEASE NOTE that the modifier keyCode is the MODIFIER MASK and not the raw key.
	 * EX: KeyEvent.SHIFT_MASK and not KeyEvent.VK_SHIFT
	 * @param modifierMask
	 * @param inputKey
	 * @param command
	 */
	public void createMouseBinding( int modifierMask , int mouseButton,  MouseCommand command ){
		MouseBinding mouseBinding = new MouseBinding( modifierMask , mouseButton , command );
		mouseBinding.setIndexListened( mouseListening.size() ); 
		mouseListening.add( mouseBinding );
	}
	
	public void keyPressed( KeyEvent e ){
		
		for ( int i = 0 ; i < keysListening.size() ; i++ ){ 
			
			if ( keysListening.get(i).keyMatch( e ) ){ // If key is being pressed
				
				KeyBinding key = keysListening.get(i);
				
				key.setIndexHeld( keysHeld.size() ); // add this key to keys being held
				keysHeld.add( key );
				
				removeFromListening( key ); // stop listening for this key while it's being held
				
				key.onPressed(); // trigger pressed event for that key
				
			}
			
		}
		
	}
	
	public void keyReleased( KeyEvent e ){
		
		for ( int i = 0 ; i < keysHeld.size() ; i++ ){
			
			if ( keysHeld.get(i).keyMatch(e) ){ 
				
				KeyBinding key = keysHeld.get(i);
				
				key.setIndexListened( keysListening.size() ); //same as above but inverted
				keysListening.add( key );
				
				removeFromHeld( key );
				
				key.onReleased();
				
			}
			
		}
		
	}
	
	public void mousePressed(MouseEvent e){
	
		for ( int i = 0 ; i < mouseListening.size() ; i++ ){ 
			
			if ( mouseListening.get(i).mouseMatch( e ) ){ // If key is being pressed
				
				MouseBinding mouse = mouseListening.get(i);
				
				mouse.setIndexHeld(  mouseHeld.size() ); // add this key to keys being held
				mouseHeld.add( mouse );
				
				removeFromListening( mouse ); // stop listening for this key while it's being held
				
				mouse.mousePressed(); // trigger pressed event for that key
			}
		}
		
	}
	
	public void mouseReleased( MouseEvent e ){
		
		for ( int i = 0 ; i < mouseHeld.size() ; i++ ){
				
				MouseBinding mouse = mouseHeld.get(i);
				
				mouse.setIndexListened( mouseListening.size() ); //same as above but inverted
				mouseListening.add( mouse );
				
				removeFromHeld( mouse );
				
				mouse.mouseReleased();
			
		}
		
	}
	public void mouseDragged(MouseEvent e){

		for ( int i = 0 ; i < mouseHeld.size() ; i++ ){

			mouseHeld.get(i).mouseDragged();
		}
	}
	public void mouseMoved(MouseEvent e) {
		for (int i = 0 ; i < mouseHeld.size() ; i++) {
			mouseHeld.get(i).mouseMoved();
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
	
	//CORE FUNCTIONALITY
	
	private void removeFromListening( KeyBinding key ){
		keysListening.remove( key.getIndexListened() );
		
		for ( int i = key.getIndexListened() ; i < keysListening.size() ; i++ ){
			keysListening.get(i).shiftListenedIndex();
		}
	}
	
	private void removeFromListening( MouseBinding mouse ){
		mouseListening.remove( mouse.getIndexListened() );
		
		for ( int i = mouse.getIndexListened() ; i < mouseListening.size() ; i++ ){
			mouseListening.get(i).shiftListenedIndex();
		}
	}
	
	private void removeFromHeld( KeyBinding key ){
		
		keysHeld.remove( key.getIndexHeld() );
		
		for ( int i = key.getIndexHeld() ; i < keysHeld.size() ; i++ ){
			keysHeld.get(i).shiftHeldIndex();
		}
		
	}
	
	private void removeFromHeld( MouseBinding mouse ){
		
		mouseHeld.remove( mouse.getIndexHeld() );
		
		for ( int i = mouse.getIndexHeld() ; i < mouseHeld.size() ; i++ ){
			mouseHeld.get(i).shiftHeldIndex();
		}
		
	}
	
	
}
