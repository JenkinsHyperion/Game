package entityComposites;

import java.awt.Point;

import physics.Boundary;

public class ColliderRotational extends Collider implements RotateableComposite{

	private Boundary storedBounds;
	
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
	public void setAngle(double angleRadians) {
		this.boundary.rotateBoundaryFromTemplate( new Point(0,0) , angleRadians , storedBounds ); 
	}

	
}
