package physics;

import entities.EntityStatic;
import misc.CollisionEvent;
import misc.DefaultCollisionEvent;

abstract public class BoundaryFeature {
	
	protected Boundary owner;
	protected int ID;
	protected CollisionEvent collisionEvent;

	
	protected void setCollisionEvent( CollisionEvent event){
		this.collisionEvent = event;
	}
	
	protected void onCollision(){}
	
	public String toString(){ return "null"; } //check for further abstraction

	public EntityStatic getOwnerEntity() {
		return null;
	}

	public Boundary getOwnerBoundary() {
		return null;
	}
	
	public void collisionTrigger(){}
	
}
