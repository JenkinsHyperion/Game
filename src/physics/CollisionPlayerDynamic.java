package physics;

import java.awt.Rectangle;
import java.awt.geom.Line2D;

import entities.EntityDynamic;

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
	/*@Override
	public void updateCollision(){ 
				
		// Initial Momentum = mass_1 * velocity_1 + mass_2 + velocity_2      Final Momentum = (mass_1+mass_2)velocity_both
		// Initial Momentum = Final Momentum            Velocty_both = (Initial Momentum)/(mass_1 + mass_2)
			entityPrimary.setDY( (entityPrimary.getDY()*2 + entitySecondary.getDY()) /3 );
			entitySecondary.setDY( (entityPrimary.getDY()*2 + entitySecondary.getDY()) /3 );
		
			Line2D[] flushSides = entityPrimary.getBoundaryLocal().getContactingSides(entitySecondary.getBoundaryLocal());
			
			if ( flushSides != null ){
				
				contactingSide1 = flushSides[0];
				contactingSide2 = flushSides[1];
				
				yequilibrium = true;
				entityPrimary.setColliding(true);
				
				if (entityPrimary.getDX() > (0.1))
		    	{
					entityPrimary.setAccX(-0.1f);
		    	}
		    	else if (entityPrimary.getDX() < (-0.1))
		    	{
		    		entityPrimary.setAccX(0.1f);
		    	}
		    	else
		    	{
		    		//entityPrimary.setDX(entitySecondary.ge;
		    		entityPrimary.setDX(0);
		    		entityPrimary.setAccX(0);
		    	}
				
				
			}
			else {
				
				yequilibrium = false;
				entityPrimary.setY(entityPrimary.getY() - 1 );	
				
			}
		
		
		
		
	}*/
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		entityPrimary.setColliding(false); // unset entity collision flag. 
		entityPrimary.setAccX(0); //remove friction 
	}
	
	public String toString(){
		return String.format("%s",collisionDebugTag + " "+ yequilibrium);
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
