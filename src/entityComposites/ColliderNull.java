package entityComposites;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

import engine.Camera;
import entities.EntityRotationalDynamic;
import entities.EntityStatic;
import misc.CollisionEvent;
import physics.Boundary;
import physics.CollidingPair;
import physics.Collision;
import physics.CollisionCheck;
import physics.CollisionEngine;
import physics.Side;
import physics.Vector;

public final class ColliderNull extends Collider{
	
	private final static ColliderNull nullCollision = new ColliderNull();

	private ColliderNull(){
		super(null);
	}
	
	public static ColliderNull getNonCollidable(){ 
		return nullCollision;
	}
	
	
	@Override	
	public void setBoundary( Boundary boundary ){
		System.err.println("Warning: Attempting to set Boundary on NonCollidable ");
		this.boundary = boundary;
	}
	
	/* #################################################################
	 * 		ENGINE FUNCTIONALITY
	 * #################################################################
	 */
	@Override
	public void checkForInteractionWith( Collider entity, CollisionCheck checkType ,CollisionEngine engine){ 
		System.err.println("Warning: Collision info checked on NonCollidable ");
	}
	@Override
	public void passInteraction( Collider entity, CollisionCheck checkType , CollisionEngine engine ){ 
		System.err.println("Warning: Collision info checked on NonCollidable ");
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
    
	@Override
	public Boundary getBoundary(){
		System.err.println("Warning: Attempted to get Boundary on NonCollidable ");
		return boundary;
	}

	@Override
	public Boundary getBoundaryLocal(){
		System.err.println("Warning: Attempted to get Boundary on NonCollidable ");
		return boundary;
	}
	@Override
	public Boundary getBoundaryDelta(){
		System.err.println("Warning: Attempted to get Boundary on NonCollidable ");
		return boundary;
	}
	
	/* #################################################################################
	 * 
	 * 		EVENT METHODS 
	 * 
	 * #################################################################################
	 */
	@Override
	public void onCollisionEvent(){ 
	}
	@Override
	public void onLeavingCollisionEvent(){	
	}
	@Override
	public void onLeavingAllCollisionsEvent(){
	}
	@Override
	public void setLeavingCollisionEvent( CollisionEvent leavingEvent ){
		System.err.println("Warning: Attempted to set Leaving Collision Event on NonCollidable ");
	}
	@Override
    public int addCollision(Collision collision , boolean pairIndex){
    	return ( 0 ); //return index of added element
    }
	@Override
    public void removeCollision(int index){ //Remove collision 
		System.err.println("Warning: Attempted to get remove Collision from NonCollidable ");	
    }
	@Override
	public Collision[] getCollisions(){
		Collision[] returnCollisions = new Collision[0];
		System.err.println("Warning: Attempted to get Collisions from NonCollidable ");
		return returnCollisions;
    }
	@Override
	public EntityStatic[] getCollidingPartners(){
		EntityStatic[] partners = new EntityStatic[ 0 ];
		System.err.println("Warning: Attempted to get Colliding Partners from NonCollidable ");
		return partners;
	}
	@Override
	public EntityStatic getOwnerEntity(){
		System.err.println("Warning: Attempted to get owner Entity of NonCollidable ");
		return this.owner;
	}
	@Override
	public void debugDrawBoundary(Camera camera , Graphics2D g){
		System.err.println("Warning: Attempted to draw Boundary of NonCollidable ");
	}
	@Override
	public float getMass(){ 
		System.err.println("Warning: Attempted to get mass of NonCollidable ");
		return 1; 
		}
	@Override	
	public void applyPointMomentum( Vector momentum , Point2D point ){
		System.err.println("Warning: Attempted to apply Point Momentum on NonCollidable ");
		
	}
	@Deprecated
	public void applyPointForce( Vector force , Point2D point ){
		System.err.println("Warning: Attempted to apply Point Force on NonCollidable ");
	}
	
	@Override
	public void addCompositeToPhysicsEngine( CollisionEngine engine ){ 
		System.err.println("Warning: Attempted to add NonCollidable to Collision Engine ");
	}
	
	
	
}