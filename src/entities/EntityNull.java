package entities;

import entityComposites.ColliderNull;

public final class EntityNull extends EntityStatic {
	
	private static EntityStatic nullEntity = new EntityNull();

	private EntityNull() {
		super(0,0);
		this.collisionType = ColliderNull.getNonCollidable();
	}
	
	public static EntityStatic getNullEntity(){
		return nullEntity;
	}

}
