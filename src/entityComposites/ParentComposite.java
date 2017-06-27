package entityComposites;

import java.util.ArrayList;

import entityComposites.AngularComposite.AngleComposite;

public abstract class ParentComposite implements EntityComposite {
	
	protected ArrayList<ChildComposite> children = new ArrayList<ChildComposite>();
	protected EntityStatic ownerParentEntity;
	/**
	 * Sets position of entity and all children if this parent composite is not null
	 * @param x
	 * @param y
	 */
	public abstract void setCompositedPosition( double x , double y);
	
	public abstract void manipulateChildren();
	
	
	// #################################################################################################################################
	//		CONCRETE INNER CLASSES
	// #################################################################################################################################
	
	public static class ParentRotateableComposite extends ParentComposite implements RotateableComposite {
		
		public ParentRotateableComposite( EntityStatic owner ){
			this.ownerParentEntity = owner;
		}
		/**
		 * 
		 * @param child
		 * @return The ChildComposite that was created
		 */
		protected ChildComposite registerChild( EntityStatic child ){

			if ( ownerParentEntity.getAngularComposite().exists() ){
				
				AngleComposite angularParent = (AngleComposite) ownerParentEntity.getAngularComposite();
			
				ChildComposite childComposite = new ChildComposite(child , ownerParentEntity.getTranslationComposite(), ownerParentEntity.getRotationComposite(), children.size() , ownerParentEntity.getPosition() , angularParent.getAngle() );
				child.childComposite = childComposite;
				this.children.add( childComposite );
				
				return childComposite;
				
			}else{
				System.err.println("TODO: Parent Rotateable Composite must be rotateable. Make non rotateable parent relationship ");
				return null;
			}
			
		}
		
		@Override
		public void setAngle(double angleRadians) {
			
			manipulateChildren();
			for ( ChildComposite child : children ){ 
				child.ownerChild.getAngularComposite().setAngleInDegrees( angleRadians - child.relativeAngleDegrees);
			}
		}
		
		@Override
		public void addAngle(double angleRadians) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void manipulateChildren() { //METHOD IN CHARGE OF ROTATING ALL CHILDREN
			
			double angleRadians = Math.toRadians( ownerParentEntity.getAngularComposite().getAngle() );
			for ( ChildComposite child : children ){
				
				double relativeX = child.zeroAnglePosition.getX();
				double relativeY = child.zeroAnglePosition.getY();
				
				double x = ( relativeX*Math.cos(angleRadians) - relativeY*Math.sin(angleRadians) );
				double y = ( relativeX*Math.sin(angleRadians) + relativeY*Math.cos(angleRadians) );

				child.ownerChild.setCompositedPos( ownerParentEntity.x + x , ownerParentEntity.y + y );

			}
			
		}
		
		public void setCompositedPosition( double x, double y){
			ownerParentEntity.setPos(  x,  y);
		}
		
		@Override
		public boolean exists() {
			return true;
		}
	}

	// #################################################################################################################################
	
	public static class NullParentComposite extends ParentComposite{
		
		public NullParentComposite( EntityStatic entity ){
			this.ownerParentEntity = entity;
		}
		
		@Override
		public void setCompositedPosition(double x, double y) {
			ownerParentEntity.setPos( x, y);
		}

		@Override
		public void manipulateChildren() {
			
		}
		
		@Override
		public boolean exists() {
			return false;
		}

		@Override
		public void disable() {
			
		}

	}
	
	// #################################################################################################################################
	//								END INNER CLASSES
	// #################################################################################################################################

	
	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub
	}

}
