package entities;

import java.awt.geom.Line2D;

import entityComposites.Collidable;
import entityComposites.NonCollidable;
import physics.Boundary;
import sprites.SpriteNull;

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
        testEntity.setCollisionProperties( NonCollidable.getNonCollidable() );
        testEntity.loadSprite( path , 0 , 0 );
        return testEntity;
	}

	public static EntityStatic createEntityFromBoundary(int x, int y, Line2D[] lines) {
		EntityStatic testEntity = new EntityStatic(x,y);
		
		testEntity.setCollisionProperties( new Collidable( testEntity , lines ) );
		testEntity.setSpriteType( SpriteNull.getNullSprite() );
		
		return testEntity;
	}
	
}
