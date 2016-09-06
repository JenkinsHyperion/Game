package engine;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import entities.*;
import physics.*;
import testEntities.*;
import misc.*;



@SuppressWarnings("serial")
public class Board extends JPanel implements Runnable {

	private double currentDuration;
	
	private Timer timer;
	
	private java.util.Timer updateEntitiesTimer;
	private java.util.Timer repaintTimer;
	
	private CollisionEngine Collisions = new CollisionEngine(this); //Refactor to a better name
	
    private Player player;
    private  PaintOverlay p;
    private LaserTest laser;
    protected ArrayList<EntityStatic> staticEntitiesList; 
    protected static ArrayList<EntityDynamic> dynamicEntitiesList; 
    protected static ArrayList<EntityDynamic> physicsEntitiesList; 
    
    protected Point clickPosition;
    protected boolean mouseClick = false;
    protected Rectangle selectedBox;
    
    protected MouseHandlerClass handler;
    private boolean ingame = true;
    private final int ICRAFT_X = 170;
    private final int ICRAFT_Y = 100;
    public static final int B_WIDTH = 400;
    public static final int B_HEIGHT = 300;
    private boolean debug1On = false; 
    private boolean debug2On = false; 
    protected EntityStatic currentSelectedEntity;
    
    private final int DELAY = 10;
    
    private double time = 0;
    private double deltaTime = 0;
    
	//private Thread collisionThread;
	//private Thread paintThread;

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

    public Board() {
    	initBoard();
    }

    private void initBoard() {
    	currentSelectedEntity = new EntityStatic(0,0);
    	//or currentSelectedEntity = new Object();
    	currentDuration = System.currentTimeMillis();
        addKeyListener(new TAdapter());
        handler = new MouseHandlerClass();
  		addMouseListener(handler);
  		addMouseMotionListener(handler);
        setFocusable(true);
        setBackground(Color.BLACK);
        ingame = true;

//        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
//        setMinimumSize(new Dimension(B_WIDTH, B_HEIGHT));
        // setSize(300,400);
        // initialize object lists
        staticEntitiesList = new ArrayList<>();
        dynamicEntitiesList = new ArrayList<>();
        physicsEntitiesList = new ArrayList<>();

        // initialize player
        player = new PlayerShape(ICRAFT_X, ICRAFT_Y);
        player.getObjectGraphic().setVisible(true);

        selectedBox = new Rectangle();
        clickPosition = new Point(0,0);
        //## TESTING ##
        //Manually add test objects here

        staticEntitiesList.add(new Platform(210,180,"platform02"));
        staticEntitiesList.add(new Platform(170,180,"platform"));
        staticEntitiesList.add(new Platform(250,240,"platform02"));
        staticEntitiesList.add(new Platform(210,240,"platform02"));
        staticEntitiesList.add(new Platform(50,180,"platform02"));
        staticEntitiesList.add(new Platform(60,270,"platform02"));
        staticEntitiesList.add(new StaticSprite(150,274, "grass01"));
        staticEntitiesList.add(new Ground(250,295,"ground01"));
        staticEntitiesList.add(new Slope(40,160));
        
      	physicsEntitiesList.add(new EntityPhysics(120,260,"box"));
        dynamicEntitiesList.add(new Bullet(100,100,1,1));
        
        //test for LaserTest entity
        laser = new LaserTest(143,260, B_WIDTH, 260);
        //dynamicEntitiesList.add(new LaserTest(400,60));  <-- can't add as long as I don't have a sprite for it
        //		--- for now will just draw in the drawObjects() method
        
        p = new PaintOverlay(200,0,150,60);
        initBullets();

        
        //THREAD AREA
        //If what I read was correct, timers all have their own threads.
        //The "tasks" below will fire at each respective timer's "scheduleAtFixedRate", putting them
        //each on their own threads. There is a thread for updating entity positions, updating collisions, and the rendering.
        updateEntitiesTimer = new java.util.Timer(); //create timer
        repaintTimer = new java.util.Timer();
        
        
        TimerTask updateEntitiesTask = new TimerTask() {
        	@Override
        	public void run(){
        		Collisions.checkCollisions();
        		updateEntities();

        	}
        };
        
        //will have to experiment how often collisions should be checked.
        //I get strange anomolies when setting the update rate (below in "scheduleAtFixedRate(collisionUpdateTask)" too
        // low or too high. We might want to try implementing something that puts the thread to sleep
        //	 when it can guarantee there are no collisions happening whatsoever. Which wouldn't be often anyway I guess.
        /*TimerTask collisionUpdateTask = new TimerTask() {

        	@Override
        	public void run() {

        		//RUN COLLISION DETECTION
        		checkCollisions();
        	}
        };*/
        TimerTask repaintUpdateTask = new TimerTask() {
        	@Override
        	public void run() {
        		repaint();
        	}
        };
        
        updateEntitiesTimer.scheduleAtFixedRate( updateEntitiesTask , 0 , 16); // fire task every 16 ms
        //collisionTimer.scheduleAtFixedRate( collisionUpdateTask , 0 , 5);
        repaintTimer.scheduleAtFixedRate( repaintUpdateTask, 0, 16);
        

        //collisionThread.start();
        //updateBoard();
    }
    
    @Override	
    public void paintComponent(Graphics g) {  
        super.paintComponent(g);
            drawObjects(g);
    }
    
    /* ##################
     * ## UPDATE BOARD ##    (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     * ##################
     */
    

      public void updateEntities(){ //TESTING CONSTANT FPS
    	      
          deltaTime = System.currentTimeMillis() - time ;
    	  
	          //if (deltaTime > 15) {
	      
          
		          //RUN POSITION AND DRAW UPDATES
		          updatePlayer();    
		          updateDynamicEntities();
		          updatePhysicsEntities();
		          
			      laser.updatePosition();		      
		          
		          time = System.currentTimeMillis();
		          
	          //}
		          
		         //Toolkit.getDefaultToolkit().sync(); // what does this even do
          
      }

    //spawn bullets (TESTING)
    public void initBullets() {

        for (int[] p : pos) {
            dynamicEntitiesList.add(new Bullet(p[0], p[1],-1,1)); 
        }
        
    }
    

    
    
    /* ####################
     * Nested Static class allowing Board objects access to the Board.
     * This is bad form, instead pass board instance through constructors of objects that need access to board 
     *###################*/
    public static class BoardAccess{   	
    	
    	//spawn new entities and add to Board
        public static void spawnDynamicEntity(EntityDynamic spawn) {
        	
        	dynamicEntitiesList.add(spawn);       	
        }
        
        //check board dimensions
        public static int getBoardHeight(){
        	return B_HEIGHT;
        }
        
        public static int getBoardWidth(){
        	return B_WIDTH;
        }
        
        
        //PLACE STATIC METHODS HERE 
    }
    

/* ########################################################################################################################
 * 
 * 		RENDERING
 * 
 * ########################################################################################################################
 */
    public void drawObjects(Graphics g) {
    	
    	//Draw all static entities from list (ex. platforms)
        for (EntityStatic stat : staticEntitiesList) {
            if (stat.getObjectGraphic().isVisible()) {
                g.drawImage(stat.getObjectGraphic().getImage(), 
                		stat.getObjectGraphic().getOffsetX() + stat.getX(), 
                		stat.getObjectGraphic().getOffsetY() + stat.getY(), this);
               //METHOD FOR DRAWING RECTANGLE
                // 	>it will take EntityStatic, Graphics, width and height as parameters
                //	>checks if the entity isSelected:
                //  >>> draws with given width and height, of if not selected doesn't draw
                //scratch pad:
                drawEditorSelectedRectangle(stat, g, selectedBox);
            }
        }
        
        //Draw all dynamic (moving) entities from list (ex. bullets)
        for (EntityDynamic dynamic : dynamicEntitiesList) {
            if (dynamic.getObjectGraphic().isVisible()) {
                g.drawImage(dynamic.getObjectGraphic().getImage(), dynamic.getX(), dynamic.getY(), this);
            }
        }
        
        //Draw physics entities
        for (EntityDynamic physics : physicsEntitiesList) {
            if (physics.getObjectGraphic().isVisible()) {
                g.drawImage(physics.getObjectGraphic().getImage(), physics.getX(), physics.getY(), this);
 
                /*
                laser.setX(physics.getX()+20);
                laser.setY(physics.getY()+20);
                laser.setxEndPoint(B_WIDTH);
                laser.setyEndPoint(physics.getY()+20);
                laser.pewpew(g);
                //code shouldn't be inside this for loop. Moved it down
                */     
             //Try messing around with this code here, it just occured to me we can use this line drawing functionality
             //to use for wire-frame type debugging
             
            }
        }

		//Draw player
        if (player.getObjectGraphic().isVisible()) {
            ((Graphics2D) g).drawImage(player.getObjectGraphic().getImage(), 
            		player.getX() + player.getObjectGraphic().getOffsetX(), 
            		player.getY() + player.getObjectGraphic().getOffsetY(), this);
        }

    //DRAW GUI - might be extended into debug console

 //**      //DRAW LASER  
        
        //calling upon physicsEntitiesList.get(0) is awkward, perhaps good reason to try out HashMaps
        laser.setX(physicsEntitiesList.get(0).getX()+25);
        laser.setY(physicsEntitiesList.get(0).getY()+10);
        
        laser.setxEndPoint((int)player.getBoundingBox().getCenterX());
        laser.setyEndPoint((int)player.getBoundingBox().getCenterY());
        
        /*
         * #####  For having laser track the mouse

        laser.setxEndPoint((int)clickPosition.getX() );
        laser.setyEndPoint((int)clickPosition.getY() );
        */
        
        //laser.setxEndPoint(B_WIDTH);
       // laser.setyEndPoint(physicsEntitiesList.get(0).getY()+10);
        laser.pewpew(g);
                
        if (debug1On){ drawDebugBoundaries(g); }
        if (debug2On){ drawDebugCollisions(g); }

    }

    
    public void drawEditorSelectedRectangle(EntityStatic stat, Graphics g, Rectangle r) {
    	
    	if (stat.isSelected == true) {
    		//int width = stat.getObjectGraphic().getImage().getWidth(null);
        	//int height = stat.getObjectGraphic().getImage().getHeight(null);
        	g.setColor(Color.BLUE);
    		g.drawRect(stat.getX(), stat.getY(), r.width, r.height);
    	}
    }
    
    
    // Update position and Graphic of dynamic objects
    private void updateDynamicEntities() {
    	//for (EntityDynamic dynamicEntity : dynamicObjects) {     	
    	for (int i = 0 ; i < dynamicEntitiesList.size() ; i++){
    		EntityDynamic dynamicEntity = dynamicEntitiesList.get(i);
    		dynamicEntity.updatePosition();
    		dynamicEntity.getObjectGraphic().updateSprite();
    		
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
    } 
    
    // Update position and Graphic of Player
    private void updatePlayer() { 

        if (player.getObjectGraphic().isVisible()) { //obsolete

        	player.updatePosition();

        }
        
		player.getObjectGraphic().getAnimatedSprite().update();
    }
    
    private void updatePhysicsEntities() {
    	//for (EntityDynamic dynamicEntity : dynamicObjects) {     	
    	for (int i = 0 ; i < physicsEntitiesList.size() ; i++){
    		EntityDynamic physicsEntity = physicsEntitiesList.get(i);
    		physicsEntity.updatePosition();
    		physicsEntity.getObjectGraphic().updateSprite();
        }
    	
    }


    
  //MOUSE INPUT
  	protected class MouseHandlerClass extends MouseInputAdapter  { 		
  	    public int clickPositionXOffset;
  	    public int clickPositionYOffset;
  		//p1 is your clicked position, p2 is the entity's original position that
  		//	will be used for calculating the offsets
  		public int entityOriginalXPosition;
		public int entityOriginalYPosition;

  		@Override
  		public void mousePressed(MouseEvent e)
  		{  	
  		//code for getting the entity's sprite's actual area: 
  		// currentSelectedEntity.getObjectGraphic().getImage().getWidth(null);"
  			
  			if (!mouseClick) {
  				deselectAllEntities();
  				mouseClick = true;
	  			clickPosition.setLocation(e.getX(),e.getY());
	  			SplitPane.getSidePanel().setEntityCoordsLabel(String.format("Mouse Click: %s, %s", e.getX(), e.getY()));
	  			  			
	  			checkForSelection(clickPosition);  			
	  		
	  			if (currentSelectedEntity != null) {  	// there is entity under cursor
	  				if(currentSelectedEntity.isSelected != true) {
	  					currentSelectedEntity.isSelected = true;
	  				}
	  				else{
	  					currentSelectedEntity.isSelected = false;
	  				}

	  				selectedBox.setSize(currentSelectedEntity.getObjectGraphic().getImage().getWidth(null),
	  									currentSelectedEntity.getObjectGraphic().getImage().getHeight(null) );
	  				
		  			System.out.println(currentSelectedEntity.name);
	  	  			//SidePanel.setSelectedEntityName("Selected: " + currentSelectedEntity.name);
		  			SplitPane.getSidePanel().setSelectedEntityName("Selected: " + currentSelectedEntity.name);
		  			SplitPane.getSidePanel().setEntityCoordsLabel("Coords. of selected entity: " + currentSelectedEntity.getX() + ", " + currentSelectedEntity.getY());
		  			//get offsets
		  			clickPositionXOffset = e.getX() - currentSelectedEntity.getX() ;
		  			clickPositionYOffset = e.getY() - currentSelectedEntity.getY() ;
		  			System.out.println("Offset "+ clickPositionXOffset + "," + clickPositionYOffset);
	  			}
	  			else { //doesnt need to be redrawn every time
	  				
	  				SplitPane.getSidePanel().setSelectedEntityName("Nothing Selected");
		  			SplitPane.getSidePanel().setEntityCoordsLabel("Coords. of selected entity: N/A");
	  			}
	
  			}
  		}

  		@Override
  		public void mouseDragged(MouseEvent e) 
  		{ 		
  			SplitPane.getSidePanel().setMousePosLabel(String.format("Mouse Click: %s, %s", e.getX(), e.getY()));

  			if (currentSelectedEntity != null) {

  				//System.out.println("Dragging " + currentSelectedEntity.name);

  				currentSelectedEntity.setX(e.getX() - clickPositionXOffset);
  				currentSelectedEntity.setY(e.getY() - clickPositionYOffset);
  				SplitPane.getSidePanel().setEntityCoordsLabel("Coords. of selected entity: " + currentSelectedEntity.getX() + ", " + currentSelectedEntity.getY());
  			}

  		}
  		@Override
  		public void mouseReleased(MouseEvent e) 
  		{	
  			if (currentSelectedEntity != null)
  				currentSelectedEntity.getObjectGraphic().setVisible(true);
  			if (clickedOnEntity(e.getPoint()) == null) {
  				deselectAllEntities();
  			}

  			System.out.println("Released");
  			mouseClick = false;
  		}
  	}
  	
  	//LEVEL EDITOR METHODS
  	
  	private void checkForSelection(Point click) { //redundant
  			currentSelectedEntity = clickedOnEntity(click);
  			
  	}
  	
  	private EntityStatic clickedOnEntity(Point click) {
  		
  		for (EntityStatic entity : staticEntitiesList) {
  			if (entity.getBoundingBox().contains(click)) 
  			{
  	  			SplitPane.getSidePanel().setSelectedEntityName("Selected: " + entity.name);
  	  			SplitPane.getSidePanel().setEntityCoordsLabel("Coords. of selected entity: " + entity.getX() + ", " + entity.getY());
  	  			//currentSelectedEntity.isSelected = true;
  				return entity;
  			}
  		}
  		return null;
  	}
  	
  	protected void deselectAllEntities() {
  		SplitPane.getSidePanel().enableEditPropertiesButton(false);
  		for (EntityStatic entity : staticEntitiesList) {
  			if (entity.isSelected == true)
  				entity.isSelected = false;
  		}
  	}
  	
  	
  	//Inner class to handle F2 keypress for debug window
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
            
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_F2) {
            	if (debug1On){
            		debug1On = false;
            	} else {
            		debug1On = true;
            	}
            }  

            if (key == KeyEvent.VK_F3) {
            	if (debug2On){
            		debug2On = false;
            	} else {
            		debug2On = true;
            	}
            } 
            
        }
    }
    
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
	    g.drawString("FPS: " + Math.round(1000/deltaTime),5,15);
	    g.drawString("DX: "+player.getDX() + " DY: " + player.getDY(),5,30);
	    g.drawString("AccX: " + player.getAccX() + "  AccY: " + player.getAccY(),5,45);
	    g.drawString("Rotation: " + player.getAngle()*5 + " degrees",5,60);
	    g.drawString("Colliding: " + player.isColliding(),5,75);
	    
	    //Draw player bounding box
	    Graphics2D g2 = (Graphics2D) g;
	        
	    g2.setColor(Color.CYAN);
	    for (Line2D line : player.getLocalBoundary().getSides()){
	    	g2.draw(line);
	    } 
	    
	    //g2.setColor(Color.DARK_GRAY);
	    //g2.draw(player.getLocalBoundary().getTestSide(3) );
	    //g2.setColor(Color.CYAN);
	    
	    for ( Line2D axis : staticEntitiesList.get(2).getLocalBoundary().getSeparatingSides() ){
	    	g2.draw(axis);
	    }
	    
	    
	    g2.draw(laser.getBoundary().getSides()[0]);
	    
	    for ( EntityStatic stat : staticEntitiesList) {	    	
	    	for (Line2D line : stat.getLocalBoundary().getSides()){
		    	g2.draw(line);
		    }	
	    }
	    
	    for ( EntityStatic dynamic : dynamicEntitiesList) {	    	
	    	for (Line2D line : dynamic.getLocalBoundary().getSides()){
		    	g2.draw(line);
		    }	
	    }
	    
	    for ( EntityStatic physics : physicsEntitiesList) {	    	
	    	for (Line2D line : physics.getLocalBoundary().getSides()){
		    	g2.draw(line);
		    }	
	    }
	    
	    
	    
	    //DEBUG - DISPLAY LIST OF COLLISIONS
	    g.drawString("Collisions: ",5,90);
	    if (!Collisions.list().isEmpty())
	    {
	    	g2.setColor(Color.YELLOW);
		    g.drawString("Collisions: ",5,90);
		   
		    for (int i = 0 ; i < Collisions.list().size() ; i++){
		    	//draw list of collisions
		    	g.drawString(""+Collisions.list().get(i) + " " + 
		    			Collisions.list().get(i).isContacting() + ": " +
		    	(int)Collisions.list().get(i).getContactDist(),5,105+(10*i));
		    	
		    	//draw colliding sides
		    	if ( Collisions.list().get(i).getSidePrimary() != null ) {
		    		
			    	g2.draw(Collisions.list().get(i).getSidePrimary() );
			    	g2.draw(Collisions.list().get(i).getSideSecondary() );
		    	}
	
		    	
		    	/*if ( collisionsList.get(i).getContactPoints()[1] != null ) {
		    		g2.setColor(Color.RED);	    		
		    		g2.drawLine( 
		    				(int) collisionsList.get(i).getContactPoints()[0].getX(), 
		    				(int) collisionsList.get(i).getContactPoints()[0].getY(), 
		    				(int) collisionsList.get(i).getContactPoints()[1].getX(), 
		    				(int) collisionsList.get(i).getContactPoints()[1].getY() 
		    				);
		    		g2.setColor(Color.YELLOW);
		    	}*/
		    	else {
		    		g.drawString("Depth "+Collisions.list().get(i).getDepth().getX()
		    				+ " " + Collisions.list().get(i).getDepth().getY(),300,105+(10*i));
		    	}
		    	
		    	//draw intersection points
		    	/*Iterator<Point2D> it = collisionsList.get(i).getIntersections().iterator();
		    	do { 
		    		Point2D point = it.next();
		    		g.drawLine((int)point.getX()-3, (int)point.getY()-3, (int)point.getX()+3, (int)point.getY()+3);
		    		g.drawLine((int)point.getX()-3, (int)point.getY()+3, (int)point.getX()+3, (int)point.getY()-3);
		    	}
		    	while (it.hasNext()); */
		    		
		    }
	    }
	    //g.drawString("Calculation time: " + dt, 55, 45);
    }
    
    private void drawDebugSAT( EntityStatic entityPrimary , EntityStatic entitySecondary , Graphics2D g2 ){
    	
	    
	    EntityStatic player = entitySecondary;
	    EntityStatic stat = entityPrimary;
	    
	    //EntityStatic stat = staticEntitiesList.get(1);
	    //EntityStatic player = this.player;
	    
	    drawCross( player.getX() , player.getY() , g2);
	    drawCross( stat.getX() , stat.getY() , g2);
	    
	    Boundary bounds = stat.getLocalBoundary() ;
	    Boundary playerBounds = player.getLocalBoundary();
	    
	    Point2D playerCenter = new Point2D.Double(player.getX(), player.getY());
	    Point2D statCenter = new Point2D.Double(stat.getX(), stat.getY());
	    
	    	//for ( Line2D axis : bounds.debugSeparatingAxes(B_WIDTH, B_HEIGHT) ){
	    	for ( Line2D side : bounds.getSeparatingSides() ){
	    		
	    		Line2D axis = bounds.debugGetAxis(side, B_WIDTH, B_HEIGHT);
	    		
		    	g2.setColor(Color.DARK_GRAY);
		    	
		    		g2.draw(axis);
		    	
		    	g2.setColor(Color.GRAY);

			    
			    Line2D centerDistance = new Line2D.Float(player.getX() , player.getY(),
			    		stat.getX() , stat.getY());
			    Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);
			    
			    g2.draw(centerProjection);
		    	
		    	g2.setColor(Color.YELLOW);
			    
			    Point2D nearStatCorner = bounds.farthestPointFromPoint( bounds.getFarthestPoints(playerBounds,axis)[0] , axis );
			      
			    Point2D nearPlayerCorner = playerBounds.farthestPointFromPoint( playerBounds.getFarthestPoints(bounds,axis)[0] , axis );
			    //drawPointCross( nearCorner , g2);

			    
			    Line2D playerHalf = new Line2D.Float( 
						playerBounds.getProjectionPoint(playerCenter,axis) ,
						playerBounds.getProjectionPoint(nearPlayerCorner,axis)
								);
				Line2D statHalf = new Line2D.Float( 
						bounds.getProjectionPoint(statCenter,axis) ,
						bounds.getProjectionPoint(nearStatCorner,axis)
								);
				
				g2.draw(playerHalf);
				g2.setColor(Color.GREEN);
				g2.draw(statHalf);
				
				int centerDistanceX = (int)(centerProjection.getX1() -  centerProjection.getX2()  );
				int centerDistanceY = (int)(centerProjection.getY1() -  centerProjection.getY2()  );
				
				if (centerDistanceX>0){ centerDistanceX -= 1; } 
				else if (centerDistanceX<0){ centerDistanceX += 1; } //NEEDS HIGHER LEVEL SOLUTION
				
				if (centerDistanceY>0){ centerDistanceY -= 1; } 
				else if (centerDistanceY<0){ centerDistanceY += 1; }
				
				int playerProjectionX = (int)(playerHalf.getX1() -  playerHalf.getX2());
				int playerProjectionY = (int)(playerHalf.getY1() -  playerHalf.getY2());
				
				int statProjectionX = (int)(statHalf.getX2() -  statHalf.getX1());
				int statProjectionY = (int)(statHalf.getY2() -  statHalf.getY1());
				
				int penetrationX = 0;
				int penetrationY = 0;  
				

				
				penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
				penetrationY = playerProjectionY + statProjectionY - centerDistanceY ;
				
	
				//Constrain X
				if ( Math.signum(penetrationX) != Math.signum(centerDistanceX)  || 
					Math.signum(penetrationY) != Math.signum(centerDistanceY)  ){
					penetrationX = 0;
					penetrationY = 0;
				}
				
				
				if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){ //LOOK INTO BETTER CONDITIONALS
					penetrationX = -(playerProjectionX + statProjectionX) ;
				}
				if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){
					penetrationY = -(playerProjectionY + statProjectionY) ;
				}
				
				
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
	    			(int)centerProjection.getX1()+20 , (int)centerProjection.getY1()+65);
			    
			    
			    //g2.setColor(Color.DARK_GRAY);
			    
			    //g2.draw(new Line2D.Float(playerBounds.getProjectionPoint( nearPlayerCorner , axis ), nearPlayerCorner) );
			    //g2.draw(new Line2D.Float(playerBounds.getProjectionPoint( playerCenter , axis ), playerCenter) );

	    	}
    	
    }
    
    private void drawDebugCollisions(Graphics g){
    	
    	g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, B_WIDTH, B_HEIGHT);
    	
	    Graphics2D g2 = (Graphics2D) g;
    	
	    drawDebugSAT( currentSelectedEntity , player , g2);
	    
	    drawDebugSAT( player , currentSelectedEntity , g2);

    }
    
    private void drawCross(int x, int y , Graphics g){
    	g.drawLine( x-3, y-3, x+3, y+3 );
		g.drawLine( x-3, y+3, x+3, y-3 );
    }
    
    private void drawCross(Point2D point , Graphics g){
    	g.drawLine((int)point.getX()-3, (int)point.getY()-3, (int)point.getX()+3, (int)point.getY()+3);
		g.drawLine((int)point.getX()-3, (int)point.getY()+3, (int)point.getX()+3, (int)point.getY()-3);
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public int getboundaryX(){ return B_WIDTH ;}
	public int getboundaryY(){ return B_HEIGHT ;}
	
	public Player getPlayer(){ return player; }
	
	public ArrayList<EntityStatic> getStaticEntities(){ return staticEntitiesList; }
	public ArrayList<EntityDynamic> getDynamicEntities(){ return dynamicEntitiesList; }
	public ArrayList<EntityDynamic> getPhysicsEntities(){ return physicsEntitiesList; }
    
}