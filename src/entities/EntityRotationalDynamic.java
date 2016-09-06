package entities;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import physics.Boundary;
import physics.BoundingBox;

public class EntityRotationalDynamic extends EntityDynamic{
	
	protected Boundary storedBounds;
	protected short angle = 0;
	protected int angularVelocity = 0;

	public EntityRotationalDynamic(int x, int y) {
		super(x, y);
		
	}
	
	@Override
    public void setBoundingBox(int x_offset, int y_offset , int width , int height) {
    	
        boundingBox = new Rectangle(x_offset, y_offset, width , height);
        boundary = new BoundingBox(boundingBox);
        storedBounds = boundary;
    }
	
	public void setAngle(double angle){ //OPTIMIZATION TRIG FUNCTIONS ARE NOTORIOUSLY EXPENSIVE Look into performing some trig magic
		// with fast trig approximations

		Line2D[] newSides = new Line2D[storedBounds.getSides().length];
		
		for ( int i = 0 ; i < storedBounds.getSides().length ; i++ ) {
			
			Line2D side = storedBounds.atPosition((int)x,(int) y).getSides()[i];
			Point2D origin = new Point2D.Float((int)x,(int)y);
			
			double r = side.getP1().distance(origin); 
			double a = Math.acos( (side.getX1()-(int)x) / r );
			if (side.getY1() > y){ a = (2*Math.PI) - a ;}
			
			Point2D p1 = new Point2D.Float( (float)(r * Math.cos( a + angle  )  ) , (float)(r * Math.sin( a + angle ) )    );
			
			double r2 = side.getP2().distance(origin);
			double a2 = Math.acos( (side.getX2()-(int)x) / r );
			if (side.getY2() > y){ a2 = (2*Math.PI) - a2 ;}
			
			Point2D p2 = new Point2D.Float( (float)(r2 * Math.cos( a2 + angle  ) ) , (float)(r2 * Math.sin( a2 + angle  ) )  );
		
			newSides[i] = new Line2D.Float(p1,p2);
			
		}
		
		boundary = new Boundary(newSides);
		
	}
	
	@Override
	public void updatePosition() {
		
		//angular velocity
    	
    	x += dx;
    	y += dy;
    	
    	dx += accX;
    	dy += accY;
    	
    }
	
	public void setAngularVelocity(){
		
	}
	
	public int getAngle(){
		return angle;
	}

}
