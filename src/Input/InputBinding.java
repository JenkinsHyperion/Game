package Input;

import java.awt.event.KeyEvent;

public abstract class InputBinding {

	protected int inputCode;

	protected int indexHeld;
	protected int indexListened;
	
	public InputBinding( int inputCode ) {

		this.inputCode = inputCode;
	}
	
	//protected abstract boolean codeMatch();
	protected int getCode(){
		return this.inputCode;
	}
	
	protected int getIndexHeld(){ return indexHeld; }
	protected int getIndexListened(){ return indexListened; }
	
	protected void setIndexHeld( int i){ indexHeld = i; }
	protected void setIndexListened( int i ){ indexListened = i; }
	
	protected void shiftHeldIndex(){ indexHeld-- ;}
	protected void shiftListenedIndex(){ indexListened-- ;}
	
}
