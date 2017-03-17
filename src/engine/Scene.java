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
		
		if ( entity.getSpriteType() instanceof SpriteComposite ){
			
			entity.getSpriteComposite().addCompositeToRenderer( ownerBoard.renderingEngine );
			
		}
		
		if ( entity.getCollisionType() instanceof Collider ){
			
			entity.getColliderComposite().addCompositeToPhysicsEngine( ownerBoard.collisionEngine );
			
		}
		
	}
	
	public EntityStatic[] listEntities(){
		EntityStatic[] returnList = new EntityStatic[ this.entityList.size() ];
		this.entityList.toArray( returnList );
		return returnList;
	}
	
}
