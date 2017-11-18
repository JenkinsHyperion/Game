package Input;

import java.awt.event.KeyEvent;

public class KeyBinding extends InputBinding{
	
	private KeyType type;
	
	private KeyCommand command;
	
	protected KeyBinding( int keyCode , KeyCommand command ){ 
		super(keyCode);
		this.command = command;
		type = new SingleKeyMatch();
	}
	
	protected KeyBinding( int modCode , int keyCode , KeyCommand command ){
		super(keyCode);
		this.command = command;
		type = new ModdedKeyMatch( modCode );
		
	}
	
	public void onPressed(){ command.onPressed(); }  //POSSIBLE 
	public void onReleased(){ command.onReleased(); }
	public void onHeld(){ command.onHeld(); }
	
	public boolean keyMatch( KeyEvent e ){
		return ( type.keyCodeMatches( e ) && type.modCodeMatches( e ) );
	}
	
	public int getModCode(){
		return type.getModCode();
	}
	
	public int getKeyCode(){
		return this.inputCode;
	}
	
	public boolean modMatch( KeyEvent e ){
		return type.modCodeMatches( e ) ;
	}

	protected abstract class KeyType{
		protected abstract boolean keyCodeMatches( KeyEvent e );
		protected abstract boolean modCodeMatches( KeyEvent e );
		protected abstract int getModCode();
	}

	private class SingleKeyMatch extends KeyType{
		@Override
		protected boolean keyCodeMatches( KeyEvent e ){ //class
			
			if ( e.getKeyCode() == inputCode )
				return true;
			else
				return false;
				
		}
		@Override
		protected boolean modCodeMatches(KeyEvent e) {
			if ( e.getModifiers() == 0 )
				return true;
			else
				return false;
		}
		
		@Override
		public String toString() {
			return "Key "+inputCode;
		}
		
		@Override
		protected int getModCode() {
			return 0;
		}
	
	}
	
	private class ModdedKeyMatch extends KeyType{

		private int modKeyCode;
		
		protected ModdedKeyMatch( int modKeyCode ){
			this.modKeyCode = modKeyCode;
		}
		
		protected boolean keyCodeMatches( KeyEvent e ){ //class

				if ( e.getKeyCode() == inputCode )
					return true;
				else
					return false;
			
		}
		@Override
		protected boolean modCodeMatches(KeyEvent e) {
			if ( e.getModifiers() == modKeyCode )
				return true;
			else
				return false;
		}
		
		@Override
		public String toString() {
			return modKeyCode +" + Key "+inputCode;
		}
		
		@Override
		protected int getModCode() {
			return modKeyCode;
		}
	
	}
	
	@Override
	public String toString() {
		return this.type.toString();
	}
	
	protected KeyCommand extractCommand(){
		return this.command;
	}
	
}
