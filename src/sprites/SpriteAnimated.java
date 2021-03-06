package sprites;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import animation.Animation;
import animation.LoadAnimation;
import engine.ReferenceFrame;
import engine.MovingCamera;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;

public class SpriteAnimated extends Sprite {  // Sprite with animation

	private String path;
	
    private Animation spriteAnimation;
    private int tileWidth;
    private int tileHeight;
    private int row;
    
    public SpriteAnimated( String path ,int offset_x, int offset_y , int length , int row, int tileWidth, int tileHeight , int delay ){
    	super( offset_x, offset_y );
    	this.path = path;
    	spriteAnimation = new Animation( LoadAnimation.buildAnimation(length, row, tileWidth, tileHeight, path) , delay );
    	spriteAnimation.start();
    }
    
    
    public SpriteAnimated( String path , int delay ){
    	super( 0 , 0 );
    	this.path = path;
    	spriteAnimation = Animation.animationFromGif( path, delay );
    	spriteAnimation.start();
    }
    // IMPLEMENTED METHODS

    public String getPathName(){
    	return this.path;
    }

    @Override
    public void draw(ReferenceFrame camera , GraphicComposite.Static composite ){

    	AffineTransform entityTransformation = new AffineTransform();

    	entityTransformation.scale( (double)this.spriteSizePercent , (double)this.spriteSizePercent );
    	entityTransformation.rotate( Math.toRadians(this.spriteAngle) );
    	entityTransformation.translate(this.spriteOffsetX, this.spriteOffsetY); 

    	camera.drawOnCamera( composite , entityTransformation );
    	
    	this.updateSprite();
    	
    }

    @Override@Deprecated
    public Image getImage() {
        return spriteAnimation.getAnimationFrame();
    }
    
    @Override
    public BufferedImage[][] getBufferedImage() {
    	return new BufferedImage[][]{{spriteAnimation.getAnimationFrame()}};
    }

    public void updateSprite(){
    	spriteAnimation.update();
    }
    
    public Animation getAnimation() {
    	return spriteAnimation;
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