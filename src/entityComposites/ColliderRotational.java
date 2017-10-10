package entityComposites;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import engine.MovingCamera;
import physics.Boundary;
import physics.Vector;

public class ColliderRotational extends Collider implements RotateableComposite{

	private Boundary storedBounds; // zero angle boundary that angled boundaries calculate from
	private AngularComposite ownerEntityAngular;
	
	public ColliderRotational(EntityStatic owner , AngularComposite ownerEntityAngular , Boundary boundary) {
		super(owner , boundary);
		this.storedBounds = boundary.temporaryClone();
		this.ownerEntityAngular = ownerEntityAngular;
	}

	protected ColliderRotational( Collider colliderOld ){ //PACKAGE CONSTRUCTOR FOR "ENHANCING" PRE-EXISTING NON ROTATIONAL COLLIDER
		this.ownerEntity = colliderOld.ownerEntity;
		this.boundary = colliderOld.boundary;
		this.storedBounds = colliderOld.boundary.temporaryClone();
		this.engine = colliderOld.engine;
	}
	
	@Override
	public void setAngle(double angleDegrees) {
		//this.boundary.rotateBoundaryFromTemplate( Entity.origin , Math.toRadians(angleDegrees) , storedBounds ); 
		//this.boundary.constructVoronoiRegions();
	}
	
	@Override
	public void addAngle(double angleRadians) {
		
	}
	
	@Override
	public Line2D getRelativeAxis(Line2D axis) {
		return Vector.lineRotatedBy(axis, this.ownerEntityAngular.getAngleInRadians() );
	}
	public Line2D getAbsoluteAxisFromRelativeAxis( Line2D axis ){
		return Vector.lineRotatedBy(axis, -this.ownerEntityAngular.getAngleInRadians() );
    }
	
	@Override
	public void debugDrawBoundary(MovingCamera camera , Graphics2D g){
		AffineTransform entityTransformation = new AffineTransform();
		Graphics2D gRotation = (Graphics2D) g.create();
		
		int originX = camera.getRelativeX( this.ownerEntity.getX()) ;
		int originY = camera.getRelativeY( this.ownerEntity.getY()) ;
		
		entityTransformation.translate( originX , originY );
		
		entityTransformation.rotate( ownerEntity.getAngularComposite().getAngleInRadians() );
		
		entityTransformation.translate( -originX , -originY );
		
		gRotation.transform(entityTransformation);
		
		this.getBoundary().debugDrawBoundary(camera, gRotation, this.ownerEntity);
	}
	
	@Override
	public void setBoundary(Boundary boundary) {
		super.setBoundary(boundary);
		this.storedBounds = boundary.temporaryClone();
	}
	
	@Override
	public Point2D absolutePositionOfRelativePoint( Point2D p ){ 
		return this.ownerEntity.getAbsolutePositionOf(p);
	}
	@Override
	public Point2D absolutePositionOfRelativePoint( Point p ){
		return this.ownerEntity.getAbsolutePositionOf(p);
	}
	
}
