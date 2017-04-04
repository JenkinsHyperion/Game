package entityComposites;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;

import engine.BoardAbstract;
import engine.Scene;
import entities.EntityAssembler;
import entities.Platform01;
import entityComposites.*;
import saving_loading.EntityData;
import sprites.Background;
import sprites.SpriteFilledShape;
import sprites.SpriteStillframe;

public class EntityFactory {
	
	public static Platform01 PLATFORM01 = new Platform01();

	private EntityFactory(){
		
	}
	
	public static EntityStatic createPlatform( int x, int y , Platform01 ID ){
		
		EntityAssembler PlatformAssembler = new EntityAssembler( ID );
		
		PlatformAssembler.assembleEntity();
		
		return PlatformAssembler.getEntity();
		
	}
	
	public static EntityStatic createBackgroundSprite( String path, int x, int y ){
		EntityStatic testEntity = new EntityStatic(x,y);
        testEntity.setCollisionComposite( ColliderNull.getNonCollidable() );
        SpriteStillframe sprite = new SpriteStillframe(path);
        CompositeFactory.addGraphicTo(testEntity, sprite);
        return testEntity;
	}
	
	public static EntityStatic createBackgroundScroll( String path , int boardW , int boardH , float xScroll , float yScroll ){
		EntityStatic testEntity = new EntityStatic(0,0);
		CompositeFactory.addGraphicTo(testEntity, new Background( path , boardW, boardH, xScroll, yScroll ) );
		return testEntity;
	}

	public static EntityStatic createEntityFromBoundary(int x, int y, Line2D[] lines) {
		EntityStatic testEntity = new EntityStatic(x,y);
		
		CompositeFactory.addColliderTo( testEntity , lines);
		
		//CompositeFactory.addGraphicFromCollider( testEntity, testEntity.getColliderComposite() );
		
		return testEntity;
	}
	
	public static EntityStatic createEntityFromBoundary(Line2D[] lines) {
		
		int avgX = (int)lines[0].getP1().getX();
		int avgY = (int)lines[0].getP1().getY();
		int cornersN = 1;
		
		Line2D[] finalLines = lines;
		
		for ( Line2D line : lines ){	
			avgX = avgX + (int)line.getP2().getX();
			avgY = avgY + (int)line.getP2().getY();
			cornersN++;
		}
		
		int centerX = avgX/cornersN;
		int centerY = avgY/cornersN;

		for ( Line2D line : lines ){
			line.setLine( line.getX1()-centerX  , line.getY1()-centerY , line.getX2()-centerX , line.getY2()-centerY );
		}
		
		EntityStatic testEntity = new EntityStatic(0,0);
		
		/*for ( int i = 0 ; i < lines.length ; i++ ){
			finalLines[i].setLine( lines[i].getX1() , lines[i].getY1()  , lines[i].getX2() , lines[i].getY2() );
		}*/
		Collider collidable = new Collider( testEntity , finalLines ); 
		testEntity.setCollisionComposite( collidable );	
		//testEntity.loadSprite("missing.png" , 0 , 0 );
		/*SpriteComposite spriteComposite = new SpriteComposite( 
				new SpriteFilledShape( collidable.getBoundary() , Color.WHITE ),
				testEntity
		);*/
		
		//testEntity.setSpriteType( NullSpriteComposite.getNullSprite() );
		CompositeFactory.addGraphicFromCollider(testEntity, collidable);
		
		testEntity.setPos( centerX , centerY );
		
		return testEntity;
	}
	
	/*#######################################################################
	 * 
	 *   ENTITY DESERIALIZATION should maybe be in a class separate
	 * 				from the more client based methods, will always be more powerful
	 ########################################################################*/
	
	
	public static void deserializeEntityData( EntityData[] dataArray , BoardAbstract board ){
		
		EntityStatic[] newEntityList = new EntityStatic[ dataArray.length ];
		
		for ( int i = 0 ; i < dataArray.length ; i++ ){

			//CONSTRUCT SPRITE
			
				Point[] corners = dataArray[i].getColliderData().getCornerPositions();
				Line2D[] sideLines = new Line2D[ corners.length ];
				for ( int j = 0 ; j < corners.length ; j++ ){
					int jNext = (j+1) % corners.length;
					sideLines[j] = new Line2D.Float( corners[j] , corners[jNext] );
				}
				
				Point pos = dataArray[i].getEntityPosition();
				
				newEntityList[i] = createEntityFromBoundary( pos.x , pos.y , sideLines );
			
			//CONSTRUCT COLLIDER
			
			
			
			//CONSTRUCT UPDATEABLE
			
			
		}
		
		createNewSceneFromEntities( newEntityList , board );

	}
	
	private static void createNewSceneFromEntities( EntityStatic[] entities , BoardAbstract board ){
		
		Scene newScene = new Scene(board);
		
		board.renderingEngine.debugClearRenderer();
		board.collisionEngine.degubClearCollidables();
		
		for ( EntityStatic addedEntity : entities )
			newScene.addEntity(addedEntity);
		
		board.createNewScene(newScene);

		
	}
	
	
}
