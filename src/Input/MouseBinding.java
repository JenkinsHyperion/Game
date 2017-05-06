package Input;

import java.awt.event.MouseEvent;

public class MouseBinding {
	
	private int mouseButton;

	private int indexHeld;
	private int indexListened;
	
	private Type type; // Substate for unmodified and modified bindings - TODO add multiple bindings
	
	private MouseCommand command;
	
	protected MouseBinding( int mouseButton , MouseCommand command ){ 

		this.command = command;
		this.mouseButton = mouseButton;
		type = new UnmodifiedMouse( );
	}
	
	protected MouseBinding( int modifierMask , int mouseButton , MouseCommand command ){

		this.command = command;
		this.mouseButton = mouseButton;
		type = new ModifiedMouse( modifierMask );
		
	}
	
	public int getMouseModifier(){
		return type.getModKey();
	}
	
	public int getMouseButton(){
		return this.mouseButton;
	}
	
	public void mousePressed(){ command.mousePressed(); } // forward events to the referenced user-defined command 
	public void mouseDragged(){ command.mouseDragged(); } // that implements MouseCommand
	public void mouseReleased(){ command.mouseReleased(); }
	
	//CORE FUNCTIONALITY 
	
	protected boolean mouseMatch( MouseEvent e ){
		return type.mouseMatches( e );
	}

	protected int getIndexHeld(){ return indexHeld; }
	protected int getIndexListened(){ return indexListened; }
	
	protected void setIndexHeld( int i){ indexHeld = i; }
	protected void setIndexListened( int i ){ indexListened = i; }
	
	protected void shiftHeldIndex(){ indexHeld-- ;}
	protected void shiftListenedIndex(){ indexListened-- ;}
	
	
	private abstract class Type{
		protected abstract boolean mouseMatches( MouseEvent e );
		protected abstract int getModKey();
		public abstract String toString();
	}

	private class UnmodifiedMouse extends Type{
		
		@Override
		protected boolean mouseMatches( MouseEvent e ){ //

				if (e.getButton() == mouseButton && 
						( e.getModifiers() | e.CTRL_MASK ) != e.getModifiers() &&
						( e.getModifiers() | e.SHIFT_MASK ) != e.getModifiers() &&
						( e.getModifiers() | e.ALT_MASK ) != e.getModifiers() 
				){ //16 is no modifiers mask for some reason
					return true;
				}
				else
					return false;		
		}
		
		@Override
		protected int getModKey() {
			return 0;
		}
		
		@Override
		public String toString() {
			return "Button "+mouseButton;
		}
	
	}
	
	private class ModifiedMouse extends Type{

		private int modKeyCode;
		
		protected ModifiedMouse( int modKeyCode ){
			this.modKeyCode = modKeyCode;
		}
		@Override
		protected boolean mouseMatches( MouseEvent e ){ //class
			
				if (e.getButton() == mouseButton && (e.getModifiers() & modKeyCode)==modKeyCode )
					return true;
				else
					return false;

			
		}
		
		@Override
		protected int getModKey() {
			return modKeyCode;
		}
	
		@Override
		public String toString() {
			return "Button "+mouseButton+" + "+modKeyCode;
		}
		
	}
	
	@Override
	public String toString() {
		return this.type.toString();
	}
	
}
