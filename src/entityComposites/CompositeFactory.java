package entityComposites;

import java.awt.Color;
import java.awt.geom.Line2D;

import engine.BoardAbstract;
import physics.Boundary;
import physics.BoundaryPolygonal;
import sprites.Sprite;
import sprites.SpriteFilledShape;

public class CompositeFactory {

	public static void addRotationTo( EntityStatic entity ){
		
		RotationCompositeActive rotation = new RotationCompositeActive( entity );
		entity.setRotationComposite( rotation );
	}
	
	public static void addTranslationTo( EntityStatic entity ){
		TranslationCompositeActive trans = new TranslationCompositeActive();
		entity.setTranslationComposite( trans );
		entity.updateables.add(trans);
	}
	
	public static void addColliderTo( EntityStatic entity , Line2D[] sides ){
		
		Collider newCollider = new Collider( entity );
		
		Boundary newBoundary = new BoundaryPolygonal( sides );
		
		newCollider.setBoundary( newBoundary );
		
		entity.setCollisionComposite( newCollider );
		
	}

	public static void addColliderTo( EntityStatic entity , Boundary boundary ){
		
		if ( entity.getColliderComposite().exists() ){
			entity.getColliderComposite().setBoundary(boundary);
		}
		else {
			Collider newCollider = new Collider( entity );
			
			newCollider.setBoundary( boundary );
			
			entity.setCollisionComposite( newCollider );
		}
		
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
	
	public static void makeChildOfParent( EntityStatic child , EntityStatic parent , BoardAbstract board ){
		
		//CREATE COMPOSITE DECOSNTRUCTOR TO ENSURE REMOVAL
		
		child.getTranslationComposite().remove();
		System.out.println("Setting "+child+" to "+parent);
		child.setTranslationComposite( parent.getTranslationComposite() );
		child.updateables.add( (UpdateableComposite) parent.getTranslationComposite() );
		
		child.addToUpdater(board);
		
		//parent.updateables.add(child);
		//child.updateables.add( (UpdateableComposite) parent.getTranslationComposite() );
		
	}
	
	
}
