package testEntities;

import java.awt.Point;

import com.studiohartman.jamepad.ControllerState;

import engine.BoardAbstract;
import engine.TestBoard;
import entityComposites.CompositeFactory;
import entityComposites.EntityStatic;
import physics.Boundary;
import physics.BoundaryCircular;
import sprites.Sprite;
import utility.PerlinNoiseGenerator;

public class Asteroid extends EntityStatic{
	
	private static TestBoard board;

	private static final Sprite.Stillframe grass01 = new Sprite.Stillframe("Prototypes/grasstest01.png",0,2,Sprite.CENTERED_BOTTOM);
	private static final Sprite.Stillframe grass02 = new Sprite.Stillframe("Prototypes/grasstest02.png",0,2,Sprite.CENTERED_BOTTOM);
	private static final Sprite.Stillframe grass03 = new Sprite.Stillframe("Prototypes/grasstest03.png",0,2,Sprite.CENTERED_BOTTOM);
	private static final Sprite.Stillframe clover1 = new Sprite.Stillframe("Prototypes/clover.png",0,2,Sprite.CENTERED_BOTTOM ,75);

	private static final Sprite.Stillframe asteroid01 = new Sprite.Stillframe("asteroid02.png", Sprite.CENTERED );
	private static final Sprite.Stillframe asteroid02 = new Sprite.Stillframe("asteroid_test_02.png", Sprite.CENTERED );
	
	public static final int PRESET01 = 1;
	public static final int PRESET02 = 2;
	public static final int PRESET03 = 3;

	private static int entityCount = 0;
	
	private Sprite.Stillframe grassSprite;
	private Sprite.Stillframe asteroidSprite;
	
	private final int radius;
	
	public Asteroid(int x, int y, int radius, TestBoard board, int...preset) {
		super("Asteroid "+entityCount,x, y);
		++entityCount;
		this.board = board;
		this.radius = radius;

		if ( preset.length > 0 ){
			if ( preset[0] == 1 ){
				asteroidSprite = asteroid01;
				grassSprite = grass01;
			}
			else if( preset[0] == 2 ){
				asteroidSprite = asteroid02;
				grassSprite = grass02;
			}
			else if( preset[0] == 3 ){
				asteroidSprite = asteroid01;
				grassSprite = grass03;
			}
		}
		else{
			asteroidSprite = asteroid01;
			grassSprite = grass01;
		}

		CompositeFactory.addGraphicTo(this, asteroidSprite );

		this.getGraphicComposite().setGraphicSizeFactor(0.334 * (radius/500.0));
        //CompositeFactory.addTranslationTo(asteroid);
        CompositeFactory.addDynamicRotationTo(this);

        Boundary bounds1 = new BoundaryCircular(radius);
        //CompositeFactory.addColliderTo(asteroid,  new BoundaryLinear( new Line2D.Double( 0 , 100 , 0, -100 ) ) );
        //CompositeFactory.addColliderTo(asteroid,  new BoundaryPolygonal.Box(100, 200, -50, -100) );
        
        CompositeFactory.addRotationalColliderTo(
        		this, 
        		bounds1, 
        		this.getAngularComposite()
        		);
        
        CompositeFactory.addRigidbodyTo(this);
		
	}
	
	public void spawnGrass(){

		final int resolution = (int)( 6/(radius/500.0)); //angle in degrees
		
		int[] perlinGen01 = PerlinNoiseGenerator.perlinNoise(360, 10, 80);
		
		for ( int i = 0 ; i < 360 ; ++i ){
			
			double keySize = (20+perlinGen01[i])/100.0;
			
			if ( i % resolution == 0 ){
	
				spawnSingleGrass( i, keySize ,grassSprite);
			}
			else{
				
				if ( i % resolution == resolution/2 ){
				
					if ( 0.5 > (20+perlinGen01[i])/100.0 ){
						spawnSingleGrass( i, keySize ,grassSprite);
					}
				}
			}
		}
		
	}
	
	public void spawnSingleGrass(double relativeAngle, double sizeFactor, Sprite sprite ){
		
		double radians = Math.toRadians(relativeAngle);
		
		Point relativeSurface = new Point( (int)(radius*Math.cos(radians)), (int)(radius*Math.sin(radians)) );
		
		EntityStatic grassEntity = new EntityStatic( this.getAbsolutePositionOf(relativeSurface) );
		grassEntity.addGraphicTo(sprite);
		
		//grassEntity.getGraphicComposite().setGraphicAngle(radians);
		grassEntity.addAngularComposite();
		grassEntity.getAngularComposite().setAngleInDegrees(relativeAngle+90);
		grassEntity.getGraphicComposite().setGraphicSizeFactor( sizeFactor );
		board.getCurrentScene().addEntity(grassEntity);
		
	}
	
	public void populateGrass(){
		
		System.out.println("POPULATING GRASS @#######################");

		final int radius = 500;
		
		int circumference = (int) (2.0 * Math.PI * radius);

		
		
		for ( int a = 0 ; a < 360 ; a=a+10 ){
		
			double radians = Math.toRadians(a);
			
			Point relativeSurface = new Point( (int)(radius*Math.cos(radians)), (int)(radius*Math.sin(radians)) );
			
			EntityStatic grassEntity = new EntityStatic( this.getAbsolutePositionOf(relativeSurface) );
			grassEntity.addGraphicTo(grass03);
			
			//grassEntity.getGraphicComposite().setGraphicAngle(radians);
			grassEntity.addAngularComposite();
			grassEntity.getAngularComposite().setAngleInDegrees(a+90);
			grassEntity.getGraphicComposite().setGraphicSizeFactor( BoardAbstract.randomInt(20,100)/100.0 );
			board.getCurrentScene().addEntity(grassEntity);
		}
	}
	
}
