package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import engine.MovingCamera;
import entityComposites.EntityStatic;

public class BoundarySingular extends Boundary{

	protected BoundaryVertex origin = new BoundaryVertex( new Point(0,0) ); //move into boundary
	
	protected Point center;
	
	public BoundarySingular(){
		center = new Point(0,0);
	}
	
	private BoundarySingular( Point center ){ //CLONGING CONSTRUCTOR
		this.center = center;
		this.origin.setPos(center);
		this.constructVoronoiRegions();
	}
	
	@Override
	protected Line2D[] getSeparatingSides(Boundary partner) { // RETURN NO AXES

		for ( VoronoiRegion region : partner.getVoronoiRegions() ){

			if ( region.containsPoint( center ) ){ //TODO OPTIMIZE TO REGION CHECK SYSTEM getRegion()
				return new Line2D[]{ region.constructDistanceLine( center ) };
			}
		}
		System.err.println(this + " is outside of Voronoi Region");
		return new Line2D[0];
		
	}
	
	@Override
	public Line2D[] collectAxesOfSeparationWith(Boundary partner) {
		return this.getSeparatingSides(partner);
	}
	
	@Override
	public void constructVoronoiRegions() {
		this.regions = new VoronoiRegion[]{ VoronoiRegion.getUndefinedVoronoiRegion( origin ) };
	}

	@Override
	public void debugDrawBoundary(MovingCamera cam, Graphics2D g2, EntityStatic ownerEntity) {
		cam.drawCrossInWorld( origin.toPoint() ); 
		cam.drawString( "Point Boundary", origin.toPoint() );
	}
	
	@Override
	public BoundaryVertex[] farthestVerticesFromPoint(BoundaryVertex boundaryVertex, Line2D axis) {
		return new BoundaryVertex[]{ origin };
	}
	
	@Override
	protected Point2D[] getOuterPointsPair(Line2D axis) {
		return new Point2D[]{ origin.toPoint(), origin.toPoint() };
	}

	@Override
	protected Point2D farthestPointFromPoint(Point2D boundaryPoint, Line2D axis) {
		return origin.toPoint();
	}

	@Override
	public BoundaryVertex[] farthestVerticesFromPoint(Point2D point, Line2D axis) {
		return new BoundaryVertex[]{ origin };
	}

	@Override
	public BoundaryVertex[] getCornersVertex() {
		return new BoundaryVertex[]{ origin };
	}

	@Override
	public Point2D[] getCornersPoint() {
		return new Point2D[]{ origin.toPoint() };
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

}
