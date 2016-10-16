package sprites;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import animation.Animation;
import engine.Camera;

public class SpriteNull extends Sprite{
	
	private final static SpriteNull nullSprite = new SpriteNull();
	
	//constructor
	private SpriteNull() {
		
	}
	
	//OPTIMIZATION - Look into better handling, this is a static factory that returns the static singleton nullSprite, which
	//apparently is difficult to substitute test without breaking everything
	public static SpriteNull getNullSprite(){ 
		return nullSprite;
	}
	
    @Override
    public void drawSprite(Graphics g){
    	//DO NOTHING 
    }
    @Override
    public void drawSprite(Graphics g , Camera camera){
    	//DO NOTHING 
    }
    @Override
    public void editorDraw(Graphics g, Point pos){
    	//do nothing
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
