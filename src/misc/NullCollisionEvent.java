package misc;

import java.io.Console;

import entityComposites.Collidable;
import physics.BoundaryFeature;
import physics.Collision;

public class NullCollisionEvent extends CollisionEvent {

	@Override
	public void run( BoundaryFeature source , BoundaryFeature collidingWith ) {
		System.out.println("Leaving Collision");
	}
	
}
