package physics;

import java.awt.geom.Point2D;

import misc.CollisionEvent;

public class BoundaryGenericFeature extends BoundaryFeature{
	
	@Override
	protected CollisionEvent getEvent(){
		return null;
	}
	
	@Override
	public Point2D getP1() {
		System.err.println("getP1() on GenericFeature");
		return null;
	}

	@Override
	public Point2D getP2() {
		System.err.println("get P2() on GenericFeature");
		return null;
	}

	@Override
	public boolean debugIsVertex() {
		return false;
	}

	@Override
	public boolean debugIsSide() {
		return false;
	}

	@Override
	public Vector getNormal() {
		System.err.println("Attempted to get Normal Vector on BoundaryGeneric");
		return null;
	}
	
}
