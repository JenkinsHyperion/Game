package sprites;

import java.awt.Image;

import animation.Animation;

/*
 * This is the base Sprite class. 
 */
public abstract class Sprite {
	
    protected boolean visibility;
    protected int spriteOffsetX = 0;
    protected int spriteOffsetY = 0;

//ABSTRACT FUNCTIONS 
	//This is a getImage() that works for both still and animated sprites, so draw functions in Board 
    //can call a generalized format.
	public abstract Image getImage(); 
	
	public abstract Animation getAnimatedSprite(); 
	
	public abstract void updateSprite();
	
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
    
    
    
}
