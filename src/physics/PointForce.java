package physics;

import java.awt.Point;

public class PointForce extends Force{

	private Point point;
	
	public PointForce(double x, double y, Point point, int ID) {
		super(x, y, ID);
		this.point = point;
	}
	
	public PointForce( Vector vector, Point point, int ID) {
		super(vector.getX(), vector.getY() , ID);
		this.point = point;
	}
	
	public void setPoint( Point point){
		this.point = point;
	}
	
	public double getTorque() {
		
		Vector radius = new Vector( point.getX() , point.getY() );
		
		double DA = radius.crossProduct( this.force );
		
		return DA;

	}

}
