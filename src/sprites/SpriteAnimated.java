package sprites;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import animation.Animation;
import engine.Camera;
import entities.EntityStatic;
import entityComposites.GraphicComposite;

public class SpriteAnimated extends Sprite {  // Sprite with animation

    private Animation spriteAnimation;

    
    public SpriteAnimated(Animation animation, int offset_x, int offset_y) {
    	spriteAnimation = animation;
    	this.spriteOffsetX = offset_x;
    	this.spriteOffsetY = offset_y;
        setVisible(true);
    }
    
    // IMPLEMENTED METHODS

    @Override
    public void drawSprite(){
    	//g.drawImage(this.getImage(), this.owner.getX() + spriteOffsetX, this.owner.getY() + spriteOffsetY, null);
    }
    @Override
    public void drawSprite(Camera camera){

    	AffineTransform entityTransformation = new AffineTransform();

    	//entityTransformation.scale( (double)this.spriteSizePercent/100 , (double)this.spriteSizePercent/100 );
    	//entityTransformation.rotate( Math.toRadians(this.spriteAngle) );
    	entityTransformation.translate(this.spriteOffsetX, this.spriteOffsetY); 

    	camera.draw(this , entityTransformation );
    	
    }
    @Override
    public void editorDraw(Point pos){
    	//do nothing
    }
    @Override@Deprecated
    public Image getImage() {
        return spriteAnimation.getAnimationFrame();
    }
    
    @Override
    public BufferedImage getBufferedImage() {
    	return spriteAnimation.getAnimationFrame();
    }

    @Override
    public void updateSprite(){
    	spriteAnimation.update();
    }
    
    public Animation getAnimation() {
    	return spriteAnimation;
    }
    /**
     * USE GETIMAGE INSTEAD
     * @return
     */
    @Deprecated
    public Image getSpriteFrame() {
        return spriteAnimation.getAnimationFrame();
    }

	@Override
	public boolean hasSprite() {
		return true;
	}

    
}