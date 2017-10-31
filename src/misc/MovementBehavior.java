package misc;

import entityComposites.EntityStatic;

public abstract class MovementBehavior {
	
	protected EntityStatic owner;
	
	public abstract void updateAIPosition();
	
}
