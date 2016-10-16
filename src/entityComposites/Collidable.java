package entityComposites;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import engine.Camera;
import entities.EntityStatic;
import physics.Boundary;
import physics.BoundingBox;
import physics.CollidingPair;
import physics.Collision;
import physics.CollisionCheck;
import physics.CollisionEngine;

public final class Collidable extends CollisionProperty{
	
	protected transient Boundary boundary;
	
	protected transient ArrayList<CollidingPair> collisionInteractions = new ArrayList<>();
	
	protected transient Collision collisionMath;


	
	public Collidable( EntityStatic owner ){
		
		this.owner = owner;
	}
	
public Collidable( EntityStatic owner , Boundary boundary){
		
		this.boundary = boundary;
		this.owner = owner;
	}
	
	/* #################################################################
	 * 		ENGINE FUNCTIONALITY
	 * #################################################################
	 */
	@Override
	public void checkForInteractionWith( CollisionProperty entity, CollisionCheck checkType ,CollisionEngine engine){ 
		//We know owner entity has composite collidable, which is THIS instance of collidable, so pass owner's physical
		// information to the other entity
		
		entity.passInteraction(this, checkType, engine); //THIS is collidable, so
		
		//Physical constants like mass, restitution (bounciness), rotational friction and other stuff to be passed 
		// to collisionEngine here:
		
	}
	@Override
	public void passInteraction( Collidable entity, CollisionCheck checkType , CollisionEngine engine ){ 
		//entity is collidable because if it wasn't it non-collidable would have terminated and returned WARNING to console.
		//Both entities have passed through null object pattern, so now we can perform the actual
		// check for interaction. For now we only have the SEPARATING AXIS THEOREM SAT check.
		
		engine.registerCollision( checkType.check(this, entity) , this , entity);
		
	}
	
	//################################################
	
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
	
	public Boundary getBoundary(){
		return boundary;
	}
	public void setBoundary(Boundary newBoundary){
		this.boundary = newBoundary;
	}
    
	public Boundary getBoundaryLocal(){
		return boundary.atPosition( (int)owner.getX(),(int)owner.getY() );
	}
	
	public Boundary getBoundaryDelta(){
		return boundary.atPosition( (int)owner.getDeltaX(),(int)owner.getDeltaY() );
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
    	collisionInteractions.add( new CollidingPair(collision , pairIndex) );
    	//printCollisions();
    	return ( collisionInteractions.size() - 1 ); //return index of added element
    }
    
    public void removeCollision(int index){ //Remove collision 
    	//System.out.println("Removing " + index + " from "+name );
    	
    	collisionInteractions.remove(index);
    	//decrement indexes for all following collisions involving this entity
    	for ( int i = index ; i < collisionInteractions.size() ; i++) {
    		collisionInteractions.get(i).collision().indexShift(collisionInteractions.get(i).pairID());
    	} 
    	//printCollisions();
    }
	
	public Collision[] getCollisions(){
		Collision[] returnCollisions = new Collision[collisionInteractions.size()];
		for ( int i = 0 ; i < collisionInteractions.size() ; i++ ){
			returnCollisions[i] = collisionInteractions.get(i).collision();
		}
		return returnCollisions;
    }
	
	public EntityStatic[] getCollidingPartners(){
		EntityStatic[] partners = new EntityStatic[ collisionInteractions.size() ];
		for (int i = 0 ; i < collisionInteractions.size() ; i++){
			partners[i] = collisionInteractions.get(i).collision().getEntityInvolved( collisionInteractions.get(i).partnerID() ) ;
		}
		return partners;
	}
	
	private void printCollisions() {
		System.out.println("\nCollisions on "+ owner.name );
		for ( int i = 0 ; i < collisionInteractions.size() ; i++) 
		System.out.println("---" + i + " " + collisionInteractions.get(i).collision().collisionName);
	}
	
	public EntityStatic getOwner(){
		return this.owner;
	}

	@Override
	public void debugDrawBoundary(Camera camera , Graphics2D g){
		
		for ( Line2D side : this.getBoundaryLocal().getSides()){
			//g.draw(side);
			camera.draw(side, g);
		}
		
	}
	
}
