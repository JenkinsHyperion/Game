package entityComposites;

import java.awt.Point;

public class ChildComposite {

	protected Point zeroAnglePosition;
	protected double relativeAngle;
	protected EntityStatic ownerChild;
	
	protected DynamicRotationComposite parentRotation;
	protected TranslationComposite parentTranslation;
	
	private int parentIndex;
	
	protected ChildComposite( EntityStatic owner , int index , Point parentPosition, double parentAngle){
		this.ownerChild = owner;
		this.parentIndex = index;
		this.zeroAnglePosition = new Point( 
				ownerChild.getX() - (int)parentPosition.getX() , 
				ownerChild.getY() - (int)parentPosition.getY()  
		);
		this.relativeAngle = parentAngle - owner.getAngularComposite().getAngle();

	}

	
}
