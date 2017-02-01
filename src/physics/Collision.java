package physics;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import entities.*;
import entityComposites.*;
import physics.Collision.Resolution;

public class Collision implements Serializable{
	
	protected boolean isComplete = false;
	
	protected ResolutionState resolutionState;
	
	protected EntityDynamic entityPrimary;
	protected EntityStatic entitySecondary;
	
	protected Collidable collidingPrimary ;
	protected Collidable collidingSecondary; 
	
	public String collisionDebugTag = "-";
	
	protected int[] entityPairIndex = new int[2];
	
	protected Side contactingSide1;
	protected Side contactingSide2;
	
	protected double depthX = 0;
	protected double depthY = 0;
	
	protected Point2D[] contactPoints = new Point2D[2];
	protected ArrayList<Point2D> debugIntersectionPoints = new ArrayList<>();
	
	public Collision(EntityDynamic e1, EntityStatic e2){
		
		entityPrimary = e1;
		entitySecondary = e2;
		
		collidingPrimary = (Collidable) e1.getCollisionType(); //TRACE ALL CASTS BACK TO PASSING COLLIDABLE IN CONSTRUCTOR
		collidingSecondary = (Collidable) e2.getCollisionType();
		
		collisionDebugTag = e1.name + " + " + e2.name;
		
		//THIS TEST COLLISION IS A NORMAL SURFACE SUCH AS A FLAT PLATFORM
		entityPairIndex[0] = ((Collidable) e1.getCollisionType()).addCollision(this,true); 
		entityPairIndex[1] = ((Collidable) e2.getCollisionType()).addCollision(this,false); 
		//initCollision();
	}
	
	protected class Resolution{ //Wrapper class for clipping resolution vector and involved features of entities involved
		
		private BoundaryFeature closestFeaturePrimary;
		private BoundaryFeature closestFeatureSecondary;
		private Vector resolutionVector;
		private Vector distanceVector;
		
		protected Resolution( BoundaryFeature featurePrimary , BoundaryFeature featureSecondary  , Vector resolution , Vector distance){
			this.closestFeaturePrimary = featurePrimary;
			this.closestFeatureSecondary = featureSecondary;
			this.resolutionVector = resolution;
			this.distanceVector = distance;
		}
		
		protected Vector getClippingVector(){ return resolutionVector; }
		protected Vector getDistanceVector(){ return distanceVector; }
		
		protected BoundaryFeature FeaturePrimary(){ return closestFeaturePrimary; }
		protected BoundaryFeature FeatureSecondary(){ return closestFeatureSecondary; }
		
	}
	
	protected void triggerResolutionEvent(){
		
	}

	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	public void initCollision(){
		
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	public void updateCollision(){ 
		   
	}
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	public void completeCollision(){
		//((Collidable) entityPrimary.getCollisionProperty()).removeCollision( entityPairIndex[0] );
		//((Collidable) entitySecondary.getCollisionProperty()).removeCollision( entityPairIndex[1] );
		
		
		
	}
	
	public void indexShift( boolean pairIndex ){
		if (!pairIndex)
			entityPairIndex[ 1 ] = entityPairIndex[ 1 ] - 1;
		else
			entityPairIndex[ 0 ] = entityPairIndex[ 0 ] - 1;
	}
	
	public EntityStatic getEntityInvolved(boolean pairID){
		if (pairID)
			return entityPrimary;
		else
			return entitySecondary;
	}
	
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
	
	
	public boolean isComplete(){ // Check if entities are no longer colliding
		
		/*if (entityPrimary.getBoundary().boundaryIntersects( entitySecondary.getBoundaryLocal() ) ) {
			return false;
		}
		else { // entities are no longer colliding
			completeCollision(); // run final commands
			return true; // return true for scanning loop in Board to delete this collision
		}*/
		return false;
	}
	
	
	//When Board detects collision, check to see if it's already in the list of active collisions
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
	
	public Side getSidePrimary(){ return contactingSide1; }
	public Side getSideSecondary(){ return contactingSide2; }
	public Point2D[] getContactPoints(){ return contactPoints; }
	public ArrayList<Point2D> getIntersections() { return debugIntersectionPoints; }

	protected void triggerResolutionEvent(Resolution closest) {
				
	}
}
