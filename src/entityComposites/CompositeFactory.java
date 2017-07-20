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

	public static DynamicRotationComposite addDynamicRotationTo( EntityStatic entity ){
		
		if ( entity.getRotationComposite().exists() ){
			System.out.println("Overriding dynamic rotation composite of "+entity);
		}else{
			System.out.println("Adding dynamic rotation composite to "+entity);
		}
		DynamicRotationComposite rotation = new DynamicRotationComposite( entity );
		entity.setRotationComposite( rotation );
		entity.updateablesList.add(rotation);
		
		if ( !(entity.getColliderComposite() instanceof ColliderNull) ){
			Collider colliderStatic = entity.getColliderComposite();
			ColliderRotational collederNew = new ColliderRotational( colliderStatic );
			entity.setCollisionComposite( collederNew );
			//rotation.rotateableCompositeList.add( collederNew );
		}
		
		return rotation;
		
	}
	
	public static void addCustomDynamicRotationTo( EntityStatic entity , DynamicRotationComposite rotation ){
		if ( entity.getRotationComposite().exists() ){
			System.out.println("Overriding dynamic rotation composite of "+entity);
		}else{
			System.out.println("Adding dynamic rotation composite to "+entity);
		}

		entity.setRotationComposite( rotation );
		entity.updateablesList.add(rotation);
	}
	
	public static TranslationComposite.Active addTranslationTo( EntityStatic entity ){
		TranslationComposite.Active trans = new TranslationComposite.Active();
		entity.setTranslationComposite( trans );
		entity.updateablesList.add(trans);
		return trans;
	}
	@Deprecated
	public static void flyweightTranslation( EntityStatic parent, EntityStatic child ){
		if ( parent.hasTranslation() ){
			child.setTranslationComposite( parent.getTranslationComposite() );
			child.updateablesList.add( (TranslationComposite.Active) parent.getTranslationComposite() );
		}
	}
	@Deprecated
	public static void flyweightRotation( EntityStatic parent, EntityStatic child ){
		if ( parent.hasRotation() ){
			child.setRotationComposite( parent.getRotationComposite() );
			child.updateablesList.add( (DynamicRotationComposite) parent.getRotationComposite() );
		}
		else{
			System.err.println("WARNING: "+parent+" has no rotational composite to flyweight");
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
		
		GraphicComposite.Active graphicComposite = new GraphicComposite.Active( entity );
		
		graphicComposite.setSprite( sprite );
		
		entity.setGraphicComposite( graphicComposite );
	}
	/**Adds anonymous graphicComposite to this entity, probably with the intention of overriding the GraphicComposite draw() Method
	 * with custom functionality
	 * @param entity
	 * @param graphicComposite
	 */
	public static void addAnonymousGraphicTo( EntityStatic entity , GraphicComposite graphicComposite ){
		
		entity.setGraphicComposite( graphicComposite );
	}
	
	public static void addGraphicFromCollider( EntityStatic entity , Collider collider){
		
		GraphicComposite.Active graphicComposite = new GraphicComposite.Active( entity );
		
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
		System.out.println("Composite Factory setting ["+child+"] as child of ["+parent+"]");
		
		if ( parent.hasTranslation() ){ 
			
			TranslationComposite.Active trans = (TranslationComposite.Active) parent.getTranslationComposite();
			
			if ( child.hasTranslation() ){  	
				
				System.out.println("|   Swapped flyweighted translation "+trans.getDX());
				child.translationType.disable();
				child.setTranslationComposite(trans );
				child.addUpdateable( trans );

			}else{ 								
				
				System.out.println("|   Flyweighted translation "+trans.getDX());
				child.setTranslationComposite( trans );
				child.updateablesList.add( trans );

			}
			
			child.addToUpdater(board); // add child to be updated
		}
		else{ //parent has no translation, so 
			System.err.println("|   Parent entity ["+parent+"] has no Translational composite");
		}
		
		if ( parent.getRotationComposite().exists() ){
			
			System.out.println("|   Setting '"+parent+"' as rotateable parent");	
			
			DynamicRotationComposite parentRotation;
			
			parentRotation = (DynamicRotationComposite) parent.getRotationComposite();
			AngleComposite parentAngular = (AngleComposite) parent.getAngularComposite();
			
			ParentComposite.ParentRotateableComposite parentComposite = new ParentComposite.ParentRotateableComposite(parent);
			parent.addParentComposite( parentComposite );		//Give parent list of children			FIXME CHECK FOR EXISTING PARENT
			parentAngular.addRotateable( parentComposite );	//Add children list to rotateables
			
			ChildComposite.Rotateable childComposite = parentComposite.registerChild(child);	
			( ( AngleComposite ) child.getAngularComposite() ).addRotateable( childComposite );
			
			/*if ( child.getAngularComposite().exists() ){
				AngleComposite childAngular = (AngleComposite) child.getAngularComposite();
				childAngular.addRotateable( childComposite );
			}*/
			

		}else{ 
			System.out.println("|   Parent isn't rotateable"); 
		}
		
		//If child has collider, remove from and add back to collision Engine as dynamic, in case it was registered as static
		if ( child.hasCollider() ){ 
			System.out.print("|   Switched ["+child+ "] collider to dynamic");
			child.getColliderComposite().disable();
			child.getColliderComposite().addCompositeToPhysicsEngineDynamic(board.collisionEngine);
			
			child.setTranslationComposite( new TranslationComposite.Active() );
		} //else do nothing
		
		System.out.println("");
		//parent.updateables.add(child);
		//child.updateables.add( (UpdateableComposite) parent.getTranslationComposite() );

		
	}
	
	public static void addScriptTo( EntityStatic entity , EntityScript script){
		
		entity.updateablesList.add(script);
		
	}
	
	public static void addLifespanTo( EntityStatic entity , int lifespanInFrames ){
		
		entity.updateablesList.add( new LifespanComposite(lifespanInFrames) );
		
	}
	
	
}
