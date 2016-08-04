package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import entities.EntityDynamic;
import entities.EntityStatic;
import entities.Player;

public class CollisionPlayerStatic extends Collision {
	
	private EntityDynamic entityPrimary;
	private EntityStatic entitySecondary;
	
	private boolean xequilibrium = false;
	private boolean yequilibrium = false;
	
	private int distance = 0;
	
	public CollisionPlayerStatic(EntityDynamic entity1, EntityStatic entity2){
		
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
		
		Line2D[] flushSides = entityPrimary.getLocalBoundary().getFlushSides(entitySecondary.getLocalBoundary());
		
		if ( flushSides != null ){
			
			contactingSide1 = flushSides[0];
			contactingSide2 = flushSides[1];
			
			yequilibrium = true;
			entityPrimary.setDY(0);
			entityPrimary.setAccY(0);
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
			entityPrimary.setDY(0);
			entityPrimary.setY(entityPrimary.getY() - 1 );	
			
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
