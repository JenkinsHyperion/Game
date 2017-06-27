package entityComposites;

import java.awt.Point;

public class ChildComposite implements RotateableComposite{

	protected Point zeroAnglePosition;
	protected double relativeAngleDegrees;
	protected EntityStatic ownerChild;
	
	protected DynamicRotationComposite parentRotation;
	protected TranslationComposite parentTranslation;
	
	private int parentIndex;
	
	protected ChildComposite( EntityStatic ownerChild , TranslationComposite parentTranslation , DynamicRotationComposite parentRotation , int index , Point parentPosition, double parentAngle){
		this.ownerChild = ownerChild;
		this.parentIndex = index;
		this.zeroAnglePosition = new Point( 
				ownerChild.getX() - (int)parentPosition.getX() , 
				ownerChild.getY() - (int)parentPosition.getY()  
		);
		this.relativeAngleDegrees = parentAngle - ownerChild.getAngularComposite().getAngle() ;
		System.err.println("Relative angle of "+relativeAngleDegrees);

	}

	@Override
	public void setAngle(double angleRadians) {

	}
	
	@Override
	public void addAngle(double angleRadians) {
		this.relativeAngleDegrees += angleRadians;
	}

	
}
