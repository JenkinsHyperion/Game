package physics;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import engine.MovingCamera;
import entities.*;
import entityComposites.*;
import testEntities.PlantPlayer;

public abstract class Collision {
	
	protected ResolutionState resolutionState;

	
	protected Collider collidingPrimary ;
	protected Collider collidingSecondary; 
	
	public String collisionDebugTag = "-";
	
	protected int[] entityPairIndex = new int[2];
	
	protected BoundarySide contactingSide1;
	protected BoundarySide contactingSide2;
	
	protected Point2D contactPoint;
	
	protected double depthX = 0;
	protected double depthY = 0;
	
	protected Point2D[] contactPoints = new Point2D[2];
	protected ArrayList<Point2D> debugIntersectionPoints = new ArrayList<>();
	
	protected boolean isComplete = false;
	
	public static abstract class DefaultType extends Collision{
		
		protected EntityStatic entityPrimary;
		protected EntityStatic entitySecondary;
		
		public DefaultType(Collider e1, Collider e2 ){
			
			entityPrimary = e1.getOwnerEntity();
			entitySecondary = e2.getOwnerEntity();
			
			collidingPrimary = e1; 
			collidingSecondary = e2;
			
			collisionDebugTag = entityPrimary.name + " + " + entitySecondary.name;
			
			//THIS TEST COLLISION IS A NORMAL SURFACE SUCH AS A FLAT PLATFORM
			entityPairIndex[0] = collidingPrimary.addCollision(this,true); 
			entityPairIndex[1] = collidingSecondary.addCollision(this,false); 
			//initCollision();
		}
		
		public EntityStatic getEntityInvolved(boolean pairID){
			if (pairID)
				return entityPrimary;
			else
				return entitySecondary;
		}
		
		public boolean isActive(EntityStatic entity1, EntityStatic entity2){
			if (entity1 == entityPrimary){
				if (entity2 == entitySecondary){
					return true;
				}
				else {
					return false;
				}
			}
			else if (entity1 == entitySecondary){
				if (entity2 == entityPrimary){
					return true;
				}
				else {
					return false;
				}
			}
			else{
				return false;
			}
		}
		
	}	
	
	
	protected class Resolution{ //Wrapper class for clipping resolution vector and involved features of entities involved
		
		private BoundaryFeature closestFeaturePrimary;
		private BoundaryFeature closestFeatureSecondary;
		private Vector resolutionVector;
		private Vector axisVector;
		
		protected Resolution( BoundaryFeature featurePrimary , BoundaryFeature featureSecondary  , Vector resolution , Vector axis){
			this.closestFeaturePrimary = featurePrimary;
			this.closestFeatureSecondary = featureSecondary;
			this.resolutionVector = resolution;
			this.axisVector = axis;
		}
		
		protected Vector getClippingVector(){ return resolutionVector; }
		protected Vector getSeparationVector(){ return axisVector; }
		
		protected BoundaryFeature FeaturePrimary(){ return closestFeaturePrimary; }
		protected BoundaryFeature FeatureSecondary(){ return closestFeatureSecondary; }
		
	}
	
	protected void triggerResolutionEvent(){
		
	}

	/**Runs any initialization of variables and registration of forces in the involved entities.
	 * <p>
	 * NOTE ON VARIABLES: When defining an anonymous Collision within an anonymous CollisionDispatcher inside a special entity,
	 * the CollisionDispatcher must be static, and both CollisionDispatcher and Collision must be type bounded.
	 * Then, instances of special entity, are received through the argument of CollisionDispatcher.createVisualCollision(). 
	 * And since the CollisionDispatcher is a static inner class of special entity, 
	 * all special entity's fields, including private ones, are accessible. See {@link PlantPlayer.FruitInRange}
	 * <p>
	 * REMEMEBR to unregister all forces in this Collision's {@link #internalCompleteCollision()} method. 
	 * 
	 */
	protected abstract void internalInitializeCollision();
	
	/**Runs any collision calculations every frame. REMEMEBER to include at least one boolean check on the Collision.isComplete field
	 * to check for completion conditions. The most basic form of this is isComplete = CollsiionCheck.check(), where Collision check
	 * was passed in through the CollisionDispatcher's constructor.
	 */
	protected abstract void updateCollision();
	
	public abstract void updateVisualCollision(MovingCamera camera, Graphics2D gOverlay);

	
	public abstract void internalCompleteCollision();
	
	public void notifyEntitiesOfCollisionCompleteion(){
		//Remove collision from involved entities lists
		collidingPrimary.removeCollision( entityPairIndex[0] );
		collidingSecondary.removeCollision(entityPairIndex[1] );
	}
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	
	public void indexShift( boolean pairIndex ){
		if (!pairIndex)
			entityPairIndex[ 1 ] = entityPairIndex[ 1 ] - 1;
		else
			entityPairIndex[ 0 ] = entityPairIndex[ 0 ] - 1;
	}
	
	public abstract EntityStatic getEntityInvolved(boolean pairID);
	
	//INTERNAL METHODS - DON'T ALTER BELOW THIS
	
	/*public boolean isComplete(){ // Check if entities are no longer colliding

		if (entityPrimary.getLocalBoundary().boundaryIntersects(entitySecondary.getLocalBoundary()) ){
			return false;
		}
		else { // entities are no longer colliding
			completeCollision(); // run final commands
			return true; // return true for scanning loop in Board to delete this collision
		}
	}*/
	public void dropCollision(){
		this.isComplete = true;
	}
	
	protected boolean isComplete(){
		return isComplete;
	}
	
	
	//When Board detects collision, check to see if it's already in the list of active collisions
	public abstract boolean isActive(EntityStatic entity1, EntityStatic entity2);
	
	protected boolean pointIsOnSegment(Point2D p, Line2D seg) {
		if ( seg.ptSegDist(p) > 0.5 ) {
			if ( seg.ptSegDist(p) < 1.5 ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isContacting(){
		if (contactPoints[1] == null || contactPoints[0] == null){
			return false;
		} else {
			if ( contactPoints[0].distance(contactPoints[1]) > 2 ) {
				return true;
			} else {
				return false;
			}
		}
		
	}
	
	public double getContactDist(){
		if ( contactPoints[1]!=null && contactPoints[1]!=null ) {
		return contactPoints[0].distance(contactPoints[1]) ;
		} else {
			return 0;
		}
			
	}
	
	public String toString(){
		//return String.format("%s",this);
		return collisionDebugTag;
	}
	
	public BoundarySide getSidePrimary(){ return contactingSide1; }
	public BoundarySide getSideSecondary(){ return contactingSide2; }
	public Point2D[] getContactPoints(){ return contactPoints; }
	public ArrayList<Point2D> getIntersections() { return debugIntersectionPoints; }

	protected void triggerResolutionEvent(Resolution closest) {
				
	}
	
	
	
	public static class BasicCheck extends DefaultType implements VisualCollision{

		private VisualCollisionCheck check;
		private boolean isComplete = false;
		
		public BasicCheck(Collider e1, Collider e2, VisualCollisionCheck check) {
			super( e1 , e2 );
			this.check = check;
			internalInitializeCollision();
		}
		
		@Override
		public void internalInitializeCollision() {
			System.out.println("COLLISION START BASIC");
			this.collidingPrimary.onCollisionEvent();
			this.collidingSecondary.onCollisionEvent();
		}
		
		@Override
		public void updateCollision() {
			
			if ( !check.check(collidingPrimary, collidingSecondary) ){
				isComplete = true;
			}
		}
		
		@Override
		public void updateVisualCollision(MovingCamera camera, Graphics2D g2) {
			
			if ( !check.check(collidingPrimary, collidingSecondary, camera, g2) ){
				isComplete = true;
			}
		}

		@Override
		public void internalCompleteCollision() {
			this.collidingPrimary.onLeavingCollisionEvent();
			this.collidingSecondary.onLeavingCollisionEvent();
		}

		@Override
		public boolean isComplete() {
			return isComplete;
		}
		
	}
	
	
	public static abstract class CustomType < E1 extends EntityStatic, E2 extends EntityStatic > extends Collision implements VisualCollision{

		protected E1 entityPrimary;
		protected E2 entitySecondary;
		
		public CustomType( E1 e1 ,Collider collider1,  E2 e2, Collider collider2 ){
			
			entityPrimary = e1;
			entitySecondary = e2;
			
			collidingPrimary = collider1; 
			collidingSecondary = collider2;
			
			//collisionDebugTag = entityPrimary.name + " + " + entitySecondary.name;
			
			//THIS TEST COLLISION IS A NORMAL SURFACE SUCH AS A FLAT PLATFORM
			entityPairIndex[0] = collidingPrimary.addCollision(this,true); 
			entityPairIndex[1] = collidingSecondary.addCollision(this,false); 
			//initCollision();
		}
		
		public EntityStatic getEntityInvolved(boolean pairID){
			if (pairID)
				return entityPrimary;
			else
				return entitySecondary;
		}
		
		public boolean isActive(EntityStatic entity1, EntityStatic entity2){
			if (entity1 == entityPrimary){
				if (entity2 == entitySecondary){
					return true;
				}
				else {
					return false;
				}
			}
			else if (entity1 == entitySecondary){
				if (entity2 == entityPrimary){
					return true;
				}
				else {
					return false;
				}
			}
			else{
				return false;
			}
		}

		@Override
		public String toString() {
			return "Unnamed Custom Collision";
		}
		
	}
	
	public static class Ultralight extends Collision{

		@Override
		protected void internalInitializeCollision() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void updateCollision() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void updateVisualCollision(MovingCamera camera, Graphics2D gOverlay) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void internalCompleteCollision() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public EntityStatic getEntityInvolved(boolean pairID) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isActive(EntityStatic entity1, EntityStatic entity2) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

}
