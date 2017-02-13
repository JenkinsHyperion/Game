package testEntities;

import java.awt.Point;

import entities.EntityRotationalDynamic;
import entityComposites.Collidable;

public class TestHinge extends EntityRotationalDynamic{
	
	Point axis;

	public TestHinge(int x, int y , Point axis) {
		super(x, y); 

		this.axis = axis;
		
	}
	
	
	@Override
	public void updatePosition() {
		super.updatePosition();
		
		//TO BE MOVED TO RIGIDBODY COMPOSITE
		this.accX = (float) ((Collidable)collisionType).sumOfForces().getX();
    	this.accY = (float) ((Collidable)collisionType).sumOfForces().getY();
	}

}
