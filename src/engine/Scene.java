package engine;

import java.util.ArrayList;

import entities.*;
import entityComposites.*;

public class Scene {
	
	private BoardAbstract ownerBoard;

	private ArrayList<LayeredEntity> entityList = new ArrayList<LayeredEntity>();

	public Scene( BoardAbstract ownerBoard ){
		this.ownerBoard = ownerBoard;

	}
	

	
	public void addEntity( EntityStatic entity , byte layer ){
		entityList.add( new LayeredEntity(entity,layer));
	}
	
	public void addEntity( EntityStatic entity ){
		
		System.out.println("Adding Entity ["+entity+"] to Current Scene");
		//ADD ENTITY TO SCENES MASTER ENTITY LIST
		entityList.add( new LayeredEntity(entity));
		
		//RUN THROUGH AND ADD UPDATEABLE COMPOSITES TO UPDATER LIST right now only translation for testing
		
		if ( (entity.getTranslationComposite() instanceof TranslationCompositeActive) ){
			TranslationCompositeActive trans = (TranslationCompositeActive) entity.getTranslationComposite();
			
			entity.addToUpdater(ownerBoard);
			trans.addToUpdater(ownerBoard);
		}
		
		//GRAPHICS COMPOSITE
		
		if ( !(entity.getGraphicComposite() instanceof GraphicCompositeNull) ){
			
			entity.getGraphicComposite().addCompositeToRenderer( ownerBoard.renderingEngine );
			
		}else{System.err.println("  Couldn't add ["+entity+"] to renderer because it's missing a Graphic Composite ");}
		
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
		
		System.out.println("----\n");
		
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
