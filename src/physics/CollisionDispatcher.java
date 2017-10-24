package physics;

import java.awt.Graphics2D;

import engine.MovingCamera;
import entityComposites.Collider;
import entityComposites.EntityStatic;
import sprites.RenderingEngine;
import testEntities.GravityMarker;

/**gsds
 * 
 * @author Jenkins
 *
 * @param <EntityTypePrimary>
 * @param <EntityTypeSecondary>
 */
public abstract class CollisionDispatcher< EntityTypePrimary extends EntityStatic, EntityTypeSecondary extends EntityStatic> {
	
	public static final CollisionDispatcher.DynamicStatic<?,?> DYNAMIC_STATIC = new DynamicStatic<>();
	public static final CollisionDispatcher.DynamicStaticRigidless<?,?> RIGIDLESS_DYNAMIC_STATIC = new DynamicStaticRigidless<>();
	public static final CollisionDispatcher.CircularGravityField<?,?> CIRCULAR_GRAVITY_FIELD = new CircularGravityField<>();

	public abstract Collision createVisualCollision( EntityTypePrimary entityInstance1 , Collider colliderInstance1, EntityTypeSecondary entityInstance2, Collider colliderInstance2, VisualCollisionCheck check, RenderingEngine engine);
	
	protected static class DynamicStaticRigidless<E1 extends EntityStatic, E2 extends EntityStatic>  extends CollisionDispatcher<EntityStatic, EntityStatic>{

		@Override
		public Collision createVisualCollision(EntityStatic entityInstance1, Collider collider1, EntityStatic entityInstance2,
				Collider collider2, VisualCollisionCheck check, RenderingEngine engine) {
			
			return new Collision.BasicCheck( collider1, collider2, check );
		}
		
		@Override
		public String toString() {
			return "BASIC FIELD COLLISION";
		}
	}
	
	public static class CircularGravityField<E1 extends EntityStatic, E2 extends GravityMarker>  extends CollisionDispatcher<EntityStatic, EntityStatic>{

		@Override
		public Collision createVisualCollision(EntityStatic entity1, Collider collider1, EntityStatic entity2,
				Collider collider2, VisualCollisionCheck check, RenderingEngine engine) {
			// TODO Auto-generated method stub
			return new Collision.CustomType<EntityStatic, EntityStatic>(entity1, collider1, entity2, collider2) {

				private Force gravity;
				
				@Override
				protected void initializeCollision() {
					gravity = entity1.getTranslationComposite().registerGravityForce(new Vector(0,0));
				}

				@Override
				protected void updateCollision() {
					
					gravity.setVector( entity1.getSeparationUnitVector(entity2).multiply(0.2) );
					
					this.isComplete = !check.check(collider1, collider2);
				}

				@Override
				public void updateVisualCollision(MovingCamera camera, Graphics2D gOverlay) {
					this.isComplete = check.check(collider1, collider2);
				}

				@Override
				public void completeCollision() {
					entity1.getTranslationComposite().unregisterGravityForce(gravity);
				}
				
				
			};
		}
	}
	
	public static class DynamicStatic<E1 extends EntityStatic, E2 extends EntityStatic> extends CollisionDispatcher<EntityStatic, EntityStatic>{

		@Override
		public Collision createVisualCollision ( EntityStatic entity1 , Collider collider1, EntityStatic entity2, Collider collider2, VisualCollisionCheck check, RenderingEngine engine ) {
			return new CollisionRigidDynamicStatic( 
					collider1 , collider2 , 
					check.axisCollector 
					); 
		}
		
		@Override
		public String toString() {
			return "RIGID DYNAMIC STATIC";
		}
	}
	
	
	
}
