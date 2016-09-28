package sprites;

import java.awt.Graphics;
import java.awt.Image;

import animation.Animation;

public class SpriteNull extends Sprite{
	
	private final static SpriteNull nullSprite = new SpriteNull();
	
	//constructor
	private SpriteNull() {
		
	}
	
	//OPTIMIZATION - Look into better handling, this is a static factory that returns the static singleton nullSprite, which
	//apparently is difficult to substitute test without breaking everything
	public static SpriteNull nullSprite(){ 
		return nullSprite;
	}
	
    @Override
    public void draw(Graphics g){
    	//DO NOTHING 
    }

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public Animation getAnimatedSprite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSprite() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasSprite() {
		return false;
	}


	
}
