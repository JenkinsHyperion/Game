package sprites;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import animation.Animation;
import engine.Camera;
import engine.MovingCamera;
import entityComposites.EntityStatic;
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
    public void draw(Camera camera){

    	AffineTransform entityTransformation = new AffineTransform();

    	//entityTransformation.scale( (double)this.spriteSizePercent/100 , (double)this.spriteSizePercent/100 );
    	//entityTransformation.rotate( Math.toRadians(this.spriteAngle) );
    	entityTransformation.translate(this.spriteOffsetX, this.spriteOffsetY); 

    	camera.drawOnCamera(this , entityTransformation );
    	
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


    
}