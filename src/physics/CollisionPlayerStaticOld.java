package physics;

import java.awt.Rectangle;

import entities.EntityDynamic;
import entities.EntityStatic;
import entities.PlayerCharacter;

public class CollisionPlayerStaticOld extends Collision {
	
	private EntityDynamic entityPrimary;
	private EntityStatic entitySecondary;
	
	private boolean xequilibrium = false;
	private boolean yequilibrium = false;
	
	private int distance = 0;
	
	public CollisionPlayerStaticOld(EntityDynamic entity1, EntityStatic entity2){
		
		super(entity1, entity2);
		
		entityPrimary = entity1;
		entitySecondary = entity2;
		collisionName = entity1.name + " + " + entity2.name;
		
		//GENERIC COLLISION
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		
		entityPrimary.setDY(0);
		
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	@Override
	public void updateCollision(){ 
		
		//There might be a better pure mathematical way to do this
		
		Rectangle box1 = entityPrimary.getBoundingBox();
		Rectangle box2 = entitySecondary.getBoundingBox();
		
		
		//COLLISION FROM TOP
		if ( sideIsAllignedX(box1, box2) ) {
			
			
			/*//TESTING CLIPPING PREVENTION
			if ( ( entityPrimary.getY() + box1.height + entityPrimary.getDY() ) 
					> entitySecondary.getY() ){
				
				entityPrimary.setY(entitySecondary.getY() - box1.height  );
				
			}*/
			
			if ( (int) box1.getCenterY() < (int) box2.getCenterY() ) {  
				
				if (  box2.getMinY() < ( box1.getMaxY() + entityPrimary.getDY() - 4) ) {
					entityPrimary.setY( (int) Math.round( box2.getMinY() - box1.height )  ) ;
				}
				
				if ( (int) box1.getMaxY() == (int) box2.getMinY()  ) {
					
					yequilibrium = true;
					entityPrimary.setColliding(true);
							
					entityPrimary.setAccY(0);
					entityPrimary.setDY(0);
					
					//find better way to read if object is running or has traction
					//if ( ((Player) entityPrimary).getAccX() != 0){
						entityPrimary.setDampeningX(0);
					//}
					
				}
				else { //INSIDE BLOCK
					entityPrimary.setDY(0);	
					entityPrimary.setY(entityPrimary.getY()-1);
					
					yequilibrium = false;
				}
			
			}
			else { //COLLISION FROM BOTTOM
				
				//if ( entityPrimary.getDY() > 0 ) {
				
					if ( (int) box1.getMinY() == (int) box2.getMaxY() + 1  ) {
						entityPrimary.setDY(0);	// equilibrium with ceiling, for hanging mechanic perhaps?
					}
					else {				
						entityPrimary.setDY(0);	
						entityPrimary.setY(entityPrimary.getY()+1);		
					}
				//}
			
			}
			
		}

			
		//SIDE COLLISION
		if (sideIsAllignedY(box1, box2)) { // make sure primary entity's side is contacting 
			
			if ((int) box1.getCenterX() > (int) box2.getCenterX() ) { 
				
				if ((int) box1.getMinX() == (int) box2.getMaxX()) {
					
					xequilibrium = true;	
								
					
					//CLIMBING TEST 
					boolean temp = ((PlayerCharacter) entityPrimary).isClimbing();
					if (  !temp  ) {
						
						distance = (int) ( box2.getMinY() - entityPrimary.getY() ) ;
						if (distance > 20){distance = 20;}
						if (distance < 0){distance = 0;}
						
						// lock player in place while climbing
						entityPrimary.setAccX(0);
						entityPrimary.setDX(0); 
						entityPrimary.setAccY(0);
						entityPrimary.setDY(0); 
						// move player to top while climb animation is playing
						entityPrimary.setY( (int) box2.getMinY() - 30);
						entityPrimary.setX( (int) box2.getMaxX() - 26);

						
						((PlayerCharacter) entityPrimary).setClimb( distance / 2 , false);	
						
					}// 
													
					entityPrimary.setDY(0);
					entityPrimary.setAccY(0);
					
				}
				else {	// Inside block
					entityPrimary.setAccX(0);
					entityPrimary.setDX(0);
					entityPrimary.setX(entityPrimary.getX()+1);	
					
					xequilibrium = false;
				} 
			
			}
			else { 
				
				if ( (int) box1.getMaxX() == (int) box2.getMinX() ) {

					xequilibrium = true;

					//CLIMBING TEST 
					boolean temp = ((PlayerCharacter) entityPrimary).isClimbing(); // deal with this better, move temp outside more
					if (  !temp  ) {
						
						distance = (int) ( box2.getMinY() - entityPrimary.getY() ) ;
						if (distance > 20){distance = 20;}
						if (distance < 0){distance = 0;}

						entityPrimary.setAccX(0);
						entityPrimary.setDX(0); // lock player in place while climbing
						entityPrimary.setAccY(0);
						entityPrimary.setDY(0); 
						
						entityPrimary.setY( (int) box2.getMinY() - 30);
						entityPrimary.setX( (int) box2.getMinX() - 6);
						

						
						((PlayerCharacter) entityPrimary).setClimb( distance / 2 , true);	
						
					}// 
					
					
				}
				else {				
					entityPrimary.setAccX(0);
					entityPrimary.setDX(0);
					entityPrimary.setX(entityPrimary.getX()-1);	
					
					xequilibrium = false;
				}
			
			}
		}
		
		
		if (  ((PlayerCharacter) entityPrimary).isClimbing()  ) {
			
			if (  ( (PlayerCharacter) entityPrimary ).getPlayerState().getAnimation().getFrameNumber()  == 20 ) {
				
				((PlayerCharacter) entityPrimary).finishClimb();
				
			}	
														
		}
		
		
	}
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		entityPrimary.setColliding(false); // unset entity collision flag. 
		entityPrimary.setAccY(0.1f); //turn gravity back on
		entityPrimary.setAccX(0); //remove friction
	}
	
	public String toString(){
		return String.format("%s",collisionName + " " + yequilibrium);
	}
	
	public boolean sideIsAllignedX(Rectangle box1, Rectangle box2){
		if ( box1.getMinX() > box2.getMaxX() - 2 || box1.getMaxX() < box2.getMinX() + 2){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean sideIsAllignedY(Rectangle box1, Rectangle box2){
		if ( box1.getMinY() > box2.getMaxY() - 2 || box1.getMaxY() < box2.getMinY() + 2 ){
			return false;
		}
		else {
			return true;
		}
	}
	
	

}
