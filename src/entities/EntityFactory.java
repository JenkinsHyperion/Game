package entities;

import entityComposites.NonCollidable;

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
	
}
