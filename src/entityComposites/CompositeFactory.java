package entityComposites;

import java.awt.Color;
import java.awt.geom.Line2D;

import physics.Boundary;
import sprites.Sprite;
import sprites.SpriteFilledShape;

public class CompositeFactory {
	
	private EntityStatic ownerEnttiy;
	
	
	public static void addTranslationTo( EntityStatic entity ){
		TranslationComposite trans = new TranslationComposite( entity );
		entity.setTranslationComposite( trans );
	}
	
	public static void addColliderTo( EntityStatic entity , Line2D[] sides ){
		
		Collider newCollider = new Collider( entity );
		
		Boundary newBoundary = new Boundary( sides );
		
		newCollider.setBoundary( newBoundary );
		
		entity.setCollisionComposite( newCollider );
		
	}

	public static void addColliderTo( EntityStatic entity , Boundary boundary ){
		
		Collider newCollider = new Collider( entity );
		
		newCollider.setBoundary( boundary );
		
		entity.setCollisionComposite( newCollider );
		
	}
	
	public static void addGraphicTo( EntityStatic entity , Sprite sprite ){
		
		GraphicComposite graphicComposite = new GraphicComposite( entity );
		
		graphicComposite.setSprite( sprite );
		
		entity.setGraphicComposite( graphicComposite );
	}
	
	
	public static void addGraphicFromCollider( EntityStatic entity , Collider collider){
		
		GraphicComposite graphicComposite = new GraphicComposite( entity );
		
		graphicComposite.setSprite( new SpriteFilledShape( collider.getBoundary() , Color.WHITE ) );
		
		
		
		entity.setGraphicComposite( graphicComposite );
	}
	
	// PARENT CHILDREN METHODS
	
	public static void makeChildOfParent( EntityStatic child , EntityStatic parent ){
		
		//CREATE COMPOSITE DECOSNTRUCTOR TO ENSURE REMOVAL
		//child.getTranslationComposite().remove()
		child.setTranslationComposite( parent.getTranslationComposite() );
		//ADD CHILD TO PARENTS CHILDREN LSIT IN CHILDREN COMPOSITE
		
	}
	
	
}
