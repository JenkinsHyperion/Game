package misc;


import entityComposites.EntityStatic;
import physics.Vector;

public abstract class MovementBehavior {
	
	protected EntityStatic owner;
	
	public abstract void updateAIPosition();
	public abstract Vector calculateVector();
	
}
