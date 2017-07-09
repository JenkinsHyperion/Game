package entityComposites;

import java.util.ArrayList;

import entityComposites.AngularComposite.AngleComposite;
import physics.ResolutionState;


public abstract class ParentComposite implements EntityComposite {
	
	private static final ParentComposite.NullParentComposite nullParent = new NullParentComposite();

	protected String compositeName = "ParentComposite";

	/**
	 * Sets position of entity and all children if this parent composite is not null
	 * @param x
	 * @param y
	 */
	public abstract void setCompositedPosition( double x , double y);
	public abstract EntityStatic[] getChildrenEntities();
	public abstract boolean isParent();
	
	public static ParentComposite nullParentComposite(){
		return nullParent;
	}
	
	// #################################################################################################################################
	//		CONCRETE INNER CLASSES
	// #################################################################################################################################
	
	public static class ParentRotateableComposite extends ParentComposite implements RotateableComposite {
		
		protected ArrayList<ChildComposite.Rotateable> childrenCompositesList = new ArrayList<ChildComposite.Rotateable>();
		protected EntityStatic ownerParentEntity;
		
		public ParentRotateableComposite( EntityStatic owner ){
			this.ownerParentEntity = owner;
		}
		
		@Override
		public EntityStatic[] getChildrenEntities(){

			final int n = childrenCompositesList.size();
			EntityStatic[] returnList = new EntityStatic[ n ];
			for ( int i = 0 ; i < n ; i++ ){
				returnList[i] = childrenCompositesList.get(i).ownerEntityChild;
			}

			return returnList;
		}
		
		/**
		 * 
		 * @param child
		 * @return The ChildComposite that was created
		 */
		protected ChildComposite.Rotateable registerChild( EntityStatic child ){

			if ( ownerParentEntity.getAngularComposite().exists() ){
				
				AngleComposite angularParent = (AngleComposite) ownerParentEntity.getAngularComposite();
			
				ChildComposite.Rotateable childComposite = new ChildComposite.Rotateable(child , ownerParentEntity, ownerParentEntity.getTranslationComposite(), ownerParentEntity.getRotationComposite(), childrenCompositesList.size() , ownerParentEntity.getPosition() , angularParent.getAngle() );

				child.childComposite = childComposite;
				this.childrenCompositesList.add( childComposite );
				
				return childComposite;
				
			}else{
				System.err.println("TODO: Parent Rotateable Composite must be rotateable. Make non rotateable parent relationship ");
				return null;
			}
			
		}
		
		@Override
		public void setAngle(double angleRadians) { //FIXME Clamping issue between angleRadiansOld and angleRadians from angularCOmposite
			
			double angleRadiansOld = Math.toRadians( ownerParentEntity.getAngularComposite().getAngle() );

			for ( ChildComposite.Rotateable child : childrenCompositesList ){ 
				
				double relativeX = child.zeroAnglePosition.getX();
				double relativeY = child.zeroAnglePosition.getY();
				
				double x = ( relativeX*Math.cos(angleRadiansOld) - relativeY*Math.sin(angleRadiansOld) );
				double y = ( relativeX*Math.sin(angleRadiansOld) + relativeY*Math.cos(angleRadiansOld) );

				child.ownerEntityChild.setCompositedPos( ownerParentEntity.x + x , ownerParentEntity.y + y ); // move children
				
				child.ownerEntityChild.getAngularComposite().setAngleInDegrees( angleRadians - child.relativeAngleDegrees); // rotate children
			}
		}
		
		@Override
		public void addAngle(double angleRadians) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public boolean isParent() {
			return true;
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
		
		@Override
		public EntityStatic[] getChildrenEntities() {
			//return new EntityStatic[0];
			return null;
		}
		
		@Override
		public void setCompositedPosition(double x, double y) {
			//FIXME EMPTY CALLING
			//System.err.println("Warning: Attempted to set child position from Null Parent Composite");
		}
		
		@Override
		public boolean isParent() {
			return false;
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
	@Override
	public void setCompositeName(String newName) {
		this.compositeName = newName;
	}
	@Override
	public String getCompositeName() {
		return this.compositeName;		
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
