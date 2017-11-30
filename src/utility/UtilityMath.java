package utility;

import java.awt.Point;
import java.awt.geom.Point2D;

public final class UtilityMath {

	
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
	
}
