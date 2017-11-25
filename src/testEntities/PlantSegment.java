package testEntities;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import engine.ReferenceFrame;
import engine.TestBoard;
import entities.*;
import entityComposites.AngularComposite;
import entityComposites.ColliderNull;
import entityComposites.CompositeFactory;
import entityComposites.DynamicRotationComposite;
import entityComposites.EntityBehaviorScript;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import physics.BoundaryCircular;
import physics.BoundaryLinear;
import physics.BoundarySingular;
import physics.Vector;
import sprites.Sprite;
import sprites.Sprite.Stillframe;
import utility.Probability;
//import sun.management.counter.Counter;
import utility.Trigger;

public abstract class PlantSegment extends EntityStatic{
	
	private final static int SEGMENT_LENGTH = 86;
	
	//protected static Sprite twigSmallSprite = new Sprite.Stillframe("Prototypes/twig.png" , -4 , -40);
	//protected static Sprite twigMediumSprite = new Sprite.Stillframe("Prototypes/twig2.png" , -8 , -40);
	static PlantSegment[] emptyNext = new PlantSegment[0];
	
	protected static Sprite twigSmallSprite = new Sprite.Stillframe("Prototypes/twig1.png" , -4 , -SEGMENT_LENGTH);
	protected static Sprite twigMediumSprite = new Sprite.Stillframe("Prototypes/twig3.png" , -8 , -SEGMENT_LENGTH);
	
	protected TestBoard board;

	public static int[] waveCounter = new int[]{0};
	
	protected int growthLevel = 0;
	protected int maxGrowth;
	
	protected int waterLevel = 0; 
	
	protected StemSegment previousSegment;
	protected PlantSegment[] nextSegments = emptyNext; //move to stems
	
	protected MattTree organism;
	
	//protected Runnable currentGrowthState;
	
	protected boolean dead;
	
	public PlantSegment(int x, int y, int maxGrowth, TestBoard board) {
		super(x,y);
		this.board = board;
		this.maxGrowth = maxGrowth;
		//this.organism = organism;
		
		CompositeFactory.addScriptTo(this, new EntityBehaviorScript("PlantSegmentScript",this){

			@Override
			protected void updateOwnerEntity(EntityStatic entity) {		
				PlantSegment.this.updateSegment();
			}
		});
		
	}
	
	protected abstract void spawnInWorld(TestBoard board);

	protected void setPreviousStem(StemSegment previous){ this.previousSegment = previous; }
	
	public void createInitialTree( String name ){
		this.organism = new MattTree(name);
	}
	
	public void updateSegment(){}

	/*###################################################################################################################################
	 * 		ACTIVATE
	 */
	
	public void activateOrganism(){
		System.err.println("ACTIVATING ORGANISM");
		this.organism.collidersAreActive = true;
		this.activateCollidersUp();
		this.activateBelow();
	}
	
	public void activateAbove(){
		for ( PlantSegment next : this.nextSegments ){
			next.activateCollidersUp();
		}
	}
	
	public void activateBelow(){
		if ( this.previousSegment != null )
		this.previousSegment.activateCollidersDown(); //FIXME integrate itteration of tree into the actual connection node classes
	}
	
	public void activateCollidersUp(){
		this.getColliderComposite().activateCollider();
		this.activateAbove();
	}
	
	public void activateCollidersDown(){
		this.getColliderComposite().activateCollider();
		this.activateBelow();
	}
	
	
	/*###################################################################################################################################
	 * 		TRUNK STEM SEGMENT
	 */
	
	public void deactivateOrganism(){
		this.organism.collidersAreActive = false;
		this.deactivateCollidersUp();
		this.deactivateBelow();
	}
	
	public void deactivateAbove(){
		for ( PlantSegment next : this.nextSegments ){
			next.deactivateCollidersUp();
		}
	}
	
	public void deactivateBelow(){
		if ( this.previousSegment != null )
		this.previousSegment.deactivateCollidersDown(); //FIXME integrate itteration of tree into the actual connection node classes
	}
	
	public void deactivateCollidersUp(){
		this.getColliderComposite().deactivateCollider();
		this.deactivateAbove();
	}
	
	public void deactivateCollidersDown(){
		this.getColliderComposite().deactivateCollider();
		this.deactivateBelow();
	}
	
	protected int randomInt( int min, int max ){

		Random temporaryRandom = new Random();
		int returnInt = temporaryRandom.nextInt( max - min );
		temporaryRandom = null;
		return min + returnInt;
	}
	
	protected boolean percentChance( float probabilityPercent ){

		Random temporaryRandom = new Random();
		boolean returnCheck = (probabilityPercent/100.0) >= temporaryRandom.nextFloat() ;
		temporaryRandom = null;
		return returnCheck;
	}
	
	
	protected static abstract class GrowingSegment extends PlantSegment{

		protected Runnable currentWaterTransportState = new InactiveWaterTransportState();
		protected SugarTransportState currentSugarTransportState = new InactiveSugarTransportState();
		protected int sugarLevel = 0;
		protected TransportWaterListener waterListener = new TransportWaterListener();
		
		protected GrowingState currentGrowthState;
		
		protected GrowingState fullyGrownState = new FullyGrownState();
		
		public GrowingSegment(int x, int y, int maxGrowth, TestBoard board) {
			super(x, y, maxGrowth, board);
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
				this.widthFactor = GrowingSegment.this.getGraphicComposite().getGraphicsSizeX();
			}
			
			@Override
			protected void notifyWaterPassed( int waterPassed ){
				if ( totalWaterTrasported < threshhold ){
					totalWaterTrasported += waterPassed;
					double newWidth = widthFactor + 0.25*totalWaterTrasported/threshhold;
					GrowingSegment.this.getGraphicComposite().setGraphicSizeFactorX(newWidth);
				}
				else{
					//event.activate();
				}
			}
			
		}
		
		protected class GrowingState implements Runnable{
			
			protected Trigger fullyGrownEvent = new Trigger(){
				@Override
				public void activate() {
					//DO NOTHING
				}
			};
			
			protected GrowingState(){
			}
			
			public Trigger getFullyGrownEvent(){
				return fullyGrownEvent;
			}
			
			protected GrowingState( Trigger fullyGrownEvent ){

				this.fullyGrownEvent = fullyGrownEvent;
			}
			
			@Override
			public void run() {
				
				if ( growthLevel >= maxGrowth ){
					//StemSegment.this.new FullyGrown().activate();
					//StemSegment.this.currentGrowthState = StemSegment.this.new FullyGrownState();
					this.fullyGrownEvent.activate();
					GrowingSegment.this.currentGrowthState = GrowingSegment.this.fullyGrownState;
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
		
		protected class FullyGrownState extends GrowingState{
			
			@Override
			public void run() {
				
			}
		}

		protected class PullTransportState implements Runnable{
			@Override
			public void run() {
				pullWaterFrom( previousSegment, 10 );
			}
		}
		
		protected class InactiveSugarTransportState implements SugarTransportState{
			@Override
			public void transport() {
			}
		}
		
		protected class InactiveWaterTransportState implements Runnable{
			@Override
			public void run() {
			}
		}
		
		protected class DebugWaterSource extends GrowingState{
			@Override
			public void run() {
				// Pull water from thin air
				if ( waterLevel < 1000 ){
					waterLevel = waterLevel + 10;
				}
				
			}
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
				
				if ( this.nextSegment.sugarLevel < GrowingSegment.this.sugarLevel -1 ){
					
					GrowingSegment.this.sugarLevel -= 1;
					this.nextSegment.sugarLevel += 1;
				}
				if ( GrowingSegment.this.previousSegment.sugarLevel < GrowingSegment.this.sugarLevel -1  ){
					
					GrowingSegment.this.sugarLevel -= 1;
					GrowingSegment.this.previousSegment.sugarLevel += 1;
				}
			}
		}
		
		protected class StemSugarForkOverflowTransport implements SugarTransportState{
			
			protected StemSegment[] nextSegmentsList;
			
			public StemSugarForkOverflowTransport(StemSegment...nextSegments){
				this.nextSegmentsList = nextSegments;
			}
			
			@Override
			public void transport() {
				
				for ( StemSegment nextSegment : nextSegmentsList ){
					if ( nextSegment.sugarLevel < GrowingSegment.this.sugarLevel -1 ){
						
						GrowingSegment.this.sugarLevel -= 1;
						nextSegment.sugarLevel += 1;
					}
					if ( GrowingSegment.this.previousSegment.sugarLevel < GrowingSegment.this.sugarLevel -1  ){
						
						GrowingSegment.this.sugarLevel -= 1;
						GrowingSegment.this.previousSegment.sugarLevel += 1;
					}
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
						if( nextSegment.sugarLevel < GrowingSegment.this.sugarLevel ){
							nextSegment.sugarLevel += 3; 
							GrowingSegment.this.sugarLevel -= 3;
						}
					}
				}
			}
		}
		
		protected class ForkSugarOverflowTransport implements SugarTransportState{
			
			protected GrowingSegment nextSegmentCW;
			protected GrowingSegment nextSegmentCCW;
			
			public ForkSugarOverflowTransport( GrowingSegment nextSegmentCW , GrowingSegment nextSegmentCCW ){
				this.nextSegmentCW = nextSegmentCW;
				this.nextSegmentCCW = nextSegmentCCW;
			}
			
			@Override
			public void transport() {
				
				if ( this.nextSegmentCW.sugarLevel < GrowingSegment.this.sugarLevel -1 ){
					
					GrowingSegment.this.sugarLevel -= 1;
					this.nextSegmentCW.sugarLevel += 1;
				}
				if ( this.nextSegmentCCW.sugarLevel < GrowingSegment.this.sugarLevel -1 ){
					
					GrowingSegment.this.sugarLevel -= 1;
					this.nextSegmentCCW.sugarLevel += 1;
				}
				if ( GrowingSegment.this.previousSegment.sugarLevel < GrowingSegment.this.sugarLevel -1  ){
					
					GrowingSegment.this.sugarLevel -= 1;
					GrowingSegment.this.previousSegment.sugarLevel += 1;
				}
			}
		}
		
		
		protected class DefaultGrowingState implements Runnable{

			private Trigger fullyGrownEvent = new Trigger(){
				@Override
				public void activate() { /* DO NOTHING */ }
			};
			
			public DefaultGrowingState() {}
			
			public DefaultGrowingState( Trigger fullyGrownEvent ) {
				this.fullyGrownEvent = fullyGrownEvent;
			}
			
			@Override
			public void run() {
				
				//lifespan.updateCounter();
					
					if ( pullWaterFrom( previousSegment, 2 ) ){
						
						getGraphicComposite().setGraphicSizeFactor( waterLevel / 100.0 );
						
						if ( waterLevel > maxGrowth ){
							this.fullyGrownEvent.activate();
							GrowingSegment.this.currentGrowthState = GrowingSegment.this.currentGrowthState;
							GrowingSegment.this.currentWaterTransportState = GrowingSegment.this.new PullTransportState();
						}
					}
			}
		}
		
		private abstract class WaterTransportState implements Runnable{ 
			
			protected int inputFlow = 10;
		}
		
		protected class StemPushTransportState extends WaterTransportState{
			
			protected PlantSegment nextSegment;
			
			protected StemPushTransportState( PlantSegment nextSegment ){
				this.nextSegment = nextSegment;
			}
			@Override 
			public void run(){ //For stems there is only one next node and thus only one behavior
				if ( GrowingSegment.this.waterLevel >= inputFlow ){
					GrowingSegment.this.pushWaterTo( nextSegment , inputFlow );
				}
			}
			@Override
			public String toString() {
				return "Pushing Stem";
			}
		}
		
		protected class ForkPushTransportState extends WaterTransportState{
			
			protected PlantSegment[] nextSegments;
			private int splitOutputRate;
			private int divisionRemainder;
			
			protected ForkPushTransportState(  PlantSegment...nextNodes){
				
				nextSegments = new PlantSegment[nextNodes.length];
				
				for ( int i = 0 ; i < nextNodes.length ; ++i ){
					nextSegments[i] = nextNodes[i];
				}
				
				this.splitOutputRate = inputFlow / nextSegments.length;
				this.divisionRemainder = inputFlow % nextSegments.length ; //keeps remainer for even flow rate between branches
			}
	
			@Override
			public void run(){

				if ( GrowingSegment.this.waterLevel >= inputFlow ){
					for ( PlantSegment next : nextSegments ){
						GrowingSegment.this.pushWaterTo( next , splitOutputRate );
					}
					GrowingSegment.this.pushWaterTo( nextSegments[0] , divisionRemainder );
				}
			}
			@Override
			public String toString() {
				return "Pushing";
			}
		}

		protected boolean pushWaterTo(PlantSegment partner, int flowRate) {
		
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

		protected boolean pullWaterFrom(PlantSegment partner, int flowRate) {
			
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
		
	}

	/*###################################################################################################################################
	 * 		TRUNK STEM SEGMENT
	 *###################################################################################################################################
	 *###################################################################################################################################
	 *###################################################################################################################################
	 */
	
	public static class StemSegment extends GrowingSegment{
		
			public static int counter = 0;
			
			protected int numberFromLastBranch = 0;
			protected boolean lastBranchedClockwise = false; 
			
			protected StemSegment(int x, int y, int maxGrowth, MattTree organism, TestBoard board) {
				super(x, y, maxGrowth, board);
				counter++;
				StemSegment.this.currentGrowthState = new GrowingState( new FullyGrownEvent() );
				this.organism = organism;
				init( maxGrowth );
			}
			
			public StemSegment(int x, int y, int maxGrowth, TestBoard board) {
				super(x, y, maxGrowth, board);
				
				counter++;
				StemSegment.this.currentGrowthState = new GrowingState( new FullyGrownEvent() );
				
				init( maxGrowth );

			}

			protected void init( int percentMax){
		
				AngularComposite angularComposite = this.addAngularComposite();
				
				CompositeFactory.addAnonymousGraphicTo(this, new GraphicComposite.Rotateable(this){
					@Override
					public void draw(ReferenceFrame camera) {
						//camera.getGraphics().setColor(Color.CYAN);
						//camera.drawString( ""+waterLevel , getX()+10, getY()+10);
						super.draw(camera);
						//camera.getGraphics().setColor(Color.YELLOW);
						//camera.drawString( ""+sugarLevel , getX()+30, getY()+10);
					}
				});
				this.getGraphicComposite().setSprite(twigSmallSprite);
				this.getGraphicComposite().setGraphicSizeFactor(0);
				
				CompositeFactory.addCustomDynamicRotationTo(this, new DynamicRotationComposite.SineWave(this , waveCounter, 0 ) );
				
				if ( this.maxGrowth > 50 ){
					CompositeFactory.addRotationalColliderTo( 
							this, 
							new BoundaryLinear( new Line2D.Double( 0,0 , 0, (int)(-SEGMENT_LENGTH*(maxGrowth/100.0)) ) ), 
							angularComposite 
							);
					
					//this.getColliderComposite().deactivateCollider();
				}
				
				this.name = "stem";
			}
			
			@Override
			protected void spawnInWorld(TestBoard board) {
				board.spawnNewSprout( this, "Tree" );
			}
			
			public void debugMakeWaterSource(){
				
				this.currentGrowthState = new DebugSeedGrowing();
				this.currentWaterTransportState = new DebugWaterSource();
				this.addGraphicTo(twigSmallSprite,true);
			}

			public void debugSetSugarLevel( int level ){
				this.sugarLevel = level;
			}
			
			public int getMaxGrowth(){ return this.maxGrowth; }
			public void killSegment(){ this.maxGrowth = 1;}
			
			public boolean isDead(){ return dead; }
		
			protected int getWaterLevel(){ return this.waterLevel; }

			
			@Override
			public void updateSegment() {
		
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
			
			protected class DebugSeedGrowing extends GrowingState{
				
				private final int GROWTH_TIME = 100;
				private int counter = 0;
				
				@Override
				public void run() {
		
						/*getGraphicComposite().setGraphicSizeFactor( waterLevel / 100.0 );
						
						if ( waterLevel > maxGrowth ){
							StemSegment.this.new SeedFullyGrownEvent().activate();
							StemSegment.this.currentGrowthState = StemSegment.this.new DebugWaterSource();
						}*/
					if (counter < GROWTH_TIME){
						getGraphicComposite().setGraphicSizeFactor( counter / (double)GROWTH_TIME );
						counter++;
					}else{
						StemSegment.this.new SeedFullyGrownEvent().activate();
						StemSegment.this.currentGrowthState = StemSegment.this.new DebugWaterSource();
					}
					
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
					
					//StemSegment.this.getColliderComposite().activateCollider();
					
					int oldRadius = (int) (getMaxGrowth() / 100.0 * SEGMENT_LENGTH );
					
					int randomShrinkage = randomInt(1 , 10); // This being greater than 0 is the only
					//thing stopping the stem from growing infinitely. Adjust chances accordingly
					
					int thisMaxGrowth = getMaxGrowth() - randomShrinkage; //reduce next segment's max growth so its always smaller				
					
					if ( StemSegment.this.getMaxGrowth() > 30) {
					//if ( numberFromLastBranch > randomInt(0, 3) ){ //start new branch every 1-6 segments
						if ( numberFromLastBranch >= 
							Probability.randomInt( StemSegment.this.organism.BRANCH_MIN , organism.BRANCH_MAX) ){ //start new branch every 1-6 segments
							
							final int STEM_FORK_ANGLE = 0;
							final int UPWARD_WILLPOWER = 0; //-20 to 40 look normal. Set to 90 or higher for chaos 
							
							int thisSegmentAngle =  (int) ( getAngularComposite().getAngleInDegrees() % 360) ; // constrain angle to 0-360 for convenience
							
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
							StemSegment sproutLeft = new StemSegment( relativeTip.x , relativeTip.y , thisMaxGrowth , StemSegment.this.organism, board) ;
	
							//LeafStem leafStemRight = new LeafStem(relativeTip.x, relativeTip.y, thisMaxGrowth , StemSegment.this.organism, board);
	
							GrowingSegment leafStemRight = organism.createBranch( relativeTip.x, relativeTip.y, thisMaxGrowth,thisSegmentAngle, board );
							
							if ( StemSegment.this.lastBranchedClockwise ){ //check last branch direction and alternate
								sproutLeft.getAngularComposite().setAngleInDegrees( thisSegmentAngle + STEM_FORK_ANGLE );
								leafStemRight.getAngularComposite().setAngleInDegrees( thisSegmentAngle - organism.LEAF_ANGLE );
								sproutLeft.lastBranchedClockwise = false;
							}
							else{
								leafStemRight.getAngularComposite().setAngleInDegrees( thisSegmentAngle + organism.LEAF_ANGLE );
								sproutLeft.getAngularComposite().setAngleInDegrees( thisSegmentAngle - STEM_FORK_ANGLE );
								sproutLeft.lastBranchedClockwise = true;
							}
							
							spawnConnectAndParentOffshoots( sproutLeft,leafStemRight );
							
							StemSegment.this.currentWaterTransportState = new ForkPushTransportState( leafStemRight, sproutLeft ) ;
							StemSegment.this.currentSugarTransportState = new ForkSugarOverflowTransport( leafStemRight, sproutLeft ) ;
							//StemSegment.this.currentSugarTransportState = new StemSugarOverflowTransport( leafStemRight ) ;
							
							new UpgradeEvent00().activate();
							
							StemSegment.this.waterListener = new TransportWaterListenerActive( 5000 , new UpgradeEvent01() ); 
						}
						else{ // Else segemnt didn't branch, so grown next segment if bigger than 30% grown
							
							final int RANDOM_BEND_RANGE = 5; // 0 is perfectly straight branch. Higher than 40 looks withered.
							final int UPWARD_WILLPOWER = 0; // 
	
							
							//PlantTwigSegment sproutStem = new PlantTwigSegment( endPointX , endPointY , thisMaxGrowth , board) ;	
							
							int randomBend = randomInt( -RANDOM_BEND_RANGE ,RANDOM_BEND_RANGE); // get random bend
							
							int thisSegmentAngle =  (int)(( getAngularComposite().getAngleInDegrees() + randomBend) % 360) ; //And add it to the next segments angle
							
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
							
							StemSegment sproutStem = new StemSegment( relativeTip.x , relativeTip.y , thisMaxGrowth, StemSegment.this.organism, board );
							
							sproutStem.getAngularComposite().setAngleInDegrees(thisSegmentAngle);
							
							//sproutStem.setPreviousStem(StemSegment.this);
							//board.spawnNewSprout( sproutStem ); //then spawn it in
							//CompositeFactory.makeChildOfParent(sproutStem, StemSegment.this , board);
							//StemSegment.this.nextSegments = new PlantSegment[]{sproutStem};
							spawnConnectAndParentOffshoots(sproutStem);
							
							
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
				
			}
			
			protected void spawnConnectAndParentOffshoots( PlantSegment...offshootsList ){
				
				for ( PlantSegment offshoot : offshootsList){
					offshoot.setPreviousStem(StemSegment.this);
					
					//board.spawnNewSprout( offshoot, "Tree" ); //then spawn it in
					
					offshoot.spawnInWorld(board);
					
					CompositeFactory.makeChildOfParent(offshoot, StemSegment.this , board);
				}
				
				StemSegment.this.nextSegments = offshootsList;
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
					
					
					final int RANDOM_BEND_RANGE = 1; // 0 is perfectly straight branch. Higher than 40 looks withered.
					final int UPWARD_WILLPOWER = 0; // 
						
					int randomShrinkage = randomInt( 1 , 10); // This being greater than 0 is the only
						//thing stopping the stem from growing infinitely. Adjust chances accordingly
						
					int thisMaxGrowth = getMaxGrowth() - randomShrinkage; //reduce next segment's max growth so its always smaller
						
						
						//PlantTwigSegment sproutStem = new PlantTwigSegment( endPointX , endPointY , thisMaxGrowth , board) ;	
						
					int randomBend = randomInt( -RANDOM_BEND_RANGE ,RANDOM_BEND_RANGE); // get random bend
						
					int thisSegmentAngle =  (int)(( getAngularComposite().getAngleInDegrees() + randomBend) % 360) ; //And add it to the next segments angle
						
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
		
						
					int oldRadius = (int) (getMaxGrowth() / 100.0 * SEGMENT_LENGTH );
					
					Point relativeTip = StemSegment.this.getAbsolutePositionOf( new Point(0,-(int)oldRadius) );
		
					System.err.println( relativeTip+" ANGLE: "+StemSegment.this.getAngularComposite().getAngleInDegrees() );

					StemSegment sproutStem = new StemSegment( relativeTip.x , relativeTip.y , thisMaxGrowth, StemSegment.this.organism , board );
						
					sproutStem.getAngularComposite().setAngleInDegrees(thisSegmentAngle);

					spawnConnectAndParentOffshoots(sproutStem);
		
					sproutStem.numberFromLastBranch = numberFromLastBranch + 1 ; //Increment next branches number in this stem
//					board.spawnNewSprout( sproutStem ); //then spawn it in
						
					StemSegment.this.currentWaterTransportState = new StemPushTransportState( sproutStem ) ;
					StemSegment.this.currentSugarTransportState = new StemSugarTerminal( sproutStem ) ;
					
					new UpgradeEvent00().activate();
					StemSegment.this.waterListener = new TransportWaterListenerActive( 5000 , new UpgradeEvent01() ); 
					
				}
				
			}
			
			
	
	}
	
	/*###################################################################################################################################
	 * 		LEAF STEM SEGMENT Grows leaves and never branches
	 */
	public static class LeafStem extends StemSegment{ //
		
		public LeafStem(int x, int y, int maxGrowth, MattTree organism, TestBoard board) {
			super(x, y, maxGrowth, organism, board);

			this.currentGrowthState = new LeafStemGrowingState() ;
			this.name = "leafStem";
			this.organism = organism;
		}

		protected class LeafStemGrowingState extends GrowingState{
			
			@Override
			public void run() {

				if ( LeafStem.this.growthLevel >= LeafStem.this.maxGrowth ){

					LeafStem.this.new FullyGrownLeafStemEvent().activate();
					LeafStem.this.currentGrowthState = LeafStem.this.fullyGrownState ;
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
		
		private class FullyGrownLeafStemEvent implements Trigger{
			
			@Override
			public void activate() {
	
				int oldRadius = (int) (getMaxGrowth() / 100.0 * SEGMENT_LENGTH );
				
				Point relativeTip = LeafStem.this.getAbsolutePositionOf( new Point(0,-(int)oldRadius) );
					
				if ( LeafStem.this.getMaxGrowth() > 30 ){
					
					final ArrayList<PlantSegment> offshoots = new ArrayList<PlantSegment>();
	
					int randomShrinkage = randomInt( 5 , 10); // This being greater than 0 is the only
	
					int thisMaxGrowth = getMaxGrowth() - randomShrinkage; //reduce next segment's max growth so its always smaller	
					
					final int RANDOM_BEND_RANGE = 10; // 0 is perfectly straight branch. Higher than 40 looks withered.
					
					final int UPWARD_WILLPOWER = 20; // 
	
					int randomBend = randomInt( -RANDOM_BEND_RANGE ,RANDOM_BEND_RANGE); // get random bend
					
					int thisSegmentAngle =  (int)(( getAngularComposite().getAngleInDegrees() + randomBend) % 360) ; //And add it to the next segments angle
					
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
	
					LeafStem sproutStem = new LeafStem( relativeTip.x , relativeTip.y , thisMaxGrowth, LeafStem.this.organism, board );

					sproutStem.getAngularComposite().setAngleInDegrees(thisSegmentAngle);

					offshoots.add(sproutStem);
					//spawnConnectAndParentOffshoots(sproutStem);
					
					LeafStem.this.nextSegments = new PlantSegment[]{ sproutStem };
					
					sproutStem.numberFromLastBranch = numberFromLastBranch + 1 ; //Increment next branches number in this stem
					
					
					double randAngle =  Math.toRadians( randomInt( thisSegmentAngle-90 , thisSegmentAngle+90) ); // get random bend
					//
					PlantSegment.Leaf newLeaf = new PlantSegment.Leaf(
							relativeTip.x, relativeTip.y, 
							thisMaxGrowth,
							LeafStem.this.getAngularComposite().getAngleInDegrees()+organism.LEAF_ANGLE,
							LeafStem.this.organism,
							board);

					newLeaf.name = "leaf";
					
					offshoots.add(newLeaf);

					//
					
					if ( percentChance(10) && LeafStem.this.getMaxGrowth() >50  ){
						
						PlantSegment.SeedFruit newFruit = new PlantSegment.SeedFruit(
							relativeTip.x, relativeTip.y,
							board);
						newFruit.name = "Fruit";
						newFruit.setPreviousStem(LeafStem.this);
						board.spawnNewSprout( newFruit , "Pickable" ); //then spawn it in
						CompositeFactory.makeChildOfParent(newFruit, LeafStem.this , board, CompositeFactory.TRANSLATIONAL_CHILD);
						offshoots.add(newFruit);
					}
					
					
					LeafStem.this.nextSegments = new PlantSegment[]{sproutStem};
					
					PlantSegment[] newShoots = offshoots.toArray(new PlantSegment[offshoots.size()]);
					
					spawnConnectAndParentOffshoots(newShoots);
					
					LeafStem.this.currentWaterTransportState = new ForkPushTransportState( newShoots ) ;
					LeafStem.this.currentSugarTransportState = new StemSugarOverflowTransport( sproutStem ) ;

				}			
				
			}
		}
		
	}
	
	/*###################################################################################################################################
	 * 		LEAF SEGMENT
	 */
	
	public static class Leaf extends GrowingSegment{

		private Runnable currentGeneratorState;
		private static Sprite leafSprite01 = new Sprite.Stillframe("Prototypes/leaftest01.png",Sprite.CENTERED_BOTTOM);
		private static Sprite leafSprite02 = new Sprite.Stillframe("Prototypes/daveleaf01.png",Sprite.CENTERED_BOTTOM);
		
		public Leaf(int x, int y, int maxGrowth, double angleOffStem, MattTree organism, TestBoard board) {
			super(x, y, maxGrowth, board);
			this.name = "Leaf";
			
			this.addAngularComposite();
			
			CompositeFactory.addGraphicTo(this, leafSprite02, true );
			this.getGraphicComposite().setGraphicSizeFactor(0);
			
			this.getGraphicComposite().setGraphicAngle(Math.toRadians(angleOffStem));
			
			this.currentGrowthState = new LeafGrowingState();
			this.currentGeneratorState = new Photosynthesis();
			
			this.organism = organism;
		}
		
		@Override
		public void updateSegment() {
			
			this.currentGrowthState.run();
			
			this.currentGeneratorState.run();
		}
		
		@Override
		protected void spawnInWorld(TestBoard board) {
			board.spawnNewSprout( this, "Tree" );
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
		
		private class LeafGrowingState extends GrowingState{

			@Override
			public void run() {
				
				if ( Leaf.this.growthLevel >= Leaf.this.maxGrowth ){

					//Leaf.this.new FullyGrownLeafStemEvent().activate();
					Leaf.this.currentGrowthState = Leaf.this.fullyGrownState;
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
	
	
	public static class SeedFruit extends GrowingSegment{

		private static Sprite.Stillframe seedfruitSprite = new Sprite.Stillframe("Prototypes/fruittest_01.png",Sprite.CENTERED_TOP);
		private EntityStatic asteroid;

		public SeedFruit(int x, int y, TestBoard board) {
			super(x, y, 100, board);
			this.asteroid = board.getAsteroid();
			init();
		}

		private void init(){
			
			CompositeFactory.addAnonymousGraphicTo(this, new GraphicComposite.Rotateable(this){
				@Override
				public void draw(ReferenceFrame camera) {
					//camera.getGraphics().setColor(Color.CYAN);
					//camera.drawString( waterLevel+"/"+(growthLevel/(float)maxGrowth) , getX()+10, getY()+50);
					super.draw(camera);
					//camera.getGraphics().setColor(Color.YELLOW);
					//camera.drawString( ""+sugarLevel , getX()+30, getY()+50);
				}
			});
			this.getGraphicComposite().setSprite(seedfruitSprite);
			this.getGraphicComposite().setGraphicSizeFactor(0);
			this.addAngularComposite();
			
			this.addRotationalColliderTo( this.getAngularComposite(), new BoundarySingular(0,10) );
			//this.addColliderTo( new BoundarySingular(0,40), board);
			//this.addColliderTo( new BoundaryCircular(10), board);
			
			this.currentGrowthState = new FruitGrowingState( );

			CompositeFactory.addScriptTo(this, new EntityBehaviorScript("FruitRotation",this) {

				@Override
				protected void updateOwnerEntity(EntityStatic ownerEntity) {
					Vector asteroidDistance = SeedFruit.this.getRelativeTranslationalVectorOf(asteroid).normalLeft();
					SeedFruit.this.getAngularComposite().setAngleInDegrees( asteroidDistance.angleFromVectorInDegrees() );
				}

			});
		}
		
		@Override
		protected void spawnInWorld(TestBoard board) {
			board.spawnNewSprout( this, "Pickable" );
		}
		
		@Override
		public void updateSegment() {
	
			this.currentGrowthState.run(); 
		}
		
		private class FruitGrowingState extends GrowingState{

			@Override
			public void run() {
				
				if ( SeedFruit.this.growthLevel >= SeedFruit.this.maxGrowth ){

					//Leaf.this.new FullyGrownLeafStemEvent().activate();
					SeedFruit.this.currentGrowthState = SeedFruit.this.fullyGrownState ;
				}
				else {
					if ( waterLevel > 20 ){
						SeedFruit.this.waterLevel = SeedFruit.this.waterLevel - 20;
						SeedFruit.this.growthLevel += 1;
						getGraphicComposite().setGraphicSizeFactor( SeedFruit.this.growthLevel / 100.0 );
					}
				}

			}
			
		}
		
	}
	
	public abstract class TreeGenome{
		
		protected final String treeName;
		
		public TreeGenome(String treeName) {
			this.treeName = treeName;
		}
		
		public abstract GrowingSegment createBranch( int x, int y , int angleOfStem, int maxGrowth, TestBoard board );
	}
	
	public class MattTree extends TreeGenome{

		public boolean collidersAreActive = true;
		
		protected int BRANCH_MIN = 0;
		protected int BRANCH_MAX = 0;
		
		protected int LEAF_ANGLE = 40;
		
		public MattTree(String treeName) {
			super(treeName);
		}

		@Override
		public GrowingSegment createBranch( int x, int y , int maxGrowth, int angleOfStemDegrees, TestBoard board ){
			//return new LeafStem( x, y, maxGrowth , this, board);
			return new Leaf(x, y, maxGrowth, angleOfStemDegrees, this, board);
			//return new SeedFruit(x, y, board);
		}
		
		
	}

}
