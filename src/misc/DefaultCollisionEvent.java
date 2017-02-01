package misc;

import entities.EntityStatic;
import entityComposites.Collidable;
import physics.BoundaryFeature;
import physics.Collision;

public class DefaultCollisionEvent extends CollisionEvent{
	
	@Override
	public void run( BoundaryFeature source , BoundaryFeature collidingWith) {
		
	}
	
	@Override
	public String toString() {
		return "defaultEvent";
	}

}
