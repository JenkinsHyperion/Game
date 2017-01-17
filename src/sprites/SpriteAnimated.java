package sprites;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import animation.Animation;
import engine.Camera;
import entities.EntityStatic;

public class SpriteAnimated extends Sprite {  // Sprite with animation

    private Animation spriteAnimation;

    public SpriteAnimated(Animation animation , EntityStatic owner) {

    	spriteAnimation = animation;
    	this.owner = owner;
    	//spriteOffsetX = animation.getAnimationOffsetX();
    	//spriteOffsetY = animation.getAnimationOffsetY();
    	
        setVisible(true);
    }
    
    public SpriteAnimated(Animation animation, int offset_x, int offset_y, EntityStatic owner) {
    	spriteAnimation = animation;
    	this.spriteOffsetX = offset_x;
    	this.spriteOffsetY = offset_y;
    	this.owner = owner;
        setVisible(true);
    }
    
    // IMPLEMENTED METHODS

    @Override
    public void drawSprite(Graphics g){
    	g.drawImage(this.getImage(), this.owner.getX() + spriteOffsetX, this.owner.getY() + spriteOffsetY, null);
    }
    @Override
    public void drawSprite(Graphics g , Camera camera){
    	camera.draw(this, g);
    }
    @Override
    public void editorDraw(Graphics g, Point pos){
    	//do nothing
    }
    @Override
    public Image getImage() {
        return spriteAnimation.getAnimationFrame();
    }

    @Override
    public void updateSprite(){
    	spriteAnimation.update();
    }
    
    public Animation getAnimation() {
    	return spriteAnimation;
    }
    
    @Override
    public void setSprite(Animation a) {
    	spriteAnimation = a;
    	spriteOffsetX = a.getAnimationOffsetX();
    	spriteOffsetY = a.getAnimationOffsetY();
    }
    
    public Image getSpriteFrame() {
        return spriteAnimation.getAnimationFrame();
    }

	@Override
	public boolean hasSprite() {
		return true;
	}

    
}