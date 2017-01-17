package testEntities;

import entities.*;
import entityComposites.Collidable;
import physics.*;

import java.awt.geom.Line2D;

import engine.Board;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;


/* just a simple class to test out drawing a laser to the screen and having it persist. */

//will be worked on later to make it run in conjunction with an enemy entity like a turret

// July 25th 2016, 3:00am
//IMPORTANT NOTE ABOUT THIS CLASS  
//-------------------
//currently sets a bounding box based on the beginning and ending point coordinates for the line being drawn,
//meaning it's practically useless for when the laser angles itself. For now it's mostly
//just goint to be a horizontal laser.

//ALSO
//Currently this class isn't being added to any of the ArrayLists in Board because of methods that are called later in Board
//that try to access Sprites and images that this class does not have, which is causing problems, so for now I'm just manually drawing it 
//inside of drawObjects in Board.java  (update: 3:19am I have its origin affixed to the floating box thing)

//--This means it can't handle any collision/killing you quite yet, but the guts for the bounding box are there now at least.



//Try messing around with the laser code in the drawObjects() area; it just occured to me we can use this line drawing functionality
//to use for wire-frame type debugging.
/**
 * @author Dave
 * <br /> <br />
 * <b>LaserTest class: </b>
 * just a simple class to test out drawing a laser to the screen and having it persist.
 * 
 */
public class Tracer extends EntityDynamic{ // Can extend either EntityStatic or EntityDynamic

	Board currentBoard;
	//note, the supplied x and y variables will act as the origin point for the line
	private int xEndPoint;
	private int yEndPoint;
	//graphics2D objects allow more sophisticated options for drawing shapes, such as a line in this case
	private Graphics2D g2;
	private Graphics2D dbb; //dbb: "debugbox" graphics object that will be drawn around laser beam
	
	//private Rectangle debugBox; // the data of the bounding box to be drawn; might be unecessessary could maybe just
								// directly draw "boundingBox" defined in EntityStatic
	//the actual Shape object for the laser that has the ability to draw itself
	private Line2D.Float beam;
	private Line2D.Float tracer;
	private EntityStatic parent;
	private Crosshair aim;
	/**
	 * <code>
	 * @param x the x-origin point for the laser
	 * @param y the y-origin point for the laser
	 * @param xEnd the x-end point for the laser
	 * @param yEnd the y-end point for the laser
	 * </code>
	 */
    public Tracer(int x, int y, int xEnd, int yEnd, Board board) {  
		super(x, y);
		setxEndPoint(xEnd);
		setyEndPoint(yEnd);
		
		currentBoard = board;
		aim = new Crosshair(xEndPoint, xEndPoint, this , currentBoard.player );
		
        initialize();
       //debugBox = new Rectangle();
        beam = new Line2D.Float();
        tracer = new Line2D.Float();
        
	}
    
    public Tracer(int x, int y, EntityStatic parent , Board board) {  
		super(x, y);
		setxEndPoint(x);
		setyEndPoint(y);
		currentBoard = board;
		aim = new Crosshair(xEndPoint, xEndPoint, this , currentBoard.player );
		this.parent = parent;
		
        initialize();
       //debugBox = new Rectangle();
        beam = new Line2D.Float();
        tracer = new Line2D.Float();
        
	}
    
    private void initialize(){
    	
		name = "Tracer";
    	//first argument x-position will be at 0 and extend to the right
    	//second argument y-position will start a bit above the y-point set so that the bounding box can extend in a range above and below it
    	
    	//NOTE: for now, don't angle the laser. Has potential to create a hit box that covers the entire screen
    //setBoundingBox( getX(), getY()-10, Math.abs(getX()-xEndPoint), Math.abs(getY()-yEndPoint) ); 
    	// ^^^ this one works, revert if the following code below doesn't work
    	    	
		//((Collidable) collisionType).setBoundary( new Boundary(new Line2D.Double(1, 1, 100, 100) , null  ) );
    	
    }
    /**
     * <code>pewpew(Graphics)</code>: contains the actual code for drawing the laser
     * @param g the graphics object that's part of JPanel (or JComponent?)
     */
    public void pewpew(Graphics g){
    	g2 = (Graphics2D) g;
    	//dbb = (Graphics2D) g;
    	g2.setColor(Color.RED);
    	//dbb.setColor(Color.CYAN);
    	//setting point variables for the laser
    	Point originPoint = new Point(getX(),getY());
    	Point endPoint = new Point(xEndPoint,yEndPoint);
    	

    	
    	tracer = new Line2D.Float(originPoint,endPoint);
    	
    	//creating the laser, getting its bounds, and drawing it
    	//beam = new Line2D.Float(originPoint,endPoint);
       	
    	g2.draw(beam); 
    	aim.getEntitySprite().drawSprite(g2); //draw crosshair

    }
    
    @Override
    public void updatePosition(){
    	
    	aim.updatePosition();
    	
       	this.setX(parent.getX()+25);
       	this.setY(parent.getY()+10);
        
       	//this.setxEndPoint((int)aim.getX()+1);
       	//this.setyEndPoint((int)aim.getY()+1);
        this.setxEndPoint(currentBoard.player.getX());
       	this.setyEndPoint(currentBoard.player.getY());
    	
    	//Rough testing collision blocking
    	beam = new Line2D.Float(getPos(),new Point(xEndPoint,yEndPoint));
    	
    	for (Collision collision : ((Collidable) this.getCollisionType()).getCollisions() ){
    		CollisionPositioning laserBlock = (CollisionPositioning) collision;
    		beam = new Line2D.Float ( getPos() , laserBlock.getClosestIntersection() );
    	}
    	
    	//((Collidable) collisionType).setBoundary( new Boundary(tracer , null ) );
       	
    }
    
    /**
	 * @return the xEndPoint coordinate
	 */
	public int getxEndPoint() {
		return xEndPoint;
	}

	/**
	 * @param xEndPoint the xEndPoint to set
	 */
	public void setxEndPoint(int xEndPoint) {
		this.xEndPoint = xEndPoint;
	}

	/**
	 * @return the yEndPoint coordinate
	 */
	public int getyEndPoint() {
		return yEndPoint;
	}

	/**
	 * @param yEndPoint the yEndPoint to set
	 */
	public void setyEndPoint(int yEndPoint) {
		this.yEndPoint = yEndPoint;
	}
	
	@Override
	public void onCollisionEvent() {

			aim.deactivate();
			System.out.println("deactivate");
	}
	
	@Override
	public void onCollisionCompletion() {
			System.out.println("targeting");
			aim.activate();
	}

}
