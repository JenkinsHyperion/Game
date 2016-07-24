package physics;

import java.awt.Rectangle;

import entities.EntityDynamic;
import entities.EntityStatic;
import entities.Player;

public class CollisionPlayerDynamic extends Collision {
	
	private EntityDynamic entityPrimary;
	private EntityDynamic entitySecondary;
	
	private boolean xequilibrium = false;
	private boolean yequilibrium = false;
	
	private int distance = 0;
	
	public CollisionPlayerDynamic(EntityDynamic entity1, EntityDynamic entity2){ 
		
		super(entity1, entity2);
		
		entityPrimary = entity1;
		entitySecondary = entity2;
		
		//GENERIC COLLISION
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		
		
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	@Override
	public void updateCollision(){ 
		
		
		Rectangle box1 = entityPrimary.getBoundingBox();
		Rectangle box2 = entitySecondary.getBoundingBox();
				
		// Initial Momentum = mass_1 * velocity_1 + mass_2 + velocity_2      Final Momentum = (mass_1+mass_2)velocity_both
		// Initial Momentum = Final Momentum            Velocty_both = (Initial Momentum)/(mass_1 + mass_2)
			entityPrimary.setDY( (entityPrimary.getDY()*2 + entitySecondary.getDY()) /3 );
			entitySecondary.setDY( (entityPrimary.getDY()*2 + entitySecondary.getDY()) /3 );

		
		//COLLISION FROM TOP
		if ( sideIsAllignedX(box1, box2) ) {
			
			
			if ( (int) box1.getCenterY() < (int) box2.getCenterY() ) {  
				
				if (  box2.getMinY() < ( box1.getMaxY() + entityPrimary.getDY() - 4) ) {
					entityPrimary.setY( (int) Math.round( box2.getMinY() - box1.height )  ) ;
				}
							
				if ( (int) box1.getMaxY() == (int) box2.getMinY()  ) {
					
					yequilibrium = true;
					entityPrimary.setColliding(true);

						entityPrimary.setDampeningX(entitySecondary.getDX()); 
					
				}
				else {
					//entityPrimary.setDY(0);	
					entityPrimary.setY(entityPrimary.getY()-1);
					
					yequilibrium = false;
				}
			
			}
			else { //COLLISION FROM BOTTOM
				
				if ( (int) box1.getMinY() == (int) box2.getMaxY() + 1  ) {
					entityPrimary.setDY(0);	// equilibrium with ceiling, for hanging mechanic perhaps?
				}
				else {				
					entityPrimary.setDY(0);	
					entityPrimary.setY(entityPrimary.getY()+1);		
				}
			
			}
			
		}

			
		//SIDE COLLISION
		if (sideIsAllignedY(box1, box2)) { // make sure primary entity's side is contacting 
			
			if ((int) box1.getCenterX() > (int) box2.getCenterX() ) { 
				
				if ((int) box1.getMinX() == (int) box2.getMaxX()) {
					
					xequilibrium = true;	
								
					
					//CLIMBING TEST 
					boolean temp = ((Player) entityPrimary).isClimbing();
					if (  !temp  ) {
						
						distance = (int) ( box2.getMinY() - entityPrimary.getY() ) ;
						if (distance > 20){distance = 20;}
						if (distance < 0){distance = 0;}
						
						// lock player in place while climbing
						entityPrimary.setAccX(0);
						entityPrimary.setDX(0); 
						//entityPrimary.setAccY(0);
						entityPrimary.setDY(0); 
						// move player to top while climb animation is playing
						entityPrimary.setY( (int) box2.getMinY() - 30);
						entityPrimary.setX( (int) box2.getMaxX() - 26);

						
						((Player) entityPrimary).setClimb( distance / 2 , false);	
						
					}// 
													
					entityPrimary.setDY(0);
					//entityPrimary.setAccY(0);
					
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
					boolean temp = ((Player) entityPrimary).isClimbing(); // deal with this better, move temp outside more
					if (  !temp  ) {
						
						distance = (int) ( box2.getMinY() - entityPrimary.getY() ) ;
						if (distance > 20){distance = 20;}
						if (distance < 0){distance = 0;}

						entityPrimary.setAccX(0);
						entityPrimary.setDX(0); // lock player in place while climbing
						//entityPrimary.setAccY(0);
						entityPrimary.setDY(0); 
						
						entityPrimary.setY( (int) box2.getMinY() - 30);
						entityPrimary.setX( (int) box2.getMinX() - 6);
						

						
						((Player) entityPrimary).setClimb( distance / 2 , true);	
						
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
		
		
		if (  ((Player) entityPrimary).isClimbing()  ) {
			
			if (  ( (Player) entityPrimary ).getPlayerState().getAnimation().getFrameNumber()  == 20 ) {
				
				((Player) entityPrimary).finishClimb();
				
			}	
														
		}
		
		
	}
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		entityPrimary.setColliding(false); // unset entity collision flag. 
		entityPrimary.setAccX(0); //remove friction
	}
	
	public String toString(){
		return String.format("%s",collisionName + " x: " + xequilibrium + " y: " + yequilibrium + "distance "+distance + " " +
				((Player) entityPrimary).getPlayerState().getAnimation().getFrameNumber());
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
