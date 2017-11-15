package entityComposites;

import java.awt.Point;

public abstract class ChildComposite implements EntityComposite{ //TODO split into inner static classes for rotation, translation, and both

	public abstract EntityStatic getParentEntity();
	
	private static ChildComposite nullComposite = new ChildComposite.Null();
	
	public static ChildComposite nullChildComposite(){
		return nullComposite;
	}
	
	public abstract void setPosition( double x, double y);
	
	protected abstract void notifyRawPositionChange( double dx, double dy );
	
	public abstract boolean isChild();

	
	public static class TranslationOnly extends ChildComposite{

		protected EntityStatic ownerEntity;
		protected ParentComposite parentComposite;
		
		protected Point relativePosition;

		protected TranslationComposite parentTranslation;
		
		private int parentIndex;

		protected TranslationOnly( EntityStatic ownerChild, ParentComposite parentComposite , int index ){
			this.ownerEntity = ownerChild;
			this.parentIndex = index;
			this.parentComposite = parentComposite;

			this.relativePosition = parentComposite.getOwnerEntity().getFullRelativePositionOf(ownerChild);
		}
		
		@Override
		protected void notifyRawPositionChange(double dx, double dy) {
			relativePosition.setLocation(-dx,-dy);
		}
		
		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public void disableComposite() {

			if ( this.parentComposite != null ){

				//this.parentComposite.unregisterChild(this);

				this.ownerEntity.nullifyChildComposite(); // have owner entity dereference this entity

				this.parentComposite = null;
			}
		}

		@Override
		public EntityStatic getOwnerEntity() {
			return this.ownerEntity;
		}

		@Override
		public EntityStatic getParentEntity() {
			return this.parentComposite.getOwnerEntity();
		}

		@Override
		public void setPosition(double x, double y) {
			this.ownerEntity.setPos(x, y);
		}

		@Override
		public boolean isChild() {
			return true;
		}
		
	}
	
	
	public static class Rotateable extends ChildComposite implements RotateableComposite{

		protected EntityStatic ownerEntity;
		protected ParentComposite.Rotateable parentComposite;
		
		protected Point zeroAnglePosition;
		protected double relativeAngleDegrees;
		
		protected DynamicRotationComposite parentRotation;
		protected TranslationComposite parentTranslation;
		
		private int parentIndex;

		protected Rotateable( EntityStatic ownerChild, ParentComposite.Rotateable parentComposite, DynamicRotationComposite parentRotation , int index , Point parentPosition, double parentAngle){
			this.ownerEntity = ownerChild;
			this.parentIndex = index;
			this.parentComposite = parentComposite;

			this.zeroAnglePosition = parentComposite.getOwnerEntity().getFullRelativePositionOf(ownerChild);

			this.relativeAngleDegrees = parentAngle - ownerChild.getAngularComposite().getAngleInDegrees() ;
		}
		
		public EntityStatic getParentEntity(){
			return this.parentComposite.getOwnerEntity();
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
			this.ownerEntity.setPos(x, y);
		}
		
		protected void notifyRawPositionChange( double dx, double dy ){
			zeroAnglePosition.setLocation(-dx,-dy);
		}
		
		@Override
		public boolean isChild() {
			return true;
		}
		
		protected int getIndex(){
			return this.parentIndex;
		}
		protected void decrementIndex(){
			this.parentIndex-- ;
		}
		
		//ENTITY COMPSOITE INTERFACE METHODS
		
		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public void disableComposite() {

			if ( this.parentComposite != null ){
				
				this.parentComposite.unregisterChild(this);
				
				this.ownerEntity.nullifyChildComposite(); // have owner entity dereference this entity
				
				this.parentComposite = null;
			}

		}

		@Override
		public EntityStatic getOwnerEntity() {
			return this.ownerEntity;
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
		
		protected void notifyRawPositionChange( double x, double y ){
			
		}
		
		@Override
		public boolean isChild() {
			return false;
		}
		
		//ENTITY COMPOSITE INTERFACE METHODS
		
		@Override
		public boolean exists() {
			return false;
		}

		@Override
		public void disableComposite() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public EntityStatic getOwnerEntity() {
			throw new NullPointerException();
		}
		
	}
	
}
