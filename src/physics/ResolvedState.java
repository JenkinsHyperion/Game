package physics;

public class ResolvedState extends ResolutionState {
	
	private static ResolvedState resolvedState = new ResolvedState();
	
	private ResolvedState(){
		
	}
	
	@Override
	protected void triggerEvent(){
		//DO NOTHING, collision has been resolved
	}
	
	public static ResolutionState resolved(){
		return resolvedState;
	}
	
}
