package entities;

import java.awt.Rectangle;
import animation.Animation;
import physics.Boundary;
import physics.BoundingBox;
import sprites.SpriteAnimated;
import sprites.SpriteStill;
import sprites.Sprite;

/*
 * Static Entity class, for unmoving sprites. Has graphic that can be either still image or animation.
 */
public class EntityStatic extends Entity{

	public boolean isSelected;

	private Sprite graphic; //might want to put into super class unless Entity without image is useful
	protected Rectangle boundingBox = new Rectangle(0,0); //Should be moved to intermediate class for only collidables
	
	protected Boundary boundary = new BoundingBox(new Rectangle(2,2));
	
    public EntityStatic(int x, int y) {
    	super(x,y);
    	isSelected = false;
        //this.x = x;
        //this.y = y;
        //visibility = true;
    }
    
    protected void loadSprite(String path){ // needs handling if failed. Also needs to be moved out of object class into sprites
    	graphic = new SpriteStill(System.getProperty("user.dir").replace( "\\", "//" ) + "//Assets//" +path + ".png");
    }
    
    /*protected void loadAnimatedSprite(String path){ // needs handling if failed. 
    	graphic = new SpriteAnimatedTest(System.getProperty("user.dir").replace( "\\", "//" ) + "//Assets//" +path + ".png");
    }*/
    
    protected void loadAnimatedSprite(Animation a){ // needs handling if failed. 
    	graphic = new SpriteAnimated(a); 
    }
    
    //OPTIONAL INIT WITH OFFSET
    protected void loadAnimatedSprite(Animation a, int offsetX, int offsetY){ // needs handling if failed. 
    	graphic = new SpriteAnimated(a); 
    }
    
    public Sprite getObjectGraphic(){ // gets the Object's sprite, still image or animation
    	return graphic;
    }
    /**
     * 
     * @param x_offset
     * @param y_offset
     * @param width
     * @param height
     * <b /> Sets the x and y coordinates and width and height for this object's bounding box
     */
    public void setBoundingBox(int x_offset, int y_offset , int width , int height) {
    	
        boundingBox = new Rectangle(x_offset, y_offset, width , height);
        boundary = new BoundingBox(boundingBox);
    }
    
    //overloaded function to accept Rectangle that getBounds() will return.
    /**
     * 
     * @param getBounds The rectangle that will be passed any time getBounds() is called [or maybe getBounds2D, gotta try it]
     */
    //public void setBoundingBox(Rectangle getBounds){
    //	boundingBox = getBounds;
    //	boundary = new BoundingBox(getBounds);
    //}
	
	public Rectangle getBoundingBox(){ //move position to override in dynamic entity since static doesnt need position calc.
		return new Rectangle (getX() + boundingBox.x , getY() + boundingBox.y , boundingBox.width , boundingBox.height);
	}
	
	public Boundary getLocalBoundary(){
		return boundary.atPosition((int)x,(int)y);
	}
	
	public Boundary getBoundary(){
		return boundary;
	}

	@Override
	public void selfDestruct(){
		setBoundingBox(0,0,0,0); // This is almost exclusively so the Collision Detector closes collisions with dead entities
		alive = false;
	}
	
	
	public String toString()
	{
		return String.format("%s", this);
	}
	
	
}
