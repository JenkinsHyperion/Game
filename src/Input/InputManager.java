package Input;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public abstract class InputManager {

	protected String name;
	
	
	
	protected <T extends InputBinding> void removeFromListening( T inputbinding, ArrayList<T> listeningList ){
		listeningList.remove( inputbinding.getIndexListened() );
		
		for ( int i = inputbinding.getIndexListened() ; i < listeningList.size() ; i++ ){
			listeningList.get(i).shiftListenedIndex();
		}
	}
	
	protected <T extends InputBinding> void removeFromHeld( T inputbinding, ArrayList<T> heldList ){
		
		heldList.remove( inputbinding.getIndexHeld() );
		
		for ( int i = inputbinding.getIndexHeld() ; i < heldList.size() ; i++ ){
			heldList.get(i).shiftHeldIndex();
		}
		
	}
	
	public abstract void runHeld();
	
	public abstract void debugPrintInputList(int x, int y, Graphics g2);
	
	/*protected <T extends InputBinding> void releaseButton( T button, ArrayList<T> listeningList, ArrayList<T> heldList ){
		button.setIndexListened( listeningList.size() ); 
		listeningList.add( button );
		removeFromHeld( button, heldList);
		button.mouseReleased();
	}
	
	protected <T extends InputBinding> void pressButton( T button, ArrayList<T> heldList, ArrayList<T> listeningList ){
		button.setIndexHeld( heldList.size() ); // add this key to keys being held
		heldList.add( button );
		removeFromListening( button, listeningList ); // stop listening for this key while it's being held
		button.mousePressed(); // trigger pressed event for that key

	}*/
	
}
