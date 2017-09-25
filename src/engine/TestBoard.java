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
import sprites.Sprite.Stillframe;
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
        this.getUnpausedInputController().createMouseBinding( MouseEvent.CTRL_MASK , MouseEvent.BUTTON1 , new MouseCommand(){
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
      			
      			currentScene.addEntity(sprout,"Tree");
      			
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
    	initEditorPanel();
    }


    private void initBoard() {
    	
    	collisionEngine.createColliderGroup("Player");
    	collisionEngine.createColliderGroup("Ground");
        collisionEngine.createColliderGroup("Tree");
        
        collisionEngine.addCustomCollisionsBetween("Player", "Ground", CollisionBuilder.DYNAMIC_STATIC );
        collisionEngine.addCustomCollisionsBetween("Player", "Tree", new PlayerOnTreeCollisions() );
    	
    	myMouseHandler = new MouseHandlerClass();
  		addMouseListener(myMouseHandler);
  		addMouseMotionListener(myMouseHandler);
        setFocusable(true);
        setBackground(Color.BLACK);
        
        asteroid = new EntityStatic( "Asteroid" , 0 , 600 );
        
        CompositeFactory.addGraphicTo(asteroid, new Sprite.Stillframe("box.png", Sprite.CENTERED ) );
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
        
        
        player = new TestPlayer(30,0 );
        CompositeFactory.addRigidbodyTo(player);

        this.currentScene.addEntity(player,"Player");
        gravity = player.getTranslationComposite().addForce( new Vector(0,0) );
        this.addInputController(player.inputController);

        final EntityStatic testSaving = new EntityStatic( "TestSaving", -100,-100 );
        testSaving.addGraphicTo( new Sprite.Stillframe("box.png",Sprite.CENTERED) );
        
        testSaving.addRotationalColliderTo( testSaving.addAngularComposite(), new BoundaryLinear( new Line2D.Double(0,100,0,-100)));
        
        //this.currentScene.addEntity(testSaving,"Ground");
        //testSaving.getAngularComposite().setAngleInDegrees(45);

    }
    
    private class PlayerOnTreeCollisions extends CollisionBuilder{

		@Override
		public Collision createVisualCollision(Collider collider1, Collider collider2, VisualCollisionCheck check,
				RenderingEngine engine) {
			System.err.println("PLAYAR ON TEREE");
			return new Collision( collider1, collider2){
				
				@Override
				public void initCollision() {
					
				}

				@Override
				public void completeCollision() {
					System.out.println("PLAYAR OFF TEREE");
				}

				@Override
				public void updateVisualCollision(MovingCamera camera, Graphics2D gOverlay) {
					isComplete = !check.check(collider1, collider2, camera, gOverlay);
				}
				
			};
			
		}
    	
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
    	this.currentScene.addEntity(newTwig,"Tree");
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
    
    //PLAYER  ########################################################################################################################

    private class TestPlayer extends Player{
    	
    	TranslationComposite trans;
    	Collider collider;
    	private final MovingState movingLeft = new MovingState();
    	private final StandingState standing = new StandingState();
    	private final FallingState falling = new FallingState();
    	State currentState = falling;
    	
    	private Force movementForce;
    	
		public TestPlayer(int x, int y) {
			super(x, y);
			
			CompositeFactory.addGraphicTo(this, new Sprite.Stillframe("box.png", Sprite.CENTERED) );
			
			Boundary boundary = new BoundarySingular( new Event() );
			Boundary boundary2 = new BoundaryCircular( 80 , new Event() );
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
				@Override
				public void onReleased() {
					currentState.offLeft();
				}
			});
			this.inputController.createKeyBinding(KeyEvent.VK_RIGHT, new KeyCommand(){
				@Override
				public void onPressed() {
					currentState.onRight();
				}
				@Override
				public void onReleased() {
					currentState.offRight();
				}
			});
			this.inputController.createKeyBinding(KeyEvent.VK_SPACE, new KeyCommand(){
				@Override
				public void onPressed() {
					currentState.onJump();
				}
				@Override
				public void onReleased() {
					currentState.offJump();
				}
			});
			
			this.inputController.createKeyBinding(KeyEvent.VK_DELETE, new KeyCommand(){
				@Override
				public void onPressed() {
					//TESTING
					Collider collider = player.getColliderComposite();
					if ( collider.isActive() ){
						collider.deactivateCollider();
					}else{
						collider.activateCollider();
					}
					//TESTING
				}
			});
			
			this.collider.setLeavingCollisionEvent( new CollisionEvent(){
				@Override
				public void run(BoundaryFeature source, BoundaryFeature collidingWith, Vector normal) {
					movementForce.setVector(Vector.zeroVector);
					changeState(falling);
				}
			});
			
		}
		
		@Override
		public void updateComposite() {
			super.updateComposite();
			this.currentState.run();
		}

		private class Event extends CollisionEvent{
			@Override
			public void run(BoundaryFeature source, BoundaryFeature collidingWith, Vector separation) {
				changeState(standing);
				System.out.println("HITTING GROUND");
			}
			@Override
			public String toString() {
				return "Hit Something Event";
			}
		}
		
		private void changeState( State state ){
			System.err.println("CHANGE STATE "+currentState+" TO "+state);
			this.currentState = state;
		}
		
    	private abstract class State implements Runnable{
    		public abstract void onLeft();
    		public abstract void offLeft();
    		public abstract void onRight();
    		public abstract void offRight();
    		public abstract void onJump();
    		public abstract void offJump();
    	}
    	
    	
    	private class StandingState extends State{
    		@Override
			public void run() {}
			@Override
			public void onLeft() {
				changeState(movingLeft);
				movingLeft.leftRight = 1;
			}
			@Override
			public void onRight() {
				changeState(movingLeft);
				movingLeft.leftRight = -1;
			}
			@Override
			public void onJump() {
				//movementForce.setVector( gravity.getVector().unitVector().multiply(-1) );
				//trans.addVelocity( gravity.getVector().unitVector().multiply(-5) );
			}
			@Override public void offLeft(){};
			@Override public void offRight(){};
			@Override public void offJump(){};
    	}
    	
    	private class FallingState extends State{
			@Override
			public void run() {}
			@Override public void onLeft(){};
			@Override public void offLeft(){ }
			@Override public void onRight(){};
			@Override public void offRight(){ }
			@Override public void onJump(){};
			@Override public void offJump(){};
			
    	}
    	
    	private class MovingState extends State{
    		
    		protected byte leftRight;
    		protected boolean running = false;
    		
    		private final Runnable accelerating = new Runnable(){
    			public void run(){
    				movementForce.setVector( gravity.getVector().normalLeft().unitVector().multiply(0.1*leftRight) );
    			}
    		};
    		
    		private final Runnable coasting = new Runnable(){
    			public void run(){}
    		};
    		
    		private Runnable currentMovementState = accelerating;
    		
			@Override
			public void run() {
				currentMovementState.run();
			}
			@Override
			public void offRight() {
				movementForce.setVector(0,0);
				currentMovementState = coasting;
			}
			@Override
			public void offLeft() {
				movementForce.setVector(0,0);
				currentMovementState = coasting;
			}
			@Override
			public void onJump() {
				trans.addVelocity( gravity.getVector().inverse().unitVector().multiply(5) );
			}
			
			@Override public void onLeft(){
				if ( leftRight < 0 )
					leftRight = 1;
				
				currentMovementState = accelerating;
			};
			@Override public void onRight(){
				if( leftRight > 0 )
					leftRight = -1;

				currentMovementState = accelerating;
			};
			@Override public void offJump(){};
    	}
    	
    }
    
    
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
		// TODO Auto-generated method stub
	}
	@Override
	protected void initEditorPanel() {
		editorPanel = new EditorPanel(this);
		editorPanel.setSize(new Dimension(240, 300));
		editorPanel.setPreferredSize(new Dimension(240, 300));

	}
}