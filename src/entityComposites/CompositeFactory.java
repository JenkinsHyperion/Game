package entityComposites;

import java.awt.Color;
import java.awt.geom.Line2D;

import editing.BrowserTreePanel;
import editing.EditorPanel;
import engine.BoardAbstract;
import entityComposites.AngularComposite.Angled;
import physics.Boundary;
import physics.BoundaryPolygonal;
import sprites.Sprite;
import sprites.SpriteFilledShape;

public class CompositeFactory {

	public static AngularComposite addAngularComposite( EntityStatic entity ){
		
		final AngularComposite returnAngular = new AngularComposite.Angled(entity);
		entity.setAngularComposite(returnAngular);
		
		return returnAngular;
	}
	
	public static DynamicRotationComposite addDynamicRotationTo( EntityStatic entity ){
		
		if ( entity.getRotationComposite().exists() ){
			System.out.println("TODO Overriding dynamic rotation composite of "+entity);
		}else{
			System.out.println("TODO Adding dynamic rotation composite to "+entity);
		}
		
		if ( !entity.getAngularComposite().exists() ){
			System.out.println("Post adding angular composite of "+entity);
			addAngularComposite(entity);
		}
		
		final DynamicRotationComposite rotation = new DynamicRotationComposite( entity );
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
	
	public static TranslationComposite addTranslationTo( EntityStatic entity ){
		TranslationComposite trans = new TranslationComposite(entity);
		entity.setTranslationComposite( trans );
		entity.addUpdateableComposite(trans);
		
		//trans.notifyAddedEntity();
		
		return trans;
	}
	@Deprecated
	public static void flyweightTranslation( EntityStatic parent, EntityStatic child ){
		if ( parent.hasTranslation() ){
			child.setTranslationComposite( parent.getTranslationComposite() );
			child.updateablesList.add( (TranslationComposite) parent.getTranslationComposite() );
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

	public static Collider addColliderTo( EntityStatic entity , Boundary boundary ){

		if ( entity.getColliderComposite().exists() ){ //FIXME 
			entity.getColliderComposite().setBoundary(boundary);
			return entity.getColliderComposite();
		}
		else {
			Collider newCollider = new Collider( entity , boundary );
			entity.setCollisionComposite( newCollider );
			return newCollider;
		}
		
	}
	
	public static Collider addColliderTo( EntityStatic entity , Boundary boundary , int colliderGroup ){

		if ( entity.getColliderComposite().exists() ){
			entity.getColliderComposite().setBoundary(boundary);
			return entity.getColliderComposite();
		}
		else {
			Collider newCollider = new Collider( entity , boundary );
			entity.setCollisionComposite( newCollider );
			return newCollider;
		}
		
	}
	
	public static Collider addRotationalColliderTo( EntityStatic entity , Boundary boundary, AngularComposite angular ){

		if ( entity.getColliderComposite().exists() ){
			entity.getColliderComposite().setBoundary(boundary);
			return entity.getColliderComposite();
		}
		else {
			ColliderRotational newCollider = new ColliderRotational( entity , boundary );
			entity.setCollisionComposite( newCollider );
			
			if ( entity.getAngularComposite().exists() ){
				( (Angled) entity.getAngularComposite() ).addRotateable(newCollider);
				System.err.println("Linking Angular");
			}else{
				System.err.println("Couldn't find Angular");
			}
			
			
			return newCollider;
		}
		
	}
	
	public static void addRigidbodyTo( EntityStatic entity ){

		if ( entity.getColliderComposite().exists() ){
			//Add reference to collider or vice versa
		}
		else {
			
		}
		entity.setRigidbody( new Rigidbody(entity) );
	}
	
	public static GraphicComposite addGraphicTo( EntityStatic entity , Sprite sprite ){
		
		GraphicComposite.Active graphicComposite = new GraphicComposite.Active( entity );
		
		graphicComposite.setSprite( sprite );
		
		entity.setGraphicComposite( graphicComposite );
		
		return graphicComposite;
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
		System.out.println("PARENTING ["+child+"] as child of ["+parent+"]");
		
		if ( parent.hasTranslation() ){ 
			
			//child.addToUpdater(board); // add child to be updated
			
			//TranslationComposite trans = (TranslationComposite) parent.getTranslationComposite();
			
			/*if ( child.hasTranslation() ){  	
				
				System.out.println("|   Swapped flyweighted translation "+trans.getDX());
				child.translationType.disableComposite();
				child.setTranslationComposite(trans );
				child.addUpdateable( trans );

			}else{ 								
				
				System.out.println("|   Flyweighted translation "+trans.getDX());
				child.setTranslationComposite( trans );
				child.getTranslationComposite().flyweightTranslation( trans );
				child.updateablesList.add( trans );

			}*/
			
		}
		else{ //parent has no translation, so 
			System.err.println("|   Parent entity ["+parent+"] has no Translational composite");
		}
		
		if ( parent.getRotationComposite().exists() ){
			
			System.out.println("|   Setting '"+parent+"' as rotateable parent");	
			
			DynamicRotationComposite parentRotation;
			
			parentRotation = (DynamicRotationComposite) parent.getRotationComposite();
			Angled parentAngular = (Angled) parent.getAngularComposite();
			
			ParentComposite.ParentRotateableComposite parentComposite = new ParentComposite.ParentRotateableComposite(parent);
			parent.addParentComposite( parentComposite );		//Give parent list of children			FIXME CHECK FOR EXISTING PARENT
			parentAngular.addRotateable( parentComposite );	//Add children list to rotateables
			
			final ChildComposite.Rotateable childComposite = parentComposite.registerChild(child);	
			//( ( Angled ) child.getAngularComposite() ).addRotateable( childComposite );
			
			if ( !child.getAngularComposite().exists() ){
				addAngularComposite(child);
			}
			

		}else{ 
			System.out.println("|   Parent isn't rotateable"); 
		}
		
		//If child has collider, remove from and add back to collision Engine as dynamic, in case it was registered as static
		if ( child.hasCollider() ){ 
			System.out.println("|   Switched ["+child+ "] collider to dynamic");
			child.getColliderComposite().disableComposite();

			child.getColliderComposite().notifyEngineOfChangeToDynamic();
			
			child.setTranslationComposite( new TranslationComposite(child) );
		} //else do nothing
		
		System.out.println("");
		//parent.updateables.add(child);
		//child.updateables.add( (UpdateableComposite) parent.getTranslationComposite() );
		
		//#### AREA TO NOTIFY BrowserTree
		//something like,
		BrowserTreePanel browserTreePanel = board.getEditorPanel().getBrowserTreePanel();
		browserTreePanel.notifyParentChildRelationshipChanged(child, parent);
		
	}
	
	public static void addScriptTo( EntityStatic entity , EntityScript script){
		
		entity.updateablesList.add(script);
		
	}
	
	public static void addLifespanTo( EntityStatic entity , int lifespanInFrames ){
		
		entity.updateablesList.add( new LifespanComposite(lifespanInFrames) );
		
	}
	
	
}
