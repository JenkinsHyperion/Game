package engine;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.event.*;

import editing.EditorPanel;
import entities.*; //local imports
import entityComposites.Collider;
import entityComposites.EntityStatic;
import entityComposites.Collider;
import physics.*;
import sprites.Background;
import sprites.RenderingEngine;
import testEntities.*;
import misc.*;



@SuppressWarnings("serial")
public class TestBoard extends BoardAbstract{
	
	private java.util.Timer updateEntitiesTimer;
	
	MouseHandlerClass myMouseHandler;
	
	private CollisionEngine collisionEngine = new CollisionEngine(this); //Refactor to a better name
	private RenderingEngine renderer = new RenderingEngine( this );
	private MovingCamera camera = renderer.getCamera();
	
	private EditorPanel editorPanel;
    public Player player;
    private  PaintOverlay p;
    public Tracer laser;
    protected ArrayList<EntityStatic> staticEntitiesList; 
    protected static ArrayList<EntityDynamic> dynamicEntitiesList; 
    protected static ArrayList<EntityPhysics> physicsEntitiesList; 
    
    private Line2D dragLine = new Line2D.Double();

    public static int B_WIDTH;// = 400;
    public static int B_HEIGHT;// = 300;
    private boolean debug1On = false; 
    private boolean debug2On = false; 

    public EntityStatic currentDebugEntity;
    
    private final int DELAY = 10;
    
    private int[] speedLogDraw = new int[300];
    private int[] speedLog = new int[300];

    public TestBoard(int width , int height ) {
    	super(width,height);
    	
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

    	//camera = new Camera(this);
    	
    	
    	
    	/*TestHinge testEntity = new TestHinge(300,300, new Point( 100 , 5 ));     
    	
    	Collider mesh = new Collider( testEntity );
    	
        testEntity.setCollisionComposite( mesh );

        testEntity.loadSprite("ground01.png" , -150 , 0);

        //testEntity.addForce( new Vector(0,0.01) );
        //testEntity.setAngleInDegrees(45);
        //testEntity.setAngularAcceleration(0.1);
        
        dynamicEntitiesList.add( testEntity );*/
    	
    }
    
    @Override
    protected void entityThreadRun() {
    	
    	updateEntities();
    }

    public void updateEntities(){ 

    	updateDynamicEntities();
    	
    }

    public PlantTwigSegment buildSprout(int x, int y ){
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
    	
    	this.editorPanel.render(g);
    	
    	g.setColor( Color.CYAN );
    	//camera.drawCrossInWorld( ((TestHinge)dynamicEntitiesList.get(0)).getPointLocal() , g);
    	g.drawString( "Number of Entities: "+dynamicEntitiesList.size() , 10,20);
    }
    
    public void drawObjects(Graphics g) {
    	
    	Graphics2D g2 = (Graphics2D)g;
    	
	    for (EntityStatic stat : staticEntitiesList) {
	    	
	        	//stat.getEntitySprite().drawSprite(g2,camera);
	    	stat.getEntitySprite().draw(camera,stat.getGraphicComposite());
	    	
	    }
	    
	    ArrayList<EntityDynamic> dynamicEntitiesBuffer = (ArrayList<EntityDynamic>) dynamicEntitiesList.clone();
	    
	    for (EntityDynamic dynamic : dynamicEntitiesBuffer) {
	    	
        	//stat.getEntitySprite().drawSprite(g2,camera);
	    	dynamic.getEntitySprite().draw(camera,dynamic.getGraphicComposite());
    	
	    }

	    
	    camera.drawCrossInFrame(300, 300, g2);
	    
	    g2.draw( dragLine );

    }
    
    // Update position and Graphic of dynamic objects
    private void updateDynamicEntities() {
    	//for (EntityDynamic dynamicEntity : dynamicObjects) {     	
    	for (int i = 0 ; i < dynamicEntitiesList.size() ; i++){
    		EntityDynamic dynamicEntity = dynamicEntitiesList.get(i);
    		
    			dynamicEntity.updatePosition();
    			dynamicEntity.getEntitySprite().updateSprite();
    			
    			if (dynamicEntity.getY()>768)
    				dynamicEntitiesList.remove(i);
    		
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
	public MovingCamera getCamera() {
		return this.camera;
	}
	
	


	
	  //MOUSE INPUT
  	protected class MouseHandlerClass extends MouseInputAdapter  { 	   

  		@Override
  		public void mousePressed(MouseEvent e)
  		{  	
  			dragLine.setLine( e.getPoint() , e.getPoint() );
  			
  			editorPanel.mousePressed(e);
  			//editorPanel.getWorldGeom().mousePressed(e);
  		}
  		@Override
  		public void mouseDragged(MouseEvent e) 
  		{ 		
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
  		public void mouseReleased(MouseEvent e) 
  		{	
  			editorPanel.mouseReleased(e);
  			//editorPanel.getWorldGeom().mouseReleased(e);
  			
  			PlantTwigSegment sprout = buildSprout( (int)dragLine.getX1(), (int)dragLine.getY1() );
  			
  			int angle = 0;
  			
  			if (dragLine.getX2() - dragLine.getX1() < 0)
  				angle = -90+(int) Math.toDegrees( Math.atan( (dragLine.getY2() - dragLine.getY1()) / (dragLine.getX2() - dragLine.getX1()) ) ); 
  			else
  				angle = 90+(int) Math.toDegrees( Math.atan( (dragLine.getY1() - dragLine.getY2()) / (dragLine.getX1() - dragLine.getX2()) ) );
	  			
  			
  			sprout.setAngle( angle);
  			
  			spawnDynamicEntity( sprout );
  			
  			System.out.println("angle "+angle);
  			
  			dragLine = new Line2D.Double( new Point() , new Point() );
  			
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