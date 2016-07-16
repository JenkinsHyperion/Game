package physics;

import java.awt.Rectangle;

import entities.EntityDynamic;
import entities.EntityStatic;
import entities.Player;

public class CollisionGenericTest extends Collision {
	
	private boolean xequilibrium = false;
	private boolean yequilibrium = false;
	
	public CollisionGenericTest(EntityDynamic entity1, EntityStatic entity2){
		
		super(entity1, entity2);
		
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
		
		
		//COLLISION FROM TOP
		if ( sideIsAllignedX(box1, box2) ) {
			
			
			if ( (int) box1.getCenterY() < (int) box2.getCenterY() ) {  
				
				if (  box2.getMinY() < ( box1.getMaxY() + entityPrimary.getDY() - 4) ) {
					entityPrimary.setY( (int) Math.round( box2.getMinY() - box1.height )  ) ;
				}
				
				if ( (int) box1.getMaxY() == (int) box2.getMinY()  ) {
					
					yequilibrium = true;
							
					entityPrimary.setAccY(0);
					entityPrimary.setDY(0);
					
					//find better way to read if object is running or has traction
					//if ( ((Player) entityPrimary).getAccX() != 0){
						entityPrimary.setDampeningX();
					//}
					
				}
				else {
					entityPrimary.setDY(0);	
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
					
					
				}
				else {				
					entityPrimary.setAccX(0);
					entityPrimary.setDX(0);
					entityPrimary.setX(entityPrimary.getX()-1);	
					
					xequilibrium = false;
				}
			
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
		return String.format("%s",collisionName + " x: " + xequilibrium + " y: " + yequilibrium);
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
