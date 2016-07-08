package engine;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

import entities.*;
import entities.Player;
import physics.*;
import testEntities.*;


public class Board extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Timer timer;
    private Player player;
    private ArrayList<EntityStatic> staticEntitiesList; 
    private static ArrayList<EntityDynamic> dynamicEntitiesList; 
    private ArrayList<Collision> collisionsList = new ArrayList<Collision>(); 
    private boolean ingame = true;
    private final int ICRAFT_X = 300;
    private final int ICRAFT_Y = 200;
    static final int B_WIDTH = 400;
    static final int B_HEIGHT = 300;
    private final int DELAY = 15;

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

        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.BLACK);
        ingame = true;
        
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        
        // initialize object lists
        staticEntitiesList = new ArrayList<>();
        dynamicEntitiesList = new ArrayList<>();

        // initialize player
        player = new Player(ICRAFT_X, ICRAFT_Y);
        player.getObjectGraphic().setVisible(true);

        
        //## TESTING ##
        //Manually add test objects here
        staticEntitiesList.add(new Ground(150,290,"ground01"));
        staticEntitiesList.add(new Platform(150,230,"platform"));
        staticEntitiesList.add(new Platform(50,260,"platform"));
        staticEntitiesList.add(new StaticSprite(150,274, "grass01"));
        dynamicEntitiesList.add(new Bullet(100,100,1,1));
        
        initBullets();
        //###########
        //

        
        timer = new Timer(DELAY, this);
        timer.start();
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

        //if (ingame) {
        
            drawObjects(g);
            
        /*} else {
            drawGameOver(g);
        }*/

        Toolkit.getDefaultToolkit().sync();
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
    	
    	//Draw all static objects from list (ex. platforms)
        for (EntityStatic stat : staticEntitiesList) {
            if (stat.getObjectGraphic().isVisible()) {
                g.drawImage(stat.getObjectGraphic().getImage(), stat.getX(), stat.getY(), this);
            }
        }
        
        //Draw all dynamic (moving) objects from list (ex. bullets)
        for (EntityDynamic dynamic : dynamicEntitiesList) {
            if (dynamic.getObjectGraphic().isVisible()) {
                g.drawImage(dynamic.getObjectGraphic().getImage(), dynamic.getX(), dynamic.getY(), this);
            }
        }

		//Draw player
        if (player.getObjectGraphic().isVisible()) {
            g.drawImage(player.getObjectGraphic().getImage(), player.getX(), player.getY(), this);
        }

    //DRAW GUI - might be extended into debug console


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

    
  /* ##################
   * ## UPDATE BOARD ##    (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   * ##################
   */
    @Override
    public void actionPerformed(ActionEvent e) {

        inGame();

        //RUN POSITION AND DRAW UPDATES
        updateCraft();    
        updateDynamicObjects();
        //updateStaticObjects();
      
        //RUN COLLISION DETECTION
        checkCollisions();
        
        //REDRAW ALL COMPONENTS
        repaint();
    }

    private void inGame() {
        if (!ingame) {
            timer.stop();
        }
    }
    
    /*
    private void updateStaticObjects() {
    	
    }
    */
    
    // Update position and Graphic of dynamic objects
    private void updateDynamicObjects() {
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
    private void updateCraft() { 

        if (player.getObjectGraphic().isVisible()) { //obsolete

        	player.updatePosition();

        }
        
		player.getObjectGraphic().getAnimatedSprite().update();
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
    	
        Rectangle r3 = player.getBoundingBox(); // get bounding box of player first
        
        //TEMPORARY override collision with bottom of screen
        if (r3.getMaxY() > B_HEIGHT) {   		
            player.setDY(0);
            player.setDampeningX();   	
        }//
        
        
        // Check collisions between player and static objects
        for (EntityStatic staticEntity : staticEntitiesList) { 
        	
            Rectangle r4 = staticEntity.getBoundingBox();
            
            if (r3.intersects(r4)) {
            		
            	//TESTING COLLISION CLASS
            	
            	if (!hasActiveCollision(player,staticEntity)) { //check to see if collision isn't already occurring
            		collisionsList.add(new CollisionGenericTest(player,staticEntity)); // if not, add new collision event
            	}
            }      
        }
        

        // Check collisions between dynamic Entities and static Entities
        for (EntityDynamic dynamicEntity : dynamicEntitiesList) { //index through dynamic entities
        	
            Rectangle r1 = dynamicEntity.getBoundingBox();
            
            for (EntityStatic staticEntity : staticEntitiesList){ // index through static entities
            	
            	Rectangle r2 = staticEntity.getBoundingBox();
            
	            if (r1.intersects(r2)) {
	            	
	            	if (!hasActiveCollision(dynamicEntity,staticEntity)) { 
	            		collisionsList.add(new CollisionGenericTest(dynamicEntity,staticEntity)); 
	            	}
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
	    g.drawString("DeltaX: " + player.getDX(),5,15);
	    g.drawString("DeltaY: " + player.getDY(),5,30);
	    g.drawString("AccX: " + player.getAccX(),5,45);
	    g.drawString("AccY: " + player.getAccY(),5,60);
	    g.drawString("Player State: " + player.getPlayerState(),5,75);
	    
	    //DEBUG - DISPLAY LIST OF COLLISIONS
	    g.drawString("Collisions: ",5,90);
	    if (!collisionsList.isEmpty())
	    {
		    g.drawString("Collisions: ",5,90);
		   
		    for (int i = 0 ; i < collisionsList.size() ; i++){
		    	//g.drawString("i: " + String.format("%d",i), 100, 90);
		    	g.drawString(""+collisionsList.get(i),5,105+(10*i));
		    }
	    }
    }
    
}