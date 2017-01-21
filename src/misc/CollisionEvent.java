package misc;

import entities.EntityStatic;
import entityComposites.Collidable;
import physics.BoundaryFeature;
import physics.Collision;

public abstract class CollisionEvent {
	
	//protected Collidable owner;
	
	public abstract void run( BoundaryFeature source, BoundaryFeature collidingWith);
	
	@Override
	public String toString(){
		return "Unnamed Event";
	}
	
}
