package testEntities;

import java.util.concurrent.ThreadLocalRandom;

import engine.TestBoard;
import entities.*;
import entityComposites.ColliderNull;
import entityComposites.GraphicComposite;
import sprites.SpriteStillframe;
//import sun.management.counter.Counter;
import utility.Trigger;

public class PlantTwigSegment extends EntityDynamic {

	private int angle;
	private TestBoard board;
	private int maxGrowth;

	private Counter growth;
	private Counter lifespan;
	private boolean dead;
	
	private int numberFromLastBranch = 0;
	
	public PlantTwigSegment(int x, int y, int maxGrowth , TestBoard board) {
		super(x, y);
		this.board = board;
		this.maxGrowth = maxGrowth;
		init( maxGrowth );
		
	}
	
	private void init( int percentMax){
		
		growth = new Counter(Counter.COUNT_UP_TO , percentMax , new FullyGrown() ); //Counter that counts up to percentMax then fires FullyGrown class
		
		lifespan = new Counter(Counter.COUNT_DOWN_FROM , 10*percentMax , new Dead() );
		
		this.setCollisionComposite( ColliderNull.getNonCollidable() );
        this.loadSprite("Prototypes/twig.png" , -4 , -40 );
        
        ((SpriteStillframe) ((GraphicComposite)this.graphicsComposite).getSprite()).setResizeFactor(0); //start initial segment at size 0
	}
	
	public int getNumberFromBranch(){ return this.numberFromLastBranch; }
	public void nextNumberFromBranch( int number ){ this.numberFromLastBranch = number; }
	
	public int getMaxGrowth(){ return this.maxGrowth; }
	public void killSegment(){ this.maxGrowth = 1;}
	
	public void setAngle( int angle ){
		
		((GraphicComposite)this.graphicsComposite).getSprite().setAngle(angle);
		this.angle = angle;
	}
	
	public int getAngle(){ return this.angle; }
	
	public boolean isDead(){ return dead; }

	@Override
	public void updatePosition() {
		super.updatePosition();
		
		growth.updateCounter(); // Counter "growth" counts up and then fires FullyGrown trigger when done
		lifespan.updateCounter();
		
    	(( SpriteStillframe )this.getEntitySprite()).setResizeFactor( growth.getCount() );

	}
	
	
	private class FullyGrown implements Trigger{ //Event that fires when this segments growth counter reaches 100%
		@Override
		public void activate() {

			float oldMaxGrowth = getMaxGrowth();
			
			float oldRadius = oldMaxGrowth/100*40;
			
			// Calculate location of the tip of this segment for next segment
			int endPointX =  (int)x + (int)( oldRadius * Math.sin( Math.toRadians(angle ))  );
			int endPointY =  (int)y - (int)( oldRadius * Math.cos( Math.toRadians(angle ))  );
			
			
			if ( getNumberFromBranch() > ThreadLocalRandom.current().nextInt( 0 , 5) ){ //start new branch every 1-6 segments
				
				final int FORK_ANGLE = 40; // Set to 90 or higher for some freaky shit
				final int UPWARD_WILLPOWER = 20; //-20 to 40 look normal. Set to 90 or higher for chaos
				
				int thisMaxGrowth = (int)oldMaxGrowth-1;
				
				int thisSegmentAngle =  (angle % 360) ; // constrain angle to 0-360 for convenience
				
				if ( thisSegmentAngle > 0)
					if (thisSegmentAngle > 180) // Adds push towards angle of 0 ( pointing up )
						thisSegmentAngle += UPWARD_WILLPOWER;
					else
						thisSegmentAngle -= UPWARD_WILLPOWER; //find better math way of doing this
				else
					if (thisSegmentAngle < -180)
						thisSegmentAngle -= UPWARD_WILLPOWER;
					else
						thisSegmentAngle += UPWARD_WILLPOWER;
				
				//Create next segments and spawn them into board
				PlantTwigSegment sprout = new PlantTwigSegment( endPointX , endPointY , thisMaxGrowth , board) ;
				sprout.setAngle( thisSegmentAngle + FORK_ANGLE );
				board.spawnDynamicEntity( sprout );
				
				sprout = new PlantTwigSegment( endPointX , endPointY , thisMaxGrowth , board) ;
				sprout.setAngle( thisSegmentAngle - FORK_ANGLE );
				board.spawnDynamicEntity( sprout );
				
			}
			else if (oldMaxGrowth > 30){ // Else segemnt didn't branch, so grown next segment if bigger than 20% grown
				
				final int RANDOM_BEND_RANGE = 20; // 0 is perfectly straight branch. Higher than 40 looks withered.
				
				final int UPWARD_WILLPOWER = 20; // 
				
				int randomShrinkage = ThreadLocalRandom.current().nextInt( 1 , 10); // This being greater than 0 is the only
				//thing stopping the stem from growing infinitely. Adjust chances accordingly
				
				int thisMaxGrowth = getMaxGrowth() - randomShrinkage; //reduce next segment's max growth so its always smaller
				
				PlantTwigSegment sprout = new PlantTwigSegment( endPointX , endPointY , thisMaxGrowth , board) ;
				
				int randomBend = ThreadLocalRandom.current().nextInt( -RANDOM_BEND_RANGE ,RANDOM_BEND_RANGE); // get random bend
				
				int thisSegmentAngle =  ((angle + randomBend) % 360) ; //And add it to the next segments angle
				
				if ( thisSegmentAngle > 0)
					if (thisSegmentAngle > 180) // Adds a 10 degree push towards angle of 0 ( pointing up ) same as above
						thisSegmentAngle += UPWARD_WILLPOWER;
					else
						thisSegmentAngle -= UPWARD_WILLPOWER;
				else
					if (thisSegmentAngle > -180)
						thisSegmentAngle += UPWARD_WILLPOWER;
					else
						thisSegmentAngle -= UPWARD_WILLPOWER;
					
				
				sprout.setAngle(thisSegmentAngle);
				sprout.nextNumberFromBranch( getNumberFromBranch()+1 ); //Increment next branches number in this stem
				board.spawnDynamicEntity( sprout ); //then spawn it in
				
			}
			
				
			
		}
	}
	
	
	private class Dead implements Trigger{

		@Override
		public void activate() {
			accY = 0.05f;
		}
		
	}
	
	
	class wheelw implements Trigger{

		@Override
		public void activate() {
			
		}
		
	}
	
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
	
	
	

}
