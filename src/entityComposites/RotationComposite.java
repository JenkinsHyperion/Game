package entityComposites;

import java.awt.Point;

import physics.BoundaryPolygonal;
import physics.BoundaryVertex;
import physics.Vector;

public class RotationComposite {
	
	protected EntityStatic owner;
	
	public RotationComposite( EntityStatic owner ){
		this.owner = owner;
	}
	
	protected void setAngle(double angle){
		System.err.println("Setting angle of null rotation");
	}
	
	protected void addAngle(float angle){
		System.err.println("Setting angle of null rotation");
	}
	
	public void setAngleInDegrees( double angle ){
		System.err.println("Setting angle of null rotation");
	}
	
	public void setAngleInRadians( double angle ){
		System.err.println("Setting angle of null rotation");
	}
	
	public void setAngleFromVector( Vector slope ){
		System.err.println("Setting angle of null rotation");
	}
	
	public void setAngleFromVector( Vector slope , BoundaryVertex rawCorner ){
		System.err.println("Setting angle of null rotation");
	}
	
	public void setAngularVelocity( double angularVelocity ){
		System.err.println("Setting angle of null rotation");
	}
	
	public void setAngularAcceleration( double angularAcc ){
		System.err.println("Setting angle of null rotation");
	}
	
	public float getAngle(){ return 0 ; }
	
	public Vector getOrientationVector(){ return new Vector(0,0); }
	
	public float getAngularVel(){ return 0; }
	
	public float getAngularAcc(){ return 0; }
	
}
