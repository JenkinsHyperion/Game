package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import engine.MovingCamera;
import entityComposites.Collider;
import entityComposites.EntityStatic;
import misc.*;

public abstract class Boundary {
	
	protected VoronoiRegion[] regions = new VoronoiRegion[0]; //construxct with undefined region to prevent crashes

	public static final byte CIRCULAR = 0;
	public static final byte POLYGONAL = 1;
	
	public abstract Boundary atPosition( Point position );
	
	public abstract <B extends Boundary> void rotateBoundaryFromTemplate( Point center, double angle , B template );
	//public abstract Point rotateBoundaryFromTemplatePoint(Point center, double angle , Boundary template);
	
	protected abstract Line2D[] getSeparatingSides();
	
	public abstract void debugDrawBoundary( MovingCamera cam , Graphics2D g2, EntityStatic ownerEntity );

	protected abstract Point2D[] getOuterPointsPair(Line2D axis);
	
	protected abstract Point2D farthestPointFromPoint(Point2D boundaryPoint, Line2D axis);
	
	protected abstract Point2D farthestLocalPointFromPoint( Point primaryOrigin, Point2D localPoint, Line2D axis);
		
	public abstract BoundaryFeature[] farthestFeatureFromPoint( Point primaryOrigin, Point secondaryOrigin, Point2D p2, Line2D axis );
	
	public abstract BoundaryVertex[] getCornersVertex();
	public abstract Point2D[] getCornersPoint();
	public abstract Point2D[] getLocalCornersPoint( Point localEntityPosition );
	
	public abstract <T> Boundary temporaryClone();
	
	public abstract void scaleBoundary(double scaleFactor);
	public abstract void scaleBoundary(double scaleFactor, Point center);
	
	public abstract void constructVoronoiRegions();
	protected VoronoiRegion[] getVoronoiRegions(){ return this.regions; }
	
	public abstract byte getTypeCode();
	/** 
	 * @param boundaryRaw - The real(raw) boundary to pass in. Inside this method it will deal with Local offset.
	 * @param ent Entity to get boundary from
	 * @return Polygon that hopefully is the same shape as the boundary
	 */
	public abstract Polygon getLocalPolygonBounds( );

	public abstract Polygon getPolygonBounds( EntityStatic owner );
	
	public static Point2D[] getFarthestPointsBetween( Boundary b1 , Boundary b2 , Line2D axis ){

		Point2D[] farthestPoints = new Point2D[]{ b1.getOuterPointsPair(axis)[0] , b2.getOuterPointsPair(axis)[0] };
		
		Point2D[] points1= b1.getOuterPointsPair(axis);
		Point2D[] points2= b2.getOuterPointsPair(axis);
		
		for ( int i = 0 ; i < points1.length ; i++ ){
			
			for ( int j = 0 ; j <points2.length ; j++ ){
				
				if (getProjectionPoint( points1[i] , axis ).distance( getProjectionPoint( points2[j] , axis ) ) 
						> 
					getProjectionPoint( farthestPoints[0] , axis ).distance( getProjectionPoint( farthestPoints[1] , axis ) ) 
				){
					// points i and j are farther apart on axis than whats stored 
					farthestPoints[0] = points1[i];
					farthestPoints[1] = points2[j];
				}
				
			}
		}	
		return farthestPoints;
	}
	
	public static Point2D shiftPoint( Point2D p , Point2D shift ){
		
		Point2D returnPoint = new Point.Double( p.getX() + shift.getX() , p.getY() + shift.getY() );
		return returnPoint;
	}
	
	/**Return is world positions
	 * 
	 * @param primary
	 * @param b1
	 * @param secondary
	 * @param b2
	 * @param axis
	 * @return
	 */
	public static Point2D[] getFarthestPointsBetween( EntityStatic primary, Boundary b1 , EntityStatic secondary, Boundary b2 , Line2D axis ){
		
		final Point2D primaryPosition = primary.getPosition();
		final Point2D secondaryPosition = secondary.getPosition();
		
		final Point2D[] points1= b1.getOuterPointsPair(axis); 
		final Point2D[] points2= b2.getOuterPointsPair(axis); 
		
		Point2D localPointPlayer = shiftPoint( points1[0] , primaryPosition );
		Point2D localPointStat = shiftPoint( points2[0] , secondaryPosition );
		
		Point2D[] farthestPoints = new Point2D[]{ localPointPlayer , localPointStat };
		
		for ( int i = 0 ; i < points1.length ; i++ ){
			
			localPointPlayer = getProjectionPoint( shiftPoint( points1[i] , primaryPosition) , axis );
			
			for ( int j = 0 ; j < points2.length ; j++ ){ 
				
				localPointStat = getProjectionPoint( shiftPoint( points2[j], secondaryPosition ) , axis );
				
				if ( localPointPlayer.distance( localPointStat ) 
						> 
					getProjectionPoint( farthestPoints[0] , axis ).distance( getProjectionPoint( farthestPoints[1] , axis ) ) 
				){
					// points i and j are farther apart on axis than whats stored 
					farthestPoints[0] = shiftPoint(points1[i] , primaryPosition );
					farthestPoints[1] = shiftPoint(points2[j] , secondaryPosition );
				}
				
			}
		}	
		return farthestPoints;
	}
	

	//SEPARATING AXIS THEORM METHODS
	
	public static double dotProduct(Line2D line1 , Line2D line2){ //Returns the magnitude of the projection vector

		
		return (line1.getX1() - line1.getX2()) * (line2.getX1() - line2.getX2()) + 
				(line1.getY1() - line1.getY2()) * (line2.getY1() - line2.getY2());
	}
	
	public static Point2D  getProjectionPoint( Point2D point, Line2D line){ //Returns the point on the line where the input point is
		//projected on the line
		
		if (line.getX1() == line.getX2()){ //Good ol' undefined slope check
			return new Point2D.Double( line.getX1() , point.getY() ); // if projection base is vertical, y on line is just y of point
		}
		if (line.getY1() == line.getY2()){ //Slope of zero saves some calculation
			return new Point2D.Double( point.getX() , line.getY1() ); // same as above but for x
		}
		
		double m1 = (line.getY1() -  line.getY2())/(line.getX1() -  line.getX2());
		double m2 = -1/m1; //normal slope
		
		double b1 = line.getP1().getY() - ( m1 * line.getP1().getX() );	//trash intercept variables, they are not useful
		double b2 = point.getY() - ( m2 * point.getX() );		
		
		double intersectX = (b2-b1) / (m1-m2) ; 
		return new Point2D.Double( intersectX , (m1 * intersectX) + b1 );
		
	}
	
	public static Line2D getProjectionLine( Line2D line1 , Line2D line2 ){
		
		double Xproj = ( dotProduct( line1 , line2 ) / (
							(line2.getX1() - line2.getX2()) * (line2.getX1() - line2.getX2()) + 
							(line2.getY1() - line2.getY2()) * (line2.getY1() - line2.getY2()) )
				) * ( line2.getX1() - line2.getX2() ) ;
		
		double Yproj = ( dotProduct( line1 , line2 ) / (
				(line2.getX1() - line2.getX2()) * (line2.getX1() - line2.getX2()) + 
				(line2.getY1() - line2.getY2()) * (line2.getY1() - line2.getY2()) )
	) * ( line2.getY1() - line2.getY2() ) ;
		
		Point2D projectedPoint = getProjectionPoint( line1.getP1() , line2);
		
		return new Line2D.Double(projectedPoint.getX() , projectedPoint.getY() , 
				projectedPoint.getX()-Xproj , projectedPoint.getY()-Yproj );
		
	}
	
	public static Line2D[] getSeparatingSidesBetween( Boundary b1 , Boundary b2){
		
		Line2D[] separatingSides1 = b1.getSeparatingSides();
		Line2D[] separatingSides2 = b2.getSeparatingSides();
		
		Line2D[] lines = new Line2D[ separatingSides1.length + separatingSides2.length  ]; //compile final array
		
		int i = 0;
		while ( i < separatingSides1.length ){
			lines[i] = separatingSides1[i];
			i++;
		}
		int j = 0;
		while ( i < lines.length ){
			lines[i] = separatingSides2[j];
			i++;
			j++;
		}
		
		return lines;
		
	}

	public void debugDrawVoronoiRegions( Point absPos, MovingCamera cam , Graphics2D g2 ){
		
		for (VoronoiRegion region : regions ){
			region.debugDrawRegion( absPos ,cam, g2);
		}
		
	}
	
}
