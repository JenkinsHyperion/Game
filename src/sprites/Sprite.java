package sprites;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import animation.Animation;
import engine.MovingCamera;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import physics.Vector;

/*
 * This is the base Sprite class. 
 */
public abstract class Sprite implements Graphic{
	
    protected boolean visibility;
    protected int spriteOffsetX = 0;
    protected int spriteOffsetY = 0;
    protected double spriteSizePercent = 1;
    protected double spriteAngle = 0;
    protected String fileName;
    
    public static final byte CENTERED = 0;
    
	AffineTransform spriteTransform = new AffineTransform();

	protected Sprite( String fileName , int xOffset, int yOffset  ){
		this.fileName = fileName;
		this.spriteOffsetX = xOffset;
		this.spriteOffsetY = yOffset;
	}
	
//ABSTRACT FUNCTIONS 
	//This is a getImage() that works for both still and animated sprites, so draw functions in Board 
    //can call a generalized format.
    //public abstract void draw(Camera camera);
    //public abstract void draw();

	public abstract Image getImage(); 
	public abstract BufferedImage getBufferedImage();
	
	public abstract Animation getAnimation(); 
	
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
    public Point getOffsetPoint() {
    	return new Point(spriteOffsetX, spriteOffsetY);
    }
    public void setOffset(int x , int y){
    	spriteOffsetX = x;
    	spriteOffsetY = y;
    }
    public Vector getRelativePoint(Vector worldVector) {
    	double newX = worldVector.getX() * (Math.cos(Math.toRadians(spriteAngle)));
    	double newY = worldVector.getY() * (Math.sin(Math.toRadians(spriteAngle)));
    	return new Vector(newX, newY);
    }
    
    public void setAngle( double angle){
		this.spriteAngle = angle;
	}
    public double getAngle() {
    	return this.spriteAngle;
    }
    public String getPathName(){
    	return this.fileName;
    }

	public void setSizeFactor(double factor) {
		this.spriteSizePercent = factor;
	}
	public double getSizeFactor() {
		return spriteSizePercent;
	}
    
}
