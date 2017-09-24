package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import engine.MovingCamera;
import entityComposites.EntityStatic;
import misc.CollisionEvent;

public class BoundarySingular extends Boundary{

	protected BoundaryVertex vertex;//move into boundary
	
	protected Point center;
	
	public BoundarySingular(){
		center = new Point(0,0);
	}
	
	private BoundarySingular( Point center ){ //CLONING CONSTRUCTOR
		this.center = center;
		this.vertex = new BoundaryVertex( new Point(0,0) );
		this.vertex.setPos(center);
		this.constructVoronoiRegions();
	}
	
	public BoundarySingular( CollisionEvent event ){
		center = new Point(0,0);
		vertex = new BoundaryVertex( new Point(0,0) , event );
	}
	
	@Override
	protected Line2D[] getSeparatingSides() { // RETURN NO AXES

		/*for ( VoronoiRegion region : partner.getVoronoiRegions() ){

			if ( region.containsPoint( center ) ){ //TODO OPTIMIZE TO REGION CHECK SYSTEM getRegion()
				return new Line2D[]{ region.constructDistanceLine( center ) };
			}
		}
		System.err.println(this + " is outside of Voronoi Region");*/
		return new Line2D[0];
		
	}
	
	@Override
	public void constructVoronoiRegions() {
		this.regions = new VoronoiRegion[]{ VoronoiRegion.getUndefinedVoronoiRegion( vertex ) };
	}

	@Override
	public void debugDrawBoundary(MovingCamera cam, Graphics2D g2, EntityStatic ownerEntity) {
		cam.drawCrossInWorld( vertex.toPoint() ); 
		cam.drawString( "Point Boundary", vertex.toPoint() );
	}
	
	@Override
	protected Point2D[] getOuterPointsPair(Line2D axis) {
		return new Point2D[]{ vertex.toPoint(), vertex.toPoint() };
	}

	@Override
	protected Point2D farthestPointFromPoint(Point2D boundaryPoint, Line2D axis) {
		return vertex.toPoint();
	}
	
	@Override
	protected Point2D farthestLocalPointFromPoint(Point primaryOrigin, Point2D localPoint, Line2D axis) {
		return vertex.toPoint();
	}
	
	@Override
	public BoundaryFeature[] farthestFeatureFromPoint(Point primary, Point secondary, Point2D p2, Line2D axis) {
		return new BoundaryVertex[]{ vertex };
	}

	@Override
	public BoundaryVertex[] getCornersVertex() {
		return new BoundaryVertex[]{ vertex };
	}

	@Override
	public Point2D[] getCornersPoint() {
		return new Point2D[]{ vertex.toPoint() };
	}
	
	@Override
	public Point2D[] getLocalCornersPoint( Point localEntityPosition ) {
		return new Point2D[]{ new Point2D.Double( vertex.toPoint().getX() + localEntityPosition.getX(), vertex.toPoint().getY() + localEntityPosition.getY() ) };
	}

	@Override
	public Boundary atPosition(Point position) {
		BoundarySingular clone = new BoundarySingular(position); //ACTUALLY DO CLONE FOR EVENTS
		return clone;
	}

	@Override
	public void rotateBoundaryFromTemplate(Point center, double angle, Boundary template) {
		// TODO Auto-generated method stub
		
	}

	public Point rotateBoundaryFromTemplatePoint(Point center, double angle, BoundaryPolygonal template) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BoundarySingular temporaryClone(){  
		System.err.println("Boundary Singular was not cloned");
		return this;
	}

	@Override
	public void scaleBoundary(double scaleFactor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scaleBoundary(double scaleFactor, Point center) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public byte getTypeCode() {
		return 0;
	}
	
	@Override
	public Polygon getPolygonBounds( ) {
		return new Polygon();
	}
	
	@Override
	public Polygon getPolygonBounds( EntityStatic owner ) {
		return new Polygon();
	}

}
