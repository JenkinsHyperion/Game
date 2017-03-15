package entities;

import java.awt.Point;
import java.io.Serializable;

import engine.Camera;

/* This is the base class for all game objects. Contains only position variables. 
 * 
 */

public class Entity { 
	//some static fields to indicate the type of entity
	//will be heavily fleshed out later when organizing large amounts of entities becomes an issue
	public static final int PLATFORM = 10;
	public static final int GROUND = 11;
	public static final int PLANT = 12;
	public static final int MISC = 13;
	public static final int ENEMY = 14;
	public static int count;
	public String name = "blank entity" + count;
	protected boolean alive = true;
	protected boolean collidable = true; //default to true
	protected float x;
    protected float y;
    protected int entityType;
    
	public Entity(int x, int y){
    	setX(x);
    	setY(y);	
    	count++;
	}
    
    
    public void selfDestruct(){
    	alive = false;
    }
    
    public boolean isAlive(){
    	return alive;
    }
    
    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }
    
    public void setX(int setx) {
        x = setx;
    }
    
    public void setX(double setx) {
        x = (float) setx;
    }

    public void setY(int sety) {
        y = sety;
    }
    
    public void setY(double sety) {
        y = (float) sety;
    }
    
    public Point getPos(){
    	return new Point( (int)x , (int)y );
    }

    public void setPos(Point p){
    	x = (float) p.getX();
    	y = (float) p.getY();
    }
    
    public void setPos(int x, int y){
    	this.x = (float) x;
    	this.y = (float) y;
    }
    @Deprecated
    public boolean isCollidable() {
		return collidable;
	}
    @Deprecated
	public void setCollidable(boolean collidable) {
		this.collidable = collidable;
	}
	
	/**
	 * Returns this entity's X position relative to the camera screen, rather than the X position in board
	 * @param camera
	 * @return the ordinate of this entity relative to the camera area
	 */
	public int getXRelativeTo(Camera camera) {
        return camera.getRelativeX((int) this.x);
    }

	/**
	 * Returns this entity's Y position relative to the camera screen, rather than the Y position in board
	 * @param camera
	 * @return the ordinate of this entity relative to the camera area
	 */
    public int getYRelativeTo(Camera camera) {
    	return camera.getRelativeY((int) this.y);
    }
    
    /**
     * Returns this entity's position relative to the camera screen, rather than the Y position in board
     * @param camera
     * @return
     */
    public Point getPositionRelativeTo(Camera camera) {
        return new Point(
        			camera.getRelativeX((int) this.x),
        			camera.getRelativeY((int) this.y)
        		);
    }
    

}
