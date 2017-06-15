package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import physics.Vector;
/**This composite gives an entity an angle, represented by Double: angleDegrees, and Vector: orientationVector 
 * 
 * @author Jenkins
 *
 */
public abstract class AngularComposite implements EntityComposite, RotateableComposite{

	private static final FixedAngleComposite fixedAngle = new AngularComposite.FixedAngleComposite();
	
	protected double angleDegrees = 0;
	
	public static FixedAngleComposite getFixedAngleSingleton(){
		return fixedAngle;
	}
	
	public abstract double getAngle();
	public abstract void setAngleInDegrees( double angle);
	public abstract void setAngleInRadians( double angle);
	public abstract Point getRotationalPositionRelativeTo( Point relativePosition);
	public abstract Vector getOrientationVector();
	
	public static class AngleComposite extends AngularComposite{
		
		protected EntityStatic ownerEntity;
		protected Vector orientation = new Vector( 1 , 0 );
		protected ArrayList<RotateableComposite> rotateableCompositeList = new ArrayList<RotateableComposite>();
		
		public AngleComposite(EntityStatic ownerEnttiy){
			this.ownerEntity = ownerEnttiy;
			orientation = new Vector( 1 , 0 );
		}
		@Override
		public void setAngle( double angleDegrees ){
			this.angleDegrees = angleDegrees;
			this.setAngleInDegrees(angleDegrees); //FIXME MAKE ROTATEABLE GRAPHICS AND COLLIDER VARIANTS TO BE USED IN ROTATEABLES LIST
			setAngleOfRotateables(angleDegrees);
		}
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
		
		private void setInternalAngle(double angle){ //composited method
			double angleRadians = (angle * ((Math.PI)/180) ) ;
			
			this.updateOrientationVector(angleRadians);
			this.ownerEntity.getEntitySprite().setAngle(angle);
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
		}
		@Override
		public void setAngleInRadians( double angleRadians ){
	
			float angleDegrees = (float) (angleRadians * (180/(Math.PI)) ) ;
			
			this.angleDegrees = angleDegrees;
			
			this.updateOrientationVector(angleRadians);
			//this.owner.getEntitySprite().setAngle(angleDegrees);
			this.ownerEntity.getGraphicComposite().setGraphicAngle(angleRadians);
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
		public void disable() {
			// TODO Auto-generated method stub
		}
	
	}
	
	private static class FixedAngleComposite extends AngularComposite{

		@Override
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
		public Point getRotationalPositionRelativeTo(Point relativePosition) {
			System.err.println("WARNING: Attempted to get Rotational Position relative to fixed angle entity");
			return relativePosition;
		}		
		@Override
		public boolean exists() {
			return false;
		}

		@Override
		public void disable() {
		}
		@Override
		public Vector getOrientationVector() {
			System.err.println("WARNING: Attempted to get Orientation Vector of fixed angle entity");
			return new Vector(1,0);
		}
		
	}
	
}
