package misc;

import physics.BoundaryFeature;
import physics.Vector;

public abstract class CollisionEvent {
	
	//protected Collidable owner;
	
	public abstract void run( BoundaryFeature source, BoundaryFeature collidingWith, Vector normal );
	
	@Override
	public String toString(){
		return "Unnamed Event";
	}
	
}
