package engine;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.w3c.dom.ls.LSInput;

import entities.*;
import physics.*;
import testEntities.*;
import misc.*;


public class Board extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double currentDuration;
	private double prevDuration;
	
	private Timer timer;
	
	private java.util.Timer timer2;
	
    private Player player;
    private  PaintOverlay p;
    private LaserTest laser;
    private ArrayList<EntityStatic> staticEntitiesList; 
    private static ArrayList<EntityDynamic> dynamicEntitiesList; 
    private static ArrayList<EntityDynamic> physicsEntitiesList; 
    private ArrayList<Collision> collisionsList = new ArrayList<Collision>(); 
    private boolean ingame = true;
    private final int ICRAFT_X = 275;
    private final int ICRAFT_Y = 150;
    public static final int B_WIDTH = 400;
    public static final int B_HEIGHT = 300;

    private final int DELAY = 10;
    
    private double time = 0;
    private double deltaTime = 0;
    
    private double t;
    private double dt;


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
    	currentDuration = System.currentTimeMillis();
    	prevDuration = currentDuration;
    	
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.BLACK);
        ingame = true;
        
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        
        // initialize object lists
        staticEntitiesList = new ArrayList<>();
        dynamicEntitiesList = new ArrayList<>();
        physicsEntitiesList = new ArrayList<>();

        // initialize player
        player = new Player(ICRAFT_X, ICRAFT_Y);
        player.getObjectGraphic().setVisible(true);

        
        //## TESTING ##
        //Manually add test objects here

        staticEntitiesList.add(new Platform(210,180,"platform02"));
        staticEntitiesList.add(new Platform(170,180,"platform"));
        staticEntitiesList.add(new Platform(250,240,"platform02"));
        staticEntitiesList.add(new Platform(210,240,"platform02"));
        staticEntitiesList.add(new Platform(50,180,"platform02"));
        staticEntitiesList.add(new Platform(60,270,"platform02"));
        staticEntitiesList.add(new StaticSprite(150,274, "grass01"));
        staticEntitiesList.add(new Ground(100,290,"ground01"));
        
      	physicsEntitiesList.add(new EntityPhysics(120,250,"box"));
        dynamicEntitiesList.add(new Bullet(100,100,1,1));
        
        //test for LaserTest entity
        laser = new LaserTest(143,260, B_WIDTH, 260);
        //dynamicEntitiesList.add(new LaserTest(400,60));  <-- can't add as long as I don't have a sprite for it
        //		--- for now will just draw in the drawObjects() method
        
        p = new PaintOverlay(200,0,150,60);
        initBullets();
        //###########
        //

        //sets the frame rate. Every 15 milliseconds, an action event is sent and performed by the 
        //actionPerformed() method overridden below.
        
        timer2 = new java.util.Timer(); //create timer
        
        TimerTask update = new UpdateBoard(this); // create new task, which uses this current board as parameter
        
        timer2.scheduleAtFixedRate( update , 15 , 15); // fire task every 15 ms

        //updateBoard();
    }
    
    /* ##################
     * ## UPDATE BOARD ##    (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     * ##################
     */
    
    // OLD TIMER
      @Override
      public void actionPerformed(ActionEvent e) {
    		  
		        //inGame(); // check if game is running
		
		        //RUN POSITION AND DRAW UPDATES
		        //updatePlayer();    
		        //updateDynamicEntities();
		        //updatePhysicsEntities(); 
		          
		        //RUN COLLISION DETECTION
		        //checkCollisions();
		          
		        //REDRAW ALL COMPONENTS

		        //repaint();
		        //Toolkit.getDefaultToolkit().sync();

      }
      
      public void updateBoard(){ //TESTING CONSTANT FPS
    	      
          deltaTime = System.currentTimeMillis() - time ;
    	  
	          //if (deltaTime > 15) {
	        	  
		          //RUN POSITION AND DRAW UPDATES
		          updatePlayer();    
		          updateDynamicEntities();
		          updatePhysicsEntities();
		          
			      laser.updatePosition();
		        
		          //RUN COLLISION DETECTION
		          checkCollisions();
		          
		          //REDRAW ALL COMPONENTS
		          repaint();
		          
		          time = System.currentTimeMillis();
		          
	          //}
		          
		         // Toolkit.getDefaultToolkit().sync(); // what does this even do
          
      }
      

      private void inGame() {
          if (!ingame) {
              timer.stop();
          }
      }

    //spawn bullets (TESTING)
    public void initBullets() {

        for (int[] p : pos) {
            dynamicEntitiesList.add(new Bullet(p[0], p[1],-1,1)); 
        }
        
    }
   
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
       // prevDuration = System.nanoTime();
        //if (ingame) {
        
            drawObjects(g);
            //Temporary Overlay Screen
            //p.drawBorder(g);
            
        /*} else {
            drawGameOver(g);
        }*/

       
      //  currentDuration = System.nanoTime();
       // dt = currentDuration - prevDuration;
    }
    
    
    /* ####################
     * Nested Static class allowing Board objects access to the Board.
     * This is interesting. Normally a class object inside Board, like the player, has
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
    

/* #################
 * ##  RENDERING  ## 
 * #################
 */
    private void drawObjects(Graphics g) {

    	drawDebug(g);
    	
    	//Draw all static entities from list (ex. platforms)
        for (EntityStatic stat : staticEntitiesList) {
            if (stat.getObjectGraphic().isVisible()) {
                g.drawImage(stat.getObjectGraphic().getImage(), 
                		stat.getObjectGraphic().getOffsetX() + stat.getX(), 
                		stat.getObjectGraphic().getOffsetY() + stat.getY(), this);
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
            g.drawImage(player.getObjectGraphic().getImage(), 
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
        //laser.setxEndPoint(B_WIDTH);
       // laser.setyEndPoint(physicsEntitiesList.get(0).getY()+10);
        laser.pewpew(g);
        
        

    }

    // Game Over screen - might be extended to own class of menu screens
    private void drawGameOver(Graphics g) {

        String msg = "Failure";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics fm = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - fm.stringWidth(msg)) / 2,
                B_HEIGHT / 2);
    }
    
   

    /*
    private void updateStaticObjects() {
    	
    }
    */
    
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
    		
    		//wrap objects around screen
    		//if ( physicsEntity.getY() > 300){
    		//	physicsEntity.setY(0);
    		//}
    		//if ( physicsEntity.getX() < 0){
    		//	physicsEntity.setX(400);
    		//}
    		
    		//CHECK IF ALIVE. IF NOT, REMOVE. 
    		//if ( !physicsEntity.isAlive()){
    		//	dynamicEntitiesList.remove(i);
    		//}
        }
    }


/* #########################
 * ## COLLISION DETECTION ##     
 * #########################
 */

    //check collision list and return true if two entities are already colliding
    public boolean hasActiveCollision(EntityStatic entity1, EntityStatic entity2){
    	
		for ( Collision activeCollision : collisionsList){
			
			if ( activeCollision.isActive(entity1, entity2) ) {
				return true;
			}
			
		}
		
		return false;
		
    }
    
    //Update status of collisions, run ongoing commands in collision, and destroy collisions that have completed
    //USE ARRAY LIST ITTERATOR INSTEAD OF FOR LOOP SINCE REMOVING INDEX CHANGES SIZE
    public void updateCollisions(){
    	
    	for ( int i = 0 ; i < collisionsList.size() ; i++ ){
    		
    		//if collision is complete, remove from active list
    		if (collisionsList.get(i).isComplete() ) {
    			collisionsList.remove(i);
    		}
    		else {
    			
    			collisionsList.get(i).updateCollision(); //Run commands from inside collision object
    			
    		}
    	}
    	
    }
    
    //THIS IS THE MAIN BODY OF THE COLLISION ENGINE
    public void checkCollisions() { 

    	updateCollisions(); // calculate and remove old collisions
    	
        Rectangle r0 = player.getBoundingBox(); // get bounding box of player first
 
        //make larger box to represent distance at which a new collision will be opened 
        Rectangle r3 = new Rectangle(r0.x - 1 , r0.y - 1, r0.width + 2, r0.height + 2); 
        
        //KILL PLAYER AT BOTTOM OF SCREEN
        if (r3.getMinY() > B_HEIGHT) {  
        	
            //player = new Player(ICRAFT_X, ICRAFT_Y);	
        		//replacing new entities should be done only when necessary, since initializing entity reloads every sprite
        		// causing slight lag
        	
        	//could be teleport(x,y) or reposition(x,y) method in either player or parent entity classes
        	player.setX(ICRAFT_X);
        	player.setY(ICRAFT_Y);
        	player.setDX(0);
        	player.setDY(0);
        	player.setAccX(0);
        	player.setAccY(0.1f);
        	
        }//
        
        
        // Check collisions between player and static objects
        for (EntityStatic staticEntity : staticEntitiesList) { 
        	
            //Rectangle r4 = staticEntity.getBoundingBox();
            
            //r4 = new Rectangle(r4.x - 4 , r4.y - 4, r4.width + 8, r4.height + 8); 
  
            //Rectangle r5 = new Rectangle( r3.x /*+ (int) player.getDX()*/ , r3.y + (int) player.getDY() , r3.width , r3.height );
                      
	        //    if (r5.intersects(r4) ) {
        		if ( player.getLocalBoundary().boundaryIntersects(staticEntity.getLocalBoundary()) ) {
	            	
	            	//OPEN COLLISION

	            	if (!hasActiveCollision(player,staticEntity)) { //check to see if collision isn't already occurring
	            		collisionsList.add(new CollisionPlayerStaticOld(player,staticEntity)); // if not, add new collision event

	            	} 	
	            }
        }
        
        // TEST LASER COLLISION 
        for (EntityStatic stat : staticEntitiesList) { 
        
        	if ( stat.getLocalBoundary().boundaryIntersects(laser.getBoundary()) ) {
	            	
	            //OPEN COLLISION
	            if (!hasActiveCollision(laser,stat)) { //check to see if collision isn't already occurring
	            	collisionsList.add(new Collision(laser, stat)); // if not, add new collision event

	            } 	
	    	}
        }
        
        
        
        //Check collisions between dynamics entities and static entities
        for (EntityDynamic dynamicEntity : dynamicEntitiesList) { //index through physics entities
        
        //EntityDynamic physEntity = physicsEntitiesList.get(0);
        	
            Rectangle r1 = dynamicEntity.getBoundingBox();
            
            for (EntityStatic statEntity : staticEntitiesList){ // index through static entities
            	
            	Rectangle r2 = statEntity.getBoundingBox();
            
	            if (r1.intersects(r2)) {
	            	
	            	if (!hasActiveCollision(dynamicEntity,statEntity)) { 
	            		collisionsList.add(new CollisionBasic(dynamicEntity,statEntity)); 
	            	}
	            }  
	            
            }
            
        }
        

        // Check collisions between player and physics objects
        for (EntityDynamic physics : physicsEntitiesList) { 
        	        
        	Rectangle r4 = physics.getBoundingBox();
        	
	        if (r3.intersects(r4) ) {  
	        	
	            	//OPEN COLLISION
	            	if (!hasActiveCollision(player,physics)) { //check to see if collision isn't already occurring
	            		collisionsList.add(new CollisionPlayerDynamic(player,physics)); // if not, add new collision event

	            	} 	
	        }
        }
        
        
        
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
        }
    }
    
    public static int getBoardWidth() {
    	return B_WIDTH;
    }
    
    public static int getBoardHeigt() {
    	return B_HEIGHT;
    }
    
    private void drawDebug(Graphics g){ // DEBUG GUI
        g.setColor(Color.GRAY);
	    g.drawString("FPS: " + Math.round(1000/deltaTime),5,15);
	    g.drawString("DX: "+player.getDX() + " DY: " + player.getDY(),5,30);
	    g.drawString("AccX: " + player.getAccX(),5,45);
	    g.drawString("AccY: " + player.getAccY(),5,60);
	    g.drawString("Player State: " + player.getPlayerStateName(),5,75);
	    
	    //Draw player bounding box
	    Graphics2D g2 = (Graphics2D) g;
	    g2.setColor(Color.CYAN);
	    for (Line2D line : player.getLocalBoundary().getSides()){
	    	g2.draw(line);
	    }
	    
	    g2.draw(laser.getBoundary().getSides()[0]);
	    
	    for ( EntityStatic stat : staticEntitiesList) {
	    	
	    	for (Line2D line : stat.getLocalBoundary().getSides()){
		    	g2.draw(line);
		    }
	    	
	    }
	    
	    
	    
	    //DEBUG - DISPLAY LIST OF COLLISIONS
	    g.drawString("Collisions: ",5,90);
	    if (!collisionsList.isEmpty())
	    {
		    g.drawString("Collisions: ",5,90);
		   
		    for (int i = 0 ; i < collisionsList.size() ; i++){
		    	g.drawString(""+collisionsList.get(i),5,105+(10*i));
		    }
	    }
	    g.drawString("Calculation time: " + dt, 55, 45);
    }
    
}