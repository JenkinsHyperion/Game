package physics;

import entityComposites.Collider;
import sprites.RenderingEngine;

public abstract class CollisionFactory {
	
	private static final CollisionFactory.DynamicStatic dynamicStatic = new DynamicStatic();
	private static final CollisionFactory.DynamicStaticRigidless rigidlessDynamicStatic = new DynamicStaticRigidless();

	abstract Collision createVisualCollision( Collider collider1 , Collider collider2, VisualCollisionCheck check, RenderingEngine engine);
	
	protected static CollisionFactory dynamicStatic(){ return dynamicStatic; }
	protected static CollisionFactory rigidlessDynamicStatic(){ return rigidlessDynamicStatic; }
	
	protected static class DynamicStaticRigidless extends CollisionFactory{

		@Override
		Collision createVisualCollision(Collider collider1, Collider collider2, VisualCollisionCheck check,
				RenderingEngine engine) {
			
			return new Collision.BasicCheck( collider1, collider2, check );
		}
		
	}
	
	public static class DynamicStatic extends CollisionFactory{

		@Override
		Collision createVisualCollision ( Collider collider1 , Collider collider2, VisualCollisionCheck check, RenderingEngine engine ) {
			return new VisualCollisionDynamicStatic( 
					collider1 , collider2 , 
					check.axisCollector ,
					engine
					); 
		}
	}

	
}
