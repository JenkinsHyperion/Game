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
public class TestBoard extends BoardAbstract{
	
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

    public TestBoard(int width , int height ) {
    	B_WIDTH = width ;
    	B_HEIGHT = height ;
    	
    	initBoard();
    	initializeBoard();
    }
    //over loaded board constructor to accept SidePanel (editor) if editor is to be enabled
    public TestBoard(){
    	
    	initBoard();
    	initializeBoard();
    }

    private void initBoard() {
    	
    	myMouseHandler = new MouseHandlerClass();
  		addMouseListener(myMouseHandler);
  		addMouseMotionListener(myMouseHandler);
        setFocusable(true);
        setBackground(Color.BLACK);
        
        staticEntitiesList = new ArrayList<>();
        dynamicEntitiesList = new ArrayList<>();

        //dynamicEntitiesList.add( new PlantTwigSegment(300,300,this) );

    	camera = new Camera(this);
    	
    	
    	
    	TestHinge testEntity = new TestHinge(300,300, new Point( 100 , 5 ));     
    	
    	Collidable mesh = new Collidable( testEntity );
    	
        testEntity.setCollisionProperties( mesh );

        testEntity.loadSprite("ground01.png" , 0 , 0);

        //mesh.addForce( new Vector(0,0.01) );
        //testEntity.setAngleInDegrees(45);
        testEntity.setAngularVelocity(2);
        
        dynamicEntitiesList.add( testEntity );
    	
    }
    
    @Override
    protected void entityThreadRun() {
    	
    	updateEntities();
    }

    public void updateEntities(){ 

    	updateDynamicEntities();

    	//camera.updatePosition();
    }

    public EntityDynamic buildSprout(int x, int y ){
    	return new PlantTwigSegment(x, y, 100, this);
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
	    	stat.getEntitySprite().drawSprite(g2, camera);
	    	
	    }
	    
	    ArrayList<EntityDynamic> dynamicEntitiesBuffer = (ArrayList<EntityDynamic>) dynamicEntitiesList.clone();
	    
	    for (EntityDynamic dynamic : dynamicEntitiesBuffer) {
	    	
        	//stat.getEntitySprite().drawSprite(g2,camera);
	    	dynamic.getEntitySprite().drawSprite(g2, camera);
    	
    }

        /*for (EntityDynamic dynamic : dynamicEntitiesList) {
    
        	dynamic.getEntitySprite().drawSprite(g,camera);
        }*/
                
        //if (debug1On){ drawDebugBoundaries(g); }
        //if (debug2On){ drawDebugCollisions(g); }
	    
	    camera.drawCrossInWorld(300, 300, g2);

    }
    
    // Update position and Graphic of dynamic objects
    private void updateDynamicEntities() {
    	//for (EntityDynamic dynamicEntity : dynamicObjects) {     	
    	for (int i = 0 ; i < dynamicEntitiesList.size() ; i++){
    		EntityDynamic dynamicEntity = dynamicEntitiesList.get(i);
    		dynamicEntity.updatePosition();
    		dynamicEntity.getEntitySprite().updateSprite();
    		
    		//System.out.println( "angle " + ((EntityRotationalDynamic)dynamicEntity).getAngle() );
    		
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
  			
  			spawnDynamicEntity( buildSprout( e .getX(), e.getY()) );
  			
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





	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
    
}