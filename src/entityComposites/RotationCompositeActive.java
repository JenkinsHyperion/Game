package entityComposites;

import java.awt.Point;

import physics.*;

public class RotationCompositeActive extends RotationComposite{

	protected Boundary storedBounds; //So that rounding errors from rotation don't degrade the vertex locations
	protected float angle = 0;
	protected double angularVelocity = 0;
	protected double angularAcc = 0;
	protected Vector orientation = new Vector( 1 , 0 );
	
	public RotationCompositeActive( EntityStatic owner ){
		super(owner);
		this.storedBounds = owner.getBoundary().temporaryClone();
		orientation = new Vector( 1 , 0 );
	}
	
	@Override
	protected void setAngle(double angle){
		double angleRadians = (angle * ((Math.PI)/180) ) ;
		this.orientation = new Vector( Math.cos(angleRadians) , Math.sin(angleRadians) );
		//((SpriteStillframe)this.getEntitySprite() ).setAngle((int)angle);

	}
	@Override
	protected void addAngle(float angle){
		this.angle = this.angle + angle;
		double angleRadians = (this.angle * ((Math.PI)/180) ) ;
		this.owner.getBoundary().rotateBoundaryFromTemplate( new Point(0,0) , angleRadians , storedBounds ); 
		this.orientation = new Vector( Math.cos(angleRadians) , Math.sin(angleRadians) );
	}
	@Override
	public void setAngleInDegrees( double angle ){
		double angleRadians = (angle * ((Math.PI)/180) ) ;
		this.owner.getBoundary().rotateBoundaryFromTemplate( new Point(0,0) , angleRadians , storedBounds ); 
		this.orientation = new Vector( Math.cos(angleRadians) , Math.sin(angleRadians) );
		this.owner.getEntitySprite().setAngle(angle);
	}
	@Override
	public void setAngleInRadians( double angle ){
		this.owner.getBoundary().rotateBoundaryFromTemplate( new Point(0,0) , angle , storedBounds ); 
		this.orientation = new Vector( Math.cos(angle) , Math.sin(angle) );
		//this.angle = (float) angle;
		//this.getEntitySprite().setAngle((int)angle);
	}
	@Override
	public void setAngleFromVector( Vector slope ){
		
		double angleRadians = slope.angleFromVectorInRadians() ;
		System.out.println( "------------------------ANGLE "+angleRadians*180/Math.PI );
		this.owner.getBoundary().rotateBoundaryFromTemplate( new Point(0,0) , angleRadians , storedBounds ); 
		this.orientation = slope.unitVector().clamp();
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
	
}
