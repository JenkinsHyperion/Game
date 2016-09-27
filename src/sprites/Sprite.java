package sprites;

import java.awt.Graphics;
import java.awt.Image;

import animation.Animation;
import entities.EntityStatic;

/*
 * This is the base Sprite class. 
 */
public abstract class Sprite {
	
	protected EntityStatic owner;
    protected boolean visibility;
    protected int spriteOffsetX = 0;
    protected int spriteOffsetY = 0;

//ABSTRACT FUNCTIONS 
	//This is a getImage() that works for both still and animated sprites, so draw functions in Board 
    //can call a generalized format.
    public abstract void draw(Graphics g);
    
	public abstract Image getImage(); 
	
	public abstract Animation getAnimatedSprite(); 
	
	public abstract void updateSprite();
	
	public abstract boolean hasSprite();
	
//PUBLIC FUNCTIONS	
	public void setSprite(Animation a){}
	//public void setSprite()
	
    public boolean isVisible() {
        return visibility;
    }

    public void setVisible(Boolean visible) {
        visibility = visible;
    }

    public int getOffsetX(){
    	return spriteOffsetX;
    }
    
    public int getOffsetY(){
    	return spriteOffsetY;
    }
    
    public void setOffset(int x , int y){
    	spriteOffsetX = x;
    	spriteOffsetY = y;
    }
    
}
