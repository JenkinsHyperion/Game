package entities;

import entityComposites.*;
import sprites.Sprite;
import sprites.SpriteStillframe;

public class EntityComposed {

	public static final int INTANGIBLE = 0;
	public static final int COLLIDABLE = 1;
	
	private EntityComposed(){
	
	}
	
	public static EntityStatic buildStaticEntity( int x , int y , int collisionType ){
		
		EntityStatic entityConstructing = new EntityStatic("new", x, y);;
		CollisionProperty collisionTypeTemp;
		Sprite spriteTypeTemp = new SpriteStillframe("missing",0,0,entityConstructing);
		
		if (collisionType == 0){
			collisionTypeTemp = NonCollidable.getNonCollidable();
		}
		else if (collisionType == 1){
			collisionTypeTemp = new Collidable(entityConstructing);
		}
		else{
			//default behavior
			System.out.println("ERROR: Unknown entity property in factory");
			collisionTypeTemp = NonCollidable.getNonCollidable();
		}
		
		entityConstructing.setCollisionProperties(collisionTypeTemp);
		entityConstructing.setSpriteType(spriteTypeTemp);
		
		return entityConstructing;
		
	}
	
	public static EntityStatic buildPlatform( int x , int y , int offsetX , int offsetY, String path ){ // don't know how names should be handled yet
		
		EntityStatic entityConstructing = new EntityStatic( x , y );
		
		entityConstructing.offsetX = offsetX;
		entityConstructing.offsetY = offsetY;
		entityConstructing.loadSprite(path, offsetX, offsetY);	
		
		Collidable collidable = new Collidable( entityConstructing );
		collidable.setBoundingBox(offsetX, offsetY, 40, 6);
		
		entityConstructing.setCollisionProperties(collidable);
		
		entityConstructing.name = "Platform" + Entity.count;
		
		return entityConstructing;
	}

	
}
