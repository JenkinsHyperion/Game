package physics;

import physics.Collision.Resolution;

public abstract class ResolutionState {
	
	protected abstract void triggerEvent( Resolution resolution );
	
}
