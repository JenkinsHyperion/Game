package utility;


public class Counter{
	
	private int counter;
	private final CounterState inc = new Increment();
	private final CounterState dec = new Decrement();
	private final CounterState idle = new Idle();
	private static final int INCREMENT = 1;
	private static final int DECREMENT = 2;
	private static final int COUNT_UP_TO = 3;
	private static final int COUNT_DOWN_FROM = 4;
	private CounterState currentState;
	
	public Counter(){
		currentState = inc;
	}
	
	public Counter( int counterType ){
		if (counterType == 1){
			currentState = inc;
		}
		else if (counterType == 2){
			currentState = dec;
		}
		else
			System.err.println("Invalid flag in counter.");
			currentState = inc;
	}
	
	public Counter( int counterType , int number , Trigger trigger){
		if (counterType == 3){
			currentState = new CountUpTo(number, trigger);
		}
		else if (counterType == 4){
			currentState =  new CountDownFrom(number, trigger);
		}
		else
			currentState = inc;
	}
	
	
	
	public int getCount(){
		return counter;
	}
	
	public void stop(){
		currentState = idle;
	}
	
	public void updateCounter(){
		currentState.updateState();
	}
	
	private abstract class CounterState{
		public abstract void updateState();
	}
	
	private class Idle extends CounterState{
		@Override
		public void updateState() { /*DO NTOHING*/}
	}
	
	private class Increment extends CounterState{
		@Override
		public void updateState() {
			counter++;
		}
	}
	
	private class Decrement extends CounterState{
		@Override
		public void updateState() {
			counter--;
		}
	}
	
	private class CountUpTo extends CounterState{
		private final int max;
		private final Trigger trigger;
		public CountUpTo( int max , Trigger trigger){
			this.max = max;
			this.trigger = trigger;
		}
		
		@Override
		public void updateState() {
			if ( counter < max )
				counter+=10;
			else{
				stop();
				this.trigger.activate();
				//OPTIMIZE Instead of do nothing idle state, remove self from updateable call 
			}
		}
	}
	
	private class CountDownFrom extends CounterState{
		private final Trigger trigger;
		public CountDownFrom( int start , Trigger trigger){
			counter = start;
			this.trigger = trigger;
		}
		@Override
		public void updateState() {
			if ( counter > 0 )
				counter--;
			else {
				stop();
				this.trigger.activate();
			}
		}
	}
	
}