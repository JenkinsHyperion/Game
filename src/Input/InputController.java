package Input;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class InputController {

	private String name;
	
	ArrayList< KeyBinding > keysListening = new ArrayList<>();
	ArrayList< KeyBinding > keysHeld = new ArrayList<>();
	
	private ArrayList< MouseBinding > mouseListening = new ArrayList<>();
	private ArrayList< MouseBinding > mouseHeld = new ArrayList<>();
	
	private InputController(){
	}
	
	/**
	 * 
	 */
	public InputController( String name){
		this.name = name;
	}
	
	private boolean noDuplicateBindingExists( MouseBinding binding ){
		for ( MouseBinding listening : this.mouseListening ){
			if( listening.getMouseButton() == binding.getMouseButton() && listening.getMouseModifier() == binding.getMouseModifier() ){
				System.err.println("Warning: "+this.name+
						" already has MouseBinding "+MouseEvent.getModifiersExText(binding.getMouseModifier()));
				return false;
			}
		}
		return true;
	}
	
	private boolean noDuplicateBindingExists( KeyBinding binding ){
		for ( KeyBinding listening : this.keysListening ){
			if( listening.getKeyCode() == binding.getKeyCode() && listening.getModCode() == binding.getModCode() ){
				System.err.println("Warning: "+this.name+
						" already has MouseBinding "+KeyEvent.getModifiersExText(binding.getModCode()));
				return false;
			}
		}
		return true;
	}
	
	/** Binds input key to user-defined command class implementing KeyCommand.
	 * 
	 * @param inputKey
	 * @param command
	 */
	public void createKeyBinding( int inputKey , KeyCommand command ){
		
		KeyBinding binding = new KeyBinding( inputKey , command );
		
		if ( noDuplicateBindingExists(binding) ){
			binding.setIndexListened( keysListening.size() ); 
			keysListening.add( binding );
		}
		else{
			binding = null;
		}
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
		
		if ( noDuplicateBindingExists(binding) ){
			binding.setIndexListened( keysListening.size() ); 
			keysListening.add( binding );
		}
		else{
			binding = null;
		}
	}
	
	/**Binds input mouse button to user-defined command class implementing MouseCommand.
	 * 
	 * @param mouseButton
	 * @param command
	 */
	public void createMouseBinding( int mouseButton , MouseCommand command ){ 

		MouseBinding mouseBinding = new MouseBinding( mouseButton , command );
		
		if ( noDuplicateBindingExists( mouseBinding ) ){
			mouseBinding.setIndexListened( mouseListening.size() ); 
			mouseListening.add( mouseBinding );
		}
		else{
			mouseBinding = null;
		}
			
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
		
		if ( noDuplicateBindingExists( mouseBinding ) ){
			mouseBinding.setIndexListened( mouseListening.size() ); 
			mouseListening.add( mouseBinding );
		}else{
			mouseBinding = null;
		}
	}
	
	private void releaseKey( KeyBinding key ){
		key.setIndexListened( keysListening.size() ); 
		keysListening.add( key );
		removeFromHeld( key );
		key.onReleased();
	}
	
	private void pressKey( KeyBinding key ){
		key.setIndexHeld( keysHeld.size() ); // add this key to keys being held
		keysHeld.add( key );
		removeFromListening( key ); // stop listening for this key while it's being held
		key.onPressed(); // trigger pressed event for that key
	}
	
	private void releaseButton( MouseBinding button ){
		button.setIndexListened( mouseListening.size() ); 
		mouseListening.add( button );
		removeFromHeld( button );
		button.mouseReleased();
	}
	
	private void pressButton( MouseBinding button ){
		button.setIndexHeld( mouseHeld.size() ); // add this key to keys being held
		mouseHeld.add( button );
		removeFromListening( button ); // stop listening for this key while it's being held
		button.mousePressed(); // trigger pressed event for that key

	}
	
	public void keyPressed( KeyEvent e ){ // being spammed from fucking keyEvent
		
		if ( e.getModifiers() != 0 ){ //mod key was pressed

			for ( int i = 0 ; i < keysHeld.size() ; i++ ){ 
				KeyBinding held = keysHeld.get(i);
				
				for ( int j = 0 ; j < keysListening.size() ; j++ ){ 
					KeyBinding listened = keysListening.get(j);
					if ( listened.getModCode() == e.getModifiers() && listened.keyCode == held.keyCode ){
						
						pressKey(listened);
						
						releaseKey(held);
						
					}
					
				}
				
			}
		}
		
		for ( int i = 0 ; i < keysListening.size() ; i++ ){ 
			
			KeyBinding key = keysListening.get(i);
			
			if ( keysListening.get(i).keyMatch( e ) && keysListening.get(i).modMatch( e )  ){ // If key is being pressed

				pressKey(key);
				
			}
			
		}
		
	}
	
	public void keyReleased( KeyEvent e ){ 
					
		
			for ( int i = 0 ; i < keysHeld.size() ; i++ ){
			
				KeyBinding key = keysHeld.get(i);
				
				if ( key.getModCode() == 2 && e.getKeyCode() == 17 ){
					//System.err.println( "CTR RELEASED ");
					releaseKey(key);
				}
				
				if ( key.getModCode() == 1 && e.getKeyCode() == 16 ){
					//System.err.println( "CTR RELEASED ");
					releaseKey(key);
				}
				
				if ( key.keyMatch(e)  ){ 
	
					releaseKey(key);
				}

			}
			
			for ( int i = 0 ; i < mouseHeld.size() ; i++ ){
				
				MouseBinding mouse = mouseHeld.get(i);
				
				System.out.println(mouse.getMouseModifier()+" - "+" - " +e.getModifiers()+" - "+e.getKeyCode() );
				
				if ( e.getKeyCode() == KeyEvent.VK_CONTROL && mouse.getMouseModifier() == 2 ){
					rigisterReleased( 2 , mouse );
					releaseButton(mouse);
				}
				
				if ( e.getKeyCode() == KeyEvent.VK_SHIFT && mouse.getMouseModifier() == 1 ){
					rigisterReleased( 1 , mouse );
					releaseButton(mouse);
				}
				
			}
	} 
	
	private void rigisterReleased( int modifierReleased , MouseBinding held ){
		
		ArrayList<MouseBinding> pressButtons = new ArrayList<MouseBinding>();
		
		for ( MouseBinding listened : mouseListening ){
			if ( listened.getMouseButton() == held.getMouseButton() && 
					( listened.getMouseModifier() & modifierReleased ) == listened.getMouseModifier()
			){
				pressButtons.add(listened);			}
		}
		for ( MouseBinding r : pressButtons ){
			pressButton(r);
		}
	}
		
	
	public void mousePressed( MouseEvent e ){ // being spammed from fucking keyEvent
		
		
		for ( int i = 0 ; i < mouseListening.size() ; i++ ){ 
			
			MouseBinding key = mouseListening.get(i);
			
			if ( key.mouseMatch(e)  ){ // If key is being pressed 

				pressButton(key); 
				
			}
			
		}
		
	}
	
	public void mouseReleased( MouseEvent e ){ 
		
			for ( int i = 0 ; i < mouseHeld.size() ; i++ ){
			
				MouseBinding key = mouseHeld.get(i);
				
				if ( key.mouseMatch(e) ){
				
					releaseButton( key );
				
					//key.mouseReleased();
					
				}
				
			}
	} 
	
	public void mouseDragged(MouseEvent e){

		for ( int i = 0 ; i < mouseHeld.size() ; i++ ){

			mouseHeld.get(i).mouseDragged();
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
			
		}
	}
	
	public void debugHeld(){
		
		for ( int i = 0 ; i < keysHeld.size() ; i++ ){
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
	
    public void debugPrintInputList( int x, int y ,Graphics g){
    	
    	g.setColor(Color.GRAY);
    	g.drawString(name, x, y);
    	
    	g.drawString("Held:                 Listening: ", x, y+15);
    	
    	int line;
    	
    	for ( line = 0 ; line < this.keysHeld.size() ; line++ ) {
	    	g.drawString( keysHeld.get(line).toString() , x , y+30+(10*line) );
	    }	
    	
    	for ( int i = 0 ; i < this.mouseHeld.size() ; i++ ) {
	    	g.drawString( mouseHeld.get(i).toString() , x , y+30+(10*(i+line)) );
	    }	
    	
    	for ( line = 0 ; line < this.keysListening.size() ; line++ ) {
	    	g.drawString( keysListening.get(line).toString() , x+80 , y+30+(10*line) );
	    }	
    	
    	for ( int i = 0 ; i < this.mouseListening.size() ; i++ ) {
	    	g.drawString( mouseListening.get(i).toString() , x+80 , y+30+(10*(i+line)) );
	    }	
    	
    	

    }
	
	
}
