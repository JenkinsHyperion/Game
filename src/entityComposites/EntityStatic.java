package entityComposites;

import java.awt.Point;
import java.io.File;
import animation.Animation;
import entityComposites.*;
import physics.Boundary;
import physics.CollisionEngine;
import sprites.SpriteAnimated;
import sprites.SpriteStillframe;
import sprites.RenderingEngine;
import sprites.Sprite;

/*
 * Static Entity class, for unmoving sprites. Has graphic that can be either still image or animation.
 */
public class EntityStatic extends Entity{

	//COMPOSITE VARIABLES, LATER TO BE LIST OF COMPOSITES
	protected TranslationComposite translationType;
	protected GraphicComposite spriteType = GraphicCompositeNull.getNullSprite(); 
	protected Collider collisionType = ColliderNull.getNonCollidable();
	
	public EntityStatic(int x, int y) {

    	super(x,y);
    	//isSelected = false;
    }  
	
	//COMPOSITE CONTRUCTION
	public EntityStatic( String name , int x, int y) {
    	super(x,y);
    	this.name = name;
    	
    }

	public EntityStatic(Point entityPosition) {
		super( entityPosition.x , entityPosition.y );
	}

	protected void setGraphicComposite(GraphicComposite spriteType){ 
		this.spriteType = spriteType; 
		}
	
	public GraphicComposite getGraphicComposite(){ 	
		return this.spriteType; 
	}
	
	protected void setTranslationComposite( TranslationComposite translationType ){ 
		this.translationType = translationType; 
	}
	
	public TranslationComposite getTranslationComposite(){
		return this.translationType;
	}
	
	protected void setCollisionComposite(Collider collisionType){ 
		this.collisionType = collisionType; 
	}

	public Collider getColliderComposite(){
		return this.collisionType;			
	}
	
	
	@Deprecated
    public void loadSprite(String path){ // needs handling if failed. Also needs to be moved out of object class into sprites

		CompositeFactory.addGraphicTo( this , new SpriteStillframe( path , this) );
    	
    }
    @Deprecated
    public void loadSprite(String path, int offset_x , int offset_y){ // needs handling if failed. Also needs to be moved out of object class into sprites
    	
    	CompositeFactory.addGraphicTo( this , new SpriteStillframe( path , this) );
    }

    
    //OPTIONAL INIT WITH OFFSET
    protected void loadAnimatedSprite(Animation a, int offsetX, int offsetY){ // needs handling if failed. 
    	this.getGraphicComposite().setSprite( new SpriteAnimated(a , offsetX,offsetY)); 
    	
    }

    @Deprecated
    public Sprite getEntitySprite(){ // gets the Object's sprite, still image or animation MOVE TO SPRITEPROPERTY
    	return ((GraphicComposite)this.spriteType).getSprite();
    }
    @Deprecated
    public void setEntitySprite( Sprite entitySprite ){
    	 ((GraphicComposite)this.spriteType).setSprite( entitySprite );
    }
    @Deprecated
    public int getSpriteOffsetX(){
    	return ((GraphicComposite)this.spriteType).getSprite().getOffsetX(); 
    }
    @Deprecated
    public int getSpriteOffsetY() {
    	return ((GraphicComposite)this.spriteType).getSprite().getOffsetY();
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
    	
        //boundingBox = new Rectangle(x_offset, y_offset, width , height);
        //boundary = new BoundingBox(boundingBox);
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
	
	//public Rectangle getBoundingBox(){ //move position to override in dynamic entity since static doesnt need position calc.
	//	return new Rectangle (getX() + boundingBox.x , getY() + boundingBox.y , boundingBox.width , boundingBox.height);
	//}

	//public Boundary getBoundaryLocal(){
		
	//	return ((Collidable)collisionType).getBoundary().atPosition((int)x,(int)y);
	//}
	
	public Boundary getBoundary(){
		
	    return ((Collider)collisionType).getBoundary();
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
	
	public String toString()
	{
		return name;
	}
	
	/**
	 * @param collision
	 * @return Adds collision to this entity's current collisions and return the index where it was put
	 */
    /*public int addCollision(Collision collision , boolean pairIndex){
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
	}*/

	public void move(Point distance) { //MAKE VECTOR LATER

		x=x+(float)distance.getX();
		y=y+(float)distance.getY();
	}

	public float getDeltaX() { return this.getX(); }
	public float getDeltaY() { return this.getY(); }
	
	public boolean hasCollider(){
		return !( this.collisionType instanceof ColliderNull );
	}
	
	public boolean hasGraphics(){
		return !( this.spriteType instanceof GraphicComposite );
	}
	
	public void deconstruct(){
		//
	}
	
}
