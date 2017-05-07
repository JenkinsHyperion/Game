package engine;

import java.util.ArrayList;

import entities.*;
import entityComposites.*;

public class Scene {
	
	private BoardAbstract ownerBoard;

	private ArrayList<LayeredEntity> entityList = new ArrayList<LayeredEntity>();
	
	private String I = ""; //Indentations tring for console printing debugging

	public Scene( BoardAbstract ownerBoard ){
		this.ownerBoard = ownerBoard;

	}
	
	public void addEntityToList( EntityStatic entity , byte layer ){
		entityList.add( new LayeredEntity(entity,layer));
	}
	
	public void addEntity( EntityStatic entity ){
		
		boolean updateableEntity = false;
		System.out.println(I+"Adding Entity ["+entity+"] to Current Scene");
		//ADD ENTITY TO SCENES MASTER ENTITY LIST
		entityList.add( new LayeredEntity(entity));
		
		I = I+"|  ";//temporary visual indentation for console output
		
		//RUN THROUGH AND ADD UPDATEABLE COMPOSITES TO UPDATER LIST
		///////////////////////////////////////////////////////////////////////////////////////////////////
		
		if ( (entity.getTranslationComposite() instanceof UpdateableComposite) ){
			UpdateableComposite trans = (UpdateableComposite) entity.getTranslationComposite();
			updateableEntity = true;
			if ( trans.addCompositeToUpdater(ownerBoard) ){
				System.out.println( I+"Adding dynamic translation composite to updater thread");
			}else
				System.out.println( I+"Dynamic translation composite already in updater thread");
		}
		
		//ROTATION 
		
		if ( (entity.getRotationComposite() instanceof UpdateableComposite) ){
			UpdateableComposite rotation = (UpdateableComposite) entity.getRotationComposite();
			updateableEntity = true;
			if ( rotation.addCompositeToUpdater(ownerBoard) ){
				System.out.println( I+"Adding dynamic rotation composite to updater thread");
			}else{
				System.out.println( I+"Dynamic rotation composite already in updater thread");
			}
		}
		
		//GRAPHICS COMPOSITE
		
		if ( !(entity.getGraphicComposite() instanceof GraphicCompositeNull) ){
			
			entity.getGraphicComposite().addCompositeToRenderer( ownerBoard.renderingEngine );
			
			System.out.println(I+"Adding graphics composite to renderer");
			
		}else{System.err.println(I+"Couldn't add ["+entity+"] to renderer because it's missing a Graphic Composite ");}
		
		//COLLIDER COMPOSITE
		
		if ( !(entity.getColliderComposite() instanceof ColliderNull) ){
			
			if ( entity.getTranslationComposite() instanceof TranslationCompositeActive ){  
				//System.out.println("     Adding "+entity+" as dynamic");
				entity.getColliderComposite().addCompositeToPhysicsEngineDynamic( ownerBoard.collisionEngine );
			}
			else{ 
				//System.out.println("     Adding "+entity+" as static");
				entity.getColliderComposite().addCompositeToPhysicsEngineStatic( ownerBoard.collisionEngine );
			}
			
		}
		
		if ( entity.hasUpdateables() ){
			System.out.println(I+"Collecting updateables");
			updateableEntity = true;
			UpdateableComposite[] updateables = entity.getUpdateables();
			for ( UpdateableComposite updateable : updateables){ //COLLECT UPDATEABLES THAT ARE CHILDREN ENTITIES
				if ( updateable instanceof EntityStatic ){
					this.addEntity( (EntityStatic)updateable  );
				}
			}
		}
		
		if ( updateableEntity ){
			entity.addToUpdater(ownerBoard);
			System.out.println(I+"Adding entity to updater");
		}

		if( !I.isEmpty() )
			I = I.substring(3);

		
		System.out.println( I+"----\n");
		

		
	}
	
	
	protected void disableEntity( int index ){
		this.entityList.get(index).disable();
		this.entityList.remove(index);
	}
	
	
	public void addBackgroundSprite( int layer , EntityStatic entity ){
		entityList.add( new LayeredEntity(entity , (byte) layer) );
		ownerBoard.renderingEngine.layersList[layer].addEntity(entity);
	}
	
	public EntityStatic[] listEntities(){
		
		EntityStatic[] returnList = new EntityStatic[ this.entityList.size() ];
		
		for (int i = 0 ; i < this.entityList.size() ; i++){
			returnList[i] = entityList.get(i).entity;
		}
		return returnList;
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
