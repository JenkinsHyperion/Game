package utility;

import java.util.ArrayList;

public class StateController {
	
	private ArrayList<State> states = new ArrayList<>();
	private ArrayList<Trigger> triggers = new ArrayList<>();
	private State currentState;
	
	public StateController(){
		Trigger nullTrigger = new DefaultTrigger();
		addTrigger(nullTrigger);
		
	}
	
	private void addTrigger(Trigger trigger){
		triggers.add(trigger);
		for ( State state : states ){
			state.addTriggerToState(trigger);
		}
	}
	
	public Trigger createKeyTrigger(){
		Trigger newTrigger = new Trigger();
		addTrigger( newTrigger );
		return newTrigger;
	}
	
	public void triggerEvent( Trigger trigger ){
		
	}
	
	public State getCurrentState(){
		return currentState;
	}
	
	public void addState( State newState ){
		states.add(newState);
	}
	
	public void changeState( State state ){
		currentState = state;
	}
	
	static class DefaultTrigger extends Trigger{
		
	}
	
}
