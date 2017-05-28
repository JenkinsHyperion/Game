package physics;

import java.awt.geom.Point2D;

import entityComposites.EntityStatic;
import misc.CollisionEvent;
import misc.DefaultCollisionEvent;

abstract public class BoundaryFeature {
	
	protected Boundary owner;
	protected int ID;
	private CollisionEvent collisionEvent = new DefaultCollisionEvent();

	
	public void setCollisionEvent( CollisionEvent event){
		this.collisionEvent = event;
	}
	
	protected CollisionEvent getEvent(){
		return this.collisionEvent;
	}
	
	protected void onCollision(){}
	
	public String toString(){ return "Null Boudnary Feature"; } //check for further abstraction
	
	public void collisionTrigger(){}
	
	public abstract Vector getNormal();
	
	public abstract Point2D getP1();
	public abstract Point2D getP2();
	
	public abstract boolean debugIsVertex();
	public abstract boolean debugIsSide();
	
}
