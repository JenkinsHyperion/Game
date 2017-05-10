package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import engine.BoardAbstract;
import physics.*;
import utility.ListNodeTicket;

public class RotationCompositeDynamic extends RotationComposite implements UpdateableComposite{

	private ListNodeTicket updaterSlot;
	
	//protected Boundary storedBounds; //So that rounding errors from rotation don't degrade the vertex locations
	protected float angle = 0;
	protected double angularVelocity = 0;
	protected double angularAcc = 0;
	protected Vector orientation = new Vector( 1 , 0 );
	
	protected ArrayList<RotateableComposite> rotateableCompositeList = new ArrayList<RotateableComposite>();
	
	public RotationCompositeDynamic( EntityStatic owner ){
		super(owner);
		orientation = new Vector( 1 , 0 );
	}
	
	@Override
	public void updateComposite() {
		
		if (angularVelocity != 0){
    		angle = (float) (angle + angularVelocity);

        	if ((int)angle>180){angle=-180;} //constrain range from -180 to 180 degrees for convenience
        	else if ((int)angle<-180){angle=180;}
        	//else if ( Math.abs(angle)<1 ){angle = 0;}
    	
        	this.setInternalAngle(angle);
    	}
    	
    	angularVelocity = angularVelocity + angularAcc;
		
	}
	@Override
	protected void addRotateable( RotateableComposite rotateable ){
		rotateableCompositeList.add( rotateable );
	}
	
	@Override
	public void updateEntity(EntityStatic entity) {
		double angleRadians = (angle * ((Math.PI)/180) ) ;
		this.updateOrientationVector(angleRadians);
		
		for ( RotateableComposite rotateable : rotateableCompositeList ){
			rotateable.setAngle(angleRadians);
		}
		
		entity.getEntitySprite().setAngle(angle);
	}
	
	private void updateOrientationVector( double angleRadians ){
		this.orientation = new Vector( Math.cos(angleRadians) , Math.sin(angleRadians) );
	}
	
	private void setAngleOfRotateables( double angleRadians ){
		for ( RotateableComposite rotateable : rotateableCompositeList ){
			rotateable.setAngle(angleRadians);
		}
	}
	
	private void setInternalAngle(double angle){ //composited method
		double angleRadians = (angle * ((Math.PI)/180) ) ;

		this.setAngleOfRotateables(angleRadians);
		
		this.updateOrientationVector(angleRadians);
		this.owner.getEntitySprite().setAngle(angle);
	}
	
	@Override
	protected void setAngle(double angle){
		double angleRadians = (angle * ((Math.PI)/180) ) ;
		this.updateOrientationVector(angleRadians);
		//((SpriteStillframe)this.getEntitySprite() ).setAngle((int)angle);

	}
	@Override
	protected void addAngleInDegrees(double angle){
		this.angle = (float) (this.angle + angle);
		double angleRadians = (this.angle * ((Math.PI)/180) ) ;

		setInternalAngle( angleRadians + angle );
	}
	@Override
	public void addAngleInRadians(double addRadians){

		this.angle = angle + (float) (addRadians * (180/(Math.PI)) ) ;
		
		this.setInternalAngle(angle);
		
	}
	@Override
	public void setAngleInDegrees( double angle ){
		this.angle = (float) angle;
		double angleRadians = (angle * ((Math.PI)/180) ) ;

		this.setAngleOfRotateables(angleRadians);
		
		this.updateOrientationVector(angleRadians);
		this.owner.getEntitySprite().setAngle(angle);
	}
	@Override
	public void setAngleInRadians( double angleRadians ){

		float angleDegrees = (float) (angleRadians * (180/(Math.PI)) ) ;
		
		this.angle = angleDegrees;
		
		this.setAngleOfRotateables(angleRadians);
		
		this.updateOrientationVector(angleRadians);
		this.owner.getEntitySprite().setAngle(angleDegrees);
	}
	@Override
	public void setAngleFromVector( Vector slope ){
		
		double angleRadians = slope.angleFromVectorInRadians() ;
		this.angle = (float) (angleRadians * (180/(Math.PI)) ) ;
		this.orientation = slope.unitVector();

		this.setAngleOfRotateables(angleRadians);
		

	}
	@Override
	public void addAngleFromVector( Vector slope ){
		
		double angleRadians = slope.angleFromVectorInRadians() ;
		this.angle = (float) (angleRadians * (180/(Math.PI)) ) ;
		this.orientation = slope.unitVector();

		this.setAngleOfRotateables(angleRadians);
		
	}
	
	
	@Override @Deprecated
	public void setAngleFromVector( Vector slope , BoundaryVertex rawCorner ){
		
		//double angleRadians = slope.calculateAngleFromVector() ;
		//Point translation = this.owner.getBoundary().rotateBoundaryFromTemplatePoint( new Point(0,0)  , angleRadians , storedBounds ); 	
		//this.owner.move(translation);
		
		//this.orientation = slope.unitVector().clamp();
	}
	@Override
	public void setAngularVelocity( double angularVelocity ){
		this.angularVelocity = angularVelocity;
	}
	@Override
	public void setAngularAcceleration( double angularAcc ){
		this.angularAcc = angularAcc;
	}
	@Override
	public float getAngle(){ return (float) angle ; }
	@Override
	public Vector getOrientationVector(){ return orientation; }
	@Override
	public float getAngularVel(){ return (float)angularVelocity; }
	@Override
	public float getAngularAcc(){ return (float)angularAcc; }

	@Override
	public void removeUpdateable() {
		this.updaterSlot.removeSelf();
	}

	@Override
	public boolean addCompositeToUpdater(BoardAbstract board) {
		if ( this.updaterSlot == null ){
    		this.updaterSlot = board.addCompositeToUpdater(this);
    		return true;
    	}
    	else{
    		return false;
    	}
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
}
