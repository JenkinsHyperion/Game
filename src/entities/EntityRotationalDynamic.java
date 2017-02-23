package entities;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import physics.Boundary;
import physics.Force;
import physics.PointForce;
import physics.Side;
import physics.Vector;
import sprites.SpriteStillframe;
import entityComposites.*;
import misc.CollisionEvent;

public class EntityRotationalDynamic extends EntityDynamic{
	
	protected Boundary storedBounds; //So that rounding errors from rotation don't degrade the vertex locations
	protected float angle = 0;
	protected double angularVelocity = 0;
	protected double angularAcc = 0;
	protected Vector orientation = new Vector( 1 , 0 );

	public EntityRotationalDynamic(int x, int y) {
		super(x, y);
		
	}
	
	
	
	public Boundary getBoundaryAtAngle(double angle){ //OPTIMIZATION TRIG FUNCTIONS ARE NOTORIOUSLY EXPENSIVE Look into performing some trig magic
		// with fast trig approximations

		/*Side[] newSides = new Side[storedBounds.getSides().length];
		
		for ( int i = 0 ; i < storedBounds.getSides().length ; i++ ) {
			
			Side side = storedBounds.atPosition((int)x,(int) y).getSides()[i];
			Point2D origin = new Point2D.Float((int)x,(int)y);
			
			double r = side.getP1().distance(origin); 
			double a = Math.acos( (side.getX1()-(int)x) / r );
			if (side.getY1() > y){ a = (2*Math.PI) - a ;}
			
			Point2D p1 = new Point2D.Float( (float)(r * Math.cos( a + angle  )  ) , (float)(r * Math.sin( a + angle ) )    );
			
			double r2 = side.getP2().distance(origin);
			double a2 = Math.acos( (side.getX2()-(int)x) / r );
			if (side.getY2() > y){ a2 = (2*Math.PI) - a2 ;}
			
			Point2D p2 = new Point2D.Float( (float)(r2 * Math.cos( a2 + angle  ) ) , (float)(r2 * Math.sin( a2 + angle  ) )  );
		
			newSides[i] = new Side( new Line2D.Float(p1,p2) , i );
			
		}
		
		return new Boundary(newSides);*/
		//return storedBounds.atPosition( this.getPos() ).rotateBoundaryAround( this.getPos() , angle);
		
		return this.getBoundary();
		
	}
	
	@Override
	public void updatePosition() {
		
		//angular velocity
    	
    	x += dx;
    	y += dy;
    	
    	dx += accX;
    	dy += accY;
    	
    	if (angularVelocity != 0){
    		angle = (float) (angle + angularVelocity);

        	if ((int)angle>180){angle=-180;} //constrain range from -180 to 180 degrees for convenience
        	else if ((int)angle<-180){angle=180;}
    	
        	this.setAngle(angle);
    	}
    	
    	angularVelocity = angularVelocity + angularAcc;
    	
    }
	
	private void setAngle(double angle){
		double angleRadians = (angle * ((Math.PI)/180) ) ;
		this.orientation = new Vector( Math.cos(angleRadians) , Math.sin(angleRadians) );
		((SpriteStillframe)this.getEntitySprite() ).setAngle((int)angle);
	}
	
	public void setAngleInDegrees( float angle ){
		double angleRadians = (angle * ((Math.PI)/180) ) ;
		this.angle = (float) angleRadians;
		//this.getBoundary().rotateBoundaryFromTemplate( new Point(0,0) , angleRadians , storedBounds ); 
		this.orientation = new Vector( Math.cos(angleRadians) , Math.sin(angleRadians) );
		((SpriteStillframe)this.getEntitySprite() ).setAngle((int)angle);
	}
	
	public void setAngleInRadians( double angle ){
		System.out.println("Setting angle " +angle);
		this.angle = (float) angle;
		this.getBoundary().rotateBoundaryFromTemplate( new Point(0,0) , angle , storedBounds ); 
		this.orientation = new Vector( Math.cos(angle) , Math.sin(angle) );
		
	}
	
	public void setAngularVelocity( double angularVelocity ){
		this.angularVelocity = angularVelocity;
	}
	
	public void setAngularAcceleration( double angularAcc ){
		this.angularAcc = angularAcc;
	}
	
	public float getAngle(){ return (float) (angle ); }
	
	public Vector getOrientationVector(){ return orientation; }
	
	public float getAngularVel(){ return (float)angularVelocity; }
	
	public float getAngularAcc(){ return (float)angularAcc; }

	@Override
	public void applyAllForces() {
		
		Vector returnLinearAcc = new Vector(0,0);
		
    	for ( Force force : forces ){
    		Vector acc = force.getLinearForce();
    		
    		returnLinearAcc = returnLinearAcc.add(acc);
    		
    		//accX = (float)acc.getX();
    		//accY = (float)acc.getY();
    	}

    	for ( PointForce force : pointForces ){
    		
    		Vector acc = force.getLinearForce();
    		returnLinearAcc = returnLinearAcc.add(acc);
    		
    		double DA = force.getTorque( );
    		this.angularAcc = DA/10;
    			
    	}
    	
		accX = (float)returnLinearAcc.getX();
		accY = (float)returnLinearAcc.getY();

	}
	
}
