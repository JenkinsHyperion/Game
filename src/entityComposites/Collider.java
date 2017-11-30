package entityComposites;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import engine.MovingCamera;
import misc.CollisionEvent;
import misc.NullCollisionEvent;
import physics.Boundary;
import physics.BoundaryFeature;
import physics.BoundaryPolygonal;
import physics.CollidingPair;
import physics.CollisionCheck;
import physics.CollisionEngine;
import physics.CollisionEngine.ActiveCollider;
import physics.Collision;
import physics.BoundarySide;
import physics.BoundaryVertex;
import physics.Vector;
import physics.VisualCollisionCheck;

public class Collider implements EntityComposite{
	protected String compositeName;
	protected EntityStatic ownerEntity;
	
	protected boolean isGrouped = false;
	
	protected CollisionEngine engine;
	protected int engineHashID;
	private CollisionEngine.ActiveCollider engineSlot = null;

	protected Boundary boundary;

	protected boolean isActive = true;
	
	protected ArrayList<CollidingPair> collisionInteractions = new ArrayList<CollidingPair>();

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
	/**Sets boundary for this collider and notifies corresponding ActiveCOllider wrapper in collision engine.
	 * 
	 * @param boundary
	 */
	public void setBoundary( Boundary boundary ){
		this.boundary = boundary;
		
		this.engineSlot.notifyBoundaryChange( boundary , this.isActive );
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
    
	
	public Boundary getBoundary(){
		return boundary;
	}
    
    public Line2D getRelativeAxis( Line2D axis ){
    	return axis;
    }
    
    public Line2D getAbsoluteAxisFromRelativeAxis( Line2D axis ){
    	return axis;
    }
    
    public Point getBoundaryCenter(){
    	return this.boundary.getRelativeOffset();
    }
	
	/* #################################################################################
	 * 
	 * 		EVENT METHODS 
	 * 
	 * #################################################################################
	 */
	
	public void onCollisionEvent(){ 
		//uponCollision.run(null, null, null);
	}
	
	public void onLeavingCollisionEvent(){
		
		//uponLeavingCollision.run();
	}
	
	public void onLeavingAllCollisionsEvent(){
		
		uponLeavingCollision.run( null , null , null, null );
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
		//this.getBoundaryLocal().debugDrawBoundary( camera , g, this.ownerEntity );
		this.getBoundary().debugDrawBoundary(camera, g, this.ownerEntity);
	}
	
	/*public void applyPointMomentum( Vector momentum , Point2D point ){
		
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
	*/
	public void addColliderToGroup(String group){
		
		//TODO FIXME 
	}
	
	public void addCompositeToPhysicsEngineStatic( CollisionEngine engine ){ 
		if ( this.engineSlot == null){
			this.engineSlot = engine.addStaticCollidableToEngineList( this );
			this.engine = engine;
			System.out.println("|  "+this+" adding static to collision engine");
		}
		if ( !this.isActive ){
			this.deactivateCollider();
		}
	}
	
	public void addCompositeToPhysicsEngineDynamic( CollisionEngine engine ){
		if ( this.engineSlot == null ){
			System.out.println("|   Adding dynamic to collision engine ");
			this.engineSlot = engine.addDynamicCollidableToEngineList( this );
			this.engine = engine;
		}
		if( !this.isActive ){
			this.deactivateCollider();
		}
	}
	
	public void addCompositeToPhysicsEngineStatic( CollisionEngine engine, String group ){ 
		if ( this.engineSlot == null ){
			
			System.out.println("|   "+this+" adding static to collision engine GROUP {"+group+"}");
			this.engineSlot = engine.addStaticCollidable( this , group );
			this.engine = engine;
		}
		else{
			System.err.println("|   Entity is already registered");
		}
		if( !this.isActive ){
			this.deactivateCollider();
		}
	}
	
	public void addCompositeToPhysicsEngineDynamic( CollisionEngine engine, String group ){
		
		if ( this.engineSlot == null ){
			
			System.out.println("|   Adding dynamic to collision engine GROUP {"+group+"}");
			this.engineSlot = engine.addDynamicCollidable( this, group );
			this.engine = engine;
		}
		else{
			System.out.println("|   TODO: Entity was added after");
		}
		if( !this.isActive ){
			this.deactivateCollider();
		}
	}

	public boolean isActive(){
		return this.isActive;
	}
	
	/**Stops this Collider from being checked by the collision engine. 
	 * Special Cases: When deactivating Colliders from inside custom Collision classes, this will set the custom Collision's isComplete 
	 * value to true. Make sure this method is called after any conditional checks affecting isComplete.
	 */
	public void deactivateCollider(){
		this.isActive = false;
		if ( this.engineSlot != null ){
			this.engineSlot.notifyDeactivatedCollider();
			this.dropAllCollisions();

			System.out.println("Deactivating Collider");
		}
		else{
			System.out.println("Deactivate event on collider that was not added");
		}
		


	}
	
	public void activateCollider(){
		this.isActive = true;
		if ( this.engineSlot != null ){
			this.engineSlot.notifyActivatedCollider();
		System.out.println("Activating Collider");
		}
		else{
			System.out.println("Activate event on collider that was not added");
		}
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public void disableComposite(){
		
		System.out.println("Disabling Collider of ["+this.ownerEntity+"]");
		
		dropAllCollisions();
		
		this.engineSlot.notifyRemovedCollider();

		this.engineSlot = null;
		this.ownerEntity.nullifyColliderComposite();
		
	}
	
	protected void dropAllCollisions(){
		for ( CollidingPair pair : this.collisionInteractions ){
			pair.collision().dropCollision();
		}
	}
	
	protected void changeColliderToStaticInEngine(){
		
		this.engineSlot.notifyChangeToStatic();
		
		for( CollidingPair pair : collisionInteractions ){	//Drop any running collisions
			pair.collision().dropCollision();
		}
	}
	
	protected void changeColliderToDynamicInEngine(){
		ActiveCollider dynamic = this.engineSlot.notifyChangeToDynamic();
		this.engineSlot = dynamic;
	}
	
	@Override
	public String toString() {
		return this.compositeName;
	}

	
	/**Takes an input point relative to this collider and returns the absolute position of that point in the world.
	 * For example: an input of (0,1) relative to a collider located at (1,1) will return (0+1,1+1) or (1,2). Is has been translated.
	 * An input of (0,1) relative to a collider rotated at 45 degrees will return ( sqrt(2),-sqrt(2) ). It has been rotated. Etc.
	 * @param The relative point p
	 * @return The point's absolute position in the world.
	 */
	public Point2D absolutePositionOfRelativePoint( Point2D p ){ 
		return this.ownerEntity.getTranslationalAbsolutePositionOf(p);
	}

	
	public static class Ultralight extends Collider{
		
		private final int radius;
		
		
		
		public Ultralight( EntityStatic owner, int radius ) {
			
			this.ownerEntity = owner;
			this.radius = radius;
			
			this.boundary = new Boundary(){

				@Override
				public <B extends Boundary> void rotateBoundaryFromTemplate(Point center, double angle, B template) {}

				@Override
				protected Line2D[] getSeparatingSides() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Point getRelativeOffset() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void debugDrawBoundary(MovingCamera cam, Graphics2D g2, EntityStatic ownerEntity) {
					// TODO Auto-generated method stub
					
				}

				@Override
				protected Point2D[] getOuterPointsPair(Line2D axis) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				protected Point2D farthestPointFromPoint(Point2D boundaryPoint, Line2D axis) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				protected Point2D farthestLocalPointFromPoint(Point primaryOrigin, Point2D localPoint, Line2D axis) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public BoundaryFeature[] farthestFeatureFromPoint(Point primaryOrigin, Point secondaryOrigin,
						Point2D p2, Line2D axis) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public BoundaryVertex[] getCornersVertex() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Point2D[] getCornersPoint() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Point2D[] getLocalCornersPoint(Point localEntityPosition) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public <T> Boundary temporaryClone() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void scaleBoundary(double scaleFactor) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void scaleBoundary(double scaleFactor, Point center) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void constructVoronoiRegions() {}

				@Override
				public byte getTypeCode() {
					return Boundary.ULTRALIGHT;
				}

				@Override
				public Polygon getLocalPolygonBounds() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Polygon getPolygonBounds(EntityStatic owner) {
					// TODO Auto-generated method stub
					return null;
				}
				
			};
		}
		
		@Override
		public Boundary getBoundary() {

			return this.boundary;
		}
		
	}
	

	
}

