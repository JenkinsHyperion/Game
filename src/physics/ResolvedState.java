package physics;

import physics.Collision.Resolution;

public class ResolvedState extends ResolutionState {
	
	private static ResolvedState resolvedState = new ResolvedState();
	
	private ResolvedState(){
		
	}
	
	public static ResolutionState resolved(){
		return resolvedState;
	}

	@Override
	protected void triggerEvent( Resolution resolution) {
		// DO NOTHING
		
	}
	
}
