package physics;

import entityComposites.Collider;
import sprites.RenderingEngine;

public abstract class CollisionBuilder {
	
	public static final CollisionBuilder.DynamicStatic DYNAMIC_STATIC = new DynamicStatic();
	public static final CollisionBuilder.DynamicStaticRigidless RIGIDLESS_DYNAMIC_STATIC = new DynamicStaticRigidless();

	public abstract Collision createVisualCollision( Collider collider1 , Collider collider2, VisualCollisionCheck check, RenderingEngine engine);
	
	protected static class DynamicStaticRigidless extends CollisionBuilder{

		@Override
		public Collision createVisualCollision(Collider collider1, Collider collider2, VisualCollisionCheck check,
				RenderingEngine engine) {
			
			return new Collision.BasicCheck( collider1, collider2, check );
		}
		
	}
	
	public static class DynamicStatic extends CollisionBuilder{

		@Override
		public Collision createVisualCollision ( Collider collider1 , Collider collider2, VisualCollisionCheck check, RenderingEngine engine ) {
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
	
	public static class Custom extends CollisionBuilder{

		private final Collision customCollision;
		
		public Custom( Collision customCollision ){
			this.customCollision = customCollision;
		}
		
		@Override
		public Collision createVisualCollision(Collider collider1, Collider collider2, VisualCollisionCheck check,
				RenderingEngine engine) {
			
			return customCollision;
			
		}
		
	}

	
}
