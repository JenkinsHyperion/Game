package misc;

import entityComposites.Collider;
import physics.BoundaryFeature;
import physics.Vector;

public class NullCollisionEvent extends CollisionEvent {

	@Override
	public void run( Collider partner , BoundaryFeature source, BoundaryFeature collidingWith, Vector separation ) {
		System.out.println("Null Collision");
	}
	
}
