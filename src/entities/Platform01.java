package entities;

public class Platform01 implements EntityBuilderInterface {
	
	public Platform01( ){	
		
	}
	
	
	public void buildCollision(  ){
		
		
		
	}
	
	public void buildSprite(  ){
		
		
		
	}
	
	//public void setPositioning(){}
	
	/*public EntityStatic buildStaticEntity( int x , int y , int collisionType ){
		
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
	
	public EntityStatic entityFromBoundary( int x , int y , Boundary boundary ){
		
		EntityStatic entityConstructing = new EntityStatic("Boundary", x, y);
		
		Collidable collisionBuilding = new Collidable(entityConstructing, boundary);
		
		entityConstructing.setCollisionProperties( collisionBuilding );
		
		return entityConstructing;
		
	}
	
	public EntityStatic buildPlatform( int x , int y , int offsetX , int offsetY, String path ){ // don't know how names should be handled yet
		
		EntityStatic entityConstructing = new EntityStatic( x , y );
		entityConstructing.name = "Platform" + Entity.count;
		
		//entityConstructing.offsetX = offsetX;
		//entityConstructing.offsetY = offsetY;
		
		entityConstructing.setSpriteType( new SpriteStillframe( 
					System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path, 
					offsetX , offsetY , entityConstructing ) 
				);
		
		Collidable collidable = new Collidable( entityConstructing );
		collidable.setBoundary( new Boundary.Box( 40 , 6 , offsetX , offsetY) );
		
		entityConstructing.setCollisionProperties(collidable);
		
		
		
		return entityConstructing;
	}
	
	*/
	
	

	
}
