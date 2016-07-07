package sprites;

import java.awt.Image;

import animation.Animation;

public class SpriteAnimated extends Sprite {  // Sprite with animation

    private Animation spriteAnimation;

    public SpriteAnimated(Animation animation) {

    	spriteAnimation = animation;
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
    }

    
}