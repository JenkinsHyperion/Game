package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import entities.EntityDynamic;
import entities.EntityStatic;

public class CollisionPositioning extends Collision { //TO BE MOVED TO ITS OWN INTERFACE
	
	private Point[] intersections = new Point[0];
	
	public CollisionPositioning(EntityDynamic entity1, EntityStatic entity2){
		
		super(entity1,entity2);
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		entityPrimary.setColliding(true); // set entity collision flag for entity events. Possibly useless
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	@Override
	public void updateCollision(){ 
		
		//OPTIMIZATION for tracers, modify the Line2D.intersects() method to trace only as far as the closest point and ignore any
		//further points
		
		intersections = new Point[entityPrimary.getBoundary().getIntersectingSides(entitySecondary.getBoundaryLocal()).length];

			//System.out.println(intersections.length);
			for (int i = 0 ; i < intersections.length ; i++){
				Line2D[] pair = entityPrimary.getBoundary().getIntersectingSides(entitySecondary.getBoundaryLocal())[i];
				
				intersections[i] = new Point(
						(int)entityPrimary.getBoundary().getIntersectionPoint(pair[1], pair[0]).getX() ,
						(int)entityPrimary.getBoundary().getIntersectionPoint(pair[1], pair[0]).getY()
					);
			}

			
		
	}
	
	public Point[] getIntersectionPoints(){
		return intersections;
	}
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		entityPrimary.setColliding(false); // unset entity collision flag. 
		entityPrimary.removeCollision( entityPairIndex[0] );
		entitySecondary.removeCollision(entityPairIndex[1] );
	}
	
	@Override
	public boolean isComplete(){ // Check if entities are no longer colliding
		
		if ( entityPrimary.getBoundary().boundaryIntersects(entitySecondary.getBoundaryLocal()) )
			return false;
		else {
			completeCollision();
			return true;
		}
	}
	
	public String toString(){
		return String.format("%s",collisionName);
	}
	
	public Point getClosestIntersection(){ //OPTIMIZE see above in updateCOllision()
		Point closest = new Point();
		if (intersections.length != 0)
			closest = intersections[0];
		for(Point intersect : intersections){
			if ( entityPrimary.getPos().distance(intersect) < entityPrimary.getPos().distance(closest) ){
				closest = intersect;
			}
		}
		return closest;
	}

}
