package misc;

import physics.Vector;

public final class InactiveBehavior extends MovementBehavior{

	@Override
	public void updateAIPosition() {
		//DO NOTHING
	}
	
	@Override
	public Vector calculateVector() {
		return Vector.zeroVector;
	}

}
