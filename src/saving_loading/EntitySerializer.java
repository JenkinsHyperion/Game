package saving_loading;

import entityComposites.Collider;
import entityComposites.EntityStatic;

public class EntitySerializer {
	
	//BULK OF ENTITYDATA CLASS CONSTRUCTION
	protected static EntityData Serialize( EntityStatic entity ){
		
		//COLLIDER DATA
		ColliderData colliderData = null; //better way of 
		
		if ( entity.getColliderComposite() instanceof Collider ){
			colliderData = new ColliderData( entity.getColliderComposite().getBoundary().getCornersVertex() );
		}
		
		//GRAPHICS DATA
		
		
		
		//-----------------
		
		return new EntityData(
				entity.getPos(),
				colliderData
				
				
		);
		
	}
	
}
