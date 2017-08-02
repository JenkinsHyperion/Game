package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import physics.Vector;
/**This composite gives an entity an angle, represented by Double: angleDegrees, and Vector: orientationVector 
 * 
 * @author Jenkins
 *
 */

public abstract class AngularComposite implements EntityComposite {
	protected String compositeName = "AngularCompositeAbstract";

	private static final FixedAngleComposite fixedAngle = new AngularComposite.FixedAngleComposite();
	
	protected double angleDegrees = 0;
	
	public static FixedAngleComposite getFixedAngleSingleton(){
		return fixedAngle;
	}
	
	public abstract double getAngle();
	public abstract void setAngleInDegrees( double angle);
	public abstract void setAngleInRadians( double angle);
	public abstract void notifyAngleChange( double angle );
	public abstract Point getRotationalPositionRelativeTo( Point relativePosition);
	public abstract Vector getOrientationVector();
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
	
	public static class AngleComposite extends AngularComposite{
		private String compositeName = "AngleComposite";
		protected EntityStatic ownerEntity;
		protected Vector orientation = new Vector( 1 , 0 );
		protected ArrayList<RotateableComposite> rotateableCompositeList = new ArrayList<RotateableComposite>();
		
		public AngleComposite(EntityStatic ownerEnttiy){
			this.ownerEntity = ownerEnttiy;
			orientation = new Vector( 1 , 0 );
		}
		/**Angle in DEGREES
		 * 
		 */
		@Override
		public double getAngle(){ return angleDegrees ; }
		@Override
		public Vector getOrientationVector(){ return orientation; }
		
		private void updateOrientationVector( double angleRadians ){
			this.orientation = new Vector( Math.cos(angleRadians) , Math.sin(angleRadians) );
		}

		private void setAngleOfRotateables( double angleRadians ){
			for ( RotateableComposite rotateable : rotateableCompositeList ){
				rotateable.setAngle(angleRadians);
			}
		}
		
		protected void addRotateable( RotateableComposite rotateable ){
			rotateableCompositeList.add( rotateable );
		}
		
		@Override
		public void notifyAngleChange(double angleRadians) {
			for ( RotateableComposite rotateable : rotateableCompositeList ){
				rotateable.addAngle(angleRadians);
			}
		}
		
		private void setInternalAngle(double angle){ //composited method
			double angleRadians = (angle * ((Math.PI)/180) ) ;
			
			this.updateOrientationVector(angleRadians);
			this.ownerEntity.getEntitySprite().setAngle(angle);
			
			setAngleOfRotateables(angle);
		}
	
		protected void addAngleInDegrees(double angle){
			this.angleDegrees = (float) (this.angleDegrees + angle);
			double angleRadians = (this.angleDegrees * ((Math.PI)/180) ) ;
	
			setInternalAngle( angleRadians + angle );
		}
	
		public void addAngleInRadians(double addRadians){
	
			this.angleDegrees = angleDegrees + (float) (addRadians * (180/(Math.PI)) ) ;
			
			this.setInternalAngle(angleDegrees);
			
		}
		@Override
		public void setAngleInDegrees( double angle ){
			this.angleDegrees = (float) angle;
			double angleRadians = (angle * ((Math.PI)/180) ) ;
			
			this.updateOrientationVector(angleRadians);
			//this.owner.getEntitySprite().setAngle(angle);
			this.ownerEntity.getGraphicComposite().setGraphicAngle(angleRadians);
			setAngleOfRotateables(angleDegrees);
		}
		@Override
		public void setAngleInRadians( double angleRadians ){
	
			float angleDegrees = (float) (angleRadians * (180/(Math.PI)) ) ;
			
			this.angleDegrees = angleDegrees;
			
			this.updateOrientationVector(angleRadians);
			//this.owner.getEntitySprite().setAngle(angleDegrees);
			this.ownerEntity.getGraphicComposite().setGraphicAngle(angleRadians);
			setAngleOfRotateables(angleDegrees);
		}
	
		public void setAngleFromVector( Vector slope ){
			
			double angleRadians = slope.angleFromVectorInRadians() ;
			this.angleDegrees = (float) (angleRadians * (180/(Math.PI)) ) ;
			this.orientation = slope.unitVector();
	
		}
	
		public void addAngleFromVector( Vector slope ){
			
			double angleRadians = slope.angleFromVectorInRadians() ;
			this.angleDegrees = (float) (angleRadians * (180/(Math.PI)) ) ;
			this.orientation = slope.unitVector();
			
		}
		@Override
		public Point getRotationalPositionRelativeTo( Point relativePosition ){
			double returnX = relativePosition.getX();
			double returnY = relativePosition.getY();
			
			double cosineTheta = Math.cos( Math.toRadians(this.angleDegrees) );
			double sineTheta = Math.sin( Math.toRadians(this.angleDegrees) );
			
			Point returnPoint = new Point(
					-(int)( returnX*cosineTheta - returnY*sineTheta ),
					-(int)( returnX*sineTheta + returnY*cosineTheta )
			);
			
			return returnPoint;
		}
		
		@Override
		public boolean exists() {
			return true;
		}
	
		@Override
		public void disableComposite() {
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
	
	private static class FixedAngleComposite extends AngularComposite{

		private String compositeName = "FixedAngleComposite";

		public void setAngle(double angleRadians) {
			System.err.println("WARNING: Attempted to set angle on fixed angle entity");
		}
		@Override
		public double getAngle() {
			System.err.println("WARNING: Attempted to get angle of fixed angle entity");
			return 0;
		}
		@Override
		public void setAngleInDegrees(double angle) {
			System.err.println("WARNING: Attempted to set angle of fixed angle entity");
		}
		@Override
		public void setAngleInRadians(double angle) {
			System.err.println("WARNING: Attempted to set angle of fixed angle entity");
		}
		@Override
		public void notifyAngleChange(double angle) {
			System.err.println("WARNING: Attempted to notify angle change on fixed angle entity");
		}
		@Override
		public Point getRotationalPositionRelativeTo(Point relativePosition) {
			System.err.println("WARNING: Attempted to get Rotational Position relative to fixed angle entity");
			return relativePosition;
		}		
		@Override
		public boolean exists() {
			return false;
		}

		@Override
		public void disableComposite() {
		}
		@Override
		public Vector getOrientationVector() {
			System.err.println("WARNING: Attempted to get Orientation Vector of fixed angle entity");
			return new Vector(1,0);
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
	
}
