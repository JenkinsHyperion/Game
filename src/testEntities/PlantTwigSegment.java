package testEntities;

import java.awt.Color;
import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

import engine.ReferenceFrame;
import engine.TestBoard;
import entities.*;
import entityComposites.ColliderNull;
import entityComposites.CompositeFactory;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import sprites.Sprite;
import sprites.SpriteStillframe;
//import sun.management.counter.Counter;
import utility.Trigger;

public class PlantTwigSegment extends EntityStatic{
	
	private static Sprite twigSprite = new SpriteStillframe("Prototypes/twig.png" , -4 , -40);

	private TestBoard board;
	private int maxGrowth;

	private boolean dead;
	
	private Runnable currentGrowthState = new GrowingState();
	private Runnable currentTransportState = new InactiveTransportState();
	
	private int numberFromLastBranch = 0;
	
	private PlantTwigSegment previousSegment;
	
	private int growthLevel = 0;
	private int waterLevel = 0; 
	
	public PlantTwigSegment(int x, int y, int maxGrowth , TestBoard board) {
		super(x, y);
		this.board = board;
		this.maxGrowth = maxGrowth;
		
		CompositeFactory.addDynamicRotationTo(this);
		
		init( maxGrowth );
		
	}
	
	private void init( int percentMax){

		CompositeFactory.addAnonymousGraphicTo(this, new GraphicComposite(this){
			@Override
			public void draw(ReferenceFrame camera) {
				camera.getGraphics().setColor(Color.CYAN);
				camera.drawString( ""+waterLevel , getX()+10, getY()+10);
				super.draw(camera);
			}
		});
		this.getGraphicComposite().setSprite(twigSprite);
		this.getGraphicComposite().setGraphicSizeFactor(0);
        
	}
	
	public int getMaxGrowth(){ return this.maxGrowth; }
	public void killSegment(){ this.maxGrowth = 1;}
	
	public boolean isDead(){ return dead; }

	protected void setPrevious(PlantTwigSegment previous){ this.previousSegment = previous; }
	protected int getWaterLevel(){ return this.waterLevel; }
	
	protected boolean pushWaterTo( PlantTwigSegment partner , int flowRate ){

		if ( partner.waterLevel < partner.maxGrowth ){ //Check that partner segment has enough space to store water
			
			final int deltaWaterLevel = partner.waterLevel + flowRate; // The value partners water level would be next frame
			
			if ( deltaWaterLevel <= 100 ){ 
			
				partner.waterLevel = partner.waterLevel + flowRate;
				this.waterLevel = this.waterLevel - flowRate;
			}
			else{ //Not enough space so take remainder
				
				final int partial = deltaWaterLevel - 100 ;
				partner.waterLevel = partner.waterLevel + partial;
				this.waterLevel = this.waterLevel - partial;
			}
			return true;
		}
		else{ 
			return false;
		}
	}
	
	protected boolean pullWaterFrom( PlantTwigSegment partner , int flowRate ){
		
		if ( partner.waterLevel > flowRate ){ //Check that partner has enough water to give
			
			final int deltaWaterLevel = this.waterLevel + flowRate; // The value the water level would be next frame
			
			if ( deltaWaterLevel <= 100 ){ // Check that this segment has enough empty space to take in water
			
				partner.waterLevel = partner.waterLevel - flowRate;
				this.waterLevel = this.waterLevel + flowRate;
			}
			else{ //Not enough space so take remainder
				
				final int partial = 100 - deltaWaterLevel;
				partner.waterLevel = partner.waterLevel - partial;
				this.waterLevel = this.waterLevel + partial;
			}
			return true;
		}
		else{ 
			return false;
		}
	}
	
	@Override
	public void updateComposite() {
		super.updateComposite();

		this.currentGrowthState.run(); 
		
		this.currentTransportState.run();
		
	}
	
	/*@Override
	public void updatePosition() {
		super.updatePosition();
		
		growth.updateCounter(); // Counter "growth" counts up and then fires FullyGrown trigger when done
		lifespan.updateCounter();
		
    	(( SpriteStillframe )this.getEntitySprite()).setResizeFactor( growth.getCount() );

	}*/
	public void debugSetWaterPercent( int waterLevel ){
		this.waterLevel = waterLevel;
		if ( this.waterLevel > 99 ){
			this.graphicsComposite.setGraphicSizeFactor(1);
			this.new FullyGrown().activate();
		}else{
			this.graphicsComposite.setGraphicSizeFactor(waterLevel/100);
		}
	}
	
	private class GrowingState2 implements Runnable{
		
		@Override
		public void run() {
			
			//lifespan.updateCounter();
				
				if ( pullWaterFrom( previousSegment, 2 ) ){
					
					getGraphicComposite().setGraphicSizeFactor( waterLevel / 100.0 );
					
					if ( waterLevel > maxGrowth ){
						PlantTwigSegment.this.new FullyGrown().activate();
						PlantTwigSegment.this.currentGrowthState = PlantTwigSegment.this.new FullyGrownState();
						PlantTwigSegment.this.currentTransportState = PlantTwigSegment.this.new PullTransportState();
					}
				}
		}
	}
	
	private class GrowingState implements Runnable{
		
		@Override
		public void run() {
			
			if ( growthLevel >= maxGrowth ){
				PlantTwigSegment.this.new FullyGrown().activate();
				PlantTwigSegment.this.currentGrowthState = PlantTwigSegment.this.new FullyGrownState();
			}
			else {
				if ( waterLevel > 0 ){
					waterLevel = waterLevel - 1;
					growthLevel = growthLevel + 1;
					getGraphicComposite().setGraphicSizeFactor( growthLevel / 100.0 );
				}
			}

		}
	}
	
	private class FullyGrownState implements Runnable{
		@Override
		public void run() {
			//
		}
	}
	
	private class PullTransportState implements Runnable{
		@Override
		public void run() {
			pullWaterFrom( previousSegment, 10 );
		}
	}
	
	private class InactiveTransportState implements Runnable{
		@Override
		public void run() {
			
		}
	}
	
	private class DebugWaterSource implements Runnable{
		@Override
		public void run() {
			// Pull water from thin air
			if ( waterLevel < 1000 ){
				waterLevel = waterLevel + 10;
			}
		}
	}
	
	private class DebugSelfGrowing implements Runnable{
		@Override
		public void run() {

				getGraphicComposite().setGraphicSizeFactor( waterLevel / 100.0 );
				
				if ( waterLevel > maxGrowth ){
					PlantTwigSegment.this.new FullyGrown().activate();
					PlantTwigSegment.this.currentGrowthState = PlantTwigSegment.this.new DebugWaterSource();
				}
			
		}
	}
	
	public void debugMakeWaterSource(){
		this.currentGrowthState = new DebugSelfGrowing();
		this.currentTransportState = new DebugWaterSource();
	}
	
	private abstract class PushTransportState implements Runnable{ 
		
		//Run is Overridden by stem or fork to allow different behaviors for different branches 
	}
	
	private class StemPushTransportState extends PushTransportState{
		
		protected PlantTwigSegment nextSegment;
		
		protected StemPushTransportState( PlantTwigSegment nextSegment ){
			this.nextSegment = nextSegment;
		}
		@Override 
		public void run(){ //For stems there is only one next node and thus only one behavior
			if ( PlantTwigSegment.this.waterLevel >= 10 ){
				PlantTwigSegment.this.pushWaterTo( nextSegment , 10 );
			}
		}
		@Override
		public String toString() {
			return "Pushing Stem";
		}
	}
	
	private class ForkPushTransportState extends PushTransportState{
		protected PlantTwigSegment nextSegmentCW;
		protected PlantTwigSegment nextSegmentCCW;
		protected ForkPushTransportState(  PlantTwigSegment nextNodeCW , PlantTwigSegment nextNodeCCW ){
			this.nextSegmentCW = nextNodeCW;
			this.nextSegmentCCW = nextNodeCCW;
		}
		protected void setNextCWNode( PlantTwigSegment nextCWNode ){
			this.nextSegmentCW = nextCWNode;
		}
		protected void setNextCCWNode( PlantTwigSegment nextCCWNode ){
			this.nextSegmentCCW = nextCCWNode;
		}
		@Override
		public void run(){
			if ( PlantTwigSegment.this.waterLevel >= 10 ){
				PlantTwigSegment.this.pushWaterTo( nextSegmentCW , 5 );
				PlantTwigSegment.this.pushWaterTo(nextSegmentCCW , 5 );
			}
		}
		@Override
		public String toString() {
			return "Pushing";
		}
	}
	
	private class FullyGrown implements Trigger{ //Event that fires when this segments growth counter reaches 100%
		@Override
		public void activate() {

			float oldMaxGrowth = getMaxGrowth();
			
			float oldRadius = oldMaxGrowth/100*40;
			
			// Calculate location of the tip of this segment for next segment
			int endPointX =  (int)x + (int)( oldRadius * Math.sin( Math.toRadians( getAngularComposite().getAngle() ))  );
			int endPointY =  (int)y - (int)( oldRadius * Math.cos( Math.toRadians( getAngularComposite().getAngle() ))  );
			
			Point relativeTip = PlantTwigSegment.this.getPosition();
			int tipX = relativeTip.x;
			int tipY = (int)(relativeTip.y - oldRadius);
			
			Point tip = PlantTwigSegment.this.getRelativePositionOf(new Point( 0 , (int)oldRadius ));
			endPointX = tip.x;
			endPointY = tip.y;
			
			if ( numberFromLastBranch > ThreadLocalRandom.current().nextInt( 0 , 8) ){ //start new branch every 1-6 segments
				
				final int FORK_ANGLE = 50; // Set to 90 or higher for some freaky shit
				final int UPWARD_WILLPOWER = 20; //-20 to 40 look normal. Set to 90 or higher for chaos
				
				int thisMaxGrowth = (int)oldMaxGrowth-1;
				
				int thisSegmentAngle =  (int) ( getAngularComposite().getAngle() % 360) ; // constrain angle to 0-360 for convenience
				
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
				//PlantTwigSegment sprout = new PlantTwigSegment( endPointX , endPointY , thisMaxGrowth , board) ;
				PlantTwigSegment sproutLeft = new PlantTwigSegment( tipX , tipY , thisMaxGrowth , board) ;
				sproutLeft.setPrevious(PlantTwigSegment.this);
				
				sproutLeft.getAngularComposite().setAngleInDegrees( thisSegmentAngle + FORK_ANGLE );
				CompositeFactory.makeChildOfParent(sproutLeft, PlantTwigSegment.this , board);

				board.spawnNewSprout( sproutLeft );
				
				PlantTwigSegment sproutRight = new PlantTwigSegment( tipX , tipY , thisMaxGrowth , board) ;
				//sprout = new PlantTwigSegment( endPointX , endPointY , thisMaxGrowth , board) ;
				sproutRight = new PlantTwigSegment( tipX , tipY , thisMaxGrowth , board) ;
				sproutRight.setPrevious(PlantTwigSegment.this);
				//sprout.setAngle( thisSegmentAngle - FORK_ANGLE );

				sproutRight.getAngularComposite().setAngleInDegrees( thisSegmentAngle - FORK_ANGLE );
				CompositeFactory.makeChildOfParent(sproutRight, PlantTwigSegment.this , board);
				
				board.spawnNewSprout( sproutRight );
				
				PlantTwigSegment.this.currentTransportState = new ForkPushTransportState( sproutRight, sproutLeft ) ;
				
			}
			else if (oldMaxGrowth > 30){ // Else segemnt didn't branch, so grown next segment if bigger than 20% grown
				
				final int RANDOM_BEND_RANGE = 20; // 0 is perfectly straight branch. Higher than 40 looks withered.
				
				final int UPWARD_WILLPOWER = 10; // 
				
				int randomShrinkage = ThreadLocalRandom.current().nextInt( 1 , 10); // This being greater than 0 is the only
				//thing stopping the stem from growing infinitely. Adjust chances accordingly
				
				int thisMaxGrowth = getMaxGrowth() - randomShrinkage; //reduce next segment's max growth so its always smaller
				
				
				//PlantTwigSegment sproutStem = new PlantTwigSegment( endPointX , endPointY , thisMaxGrowth , board) ;	
				PlantTwigSegment sproutStem = new PlantTwigSegment( tipX , tipY , thisMaxGrowth , board );
				sproutStem.setPrevious(PlantTwigSegment.this);
				
				int randomBend = ThreadLocalRandom.current().nextInt( -RANDOM_BEND_RANGE ,RANDOM_BEND_RANGE); // get random bend
				
				int thisSegmentAngle =  (int)(( getAngularComposite().getAngle() + randomBend) % 360) ; //And add it to the next segments angle
				
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

				CompositeFactory.makeChildOfParent(sproutStem, PlantTwigSegment.this , board);
				
				sproutStem.getAngularComposite().setAngleInDegrees(thisSegmentAngle);
				
				//sproutStem.getRotationComposite().setAngularVelocity(1f);

				sproutStem.numberFromLastBranch = numberFromLastBranch + 1 ; //Increment next branches number in this stem
				board.spawnNewSprout( sproutStem ); //then spawn it in
				
				PlantTwigSegment.this.currentTransportState = new StemPushTransportState( sproutStem ) ;
				
			}
			
				
			
		}
	}
	
	
	private class Dead implements Trigger{

		@Override
		public void activate() {
			PlantTwigSegment.this.disable();
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
