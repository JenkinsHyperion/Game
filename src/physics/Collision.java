package physics;

import java.awt.Rectangle;

import entities.EntityDynamic;
import entities.EntityStatic;

public class Collision {
	
	protected EntityDynamic entityPrimary;
	protected EntityStatic entitySecondary;
	public String collisionName;
	
	public Collision(EntityDynamic entity1, EntityStatic entity2){
		
		entityPrimary = entity1;
		entitySecondary = entity2;
		collisionName = entity1.name + " + " + entity2.name;
		
		//THIS TEST COLLISION IS A NORMAL SURFACE SUCH AS A FLAT PLATFORM
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	public void initCollision(){
		
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	public void updateCollision(){ 
		   
	}
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	public void completeCollision(){
		
	}
	
	
	//INTERNAL METHODS - DON'T ALTER BELOW THIS
	public boolean isComplete(){ // Check if entities are no longer colliding
		
		Rectangle r1 = entityPrimary.getBoundingBox();
				
		r1 = new Rectangle(r1.x - 1 , r1.y - 1, r1.width + 2, r1.height + 2 );
		
		if (r1.intersects(entitySecondary.getBoundingBox()) ){
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
		return String.format("%s",this);
	}
}
