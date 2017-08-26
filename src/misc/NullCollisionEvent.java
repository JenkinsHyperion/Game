package misc;

import physics.BoundaryFeature;
import physics.Vector;

public class NullCollisionEvent extends CollisionEvent {

	@Override
	public void run( BoundaryFeature source , BoundaryFeature collidingWith, Vector separation ) {
		System.out.println("Null Collision");
	}
	
}
