package Input;

import java.awt.event.MouseEvent;

public class MouseBinding extends InputBinding{
	
	private MouseType type; // Substate for unmodified and modified bindings - TODO add multiple bindings
	
	private MouseCommand command;
	
	protected MouseBinding( int mouseButton , MouseCommand command ){ 
		super(mouseButton);
		this.command = command;
		type = new UnmodifiedMouse();
	}
	
	protected MouseBinding( int modifierMask , int mouseButton , MouseCommand command ){
		super(mouseButton);
		this.command = command;
		type = new ModifiedMouse( modifierMask );
		
	}
	
	public int getMouseModifier(){
		return type.getModKey();
	}
	
	public int getMouseButton(){
		return this.inputCode;
	}
	
	public void mousePressed(){ command.mousePressed(); } // forward events to the referenced user-defined command 
	public void mouseDragged(){ command.mouseDragged(); } // that implements MouseCommand
	public void mouseReleased(){ command.mouseReleased(); }
	
	//CORE FUNCTIONALITY 
	
	protected boolean mouseMatch( MouseEvent e ){
		return type.mouseMatches( e );
	}
	
	private abstract class MouseType{
		protected abstract boolean mouseMatches( MouseEvent e );
		protected abstract int getModKey();
		public abstract String toString();
	}

	private class UnmodifiedMouse extends MouseType{
		
		@Override
		protected boolean mouseMatches( MouseEvent e ){ //

				if (e.getButton() == inputCode && 
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
			return "Button "+inputCode;
		}
	
	}
	
	private class ModifiedMouse extends MouseType{

		private int modKeyCode;
		
		protected ModifiedMouse( int modKeyCode ){
			this.modKeyCode = modKeyCode;
		}
		@Override
		protected boolean mouseMatches( MouseEvent e ){ //class
			
				if (e.getButton() == inputCode && (e.getModifiers() & modKeyCode)==modKeyCode )
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
			return "Button "+inputCode+" + "+modKeyCode;
		}
		
	}
	
	@Override
	public String toString() {
		return this.type.toString();
	}
	
}
