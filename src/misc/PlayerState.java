package misc;

import misc.PlayerDirection.*;
import sprites.Sprite;

public abstract class PlayerState extends EntityState{

	private Sprite stateSpriteRight;
	private Sprite stateSpriteLeft;
	
	public PlayerState( String name , Sprite spriteRight , Sprite spriteLeft ){
		super( name );
		this.stateSpriteRight = spriteRight;
		this.stateSpriteLeft = spriteLeft;
	}

	public void updateState(){}
	
	public void onEnteringState(){}
	public void onLeavingState(){}
	
	//public abstract void onKeyEvent( int key );
	
	public void onJump(){}  //default do nothing methods unless overridden by children state. Better way to do this? 
	public void onShift(){holdingShift();}
	public void onCrouch(){ holdingCrouch(); } // 
	public void onUp(){}
	public void onDown(){}
	public void onRight(PlayerDirection playerDirection){ playerDirection.onRight( this ); }
	public void onLeft(PlayerDirection playerDirection){ playerDirection.onLeft( this ); }
	public void onForward( ){ holdingForward(); }
	public void onBackward(){ holdingBackward(); }

	public void holdingJump(){}
	public void holdingShift(){}
	public void holdingCrouch(){}
	public void holdingUp(){}
	public void holdingDown(){}
	public void holdingRight(PlayerDirection playerDirection){ playerDirection.holdingRight( this ); }
	public void holdingLeft(PlayerDirection playerDirection){ playerDirection.holdingLeft( this ); }
	public void holdingForward(){}
	public void holdingBackward(){}
	
	public void offJump(){}
	public void offShift(){}
	public void offCrouch(){}
	public void offUp(){}
	public void offDown(){}
	public void offRight(PlayerDirection playerDirection){ playerDirection.offRight( this ); }
	public void offLeft(PlayerDirection playerDirection){ playerDirection.offLeft( this ); }
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
