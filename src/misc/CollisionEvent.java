package misc;

import physics.BoundaryFeature;

public abstract class CollisionEvent {
	
	//protected Collidable owner;
	
	public abstract void run( BoundaryFeature source, BoundaryFeature collidingWith);
	
	@Override
	public String toString(){
		return "Unnamed Event";
	}
	
}
