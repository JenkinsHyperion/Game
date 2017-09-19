package testEntities;

import entities.*;
import entityComposites.Collider;
import entityComposites.CompositeFactory;
import physics.BoundaryPolygonal;
import sprites.Sprite;

public class TestRotation extends EntityRotationalDynamic{

	public TestRotation(int x, int y) {
		super(x, y);

		//Collider collidable = new Collider( this );
        //this.setCollisionComposite( collidable );
        //collidable.setBoundary( new BoundaryPolygonal.Box(446,100,-223,-50) );
        
        CompositeFactory.addColliderTo( this , new BoundaryPolygonal.Box(446,100,-223,-50) );

        this.storedBounds = new BoundaryPolygonal.Box(446,100,-223,-50 );
        
        Sprite.Stillframe sprite = new Sprite.Stillframe("ground_1.png" , -223 , -53 );
		
        CompositeFactory.addGraphicTo( this , sprite);
        
        this.angularVelocity = 0.05;
		
		
	}
	
	@Override
	public void updatePosition() {
		super.updatePosition();

		//System.out.println("AngVel"+this.angle);
	}

}
