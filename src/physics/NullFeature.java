package physics;

import java.awt.geom.Point2D;

import misc.CollisionEvent;

public class NullFeature extends BoundaryFeature{
	
	final NullFeature nullFeature= new NullFeature();
	
	private NullFeature(){
		
	}

	@Override
	public Point2D getP1() {
		return null;
	}

	@Override
	public Point2D getP2() {
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
		System.err.println("Attempted normal on Null Feature");
		return null;
	}
	
	@Override
	protected CollisionEvent getEvent() {
		return this.collisionEvent;
	}
	
}
