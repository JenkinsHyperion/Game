package entities;

import entityComposites.ColliderNull;
import entityComposites.EntityStatic;

public final class EntityNull extends EntityStatic {
	
	private static EntityNull nullEntity = new EntityNull();

	private EntityNull() {
		super(0,0);
		this.colliderComposite = ColliderNull.nullColliderComposite();
	}
	
	public static EntityNull getNullEntity(){
		return nullEntity;
	}

}
