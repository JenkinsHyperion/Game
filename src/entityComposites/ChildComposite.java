package entityComposites;

import java.awt.Point;

public abstract class ChildComposite{ //TODO split into inner static classes for rotation, translation, and both

	public abstract EntityStatic getParentEntity();
	
	private static ChildComposite nullComposite = new ChildComposite.Null();
	
	public static ChildComposite nullChildComposite(){
		return nullComposite;
	}
	
	public abstract void setPosition( double x, double y);
	
	public abstract boolean isChild();

	public static class Rotateable extends ChildComposite implements RotateableComposite{

		protected EntityStatic ownerEntityChild;
		protected EntityStatic parentEntity;
		
		protected Point zeroAnglePosition;
		protected double relativeAngleDegrees;
		
		protected DynamicRotationComposite parentRotation;
		protected TranslationComposite parentTranslation;
		
		private int parentIndex;
		
		protected Rotateable( EntityStatic ownerChild, EntityStatic parentEntity , TranslationComposite parentTranslation , DynamicRotationComposite parentRotation , int index , Point parentPosition, double parentAngle){
			this.ownerEntityChild = ownerChild;
			this.parentIndex = index;

			this.zeroAnglePosition = parentEntity.getFullRelativePositionOf(ownerChild);

			this.relativeAngleDegrees = parentAngle - ownerChild.getAngularComposite().getAngleInDegrees() ;
		}
		
		public EntityStatic getParentEntity(){
			return this.parentEntity;
		}
		
		@Override
		public void setAngle(double angleRadians) {

		}
		
		@Override
		public void addAngle(double angleRadians) {
			this.relativeAngleDegrees += angleRadians;
		}
		
		@Override
		public void setPosition(double x, double y) {
			
		}
		
		@Override
		public boolean isChild() {
			return true;
		}
		
	}
	
	
	
	public static class Null extends ChildComposite{

		@Override
		public EntityStatic getParentEntity() { 
			return null;
		}
		
		@Override
		public void setPosition(double x, double y) {
			
		}
		
		@Override
		public boolean isChild() {
			return false;
		}
		
	}
	
}
