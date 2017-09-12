package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import engine.MovingCamera;
import entityComposites.EntityStatic;
import misc.CollisionEvent;
import misc.DefaultCollisionEvent;

public class BoundaryLinear extends BoundaryPolygonal {
	
	protected CollisionEvent defaultCollisionEvent = new DefaultCollisionEvent();
	
	public BoundaryLinear( Line2D relativeLine ){
		super( new Line2D[]{ relativeLine , new Line2D.Double( relativeLine.getP2(), relativeLine.getP1() ) } );
	}
	
	@Override
	public Boundary atPosition(Point position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rotateBoundaryFromTemplate(Point center, double angle, Boundary template) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Line2D[] getSeparatingSides() {
		return new Line2D[]{ sides[0].toLine() };
	}

	@Override
	public void debugDrawBoundary(MovingCamera cam, Graphics2D g2, EntityStatic ownerEntity) {
		
		cam.draw( new Line2D.Double(
				corners[0].toPoint().getX() + ownerEntity.getPosition().getX(),
				corners[0].toPoint().getY() + ownerEntity.getPosition().getY(),
				corners[1].toPoint().getX() + ownerEntity.getPosition().getX(),
				corners[1].toPoint().getY() + ownerEntity.getPosition().getY()
				) );
	}

	@Override
	protected Point2D[] getOuterPointsPair(Line2D axis) {
		
		return new Point2D[]{ corners[0].toPoint() , corners[1].toPoint() };
		
	}

	@Override
	protected Point2D farthestPointFromPoint(Point2D boundaryPoint, Line2D axis) {

		if ( boundaryPoint.distance( corners[0].toPoint() ) > boundaryPoint.distance( corners[1].toPoint() ) ){
			return corners[0].toPoint();
		}else{
			return corners[1].toPoint();
		}
		
	}

	@Override
	protected Point2D farthestLocalPointFromPoint(Point primaryOrigin, Point2D localPoint, Line2D axis) {
		
		if ( localPoint.distance( corners[0].toPoint() ) > localPoint.distance( corners[1].toPoint() ) ){
			
			final Point2D returnP1 = new Point2D.Double( corners[0].toPoint().getX() + primaryOrigin.x , 
					corners[0].toPoint().getY() + primaryOrigin.y );
			return returnP1;
		}else{
			final Point2D returnP2 = new Point2D.Double( corners[1].toPoint().getX() + primaryOrigin.x , 
					corners[1].toPoint().getY() + primaryOrigin.y );
			return returnP2;
		}
	}

	@Override //FIXME WILL NOT RETURN SIDE YET
	public BoundaryFeature[] farthestFeatureFromPoint(Point primaryOrigin, Point secondaryOrigin, Point2D pointRel,
			Line2D axis) {

		Point relativeSecondary = new Point( secondaryOrigin.x - primaryOrigin.x , secondaryOrigin.y - primaryOrigin.y );
		
		Point2D origin = new Point2D.Double( relativeSecondary.x + pointRel.getX() , relativeSecondary.y + pointRel.getY() );
		
		if ( origin.distance( corners[0].toPoint() ) > origin.distance( corners[1].toPoint() ) ){
			
			return new BoundaryFeature[]{corners[0] };
		}else{
			return new BoundaryFeature[]{corners[1] };
		}
		
	}

	@Override
	public Point2D[] getCornersPoint() {
		return new Point2D[]{ corners[0].toPoint() , corners[1].toPoint() };
	}

	@Override
	public Point2D[] getLocalCornersPoint(Point localEntityPosition) {

		final Point2D p1 = new Point2D.Double( 
				corners[0].toPoint().getX() + localEntityPosition.x ,
				corners[0].toPoint().getY() + localEntityPosition.y
				);
		final Point2D p2 = new Point2D.Double( 
				corners[1].toPoint().getX() + localEntityPosition.x ,
				corners[1].toPoint().getY() + localEntityPosition.y
				);
		
		return new Point2D[]{ p1,p2 };
	}



	@Override
	public void constructVoronoiRegions(){
		
		final VoronoiRegionDefined[] newRegions = new VoronoiRegionDefined[4];//OPTIMIZATION check side/corner ratio guaranteed
		
		// Lay out boundary features in clockwise loop, alternating corners and sides
		for ( int i = 0 ; i < this.sides.length ; i++ ){ 
			newRegions[(2*i)+1] = new VoronoiRegionDefined(sides[i]);
			newRegions[2*i] = new VoronoiRegionDefined(corners[i]); // Orders regions like so: V0 , Side0 , V1, SIde1, V2
	    }
		
		// Itterate over each side and separate it from its adjacent corners
		VoronoiRegionDefined.addSideOuterBounds( newRegions[0] , newRegions[1] , newRegions[2] , sides[0]);
		for ( int i = 3 ; i < newRegions.length ; i=i+2 ){ 
			int iNext = (i+1) % newRegions.length;
			VoronoiRegionDefined.addSideOuterBounds( newRegions[i-1] , newRegions[i] , newRegions[iNext] , sides[i/2]);
	    }
		
		VoronoiRegionDefined.splitOpposingSides( newRegions[1] , newRegions[3] );
		
		this.regions = newRegions;
		
	}

	@Override
	public byte getTypeCode() {
		return Boundary.POLYGONAL;
	}

	@Override
	public Polygon getPolygonBounds(EntityStatic owner) {
		
		Rectangle b = sides[0].toLine().getBounds();
		
		int[] xpoints = {b.x, b.x + b.width, b.x + b.width, b.x};
		int[] ypoints = {b.y, b.y, b.y + b.height, b.y + b.height};
		return new Polygon(xpoints, ypoints, 4); 
	}

}
