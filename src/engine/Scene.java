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
		
		//ADD ENTITY TO SCENES MASTER ENTITY LIST
		entityList.add(entity);
		
		//RUN THROUGH AND ADD UPDATEABLE COMPOSITES TO UPDATER LIST right now only translation for testing
		
		if ( (entity.getTranslationComposite() instanceof UpdateableComposite) ){
			
			ownerBoard.updateablesList.add( entity.getTranslationComposite() );
			
		}
		
		//
		
		if ( !(entity.getGraphicComposite() instanceof GraphicCompositeNull) ){
			
			entity.getGraphicComposite().addCompositeToRenderer( ownerBoard.renderingEngine );
			
		}else{System.err.println("Couldn't add ["+entity+"] to renderer because it's missing a Graphic Composite ");}
		
		if ( entity.getColliderComposite() instanceof Collider ){
			
			entity.getColliderComposite().addCompositeToPhysicsEngine( ownerBoard.collisionEngine );
			
		}
		
	}
	
	public EntityStatic[] listEntities(){
		EntityStatic[] returnList = new EntityStatic[ this.entityList.size() ];
		this.entityList.toArray( returnList );
		return returnList;
	}
	
}
