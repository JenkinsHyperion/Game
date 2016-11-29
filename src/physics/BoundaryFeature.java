package physics;

import entities.EntityStatic;

abstract public class BoundaryFeature {
	
	protected Boundary owner;
	
	protected int ID;

	protected void onCollision(){}
	
	public String toString(){ return "null"; }

	public EntityStatic getOwnerEntity() {
		return null;
	}

	public Boundary getOwnerBoundary() {
		return null;
	}
	
}
