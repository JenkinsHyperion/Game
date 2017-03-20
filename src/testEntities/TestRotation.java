package testEntities;

import entities.*;
import entityComposites.Collider;
import entityComposites.EntityComposite;
import physics.Boundary;
import sprites.SpriteStillframe;

public class TestRotation extends EntityRotationalDynamic{

	public TestRotation(int x, int y) {
		super(x, y);

		Collider collidable = new Collider( this );
        this.setCollisionProperties( collidable );
        
        collidable.setBoundary( new Boundary.Box(446,100,-223,-50 , collidable) );

        this.storedBounds = new Boundary.Box(446,100,-223,-50 , collidable);
        
        SpriteStillframe sprite = new SpriteStillframe("ground_1.png" , -223 , -53 );
		
        EntityComposite.addGraphicTo( this , sprite);
        
        this.angularVelocity = 0.05;
		
		
	}
	
	@Override
	public void updatePosition() {
		super.updatePosition();

		//System.out.println("AngVel"+this.angle);
	}

}