package engine;

import java.util.ArrayList;

import entities.*;
import entityComposites.*;

public class Scene {
	
	private BoardAbstract ownerBoard;

	private ArrayList<EntityStatic> entityList = new ArrayList<EntityStatic>();

	public Scene( BoardAbstract ownerBoard ){
		this.ownerBoard = ownerBoard;
	}
	
	public void addEntity( EntityStatic entity ){
		
		System.out.println("Adding Entity to Scene");
		//ADD ENTITY TO SCENES MASTER ENTITY LIST
		entityList.add(entity);
		
		//RUN THROUGH AND ADD UPDATEABLE COMPOSITES TO UPDATER LIST right now only translation for testing
		
		if ( (entity.getTranslationComposite() instanceof TranslationCompositeActive) ){
			TranslationCompositeActive trans = (TranslationCompositeActive) entity.getTranslationComposite();
			ownerBoard.updateablesList.add( trans );
		}
		
		//GRAPHICS COMPOSITE
		
		if ( !(entity.getGraphicComposite() instanceof GraphicCompositeNull) ){
			
			entity.getGraphicComposite().addCompositeToRenderer( ownerBoard.renderingEngine );
			
		}else{System.err.println("     Couldn't add ["+entity+"] to renderer because it's missing a Graphic Composite ");}
		
		//COLLIDER COMPOSITE
		
		if ( entity.getColliderComposite() instanceof Collider ){
			
			if ( entity.getTranslationComposite() instanceof TranslationCompositeActive ){  
				System.out.println("     Adding "+entity+" as dynamic");
				entity.getColliderComposite().addCompositeToPhysicsEngineDynamic( ownerBoard.collisionEngine );
			}
			else{ 
				System.out.println("     Adding "+entity+" as static");
				entity.getColliderComposite().addCompositeToPhysicsEngineStatic( ownerBoard.collisionEngine );
			}
			
		}
		
		//
		
	}
	
	
	protected void disableEntity( int index ){
		this.entityList.get(index).disable();
		this.entityList.remove(index);
	}
	
	
	public void addBackgroundSprite( int layer , EntityStatic entity ){
		entityList.add(entity);
		ownerBoard.renderingEngine.layersList[layer].addEntity(entity);
	}
	
	public EntityStatic[] listEntities(){
		EntityStatic[] returnList = new EntityStatic[ this.entityList.size() ];
		this.entityList.toArray( returnList );
		return returnList;
	}
	
	public void deconstructScene(){
		
	}
	
}
