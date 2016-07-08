package physics;

import entities.EntityDynamic;
import entities.EntityStatic;

public class Collision {
	
	private EntityDynamic entityPrimary;
	private EntityStatic entitySecondary;
	public String collisionName;
	
	public Collision(EntityDynamic entity1, EntityStatic entity2){

		entityPrimary = entity1;
		entitySecondary = entity2;
		collisionName = entity2.name;
		
		//THIS TEST COLLISION IS A NORMAL SURFACE SUCH AS A FLAT PLATFORM
		
		//INITAL COLLISION COMMANDS
		entityPrimary.setAccY(0); //remove normal force (against gravity)
		entityPrimary.setColliding(true);
		
	}
	
	//FINAL COLLISION COMMANDS - Last commands before collision object self destructs
	public void completeCollision(){
		entityPrimary.setColliding(false); //messing with colliding flag on entities. Possibly useful.
		entityPrimary.setAccY(0.1f); //turn gravity back on
		entityPrimary.setAccX(0); //remove friction
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	public void updateCollision(){ 
		
        //if player is at surface, apply friction and cancel gravity
        if (entityPrimary.getBoundingBox().getMaxY() == entitySecondary.getBoundingBox().getMinY() + 1){ 
        	entityPrimary.setDY(0);
        	entityPrimary.setDampeningX();
        }
        //if player is inside surface, push them out (should hard teleport them to surface, not push)
	    else if (entityPrimary.getBoundingBox().getMaxY() > entitySecondary.getBoundingBox().getMinY()){
	    	entityPrimary.setDY(-1);
		}     
	}
	
	public boolean isComplete(){ // Check if entities are no longer colliding
		
		if (entityPrimary.getBoundingBox().intersects(entitySecondary.getBoundingBox()) ){
			return false;
		}
		else { // entities are no longer colliding
			completeCollision(); // run final commands
			return true; // return true for scanning loop in Board to delete this collision
		}
	}
	
	//When Board detects collision, check to see if it's already in the list of active collisions
	public boolean isActive(EntityStatic entity1, EntityStatic entity2){
		if (entity1 == entityPrimary){
			if (entity2 == entitySecondary){
				return true;
			}
			else {
				return false;
			}
			
		}
		else if (entity1 == entitySecondary){
			if (entity2 == entityPrimary){
				return true;
			}
			else {
				return false;
			}
		}
		

		else{
			return false;
		}
	}
	
	public String toString(){
		return String.format("%s",collisionName);
	}
}
