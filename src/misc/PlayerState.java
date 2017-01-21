package misc;

import java.awt.event.KeyEvent;

import misc.Direction.*;
import sprites.Sprite;

public abstract class PlayerState extends EntityState{

	private Sprite stateSpriteRight;
	private Sprite stateSpriteLeft;
	
	public PlayerState( String name , Sprite spriteRight , Sprite spriteLeft){
		super( name );
		this.stateSpriteRight = spriteRight;
		this.stateSpriteLeft = spriteLeft;
	}

	public void updateState(){}
	
	public void uponChange(){}
	
	//public abstract void onKeyEvent( int key );
	
	public void onJump(){}  //default do nothing methods unless overridden by children state. Better way to do this? 
	public void onShift(){holdingShift();}
	public void onCrouch(){ holdingCrouch(); } // 
	public void onUp(){}
	public void onDown(){}
	public void onRight(Direction direction){ direction.onRight( this ); }
	public void onLeft(Direction direction){ direction.onLeft( this ); }
	public void onForward( ){ holdingForward(); }
	public void onBackward(){ holdingBackward(); }

	public void holdingJump(){}
	public void holdingShift(){}
	public void holdingCrouch(){}
	public void holdingUp(){}
	public void holdingDown(){}
	public void holdingRight(Direction direction){ direction.holdingRight( this ); }
	public void holdingLeft(Direction direction){ direction.holdingLeft( this ); }
	public void holdingForward(){}
	public void holdingBackward(){}
	
	public void offJump(){}
	public void offShift(){}
	public void offCrouch(){}
	public void offUp(){}
	public void offDown(){}
	public void offRight(Direction direction){ direction.offRight( this ); }
	public void offLeft(Direction direction){ direction.offLeft( this ); }
	public void offForward(){ }
	public void offBackward(){ }
	
	public void onCollision(){}
	
	public Sprite getSpriteRight() {
		return stateSpriteRight;
	}
	
	public Sprite getSpriteLeft() {
		return stateSpriteLeft;
	}
	
}
