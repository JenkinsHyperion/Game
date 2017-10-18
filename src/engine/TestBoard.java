package engine;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.event.*;

import Input.InputController;
import Input.KeyCommand;
import Input.MouseCommand;
import editing.EditorPanel;
import entities.*; //local imports
import entityComposites.AngularComposite;
import entityComposites.Collider;
import entityComposites.CompositeFactory;
import entityComposites.EntityStatic;
import entityComposites.TranslationComposite;
import entityComposites.TranslationComposite.VelocityVector;
import entityComposites.Collider;
import physics.*;
import physics.CollisionEngine.ColliderGroup;
import physics.Vector;
import sprites.Background;
import sprites.RenderingEngine;
import sprites.Sprite;
import sprites.Sprite.Stillframe;
import testEntities.*;
import testEntities.PlantSegment.SeedFruit;
import testEntities.PlantSegment.StemSegment;
import testEntities.PlantSegment.TreeUnit;
import misc.*;



@SuppressWarnings("serial")
public class TestBoard extends BoardAbstract{
	
	private java.util.Timer updateEntitiesTimer;
	
	MouseHandlerClass myMouseHandler;

	private InputController boardInput = new InputController("Test Board Input");

	private MovingCamera.FollowTargetAroundPoint cameraRotationBehavior;
	
    public PlantPlayer player;
    private Force gravity;
    private Asteroid asteroid;
    private Asteroid testAsteroid;

    private Line2D dragLine = new Line2D.Double();

    public static int B_WIDTH;// = 400;
    public static int B_HEIGHT;// = 300;

    public EntityStatic currentDebugEntity;
    
    private OverlayComposite boundaryOverlay;
    private OverlayComposite forcesOverlay;
    
    public static ColliderGroup<PlantPlayer> playerGroup;
    public static ColliderGroup<EntityStatic> worldGeometryGroup;
    public static ColliderGroup<PlantSegment> treeStemGroup;
    public static ColliderGroup<SeedFruit> pickableGroup;

    public TestBoard(int width , int height, JFrame frame ) {
    	super(width,height,frame);
    	
    	this.renderingEngine = new RenderingEngine( this );
    	this.camera = this.renderingEngine.getCamera();
    	
    	this.diagnosticsOverlay = this.renderingEngine.addOverlay( this.new DiagnosticsOverlay() );
    	this.diagnosticsOverlay.toggle();
    	
    	this.boundaryOverlay = this.renderingEngine.addOverlay( this.new BoundaryOverlay() );
    	//this.boundaryOverlay.toggle();
    	//TEST BUTTON
    	
    	this.getUnpausedInputController().createKeyBinding(KeyEvent.VK_F1, new KeyCommand(){
    		public void onPressed() { boundaryOverlay.toggle(); }
    		public void onReleased() {  }
    	});
    	this.getUnpausedInputController().createKeyBinding(KeyEvent.VK_F2, new KeyCommand(){
    		public void onPressed() { ((VisualCollisionEngine) collisionEngine).toggleCalculationDisplay(); }
    		public void onReleased() {  }
    	});
    	this.getUnpausedInputController().createKeyBinding(KeyEvent.VK_5, new KeyCommand(){
    		public void onPressed() { CompositeFactory.makeChildOfParent(testAsteroid, player, TestBoard.this); }
    		public void onReleased() {  }
    	});
    	this.getUnpausedInputController().createKeyBinding(KeyEvent.VK_6, new KeyCommand(){
    		public void onPressed() { CompositeFactory.abandonAllChildren(player); }
    		public void onReleased() {  }
    	});
    	this.getUnpausedInputController().createKeyBinding(KeyEvent.VK_7, new KeyCommand(){
    		public void onPressed() { camera.lockAtCurrentPosition(); }
    		public void onReleased() {  }
    	});
    	//TEST BUTTON
    	this.getUnpausedInputController().createKeyBinding(KeyEvent.VK_DIVIDE, new KeyCommand(){
    		public void onPressed() { camera.setAngle( camera.getAngle() + Math.PI/360.0 ); }
    		public void onReleased() {  }
    	});
    	this.getUnpausedInputController().createKeyBinding(KeyEvent.VK_W, new KeyCommand(){
    		public void onPressed() { camera.setDY(-10f); }
    		public void onReleased() { camera.setDY(0); }
    	});
    	this.getUnpausedInputController().createKeyBinding(KeyEvent.VK_A, new KeyCommand(){
    		public void onPressed() { camera.setDX(-10f); }
    		public void onReleased() { camera.setDX(0); }
    	});
    	this.getUnpausedInputController().createKeyBinding(KeyEvent.VK_S, new KeyCommand(){
    		public void onPressed() { camera.setDY(10f); }
    		public void onReleased() { camera.setDY(0); }
    	});
    	this.getUnpausedInputController().createKeyBinding(KeyEvent.VK_D, new KeyCommand(){
    		public void onPressed() { camera.setDX(10f); }
    		public void onReleased() { camera.setDX(0); }
    	});
    	this.getUnpausedInputController().createMouseBinding(MouseEvent.CTRL_MASK , MouseEvent.BUTTON3, new MouseCommand(){
    		public void mousePressed() { asteroid.getRotationComposite().setAngularVelocity(0.1f); }
    	});
    	//MOUSE COMMAND FOR GROWING NEW PLANT  
        this.getUnpausedInputController().createMouseBinding( MouseEvent.CTRL_MASK , MouseEvent.BUTTON2 , new MouseCommand(){
    		public void mousePressed() {
      			//editorPanel.getWorldGeom().mousePressed(e);
    		}
    		public void mouseReleased() {
    			
    			Point p1 = camera.getWorldPos( dragLine.getP1() );
    			
    			PlantSegment.StemSegment sprout = new PlantSegment.StemSegment( 
      				p1.x, 
      				p1.y,
      				100,
      				TestBoard.this
      			);
    			sprout.name = "Seed" + PlantSegment.StemSegment.counter;
    			
    			sprout.debugMakeWaterSource();
    			sprout.debugSetSugarLevel(700);
    			
      			int angle = 0;
      			
      			if (dragLine.getX2() - dragLine.getX1() < 0)
      				angle = -90+(int) Math.toDegrees( Math.atan( (dragLine.getY2() - dragLine.getY1()) / (dragLine.getX2() - dragLine.getX1()) ) ); 
      			else
      				angle = 90+(int) Math.toDegrees( Math.atan( (dragLine.getY1() - dragLine.getY2()) / (dragLine.getX1() - dragLine.getX2()) ) );
    	  			
      			//sprout.getRotationComposite().setAngularVelocity(0.4f);

      			CompositeFactory.addTranslationTo(sprout);
      			
      			sprout.getAngularComposite().setAngleInDegrees( angle - Math.toDegrees(camera.getAngle()) );
      			
      			System.out.println("angle "+angle);
      			
      			dragLine = new Line2D.Double( new Point() , new Point() );
      			
      			currentScene.addEntity(sprout,"Tree");
      			
      			CompositeFactory.makeChildOfParent(sprout, asteroid, TestBoard.this);
    		}
    	});
        
    	this.collisionEngine = new VisualCollisionEngine(this,renderingEngine); //Refactor to a better name
    	
    	this.forcesOverlay = renderingEngine.addOverlay( ((VisualCollisionEngine)collisionEngine).createForcesOverlay() );
    	
    	initBoard();
    	postInitializeBoard();
    	initEditorPanel();
    }


    private void initBoard() {
    	
        pickableGroup = collisionEngine.<SeedFruit>createColliderGroup("Pickable");
    	playerGroup = collisionEngine.<PlantPlayer>createColliderGroup("Player");
    	worldGeometryGroup = collisionEngine.<EntityStatic>createColliderGroup("Ground");
        treeStemGroup = collisionEngine.<PlantSegment>createColliderGroup("Tree");

        
        collisionEngine.addCustomCollisionsBetween(playerGroup, worldGeometryGroup, new PlantPlayer.GroundCollision() );

        //collisionEngine.addCustomCollisionsBetween("Player", "Tree", new PlantPlayer.ClingCollision() );
        
        collisionEngine.addCustomCollisionsBetween( playerGroup, treeStemGroup, new PlantPlayer.ClingCollision() );
        
        collisionEngine.addCustomCollisionsBetween( playerGroup, pickableGroup, new PlantPlayer.FruitInRange() );
    	
    	myMouseHandler = new MouseHandlerClass();
  		this.addMouseListener(myMouseHandler);
  		this.addMouseMotionListener(myMouseHandler);
        setFocusable(true);
        setBackground(Color.BLACK);
        
        asteroid = new Asteroid( 0 , 600 ,this);
        
        CompositeFactory.addGraphicTo(asteroid, new Sprite.Stillframe("asteroid02.png", Sprite.CENTERED ) );
        asteroid.getGraphicComposite().setGraphicSizeFactor(0.334);
        //CompositeFactory.addTranslationTo(asteroid);
        CompositeFactory.addDynamicRotationTo(asteroid);

        Boundary bounds1 = new BoundaryCircular(500);
        //CompositeFactory.addColliderTo(asteroid,  new BoundaryLinear( new Line2D.Double( 0 , 100 , 0, -100 ) ) );
        //CompositeFactory.addColliderTo(asteroid,  new BoundaryPolygonal.Box(100, 200, -50, -100) );
        
        CompositeFactory.addRotationalColliderTo(
        		asteroid, 
        		bounds1, 
        		asteroid.getAngularComposite()
        		);
        
        CompositeFactory.addRigidbodyTo(asteroid);
        
        //CompositeFactory.addColliderTo(asteroid,  new BoundaryPolygonal.Box(500, 200, -250,-100) );
        //asteroid.getTranslationComposite().setDX(-0.25f);
        
        this.currentScene.addEntity(asteroid,"Ground");
        
        asteroid.spawnGrass();
        
        //asteroid.populateGrass(this);
        //
        /*testAsteroid = new EntityStatic( "Asteroid" , -900 , -2000 );
        
        CompositeFactory.addRotationalColliderTo(
        		testAsteroid, 
        		new BoundaryCircular(1000), 
        		testAsteroid.getAngularComposite()
        		);
        
        CompositeFactory.addRigidbodyTo(testAsteroid);
        this.currentScene.addEntity(testAsteroid,"Pickable");*/
        //
        
        
        player = new PlantPlayer(30,-100);
        CompositeFactory.addRigidbodyTo(player);

        this.currentScene.addEntity(player,"Player");
        gravity = player.getTranslationComposite().addForce( new Vector(0,0) );
        
        player.setGravity(gravity);
        
        this.addInputController(player.inputController);

        /*final EntityStatic testSaving = new EntityStatic( "TestSaving", -100,-100 );
        testSaving.addGraphicTo( new Sprite.Stillframe("box.png",Sprite.CENTERED) );
        
        testSaving.addRotationalColliderTo( testSaving.addAngularComposite(), new BoundaryLinear( new Line2D.Double(0,100,0,-100)));

        
        //this.currentScene.addEntity(testSaving,"Testing");
        testSaving.getAngularComposite().setAngleInDegrees(45);*/
        
       // final PlantTwigSegment testStem = new PlantTwigSegment.StemSegment(-100, 30, 100, this);
       // testStem.getAngularComposite().setAngleInDegrees(-45);
        //this.currentScene.addEntity(testStem, "Tree" );
        
        //PlantSegment.SeedFruit testFruit = new PlantSegment.SeedFruit(100, 0, this);
        //currentScene.addEntity(testFruit, "Pickable");
 
    	this.cameraRotationBehavior = camera.createRotationalCameraBehavior(player,asteroid.getPosition() );
    }
    
    public EntityStatic getAsteroid(){
    	return this.asteroid;
    }

    @Override
    protected void entityThreadRun() {
    	
    	if ( PlantSegment.waveCounter[0] <= 100 ){
    		PlantSegment.waveCounter[0]++;
    	}else{
    		PlantSegment.waveCounter[0] = -100;
    	}
    	
    	collisionEngine.checkCollisions();

    	gravity.setVector( player.getSeparationUnitVector(asteroid).multiply(0.2) );
    	
    	double playerAbsoluteAngle = gravity.toVector().normalLeft().angleFromVectorInRadians();
    	
		player.getAngularComposite().setAngleInRadians( playerAbsoluteAngle );
		
		//TESTING CAMERA ROTATION <ETHODS to be moved into camera when working

		cameraRotationBehavior.manualUpdatePosition( -playerAbsoluteAngle, player.getPlayerCameraFocus() );
    }

    public void spawnNewSprout( EntityStatic newTwig, String group ){
    	this.currentScene.addEntity(newTwig,group);
    }

/* ########################################################################################################################
 * 
 * 		RENDERING
 * 
 * ########################################################################################################################
 */
    
    @Override
    public void paint(Graphics g) {
    	super.paint(g); 
    	graphicsThreadPaint(g);
    }
    
    @Override
    protected void graphicsThreadPaint(Graphics g) {
    	
    	Graphics2D g2 = (Graphics2D)g;
    	
		this.renderingEngine.render( g2 );
		editorPanel.render( g ); 
    	g.setColor( Color.RED );

    	//this.player.debugCollisions(camera, g2);
    	this.player.debugDraw(camera, g2);

    	
    	
    	for ( Vector vector : player.getTranslationComposite().debugForceArrows() ){
    		camera.drawInBoard( vector.multiply(300).toLine( player.getPosition() ), (Graphics2D)g );
    	}
    	
    	camera.drawLineInWorld(dragLine, g2);
    	
    }
    
    @Override
    public void activeRender(Graphics g) {
    	
    	Graphics2D g2 = (Graphics2D)g;
    	
		this.renderingEngine.render( g2 );
		editorPanel.render( g ); 
    	g.setColor( Color.RED );

    	//this.player.debugCollisions(camera, g2);
    	this.player.debugDraw(camera, g2);
    	
    	//for ( Vector vector : player.getTranslationComposite().debugForceArrows() ){
    	//	camera.drawInBoard( vector.multiply(300).toLine( player.getPosition() ), (Graphics2D)g );
    	//}
    }

    /* ########################################################################################################################
     * 
     * 		MOUSE AND KEY INPUT
     * 
     * ########################################################################################################################
     */

	//MOUSE INPUT ########################################################################################################################
    
  	protected class MouseHandlerClass extends MouseInputAdapter  { 	   

  		@Override
  		public void mousePressed(MouseEvent e){  	 
  			dragLine.setLine( e.getPoint() , e.getPoint() );
  			editorPanel.mousePressed(e);
  			getCurrentBoardInputController().mousePressed(e);
  		}
  		@Override
  		public void mouseDragged(MouseEvent e){ 		
  			dragLine.setLine( dragLine.getP1(), e.getPoint());
  			
  			editorPanel.mouseDragged(e);
  			//editorPanel.getWorldGeom().mouseDragged(e);
  		}
  		@Override
  		public void mouseMoved(MouseEvent e){
  			editorPanel.mouseMoved(e);
  			//editorPanel.getWorldGeom().mouseMoved(e);
  		}
  		@Override
  		public void mouseReleased(MouseEvent e) {	
  			editorPanel.mouseReleased(e);
  			getCurrentBoardInputController().mouseReleased(e);
  		}
  			
  	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		
		if ( arg0.getWheelRotation() > 0 ){
			camera.quadupleZoom();
		}
		else{
			camera.quarterZoom();
		}

		
	}
	@Override
	protected void initEditorPanel() {
		editorPanel = new EditorPanel(this);
		editorPanel.setSize(new Dimension(240, 300));
		editorPanel.setPreferredSize(new Dimension(240, 300));

	}
}