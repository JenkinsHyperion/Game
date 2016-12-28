package misc;

import entities.EntityStatic;
import entityComposites.Collidable;

public class DefaultCollisionEvent extends CollisionEvent{
	
	public DefaultCollisionEvent( Collidable owner ){
		this.owner = owner;
		this.name = "defaultEvent";
	}
	
	@Override
	public void run( ) {
		
		this.owner.onCollisionEvent();
		
	}

}
