package physics;

import java.awt.geom.Point2D;

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
	
}
