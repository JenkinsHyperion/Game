package physics;

import java.awt.Rectangle;
import java.util.LinkedList;

import engine.Board;
import entities.EntityDynamic;
import entities.EntityStatic;
import entities.Player;

public class CollisionEngine {

	private Board currentBoard;
    private LinkedList<Collision> collisionsList = new LinkedList<Collision>(); 
	
	public CollisionEngine(Board board){
		currentBoard = board;
		
	}
	
	//check collision list and return true if two entities are already colliding
	private boolean hasActiveCollision(EntityStatic entity1, EntityStatic entity2){
		    	
		for ( Collision activeCollision : collisionsList){
					
			if ( activeCollision.isActive(entity1, entity2) ) {
				return true;
			}
				
		}
		return false;
	}
	    
	//Update status of collisions, run ongoing commands in collision, and destroy collisions that have completed
	//USE ARRAY LIST ITTERATOR INSTEAD OF FOR LOOP SINCE REMOVING INDEX CHANGES SIZE
	private void updateCollisions(){
	    	
	    for ( int i = 0 ; i < collisionsList.size() ; i++ ){
	    		
	    	//if collision is complete, remove from active list
	    	if (collisionsList.get(i).isComplete() ) {
	    		collisionsList.remove(i);
	    	}
	    	else {
	    			
    			collisionsList.get(i).updateCollision(); //Run commands from inside collision object
	    			
    		}
	  		
    	}
	    	
    }
	    
    //THIS IS THE MAIN BODY OF THE COLLISION ENGINE
    public void checkCollisions() { 
	    	
    	Player player = currentBoard.getPlayer();
	    	
        Rectangle r0 = currentBoard.getPlayer().getBoundingBox(); // get bounding box of player first
	 
        //make larger box to represent distance at which a new collision will be opened 
        Rectangle r3 = new Rectangle(r0.x - 1 , r0.y - 1, r0.width + 2, r0.height + 2); 

        //KILL PLAYER AT BOTTOM OF SCREEN
        if (r3.getMinY() > currentBoard.getboundaryY()) {  
	        	
        	//could be teleport(x,y) or reposition(x,y) method in either player or parent entity classes
        	player.setX(100);
        	player.setY(100);
        	player.setDX(0);
        	player.setDY(0);
        	player.setAccX(0);
        	player.setAccY(0.1f);
	        	
        }//
	        
	        
        // Check collisions between player and static objects
        for (EntityStatic staticEntity : currentBoard.getStaticEntities() ) {    
        	
        		if ( player.getDeltaBoundary().checkForInteraction( staticEntity.getLocalBoundary()) ) {
        			if (!hasActiveCollision(player,staticEntity)) { //check to see if collision isn't already occurring
        				collisionsList.add(new CollisionPlayerStaticSAT(player,staticEntity)); // if not, add new collision event
        			} 	
        		}
        }
	        
	        // TEST LASER COLLISION 
	        /*for (EntityStatic stat : staticEntitiesList) {                	
	        	if ( stat.getLocalBoundary().boundaryIntersects(laser.getBoundary()) ) {
		            	
		            //OPEN COLLISION
		            if (!hasActiveCollision(laser,stat)) { //check to see if collision isn't already occurring
		            	collisionsList.add(new Collision(laser, stat)); // if not, add new collision event
		            } 		            
		   		}
	        }*/
	        
	        
	        
	        //Check collisions between dynamics entities and static entities
        for (EntityDynamic dynamicEntity : currentBoard.getDynamicEntities()) { //index through physics entities        
	            
        	Rectangle r1 = dynamicEntity.getBoundingBox();            
            for (EntityStatic statEntity : currentBoard.getDynamicEntities()){ // index through static entities	
            	
            	Rectangle r2 = statEntity.getBoundingBox();
            
	            if (r1.intersects(r2)) {
	            	
	            	if (!hasActiveCollision(dynamicEntity,statEntity)) { 
	            		collisionsList.add(new CollisionBasic(dynamicEntity,statEntity)); 
	            	}
	            }  
	            
            }
            
        }
        

        // Check collisions between player and physics objects
        for (EntityDynamic physics : currentBoard.getPhysicsEntities() ) { 
        	
        	Rectangle r4 = physics.getBoundingBox();
	        	
		        if (r3.intersects(r4) ) { 
	        	 //if (r3.intersects(new Rectangle(clickPosition, new Dimension(10,10))) ) {
		        	
	            	//OPEN COLLISION
	            	if (!hasActiveCollision(player,physics)) { //check to see if collision isn't already occurring
	            		collisionsList.add(new CollisionPlayerDynamic(player,physics)); // if not, add new collision event
		            	} 	
	        }
        }
        
    	updateCollisions(); // calculate and remove old collisions    
        
    }
	    

    public LinkedList<Collision> list(){ return collisionsList; }
	    
}
	   


