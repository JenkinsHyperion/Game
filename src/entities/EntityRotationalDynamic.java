package entities;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import physics.Boundary;
import physics.Side;
import entityComposites.*;

public class EntityRotationalDynamic extends EntityDynamic{
	
	protected Boundary storedBounds; //So that rounding errors from rotation don't degrade the vertex locations
	protected float angle = 0;
	protected float angularVelocity = 0;

	public EntityRotationalDynamic(int x, int y) {
		super(x, y);
	}
	
	@Override
    public void setBoundingBox(int xOffset, int yOffset , int width , int height) {
    	
		Boundary boundarytemp =  new Boundary.Box(width, height, xOffset, yOffset);
		
		((Collidable) collisionType).setBoundary( boundarytemp );
		
		storedBounds = boundarytemp;
		boundarytemp = null;
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
		return storedBounds.atPosition( this.getPos() ).rotateBoundaryAround( this.getPos() , angle);
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

        	if ((int)angle>36){angle=-36;} //constrain range from -180 to 180 degrees for convenience
        	else if ((int)angle<-36){angle=36;}

    	
        	((Collidable) collisionType).setBoundary( getBoundaryAtAngle((int)angle * ((2*Math.PI)/72) ) ); 
    	}
    }
	
	public void setAngularVelocity(){
		
	}
	
	public int getAngle(){
		return (int)angle;
	}

	@Override
	public Boundary getBoundaryDelta(){
		return getBoundaryAtAngle((int)(angle+angularVelocity) * ((2*Math.PI)/72)) 
				.atPosition( (int) (x+dx+accX), (int) (y+dy+accY ));
	}
}
