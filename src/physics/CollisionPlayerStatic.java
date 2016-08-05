package physics;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import entities.EntityDynamic;
import entities.EntityStatic;

public class CollisionPlayerStatic extends Collision {
	
	private EntityDynamic entityPrimary;
	private EntityStatic entitySecondary;
	
	private boolean xequilibrium = false;
	private boolean yequilibrium = false;
	
	public CollisionPlayerStatic(EntityDynamic entity1, EntityStatic entity2){
		
		super(entity1, entity2);
		
		entityPrimary = entity1;
		entitySecondary = entity2;
		collisionName = entity1.name + " + " + entity2.name;
		
		//GENERIC COLLISION
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		
		entityPrimary.setDY(0);
		
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	@Override
	public void updateCollision(){ 
		
		Line2D[] flushSides = entityPrimary.getLocalBoundary().getFlushSides(entitySecondary.getLocalBoundary());
		
		if ( flushSides != null ){
			
			contactingSide1 = flushSides[0];
			contactingSide2 = flushSides[1];
	
			//MATH TO CALCULATE CONTACT POINTS. TEMPORARY
			
			contactPoints[0]=null; contactPoints[1]=null;

			if ( pointIsOnSegment(contactingSide1.getP1(), contactingSide2) ) {
				if (contactPoints[0]==null)  { contactPoints[0] = contactingSide1.getP1(); } 
				else  { contactPoints[1] = contactingSide1.getP1(); }
			}
			if ( pointIsOnSegment(contactingSide1.getP2(), contactingSide2) ) {
				if (contactPoints[0]==null)  { contactPoints[0] = contactingSide1.getP2(); } 
				else  { contactPoints[1] = contactingSide1.getP2(); }
			}
			if ( pointIsOnSegment(contactingSide2.getP1(), contactingSide1) ) {
				if (contactPoints[0]==null)  { contactPoints[0] = contactingSide2.getP1(); } 
				else  { contactPoints[1] = contactingSide2.getP1(); }
			}
			if ( pointIsOnSegment(contactingSide2.getP2(), contactingSide1) ) {
				if (contactPoints[0]==null)  { contactPoints[0] = contactingSide2.getP2(); } 
				else  { contactPoints[1] = contactingSide2.getP2(); }
			}
			//
			
			yequilibrium = true;
			entityPrimary.setDY(0);
			entityPrimary.setAccY(0);
			entityPrimary.setColliding(true);
			
			if (entityPrimary.getDX() > (0.1))
	    	{
				entityPrimary.setAccX(-0.1f);
	    	}
	    	else if (entityPrimary.getDX() < (-0.1))
	    	{
	    		entityPrimary.setAccX(0.1f);
	    	}
	    	else
	    	{
	    		//entityPrimary.setDX(entitySecondary.ge;
	    		entityPrimary.setDX(0);
	    		entityPrimary.setAccX(0);
	    	}
			
			
		}
		
		if ( entityPrimary.getLocalBoundary().boundaryIntersects(entitySecondary.getLocalBoundary() ) ) {
			yequilibrium = false; 
			entityPrimary.setDY(0);
			entityPrimary.setY(entityPrimary.getY() - 1 );	
			
		}
		
	}
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		entityPrimary.setColliding(false); // unset entity collision flag. 
		entityPrimary.setAccY(0.1f); //turn gravity back on
		entityPrimary.setAccX(0); //remove friction
	}
	
	public String toString(){
		return String.format("%s",collisionName + " " + yequilibrium);
	}
	
	public boolean sideIsAllignedX(Rectangle box1, Rectangle box2){
		if ( box1.getMinX() > box2.getMaxX() - 2 || box1.getMaxX() < box2.getMinX() + 2){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean sideIsAllignedY(Rectangle box1, Rectangle box2){
		if ( box1.getMinY() > box2.getMaxY() - 2 || box1.getMaxY() < box2.getMinY() + 2 ){
			return false;
		}
		else {
			return true;
		}
	}
	
	

}
