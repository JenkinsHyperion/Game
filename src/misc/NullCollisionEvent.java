package misc;

import physics.BoundaryFeature;

public class NullCollisionEvent extends CollisionEvent {

	@Override
	public void run( BoundaryFeature source , BoundaryFeature collidingWith ) {
		System.out.println("Null Collision");
	}
	
}
