package testEntities;

import entities.*;
import entityComposites.Collider;
import entityComposites.CompositeFactory;
import physics.Boundary;
import sprites.SpriteStillframe;

public class TestRotation extends EntityRotationalDynamic{

	public TestRotation(int x, int y) {
		super(x, y);

		Collider collidable = new Collider( this );
        this.setCollisionComposite( collidable );
        
        collidable.setBoundary( new Boundary.Box(446,100,-223,-50) );

        this.storedBounds = new Boundary.Box(446,100,-223,-50 );
        
        SpriteStillframe sprite = new SpriteStillframe("ground_1.png" , -223 , -53 );
		
        CompositeFactory.addGraphicTo( this , sprite);
        
        this.angularVelocity = 0.05;
		
		
	}
	
	@Override
	public void updatePosition() {
		super.updatePosition();

		//System.out.println("AngVel"+this.angle);
	}

}
