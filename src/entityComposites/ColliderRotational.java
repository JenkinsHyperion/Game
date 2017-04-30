package entityComposites;

import java.awt.Point;

import physics.Boundary;

public class ColliderRotational extends Collider implements RotateableComposite{

	private Boundary storedBounds;
	
	public ColliderRotational(EntityStatic owner , Boundary boundary) {
		super(owner , boundary);
		this.storedBounds = boundary.temporaryClone();
	}

	@Override
	public void setAngle(double angleRadians) {
		this.boundary.rotateBoundaryFromTemplate( new Point(0,0) , angleRadians , storedBounds ); 
	}

	
}
