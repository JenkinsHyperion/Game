package physics;

import entities.EntityDynamic;
import entities.EntityStatic;

public class CollisionBasic extends Collision {
	
	private EntityDynamic entityPrimary1;
	
	public CollisionBasic(EntityDynamic entity1, EntityStatic entity2){
		
		super(entity1,entity2);
		
		entityPrimary1 = entity1;
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		entityPrimary1.setAccY(0); //cancel gravity
		entityPrimary1.setColliding(true); // set entity collision flag for entity events. Possibly useless
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	@Override
	public void updateCollision(){ 
		
        //if player is at surface, apply friction and cancel gravity
        if (entityPrimary1.getBoundingBox().getMaxY() == entitySecondary.getBoundingBox().getMinY() + 1){ 
        	entityPrimary1.setDY(0);
        	entityPrimary1.setDampeningX(0);
        }
        //if player is inside surface, push them out (should hard teleport them to surface, not push)
	    else if (entityPrimary1.getBoundingBox().getMaxY() > entitySecondary.getBoundingBox().getMinY()){
	    	entityPrimary1.setDY(-1);
	    	entityPrimary1.setDampeningX(0);
		}     
	}
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		entityPrimary1.setColliding(false); // unset entity collision flag. 
		entityPrimary1.setAccY(0.1f); //turn gravity back on
		entityPrimary1.setAccX(0); //remove friction
	}
	
	public String toString(){
		return String.format("%s",collisionName);
	}
	

}
