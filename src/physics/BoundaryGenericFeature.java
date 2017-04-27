package physics;

import java.awt.geom.Point2D;

public class BoundaryGenericFeature extends BoundaryFeature{
	
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
	
}
