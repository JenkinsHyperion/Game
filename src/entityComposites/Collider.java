package entityComposites;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import engine.MovingCamera;
import entities.EntityRotationalDynamic;
import misc.CollisionEvent;
import misc.NullCollisionEvent;
import physics.Boundary;
import physics.BoundaryPolygonal;
import physics.CollidingPair;
import physics.CollisionCheck;
import physics.CollisionEngine;
import physics.Collision;
import physics.Side;
import physics.Vector;
import physics.VisualCollisionCheck;

public class Collider implements EntityComposite{
	protected String compositeName;
	protected EntityStatic ownerEntity;
	
	protected CollisionEngine engine;
	protected int engineHashID;
	protected CollisionEngine.ActiveCollider engineSlot;

	protected Boundary boundary;
	
	protected float friction = 1;
	protected float mass = 1;
	
	protected ArrayList<CollidingPair> collisionInteractions = new ArrayList<>();

	private CollisionEvent uponCollision = new NullCollisionEvent();
	private CollisionEvent uponLeavingCollision = new NullCollisionEvent();
	
	protected Collider(){
		this.boundary = null;
		this.ownerEntity = null; 
		compositeName = this.getClass().getSimpleName();
	}
	
	public Collider( EntityStatic owner , Boundary boundary){
		
		this.boundary = boundary;
		this.ownerEntity = owner; 
		compositeName = this.getClass().getSimpleName();
	}
	
	public Collider( EntityStatic owner , Line2D[] lines){
		
		//lines[lines.length-1].setLine( lines[lines.length-1].getP1() , lines[0].getP1() );
		
		this.boundary = new BoundaryPolygonal( lines ) ;
		this.ownerEntity = owner; 
		compositeName = this.getClass().getSimpleName();
	}

	public void setBoundary( Boundary boundary ){
		this.boundary = boundary;
	}
	
	/* #################################################################
	 * 		ENGINE FUNCTIONALITY
	 * #################################################################
	 */

	public void checkForInteractionWith( Collider entity, CollisionCheck checkType ,CollisionEngine engine){ 
		//We know owner entity has composite collidable, which is THIS instance of collidable, so pass owner's physical
		// information to the other entity
		
		engine.registerCollision( checkType.check(this, entity) , this , entity , checkType );
		//Physical constants like mass, restitution (bounciness), rotational friction and other stuff to be passed 
		// to collisionEngine here:
		
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

    @Deprecated
	public Boundary getBoundaryLocal(){
		return boundary.atPosition( ownerEntity.getPosition() );
	}
	@Deprecated
	public Boundary getBoundaryDelta(){
		Point positionDelta = new Point( 
				(int)ownerEntity.getTranslationComposite().getDeltaX(ownerEntity) , 
				(int)ownerEntity.getTranslationComposite().getDeltaY(ownerEntity) 
		);
		return boundary.atPosition( positionDelta );
	}
	
	/* #################################################################################
	 * 
	 * 		EVENT METHODS 
	 * 
	 * #################################################################################
	 */
	
	public void onCollisionEvent(){ 
		uponCollision.run(null, null, null);
	}
	
	public void onLeavingCollisionEvent(){
		
		//uponLeavingCollision.run();
	}
	
	public void onLeavingAllCollisionsEvent(){
		
		uponLeavingCollision.run( null , null , null );
	}
	
	public void setLeavingCollisionEvent( CollisionEvent leavingEvent ){
		uponLeavingCollision = leavingEvent;
	}
	
	public void setCollisionEvent( CollisionEvent collidingEvent ){
		uponCollision = collidingEvent;
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
    	for ( int i = index ; i < collisionInteractions.size() ; i++) {
    		collisionInteractions.get(i).collision().indexShift(collisionInteractions.get(i).pairID());
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
		System.out.println("\nCollisions on "+ ownerEntity.name );
		for ( int i = 0 ; i < collisionInteractions.size() ; i++) 
		System.out.println("---" + i + " " + collisionInteractions.get(i).collision().collisionDebugTag);
	}
	@Override
	public EntityStatic getOwnerEntity(){
		return this.ownerEntity;
	}

	public void debugDrawBoundary(MovingCamera camera , Graphics2D g){
		this.getBoundaryLocal().debugDrawBoundary( camera , g, this.ownerEntity );
	}
	
	public void debugDrawBoundaryDelta(MovingCamera camera , Graphics2D g){
		this.getBoundaryDelta().debugDrawBoundary( camera , g, this.ownerEntity );
	}
	
	public float getMass(){ return mass; }
	
	public void applyPointMomentum( Vector momentum , Point2D point ){
		
		EntityRotationalDynamic thisEntity = (EntityRotationalDynamic)this.ownerEntity;
		
		//Vector momentumLinear = new Vector( mass*thisEntity.getDX() , mass*thisEntity.getDY() );
		Vector radius = new Vector( thisEntity.getX() - point.getX() , thisEntity.getY() - point.getY() );
		double momentumAngular = momentum.crossProduct( radius );
		
		thisEntity.setAngularVelocity( momentumAngular * 0.05 );
		
	}
	@Deprecated
	public void applyPointForce( Vector force , Point2D point ){
		
		EntityRotationalDynamic thisEntity = (EntityRotationalDynamic)this.ownerEntity;
		
		//Vector momentumLinear = new Vector( mass*thisEntity.getDX() , mass*thisEntity.getDY() );
		Vector radius = new Vector( thisEntity.getX() - point.getX() , thisEntity.getY() - point.getY() );
		double torque = force.crossProduct( radius );
		
		thisEntity.setAngularVelocity( torque * 0.1 );
		
	}
	
	
	public void addCompositeToPhysicsEngineStatic( CollisionEngine engine ){ 
		this.engineSlot = engine.addStaticCollidable( this );
		this.engine = engine;
		System.out.print("   "+this+" adding static to collision engine");
	}
	
	public void addCompositeToPhysicsEngineDynamic( CollisionEngine engine ){
		System.out.print("Adding dynamic to collision engine ");
		this.engineSlot = engine.addDynamicCollidable( this );
		this.engine = engine;
	}

	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public void disableComposite(){
		this.engineSlot.removeSelf();
	}
	@Override
	public String toString() {
		return this.compositeName;
	}

	@Override
	public void setCompositeName(String newName) {
		this.compositeName = newName;
	}
	@Override
	public String getCompositeName() {
		return this.compositeName;		
	}


}

