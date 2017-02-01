package entityComposites;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import engine.Camera;
import entities.EntityDynamic;
import entities.EntityRotationalDynamic;
import entities.EntityStatic;
import misc.CollisionEvent;
import misc.NullCollisionEvent;
import physics.Boundary;
import physics.CollidingPair;
import physics.Collision;
import physics.CollisionCheck;
import physics.CollisionEngine;
import physics.Force;
import physics.Side;
import physics.Vector;

public final class Collidable extends CollisionProperty{
	
	protected Boundary boundary;
	
    private float mass = 1;
	
	protected ArrayList<CollidingPair> collisionInteractions = new ArrayList<>();
	
	protected ArrayList<Force> forces = new ArrayList<>();
	
	protected Collision collisionMath;

	private CollisionEvent uponLeavingCollision = new NullCollisionEvent();
	
	public Collidable( EntityStatic owner ){
		
		this.owner = owner;
		this.boundary = null;
	}
	
	public Collidable( EntityStatic owner , Boundary boundary){
		
		this.boundary = boundary;
		this.owner = owner; 
		
	}
	
	public Collidable( EntityStatic owner , Line2D[] lines){
		
		this.boundary = new Boundary( lines , this ) ;
		this.owner = owner; 
		
	}

	public void setBoundary( Boundary boundary ){
		
		
		this.boundary = boundary;
		
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
    
	
	public Boundary getBoundary(){
		return boundary;
	}

    
	public Boundary getBoundaryLocal(){
		return boundary.atPosition( owner.getPos() );
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
	
	public void onCollisionEvent(){ 
		
	}
	
	public void onLeavingCollisionEvent(){
		
		//uponLeavingCollision.run();
	}
	
	public void onLeavingAllCollisionsEvent(){
		
		uponLeavingCollision.run( null , null );
	}
	
	public void setLeavingCollisionEvent( CollisionEvent leavingEvent ){
		uponLeavingCollision = leavingEvent;
	}
	
	/**
	 * @param collision
	 * @return Adds collision to this entity's current collisions and return the index where it was put
	 */
    public int addCollision(Collision collision , boolean pairIndex){
    	
    	collisionInteractions.add( new CollidingPair(collision , pairIndex) );
    	return ( collisionInteractions.size() - 1 ); //return index of added element
    }
    
    public void removeCollision(int index){ //Remove collision 
    	
    	collisionInteractions.remove(index);
    	if ( collisionInteractions.size() == 0 ){
    		onLeavingAllCollisionsEvent();
    	}
    	else{
	    	for ( int i = index ; i < collisionInteractions.size() ; i++) {
	    		collisionInteractions.get(i).collision().indexShift(collisionInteractions.get(i).pairID());
	    	} 
    	}
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
		System.out.println("---" + i + " " + collisionInteractions.get(i).collision().collisionDebugTag);
	}
	
	public EntityStatic getOwnerEntity(){
		return this.owner;
	}

	@Override
	public void debugDrawBoundary(Camera camera , Graphics2D g){
		
		for ( Side side : this.getBoundaryLocal().getSides() ){
			//g.draw(side);
			camera.draw( side.toLine() , g);
			camera.drawString(side.toString(), side.getX1()+(side.getX2()-side.getX1())/2 , side.getY1()+(side.getY2()-side.getY1())/2 , g);
		}
		
	}
	
	public float getMass(){ return mass; }
	
	public void applyPointMomentum( Vector momentum , Point2D point ){
		
		EntityRotationalDynamic thisEntity = (EntityRotationalDynamic)this.owner;
		
		//Vector momentumLinear = new Vector( mass*thisEntity.getDX() , mass*thisEntity.getDY() );
		Vector radius = new Vector( thisEntity.getX() - point.getX() , thisEntity.getY() - point.getY() );
		double momentumAngular = momentum.crossProduct( radius );
		
		thisEntity.setAngularVelocity( momentumAngular * 0.05 );
		
	}
	
	public void applyPointForce( Vector force , Point2D point ){
		
		EntityRotationalDynamic thisEntity = (EntityRotationalDynamic)this.owner;
		
		//Vector momentumLinear = new Vector( mass*thisEntity.getDX() , mass*thisEntity.getDY() );
		Vector radius = new Vector( thisEntity.getX() - point.getX() , thisEntity.getY() - point.getY() );
		double torque = force.crossProduct( radius );
		
		thisEntity.setAngularVelocity( torque * 0.1 );
		
	}
	
	/** Creates new Force on this Collidable out of input Vector, and returns the Force that was added
	 * 
	 * @param vector
	 * @return
	 */
    public Force addForce( Vector vector ){

    	int indexID = forces.size();     	
    	Force newForce = new Force( vector , indexID );
    	forces.add( newForce ) ;
    	return newForce;
    }
    
    public void removeForce(int index){ //Remove collision 
    	
    	forces.remove(index); 
	    for ( int i = index ; i < forces.size() ; i++) {
	    	forces.get(i).indexShift();
	    } 
    }
    
    public Vector[] debugForceArrows(){
    	Vector[] returnVectors = new Vector[ forces.size() ];
    	for ( int i = 0 ; i < forces.size() ; i++ ){
    		returnVectors[i] = forces.get(i).getVector() ;
    	}
    	return returnVectors;
    }
	
    public Vector sumOfForces(){
    	
    	Vector returnVector = new Vector(0,0);
    	for ( Force force : forces ){
    		returnVector = returnVector.add( force.getVector() );
    	}
    	
    	return returnVector;
    }
    
	
}
