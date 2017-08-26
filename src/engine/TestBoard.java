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
import entityComposites.Collider;
import entityComposites.CompositeFactory;
import entityComposites.EntityStatic;
import entityComposites.TranslationComposite;
import entityComposites.Collider;
import physics.*;
import physics.Vector;
import sprites.Background;
import sprites.RenderingEngine;
import sprites.Sprite;
import sprites.SpriteStillframe;
import testEntities.*;
import misc.*;



@SuppressWarnings("serial")
public class TestBoard extends BoardAbstract implements MouseWheelListener{
	
	private java.util.Timer updateEntitiesTimer;
	
	MouseHandlerClass myMouseHandler;

	private InputController boardInput = new InputController("Test Board Input");
	
    public TestPlayer player;
    private Force gravity;
    private EntityStatic asteroid;
    
    private Line2D dragLine = new Line2D.Double();

    public static int B_WIDTH;// = 400;
    public static int B_HEIGHT;// = 300;

    public EntityStatic currentDebugEntity;
    
    private OverlayComposite boundaryOverlay;
    private OverlayComposite forcesOverlay;

    public TestBoard(int width , int height, JFrame frame ) {
    	super(width,height,frame);
    	
    	this.renderingEngine = new RenderingEngine( this );
    	this.camera = this.renderingEngine.getCamera();
    	
    	this.diagnosticsOverlay = this.renderingEngine.addOverlay( this.new DiagnosticsOverlay() );
    	this.diagnosticsOverlay.toggle();
    	
    	this.boundaryOverlay = this.renderingEngine.addOverlay( this.new BoundaryOverlay() );
    	//this.boundaryOverlay.toggle();
    	
    	this.inputController.createKeyBinding(KeyEvent.VK_W, new KeyCommand(){
    		public void onPressed() { camera.setDY(-10f); }
    		public void onReleased() { camera.setDY(0); }
    	});
    	this.inputController.createKeyBinding(KeyEvent.VK_A, new KeyCommand(){
    		public void onPressed() { camera.setDX(-10f); }
    		public void onReleased() { camera.setDX(0); }
    	});
    	this.inputController.createKeyBinding(KeyEvent.VK_S, new KeyCommand(){
    		public void onPressed() { camera.setDY(10f); }
    		public void onReleased() { camera.setDY(0); }
    	});
    	this.inputController.createKeyBinding(KeyEvent.VK_D, new KeyCommand(){
    		public void onPressed() { camera.setDX(10f); }
    		public void onReleased() { camera.setDX(0); }
    	});
    	this.inputController.createMouseBinding(MouseEvent.CTRL_MASK , MouseEvent.BUTTON3, new MouseCommand(){
    		public void mousePressed() { asteroid.getRotationComposite().setAngularVelocity(0.1f); }
    	});
    	//MOUSE COMMAND FOR GROWING NEW PLANT  
        this.inputController.createMouseBinding( MouseEvent.CTRL_MASK , MouseEvent.BUTTON1 , new MouseCommand(){
    		public void mousePressed() {
      			//editorPanel.getWorldGeom().mousePressed(e);
    		}
    		public void mouseReleased() {
    			
    			PlantTwigSegment.StemSegment sprout = new PlantTwigSegment.StemSegment( 
      				camera.getLocalX( (int)dragLine.getX1() ), 
      				camera.getLocalY( (int)dragLine.getY1() ),
      				100,
      				TestBoard.this
      			);
    			sprout.name = "Seed" + PlantTwigSegment.StemSegment.counter;
    			sprout.debugMakeWaterSource();
    			sprout.debugSetSugarLevel(700);
      			
      			int angle = 0;
      			
      			if (dragLine.getX2() - dragLine.getX1() < 0)
      				angle = -90+(int) Math.toDegrees( Math.atan( (dragLine.getY2() - dragLine.getY1()) / (dragLine.getX2() - dragLine.getX1()) ) ); 
      			else
      				angle = 90+(int) Math.toDegrees( Math.atan( (dragLine.getY1() - dragLine.getY2()) / (dragLine.getX1() - dragLine.getX2()) ) );
    	  			
      			//sprout.getRotationComposite().setAngularVelocity(0.4f);

      			CompositeFactory.addTranslationTo(sprout);
      			
      			currentScene.addEntity(sprout);
      			
      			sprout.getAngularComposite().setAngleInDegrees( angle);
      			
      			CompositeFactory.makeChildOfParent(sprout, asteroid, TestBoard.this);
      			
      			System.out.println("angle "+angle);
      			
      			dragLine = new Line2D.Double( new Point() , new Point() );
    		}
    	});
        
    	this.collisionEngine = new VisualCollisionEngine(this,renderingEngine); //Refactor to a better name
    	
    	this.forcesOverlay = renderingEngine.addOverlay( ((VisualCollisionEngine)collisionEngine).createForcesOverlay() );
    	
    	initBoard();
    	postInitializeBoard();
    }


    private void initBoard() {
    	
    	myMouseHandler = new MouseHandlerClass();
  		addMouseListener(myMouseHandler);
  		addMouseMotionListener(myMouseHandler);
        setFocusable(true);
        setBackground(Color.BLACK);
        
        asteroid = new EntityStatic( "Asteroid" , 0 , 0 );
        
        CompositeFactory.addGraphicTo(asteroid, new SpriteStillframe("box.png", Sprite.CENTERED ) );
        CompositeFactory.addTranslationTo(asteroid);
        CompositeFactory.addDynamicRotationTo(asteroid);

        //CompositeFactory.addColliderTo(asteroid,  new BoundaryCircular(300, asteroid) );
        
        //CompositeFactory.addColliderTo(asteroid,  new BoundaryPolygonal.Box(500, 200, -250,-100) );
        //asteroid.getTranslationComposite().setDX(-0.25f);
        
        this.currentScene.addEntity(asteroid);
        
        /*
        player = new TestPlayer(20,-400 );
        this.currentScene.addEntity(player);
        gravity = player.getTranslationComposite().addForce( new Vector(0,0) );
        this.addInputController(player.inputController);
    	*/
    }
    
    @Override
    protected void entityThreadRun() {
    	
    	camera.updatePosition();
    	if ( PlantTwigSegment.waveCounter[0] <= 100 ){
    		PlantTwigSegment.waveCounter[0]++;
    	}else{
    		PlantTwigSegment.waveCounter[0] = -100;
    	}
    	

    	collisionEngine.checkCollisions();

    	//gravity.setVector( player.getSeparationUnitVector(asteroid).multiply(0.2) );
    	
    }

    public void spawnNewSprout( EntityStatic newTwig ){
    	this.currentScene.addEntity(newTwig);
    }

/* ########################################################################################################################
 * 
 * 		RENDERING
 * 
 * ########################################################################################################################
 */
    @Override
    protected void graphicsThreadPaint(Graphics g) {
    	
		this.renderingEngine.render( (Graphics2D)g );
		editorPanel.render( g ); 
    	g.setColor( Color.RED );
    	
    	/*for( EntityStatic entity : this.listCurrentSceneEntities() ){
    		camera.drawCrossInWorld( entity.getPosition() , (Graphics2D)g);
    	}*/
    	
    }

    /* ########################################################################################################################
     * 
     * 		MOUSE AND KEY INPUT
     * 
     * ########################################################################################################################
     */
    
    //KEY COMMANDS

    private class TestPlayer extends Player{
    	
    	TranslationComposite trans;
    	Collider collider;
    	private final State moving = new MovingState();
    	State currentState = moving;
    	
    	private Force movementForce;
    	
		public TestPlayer(int x, int y) {
			super(x, y);
			
			CompositeFactory.addGraphicTo(this, new SpriteStillframe("box.png", Sprite.CENTERED) );
			
			Boundary boundary = new BoundarySingular( new Event() );
			Boundary boundary2 = new BoundaryCircular( 80 , this );
			Boundary boundary3 = new BoundaryPolygonal.Box( 20,20,-10,-10 );
			CompositeFactory.addColliderTo(this, boundary2 );
    
			CompositeFactory.addTranslationTo(this);
			
			trans = this.getTranslationComposite();
			collider = this.getColliderComposite();
			
			movementForce = trans.addForce( new Vector(0,0) );
			
			this.inputController.createKeyBinding(KeyEvent.VK_LEFT, new KeyCommand(){
				@Override
				public void onPressed() {
					currentState.onLeft();
				}
			});
			
		}
		
		public void updatePlayer(){
			this.currentState.run();
		}
		
		private class Event extends CollisionEvent{
			@Override
			public void run(BoundaryFeature source, BoundaryFeature collidingWith, Vector separation) {
				
			}
			@Override
			public String toString() {
				return "Hit Something Event";
			}
		}
		
    	private abstract class State implements Runnable{
    		public abstract void onLeft();
    		public abstract void onRight();
    	}
    	
    	private class MovingState extends State{
			@Override
			public void run() {
				
			}
			@Override
			public void onLeft() {
				
			}
			@Override
			public void onRight() {
				
			}
			
    		
    	}
    	
    }
    
	  //MOUSE INPUT
    
  	protected class MouseHandlerClass extends MouseInputAdapter  { 	   

  		@Override
  		public void mousePressed(MouseEvent e){  	
  			dragLine.setLine( e.getPoint() , e.getPoint() );
  			editorPanel.mousePressed(e);
  			inputController.mousePressed(e);
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
  			inputController.mouseReleased(e);
  		}
  			
  	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

    
}