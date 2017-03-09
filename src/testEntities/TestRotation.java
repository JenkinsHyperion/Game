package testEntities;

import entities.*;
import entityComposites.Collidable;
import physics.Boundary;

public class TestRotation extends EntityRotationalDynamic{

	public TestRotation(int x, int y) {
		super(x, y);

		Collidable collidable = new Collidable( this );
        this.setCollisionProperties( collidable );
        
        collidable.setBoundary( new Boundary.Box(446,100,-223,-50 , collidable) );

        this.storedBounds = new Boundary.Box(446,100,-223,-50 , collidable);
        
        this.loadSprite("missing.png" , -223 , -53 );
		
        this.angularVelocity = 0.05;
		
		
	}
	
	@Override
	public void updatePosition() {
		super.updatePosition();

		//System.out.println("AngVel"+this.angle);
	}

}
