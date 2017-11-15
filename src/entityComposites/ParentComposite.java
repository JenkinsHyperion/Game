package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import entityComposites.AngularComposite.Angled;
import physics.ResolutionState;


public abstract class ParentComposite implements EntityComposite {
	
	private static final ParentComposite.Null nullParent = new Null();

	protected String compositeName = "ParentComposite";

	/**
	 * Sets position of entity and all children if this parent composite is not null
	 * @param x
	 * @param y
	 */
	public abstract void notifyPositionChange( double x , double y);
	public abstract EntityStatic[] getChildrenEntities();
	public abstract boolean isParent();
	protected abstract boolean isRotateable();

	public abstract void debugPrintChildren();
	
	public static ParentComposite nullParentComposite(){
		return nullParent;
	}
	
	// #################################################################################################################################
	//		CONCRETE INNER CLASSES
	// #################################################################################################################################
	
	public static class Rotateable extends ParentComposite implements RotateableComposite {
		
		protected ArrayList<ChildComposite.TranslationOnly> translationalChildrenList = new ArrayList<ChildComposite.TranslationOnly>();
		protected ArrayList<ChildComposite.Rotateable> rotationalChildrenList = new ArrayList<ChildComposite.Rotateable>();
		protected EntityStatic ownerParentEntity;
		
		public Rotateable( EntityStatic owner ){
			this.ownerParentEntity = owner;
		}
		
		@Override
		public EntityStatic[] getChildrenEntities(){

			final int n = rotationalChildrenList.size();
			EntityStatic[] returnList = new EntityStatic[ n ];
			for ( int i = 0 ; i < n ; i++ ){
				returnList[i] = rotationalChildrenList.get(i).ownerEntity;
			}

			return returnList;
		}
		@Override
		public void debugPrintChildren(){
			
			System.out.println("CHLDREN OF "+this.ownerParentEntity+" {");
			
			for (  ChildComposite.Rotateable child : rotationalChildrenList ){
				System.out.println( child.ownerEntity + "(rotational)" );
			}
			for ( ChildComposite.TranslationOnly child : translationalChildrenList ){
				System.out.println( child.ownerEntity+ "(translation only)" );
			}
			
			System.out.println("}");
		}
		
		protected ChildComposite.TranslationOnly createTranslationalChild( EntityStatic newChild ){

			if ( !newChild.childComposite.exists() ){ //Make sure newChild isn't already a child of soemthing else
				if ( ownerParentEntity.getAngularComposite().exists() ){

					ChildComposite.TranslationOnly childComposite = new ChildComposite.TranslationOnly(newChild , this, translationalChildrenList.size() );
	
					newChild.childComposite = childComposite;
					this.translationalChildrenList.add( childComposite );
					
					return childComposite;
					
				}else{
					System.err.println("TODO: Parent Rotateable Composite must be rotateable. Make non rotateable parent relationship ");
					return null;
				}
			}else{
				return null;
			}
		}
		
		/**
		 * 
		 * @param newChild
		 * @return The ChildComposite that was created
		 */
		protected ChildComposite.Rotateable createRotateableChild( EntityStatic newChild ){

			if ( !newChild.childComposite.exists() ){ //Make sure newChild isn't already a child of soemthing else
				if ( ownerParentEntity.getAngularComposite().exists() ){
					
					Angled angularParent = (Angled) ownerParentEntity.getAngularComposite();
				
					ChildComposite.Rotateable childComposite = new ChildComposite.Rotateable(newChild , this, ownerParentEntity.getRotationComposite(), rotationalChildrenList.size() , ownerParentEntity.getPosition() , angularParent.getAngleInDegrees() );
	
					newChild.setChildComposite( childComposite );
					this.rotationalChildrenList.add( childComposite );
					return childComposite;
					
				}else{
					System.err.println("TODO: Parent Rotateable Composite must be rotateable. Make non rotateable parent relationship ");
					return null;
				}
			}else{
				return null;
			}
		}
		
		@Override
		public EntityStatic getOwnerEntity() {
			return this.ownerParentEntity;
		}
		
		protected void unregisterChild( ChildComposite.Rotateable childComposite ){
			
			this.rotationalChildrenList.remove( childComposite.getIndex() );
			
			for ( int i = childComposite.getIndex() ; i < rotationalChildrenList.size() ; ++i ){
				rotationalChildrenList.get(i).decrementIndex(); 
			}
		}
		
		@Override
		public void setAngle(double angleRadians) { //FIXME Clamping issue between angleRadiansOld and angleRadians from angularCOmposite
			
			double angleRadiansOld = Math.toRadians( ownerParentEntity.getAngularComposite().getAngleInDegrees() );

			for ( ChildComposite.TranslationOnly child : translationalChildrenList ){ //Move translatable children children
				
				double relativeX = child.relativePosition.getX();
				double relativeY = child.relativePosition.getY();
				
				double x = ( relativeX*Math.cos(angleRadiansOld) - relativeY*Math.sin(angleRadiansOld) );
				double y = ( relativeX*Math.sin(angleRadiansOld) + relativeY*Math.cos(angleRadiansOld) );

				child.ownerEntity.setCompositedPos( ownerParentEntity.x + x , ownerParentEntity.y + y ); // move children

			}
			
			for ( ChildComposite.Rotateable child : rotationalChildrenList ){ //Move AND ROTATE rotational children
				
				double relativeX = child.zeroAnglePosition.getX();
				double relativeY = child.zeroAnglePosition.getY();
				
				double x = ( relativeX*Math.cos(angleRadiansOld) - relativeY*Math.sin(angleRadiansOld) );
				double y = ( relativeX*Math.sin(angleRadiansOld) + relativeY*Math.cos(angleRadiansOld) );

				child.ownerEntity.setCompositedPos( ownerParentEntity.x + x , ownerParentEntity.y + y ); // move children
				
				child.ownerEntity.getAngularComposite().setAngleInDegrees( angleRadians - child.relativeAngleDegrees); // rotate children
			
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
		@Override
		public void notifyPositionChange( double x, double y){
			for( ChildComposite.TranslationOnly child : translationalChildrenList ){
				child.setPosition( x + child.relativePosition.x , y + child.relativePosition.y );
			}
			for( ChildComposite.Rotateable child : rotationalChildrenList ){
				
				double relativeX = child.zeroAnglePosition.getX();
				double relativeY = child.zeroAnglePosition.getY();
				double angleRadians = this.ownerParentEntity.getAngularComposite().getAngleInRadians();
				
				double sx = ( relativeX*Math.cos(angleRadians) - relativeY*Math.sin(angleRadians) );
				double sy = ( relativeX*Math.sin(angleRadians) + relativeY*Math.cos(angleRadians) );

				child.ownerEntity.setCompositedPos( x + sx , y + sy ); // move children
				
			}
		}
		
		@Override
		protected boolean isRotateable() {
			return true;
		}
		
		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public void disableComposite() {
			// TODO Auto-generated method stub
			
		}
	}

	// #################################################################################################################################
	
	public static class Null extends ParentComposite{
		
		private static EntityStatic[] emptyChildrenArray = new EntityStatic[0];
		
		@Override
		public EntityStatic[] getChildrenEntities() {
			return emptyChildrenArray;
		}
		
		@Override
		public void debugPrintChildren(){
			
		}
		
		@Override
		public void notifyPositionChange(double x, double y) {
			//FIXME EMPTY CALLING
			//System.err.println("Warning: Attempted to set child position from Null Parent Composite");
		}
		
		@Override
		public boolean isParent() {
			return false;
		}
		
		@Override
		public EntityStatic getOwnerEntity() {
			throw new NullPointerException();
		}
		
		@Override
		protected boolean isRotateable() {
			return false;
		}
		
		@Override
		public boolean exists() {
			return false;
		}

		@Override
		public void disableComposite() {
			
		}

	}
	
	// #################################################################################################################################
	//								END INNER CLASSES
	// #################################################################################################################################

}
