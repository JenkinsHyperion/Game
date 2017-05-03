package sprites;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import animation.Animation;
import animation.LoadAnimation;
import engine.Camera;
import engine.MovingCamera;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;

public class SpriteAnimated extends Sprite {  // Sprite with animation

    private Animation spriteAnimation;
    private int tileWidth;
    private int tileHeight;
    private int row;
    
    public SpriteAnimated( String path ,int offset_x, int offset_y , int length , int row, int tileWidth, int tileHeight , int delay ){
    	super( path, offset_x, offset_y );
    	spriteAnimation = new Animation( LoadAnimation.buildAnimation(length, row, tileWidth, path) , delay );
    }
    
    
    public SpriteAnimated( String path , int delay ){
    	super( path , 0 , 0 );
    	spriteAnimation = Animation.animationFromGif( path, delay );
    	spriteAnimation.start();
    }
    // IMPLEMENTED METHODS


    @Override
    public void draw(Camera camera){

    	AffineTransform entityTransformation = new AffineTransform();

    	//entityTransformation.scale( (double)this.spriteSizePercent/100 , (double)this.spriteSizePercent/100 );
    	//entityTransformation.rotate( Math.toRadians(this.spriteAngle) );
    	entityTransformation.translate(this.spriteOffsetX, this.spriteOffsetY); 

    	camera.drawOnCamera(this , entityTransformation );
    	
    	this.updateSprite();
    	
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

    public int getRow(){
    	return this.row;
    }
    
    public int getFrameCount(){
    	return this.spriteAnimation.getFrameCount();
    }
    public int getFrameWidth(){
    	return this.tileWidth;
    }
    public int getFrameHeight(){
    	return this.tileHeight;
    }

	public int getDelay() {
		return this.spriteAnimation.getDelay();
	}
    
}