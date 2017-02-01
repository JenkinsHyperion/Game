package entities;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import physics.Boundary;
import physics.Side;
import entityComposites.*;
import misc.CollisionEvent;

public class EntityRotationalDynamic extends EntityDynamic{
	
	protected Boundary storedBounds; //So that rounding errors from rotation don't degrade the vertex locations
	protected float angle = 0;
	protected double angularVelocity = 0;
	protected double angularAcc = 0;

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
    		angle += angularVelocity;

        	if ((int)angle>180){angle=-180;} //constrain range from -180 to 180 degrees for convenience
        	else if ((int)angle<-180){angle=180;}

    	
        	this.setAngleInDegrees((int)angle);
    	}
    	
    	angularVelocity += angularAcc;
    	
    }
	
	public void setAngleInDegrees( float angle ){
		this.angle = (float) angle;
		this.getBoundary().rotateBoundaryFromTemplate( new Point(0,0) , (angle * ((Math.PI)/180) ) , storedBounds ); 
	}
	
	public void setAngleInRadians( double angle ){
		this.angle = (float) angle;
		this.getBoundary().rotateBoundaryFromTemplate( new Point(0,0) , angle , storedBounds ); 
	}
	
	public void setAngularVelocity( double angularVelocity ){
		this.angularVelocity = angularVelocity;
	}
	
	public void setAngularAcceleration( double angularAcc ){
		this.angularAcc = angularAcc;
	}
	
	public float getAngle(){ return (float) (angle * (180/(Math.PI))); }
	
	public float getAngularVel(){ return (float)angularVelocity; }
	
	public float getAngularAcc(){ return (float)angularAcc; }

	/*@Override
	public Boundary getBoundaryDelta(){
		return getBoundaryAtAngle((int)(angle+angularVelocity) * ((2*Math.PI)/72)) 
				.atPosition( (int) (x+dx+accX), (int) (y+dy+accY ));
	}*/
}
