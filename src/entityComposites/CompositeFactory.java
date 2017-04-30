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
		
		RotationCompositeDynamic rotation = new RotationCompositeDynamic( entity );
		entity.setRotationComposite( rotation );
		entity.updateables.add(rotation);
	}
	
	public static void addTranslationTo( EntityStatic entity ){
		TranslationCompositeActive trans = new TranslationCompositeActive();
		entity.setTranslationComposite( trans );
		entity.updateables.add(trans);
	}
	
	public static void flyweightTranslation( EntityStatic parent, EntityStatic child ){
		if ( parent.hasTranslation() ){
			child.setTranslationComposite( parent.getTranslationComposite() );
			child.updateables.add( (TranslationCompositeActive) parent.getTranslationComposite() );
		}
	}
	
	public static void flyweightRotation( EntityStatic parent, EntityStatic child ){
		if ( parent.hasRotation() ){
			child.setRotationComposite( parent.getRotationComposite() );
			child.updateables.add( (RotationCompositeDynamic) parent.getRotationComposite() );
		}
	}
	
	public static void addColliderTo( EntityStatic entity , Line2D[] sides ){
		
		Boundary newBoundary = new BoundaryPolygonal( sides );
		
		Collider newCollider = new Collider( entity , newBoundary );
		
		entity.setCollisionComposite( newCollider );
		
	}

	public static void addColliderTo( EntityStatic entity , Boundary boundary ){
		
		if ( entity.getColliderComposite().exists() ){
			entity.getColliderComposite().setBoundary(boundary);
		}
		else {
			Collider newCollider = new Collider( entity , boundary );
			
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
		parenting(child, parent, board);
	}
	
	public static void makeChildOfParentUsingPosition(EntityStatic child , EntityStatic parent , BoardAbstract board ){
		parenting(child, parent, board);
		child.setPos( child.getX() + parent.getX() , child.getY() + parent.getY() );
	}
	
	private static void parenting( EntityStatic child , EntityStatic parent , BoardAbstract board ){
		
		//CREATE COMPOSITE DECOSNTRUCTOR TO ENSURE REMOVAL
		System.out.println("Composite Factory setting ["+child+"] as child of ["+parent+"]");
		
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
	
	public static void addScriptTo( EntityStatic entity , EntityScript script){
		
		entity.updateables.add(script);
		
	}
	
	
}
