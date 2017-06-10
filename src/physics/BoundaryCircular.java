package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import engine.MovingCamera;
import entityComposites.EntityStatic;

public class BoundaryCircular extends Boundary{

	private EntityStatic ownerEntity;
	
	private BoundaryVertex centerVertex;
	private Point center;
	private Point origin;
	private int radius;
	
	public BoundaryCircular( int radius , EntityStatic ownerEntity ) {
		this.radius = radius;
		this.center = new Point(0,0);
		this.origin = new Point(-radius,-radius);
		this.ownerEntity = ownerEntity;
		this.centerVertex = new BoundaryVertex(center);
		this.constructVoronoiRegions();
	}
	
	private BoundaryCircular( int radius , Point center , EntityStatic ownerEntity){ // FOR CLONING ONLY
		this.radius = radius;
		this.center = new Point( center.x , center.y );
		this.origin = new Point( center.x-radius , center.y-radius );
		this.ownerEntity = ownerEntity;
		this.centerVertex = new BoundaryVertex(center);
		this.constructVoronoiRegions();
	}
	
	@Override
	protected Line2D[] getSeparatingSides() { //CIRCLE BOUNDARY ADDS AXIS FROM CETNER TO CLOSEST CORNER ON OTHER BOUNDARY
		return new Line2D[0];
	}
	
	@Override
	public void constructVoronoiRegions() {
		this.regions = new VoronoiRegion[]{ VoronoiRegion.getUndefinedVoronoiRegion( centerVertex ) };
	}

	@Override
	public void debugDrawBoundary(MovingCamera cam, Graphics2D g2, EntityStatic ownerEntity) {
		Shape boundary = new Ellipse2D.Float( -radius , -radius , 2*radius, 2*radius );
		cam.drawCrossInWorld( center );
		cam.drawShapeInWorld( boundary , ownerEntity.getPosition() );
	}

	@Override
	public BoundaryVertex[] farthestVerticesFromPoint(BoundaryVertex boundaryVertex, Line2D axis) {
		return new BoundaryVertex[0]; //NO VERTICES ON CIRCLE
	}

	@Override
	protected Point2D farthestPointFromPoint(Point2D boundaryPoint, Line2D axis) {
		
		Point2D projection = getProjectionPoint( boundaryPoint , axis);
		
		if ( axis.getX1()-axis.getX2()==0 ){ //vertical line
			
			Point2D top = getProjectionPoint( new Point2D.Float( center.x , center.y - radius ) , axis );
			Point2D bottom = getProjectionPoint( new Point2D.Float( center.x , center.y + radius ) , axis );
			
			if ( projection.distance( top ) > projection.distance( bottom ) ){
				return new Point2D.Float( center.x , center.y - radius );
			}
			else{
				return new Point2D.Float( center.x , center.y + radius );
			}
			
		}
		else {
			double slope = (axis.getY2() - axis.getY1())/(axis.getX2()-axis.getX1());
			
			int outerX = (int)Math.sqrt( (radius*radius) / (slope*slope + 1) ); 
			int outerY = (int)Math.sqrt( radius*radius - outerX*outerX );
			
			Point2D positivePoint;
			Point2D negativePoint;
			
			if ( slope > 0 ){
				positivePoint = new Point2D.Float( outerX+center.x , outerY+center.y );
				negativePoint = new Point2D.Float( -outerX+center.x , -outerY+center.y );
			}
			else{
				positivePoint = new Point2D.Float( -outerX+center.x , outerY+center.y );
				negativePoint = new Point2D.Float( outerX+center.x , -outerY+center.y );
			}
			//Point2D pointLocal = new Point2D.Double( boundaryPoint.getX() , boundaryPoint.getY() );
			
			if ( projection.distance( getProjectionPoint(positivePoint,axis) ) > 
				projection.distance( getProjectionPoint(negativePoint,axis) ) 
			){
				return  new Point2D.Double( positivePoint.getX() , positivePoint.getY() );
			}
			else{
				return  new Point2D.Double( negativePoint.getX() , negativePoint.getY() );
			}
		}
		
	}

	@Override
	protected Point2D[] getOuterPointsPair(Line2D axis) {
		// y^2 = r^2 - x^2  formula of circle
		// y = m*x    		slope intercept form
		
		// (m*x)^2 = r^2 - x^2  plug equation 2
		// (m*x)^2 + x^2 = r^2
		// (m^2 + 1) * x^2 = r^2 pull x^2 out and isolate
		// x = squareRoot[ (r^2) / (m^2 + 1) ]
		if ( axis.getX2()-axis.getX1() ==0 ){
			return new Point2D[]{
					new Point2D.Float(center.x,-radius+center.y),
					new Point2D.Float(center.x,radius+center.y)
			};
		}
		
		
		double slope = (axis.getY2() - axis.getY1())/(axis.getX2()-axis.getX1());
		
		if (slope == 0 ){  //OPTIMIZE organize conditional order
			return new Point2D[]{
					new Point2D.Float( center.x + radius ,center.y ),
					new Point2D.Float( center.x - radius ,center.y )
			};
		}
		
		int outerX = (int)Math.sqrt( (radius*radius) / (slope*slope + 1) ); 
		int outerY = (int)Math.sqrt( (radius*radius) / (1/(slope*slope) + 1 ) );
		
		if ( slope > 0 ){
			return new Point2D[]{
					new Point2D.Float(outerX+center.x,outerY+center.y),
					new Point2D.Float(-outerX+center.x,-outerY+center.y)
			};
		}
		else{
			return new Point2D[]{
					new Point2D.Float(outerX+center.x,-outerY+center.y),
					new Point2D.Float(-outerX+center.x,outerY+center.y)
			};
		}
		
	}

	@Override
	public BoundaryCorner[] farthestVerticesFromPoint(Point2D point, Line2D axis) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoundaryVertex[] getCornersVertex() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point2D[] getCornersPoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boundary atPosition(Point position) {
		//System.err.println("Circular Boundary was not cloned");
		return new BoundaryCircular( this.radius , position , this.ownerEntity);
	}

	@Override
	public void rotateBoundaryFromTemplate(Point center, double angle, Boundary template) {
		// TODO
	}

	public Point rotateBoundaryFromTemplatePoint(Point center, double angle, BoundaryPolygonal template) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Boundary temporaryClone(){
		//System.err.println("Circular Boundary was not cloned");
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
	

}
