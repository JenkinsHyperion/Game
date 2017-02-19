package testEntities;

import java.awt.Point;

import entities.EntityRotationalDynamic;
import entityComposites.Collidable;
import physics.PointForce;
import physics.Vector;

public class TestHinge extends EntityRotationalDynamic{
	
	Point point;
	
	PointForce hingeNormal;

	public TestHinge(int x, int y , Point axis) {
		super(x, y); 

		this.point = axis;
		init();
		
	}

	private void init(){
		
		hingeNormal = this.addPointForce( new Vector(0,0.01) , new Point(1,0) );
	}
	
	@Override
	public void updatePosition() {
		super.updatePosition();
		//axis.setLocation(endPointX, endPointY);
		//hingeNormal.setPoint(axis);
		//TO BE MOVED TO RIGIDBODY COMPOSITE
		this.applyAllForces();
	}
	
	public Point getPointLocal(){

		int radians = (int)Math.toRadians( angle );
		
		int rotatedX = (int)( Math.cos(angle) * (point.x - this.x) - Math.sin(angle) * (point.y-this.y) + this.x );
		int rotatedY = (int)( Math.sin(angle) * (point.x - this.x) + Math.cos(angle) * (point.y - this.y) + this.y );
		
		return new Point( (int)( rotatedX + x ) , (int)( rotatedY +y ) );
	}

}
