package testEntities;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import com.studiohartman.jamepad.ControllerState;

import engine.BoardAbstract;
import engine.MovingCamera;
import engine.TestBoard;
import entityComposites.Collider;
import entityComposites.CompositeFactory;
import entityComposites.EntityBehaviorScript;
import entityComposites.EntityStatic;
import physics.Boundary;
import physics.BoundaryCircular;
import physics.BoundarySingular;
import physics.Collision;
import physics.CollisionDispatcher;
import physics.VisualCollisionCheck;
import sprites.RenderingEngine;
import sprites.Sprite;
import utility.PerlinNoiseGenerator;
import utility.Probability;

public class Asteroid extends EntityStatic{
	
	private static TestBoard board;

	private static final Sprite.Stillframe grass01 = new Sprite.Stillframe("Prototypes/grasstest01.png",0,2,Sprite.CENTERED_BOTTOM);
	private static final Sprite.Stillframe grass02 = new Sprite.Stillframe("Prototypes/grasstest02.png",0,2,Sprite.CENTERED_BOTTOM);
	private static final Sprite.Stillframe grass03 = new Sprite.Stillframe("Prototypes/grasstest03.png",0,2,Sprite.CENTERED_BOTTOM);
	private static final Sprite.Stillframe clover1 = new Sprite.Stillframe("Prototypes/clover.png",0,2,Sprite.CENTERED_BOTTOM ,75);

	private static final Sprite.Stillframe asteroid01 = new Sprite.Stillframe("asteroid02.png", Sprite.CENTERED );
	private static final Sprite.Stillframe asteroid02 = new Sprite.Stillframe("asteroid_test_02.png", Sprite.CENTERED );
	
	private static final Sprite.Stillframe[] grassList01 = new Sprite.Stillframe[]{
			
		new Sprite.Stillframe("Prototypes/grass01_01.png",0,0,Sprite.CENTERED_BOTTOM,50),		
		new Sprite.Stillframe("Prototypes/grass01_02.png",0,0,Sprite.CENTERED_BOTTOM,50),
		new Sprite.Stillframe("Prototypes/grass01_03.png",0,0,Sprite.CENTERED_BOTTOM,50)
			
	};
	
	public static final int PRESET01 = 1;
	public static final int PRESET02 = 2;
	public static final int PRESET03 = 3;
	public static final int PRESET04 = 4;

	private static int entityCount = 0;
	
	private Sprite.Stillframe[] grassSpritesList;
	
	private Sprite.Stillframe asteroidSprite;
	
	private final int radius;
	
	
	private final ArrayList<Point> flowerNodes = new ArrayList<Point>();
	
	
	public Asteroid(int x, int y, int radius, TestBoard board, int...preset) {
		super("Asteroid "+radius+" "+entityCount,x, y);
		++entityCount;
		this.board = board;
		this.radius = radius;

		grassSpritesList = new Sprite.Stillframe[1];
		
		if ( preset.length > 0 ){
			if ( preset[0] == 1 ){
				asteroidSprite = asteroid01;
				grassSpritesList[0] = grass01;
			}
			else if( preset[0] == 2 ){
				asteroidSprite = asteroid02;
				grassSpritesList[0] = grass02;
			}
			else if( preset[0] == 3 ){
				asteroidSprite = asteroid01;
				grassSpritesList[0] = grass03;
			}
			else if( preset[0] == 4 ){
				asteroidSprite = asteroid01;
				grassSpritesList = grassList01;
			}
		}
		else{
			asteroidSprite = asteroid01;
			grassSpritesList[0] = grass01;
		}

		CompositeFactory.addGraphicTo(this, asteroidSprite, false );

		this.getGraphicComposite().setGraphicSizeFactor(0.334 * (radius/500.0));
        //CompositeFactory.addTranslationTo(asteroid);
        //CompositeFactory.addDynamicRotationTo(this);

        Boundary bounds1 = new BoundaryCircular(radius);
        //CompositeFactory.addColliderTo(asteroid,  new BoundaryLinear( new Line2D.Double( 0 , 100 , 0, -100 ) ) );
        //CompositeFactory.addColliderTo(asteroid,  new BoundaryPolygonal.Box(100, 200, -50, -100) );
        
        CompositeFactory.addRotationalColliderTo(
        		this, 
        		bounds1, 
        		this.getAngularComposite()
        		);
        
        CompositeFactory.addRigidbodyTo(this);
		
        
        flowerNodes.add(new Point(100,0));
        
	}

	public Point getFlowerNode( int i ){
		return flowerNodes.get(i);
	}
	
	public void spawnGrass(){

		//final int resolution = (int)( 6/(radius/500.0)); //angle in degrees
		
		final int resolution = 30;
		
		final int circumfrence = (int)(2.0*Math.PI*radius);
		
		final double resolutionAngle =( 360.0 / (circumfrence / resolution) );
		
		System.err.println("RESOLUTION "+360.0/(circumfrence/6.0));
		int[] perlinGen01 = PerlinNoiseGenerator.perlinNoise(360, 10, 100);
		
		for ( int i = 0 ; i < 360 ; ++i ){
			
			double keySize = (20+perlinGen01[i])/100.0;
			
			if ( (int)(i % resolutionAngle) == 0 ){
	
				if ( grassSpritesList.length > 1 ){
				
					int index = Probability.randomInt(0, grassSpritesList.length);
					spawnSingleGrass( i, keySize , this.grassSpritesList[index] );
				}
				else{
					spawnSingleGrass( i, keySize , this.grassSpritesList[0] );
				}
				
			}
			else{
				
//				if ( i % resolutionAngle == resolutionAngle/2 ){
//				
//					if ( 0.5 > (20+perlinGen01[i])/100.0 ){
//						spawnSingleGrass( i, keySize ,grassSprite);
//					}
//				}
			}
		}
		
	}
	
	public void plantEntityOnSurface( EntityStatic entity, double angle ){
		
		Point position = new Point( 
				(int)(radius*Math.cos(Math.toRadians(angle-90))) + this.getX(), 
				(int)(radius*Math.sin(Math.toRadians(angle-90))) + this.getY()
				);
		
		entity.setCompositedPos(position);
		entity.getAngularComposite().setAngleInDegrees(angle);

	}
	
	public void spawnPresetBush( double relativeAngle, double sizeFactor, int flag ){
		
		Sprite.Stillframe presetSprite;
		
		if( flag == 1 )
			presetSprite = grass01;
		else if( flag == 2 )
			presetSprite = grass02;
		else if( flag == 3 )
			presetSprite = grass03;
		else if( flag == 4 )
			presetSprite = clover1;
		else
			presetSprite = grass01;
		
		spawnSingleGrass(relativeAngle,sizeFactor,presetSprite);
	}
	
	public void spawnSingleGrass(double relativeAngle, double sizeFactor, Sprite sprite ){
		
		double radians = Math.toRadians(relativeAngle);
		
		Point relativeSurface = new Point( (int)(radius*Math.cos(radians)), (int)(radius*Math.sin(radians)) );
		
		EntityStatic grassEntity = new EntityStatic( this.getAbsolutePositionOf(relativeSurface) );
		grassEntity.addGraphicTo(sprite,true);
		
		//grassEntity.getGraphicComposite().setGraphicAngle(radians);
		grassEntity.addAngularComposite();
		grassEntity.getAngularComposite().setAngleInDegrees( relativeAngle+90 );
		grassEntity.getGraphicComposite().setGraphicSizeFactor( sizeFactor );
		
		//grassEntity.addUltralightColliderTo(10,board);
		//grassEntity.addColliderTo( new BoundarySingular(0,0), board);
		grassEntity.addColliderTo( new BoundaryCircular(10), board);
		
		board.getCurrentScene().addEntity(grassEntity,"Grass");
		
		CompositeFactory.makeChildOfParent(grassEntity, this, board);
	}
	

	public static final class GrassBump extends CollisionDispatcher<EntityStatic, EntityStatic>{

		@Override
		public Collision createVisualCollision(EntityStatic entityInstance1, Collider colliderInstance1,
				EntityStatic entityInstance2, Collider colliderInstance2, VisualCollisionCheck check,
				RenderingEngine engine) {
			
			System.out.println("gdstg");
			
			return new Collision.CustomType<EntityStatic, EntityStatic>( entityInstance1 , colliderInstance1 , entityInstance2, colliderInstance2 ){

				@Override
				protected void internalInitializeCollision() {
		
					entityInstance2.addUpdateableEntityToUpdater(board);

					EntityBehaviorScript grassBump = new EntityBehaviorScript("Bump",entityInstance2){

						int count = 0;
						double originalAngle = entityInstance2.getAngularComposite().getAngleInRadians();

						@Override
						protected void updateOwnerEntity(EntityStatic entity) {

							//								entityInstance2.getGraphicComposite().setGraphicAngle(
							//										originalAngle + (( -( count/20.0 )*( count/20.0 ) + 1 )/2.0) 
							//										);

							entityInstance2.getGraphicComposite().setGraphicAngle(
									originalAngle + ((Math.sin(count*Math.PI/30.0) * Math.pow(2, (-count/60.0)) )/3.0)
									);

							if( count < 300 ){
								++count;
							}
							else{

								removeThisUpdateableComposite();
								entityInstance2.getGraphicComposite().setGraphicAngle(originalAngle);

								//entityInstance2.removeUpdateableEntityFromUpdater();
							}
						}
					};

					CompositeFactory.addScriptTo(entityInstance2, grassBump);

				}

				@Override
				protected void updateCollision() {
					if ( !check.check(colliderInstance1, colliderInstance2) ){
						isComplete = true;
					}
				}

				@Override
				public void updateVisualCollision(MovingCamera camera, Graphics2D gOverlay) {
					
				}

				@Override
				public void internalCompleteCollision() {
					
				}
				
			};
		}
		
	}
	
}
