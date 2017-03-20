package sprites;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import animation.Animation;
import engine.Camera;
import entities.EntityStatic;
import entityComposites.GraphicComposite;

/*
 * This is the base Sprite class. 
 */
public abstract class Sprite{
	
	protected GraphicComposite ownerComposite;
    protected boolean visibility;
    protected int spriteOffsetX = 0;
    protected int spriteOffsetY = 0;
    protected int spriteSizePercent = 100;
    protected double spriteAngle = 0;
    protected String fileName;
    
	AffineTransform spriteTransform = new AffineTransform();

//ABSTRACT FUNCTIONS 
	//This is a getImage() that works for both still and animated sprites, so draw functions in Board 
    //can call a generalized format.
    public abstract void drawSprite(Camera camera);
    public abstract void drawSprite();
    public abstract void editorDraw(Point pos);
    
    public EntityStatic ownerEntity(){
    	return this.ownerComposite.ownerEntity();
    }
    
	public abstract Image getImage(); 
	public abstract BufferedImage getBufferedImage();
	
	public abstract Animation getAnimation(); 
	
	public abstract void updateSprite();
	
	public abstract boolean hasSprite();
	
//PUBLIC FUNCTIONS	
	public void setSprite(Animation a){}
	//public void setSprite()
	
    public boolean isVisible() {
        return visibility;
    }
    
    public void setOwner( GraphicComposite spriteComposite){
    	this.ownerComposite = spriteComposite;
    }
    
    public GraphicComposite ownerComposite(){
    	return this.ownerComposite;
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
    
    public void setAngle( double angle){
		this.spriteAngle = angle;
	}
    
}
