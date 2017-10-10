package entityComposites;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import physics.Vector;
/**This composite gives an entity an angle, represented by Double: angleDegrees, and Vector: orientationVector 
 * 
 * @author Jenkins
 *
 */

public abstract class AngularComposite implements EntityComposite {
	protected String compositeName = "AngularCompositeAbstract";
	protected EntityStatic ownerEntity;
	private static final FixedAngleComposite fixedAngle = new AngularComposite.FixedAngleComposite();
	
	protected double angleDegrees = 0;
	
	public static FixedAngleComposite getFixedAngleSingleton(){
		return fixedAngle;
	}

	public abstract double getAngleInDegrees();
	public abstract double getAngleInRadians();
	public abstract void setAngleInDegrees( double angle);
	public abstract void setAngleInRadians( double angle);
	public abstract void notifyAngleChange( double angle );
	public abstract Point getRotationalRelativePositionOf( Point absolutePosition);
	public abstract Point getRotationalAbsolutePositionOf( Point relativePosition);
	public abstract Point getRotationalAbsolutePositionOf( Point2D relativePosition);
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
	
	public static class Angled extends AngularComposite{
		private String compositeName = "AngleComposite";
		protected Vector orientation = new Vector( 1 , 0 );
		protected ArrayList<RotateableComposite> rotateableCompositeList = new ArrayList<RotateableComposite>();
		
		public Angled(EntityStatic ownerEnttiy){
			this.ownerEntity = ownerEnttiy;
			orientation = new Vector( 1 , 0 );
		}
		/**Angle in DEGREES
		 * 
		 */
		@Override
		public double getAngleInDegrees(){ return angleDegrees ; }
		@Override
		public double getAngleInRadians(){ return Math.toRadians(angleDegrees) ; }
		@Override
		public Vector getOrientationVector(){ return orientation; }
		
		public EntityStatic getOwnerEntity(){
			return this.ownerEntity;
		}
		
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
			//this.ownerEntity.getGraphicComposite().setGraphicAngle(angleRadians);
			setAngleOfRotateables(angleDegrees);
		}
		@Override
		public void setAngleInRadians( double angleRadians ){
	
			float angleDegrees = (float) (angleRadians * (180/(Math.PI)) ) ;
			
			this.angleDegrees = angleDegrees;
			
			this.updateOrientationVector(angleRadians);
			//this.owner.getEntitySprite().setAngle(angleDegrees);
			//this.ownerEntity.getGraphicComposite().setGraphicAngle(angleRadians);
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
		public Point getRotationalRelativePositionOf( Point absolutePosition ){
			double returnX = absolutePosition.getX();
			double returnY = absolutePosition.getY(); 
			
			double cosineTheta = Math.cos( Math.toRadians(-this.angleDegrees) );
			double sineTheta = Math.sin( Math.toRadians(-this.angleDegrees) );
			
			Point returnPoint = new Point(
					(int)( returnX*cosineTheta - returnY*sineTheta ),
					(int)( returnX*sineTheta + returnY*cosineTheta )
			);
			
			return returnPoint;
		}
		
		@Override
		public Point getRotationalAbsolutePositionOf(Point relativePosition) {
			
			double returnX = relativePosition.getX();
			double returnY = relativePosition.getY(); 
			
			double cosineTheta = Math.cos( Math.toRadians(this.angleDegrees) );
			double sineTheta = Math.sin( Math.toRadians(this.angleDegrees) );
			
			Point returnPoint = new Point(
					(int)( returnX*cosineTheta - returnY*sineTheta ),
					(int)( returnX*sineTheta + returnY*cosineTheta )
			);
			
			return returnPoint;
		}
		
		@Override
		public Point getRotationalAbsolutePositionOf(Point2D relativePosition) {
			
			double returnX = relativePosition.getX();
			double returnY = relativePosition.getY(); 
			
			double cosineTheta = Math.cos( Math.toRadians(this.angleDegrees) );
			double sineTheta = Math.sin( Math.toRadians(this.angleDegrees) );
			
			Point returnPoint = new Point(
					(int)( returnX*cosineTheta - returnY*sineTheta ),
					(int)( returnX*sineTheta + returnY*cosineTheta )
			);
			
			return returnPoint;
		}
		
		@Override
		public boolean exists() {
			return true;
		}
	
		@Override
		public void disableComposite() {
			
			this.ownerEntity.nullifyAngularComposite();
			
			this.ownerEntity.getGraphicComposite().setGraphicAngle(0);	//notify graphic of fixed angle 0

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
		public double getAngleInDegrees() {
			System.err.println("WARNING: Attempted to get angle of fixed angle entity");
			return 0;
		}
		@Override
		public double getAngleInRadians() {
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
		public Point getRotationalRelativePositionOf(Point absolutePosition) {
			return absolutePosition;
		}		
		@Override
		public Point getRotationalAbsolutePositionOf(Point relativePosition) {
			return relativePosition;
		}
		@Override
		public Point getRotationalAbsolutePositionOf(Point2D relativePosition) {
			return new Point((int)relativePosition.getX(), (int)relativePosition.getY());
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
		public EntityStatic getOwnerEntity(){
			return this.ownerEntity;
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
