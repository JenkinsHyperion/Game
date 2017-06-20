package engine;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.event.*;

import Input.InputController;
import Input.KeyCommand;
import Input.MouseCommand;
import editing.EditorPanel;
import entities.*; //local imports
import entityComposites.Collider;
import entityComposites.CompositeFactory;
import entityComposites.EntityStatic;
import entityComposites.Collider;
import physics.*;
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
	
	private CollisionEngine collisionEngine = new CollisionEngine(this); //Refactor to a better name

	private InputController boardInput = new InputController("Test Board Input");
	
    public Player player;
    private EntityStatic asteroid;
    
    private Line2D dragLine = new Line2D.Double();

    public static int B_WIDTH;// = 400;
    public static int B_HEIGHT;// = 300;

    public EntityStatic currentDebugEntity;

    public TestBoard(int width , int height ) {
    	super(width,height);
    	
    	this.renderingEngine = new RenderingEngine( this );
    	this.camera = this.renderingEngine.getCamera();
    	
    	this.diagnosticsOverlay = this.renderingEngine.addOverlay( this.new DiagnosticsOverlay() );
    	this.diagnosticsOverlay.toggle();
    	
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
    		public void mousePressed() { asteroid.getRotationComposite().setAngularVelocity(0.4f); }
    	});
    	//MOUSE COMMAND FOR GROWING NEW PLANT  
        this.inputController.createMouseBinding( MouseEvent.CTRL_MASK , MouseEvent.BUTTON1 , new MouseCommand(){
    		public void mousePressed() {
      			//editorPanel.getWorldGeom().mousePressed(e);
    		}
    		public void mouseReleased() {
    			
    			PlantTwigSegment sprout = new PlantTwigSegment( 
      				camera.getLocalX( (int)dragLine.getX1() ), 
      				camera.getLocalY( (int)dragLine.getY1() ),
      				100,
      				TestBoard.this
      			);
    			sprout.name = "Seed";
    			sprout.debugMakeWaterSource();
      			
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
    	
    	initBoard();
    	initializeBoard();
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
        
        //asteroid.getTranslationComposite().setDX(0.25f);
        
        this.currentScene.addEntity(asteroid);
    	
    }
    
    @Override
    protected void entityThreadRun() {
    	
    	camera.updatePosition();
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
	public void mouseWheelMoved(MouseWheelEvent e) {
		
	}
    
}