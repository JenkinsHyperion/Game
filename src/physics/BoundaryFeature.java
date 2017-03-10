package physics;

import java.awt.geom.Point2D;

import entities.EntityStatic;
import misc.CollisionEvent;
import misc.DefaultCollisionEvent;

abstract public class BoundaryFeature {
	
	protected Boundary owner;
	protected int ID;
	private CollisionEvent collisionEvent;

	
	public void setCollisionEvent( CollisionEvent event){
		this.collisionEvent = event;
	}
	
	protected CollisionEvent getEvent(){
		return this.collisionEvent;
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
	
	public abstract Point2D getP1();
	public abstract Point2D getP2();
	
	public abstract boolean debugIsVertex();
	public abstract boolean debugIsSide();
	
}
