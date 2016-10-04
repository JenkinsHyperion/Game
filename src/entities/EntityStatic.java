package entities;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import animation.Animation;
import entityComposites.*;
import physics.Boundary;
import physics.BoundingBox;
import physics.CollidingPair;
import physics.Collision;
import sprites.SpriteAnimated;
import sprites.SpriteNull;
import sprites.SpriteStillframe;
import sprites.Sprite;

/*
 * Static Entity class, for unmoving sprites. Has graphic that can be either still image or animation.
 */
public class EntityStatic extends Entity{

	public transient boolean isSelected;
    protected transient ArrayList<CollidingPair> collisions = new ArrayList<>(); //moving to composit
	protected transient Rectangle boundingBox = new Rectangle(0,0); //moving to composite
	protected transient Boundary boundary = new BoundingBox(new Rectangle(2,2)); //moving to composite
	protected int offsetX;
	protected int offsetY;
	//COMPOSITE TESTING
	private Sprite entitySprite = SpriteNull.getNullSprite(); //might want to put into super class unless Entity without image is useful
	private CollisionType collisionType;
   
	public EntityStatic(int x, int y) {

    	super(x,y);
    	isSelected = false;
    }  
	
    public void loadSprite(String path){ // needs handling if failed. Also needs to be moved out of object class into sprites


    	entitySprite = new SpriteStillframe(System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path, this);
    }
    
    public void loadSprite(String path, int offset_x , int offset_y){ // needs handling if failed. Also needs to be moved out of object class into sprites
    	entitySprite = new SpriteStillframe(System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path, offset_x,offset_y, this);


    }
    
    /*protected void loadAnimatedSprite(String path){ // needs handling if failed. 
    	graphic = new SpriteAnimatedTest(System.getProperty("user.dir").replace( "\\", "//" ) + "//Assets//" +path + ".png");
    }*/
    
    protected void loadAnimatedSprite(Animation a){ // needs handling if failed. 
    	entitySprite = new SpriteAnimated(a,this); 
    }
    
    //OPTIONAL INIT WITH OFFSET
    protected void loadAnimatedSprite(Animation a, int offsetX, int offsetY){ // needs handling if failed. 
    	entitySprite = new SpriteAnimated(a,offsetX,offsetY,this); 
    }
    
    protected void setEntitySpriteOffset(int x , int y){
    	entitySprite.setOffset(x, y); 
    }
    
    public Sprite getEntitySprite(){ // gets the Object's sprite, still image or animation
    	return entitySprite;
    }
    public int getSpriteOffsetX(){
    	return entitySprite.getOffsetX(); 
    }
    public int getSpriteOffsetY() {
    	return entitySprite.getOffsetY();
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
	
	public Boundary getBoundaryLocal(){
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
	
	/* #################################################################################
	 * 
	 * 		EVENT METHODS - to be overriden in children classes
	 * 
	 * #################################################################################
	 */
	
	public void onCollisionEvent(){ //TO BE MOVED TO COLLIDABLE INTERFACE
		
	}
	
	public void onCollisionCompletion(){ //TO BE MOVED TO COLLIDABLE INTERFACE
		
	}
	
	public String toString()
	{
		return String.format("%s", this);
	}
	
	/**
	 * @param collision
	 * @return Adds collision to this entity's current collisions and return the index where it was put
	 */
    public int addCollision(Collision collision , boolean pairIndex){
    	collisions.add( new CollidingPair(collision , pairIndex) );
    	//printCollisions();
    	onCollisionEvent();
    	return ( collisions.size() - 1 );
    }
    
    public void removeCollision(int index){ //Remove collision 
    	//System.out.println("Removing " + index + " from "+name );
    	collisions.remove(index);
    	//decrement indexes for all following collisions involving this entity
	    for ( int i = index ; i < collisions.size() ; i++) {
	    	collisions.get(i).collision().indexShift(collisions.get(i).pairID());
	    } 

    	//printCollisions();
    }
	
	public Collision[] getCollisions(){
		Collision[] returnCollisions = new Collision[collisions.size()];
		for ( int i = 0 ; i < collisions.size() ; i++ ){
			returnCollisions[i] = collisions.get(i).collision();
		}
		return returnCollisions;
    }
	
	public EntityStatic[] getCollidingPartners(){
		EntityStatic[] partners = new EntityStatic[ collisions.size() ];
		for (int i = 0 ; i < collisions.size() ; i++){
			partners[i] = collisions.get(i).collision().getEntityInvolved( collisions.get(i).partnerID() ) ;
		}
		return partners;
	}
	
	private void printCollisions() {
		System.out.println("\nCollisions on "+ name );
		for ( int i = 0 ; i < collisions.size() ; i++) 
		System.out.println("---" + i + " " + collisions.get(i).collision().collisionName);
	}

	public void move(Point distance) { //MAKE VECTOR LATER

		x=x+(float)distance.getX();
		y=y+(float)distance.getY();
	}
	
}
