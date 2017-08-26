package physics;

import java.awt.Point;
import java.awt.geom.Point2D;

import misc.CollisionEvent;

public class BoundaryVertex extends BoundaryFeature{

	protected Point position;

	protected BoundaryVertex( Point position ){
		this.position = position;
	}
	protected BoundaryVertex( Point2D position ){
		this.position = new Point( (int)position.getX(), (int)position.getY() );
	}
	
	protected BoundaryVertex( Point position, CollisionEvent event ){
		super(event);
		this.position = position;
	}
	
	public Point toPoint() {
		return position;
	}

	public int getX() { return (int) this.position.getX(); }
	public int getY() { return (int) this.position.getY(); }

	protected void setX(int x) { this.position.x = x; }
	protected void setY(int y) { this.position.y = y; }

	protected void setPos(Point point) { this.position = point; }
	protected void setPos(Point2D point) { this.position = new Point( (int)point.getX() , (int)point.getY() ); }

	public int getID() { return this.ID; }

	@Override
	public Point2D getP1() {
		return position;
	}

	@Override
	public Point2D getP2() {
		return position;
	}
	
	@Override
	public boolean debugIsVertex() {
		return true;
	}

	@Override
	public boolean debugIsSide() {
		return false;
	}
	@Override
	public Vector getNormal() {
		System.err.println("Attempted to get Normal Vector on BOundaryVertex (not Corner)");
		return null;
	}
	
	@Override
	public String toString() {
		return "Boundary Vertex";
	}
	@Override
	protected CollisionEvent getEvent() {
		return this.collisionEvent;
	}

}
