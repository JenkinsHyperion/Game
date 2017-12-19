package utility;

import java.awt.Point;
import java.awt.geom.Point2D;

public final class UtilityMath {

	public static Point2D shiftPoint( Point2D p , Point2D shift ){
		
		Point2D returnPoint = new Point.Double( p.getX() + shift.getX() , p.getY() + shift.getY() );
		return returnPoint;
	}
	public static Point2D.Double shiftPoint( Point2D p , Point shift ){
		
		Point2D.Double returnPoint = new Point.Double( p.getX() + shift.getX() , p.getY() + shift.getY() );
		return returnPoint;
	}
	public static Point2D dividePoint( Point2D p , double factor ){
		
		Point2D returnPoint = new Point.Double( p.getX() /factor , p.getY() /factor );
		return returnPoint;
	}
	
	public static Point getRotationalAbsolutePositionOf(Point2D relativePosition , double angleRadians ) {
		
		double returnX = relativePosition.getX();
		double returnY = relativePosition.getY(); 
		
		double cosineTheta = Math.cos( angleRadians );
		double sineTheta = Math.sin( angleRadians );
		
		Point returnPoint = new Point(
				(int)( returnX*cosineTheta - returnY*sineTheta ),
				(int)( returnX*sineTheta + returnY*cosineTheta )
		);
		
		return returnPoint;
	}
	public static Point subtractPosition(Point point1, Point point2) {

		return new Point( point1.x - point2.x , point1.y - point2.y );
	}
	
}
