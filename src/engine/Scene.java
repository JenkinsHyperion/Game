package engine;

import java.util.ArrayList;

import entities.*;
import entityComposites.*;
import utility.DoubleLinkedList;

public class Scene {
	
	private BoardAbstract ownerBoard;
	private String sceneName;
	private ArrayList<LayeredEntity> entityList = new ArrayList<LayeredEntity>();
	
	//private DoubleLinkedList<LayeredEntity> entityList2 = new DoubleLinkedList<LayeredEntity>();
	
	private String I = ""; //Indentations tring for console printing debugging

	public Scene( BoardAbstract ownerBoard ){
		this.ownerBoard = ownerBoard;
		this.sceneName = "Unnamed Scene";
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
		
		if ( checkTranslation(entity) ){
			updateableEntity = true;
		}
		
		//ROTATION 
		
		if ( (entity.getRotationComposite().exists() ) ){
			UpdateableComposite rotation = (UpdateableComposite) entity.getRotationComposite();
			updateableEntity = true;
			if ( rotation.addCoreMathToUpdater(ownerBoard) ){
				System.out.println( I+"Adding dynamic rotation composite to updater thread");
			}else{
				System.out.println( I+"Dynamic rotation composite already in updater thread");
			}
		}
		
		//GRAPHICS COMPOSITE
		
		if ( entity.getGraphicComposite().exists() ){
			
			((GraphicComposite.Active) entity.getGraphicComposite()).addCompositeToRenderer( ownerBoard.renderingEngine );
			
			System.out.println(I+"Adding graphics composite to renderer");
			
		}else{System.err.println(I+"Couldn't add ["+entity+"] to renderer because it's missing a Graphic Composite ");}
		
		//COLLIDER COMPOSITE
		if ( groups.length == 0){
			checkForAndRegisterCollider(entity);
		}else{
			for ( String group : groups ){
				System.err.println(I+"Adding ["+entity+"] to Collider Group "+group);
				checkForAndRegisterGroupedCollider(entity,group);
			}

		}
		
		/*if ( entity.hasUpdateables() ){
			System.out.println(I+"Collecting updateables");
			updateableEntity = true;
			UpdateableComposite[] updateables = entity.getUpdateables();
			for ( UpdateableComposite updateable : updateables){ //COLLECT UPDATEABLES THAT ARE CHILDREN ENTITIES
				if ( updateable instanceof EntityStatic ){
					this.addEntity( (EntityStatic)updateable  );
				}
			}
		}*/
		
		if ( updateableEntity ){
			entity.addToUpdater(ownerBoard);
			System.out.println(I+"Adding entity to updater");
		}

		if( !I.isEmpty() )
			I = I.substring(4);

		
		System.out.println( I+"----\n");
		
		
		
	}
	
	 /* #################################################################################################################
	 *	INTERNAL ADDER FUCNTIONALITY
	 */
	
	private boolean checkTranslation( EntityStatic entity ){
		
		if ( ( entity.getTranslationComposite().exists() ) ){
			UpdateableComposite trans = (UpdateableComposite) entity.getTranslationComposite();

			if ( trans.addCoreMathToUpdater(ownerBoard) ){
				System.out.println( I+"Adding dynamic translation composite to updater");
			}else
				System.err.println( I+"Dynamic translation composite was not added to updater");
			
			return true;
		}else{
			return false;
		}
		
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
	
	private void checkForAndRegisterGroupedCollider( EntityStatic entity, String groupName ){
		
		if ( entity.getColliderComposite().exists() ){
			
			if ( entity.getTranslationComposite().exists() ){  
				
				entity.getColliderComposite().addCompositeToPhysicsEngineDynamic( ownerBoard.collisionEngine, groupName );
			}
			else{ 
				entity.getColliderComposite().addCompositeToPhysicsEngineStatic( ownerBoard.collisionEngine, groupName );
			}
			
		}else{
			System.out.println(I+"No collider detected"); 
		}
	}
	
	
	public void removeEntity( int index ){
		this.entityList.remove(index);
		for( int i = index ; i < entityList.size() ; i++ ){
			entityList.get(i).entity.indexShift();
		}
	}
	
	
	public void addBackgroundSprite( int layer , EntityStatic entity ){
		entityList.add( new LayeredEntity(entity , (byte) layer) );
		ownerBoard.renderingEngine.layersList[layer].addGraphicToLayer(entity);
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
