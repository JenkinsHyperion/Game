package misc;

import physics.BoundaryFeature;
import physics.Vector;

public class DefaultCollisionEvent extends CollisionEvent{
	
	@Override
	public void run( BoundaryFeature source , BoundaryFeature collidingWith, Vector separation) {
		
	}
	
	@Override
	public String toString() {
		return "defaultEvent";
	}

}
