package engine;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.event.*;

import editing.EditorPanel;
import entities.*; //local imports
import entityComposites.Collider;
import entityComposites.Collider;
import entityComposites.CompositeFactory;
import entityComposites.EntityFactory;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import physics.*;
import physics.Vector;
import sprites.*;
import testEntities.*;



@SuppressWarnings("serial")
public class Board extends BoardAbstract {
	
	private java.util.Timer updateEntitiesTimer;
	
    private OverlayComposite debugBoundaries;
    private OverlayComposite debugCollisions;
	
    public Player player;
    public Tracer laser;
    
    protected static ArrayList<EntityDynamic> dynamicEntitiesList;  
    
    
    
    // RENDERING DECLARATION

    protected boolean mouseClick = false;

    
    protected MouseHandlerClass myMouseHandler;
    public final int ICRAFT_X = 170;
    public final int ICRAFT_Y = 100;

    public EntityStatic currentDebugEntity;

    private final int[][] pos = {
        {2380, 29}, {2500, 59}, {1380, 89},
        {780, 109}, {580, 139}, {680, 239},
        {790, 259}, {760, 50}, {790, 150},
        {980, 209}, {560, 45}, {510, 70},
        {930, 159}, {590, 80}, {530, 60},
        {940, 59}, {990, 30}, {920, 200},
        {900, 259}, {660, 50}, {540, 90},
        {810, 220}, {860, 20}, {740, 180},
        {820, 128}, {490, 170}, {700, 30}
    };

    public Board(int width , int height ) {
    	super(width,height);
    	
    	initBoard();
    	postInitializeBoard();
    }
    //over loaded board constructor to accept SidePanel (editor) if editor is to be enabled

    private void initBoard() {
    	
    	//INITIALIZE RENDERING
    	this.renderingEngine = new RenderingEngine( this );
    	
    	this.camera = renderingEngine.getCamera(); 
    	this.debugBoundaries = renderingEngine.addOverlay( new DebugBoundaryOverlay() );

    	//this.diagnosticsOverlay = renderingEngine.addOverlay( new DiagnosticsOverlay() );
    	
    	
    	collisionEngine = new VisualCollisionEngine(this , this.renderingEngine ); 
    	
    	
        myMouseHandler = new MouseHandlerClass();
  		addMouseListener(myMouseHandler);
  		addMouseMotionListener(myMouseHandler);
  		addKeyListener(this);
        setFocusable(true);
        
        setBackground(Color.BLACK);

        dynamicEntitiesList = new ArrayList<>();
        
        /* ####################################
        
        	In progress of factorization. For now each collidable and sprite are manually added to the
        	Collision and Rendering Engines, respectively. 
        	
        	Eventually the factory will take care of this upon construction of the composites, and/or the decorators will
        	take the rendering and collision engines as arguments.
        
        #################################### */
        
        
        // initialize player
        player = new PlayerCharacter(ICRAFT_X, ICRAFT_Y,this);
  
        collisionEngine.addDynamicCollidable( player.getColliderComposite() );
        renderingEngine.addSpriteComposite( player.getGraphicComposite() );

        currentScene.addEntity( player ); 
  
        EntityStatic testEntity;
        
        Line2D[] triangleBounds = new Line2D[]{
			new Line2D.Double( -25 , -50 , 2000 , 500 ),
			new Line2D.Double( 2000 , 500 , -25 , 500 ),
			new Line2D.Double( -25 , 500 , -25 , -50 )
		};

		testEntity = EntityFactory.createEntityFromBoundary(300, 500, triangleBounds );
		testEntity.name = "Test Slope";
		CompositeFactory.addGraphicFromCollider( testEntity , testEntity.getColliderComposite() );
		//currentScene.addEntity( testEntity );   

		//renderingEngine.addSpriteComposite( testEntity.getSpriteType() );
        
        Collider collidable;
        
        testEntity = new EntityStatic("Test Ground1",50,500);     
        CompositeFactory.addColliderTo( testEntity , new BoundaryPolygonal.Box(446,100,-223,-50 ) );


        CompositeFactory.addGraphicTo(testEntity, new SpriteStillframe("ground_1.png" , -223 , -53 ) );
        //renderingEngine.addSpriteComposite( testEntity.getSpriteType() );
        currentScene.addEntity( testEntity );
        
        
        testEntity = new EntityStatic("Test Ground",700,500);  
        CompositeFactory.addColliderTo( testEntity , new BoundaryPolygonal.Box(446,100,-223,-50 ) );
        CompositeFactory.addGraphicTo(testEntity, new SpriteStillframe("ground_1.png" , -223 , -53 ));
        CompositeFactory.addTranslationTo( testEntity );
        //renderingEngine.addSpriteComposite( testEntity.getSpriteType() );
        //currentScene.addEntity( testEntity );
        testEntity.getTranslationComposite().setDY(-0.1f);
        //currentScene.addEntity(new EntityPhysics(120,260,"box.png"));
        //dynamicEntitiesList.add(new Bullet(100,100,1,1));
        
      	//EntityRotationalDynamic rotationTest = new TestRotation(-400,400);

      	//currentScene.addEntity( rotationTest );
      	
        //test for LaserTest entity
        //laser = new Tracer(143,260, physicsEntitiesList.get(0) , this ); //later will be parent system
        //dynamicEntitiesList.add(new LaserTest(400,60));  <-- can't add as long as I don't have a sprite for it
        //		--- for now will just draw in the drawObjects() method
        
        //############################################## TESTING BACKGROUND SPRITES #######################
        
        int offset_x = 400;
        int offset_y = 0;
        
        renderingEngine.layersList[7].addGraphicToLayer( EntityFactory.createBackgroundSprite("Prototypes/Sky.png", 0,0 ) );
 
        
        
        camera.setTarget( player );

        
        currentScene.addBackgroundSprite( 6 , EntityFactory.createBackgroundSprite("Prototypes/L7.png", 400-offset_x, 150-offset_y) );
        currentScene.addBackgroundSprite( 5 , EntityFactory.createBackgroundSprite("Prototypes/L6.png", 260-offset_x, -450-offset_y) );//bass
        currentScene.addBackgroundSprite( 4 , EntityFactory.createBackgroundSprite("Prototypes/L5_02.png", 650-offset_x, 400-offset_y) );
        currentScene.addBackgroundSprite( 3 , EntityFactory.createBackgroundSprite("Prototypes/L4.png", 300-offset_x, -300-offset_y) );//base
        currentScene.addBackgroundSprite( 2 , EntityFactory.createBackgroundSprite("Prototypes/L5_03.png", 650-offset_x, 500-offset_y) ); //forest
        currentScene.addBackgroundSprite( 1 , EntityFactory.createBackgroundSprite("Prototypes/L2.png", 250-offset_x, -160-offset_y) );//pipe
        currentScene.addBackgroundSprite( 0 , EntityFactory.createBackgroundSprite("Prototypes/L1_01.png", 300-offset_x, -160-offset_y) );//base
        
        //renderingEngine.layersList[4].addEntity( EntityFactory.createBackgroundScroll( "Prototypes/shader_rain01.png", getBoardWidth() ,getBoardHeight() ,0,-3  ) );
        renderingEngine.layersList[1].addGraphicToLayer( EntityFactory.createBackgroundScroll( "Prototypes/shader_rain03.png", getBoardWidth() ,getBoardHeight() ,0,-8  ) );
       
        
        initBullets();
    
        /*//ADD COLLIDABLES TO COLLISION ENGINE\
        //TO BE MOVED TO MASTER ADDING FACTOR THAT SORTS COMPOSITES AND PASSES THEM TO RESPECTIVE ENGINES
        
        for ( EntityStatic stat : this.currentScene. ){
        	collisionEngine.addStaticCollidable( (Collider) stat.getCollisionType() );
        }*/

        
    } 
    //END INITIALIZE #############################################################################
    
    @Override
    protected void graphicsThreadPaint(Graphics g) {
    	camera.repaint(g);
 		//System.out.println("Graphics running");
        
        drawObjects( (Graphics2D) g );
        
    }
    
   
    
    
    @Override
    protected void entityThreadRun() {
    	updateEntities();
    	collisionEngine.checkCollisions();
    }
     
    private void updateEntities(){ //RUN POSITION AND DRAW UPDATES
    	
		          updatePlayer();    
		          updateDynamicEntities();
			      
			      camera.updatePosition();   
      }
    
    // Update position and Graphic of dynamic objects
    private void updateDynamicEntities() {
    	//for (EntityDynamic dynamicEntity : dynamicObjects) {     	
    	for (int i = 0 ; i < dynamicEntitiesList.size() ; i++){
    		EntityDynamic dynamicEntity = dynamicEntitiesList.get(i);
    		dynamicEntity.updatePosition();
    		dynamicEntity.getEntitySprite().updateSprite();
    		
    		//wrap objects around screen

    		
    		//CHECK IF ALIVE. IF NOT, REMOVE. 
    		if ( !dynamicEntity.isAlive()){
    			dynamicEntitiesList.remove(i);
    		}
        }
    	camera.updatePosition();
    } 
    
    // Update position and Graphic of Player
    private void updatePlayer() { 

		player.getEntitySprite().getAnimation().update();
    }
    
    /*private void updatePhysicsEntities() {
    	//for (EntityDynamic dynamicEntity : dynamicObjects) {     	
    	for (int i = 0 ; i < physicsEntitiesList.size() ; i++){
    		EntityDynamic physicsEntity = physicsEntitiesList.get(i);
    		physicsEntity.updatePosition();
    		physicsEntity.getEntitySprite().updateSprite();
        }
    	
    }*/

    public void initBullets() {

        /*for (int[] p : pos) {
            dynamicEntitiesList.add(new Bullet(p[0], p[1],-1,1)); 
        }*/
        
    }
    

    public void spawnDynamicEntity(EntityDynamic spawn) {
        	
        	dynamicEntitiesList.add(spawn);       	
    }
        
        //check board dimensions
    public int getBoardHeight(){
        return B_HEIGHT;
    }
        
    public int getBoardWidth(){
       	return B_WIDTH;
    }
        

    

/* ########################################################################################################################
 * 		RENDERING
 * ########################################################################################################################
 */
    public void drawObjects(Graphics2D g2) {
    	
    	// TESTING RENDERING ENGINE
    	
    	this.renderingEngine.render(g2);
    	
    	
        
        //####################################################

        editorPanel.render( camera.getGraphics() ); 
        
        //####################################################

    }
    
    
  //MOUSE INPUT
  	protected class MouseHandlerClass extends MouseInputAdapter  { 		
  	    /*public int clickPositionXOffset;
  	    public int clickPositionYOffset;*/

  		@Override
  		public void mousePressed(MouseEvent e)
  		{  	
  			player.inputController.mousePressed(e);
  			editorPanel.mousePressed(e);
  			//editorPanel.getWorldGeom().mousePressed(e);
  		}
  		@Override
  		public void mouseDragged(MouseEvent e) 
  		{ 		
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
  			player.inputController.mouseReleased(e);
  			editorPanel.mouseReleased(e);
  			//editorPanel.getWorldGeom().mouseReleased(e);
  		}
  			
  	}
  	
  	//protected class KeyHandlerClass implements KeyListener  { 	
  	
  		@Override
        public void keyReleased(KeyEvent e) {
            player.inputController.keyReleased(e);
            editorPanel.keyReleased(e);         
        }
  		@Override
        public void keyPressed(KeyEvent e) {
            player.inputController.keyPressed(e);
            editorPanel.keyPressed(e);
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_F1) {
            	if (camera.isLocked())
            		camera.unlock();
            	else
            		camera.lockAtPosition(MovingCamera.ORIGIN);
            }  
            
            else if (key == KeyEvent.VK_F2) {

            	debugBoundaries.toggle();
            	
            }  

            else if (key == KeyEvent.VK_F3) {
            	
            }
            else if (key == KeyEvent.VK_F4) {
            	this.diagnosticsOverlay.toggle();
            }
            else if (key == KeyEvent.VK_ESCAPE) {
            	//editorPanel.entityPlacementMode = false;
            	//editorPanel.mode = EditorPanel.DEFAULT_MODE;
            	//editorPanel.nullifyGhostSprite();
            	//editorPanel.nullifyGhostVertex();
            }   
        }
		@Override
		public void keyTyped(KeyEvent e) {
			
		}
  	//}

    
/* ########################################################################################################################
 * 
 * 		DEBUG RENDERING - TO BE MOVED TO OVERLAY CLASSES
 * 
 * ########################################################################################################################
 */
    
	private class DebugBoundaryOverlay implements Overlay{
		
			public void paintOverlay(Graphics2D g2, MovingCamera cam){ // DEBUG GUI
		
		    	g2.setColor(new Color(0, 0, 0, 150));
		        g2.fillRect(0, 0, B_WIDTH, B_HEIGHT);
		        
		        g2.setColor(Color.GRAY);
			    g2.drawString("Entities: "+ currentScene.listEntities().length + " , Collidables:"+
			    		collisionEngine.debugNumberofStaticCollidables() +" + "+
			    		collisionEngine.debugNumberofDynamicCollidables() ,5,15);
			    g2.drawString("DX: "+player.getTranslationComposite().getDX() + " DY: " + player.getTranslationComposite().getDY(),5,30);
			    g2.drawString("AccX: " + player.getTranslationComposite().getAccX() + "  AccY: " + player.getTranslationComposite().getAccY(),5,45);
			    g2.drawString("Rotation: " + (int)player.getAngularComposite().getAngle() + " degrees " + player.getRotationComposite().getAngularVel() + " " + player.getRotationComposite().getAngularAcc(),5,60);
			    g2.drawString("State: "+ ((PlayerCharacter)player).printState() + "Colliding: TODO" ,5,75);
			    g2.drawString( ((PlayerCharacter)player).printBufferState() ,5,90 );

			    //Draw player bounding box
		
			    g2.setColor(Color.CYAN);
			    
			    player.inputController.debugPrintInputList(5, 105, g2);
			    //collisionEngine.debugPrintCollisionList(5, 105, g2);
			    
			    
			    player.getColliderComposite().getBoundaryLocal().debugDrawBoundary(camera , g2, player);
			    
			    for (Collider collider : collisionEngine.debugListActiveColliders() ){
			    	
			    	collider.getBoundaryLocal().debugDrawBoundary(camera , g2, collider.getOwnerEntity() );
			    	camera.drawCrossInWorld( collider.getOwnerEntity().getPosition());
			    	
			    }


		    }
    
	}
    
    private void drawDebugSAT( EntityStatic playerRef , EntityStatic entitySecondary , Graphics2D g2 ){
  	
	    
    }
    
    private void drawCross(int x, int y , Graphics g){
    	g.drawLine( x-3, y-3, x+3, y+3 );
		g.drawLine( x-3, y+3, x+3, y-3 );
    }


    
/* ########################################################################################################################
 * 
 * 		GET AND SET METHODS AND OTHER MISC STUFF
 * 
 * ########################################################################################################################
 */
    
    /*@Override
    public MovingCamera getCamera() { return this.camera; }*/
	
	public int getboundaryX(){ return B_WIDTH ;}
	public int getboundaryY(){ return B_HEIGHT ;}
	
	public Player getPlayer(){ return player; }
	
	//public ArrayList<EntityStatic> getStaticEntities(){ return entitiesList; }
	public ArrayList<EntityDynamic> getDynamicEntities(){ return dynamicEntitiesList; }

	public void transferEditorPanel(EditorPanel instance){
		this.editorPanel = instance;  
	}
    
	
	/*public void testingDeconstructScene(){
		
		this.renderingEngine.debugClearRenderer();
		this.collisionEngine.degubClearCollidables();
		
	}
	*/
	@Override
	public void createNewScene(Scene scene){ //NOT DEREFFED YET

		this.currentScene = scene;

		//put player back into rendering
		((GraphicComposite.Active) player.getGraphicComposite()).addCompositeToRenderer(renderingEngine);
		
	}
	
	
}