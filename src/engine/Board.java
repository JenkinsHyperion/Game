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
import entityComposites.EntityComposite;
import entityComposites.GraphicComposite;
import physics.*;
import physics.Vector;
import sprites.*;
import testEntities.*;



@SuppressWarnings("serial")
public class Board extends BoardAbstract {
	
	private Background background = new Background("Prototypes/Sky.png"); //move to rendering later
	
	private java.util.Timer updateEntitiesTimer;
	
    private OverlayComposite debugBoundaries;
    private OverlayComposite debugCollisions;
	
    public Player player;
    public Tracer laser;
    
    protected static ArrayList<EntityDynamic> dynamicEntitiesList;  
    
    // RENDERING DECLARATION
    private RenderingLayer[] layer = {
    		
    		new RenderingLayer(1,1),
    		new RenderingLayer(1.15,1.15),
    		new RenderingLayer(1.3, 1.3),
    		new RenderingLayer(1.6, 1.6 ),
    		new RenderingLayer(1.8, 1.8),
    		new RenderingLayer(3, 3),
    		new RenderingLayer(5, 5)
    };

    protected Point clickPosition;
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
    	initializeBoard();
    }
    //over loaded board constructor to accept SidePanel (editor) if editor is to be enabled

    private void initBoard() {
    	
    	//INITIALIZE RENDERING
    	this.renderingEngine = new RenderingEngine( this );
    	
    	this.camera = renderingEngine.getCamera();
    	this.debugBoundaries = renderingEngine.addOverlay( new DebugBoundaryOverlay() );
    	this.diagnosticsOverlay = renderingEngine.addOverlay( new DiagnosticsOverlay() );
    	
    	
    	collisionEngine = new CollisionEngine(this); 
    	
    	
        myMouseHandler = new MouseHandlerClass();
  		addMouseListener(myMouseHandler);
  		addMouseMotionListener(myMouseHandler);
  		addKeyListener(this);
        setFocusable(true);
        
        setBackground(Color.BLACK);

        dynamicEntitiesList = new ArrayList<>();

        clickPosition = new Point(0,0);
        
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

  
        EntityStatic testEntity;
        
        Line2D[] triangleBounds = new Line2D[]{
			new Line2D.Double( -25 , -50 , 2000 , 500 ),
			new Line2D.Double( 2000 , 500 , -25 , 500 ),
			new Line2D.Double( -25 , 500 , -25 , -50 )
		};

		testEntity = EntityFactory.createEntityFromBoundary(300, 500, triangleBounds );
		testEntity.name = "Test Slope";
		currentScene.addEntity( testEntity );    
		//renderingEngine.addSpriteComposite( testEntity.getSpriteType() );
        
        
        testEntity = new EntityStatic("Test Ground1",50,500);     
        Collider collidable = new Collider( testEntity );
        testEntity.setCollisionProperties( collidable );
        collidable.setBoundary( new Boundary.Box(446,100,-223,-50 , collidable ) );

        Sprite graphic = new SpriteStillframe("ground_1.png" , -223 , -53 );
        EntityComposite.addGraphicTo(testEntity, graphic);
        //renderingEngine.addSpriteComposite( testEntity.getSpriteType() );
        currentScene.addEntity( testEntity );
        
        
        testEntity = new EntityStatic("Test Ground",700,500);     
        collidable = new Collider( testEntity );
        collidable.setBoundary( new Boundary.Box(446,100,-223,-50 , collidable ) );
        testEntity.setCollisionProperties( collidable );
        graphic = new SpriteStillframe("ground_1.png" , -223 , -53 );
        EntityComposite.addGraphicTo(testEntity, graphic);
        //renderingEngine.addSpriteComposite( testEntity.getSpriteType() );
        currentScene.addEntity( testEntity );
        
        //currentScene.addEntity(new EntityPhysics(120,260,"box.png"));
        //dynamicEntitiesList.add(new Bullet(100,100,1,1));
        
      	EntityRotationalDynamic rotationTest = new TestRotation(-400,400);

      	currentScene.addEntity( rotationTest );
      	
        //test for LaserTest entity
        //laser = new Tracer(143,260, physicsEntitiesList.get(0) , this ); //later will be parent system
        //dynamicEntitiesList.add(new LaserTest(400,60));  <-- can't add as long as I don't have a sprite for it
        //		--- for now will just draw in the drawObjects() method
        
        //############################################## TESTING BACKGROUND SPRITES #######################
        
        int offset_x = 400;
        int offset_y = 0;
        
        layer[6].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L7.png", 280-offset_x, 300-offset_y) );
        layer[5].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L6.png", 260-offset_x, -60-offset_y) );//bass
        layer[4].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L5.png", 550-offset_x, 400-offset_y) );
        layer[3].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L4.png", 300-offset_x, -320-offset_y) );//base
        layer[2].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L3.png", 260-offset_x, 600-offset_y) ); //forest
        layer[1].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L2.png", 200-offset_x, -160-offset_y) );//pipe
        layer[0].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L1.png", 300-offset_x, -160-offset_y) );//base
        
        
        //############################################### CAMERA #######################
    	//camera = new Camera(this,player,g2 );
        camera.setTarget( player );

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
    	background.drawBackground( camera );
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

        player.updatePosition();
        
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

    	//TO BE MOVED TO RENDERING ENGINE
    	for ( int i = 6 ; i > -1 ; i-- ) {	        	

    		layer[i].renderLayer(camera);
    	}
    	
    	// TESTING RENDERING ENGINE
    	
    	this.renderingEngine.draw(g2);
    	
    	
        
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
            		camera.lockAtPosition(Camera.ORIGIN);
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
		
			public void paintOverlay(Graphics2D g2, Camera cam){ // DEBUG GUI
		
		    	g2.setColor(new Color(0, 0, 0, 150));
		        g2.fillRect(0, 0, B_WIDTH, B_HEIGHT);
		        
		        g2.setColor(Color.GRAY);
			    g2.drawString("Entities: "+ currentScene.listEntities().length + " , Collidables:"+
			    		collisionEngine.debugNumberofCollidables() ,5,15);
			    g2.drawString("DX: "+player.getDX() + " DY: " + player.getDY(),5,30);
			    g2.drawString("AccX: " + player.getAccX() + "  AccY: " + player.getAccY(),5,45);
			    g2.drawString("Rotation: " + (int)player.getAngle() + " degrees " + player.getAngularVel() + " " + player.getAngularAcc(),5,60);
			    g2.drawString("State: "+ ((PlayerCharacter)player).printState() + "Colliding: " + player.isColliding(),5,75);
			    g2.drawString( ((PlayerCharacter)player).printBufferState() ,5,90 );
			        
			    //Draw player bounding box
		
			    g2.setColor(Color.CYAN);
			    
			    collisionEngine.debugPrintCollisionList(5, 105, g2);
			    
			    
			    
			    player.getColliderComposite().debugDrawBoundary(camera , g2);
			    
			    for (EntityStatic entity : currentScene.listEntities() ){
			    	
			    	entity.getColliderComposite().debugDrawBoundary(camera , g2);
			    	camera.drawCrossOnCamera( entity.getPos());
			    	
			    }
			    
			    for (EntityStatic entity : dynamicEntitiesList){
			    	
			    	entity.getColliderComposite().debugDrawBoundary(camera , g2);
			    	camera.drawCrossOnCamera( entity.getPos());
			    	
			    }

		    }
    
	}
    
    private void drawDebugSAT( EntityStatic playerRef , EntityStatic entitySecondary , Graphics2D g2 ){
  	
	    //EntityStatic stat = entitySecondary;
	   // EntityStatic stat = editorPanel.getCurrentSelectedEntity();
    	
    	EntityStatic stat = entitySecondary;
	    
	    //EntityStatic stat = staticEntitiesList.get(1);
	    //EntityStatic playerRef = this.playerRef;
	    
	    g2.setColor(Color.RED);
	    for ( int i = 0 ; i < ((EntityDynamic)playerRef).debugForceArrows().length ; i++ ){
	    	
	    	Vector force = ((EntityDynamic)playerRef).debugForceArrows()[i];
	    	Line2D forceArrow = new Line2D.Double( player.getPos() , new Point2D.Double(player.getX() + force.getX()*200 , player.getY() + force.getY()*200 ) );
	    	
	    	camera.draw( forceArrow );
	    
	    	g2.drawString( "Force "+i+" "+force.getX()+" , "+force.getY() ,800,50+i*10);
	    }
	    
	    g2.setColor(Color.BLUE);
	    
	    
	    drawCross( playerRef.getX() , playerRef.getY() , g2);
	    drawCross( stat.getX() , stat.getY() , g2);
	    
	    Boundary statBounds = stat.getColliderComposite().getBoundaryLocal() ;
	    Boundary playerBounds = playerRef.getColliderComposite().getBoundaryLocal();
	    
	    Point2D playerCenter = new Point2D.Double(playerRef.getX(), playerRef.getY());
	    Point2D statCenter = new Point2D.Double(stat.getX(), stat.getY());


	    //for ( Line2D axis : bounds.debugSeparatingAxes(B_WIDTH, B_HEIGHT) ){
	    for ( int i = 0 ; i < statBounds.getSpearatingSidesBetween(playerBounds).length ; i++ ){

	    	Line2D side = statBounds.getSpearatingSidesBetween(playerBounds)[i];

	    	Line2D axis2 = Boundary.debugGetSeparatingAxis(side, B_WIDTH, B_HEIGHT , new Point(20,20));
	    	
	    	Line2D axis = Boundary.getSeparatingAxis( side );

	    	g2.setColor(Color.DARK_GRAY);

	    	camera.drawInFrame(axis);




	    	//Line2D centerDistance = new Line2D.Float(playerRef.getX() , playerRef.getY(),
	    	//		stat.getX() , stat.getY());
	    	//Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);

	    	//g2.draw(centerProjection);

	    	

	    	Vertex[] statOuter= statBounds.getFarthestVertices(playerBounds,axis);
	    	Vertex[] playerOuter= playerBounds.getFarthestVertices(statBounds,axis);

	    	Vertex[] nearStatCorner = statBounds.farthestVerticesFromPoint( statOuter[0] , axis ); //merge below
	    	Vertex[] nearPlayerCorner = playerBounds.farthestVerticesFromPoint( playerOuter[0] , axis );
	    	
	    	Vertex farStatCorner = statBounds.farthestVerticesFromPoint(nearStatCorner[0] , axis)[0];
	    	Vertex farPlayerCorner = playerBounds.farthestVerticesFromPoint(nearPlayerCorner[0] , axis)[0];
	    	
	    	Point2D centerStat = statOuter[0].getCenter(nearStatCorner[0]);
	    	Point2D centerPlayer = playerOuter[0].getCenter(nearPlayerCorner[0]);

	    	Line2D centerDistance = new Line2D.Double( centerPlayer , centerStat );
	    	Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);
	    	
	    	g2.setColor(Color.GRAY);
	    	camera.drawInFrame(centerProjection);
	    	//CLOSEST SIDE TESTING
	    	g2.setColor(Color.YELLOW);
	    	//selected entity
	    	if ( nearStatCorner.length > 1 ){ 
	    		Side closest = nearStatCorner[0].getSharedSide(nearStatCorner[1]);
	    		camera.draw(closest.toLine());
	    		camera.drawString( closest.toString() , closest.getX1(), closest.getY1());
	    	}
	    	else 
	    		camera.drawCrossInWorld(nearStatCorner[0].toPoint());

	    	//make verticesFromPoint
	    	//playerRef
	    	if ( nearPlayerCorner.length > 1 ){
	    		Side closest = nearPlayerCorner[0].getSharedSide(nearPlayerCorner[1]);
	    		camera.draw(closest.toLine());
	    		camera.drawString( closest.toString() , closest.getX1(), closest.getY1());
	    	}
	    	else 
	    		camera.drawCrossInWorld( nearPlayerCorner[0].toPoint() );

	    	// -----------------

	    	Line2D playerHalf = new Line2D.Float( 
	    			playerBounds.getProjectionPoint(playerCenter,axis) ,
	    			playerBounds.getProjectionPoint(nearPlayerCorner[0].toPoint(),axis)
	    			);
	    	Line2D statHalf = new Line2D.Float( 
	    			statBounds.getProjectionPoint(centerStat,axis) ,
	    			statBounds.getProjectionPoint(nearStatCorner[0].toPoint(),axis) 
	    			);

	    	camera.drawInFrame(playerHalf);
	    	g2.setColor(Color.GREEN);
	    	camera.drawInFrame(statHalf );

	    	int centerDistanceX = (int)(centerProjection.getX1() -  centerProjection.getX2()  );
	    	int centerDistanceY = (int)(centerProjection.getY1() -  centerProjection.getY2()  );

	    	int playerProjectionX = (int)(playerHalf.getX1() -  playerHalf.getX2());
	    	int playerProjectionY = (int)(playerHalf.getY1() -  playerHalf.getY2());

	    	int statProjectionX = (int)(statHalf.getX2() -  statHalf.getX1());
	    	int statProjectionY = (int)(statHalf.getY2() -  statHalf.getY1());

	    	int penetrationX = 0;
	    	int penetrationY = 0;  
	    	

	    	if (centerDistanceX>0){
	    		//centerDistanceX -= 1;
	    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
	    	}
	    	else if (centerDistanceX<0){
	    		//centerDistanceX += 1;  //NEEDS HIGHER LEVEL SOLUTION
	    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
	    	}
	    	else
	    		penetrationX = Math.abs(playerProjectionX) + Math.abs(statProjectionX);

	    	if (centerDistanceY>0){
	    		//centerDistanceY -= 1;
	    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY ; 
	    	}
	    	else if (centerDistanceY<0){
	    		//centerDistanceY += 1; 
	    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY ; 
	    	}else
	    		penetrationY = Math.abs(playerProjectionY) + Math.abs(statProjectionY);


	    	
	    	if ( penetrationX * centerDistanceX < 0 ) //SIGNS ARE NOT THE SAME
				penetrationX = 0;
	    	if ( penetrationY * centerDistanceY < 0 )
				penetrationY = 0;



	    	g2.setFont( new Font( Font.DIALOG , Font.PLAIN , 10 ) );

	    	// g2.drawString("Center distance X: " + centerDistanceX + " Y: " + centerDistanceY ,
	    	//(int)centerProjection.getX1()+20 , (int)centerProjection.getY1()+20);
	    	/*
			   g2.drawString("Player projection X: " + playerProjectionX + " Y: " + playerProjectionY ,
		    			(int)centerProjection.getX1()+20 , (int)centerProjection.getY1()+35);

			   g2.drawString("Stat projection X: " + statProjectionX + " Y: " + statProjectionY ,
		    		(int)centerProjection.getX1()+20 , (int)centerProjection.getY1()+50);
	    	 */
	    	g2.drawString("Penetration X: " + penetrationX + " Y: " + penetrationY ,
	    			(int)playerHalf.getX1() , (int)playerHalf.getY1()+10);


	    	//g2.setColor(Color.DARK_GRAY);

	    	//g2.draw(new Line2D.Float(playerBounds.getProjectionPoint( nearPlayerCorner , axis ), nearPlayerCorner) );
	    	//g2.draw(new Line2D.Float(playerBounds.getProjectionPoint( playerCenter , axis ), playerCenter) );

	    }

	    g2.setColor(Color.YELLOW);
		g2.drawString( statBounds.getSides().length +" sides" ,30,45);
		//g2.drawString( ""+testingNearStatCorners[0].getEndingSide()+" "+testingNearStatCorners[0].getStartingSide() ,30,60);
	
    	
    }
    
    private void drawDebugCollisions(Graphics g){
    	
    	g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, B_WIDTH, B_HEIGHT);
    	
	    Graphics2D g2 = (Graphics2D) g;
    	

	    
	    drawDebugSAT( player , editorPanel.getSelectedEntities().get(0) , g2);

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
    
    public void addStaticEntity(EntityStatic entity){
    	this.currentScene.addEntity( entity );
    	this.collisionEngine.addStaticCollidable( entity.getColliderComposite() );
    	this.renderingEngine.addSpriteComposite(entity.getGraphicComposite());
    }
    
    @Override
    public Camera getCamera() { return this.camera; }
	
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
		renderingEngine.addSpriteComposite(player.getGraphicComposite() );
		
	}
	
	
}