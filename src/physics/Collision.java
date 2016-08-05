package physics;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import entities.EntityDynamic;
import entities.EntityStatic;

public class Collision {
	
	protected EntityDynamic entityPrimary;
	protected EntityStatic entitySecondary;
	public String collisionName;
	
	protected Line2D contactingSide1;
	protected Line2D contactingSide2;
	
	protected Point2D[] contactPoints = new Point2D[2];
	
	public Collision(EntityDynamic e1, EntityStatic e2){
		
		entityPrimary = e1;
		entitySecondary = e2;
		collisionName = e1.name + " + " + e2.name;
		
		//THIS TEST COLLISION IS A NORMAL SURFACE SUCH AS A FLAT PLATFORM
		
		//initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	public void initCollision(){
		
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	public void updateCollision(){ 
		   
	}
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	public void completeCollision(){
		
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

		if (entityPrimary.getLocalBoundary().boundaryIntersects( entitySecondary.getLocalBoundary() ) ) {
			return false;
		}
		else if (entityPrimary.getLocalBoundary().sideIsFlush( entitySecondary.getLocalBoundary() ) ) {
			return false;
		}
		else { // entities are no longer colliding
			completeCollision(); // run final commands
			return true; // return true for scanning loop in Board to delete this collision
		}
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
	
	public String toString(){
		//return String.format("%s",this);
		return collisionName;
	}
	
	public Line2D getSidePrimary(){ return contactingSide1; }
	public Line2D getSideSecondary(){ return contactingSide2; }
	public Point2D[] getContactPoints(){ return contactPoints; }
}
