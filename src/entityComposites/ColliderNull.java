package entityComposites;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

import engine.MovingCamera;
import misc.CollisionEvent;
import physics.Boundary;
import physics.BoundaryPolygonal;
import physics.CollidingPair;
import physics.CollisionCheck;
import physics.CollisionEngine;
import physics.Collision;
import physics.BoundarySide;
import physics.Vector;

public final class ColliderNull extends Collider{ 
	//private String compositeName;
	private final static ColliderNull nullCollision = new ColliderNull();

	private ColliderNull(){
		super();
	}
	
	public static ColliderNull nullColliderComposite(){ 
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
		return this.ownerEntity;
	}
	@Override
	public void debugDrawBoundary(MovingCamera camera , Graphics2D g){
		System.err.println("Warning: Attempted to draw Boundary of NonCollidable ");
	}
	/*
	@Override	
	public void applyPointMomentum( Vector momentum , Point2D point ){
		System.err.println("Warning: Attempted to apply Point Momentum on NonCollidable ");
	}*/
	@Deprecated
	public void applyPointForce( Vector force , Point2D point ){
		System.err.println("Warning: Attempted to apply Point Force on NonCollidable ");
	}
	
	@Override
	public void addCompositeToPhysicsEngineStatic( CollisionEngine engine ){ 
		System.err.println("Warning: Attempted to add NonCollidable to Collision Engine ");
	}
	
	@Override
	public boolean exists(){
		return false;
	}
	
	@Override
	protected void changeColliderToStaticInEngine() {
		//DO NOTHING, DYMAMIC ENTITY HAD NO COLLIDER
	}
	
	@Override
	public void deactivateCollider() {
		
	}
	
	@Override
	public void activateCollider() {
		
	}
	
	@Override
	public void disableComposite() {
		System.err.println("No collider to disable");
	}


	@Override
	public String toString() {
		return this.compositeName;
	}
}
