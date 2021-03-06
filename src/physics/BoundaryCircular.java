package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import engine.MovingCamera;
import entityComposites.EntityStatic;
import misc.CollisionEvent;

public class BoundaryCircular extends Boundary{


	
	private BoundaryVertex centerVertex;
	private Point center;
	private Point origin;
	private int radius;
	
	public BoundaryCircular( int radius ) {
		this.radius = radius;
		this.center = new Point(0,0);
		this.origin = new Point(-radius,-radius);
		this.centerVertex = new BoundaryVertex(center);
		this.constructVoronoiRegions();
	}
	
	public BoundaryCircular( int radius , CollisionEvent event ) {
		this.radius = radius;
		this.center = new Point(0,0);
		this.origin = new Point(-radius,-radius);
		this.centerVertex = new BoundaryVertex(center,event);
		this.constructVoronoiRegions();
	}
	@Deprecated
	private BoundaryCircular( int radius , Point center ){ // FOR CLONING ONLY

		this.radius = radius;
		this.center = new Point( center.x , center.y );
		this.origin = new Point( center.x-radius , center.y-radius );
		this.centerVertex = new BoundaryVertex(center);
		this.constructVoronoiRegions();
	}
	
	@Override
	protected Line2D[] getSeparatingSides() { //CIRCLE BOUNDARY ADDS AXIS FROM CETNER TO CLOSEST CORNER ON OTHER BOUNDARY
		return new Line2D[0];
	}
	
	@Override
	public Point getRelativeCenter() {
		return center;
	}
	public int getRadius() {
		return this.radius;
	}
	@Override
	public void constructVoronoiRegions() {
		this.regions = new VoronoiRegion[]{ VoronoiRegion.getUndefinedVoronoiRegion( centerVertex ) };
	}

	@Override
	public void debugDrawBoundary(MovingCamera cam, Graphics2D g2, EntityStatic ownerEntity) {
		Shape boundary = new Ellipse2D.Float( -radius , -radius , 2*radius, 2*radius );
		cam.drawCrossInWorld( ownerEntity.getPosition(), g2 );
		cam.drawShapeInWorld( boundary , ownerEntity.getPosition(), g2 );
	}
	public void debugDrawCircleForEditor(MovingCamera cam, Graphics2D g2, Point centerPoint, int radius) {
		Shape boundary = new Ellipse2D.Float( -radius , -radius , 2*radius, 2*radius );
		cam.drawShapeInWorld( boundary , centerPoint, g2 );
	}
	@Override
	protected Point2D farthestPointFromPoint(Point2D boundaryPoint, Line2D axis) {
		
		Point2D projection = getProjectionPoint( boundaryPoint , axis);
		
		if ( axis.getX1()-axis.getX2()==0 ){ //vertical line
			
			Point2D top = getProjectionPoint( new Point2D.Float( 0 , -radius ) , axis );
			Point2D bottom = getProjectionPoint( new Point2D.Float( 0 , +radius ) , axis );
			
			if ( projection.distance( top ) > projection.distance( bottom ) ){
				return new Point2D.Double( 0 , -radius );
			}
			else{
				return new Point2D.Double( 0 , +radius );
			}
			
		}
		else {
			double slope = (axis.getY2() - axis.getY1())/(axis.getX2()-axis.getX1()); //FIXME ensure divide by zero exceptions
			
			int outerX = (int)Math.sqrt( (radius*radius) / (slope*slope + 1) ); 
			int outerY = (int)Math.sqrt( radius*radius - outerX*outerX );
			
			Point2D positivePoint;
			Point2D negativePoint;
			
			if ( slope <= 0 ){
				positivePoint = new Point2D.Double( -outerX , outerY );
				negativePoint = new Point2D.Double( outerX , -outerY );
			}
			else{
				positivePoint = new Point2D.Double( outerX , outerY );
				negativePoint = new Point2D.Double( -outerX , -outerY );
			}
			//Point2D pointLocal = new Point2D.Double( boundaryPoint.getX() , boundaryPoint.getY() );
			
			if ( projection.distance( getProjectionPoint(positivePoint,axis) ) < 
				projection.distance( getProjectionPoint(negativePoint,axis) ) 
			){
				return  new Point2D.Double( negativePoint.getX() , negativePoint.getY() );
				
			}
			else{
				return  new Point2D.Double( positivePoint.getX() , positivePoint.getY() );
			}
		}
		
	}
	
	@Override
	protected Point2D farthestLocalPointFromPoint(Point primaryOrigin, Point2D localPoint, Line2D axis) { //OPTIMIZE REDUCE RELATIVISM
		
		Point2D relativePosition = new Point2D.Double( 
				localPoint.getX() - primaryOrigin.x , 
				localPoint.getY() - primaryOrigin.y
				);
		
		Point2D returnPoint = farthestPointFromPoint(relativePosition, axis);
		
		relativePosition = new Point2D.Double( 
				returnPoint.getX() + primaryOrigin.getX() , 
				returnPoint.getY() + primaryOrigin.getY()
				);
		
		return relativePosition;
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
					new Point2D.Float(0,-radius),
					new Point2D.Float(0,radius)
			};
		}
		
		
		double slope = (axis.getY2() - axis.getY1())/(axis.getX2()-axis.getX1());
		
		if (slope == 0 ){  //OPTIMIZE organize conditional order
			return new Point2D[]{
					new Point2D.Float( radius ,0),
					new Point2D.Float( -radius ,0 )
			};
		}
		
		int outerX = (int)Math.sqrt( (radius*radius) / (slope*slope + 1) ); 
		int outerY = (int)Math.sqrt( (radius*radius) / (1/(slope*slope) + 1 ) );
		
		if ( slope > 0 ){
			return new Point2D[]{
					new Point2D.Float(outerX,outerY),
					new Point2D.Float(-outerX,-outerY)
			};
		}
		else{
			return new Point2D[]{
					new Point2D.Float(outerX,-outerY),
					new Point2D.Float(-outerX,outerY)
			};
		}
		
	}
	
	@Override
	public BoundaryFeature[] farthestFeatureFromPoint(Point primary, Point secondary, Point2D p2, Line2D axis) {
		return new BoundaryFeature[]{ this.centerVertex };
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
	public Point2D[] getLocalCornersPoint( Point localEntityPosition ) {
		// TODO Auto-generated method stub
		return null;
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
		this.radius = (int)( this.radius * scaleFactor );
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
	public Polygon getPolygonBounds( EntityStatic owner ) {
		return new Polygon(new int[]{-this.radius+owner.getPosition().x, this.radius+owner.getPosition().x, this.radius+owner.getPosition().x, -this.radius+owner.getPosition().x}, 
							new int[]{-this.radius+owner.getPosition().y,-this.radius+owner.getPosition().y, this.radius+owner.getPosition().y, this.radius+owner.getPosition().y}, 4 );
	}
	
	@Override
	public Polygon getLocalPolygonBounds() {
		return new Polygon(new int[]{-this.radius, this.radius, this.radius, -this.radius}, 
							new int[]{-this.radius,-this.radius, this.radius, this.radius}, 4 );
	}

}
