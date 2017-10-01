package physics;

import entityComposites.Collider;
import entityComposites.EntityStatic;
import sprites.RenderingEngine;

public abstract class CollisionBuilder< E1 extends EntityStatic, E2 extends EntityStatic> {
	
	public static final CollisionBuilder.DynamicStatic DYNAMIC_STATIC = new DynamicStatic();
	public static final CollisionBuilder.DynamicStaticRigidless RIGIDLESS_DYNAMIC_STATIC = new DynamicStaticRigidless();

	public abstract Collision createVisualCollision( E1 entity1 , Collider collider1, E2 entity2, Collider collider2, VisualCollisionCheck check, RenderingEngine engine);
	
	protected static class DynamicStaticRigidless extends CollisionBuilder<EntityStatic, EntityStatic>{

		@Override
		public Collision createVisualCollision(EntityStatic entity1, Collider collider1, EntityStatic entity2,
				Collider collider2, VisualCollisionCheck check, RenderingEngine engine) {
			
			return new Collision.BasicCheck( collider1, collider2, check );
		}
		
	}
	
	public static class DynamicStatic extends CollisionBuilder<EntityStatic, EntityStatic>{

		@Override
		public Collision createVisualCollision ( EntityStatic entity1 , Collider collider1, EntityStatic entity2, Collider collider2, VisualCollisionCheck check, RenderingEngine engine ) {
			return new VisualCollisionRigidDynamicStatic( 
					collider1 , collider2 , 
					check.axisCollector ,
					engine
					); 
		}
		
		@Override
		public String toString() {
			return "DYNAMIC_STATIC";
		}
	}
	
	/*public abstract static class CustomTyped<E1 extends EntityStatic,E2 extends EntityStatic> extends CollisionBuilder<E1,E2>{

		private final Collision customCollision;
		
		public CustomTyped( Collision customCollision ){
			this.customCollision = customCollision;
		}
		
		@Override
		public Collision createVisualCollision(E1 entity1, Collider collider1, E2 entity2,
				Collider collider2, VisualCollisionCheck check, RenderingEngine engine) {
			
			return customCollision;
			
		}
		
	}*/
	
}
