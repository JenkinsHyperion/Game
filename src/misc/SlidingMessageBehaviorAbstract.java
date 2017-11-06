package misc;

import java.awt.Point;

import physics.Vector;

public abstract class SlidingMessageBehaviorAbstract extends MovementBehavior{

	
	protected Point target;
	
	public SlidingMessageBehaviorAbstract() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public abstract void updateAIPosition();

	public abstract void setTargetPoint(Point target);
	
	public abstract void setBehavior(SlidingMessageBehaviorAbstract newBehavior);
	
	
}
