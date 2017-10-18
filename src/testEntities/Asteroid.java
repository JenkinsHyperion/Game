package testEntities;

import java.awt.Point;

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

	private static int entityCount = 0;
	
	private final int radius;
	
	public Asteroid(int x, int y, int radius, TestBoard board) {
		super("Asteroid "+entityCount,x, y);
		++entityCount;
		this.board = board;
		this.radius = radius;

		
		CompositeFactory.addGraphicTo(this, new Sprite.Stillframe("asteroid02.png", Sprite.CENTERED ) );
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
	
				spawnSingleGrass( i, keySize ,grass03);
			}
			else{
				
				if ( i % resolution == resolution/2 ){
				
					if ( 0.5 > (20+perlinGen01[i])/100.0 ){
						spawnSingleGrass( i, keySize ,grass03);
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
