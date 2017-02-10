package utility;

import java.util.ArrayList;

public class State {
	
	ArrayList<Trigger> triggers = new ArrayList<>();
	
	private State(){
		
	}
	
	protected void addTriggerToState( Trigger trigger ){
		triggers.add(trigger);
	}
	
	public void update(){
		
	}
	
}
