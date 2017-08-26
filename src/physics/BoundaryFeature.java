package physics;

import java.awt.geom.Point2D;

import entityComposites.EntityStatic;
import misc.CollisionEvent;
import misc.DefaultCollisionEvent;

abstract public class BoundaryFeature {
	
	protected Boundary owner;
	protected int ID;
	protected CollisionEvent collisionEvent;

	public BoundaryFeature(){
		System.err.println( " CREATING NEW BOUNDARY FEATURE " );
		this.collisionEvent = new DefaultCollisionEvent();
	}
	
	public BoundaryFeature( CollisionEvent event){
		this.collisionEvent = event;
	}
	
	public void setCollisionEvent( CollisionEvent event){
		collisionEvent = event;
	}
	
	protected abstract CollisionEvent getEvent();
	
	protected void onCollision(){}
	
	public String toString(){ return "Null Boudnary Feature"; } //check for further abstraction
	
	public void collisionTrigger(){}
	
	public abstract Vector getNormal();
	
	public abstract Point2D getP1();
	public abstract Point2D getP2();
	
	public abstract boolean debugIsVertex();
	public abstract boolean debugIsSide();
	
}
