package Input;

import java.awt.event.KeyEvent;

public class KeyBinding {
	
	protected int keyCode;

	private int indexHeld;
	private int indexListened;
	
	private Type type;
	
	private KeyCommand command;
	
	protected KeyBinding( int keyCode , KeyCommand command ){ 
		
		this.keyCode = keyCode; 
		this.command = command;
		type = new SingleKeyMatch();
	}
	
	protected KeyBinding( int modCode , int keyCode , KeyCommand command ){
		
		this.keyCode = keyCode;
		this.command = command;
		type = new ModdedKeyMatch( modCode );
		
	}
	
	public void onPressed(){ command.onPressed(); }  //POSSIBLE 
	public void onReleased(){ command.onReleased(); }
	public void onHeld(){ command.onHeld(); }
	
	public boolean keyMatch( KeyEvent e ){
		return type.keyCodeMatches( e );
	}

	protected int getIndexHeld(){ return indexHeld; }
	protected int getIndexListened(){ return indexListened; }
	
	protected void setIndexHeld( int i){ indexHeld = i; }
	protected void setIndexListened( int i ){ indexListened = i; }
	
	protected void shiftHeldIndex(){ indexHeld-- ;}
	protected void shiftListenedIndex(){ indexListened-- ;}
	
	
	private abstract class Type{
		protected abstract boolean keyCodeMatches( KeyEvent e );
	}

	private class SingleKeyMatch extends Type{
		@Override
		protected boolean keyCodeMatches( KeyEvent e ){ //class
			
				if ( ( e.getKeyCode() | e.getModifiers() ) == keyCode)
					return true;
				else
					return false;
				
		}
	
	}
	
	private class ModdedKeyMatch extends Type{

		private int modKeyCode;
		
		protected ModdedKeyMatch( int modKeyCode ){
			this.modKeyCode = modKeyCode;
		}
		
		protected boolean keyCodeMatches( KeyEvent e ){ //class
			
			if ( (e.getModifiers() & modKeyCode ) != 0 ){
				if ( e.getKeyCode() == keyCode )
					return true;
				else
					return false;
			}
			else 
				return false;
			
		}
	
	}
	
	
}
