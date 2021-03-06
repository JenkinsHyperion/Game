package misc.PlayerDirection;

import misc.*;
import sprites.Sprite;

public class PlayerDirection{
	
	private final Right right = new Right();
	private final Left left = new Left();
	private Facing facing = right;

	
	public void onLeft( PlayerState state ){ facing.onLeftDir(state); }
	public void onRight( PlayerState state ){ facing.onRightDir(state); }
	public void holdingLeft( PlayerState state ){ facing.holdingLeftDir(state); }
	public void holdingRight( PlayerState state ){ facing.holdingRightDir(state); }
	public void offLeft( PlayerState state ){ facing.offLeftDir(state); }
	public void offRight( PlayerState state ){ facing.offRightDir(state); }
	
	public void reveseDirection(){
		facing.swapDirection();
	}
	
	public int normalize( int a ){
		return facing.normalize(a);
	}
	
	public double normalize( double a ){
		return facing.normalize(a);
	}
	
	public Sprite getDirectionalSprite( PlayerState state ){
		return facing.getDirectionSprite(state);
	}
	
	public String toString(){ return facing.toString(); } 
	
	private abstract class Facing{
	
		public abstract void onLeftDir( PlayerState state );
		public abstract void onRightDir( PlayerState state );
		public abstract void holdingLeftDir( PlayerState state );
		public abstract void holdingRightDir( PlayerState state );
		public abstract void offLeftDir( PlayerState state );
		public abstract void offRightDir( PlayerState state );
		
		protected abstract void swapDirection();
		
		public abstract int normalize( int a );
		public abstract double normalize( double a );
		
		public abstract Sprite getDirectionSprite( PlayerState state );
	}
	
	private class Right extends Facing{
		
		@Override
		public void onLeftDir( PlayerState state ) { state.onBackward(); }

		@Override
		public void onRightDir( PlayerState state ) { state.onForward(); }

		@Override
		public void holdingLeftDir( PlayerState state ) { state.holdingBackward(); }

		@Override
		public void holdingRightDir( PlayerState state) { state.holdingForward();}

		@Override
		public void offLeftDir( PlayerState state ) { state.offBackward(); }

		@Override
		public void offRightDir( PlayerState state ) { state.offForward(); }
		
		@Override
		public int normalize(int a) {
			return a;
		}
		
		@Override
		public double normalize(double a) {
			return a;
		}
		
		@Override
		protected void swapDirection(){
			facing = left;
		}
		
		@Override
		public String toString(){ return "Right"; }

		@Override
		public Sprite getDirectionSprite( PlayerState state ) {
			return state.getSpriteRight();
		} 
		
	}
		
	private class Left extends Facing {

			@Override
			public void onLeftDir(PlayerState state) {
				state.onForward();
			}

			@Override
			public void onRightDir(PlayerState state) {
				state.onBackward();
			}

			@Override
			public void holdingLeftDir(PlayerState state) {
				state.holdingForward();
			}

			@Override
			public void holdingRightDir(PlayerState state) {
				state.holdingBackward();
			}

			@Override
			public void offLeftDir(PlayerState state) {
				state.offForward();
			}

			@Override
			public void offRightDir(PlayerState state) {
				state.offBackward();
			}

			@Override
			public int normalize(int a) {
				return -a;
			}
			
			@Override
			public double normalize(double a) {
				return -a;
			}
			
			@Override
			protected void swapDirection(){
				facing = right;
			}
			
			@Override
			public String toString(){ return "Left"; } 
			
			@Override
			public Sprite getDirectionSprite( PlayerState state ) {
				return state.getSpriteLeft();
			} 

	}
	
}
