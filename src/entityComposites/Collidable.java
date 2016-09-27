package entityComposites;

import java.awt.Rectangle;
import java.util.ArrayList;

import entities.EntityStatic;
import physics.Boundary;
import physics.BoundingBox;
import physics.CollidingPair;
import physics.Collision;

public final class Collidable extends CollisionType{
	
	protected transient ArrayList<CollidingPair> collisions = new ArrayList<>();
	
	protected transient Boundary boundary;
	private EntityStatic owner;
	
	public Collidable(  ){
		
		this.boundary = boundary;
		this.owner = owner;
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
    	
        boundary = new BoundingBox( new Rectangle(x_offset, y_offset, width , height) );
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
	
	public Boundary getBoundaryLocal(){
		return boundary.atPosition( (int)owner.getX(),(int)owner.getY() );
	}
	
	public Boundary getBoundary(){
		return boundary;
	}

	
	/* #################################################################################
	 * 
	 * 		EVENT METHODS 
	 * 
	 * #################################################################################
	 */
	
	public void onCollisionEvent(){ //TO BE MOVED TO COLLIDABLE INTERFACE
		
	}
	
	/**
	 * @param collision
	 * @return Adds collision to this entity's current collisions and return the index where it was put
	 */
    public int addCollision(Collision collision , boolean pairIndex){
    	collisions.add( new CollidingPair(collision , pairIndex) );
    	//printCollisions();
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
		System.out.println("\nCollisions on "+ owner.name );
		for ( int i = 0 ; i < collisions.size() ; i++) 
		System.out.println("---" + i + " " + collisions.get(i).collision().collisionName);
	}
	
	

}
