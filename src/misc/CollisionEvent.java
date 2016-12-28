package misc;

import entities.EntityStatic;
import entityComposites.Collidable;

public abstract class CollisionEvent {
	
	protected Collidable owner;
	protected String name;

	public abstract void run();
	
	@Override
	public String toString(){
		return name;
	}
	
}
