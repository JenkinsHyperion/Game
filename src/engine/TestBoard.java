package engine;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.Timer;
import javax.swing.*;
import javax.swing.event.*;

import editing.EditorPanel;
import engine.Board.MouseHandlerClass;
import entities.*; //local imports
import entityComposites.Collidable;
import entityComposites.CollisionProperty;
import entityComposites.NonCollidable;
import physics.*;
import physics.Vector;
import sprites.Background;
import sprites.RenderingLayer;
import sprites.Sprite;
import sprites.SpriteAnimated;
import sprites.SpriteStillframe;
import testEntities.*;
import misc.*;



@SuppressWarnings("serial")
public class TestBoard extends BoardAbstract {
	
	private Background background = new Background("Prototypes/Sky.png"); //move to rendering later
	
	private java.util.Timer updateEntitiesTimer;
	
	MouseHandlerClass myMouseHandler;
	
	private CollisionEngine collisionEngine = new CollisionEngine(this); //Refactor to a better name
	private EditorPanel editorPanel;
    public Player player;
    private  PaintOverlay p;
    public Tracer laser;
    protected ArrayList<EntityStatic> staticEntitiesList; 
    protected static ArrayList<EntityDynamic> dynamicEntitiesList; 
    protected static ArrayList<EntityPhysics> physicsEntitiesList; 
    
    public Camera camera;

    public static int B_WIDTH;// = 400;
    public static int B_HEIGHT;// = 300;
    private boolean debug1On = false; 
    private boolean debug2On = false; 

    public EntityStatic currentDebugEntity;
    
    private final int DELAY = 10;
    
    private int[] speedLogDraw = new int[300];
    private int[] speedLog = new int[300];
    private static int counter=0;


    public TestBoard(int width , int height ) {
    	B_WIDTH = width ;
    	B_HEIGHT = height ;
    	
    	initBoard();
    }
    //over loaded board constructor to accept SidePanel (editor) if editor is to be enabled
    public TestBoard(){
    	
    	initBoard();
    }

    private void initBoard() {
    	
    	myMouseHandler = new MouseHandlerClass();
  		addMouseListener(myMouseHandler);
  		addMouseMotionListener(myMouseHandler);
        setFocusable(true);
        setBackground(Color.BLACK);
        
        staticEntitiesList = new ArrayList<>();
        dynamicEntitiesList = new ArrayList<>();
  
        EntityStatic testEntity;
        
        testEntity = new TestHinge(300,300);     
        testEntity.setCollisionProperties( NonCollidable.getNonCollidable() );

        testEntity.loadSprite("ground_1.png" , -223 , -53 );

        staticEntitiesList.add( testEntity );

    	camera = new Camera(this);
    	
    }
    
    @Override
    protected void entityThreadRun() {
    	
    	updateEntities();
    }

    public void updateEntities(){ 

    	updateDynamicEntities();

    	//camera.updatePosition();
    }

    public void spawnDynamicEntity(EntityDynamic spawn) {

    	dynamicEntitiesList.add(spawn);       	
    }

    public int getBoardHeight(){
    	return B_HEIGHT;
    }

    public int getBoardWidth(){
    	return B_WIDTH;
    }
        

    

/* ########################################################################################################################
 * 
 * 		RENDERING
 * 
 * ########################################################################################################################
 */
    @Override
    protected void graphicsThreadPaint(Graphics g) {
    	
    	drawObjects(g);
    }
    
    
    public void drawObjects(Graphics g) {

    	Graphics2D g2 = (Graphics2D)g;
    	
	    for (EntityStatic stat : staticEntitiesList) {
	    	
	        	//stat.getEntitySprite().drawSprite(g2,camera);
	    	camera.draw(stat.getEntitySprite(), g2);
	    	
	    }
	    
        /*for (EntityDynamic dynamic : dynamicEntitiesList) {
    
        	dynamic.getEntitySprite().drawSprite(g,camera);
        }*/
                
        //if (debug1On){ drawDebugBoundaries(g); }
        //if (debug2On){ drawDebugCollisions(g); }

    }
    
    // Update position and Graphic of dynamic objects
    private void updateDynamicEntities() {
    	//for (EntityDynamic dynamicEntity : dynamicObjects) {     	
    	for (int i = 0 ; i < dynamicEntitiesList.size() ; i++){
    		EntityDynamic dynamicEntity = dynamicEntitiesList.get(i);
    		dynamicEntity.updatePosition();
    		dynamicEntity.getEntitySprite().updateSprite();
    		
    		//wrap objects around screen
    		if ( dynamicEntity.getY() > 300){
    			dynamicEntity.setY(0);
    		}
    		if ( dynamicEntity.getX() < 0){
    			dynamicEntity.setX(400);
    		}
    		
    		//CHECK IF ALIVE. IF NOT, REMOVE. 
    		if ( !dynamicEntity.isAlive()){
    			dynamicEntitiesList.remove(i);
    		}
        }
    	camera.updatePosition();
    } 
    
    // Update position and Graphic of Player
    private void updatePlayer() { 

        player.updatePosition();
        
		player.getEntitySprite().getAnimation().update();
    }

  	
    
    
  	
	
	public int getboundaryX(){ return B_WIDTH ;}
	public int getboundaryY(){ return B_HEIGHT ;}
	
	public Player getPlayer(){ return player; }
	
	public ArrayList<EntityStatic> getStaticEntities(){ return staticEntitiesList; }
	public ArrayList<EntityDynamic> getDynamicEntities(){ return dynamicEntitiesList; }
	public ArrayList<EntityPhysics> getPhysicsEntities(){ return physicsEntitiesList; }

	public void transferEditorPanel(EditorPanel instance){
		this.editorPanel = instance; 
	}
	
	@Override
	public Camera getCamera() {
		return this.camera;
	}
	
	


	
	  //MOUSE INPUT
  	protected class MouseHandlerClass extends MouseInputAdapter  { 		
  	    /*public int clickPositionXOffset;
  	    public int clickPositionYOffset;*/

  		@Override
  		public void mousePressed(MouseEvent e)
  		{  	
  			editorPanel.mousePressed(e);
  			editorPanel.getWorldGeom().mousePressed(e);
  		}
  		@Override
  		public void mouseDragged(MouseEvent e) 
  		{ 		
  			editorPanel.mouseDragged(e);
  			editorPanel.getWorldGeom().mouseDragged(e);
  		}
  		@Override
  		public void mouseMoved(MouseEvent e){
  			editorPanel.mouseMoved(e);
  			editorPanel.getWorldGeom().mouseMoved(e);
  		}
  		@Override
  		public void mouseReleased(MouseEvent e) 
  		{	
  			editorPanel.mouseReleased(e);
  			editorPanel.getWorldGeom().mouseReleased(e);
  		}
  			
  	}
	
	
    
}