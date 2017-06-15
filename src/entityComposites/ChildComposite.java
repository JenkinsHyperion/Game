package entityComposites;

import java.awt.Point;

public class ChildComposite extends ParentChildRelationship{

	protected Point zeroAnglePosition;
	protected double relativeAngle;
	protected EntityStatic ownerChild;
	private int parentIndex;
	
	protected ChildComposite( EntityStatic owner , int index , Point parentPosition, double parentAngle){
		this.ownerChild = owner;
		this.parentIndex = index;
		this.zeroAnglePosition = new Point( 
				ownerChild.getX() - (int)parentPosition.getX() , 
				ownerChild.getY() - (int)parentPosition.getY()  
		);
		
		//double childAngle = ((RotationCompositeDynamic)owner.getRotationComposite()).angle;
		
		//relativeAngle = childAngle - parentAngle;

	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
}
