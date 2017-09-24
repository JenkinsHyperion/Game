package testEntities;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.concurrent.ThreadLocalRandom;
import engine.ReferenceFrame;
import engine.TestBoard;
import entities.*;
import entityComposites.AngularComposite;
import entityComposites.ColliderNull;
import entityComposites.CompositeFactory;
import entityComposites.DynamicRotationComposite;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import physics.BoundaryLinear;
import sprites.Sprite;
import sprites.Sprite.Stillframe;
//import sun.management.counter.Counter;
import utility.Trigger;

public class PlantTwigSegment extends EntityStatic{
	
	//protected static Sprite twigSmallSprite = new Sprite.Stillframe("Prototypes/twig.png" , -4 , -40);
	//protected static Sprite twigMediumSprite = new Sprite.Stillframe("Prototypes/twig2.png" , -8 , -40);
	
	protected static Sprite twigSmallSprite = new Sprite.Stillframe("Prototypes/twig1.png" , -4 , -80);
	protected static Sprite twigMediumSprite = new Sprite.Stillframe("Prototypes/twig3.png" , -8 , -80);
	
	protected TestBoard board;

	public static int[] waveCounter = new int[]{0};
	
	protected int growthLevel = 0;
	protected int maxGrowth;
	
	protected int waterLevel = 0; 
	
	protected StemSegment previousSegment;
	protected StemSegment nextSegment;
	protected static int colliderGroup;
	
	protected Runnable currentGrowthState;
	
	protected boolean dead;
	
	public PlantTwigSegment(int x, int y, int maxGrowth, TestBoard board) {
		super(x,y);
		this.board = board;
		this.maxGrowth = maxGrowth;
	}
	
	protected void setPreviousStem(StemSegment previous){ this.previousSegment = previous; }
	
	public void setColliderGroup(int i){
		colliderGroup = i;
	}
	
	protected class FullyGrownState implements Runnable{
		@Override
		public void run() {
			//
		}
	}
	
	/*###################################################################################################################################
	 * 		TRUNK STEM SEGMENT
	 */
	
	
	
	/*###################################################################################################################################
	 * 		TRUNK STEM SEGMENT
	 */
	
	public static class StemSegment extends PlantTwigSegment{
		
			public static int counter = 0;
			protected Runnable currentWaterTransportState = new InactiveWaterTransportState();
			protected SugarTransportState currentSugarTransportState = new InactiveSugarTransportState();
			
			protected int numberFromLastBranch = 0;
			protected boolean lastBranchedClockwise = false;
			
			protected int sugarLevel = 0;
			
			protected TransportWaterListener waterListener = new TransportWaterListener();
			
			public StemSegment(int x, int y, int maxGrowth , TestBoard board) {
				super(x, y, maxGrowth, board);
				counter++;
				//CompositeFactory.addDynamicRotationTo(this);
				//CompositeFactory.addCustomDynamicRotationTo(this, new DynamicRotationComposite.SineWave(this , waveCounter ) );
				
				this.currentGrowthState = new GrowingState( new FullyGrownEvent() , new FullyGrownState() );
				
				init( maxGrowth );
				
			}
			
			protected void init( int percentMax){
		
				AngularComposite angularComposite = CompositeFactory.addAngularComposite(this);
				
				CompositeFactory.addAnonymousGraphicTo(this, new GraphicComposite.Active(this){
					@Override
					public void draw(ReferenceFrame camera) {
						camera.getGraphics().setColor(Color.CYAN);
						camera.drawString( ""+waterLevel , getX()+10, getY()+10);
						super.draw(camera);
						camera.getGraphics().setColor(Color.YELLOW);
						camera.drawString( ""+sugarLevel , getX()+30, getY()+10);
					}
				});
				this.getGraphicComposite().setSprite(twigSmallSprite);
				this.getGraphicComposite().setGraphicSizeFactor(0);
				
				if ( this.maxGrowth > 50 ){
					CompositeFactory.addRotationalColliderTo( 
							this, 
							new BoundaryLinear( new Line2D.Double( 0,0 , 0, (int)(-80*(maxGrowth/100.0)) ) ), 
							angularComposite 
							);
				}
			}
			
			public void debugSetSugarLevel( int level ){
				this.sugarLevel = level;
			}
			
			public int getMaxGrowth(){ return this.maxGrowth; }
			public void killSegment(){ this.maxGrowth = 1;}
			
			public boolean isDead(){ return dead; }
		
			protected int getWaterLevel(){ return this.waterLevel; }

			
			protected boolean pushWaterTo( PlantTwigSegment partner , int flowRate ){
		
				if ( partner.waterLevel < partner.maxGrowth ){ //Check that partner segment has enough space to store water
					
					final int deltaWaterLevel = partner.waterLevel + flowRate; // The value partners water level would be next frame
					
					if ( deltaWaterLevel <= 100 ){ 
					
						partner.waterLevel = partner.waterLevel + flowRate;
						this.waterLevel = this.waterLevel - flowRate;
						this.waterListener.notifyWaterPassed(flowRate);
					}
					else{ //Not enough space so take remainder
						
						final int partial = deltaWaterLevel - 100 ;
						partner.waterLevel = partner.waterLevel + partial;
						this.waterLevel = this.waterLevel - partial;
						this.waterListener.notifyWaterPassed(partial);
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
				
				this.currentWaterTransportState.run();
				
				this.currentSugarTransportState.transport();
			}
			
			/*@Override
			public void updatePosition() {
				super.updatePosition();
				
				growth.updateCounter(); // Counter "growth" counts up and then fires FullyGrown trigger when done
				lifespan.updateCounter();
				
		    	(( Sprite.Stillframe )this.getEntitySprite()).setResizeFactor( growth.getCount() );
		
			}*/
			public void debugSetWaterPercent( int waterLevel ){
				this.waterLevel = waterLevel;
				if ( this.waterLevel > 99 ){
					this.graphicsComposite.setGraphicSizeFactor(1);
					this.new FullyGrownEvent().activate();
				}else{
					this.graphicsComposite.setGraphicSizeFactor(waterLevel/100);
				}
			}
			
			protected class TransportWaterListener{
				void notifyWaterPassed( int waterPassed ){ /*DO NOTHING*/ }
			}
			
			protected class TransportWaterListenerActive extends TransportWaterListener{
				
				private int totalWaterTrasported = 0;
				private Trigger event;
				private int threshhold;
				private double widthFactor;
				
				protected TransportWaterListenerActive( int threshhold , Trigger event ){
					this.event = event;
					this.threshhold = threshhold;
					this.widthFactor = StemSegment.this.getGraphicComposite().getGraphicsSizeX();
				}
				
				@Override
				protected void notifyWaterPassed( int waterPassed ){
					if ( totalWaterTrasported < threshhold ){
						totalWaterTrasported += waterPassed;
						double newWidth = widthFactor + 0.25*totalWaterTrasported/threshhold;
						StemSegment.this.getGraphicComposite().setGraphicSizeFactorX(newWidth);
					}
					else{
						//event.activate();
					}
				}
				
			}
			
			protected class GrowingState implements Runnable{
				
				Runnable fullyGrownState;
				Trigger fullyGrownEvent;
				
				protected GrowingState( Trigger fullyGrownEvent, Runnable fullyGrownState ){
					this.fullyGrownState = fullyGrownState;
					this.fullyGrownEvent = fullyGrownEvent;
				}
				
				@Override
				public void run() {
					
					if ( growthLevel >= maxGrowth ){
						//StemSegment.this.new FullyGrown().activate();
						//StemSegment.this.currentGrowthState = StemSegment.this.new FullyGrownState();
						this.fullyGrownEvent.activate();
						StemSegment.this.currentGrowthState = this.fullyGrownState;
					}
					else {
						if ( sugarLevel > 0 ){
							sugarLevel = sugarLevel - 1;
							growthLevel = growthLevel + 1;
							getGraphicComposite().setGraphicSizeFactor( growthLevel / 100.0 );
						}
					}

				}
			}
			
			protected class GrowingState2 implements Runnable{
				
				@Override
				public void run() {
					
					//lifespan.updateCounter();
						
						if ( pullWaterFrom( previousSegment, 2 ) ){
							
							getGraphicComposite().setGraphicSizeFactor( waterLevel / 100.0 );
							
							if ( waterLevel > maxGrowth ){
								StemSegment.this.new FullyGrownEvent().activate();
								StemSegment.this.currentGrowthState = StemSegment.this.new FullyGrownState();
								StemSegment.this.currentWaterTransportState = StemSegment.this.new PullTransportState();
							}
						}
				}
			}
			
			private class PullTransportState implements Runnable{
				@Override
				public void run() {
					pullWaterFrom( previousSegment, 10 );
				}
			}
			
			private class InactiveSugarTransportState implements SugarTransportState{
				@Override
				public void transport() {
				}
			}
			private class InactiveWaterTransportState implements Runnable{
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
			
			private class DebugSeedGrowing implements Runnable{
				@Override
				public void run() {
		
						getGraphicComposite().setGraphicSizeFactor( waterLevel / 100.0 );
						
						if ( waterLevel > maxGrowth ){
							StemSegment.this.new SeedFullyGrownEvent().activate();
							StemSegment.this.currentGrowthState = StemSegment.this.new DebugWaterSource();
						}
					
				}
			}
			
			public void debugMakeWaterSource(){
				this.currentGrowthState = new DebugSeedGrowing();
				this.currentWaterTransportState = new DebugWaterSource();
			}
			
			protected abstract interface SugarTransportState{
				void transport();
			}
			
			protected class StemSugarOverflowTransport implements SugarTransportState{
				
				protected StemSegment nextSegment;
				
				public StemSugarOverflowTransport(StemSegment nextSegment){
					this.nextSegment = nextSegment;
				}
				
				@Override
				public void transport() {
					
					if ( this.nextSegment.sugarLevel < StemSegment.this.sugarLevel -1 ){
						
						StemSegment.this.sugarLevel -= 1;
						this.nextSegment.sugarLevel += 1;
					}
					if ( StemSegment.this.previousSegment.sugarLevel < StemSegment.this.sugarLevel -1  ){
						
						StemSegment.this.sugarLevel -= 1;
						StemSegment.this.previousSegment.sugarLevel += 1;
					}
				}
			}
			
			protected class StemSugarTerminal implements SugarTransportState{
				
				private StemSegment nextSegment;
				
				public StemSugarTerminal( StemSegment nextSegment ){
					this.nextSegment = nextSegment;
				}
				@Override
				public void transport() {
					
					if ( sugarLevel >= 53 ){	
						if ( nextSegment.sugarLevel < 100 ) {	
							if( nextSegment.sugarLevel < StemSegment.this.sugarLevel ){
								nextSegment.sugarLevel += 3; 
								StemSegment.this.sugarLevel -= 3;
							}
						}
					}
				}
			}
			
			private class ForkSugarOverflowTransport implements SugarTransportState{
				
				protected StemSegment nextSegmentCW;
				protected StemSegment nextSegmentCCW;
				
				public ForkSugarOverflowTransport( StemSegment nextSegmentCW , StemSegment nextSegmentCCW ){
					this.nextSegmentCW = nextSegmentCW;
					this.nextSegmentCCW = nextSegmentCCW;
				}
				
				@Override
				public void transport() {
					
					if ( this.nextSegmentCW.sugarLevel < StemSegment.this.sugarLevel -1 ){
						
						StemSegment.this.sugarLevel -= 1;
						this.nextSegmentCW.sugarLevel += 1;
					}
					if ( this.nextSegmentCCW.sugarLevel < StemSegment.this.sugarLevel -1 ){
						
						StemSegment.this.sugarLevel -= 1;
						this.nextSegmentCCW.sugarLevel += 1;
					}
					if ( StemSegment.this.previousSegment.sugarLevel < StemSegment.this.sugarLevel -1  ){
						
						StemSegment.this.sugarLevel -= 1;
						StemSegment.this.previousSegment.sugarLevel += 1;
					}
				}
			}
			
			private abstract class WaterTransportState implements Runnable{ 
				
				//Run is Overridden by stem or fork to allow different behaviors for different branches 
			}
			
			protected class StemPushTransportState extends WaterTransportState{
				
				protected PlantTwigSegment nextSegment;
				
				protected StemPushTransportState( PlantTwigSegment nextSegment ){
					this.nextSegment = nextSegment;
				}
				@Override 
				public void run(){ //For stems there is only one next node and thus only one behavior
					if ( StemSegment.this.waterLevel >= 10 ){
						StemSegment.this.pushWaterTo( nextSegment , 10 );
					}
				}
				@Override
				public String toString() {
					return "Pushing Stem";
				}
			}
			
			protected class ForkPushTransportState extends WaterTransportState{
				protected PlantTwigSegment nextSegmentCW;
				protected PlantTwigSegment nextSegmentCCW;
				protected ForkPushTransportState(  PlantTwigSegment nextNodeCW , PlantTwigSegment nextNodeCCW ){
					this.nextSegmentCW = nextNodeCW;
					this.nextSegmentCCW = nextNodeCCW;
				}
		
				@Override
				public void run(){
					if ( StemSegment.this.waterLevel >= 10 ){
						StemSegment.this.pushWaterTo( nextSegmentCW , 5 );
						StemSegment.this.pushWaterTo(nextSegmentCCW , 5 );
					}
				}
				@Override
				public String toString() {
					return "Pushing";
				}
			}
			
			private class UpgradeEvent01 implements Trigger{
				@Override
				public void activate() {
					StemSegment.this.getGraphicComposite().setSprite( twigMediumSprite );
					StemSegment.this.getGraphicComposite().setGraphicSizeFactor( 2 );
					StemSegment.this.waterListener = new TransportWaterListener();
				}
			}
			
			private class UpgradeEvent00 implements Trigger{
				@Override
				public void activate() {
					StemSegment.this.getGraphicComposite().setSprite( twigMediumSprite );
					StemSegment.this.getGraphicComposite().setGraphicSizeFactorX(
							StemSegment.this.getGraphicComposite().getGraphicsSizeX()*0.5
						);
					StemSegment.this.waterListener = new TransportWaterListener();
				}
			}
			
			private class FullyGrownEvent implements Trigger{ //Event that fires when this segments growth counter reaches 100%
				@Override
				public void activate() {
					
					int oldRadius = (int) (getMaxGrowth() / 100.0 * 80.0 );
					
						
					
					if ( numberFromLastBranch > ThreadLocalRandom.current().nextInt( 0 , 8) ){ //start new branch every 1-6 segments
						
						final int FORK_ANGLE = 40; // Set to 90 or higher for some freaky shit
						final int UPWARD_WILLPOWER = 20; //-20 to 40 look normal. Set to 90 or higher for chaos
						
						int thisMaxGrowth = (int)getMaxGrowth()-1;
						
						int thisSegmentAngle =  (int) ( getAngularComposite().getAngle() % 360) ; // constrain angle to 0-360 for convenience
						
						if ( thisSegmentAngle > 0){
							if (thisSegmentAngle > 180) // Adds push towards angle of 0 ( pointing up )
								thisSegmentAngle += UPWARD_WILLPOWER;
							else
								thisSegmentAngle -= UPWARD_WILLPOWER; //find better math way of doing this
						}
						else{
							if (thisSegmentAngle < -180)
								thisSegmentAngle -= UPWARD_WILLPOWER;
							else
								thisSegmentAngle += UPWARD_WILLPOWER;
						}
						
						
						Point relativeTip = StemSegment.this.getAbsolutePositionOf( new Point(0,-(int)oldRadius) );
						//Create next segments and spawn them into board
						//PlantTwigSegment sprout = new PlantTwigSegment( endPointX , endPointY , thisMaxGrowth , board) ;
						StemSegment sproutLeft = new StemSegment( relativeTip.x , relativeTip.y , thisMaxGrowth , board) ;
						sproutLeft.setPreviousStem(StemSegment.this);
	
						LeafStem leafStemRight = new LeafStem(relativeTip.x, relativeTip.y, thisMaxGrowth, board);
						leafStemRight.setPreviousStem(StemSegment.this);

						if ( StemSegment.this.lastBranchedClockwise ){ //check last branch direction and alternate
							sproutLeft.getAngularComposite().setAngleInDegrees( thisSegmentAngle + FORK_ANGLE/2.0 );
							leafStemRight.getAngularComposite().setAngleInDegrees( thisSegmentAngle - FORK_ANGLE );
							sproutLeft.lastBranchedClockwise = false;
						}
						else{
							leafStemRight.getAngularComposite().setAngleInDegrees( thisSegmentAngle + FORK_ANGLE );
							sproutLeft.getAngularComposite().setAngleInDegrees( thisSegmentAngle - FORK_ANGLE/2.0 );
							sproutLeft.lastBranchedClockwise = true;
							
						}
						board.spawnNewSprout( sproutLeft );
						board.spawnNewSprout( leafStemRight );
						CompositeFactory.makeChildOfParent(sproutLeft, StemSegment.this , board);
						CompositeFactory.makeChildOfParent(leafStemRight, StemSegment.this , board);
						
//						board.spawnNewSprout( sproutLeft );
//						board.spawnNewSprout( leafStemRight );
						
						StemSegment.this.currentWaterTransportState = new ForkPushTransportState( leafStemRight, sproutLeft ) ;
						StemSegment.this.currentSugarTransportState = new ForkSugarOverflowTransport( leafStemRight, sproutLeft ) ;
						//StemSegment.this.currentSugarTransportState = new StemSugarOverflowTransport( leafStemRight ) ;
						
						new UpgradeEvent00().activate();
						
						StemSegment.this.waterListener = new TransportWaterListenerActive( 5000 , new UpgradeEvent01() ); 
					}
					else if ( getMaxGrowth() > 30){ // Else segemnt didn't branch, so grown next segment if bigger than 30% grown
						
						final int RANDOM_BEND_RANGE = 20; // 0 is perfectly straight branch. Higher than 40 looks withered.
						
						final int UPWARD_WILLPOWER = 10; // 
						
						int randomShrinkage = ThreadLocalRandom.current().nextInt( 1 , 10); // This being greater than 0 is the only
						//thing stopping the stem from growing infinitely. Adjust chances accordingly
						
						int thisMaxGrowth = getMaxGrowth() - randomShrinkage; //reduce next segment's max growth so its always smaller
						
						
						//PlantTwigSegment sproutStem = new PlantTwigSegment( endPointX , endPointY , thisMaxGrowth , board) ;	
						
						int randomBend = ThreadLocalRandom.current().nextInt( -RANDOM_BEND_RANGE ,RANDOM_BEND_RANGE); // get random bend
						
						int thisSegmentAngle =  (int)(( getAngularComposite().getAngle() + randomBend) % 360) ; //And add it to the next segments angle
						
						if ( thisSegmentAngle > 0){
							if (thisSegmentAngle > 180) // Adds a 10 degree push towards angle of 0 ( pointing up ) same as above
								thisSegmentAngle += UPWARD_WILLPOWER;
							else
								thisSegmentAngle -= UPWARD_WILLPOWER;
						}
						else{
							if (thisSegmentAngle > -180)
								thisSegmentAngle += UPWARD_WILLPOWER;
							else
								thisSegmentAngle -= UPWARD_WILLPOWER;
						}
						
						Point relativeTip = StemSegment.this.getAbsolutePositionOf( new Point(0,-(int)oldRadius) );
						
						StemSegment sproutStem = new StemSegment( relativeTip.x , relativeTip.y , thisMaxGrowth , board );
						sproutStem.setPreviousStem(StemSegment.this);
						
						sproutStem.getAngularComposite().setAngleInDegrees(thisSegmentAngle);
						board.spawnNewSprout( sproutStem ); //then spawn it in
						CompositeFactory.makeChildOfParent(sproutStem, StemSegment.this , board);
						
						//sproutStem.getRotationComposite().setAngularVelocity(1f);
		
						sproutStem.numberFromLastBranch = numberFromLastBranch + 1 ; //Increment next branches number in this stem
						sproutStem.lastBranchedClockwise = StemSegment.this.lastBranchedClockwise; //didn't branch so pass same direction to next
//						board.spawnNewSprout( sproutStem ); //then spawn it in
						
						StemSegment.this.currentWaterTransportState = new StemPushTransportState( sproutStem ) ;
						StemSegment.this.currentSugarTransportState = new StemSugarOverflowTransport( sproutStem ) ;
						
						new UpgradeEvent00().activate();
						
						StemSegment.this.waterListener = new TransportWaterListenerActive( 5000 , new UpgradeEvent01() ); 
					}
				}
				
			}
			
			
			private class Dead implements Trigger{
		
				@Override
				public void activate() {
					StemSegment.this.disable();
				}
				
			}
	
			private class SeedFullyGrownEvent implements Trigger{ //Event that fires when this segments growth counter reaches 100%
				@Override
				public void activate() {
					
					
					final int RANDOM_BEND_RANGE = 20; // 0 is perfectly straight branch. Higher than 40 looks withered.
						
					final int UPWARD_WILLPOWER = 10; // 
						
					int randomShrinkage = ThreadLocalRandom.current().nextInt( 1 , 10); // This being greater than 0 is the only
						//thing stopping the stem from growing infinitely. Adjust chances accordingly
						
					int thisMaxGrowth = getMaxGrowth() - randomShrinkage; //reduce next segment's max growth so its always smaller
						
						
						//PlantTwigSegment sproutStem = new PlantTwigSegment( endPointX , endPointY , thisMaxGrowth , board) ;	
						
					int randomBend = ThreadLocalRandom.current().nextInt( -RANDOM_BEND_RANGE ,RANDOM_BEND_RANGE); // get random bend
						
					int thisSegmentAngle =  (int)(( getAngularComposite().getAngle() + randomBend) % 360) ; //And add it to the next segments angle
						
						if ( thisSegmentAngle > 0){
							if (thisSegmentAngle > 180) // Adds a 10 degree push towards angle of 0 ( pointing up ) same as above
								thisSegmentAngle += UPWARD_WILLPOWER;
							else
								thisSegmentAngle -= UPWARD_WILLPOWER;
						}
						else{
							if (thisSegmentAngle > -180)
								thisSegmentAngle += UPWARD_WILLPOWER;
							else
								thisSegmentAngle -= UPWARD_WILLPOWER;
						}
		
						
					int oldRadius = (int) (getMaxGrowth() / 100.0 * 80.0 );
						
					Point relativeTip = StemSegment.this.getAbsolutePositionOf( new Point(0,-(int)oldRadius) );
						
					StemSegment sproutStem = new StemSegment( relativeTip.x , relativeTip.y , thisMaxGrowth , board );
					sproutStem.setPreviousStem(StemSegment.this);
						
					sproutStem.getAngularComposite().setAngleInDegrees(thisSegmentAngle);
					board.spawnNewSprout( sproutStem ); //then spawn it in	
					CompositeFactory.makeChildOfParent(sproutStem, StemSegment.this , board);
						
						//sproutStem.getRotationComposite().setAngularVelocity(1f);
		
					sproutStem.numberFromLastBranch = numberFromLastBranch + 1 ; //Increment next branches number in this stem
//					board.spawnNewSprout( sproutStem ); //then spawn it in
						
					StemSegment.this.currentWaterTransportState = new StemPushTransportState( sproutStem ) ;
					StemSegment.this.currentSugarTransportState = new StemSugarTerminal( sproutStem ) ;
					
				}
				
			}
			
			
	
	}
	
	/*###################################################################################################################################
	 * 		LEAF STEM SEGMENT Grows leaves and never branches
	 */
	public static class LeafStem extends StemSegment{ //

		public LeafStem(int x, int y, int maxGrowth, TestBoard board) {
			super(x, y, maxGrowth, board);

			this.currentGrowthState = new LeafStemGrowingState() ;
		}

		protected class LeafStemGrowingState implements Runnable{
			
			@Override
			public void run() {

				if ( LeafStem.this.growthLevel >= LeafStem.this.maxGrowth ){

					LeafStem.this.new FullyGrownLeafStemEvent().activate();
					LeafStem.this.currentGrowthState = LeafStem.this.new Grown() ;
				}
				else {
					if ( waterLevel > 0 ){
						LeafStem.this.waterLevel = LeafStem.this.waterLevel - 1;
						LeafStem.this.growthLevel += 1;
						getGraphicComposite().setGraphicSizeFactor( LeafStem.this.growthLevel / 100.0 );
					}
				}

			}
		}
		
		private class Grown implements Runnable{
			@Override
			public void run() {}
		}
		
		private class FullyGrownLeafStemEvent implements Trigger{
			
			@Override
			public void activate() {
	
				int oldRadius = (int) (getMaxGrowth() / 100.0 * 80.0 );
				
				Point relativeTip = LeafStem.this.getAbsolutePositionOf( new Point(0,-(int)oldRadius) );
					
	
				if ( LeafStem.this.getMaxGrowth() > 30 ){
					
	
					int randomShrinkage = ThreadLocalRandom.current().nextInt( 5 , 10); // This being greater than 0 is the only
	
					int thisMaxGrowth = getMaxGrowth() - randomShrinkage; //reduce next segment's max growth so its always smaller	
					
					final int RANDOM_BEND_RANGE = 20; // 0 is perfectly straight branch. Higher than 40 looks withered.
					
					final int UPWARD_WILLPOWER = 10; // 
	
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
	
					LeafStem sproutStem = new LeafStem( relativeTip.x , relativeTip.y , thisMaxGrowth , board );
					sproutStem.setPreviousStem(LeafStem.this);
					
					sproutStem.getAngularComposite().setAngleInDegrees(thisSegmentAngle);
					
					board.spawnNewSprout( sproutStem ); //then spawn it in
					CompositeFactory.makeChildOfParent(sproutStem, LeafStem.this , board);
					
					//sproutStem.getRotationComposite().setAngularVelocity(1f);
	
					sproutStem.numberFromLastBranch = numberFromLastBranch + 1 ; //Increment next branches number in this stem
					//board.spawnNewSprout( sproutStem ); //then spawn it in		
				
					PlantTwigSegment.Leaf newLeaf = new PlantTwigSegment.Leaf(relativeTip.x+10, relativeTip.y, thisMaxGrowth, board);
					newLeaf.setPreviousStem(LeafStem.this);
					
					board.spawnNewSprout( newLeaf );
					CompositeFactory.makeChildOfParent(newLeaf, LeafStem.this , board);
//					board.spawnNewSprout( newLeaf );
					
					LeafStem.this.currentWaterTransportState = new ForkPushTransportState( newLeaf , sproutStem ) ;
					LeafStem.this.currentSugarTransportState = new StemSugarOverflowTransport( sproutStem ) ;

				}
			}
		}
		
	}
	
	/*###################################################################################################################################
	 * 		LEAF SEGMENT
	 */
	
	public static class Leaf extends PlantTwigSegment{

		private Runnable currentGeneratorState;
		
		public Leaf(int x, int y, int maxGrowth, TestBoard board) {
			super(x, y, maxGrowth, board);
			
			CompositeFactory.addGraphicTo(this, new Sprite.Stillframe("box.png",Sprite.CENTERED) );
			this.getGraphicComposite().setGraphicSizeFactor( Leaf.this.growthLevel / 100.0 );
			
			this.currentGrowthState = new LeafGrowingState();
			this.currentGeneratorState = new Photosynthesis();
		}
		
		@Override
		public void updateComposite() {
			super.updateComposite();
			
			this.currentGrowthState.run();
			
			this.currentGeneratorState.run();
		}
		
		private class Photosynthesis implements Runnable{
			@Override
			public void run() {
				if ( Leaf.this.waterLevel > 0 ){
					if ( Leaf.this.previousSegment.sugarLevel < 100){
						Leaf.this.waterLevel -= Leaf.this.waterLevel;
						Leaf.this.previousSegment.sugarLevel += 1;
					}
				}
			}
		}
		
		private class LeafGrowingState implements Runnable{

			@Override
			public void run() {
				
				if ( Leaf.this.growthLevel >= Leaf.this.maxGrowth ){

					//Leaf.this.new FullyGrownLeafStemEvent().activate();
					Leaf.this.currentGrowthState = PlantTwigSegment.Leaf.this.new FullyGrownState() ;
				}
				else {
					if ( waterLevel > 0 ){
						Leaf.this.waterLevel = Leaf.this.waterLevel - 1;
						Leaf.this.growthLevel += 1;
						getGraphicComposite().setGraphicSizeFactor( Leaf.this.growthLevel / 100.0 );
					}
				}

			}
			
		}
		
	}


}
