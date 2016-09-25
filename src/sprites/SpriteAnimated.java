package sprites;

import java.awt.Image;

import animation.Animation;

public class SpriteAnimated extends Sprite {  // Sprite with animation

    private Animation spriteAnimation;

    public SpriteAnimated(Animation animation) {

    	spriteAnimation = animation;
    	//spriteOffsetX = animation.getAnimationOffsetX();
    	//spriteOffsetY = animation.getAnimationOffsetY();
    	
        setVisible(true);
    }
    
    public SpriteAnimated(Animation animation, int offset_x, int offset_y) {
    	spriteAnimation = animation;
    	this.spriteOffsetX = offset_x;
    	this.spriteOffsetY = offset_y;
    	
        setVisible(true);
    }
    
    
    //Implemented from Sprite Interface (not overridden)
    public void loadImage(String path){}

    
    public Image getSpriteFrame() {
        return spriteAnimation.getAnimationFrame();
    }
    
    // This is "overriding" the abstract getImage() from Sprite class 
    public Image getImage() {
        return spriteAnimation.getAnimationFrame();
    }
    
    public void updateSprite(){
    	spriteAnimation.update();
    }
    
    public Animation getAnimatedSprite() {
    	return spriteAnimation;
    }
    
    @Override
    public void setSprite(Animation a) {
    	spriteAnimation = a;
    	spriteOffsetX = a.getAnimationOffsetX();
    	spriteOffsetY = a.getAnimationOffsetY();
    }
    
    

    
}