package engine;

import java.util.ArrayList;

import entities.*;
import entityComposites.*;
import physics.CollisionEngine.ColliderGroup;
import sprites.Sprite;
import utility.DoubleLinkedList;

public class Scene {
	
	private BoardAbstract ownerBoard;
	private String sceneName;
	private ArrayList<LayeredEntity> entityList = new ArrayList<LayeredEntity>();
	
	//private DoubleLinkedList<LayeredEntity> entityList2 = new DoubleLinkedList<LayeredEntity>();
	
	private String I = ""; //Indentation string for console printing debugging

	public Scene( BoardAbstract ownerBoard ){
		this.ownerBoard = ownerBoard;
		this.sceneName = "Unnamed Scene";
	}
	
	public boolean isWorking(){
		return this.ownerBoard.isWorking();
	}
	
	public void addEntityToList( EntityStatic entity , byte layer ){
		entity.addEntityToScene( this, entityList.size() );
		entityList.add( new LayeredEntity(entity,layer));
	}
	
	
	
	public void addEntity( EntityStatic entity, String...groups ){
		//NOTIFY BROWSER TREE THAT ENTITY WAS ADDED TO SCENE
		if (ownerBoard.editorPanel != null)
			ownerBoard.editorPanel.getBrowserTreePanel().notifyTreeAddedEntity(entity);
		boolean updateableEntity = false;
		System.out.println(I+"ADDING ENTITY ["+entity+"] to Current Scene, index "+entityList.size());
		//ADD ENTITY TO SCENES MASTER ENTITY LIST
		addEntityToList(entity,(byte) 0);

		I = I+"|   ";//temporary visual indentation for console output

		//RUN THROUGH AND ADD UPDATEABLE COMPOSITES TO UPDATER LIST
		///////////////////////////////////////////////////////////////////////////////////////////////////
		
		if ( addTranslationAndReturnIfUpdateable(entity) ){ //TRANSLATION
			updateableEntity = true;
		}
		
		if (addRotationAndReturnIfUpdateable(entity) ){		//ROTATION 
			updateableEntity = true;
		}
		
		checkForAndRegisterGraphic(entity); 	//GRAPHICS COMPOSITE
		
		//COLLIDER COMPOSITE
		if ( groups.length == 0){
			checkForAndRegisterCollider(entity);
		}else{
			for ( String group : groups ){
				checkForAndRegisterGroupedCollider(entity,group);
			}

		}
		
		if (entity.hasUpdateables()){	//if entity has any reference to miscellaneous updateables, add entity to updater
			updateableEntity = true;
		}
		
		if ( updateableEntity ){	//If any composite was updateable, add entitiy to updater thread
			entity.addUpdateableEntityToUpdater(ownerBoard);
		}

		if( !I.isEmpty() )
			I = I.substring(4);

		System.out.println( I+"----\n");	
	}



	
	 /* #################################################################################################################
	 *	INTERNAL ADDER FUCNTIONALITY
	 */
	
	private boolean addTranslationAndReturnIfUpdateable( EntityStatic entity ){
		
		if ( ( entity.getTranslationComposite().exists() ) ){
			UpdateableComposite trans = (UpdateableComposite) entity.getTranslationComposite();

			if ( trans.addUpdateableCompositeTo(entity) ){
				System.out.println( I+"Adding Translation Composite to ["+entity+"] updateables");
			}else
				System.err.println( I+"Translation Composite was already in ["+entity+"] updateables, probably already initialized");
			
			return true;
		}else{
			return false;
		}
		
	}
	
	private boolean addRotationAndReturnIfUpdateable( EntityStatic entity ){
		
		if ( (entity.getRotationComposite().exists() ) ){
			UpdateableComposite rotation = (UpdateableComposite) entity.getRotationComposite();
			if ( rotation.addUpdateableCompositeTo(entity) ){
				System.out.println( I+"Dynamic rotation composite added to updater thread");
			}else{
				System.out.println( I+"Dynamic rotation composite already in updater thread");
			}
			return true;
		}else{
			return false;
		}
	}
	
	private void checkForAndRegisterGraphic( EntityStatic entity ){
		
		if ( entity.getGraphicComposite().exists() ){
			
			((GraphicComposite.Static) entity.getGraphicComposite()).addCompositeToRenderer( ownerBoard.renderingEngine );
			
			System.out.println(I+"Graphics composite added to rendering engine");
			
		}else{System.err.println(I+"No Graphic Composite on ["+entity+"]");}
	}
	
	private void checkForAndRegisterCollider( EntityStatic entity ){
		
		if ( entity.getColliderComposite().exists() ){
			
			if ( entity.getTranslationComposite().exists() ){  
				//System.out.println("     Adding "+entity+" as dynamic");
				entity.getColliderComposite().addCompositeToPhysicsEngineDynamic( ownerBoard.collisionEngine );
			}
			else{ 
				//System.out.println("     Adding "+entity+" as static");
				entity.getColliderComposite().addCompositeToPhysicsEngineStatic( ownerBoard.collisionEngine );
			}
			
		}else{
			System.out.println(I+"No collider detected"); 
		}
	}
	
	protected void checkForAndRegisterGroupedCollider( EntityStatic entity, String groupName ){
		
		if ( entity.getColliderComposite().exists() ){
			
			if ( entity.getTranslationComposite().exists() ){  
				
				System.out.println(I+"Adding ["+entity+"] to Collider Group '"+groupName+"' as dynamic");
				entity.getColliderComposite().addCompositeToPhysicsEngineDynamic( ownerBoard.collisionEngine, groupName );
			}
			else{ 
				System.out.println(I+"Adding ["+entity+"] to Collider Group '"+groupName+"' as static");
				entity.getColliderComposite().addCompositeToPhysicsEngineStatic( ownerBoard.collisionEngine, groupName );
			}
			
		}else{
			System.out.println(I+"No collider on ["+entity+"] detected"); 
		}
	}
	
	
	public void refreshEntityComposites( EntityStatic entity ){
		
		if ( entity.getColliderComposite().exists() ){
			
			if ( entity.getTranslationComposite().exists() ){  
				//System.out.println("     Adding "+entity+" as dynamic");
				entity.getColliderComposite().addCompositeToPhysicsEngineDynamic( ownerBoard.collisionEngine );
			}
			else{ 
				//System.out.println("     Adding "+entity+" as static");
				entity.getColliderComposite().addCompositeToPhysicsEngineStatic( ownerBoard.collisionEngine );
			}
		}
	}
	
	public void removeEntity( int index ){
		this.entityList.remove(index);
		// vvvv is crashing sometimes
		/*for( int i = index ; i < entityList.size() ; i++ ){
			entityList.get(i).entity.indexShift();
		}*/
	}
	
	
	public void addBackgroundSprite( int layer , EntityStatic entity ){
		entityList.add( new LayeredEntity(entity , (byte) layer) );
		ownerBoard.renderingEngine.layersList[layer].addGraphicToLayer(entity);
	}
	
	public EntityStatic createBackgroundSprite( int layer , Sprite sprite , int x, int y){ 
		
		EntityStatic newSpriteEntity = new EntityStatic(x,y);
		CompositeFactory.addGraphicTo(newSpriteEntity, sprite, false);
		
		addBackgroundSprite(layer,newSpriteEntity);
		
		return newSpriteEntity;
	}
	
	public EntityStatic[] listEntities(){
		
		EntityStatic[] returnList = new EntityStatic[ this.entityList.size() ];
		
		for (int i = 0 ; i < this.entityList.size() ; i++){
			returnList[i] = entityList.get(i).entity;
		}
		return returnList;
	}
	
	public void uponSwitchingToThisScene(){
		System.out.println( "Scene: Switced to Scene " + this.sceneName );
	}
	
	public void deconstructScene(){
		
	}
	
	
	
	private class LayeredEntity{
		protected EntityStatic entity;
		protected byte layer;
		protected LayeredEntity( EntityStatic entity ){
			this.entity= entity;
			this.layer = 0;
		}
		protected LayeredEntity( EntityStatic entity , byte layer ){
			this.entity= entity;
			this.layer = layer;
		}
		protected void disable(){
			this.entity.disable();
			this.entity = null;
		}
	}
	
	
}
