package entityComposites;

import java.awt.Color;

import entities.EntityStatic;
import sprites.Sprite;
import sprites.SpriteFilledShape;

public class EntityComposite {
	
	private EntityStatic ownerEnttiy;
	
	
	
	public static void addGraphicTo( EntityStatic entity , Sprite sprite ){
		
		GraphicComposite graphicComposite = new GraphicComposite( entity );
		
		graphicComposite.setSprite( sprite );
		
		entity.setSpriteType( graphicComposite );
	}
	
	
	public static void addGraphicFromCollider( EntityStatic entity , Collider collider){
		
		GraphicComposite graphicComposite = new GraphicComposite( entity );
		
		graphicComposite.setSprite( new SpriteFilledShape( collider.getBoundary() , Color.WHITE ) );
		
		
		
		entity.setSpriteType( graphicComposite );
	}
	
	
}
