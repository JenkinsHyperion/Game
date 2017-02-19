package engine;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.Timer;
import javax.swing.*;
import javax.swing.event.*;

import editing.EditorPanel;
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
public class Board extends BoardAbstract {
	
	private Background background = new Background("Prototypes/Sky.png"); //move to rendering later
	
	private java.util.Timer updateEntitiesTimer;
	
	private CollisionEngine collisionEngine = new CollisionEngine(this); //Refactor to a better name

    public Player player;
    public Tracer laser;
    protected ArrayList<EntityStatic> staticEntitiesList; 
    protected static ArrayList<EntityDynamic> dynamicEntitiesList; 
    protected static ArrayList<EntityPhysics> physicsEntitiesList; 
    
    //RENDERING
    public Camera camera;
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
    public static int B_WIDTH;// = 400;
    public static int B_HEIGHT;// = 300;
    private boolean debug1On = false; 
    private boolean debug2On = false; 

    public EntityStatic currentDebugEntity;
    
    private final int DELAY = 10;
    
    private int[] speedLogDraw = new int[300];
    private int[] speedLog = new int[300];
    private static int counter=0;
    

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
    	
    	B_WIDTH = width ;
    	B_HEIGHT = height ;
    	
    	initBoard();
    	initializeBoard();
    }
    //over loaded board constructor to accept SidePanel (editor) if editor is to be enabled

    private void initBoard() {
    	
        myMouseHandler = new MouseHandlerClass();
  		addMouseListener(myMouseHandler);
  		addMouseMotionListener(myMouseHandler);
  		addKeyListener(this);
        setFocusable(true);
        
        setBackground(Color.BLACK);
        
        staticEntitiesList = new ArrayList<>();
        dynamicEntitiesList = new ArrayList<>();
        physicsEntitiesList = new ArrayList<>();

        // initialize player
        player = new PlayerCharacter(ICRAFT_X, ICRAFT_Y,this);
        player.getEntitySprite().setVisible(true);

        
        clickPosition = new Point(0,0);

        
  
        EntityStatic testEntity;
        
        Line2D[] triangleBounds = new Line2D[]{
			new Line2D.Double( -25 , -50 , 500 , 500 ),
			new Line2D.Double( 500 , 500 , -25 , 500 ),
			new Line2D.Double( -25 , 500 , -25 , -50 )
		};

		testEntity = EntityFactory.createEntityFromBoundary(300, 500, triangleBounds );
		testEntity.loadSprite("bullet.png" , 0 , 0 );
		staticEntitiesList.add( testEntity );    
        
        
        
        testEntity = new EntityStatic("Test Ground1",50,500);     
        Collidable collidable = new Collidable( testEntity );
        testEntity.setCollisionProperties( collidable );
        collidable.setBoundary( new Boundary.Box(446,100,-223,-50 , collidable ) );

        testEntity.loadSprite("ground_1.png" , -223 , -53 );

        staticEntitiesList.add( testEntity );
        
        
        testEntity = new EntityStatic("Test Ground",700,500);     
        collidable = new Collidable( testEntity );
        collidable.setBoundary( new Boundary.Box(446,100,-223,-50 , collidable ) );
        testEntity.setCollisionProperties( collidable );
        testEntity.loadSprite("ground_1.png" , -223 , -53 );
        staticEntitiesList.add( testEntity );
        
      	physicsEntitiesList.add(new EntityPhysics(120,260,"box.png"));
        dynamicEntitiesList.add(new Bullet(100,100,1,1));
        
        //test for LaserTest entity
        laser = new Tracer(143,260, physicsEntitiesList.get(0) , this ); //later will be parent system
        //dynamicEntitiesList.add(new LaserTest(400,60));  <-- can't add as long as I don't have a sprite for it
        //		--- for now will just draw in the drawObjects() method
        
        //############################################## TESTING BACKGROUND SPRITES #######################
        
        int offset_x = 500;
        int offset_y = 100;
        
        layer[6].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L7.png", 350-offset_x, 600-offset_y) );
        layer[5].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L6.png", 200-offset_x, -200-offset_y) );//bass
        layer[4].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L5.png", 590-offset_x, 700-offset_y) );
        layer[3].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L4.png", 220-offset_x, -200-offset_y) );//base
        layer[2].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L3.png", 200-offset_x, 820-offset_y) );
        layer[1].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L2.png", 250-offset_x, -200-offset_y) );
        layer[0].addEntity( EntityFactory.createBackgroundSprite("Prototypes/L1.png", 300-offset_x, -200-offset_y) );//base
        
        
        //############################################### CAMERA #######################
    	camera = new Camera(this,player);

        initBullets();
    
        //ADD COLLIDABLES TO COLLISION ENGINE
        
        for ( EntityStatic stat : staticEntitiesList ){
        	collisionEngine.addStaticCollidable( (Collidable) stat.getCollisionType() );
        }
        
        	collisionEngine.addDynamicCollidable( (Collidable)player.getCollisionType() );
        
    } 
    //END INITIALIZE #############################################################################
    
    @Override
    protected void graphicsThreadPaint(Graphics g) {
    	background.drawBackground( g , camera );
 		//System.out.println("Graphics running");
        
        drawObjects(g);
        
        drawGhostSprite(g, editorPanel.getGhostSprite(), editorPanel.getEditorMousePos());
        if (editorPanel.mode == EditorPanel.WORLDGEOM_MODE) {
        	editorPanel.getWorldGeom().render(g);
        	
        }
    }
    
   
    
    
    @Override
    protected void entityThreadRun() {
    	updateEntities();
    	collisionEngine.checkCollisions();
    }
     
    private void updateEntities(){ //RUN POSITION AND DRAW UPDATES
    	
		          updatePlayer();    
		          updateDynamicEntities();
		          updatePhysicsEntities();
		          
			      laser.updatePosition();	
			      
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
    
    private void updatePhysicsEntities() {
    	//for (EntityDynamic dynamicEntity : dynamicObjects) {     	
    	for (int i = 0 ; i < physicsEntitiesList.size() ; i++){
    		EntityDynamic physicsEntity = physicsEntitiesList.get(i);
    		physicsEntity.updatePosition();
    		physicsEntity.getEntitySprite().updateSprite();
        }
    	
    }

    public void initBullets() {

        for (int[] p : pos) {
            dynamicEntitiesList.add(new Bullet(p[0], p[1],-1,1)); 
        }
        
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
    public void drawObjects(Graphics g) {
    	
    	//Draw all static entities from list (ex. platforms)
    	
    	//Draw ghostSprite for editor using null-object pattern
    	//
    	for ( int i = 6 ; i > -1 ; i-- ) {	        	
    		layer[i].drawLayer(g, camera);
    	}
    	

	    for (EntityStatic stat : staticEntitiesList) {
	        	//g.drawImage(stat.getEntitySprite().getImage(), 
	        	//		stat.getSpriteOffsetX() + stat.getX(), 
	        	//		stat.getSpriteOffsetY() + stat.getY(), this);	        	
	        	stat.getEntitySprite().drawSprite(g,camera);
	        	editorPanel.drawEditorSelectedRectangle(stat, g);
	    }
        //Draw all dynamic (moving) entities from list (ex. bullets)
        for (EntityDynamic dynamic : dynamicEntitiesList) {
        	
                //g.drawImage(dynamic.getEntitySprite().getImage(), dynamic.getX(), dynamic.getY(), this);
        	dynamic.getEntitySprite().drawSprite(g,camera);
                
        }
        
        //Draw physics entities
        for (EntityDynamic physics : physicsEntitiesList) {
        	
                //g.drawImage(physics.getEntitySprite().getImage(), physics.getX(), physics.getY(), this);
        	physics.getEntitySprite().drawSprite(g,camera);
        }

		//Draw player
        /*if (player.getEntitySprite().isVisible()) {
            ((Graphics2D) g).drawImage(player.getEntitySprite().getImage(), 
            		player.getX() - player.getEntitySprite().getOffsetX() , 
            		player.getY() - player.getEntitySprite().getOffsetY(), this);
        }*/
        player.getEntitySprite().drawSprite(g,camera);

        
        //laser.setxEndPoint(B_WIDTH);
       // laser.setyEndPoint(physicsEntitiesList.get(0).getY()+10);
        laser.pewpew(g);
                
        if (debug1On){ drawDebugBoundaries(g); }
        if (debug2On){ drawDebugCollisions(g); }

    }

    
   
    public void drawGhostSprite(Graphics g, Sprite ghost, Point mousePos) {
    	ghost.editorDraw(g, mousePos);
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
  			player.inputController.mouseReleased(e);
  			editorPanel.mouseReleased(e);
  			editorPanel.getWorldGeom().mouseReleased(e);
  		}
  			
  	}
  	
  	//protected class KeyHandlerClass implements KeyListener  { 	
  	
  		@Override
        public void keyReleased(KeyEvent e) {
            player.inputController.keyReleased(e);
            editorPanel.keyReleased(e);
            editorPanel.getWorldGeom().keyReleased(e);           
        }
  		@Override
        public void keyPressed(KeyEvent e) {
            player.inputController.keyPressed(e);
            editorPanel.keyPressed(e);
            editorPanel.getWorldGeom().keyPressed(e);
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_F1) {
            	if (camera.isLocked())
            		camera.unlock();
            	else
            		camera.lockAtPosition(Camera.ORIGIN);
            }  
            
            else if (key == KeyEvent.VK_F2) {
            	if (debug1On){
            		debug1On = false;
            	} else {
            		debug1On = true;
            	}
            }  

            else if (key == KeyEvent.VK_F3) {
            	if (debug2On){
            		debug2On = false;
            	} else {
            		debug2On = true;
            	}
            }
            else if (key == KeyEvent.VK_ESCAPE) {
            	//editorPanel.entityPlacementMode = false;
            	editorPanel.mode = EditorPanel.DEFAULT_MODE;
            	editorPanel.nullifyGhostSprite();
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
    
    private void drawDebugBoundaries(Graphics g){ // DEBUG GUI

    	g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, B_WIDTH, B_HEIGHT);
        
        g.setColor(Color.GRAY);
	    g.drawString(player.name,5,15);
	    g.drawString("DX: "+player.getDX() + " DY: " + player.getDY(),5,30);
	    g.drawString("AccX: " + player.getAccX() + "  AccY: " + player.getAccY(),5,45);
	    g.drawString("Rotation: " + (int)player.getAngle() + " degrees " + player.getAngularVel() + " " + player.getAngularAcc(),5,60);
	    g.drawString("State: "+ ((PlayerCharacter)player).printState() + "Colliding: " + player.isColliding(),5,75);
	    g.drawString( ((PlayerCharacter)player).printBufferState() ,5,90 );
	        
	    //Draw player bounding box
	    Graphics2D g2 = (Graphics2D) g;

	    g2.setColor(Color.CYAN);
	    
	    collisionEngine.debugPrintCollisionList(5, 105, g);
	    
	    
	    
	    player.getCollisionType().debugDrawBoundary(camera , g2);
	    
	    for (EntityStatic entity : staticEntitiesList){
	    	
	    	entity.getCollisionType().debugDrawBoundary(camera , g2);
	    	
	    }
	    
	    for (EntityStatic entity : physicsEntitiesList){
	    	
	    	entity.getCollisionType().debugDrawBoundary(camera , g2);
	    	
	    }
	    
	    //DIAGNOSTIC GRAPH
	    for (int i = 0 ; i < 320 ; i += 20){
	    	g2.drawLine(45, 600-i , 1280, 600-i); //make better overlay class
	    	g2.drawString( i/20 + " ms" , 10,603-i);
    	}	
    
	    for (int i = 0 ; i < speedLog.length ; i++){
	    	
	    		int speed = speedLogDraw[i]/50; //1ms = 20px
	    		g2.setColor(Color.MAGENTA);
	    		g2.drawLine(3*i + 50 , 600 , 3*i + 50 , 600 - speed );
	    		g2.setColor(Color.CYAN);
	    		g2.drawLine(3*i + 50 , 600 - speed , 3*i + 50 , 600 - speed-(speedLog[i]/50) );
	    }
	    

    }
    
    private void drawDebugSAT( EntityStatic playerRef , EntityStatic entitySecondary , Graphics2D g2 ){
  	
	    //EntityStatic stat = entitySecondary;
	    EntityStatic stat = editorPanel.getCurrentSelectedEntity();
	    
	    //EntityStatic stat = staticEntitiesList.get(1);
	    //EntityStatic playerRef = this.playerRef;
	    
	    g2.setColor(Color.RED);
	    for ( int i = 0 ; i < ((EntityDynamic)playerRef).debugForceArrows().length ; i++ ){
	    	
	    	Vector force = ((EntityDynamic)playerRef).debugForceArrows()[i];
	    	Line2D forceArrow = new Line2D.Double( player.getPos() , new Point2D.Double(player.getX() + force.getX()*200 , player.getY() + force.getY()*200 ) );
	    	
	    	camera.draw( forceArrow , g2);
	    
	    	g2.drawString( "Force "+i+" "+force.getX()+" , "+force.getY() ,800,50+i*10);
	    }
	    
	    g2.setColor(Color.BLUE);
	    
	    
	    drawCross( playerRef.getX() , playerRef.getY() , g2);
	    drawCross( stat.getX() , stat.getY() , g2);
	    
	    Boundary statBounds = stat.getCollisionType().getBoundaryLocal() ;
	    Boundary playerBounds = ((Collidable) playerRef.getCollisionType()).getBoundaryLocal();
	    
	    Point2D playerCenter = new Point2D.Double(playerRef.getX(), playerRef.getY());
	    Point2D statCenter = new Point2D.Double(stat.getX(), stat.getY());


	    //for ( Line2D axis : bounds.debugSeparatingAxes(B_WIDTH, B_HEIGHT) ){
	    for ( int i = 0 ; i < statBounds.getSpearatingSidesBetween(playerBounds).length ; i++ ){

	    	Line2D side = statBounds.getSpearatingSidesBetween(playerBounds)[i];

	    	Line2D axis = statBounds.debugGetSeparatingAxis(side, B_WIDTH, B_HEIGHT);

	    	g2.setColor(Color.DARK_GRAY);

	    	g2.draw(axis);




	    	//Line2D centerDistance = new Line2D.Float(playerRef.getX() , playerRef.getY(),
	    	//		stat.getX() , stat.getY());
	    	//Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);

	    	//g2.draw(centerProjection);

	    	

	    	Point2D[] statOuter= statBounds.getFarthestPoints(playerBounds,axis);
	    	Point2D[] playerOuter= playerBounds.getFarthestPoints(statBounds,axis);

	    	Vertex[] nearStatCorner = statBounds.farthestVerticesFromPoint( statOuter[0] , axis ); //merge below
	    	Vertex[] nearPlayerCorner = playerBounds.farthestVerticesFromPoint( playerOuter[0] , axis );
	    	
	    	Vertex farStatCorner = statBounds.farthestVerticesFromPoint(nearStatCorner[0].toPoint(), axis)[0];
	    	Vertex farPlayerCorner = playerBounds.farthestVerticesFromPoint(nearPlayerCorner[0].toPoint(), axis)[0];
	    	
	    	Point2D centerStat = farStatCorner.getCenter(nearStatCorner[0]);
	    	Point2D centerPlayer = farPlayerCorner.getCenter(nearPlayerCorner[0]);

	    	Line2D centerDistance = new Line2D.Double( centerPlayer , centerStat );
	    	Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);
	    	
	    	g2.setColor(Color.GRAY);
	    	g2.draw(centerProjection);
	    	//CLOSEST SIDE TESTING
	    	g2.setColor(Color.YELLOW);
	    	//selected entity
	    	if ( nearStatCorner.length > 1 ){ 
	    		Side closest = nearStatCorner[0].getSharedSide(nearStatCorner[1]);
	    		camera.draw(closest.toLine(), g2);
	    		camera.drawString( closest.toString() , closest.getX1(), closest.getY1(), g2);
	    	}
	    	else 
	    		drawCross(nearStatCorner[0], g2);

	    	//make verticesFromPoint
	    	//playerRef
	    	if ( nearPlayerCorner.length > 1 ){
	    		Side closest = nearPlayerCorner[0].getSharedSide(nearPlayerCorner[1]);
	    		camera.draw(closest.toLine(), g2);
	    		camera.drawString( closest.toString() , closest.getX1(), closest.getY1(), g2);
	    	}
	    	else 
	    		drawCross(nearPlayerCorner[0], g2);

	    	// -----------------

	    	Line2D playerHalf = new Line2D.Float( 
	    			playerBounds.getProjectionPoint(playerCenter,axis) ,
	    			playerBounds.getProjectionPoint(nearPlayerCorner[0].toPoint(),axis)
	    			);
	    	Line2D statHalf = new Line2D.Float( 
	    			statBounds.getProjectionPoint(centerStat,axis) ,
	    			statBounds.getProjectionPoint(nearStatCorner[0].toPoint(),axis) 
	    			);

	    	g2.draw(playerHalf);
	    	g2.setColor(Color.GREEN);
	    	g2.draw(statHalf );

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

	    	if (centerDistanceY>0){
	    		//centerDistanceY -= 1;
	    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY ; 
	    	}
	    	else if (centerDistanceY<0){
	    		//centerDistanceY += 1; 
	    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY ; 
	    	}


	    	
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
    	

	    
	    drawDebugSAT( player , staticEntitiesList.get(0) , g2);

    }
    
    private void drawCross(int x, int y , Graphics g){
    	g.drawLine( x-3, y-3, x+3, y+3 );
		g.drawLine( x-3, y+3, x+3, y-3 );
    }
    
    private void drawCross(Point2D point , Graphics g){
    	camera.draw( new Line2D.Float( (int)point.getX()-3, (int)point.getY()-3, (int)point.getX()+3, (int)point.getY()+3 ) , g);
    	camera.draw( new Line2D.Float( (int)point.getX()-3, (int)point.getY()+3, (int)point.getX()+3, (int)point.getY()-3) , g);
    }
    
    private void drawCross(Vertex vertex , Graphics g){
    	camera.draw( new Line2D.Float( (int)vertex.getX()-3, (int)vertex.getY()-3, (int)vertex.getX()+3, (int)vertex.getY()+3 ) , g);
		camera.draw( new Line2D.Float( (int)vertex.getX()-3, (int)vertex.getY()+3, (int)vertex.getX()+3, (int)vertex.getY()-3 ) , g);
    }

    
/* ########################################################################################################################
 * 
 * 		GET AND SET METHODS AND OTHER MISC STUFF
 * 
 * ########################################################################################################################
 */
    @Override
    public Camera getCamera() { return this.camera; }
	
	public int getboundaryX(){ return B_WIDTH ;}
	public int getboundaryY(){ return B_HEIGHT ;}
	
	public Player getPlayer(){ return player; }
	
	public ArrayList<EntityStatic> getStaticEntities(){ return staticEntitiesList; }
	public ArrayList<EntityDynamic> getDynamicEntities(){ return dynamicEntitiesList; }
	public ArrayList<EntityPhysics> getPhysicsEntities(){ return physicsEntitiesList; }

	public void transferEditorPanel(EditorPanel instance){
		this.editorPanel = instance; 
	}
    
}