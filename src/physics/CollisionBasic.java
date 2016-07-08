package physics;

import entities.EntityDynamic;
import entities.EntityStatic;

public class CollisionBasic extends Collision {
	
	public CollisionBasic(EntityDynamic entity1, EntityStatic entity2){
		
		super(entity1, entity2);
		
		//GENERIC COLLISION
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		entityPrimary.setAccY(0); //cancel gravity
		entityPrimary.setColliding(true); // set entity collision flag for entity events. Possibly useless
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	@Override
	public void updateCollision(){ 
		
        //if player is at surface, apply friction and cancel gravity
        if (entityPrimary.getBoundingBox().getMaxY() == entitySecondary.getBoundingBox().getMinY() + 1){ 
        	entityPrimary.setDY(0);
        	entityPrimary.setDampeningX();
        }
        //if player is inside surface, push them out (should hard teleport them to surface, not push)
	    else if (entityPrimary.getBoundingBox().getMaxY() > entitySecondary.getBoundingBox().getMinY()){
	    	entityPrimary.setDY(-1);
	    	entityPrimary.setDampeningX();
		}     
	}
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		entityPrimary.setColliding(false); // unset entity collision flag. 
		//entityPrimary.setAccY(0.1f); //turn gravity back on
		entityPrimary.setAccX(0); //remove friction
	}
	
	public String toString(){
		return String.format("%s",collisionName);
	}
	

}
