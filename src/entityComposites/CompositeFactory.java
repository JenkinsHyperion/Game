package entityComposites;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;

import javax.swing.SwingUtilities;

import editing.BrowserTreePanel;
import editing.EditorPanel;
import engine.BoardAbstract;
import entityComposites.AngularComposite.Angled;
import physics.Boundary;
import physics.BoundaryPolygonal;
import sprites.Sprite;
import sprites.SpriteFilledShape;

public class CompositeFactory {
	
	public static final byte TRANSLATIONAL_CHILD = 1;
	public static final byte ROTATIONAL_CHILD = 2;

	public static AngularComposite addAngularComposite( EntityStatic entity ){
		
		if ( !entity.getAngularComposite().exists() ){
			final AngularComposite returnAngular = new AngularComposite.Angled(entity);
			entity.setAngularComposite(returnAngular);
			return returnAngular;
		}
		else{
			System.out.println("CompositeFactory: Angular composite already exists on ["+entity+"]");
			return entity.getAngularComposite();
		}
	}
	
	public static DynamicRotationComposite addDynamicRotationTo( EntityStatic entity ){
		
		if ( entity.getRotationComposite().exists() ){
			System.out.println("CompositeFactory: TODO Overriding dynamic rotation composite of "+entity);
		}else{
			System.out.println("CompositeFactory: TODO Adding dynamic rotation composite to "+entity);
		}
		
		if ( !entity.getAngularComposite().exists() ){
			System.out.println("CompositeFactory: Post adding angular composite of "+entity);
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
			System.out.println("CompositeFactory: CUSTOM Overriding dynamic rotation composite of "+entity);
		}else{
			System.out.println("CompositeFactory: Adding CUSTOM dynamic rotation composite to "+entity);
		}

		entity.setRotationComposite( rotation );
		entity.updateablesList.add(rotation);
	}
	
	public static TranslationComposite addTranslationTo( EntityStatic entity ){
		TranslationComposite trans = new TranslationComposite(entity);
		entity.setTranslationComposite( trans );

		if ( trans.addUpdateableCompositeTo(entity) ){
			System.out.println( "CompositeFactory: Adding Translation Composite to ["+entity+"] updateables");
		}else
			System.err.println( "CompositeFactory: Translation Composite was already in ["+entity+"] updateables, probably already initialized");
		
		return trans;
	}
	
	public static void notifyAddedComposite(){
		
	}
	
	public static void addColliderTo( EntityStatic entity , Line2D[] sides ){
		
		Boundary newBoundary = new BoundaryPolygonal( sides );
		
		Collider newCollider = new Collider( entity , newBoundary );
		
		entity.setCollisionComposite( newCollider );
		
	}
	
	public static Collider addInitialColliderTo( EntityStatic entity , Boundary boundary ){
		
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

	public static Collider addColliderTo( EntityStatic entity , Boundary boundary , BoardAbstract board ){
		
		if ( entity.getColliderComposite().exists() ){ 
			entity.getColliderComposite().setBoundary(boundary);
			return entity.getColliderComposite();
		}
		else {
			System.out.println("CompositeFactory: Adding Collider to ["+entity+"]");
			
			Collider newCollider = new Collider( entity , boundary ); //FIXME pass in collision engine instead of board?
			entity.setCollisionComposite( newCollider );
			
			if ( entity.getTranslationComposite().exists() ){
				//newCollider.addCompositeToPhysicsEngineDynamic(board.collisionEngine);
				
			}else{
				//newCollider.addCompositeToPhysicsEngineStatic(board.collisionEngine);
			}
			
			return newCollider;
		}
		
	}
	
	public static Collider addRotationalColliderTo( EntityStatic entity , Boundary boundary, AngularComposite angular ){

		if ( entity.getColliderComposite().exists() ){
			entity.getColliderComposite().setBoundary(boundary);
			return entity.getColliderComposite();
		}
		else {

			if ( entity.getAngularComposite().exists() ){
				
				ColliderRotational newRotationalCollider = new ColliderRotational( entity , entity.getAngularComposite(), boundary );
				entity.setCollisionComposite( newRotationalCollider );
				( (Angled) entity.getAngularComposite() ).addRotateable(newRotationalCollider);
				System.err.println("CompositeFactory: Linking Angular");
				return newRotationalCollider;
			}else{
				System.err.println("CompositeFactory: Couldn't find Angular");
				Collider newCollider = new Collider( entity, boundary );
				entity.setCollisionComposite( newCollider );
				return newCollider;
			}
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
	
	public static void abandonAllChildren( EntityStatic entity ){
		
		System.out.print("CompositeFactory: PARENT ["+entity+"] abandoning all children: ");
		
		if ( entity.parentComposite.exists() ){
			for ( EntityStatic child : entity.parentComposite.getChildrenEntities() ){
				child.childComposite.disableComposite();
			}
			
			if (entity.parentComposite.getChildrenEntities().length == 0 ){		//Once parent composite has zero children, self destruct
				System.err.println("["+entity+"] has abandonded all children");
				entity.parentComposite.disableComposite();
			}
			
		}else{
			System.out.println();
			throw new RuntimeException("["+entity+"] has no children"){
				
			};
		}
	}
	
	public static void makeChildOfParent( EntityStatic child , EntityStatic parent , BoardAbstract board, byte...flags ){
		parentingFunctionality(child, parent, board, flags);
	}
	
	public static void makeChildOfParentUsingPosition(EntityStatic child , EntityStatic parent , BoardAbstract board ){
		parentingFunctionality(child, parent, board);
		child.setPos( child.getX() + parent.getX() , child.getY() + parent.getY() );
	}
	
	private static void parentingFunctionality( EntityStatic child , EntityStatic parentEntity , BoardAbstract board, byte...flags ){

		//CREATE COMPOSITE DECOSNTRUCTOR TO ENSURE REMOVAL
		System.out.println("CompositeFactory: PARENTING ["+child+"] as child of ["+parentEntity+"]");
		
		if ( parentEntity.hasTranslation() ){ 
			
		}
		else{ //parent has no translation, so 
			System.err.println("|   Parent entity ["+parentEntity+"] has no Translational composite");
		}
		
		if ( parentEntity.getRotationComposite().exists() ){
			
			System.out.println("|   Setting '"+parentEntity+"' as rotateable parent");	
			
			ParentComposite.Rotateable parentComposite = new ParentComposite.Rotateable(parentEntity);
			
			if ( flags.length == 0 || flags[0] == ROTATIONAL_CHILD){
				
				System.out.println("|   Setting '"+child+"' as rotateable child");	
				
				ChildComposite.Rotateable childComposite = parentComposite.createRotateableChild(child);	

				if ( childComposite == null ){ //child was not created, probably because it was already a child of something else
					System.err.println("|   WARNING: ["+parentEntity+"] was unable to accept ["+child+"] as a child");
					System.err.println("----");
					return;
				}

				Angled parentAngular = (Angled) parentEntity.getAngularComposite();
				
				parentEntity.addParentComposite( parentComposite );		//Give parent list of children
				parentAngular.addRotateable( parentComposite );	//Add children list to rotateables
				
				if ( !child.getAngularComposite().exists() ){
					addAngularComposite(child);
				}
				
				( ( Angled ) child.getAngularComposite() ).addRotateable( childComposite );
				
			}
			else if ( flags[0] == TRANSLATIONAL_CHILD ){
				
				System.out.println("|   Setting '"+child+"' as TRANSLATION ONLY child");	
				
				ChildComposite.TranslationOnly childComposite = parentComposite.createTranslationalChild(child);
				
				if ( childComposite == null ){ //child was not created, probably because it was already a child of something else
					System.err.println("|   WARNING: ["+parentEntity+"] was unable to accept ["+child+"] as a child");
					System.err.println("----");
					return;
				}
				
				Angled parentAngular = (Angled) parentEntity.getAngularComposite();
				
				parentEntity.addParentComposite( parentComposite );		//Give parent list of children
				parentAngular.addRotateable( parentComposite );	//Add children list to rotateables

				if ( !child.getTranslationComposite().exists() ){
					addTranslationTo(child);
				}

				child.setChildComposite(childComposite);
				
			}
			else{
				throw new InvalidParameterException();
			}

		}else{ 
			System.out.println("|   Parent isn't rotateable TODO MAKE TRANSLATION-OLNLY PARENT"); 
		}
		
		//If child has collider, remove from and add back to collision Engine as dynamic, in case it was registered as static
		if ( child.hasCollider() ){ 
			System.out.println("|   Switched ["+child+ "] collider to dynamic");

			child.getColliderComposite().changeColliderToDynamicInEngine();
			
			child.setTranslationComposite( new TranslationComposite(child) );
		} //else do nothing
		
		System.out.println("----");
		//parent.updateables.add(child);
		//child.updateables.add( (UpdateableComposite) parent.getTranslationComposite() );

		//#### AREA TO NOTIFY BrowserTree
		//something like,
		BrowserTreePanel browserTreePanel = board.getEditorPanel().getBrowserTreePanel();
		browserTreePanel.notifyParentChildRelationshipChanged(child, parentEntity);
	}
	
	public static void addScriptTo( EntityStatic entity , EntityBehaviorScript behavior ){

		entity.updateablesList.add(behavior);
		
	}
	
	public static void addLifespanTo( EntityStatic entity , int lifespanInFrames ){
		
		entity.updateablesList.add( new LifespanComposite(lifespanInFrames) );
		
	}
	
	
}
