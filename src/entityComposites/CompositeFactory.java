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
		System.out.println("Composite Factory setting "+child+" as child of "+parent);
		
		if ( child.hasTranslation() ){//if child already has translation, remove it and flyweight parent's instead
			System.err.println("TODO Composite Factory cannot yet make entity with translation into child");
		}
		else{//if child does not have translation, flyweight parent's translation
			
			child.setTranslationComposite( parent.getTranslationComposite() );
			child.updateables.add( (UpdateableComposite) parent.getTranslationComposite() );
			
			child.addToUpdater(board); // add child to be updated
		}
		
		//If child has collider, remove from and add back to collision Engine as dynamic, in case it was registered as static
		if ( child.hasCollider() ){ 
			System.out.println("Switched ["+child+ "] collider to dynamic");
			child.getColliderComposite().disable();
			child.getColliderComposite().addCompositeToPhysicsEngineDynamic(board.collisionEngine);
		} //else do nothing
		
		
		//parent.updateables.add(child);
		//child.updateables.add( (UpdateableComposite) parent.getTranslationComposite() );
		
	}
	
	
}
