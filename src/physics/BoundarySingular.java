package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import engine.MovingCamera;
import entityComposites.EntityStatic;

public class BoundarySingular extends Boundary{

	protected BoundaryVertex origin = new BoundaryVertex( new Point(0,0) ); //move into boundary
	private final Line2D[] separatingSides = new Line2D[0];
	
	
	@Override
	protected Line2D[] getSeparatingSides(Boundary partner) { // RETURN NO AXES
		return separatingSides;
	}
	
	@Override
	public Line2D[] collectAxesOfSeparationWith(Boundary partner) {
		return new Line2D[0];
	}
	
	@Override
	public void constructVoronoiRegions() {
		//all signleton
	}

	@Override
	public void debugDrawBoundary(MovingCamera cam, Graphics2D g2, EntityStatic ownerEntity) {
		cam.drawCrossInWorld( origin.toPoint() ); 
		cam.drawString( "Point", origin.toPoint() );
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
	public BoundaryVertex[] farthestVerticesFromPoint(BoundaryVertex boundaryVertex, Line2D axis) {
		return new BoundaryVertex[]{ origin };
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
		BoundarySingular clone = new BoundarySingular(); //ACTUALLY DO CLONE FOR EVENTS
		clone.origin.setPos(position);
		return clone;
	}

	@Override
	public void rotateBoundaryFromTemplate(Point center, double angle, Boundary template) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point rotateBoundaryFromTemplatePoint(Point center, double angle, BoundaryPolygonal template) {
		// TODO Auto-generated method stub
		return null;
	}

}
