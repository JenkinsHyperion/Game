package testEntities;

import animation.*;
import engine.Board;
import entities.*;

import java.awt.geom.Line2D;
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
public class LaserTest extends EntityDynamic{ // Can extend either EntityStatic or EntityDynamic

	//note, the supplied x and y variables will act as the origin point for the line
	private int xEndPoint;
	private int yEndPoint;
	//graphics2D objects allow more sophisticated options for drawing shapes, such as a line in this case
	private Graphics2D g2;
	/**
	 * <code>
	 * @param x the x-origin point for the laser
	 * @param y the y-origin point for the laser
	 * @param xEnd the x-end point for the laser
	 * @param yEnd the y-end point for the laser
	 * </code>
	 */
    public LaserTest(int x, int y, int xEnd, int yEnd) {  
		super(x, y);
		setxEndPoint(xEnd);
		setyEndPoint(yEnd);
		
        initialize();
        
	}
    
    private void initialize(){
    	
		
    	//first argument x-position will be at 0 and extend to the right
    	//second argument y-position will start a bit above the y-point set so that the bounding box can extend in a range above and below it
    	
    	//NOTE: for now, don't angle the laser. Has potential to create a hit box that covers the entire screen
    	setBoundingBox( getX(), getY()-10, Math.abs(getX()-xEndPoint), Math.abs(getY()-yEndPoint) ); 
    	
    }
    /**
     * <code>pewpew(Graphics)</code>: contains the actual code for drawing the laser
     * @param g the graphics object that's part of JPanel (or JComponent?)
     */
    public void pewpew(Graphics g){
    	g2 = (Graphics2D) g;
    	g2.setColor(Color.RED);
    	Point originPoint = new Point(getX(),getY());
    	Point endPoint = new Point(xEndPoint,yEndPoint);
    	Line2D.Double beam = new Line2D.Double(originPoint,endPoint);
    	g2.draw(beam);
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

}
