package entityComposites;

import java.awt.Color;
import java.awt.geom.Line2D;

import engine.BoardAbstract;
import entityComposites.AngularComposite.AngleComposite;
import physics.Boundary;
import physics.BoundaryPolygonal;
import sprites.Sprite;
import sprites.SpriteFilledShape;

public class CompositeFactory {

	public static void addDynamicRotationTo( EntityStatic entity ){
		
		DynamicRotationComposite rotation = new DynamicRotationComposite( entity );
		entity.setRotationComposite( rotation );
		entity.updateables.add(rotation);
		
		if ( !(entity.getColliderComposite() instanceof ColliderNull) ){
			Collider colliderStatic = entity.getColliderComposite();
			ColliderRotational collederNew = new ColliderRotational( colliderStatic );
			entity.setCollisionComposite( collederNew );
			//rotation.rotateableCompositeList.add( collederNew );
		}
		
		
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
			child.updateables.add( (DynamicRotationComposite) parent.getRotationComposite() );
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
		parentingFunctionality(child, parent, board);
	}
	
	public static void makeChildOfParentUsingPosition(EntityStatic child , EntityStatic parent , BoardAbstract board ){
		parentingFunctionality(child, parent, board);
		child.setPos( child.getX() + parent.getX() , child.getY() + parent.getY() );
	}
	
	private static void parentingFunctionality( EntityStatic child , EntityStatic parent , BoardAbstract board ){
		
		//CREATE COMPOSITE DECOSNTRUCTOR TO ENSURE REMOVAL
		System.out.print("Composite Factory setting ["+child+"] as child of ["+parent+"]");
		
		if ( parent.hasTranslation() ){ 
			
			TranslationCompositeActive trans = (TranslationCompositeActive) parent.getTranslationComposite();
			
			if ( child.hasTranslation() ){  	
				
				System.out.print("... Swapped flyweighted translation "+trans.getDX());
				child.translationType.disable();
				child.setTranslationComposite(trans );
				child.addUpdateable( trans );

			}else{ 								
				
				System.out.print("... Flyweighted translation "+trans.getDX());
				child.setTranslationComposite( trans );
				child.updateables.add( trans );

			}
			
			child.addToUpdater(board); // add child to be updated
		}
		else{ //parent has no translation, so 
			System.err.print("... Parent entity ["+parent+"] has no Translational composite");
		}
		
		//If child has collider, remove from and add back to collision Engine as dynamic, in case it was registered as static
		if ( child.hasCollider() ){ 
			System.out.print("... Switched ["+child+ "] collider to dynamic");
			child.getColliderComposite().disable();
			child.getColliderComposite().addCompositeToPhysicsEngineDynamic(board.collisionEngine);
			
			child.setTranslationComposite( new TranslationCompositeActive() );
		} //else do nothing
		
		if ( parent.getRotationComposite().exists() ){
			
			System.out.print("... Setting '"+parent+"' as rotateable parent");	
			
			DynamicRotationComposite parentRotation = (DynamicRotationComposite) parent.getRotationComposite();
			AngleComposite parentAngular = (AngleComposite) parent.getAngularComposite();
			
			ParentRotateableComposite parentComposite = new ParentRotateableComposite(parent);
			parent.addFamilyRole( parentComposite );		//Give parent list of children
			parentComposite.addChild(child);				//Add child to parent's list //FIXME CHECK FOR EXISTING PARENT
			parentAngular.addRotateable( parentComposite );	//Add children list to rotateables
			
			child.setRotationComposite(parentRotation);
			child.updateables.add(parentRotation);
			
		}else{
			System.err.print("Parent isn't rotateable");
		}
		
		System.out.println("");
		//parent.updateables.add(child);
		//child.updateables.add( (UpdateableComposite) parent.getTranslationComposite() );
		
	}
	
	public static void addScriptTo( EntityStatic entity , EntityScript script){
		
		entity.updateables.add(script);
		
	}
	
	public static void addLifespanTo( EntityStatic entity , int lifespanInFrames ){
		
		entity.updateables.add( new LifespanComposite(lifespanInFrames) );
		
	}
	
	
}
