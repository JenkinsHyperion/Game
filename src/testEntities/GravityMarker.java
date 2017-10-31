package testEntities;

import java.awt.Graphics2D;
import java.awt.Point;

import engine.MovingCamera;
import entityComposites.Collider;
import entityComposites.EntityStatic;
import physics.BoundaryCircular;
import physics.Collision;
import physics.CollisionDispatcher;
import physics.Force;
import physics.Vector;
import physics.VisualCollisionCheck;
import sprites.RenderingEngine;

public class GravityMarker extends EntityStatic{

	private int radius;
	private double falloffFactor;
	
	public GravityMarker(int x, int y, int radius) {
		super(x, y);
		this.radius = radius;
		init();
	}
	
	public GravityMarker(String name , int x, int y, int radius ) {
		super(name, x, y);
		this.radius = radius;
		init();
	}
	
	public GravityMarker(String name , Point p, int radius ) {
		super(name, p.x, p.y);
		this.radius = radius;
		init();
	}
	
	private void init(){
        this.addInitialColliderTo(new BoundaryCircular(radius));

	}
	
	public void setFalloff( double surfaceGravity , int surfaceRadius ){//calculate fall-off equation, in this case linear fall-off
        
		this.falloffFactor = surfaceGravity / (radius - surfaceRadius); 
	}

	public Vector calculateGravityAtDistance( int radius ){
		return null;
	}

	public static class CircularGravityField extends CollisionDispatcher<EntityStatic, GravityMarker>{

		@Override
		public Collision createVisualCollision(EntityStatic entity1, Collider collider1, GravityMarker gravityMarker,
				Collider collider2, VisualCollisionCheck check, RenderingEngine engine) {
			// TODO Auto-generated method stub
			return new Collision.CustomType<EntityStatic, GravityMarker>(entity1, collider1, gravityMarker, collider2) {

				private Force gravity;
				private int upperRadius;
				
				@Override
				protected void initializeCollision() {
					gravity = entity1.getTranslationComposite().registerGravityForce();	//add new force to entity
					this.upperRadius = gravityMarker.radius;		//store gravity markers radius for gravity fall-off purposes
				}

				@Override
				protected void updateCollision() {
					
					final Vector separation = entity1.getSeparationVector(gravityMarker);
					
					final double distance = separation.getIntegerMagnitude();
					
					final double magnitude = (upperRadius-distance)*gravityMarker.falloffFactor ; //OPT inline
					
					if ( distance < upperRadius ){
						gravity.setVector( separation.unitVector().multiply(magnitude) );
					}else{
						gravity.setVector( 0,0 );
					}
					
					this.isComplete = !check.check(collider1, collider2);
				}

				@Override
				public void updateVisualCollision(MovingCamera camera, Graphics2D gOverlay) {
					this.isComplete = check.check(collider1, collider2);
				}

				@Override
				public void completeCollision() {
					entity1.getTranslationComposite().unregisterGravityForce(this.gravity);
				}
				
				
			};
		}
	}
	
}
