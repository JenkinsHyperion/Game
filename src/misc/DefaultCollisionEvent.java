package misc;

import physics.BoundaryFeature;

public class DefaultCollisionEvent extends CollisionEvent{
	
	@Override
	public void run( BoundaryFeature source , BoundaryFeature collidingWith) {
		
	}
	
	@Override
	public String toString() {
		return "defaultEvent";
	}

}
