package Input;

public abstract class KeyBindingAbstract {
	
	protected int key;

	private int indexHeld;
	private int indexListened;
	
	protected KeyBindingAbstract( int keycode ){ this.key = keycode; }
	
	public abstract void onPressed();
	public abstract void onReleased();
	public abstract void onHeld();
	
	protected int getKeyCode(){ return key; }

	protected int getIndexHeld(){ return indexHeld; }
	protected int getIndexListened(){ return indexListened; }
	
	protected void setIndexHeld( int i){ indexHeld = i; }
	protected void setIndexListened( int i ){ indexListened = i; }
	
	protected void shiftHeldIndex(){ indexHeld-- ;}
	protected void shiftListenedIndex(){ indexListened-- ;}
	
	
}
