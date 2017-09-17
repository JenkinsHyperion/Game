package entityComposites;

import java.awt.Point;

import physics.Boundary;

public class ColliderRotational extends Collider implements RotateableComposite{

	private Boundary storedBounds; // zero angle boundary that angled boundaries calculate from
	
	public ColliderRotational(EntityStatic owner , Boundary boundary) {
		super(owner , boundary);
		this.storedBounds = boundary.temporaryClone();
	}

	protected ColliderRotational( Collider colliderOld ){ //PACKAGE CONSTRUCTOR FOR "ENHANCING" PRE-EXISTING NON ROTATIONAL COLLIDER
		this.ownerEntity = colliderOld.ownerEntity;
		this.boundary = colliderOld.boundary;
		this.storedBounds = colliderOld.boundary.temporaryClone();
		this.engine = colliderOld.engine;
	}
	
	@Override
	public void setAngle(double angleDegrees) {
		this.boundary.rotateBoundaryFromTemplate( Entity.origin , Math.toRadians(angleDegrees) , storedBounds ); 
		//this.boundary.constructVoronoiRegions();
	}
	
	@Override
	public void addAngle(double angleRadians) {
		
	}
	
	@Override
	public void setBoundary(Boundary boundary) {
		super.setBoundary(boundary);
		this.storedBounds = boundary.temporaryClone();
	}
	
}
