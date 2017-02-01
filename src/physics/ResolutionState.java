package physics;

import physics.Collision.Resolution;

public abstract class ResolutionState {
	
	protected Resolution resolution;
	
	public Resolution getResolution(){
		return resolution;
	}
	
	protected abstract void triggerEvent( Resolution resolution );
	
}
