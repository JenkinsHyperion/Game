package physics;

import java.awt.Point;
import entities.*;
import entityComposites.*;

public class CollisionPositioning extends Collision { //TO BE MOVED TO ITS OWN INTERFACE
	
	private Collider collisionPrimary;
	private Collider collisionSecondary;
	
	private Point[] intersections = new Point[0];
	
	public CollisionPositioning(EntityDynamic entity1, EntityStatic entity2 ,  CollisionEngine ownerEngine){
		
		super(entity1,entity2, ownerEngine);
		
		collisionPrimary = entity1.getColliderComposite(); // TAKE COLLIDABLE IN COSNTRUCTOR INSTEAD OF ENTITY
		collisionSecondary = entity2.getColliderComposite();
		
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
		
		intersections = new Point[entityPrimary.getBoundary().getIntersectingSides(collidingSecondary.getBoundaryLocal()).length];

			//System.out.println(intersections.length);
			for (int i = 0 ; i < intersections.length ; i++){
				Side[] pair = entityPrimary.getBoundary().getIntersectingSides(collidingSecondary.getBoundaryLocal())[i];
				
				intersections[i] = new Point(
						(int)entityPrimary.getBoundary().getIntersectionPoint(pair[1].toLine() , pair[0].toLine() ).getX() ,
						(int)entityPrimary.getBoundary().getIntersectionPoint(pair[1].toLine() , pair[0].toLine() ).getY()
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
		collisionPrimary.removeCollision( entityPairIndex[0] );
		collisionSecondary.removeCollision(entityPairIndex[1] );
	}
	
	@Override
	public boolean isComplete(){ // Check if entities are no longer colliding
		
		if ( entityPrimary.getBoundary().boundaryIntersects(collidingSecondary.getBoundaryLocal()) )
			return false;
		else {
			completeCollision();
			return true;
		}
	}
	
	public String toString(){
		return String.format("%s",collisionDebugTag);
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
