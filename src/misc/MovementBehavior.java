package misc;

import entities.EntityDynamic;

public abstract class MovementBehavior {
	
	protected EntityDynamic owner;
	
	public abstract void updateAIPosition();
	
}
