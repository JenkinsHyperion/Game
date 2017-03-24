package misc;

import entities.EntityDynamic;
import entityComposites.EntityStatic;

public class LinearFollow extends MovementBehavior{
	
	private EntityStatic target;

	public LinearFollow(EntityDynamic owner, EntityStatic target){
		this.owner = owner;
		this.target = target;
	}
	
	@Override
	public void updateAIPosition() {
		
		this.owner.setDX( (float)( this.target.getX() - this.owner.getX() ) /30 );
		this.owner.setDY( (float)( this.target.getY() - this.owner.getY() ) /30 );
		
	}

}
