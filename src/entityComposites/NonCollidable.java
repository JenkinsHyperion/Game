package entityComposites;

import java.awt.Graphics2D;

import engine.Camera;
import physics.CollisionCheck;
import physics.CollisionEngine;

public final class NonCollidable extends CollisionProperty{
	
	private final static NonCollidable nullCollision = new NonCollidable();

	private NonCollidable(){
		
	}
	
	public static NonCollidable getNonCollidable(){ 
		return nullCollision;
	}
	
	//check and pass are both points of termination in the event of a collision check on a non collidable entity 
	@Override
	public void checkForInteractionWith(CollisionProperty entity , CollisionCheck checkType ,CollisionEngine engine){
		//System.out.println("WARNING: Interactions were checked on a non-colliding entity");
		//MOVE THAT ENTITY TO NONCOLLIDING ARRAY AUTOMATICALLY
	}
	@Override
	public void passInteraction( Collider entity , CollisionCheck checkType, CollisionEngine engine ){
		//System.out.println("WARNING: Interactions were checked on "+entity.owner.name+", a non-colliding entity");
		//MOVE THAT ENTITY TO NONCOLLIDING ARRAY AUTOMATICALLY
	}
	
	@Override
	public void debugDrawBoundary(Camera camera , Graphics2D g){}
	
	
	
}
