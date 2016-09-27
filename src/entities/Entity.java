package entities;

import java.awt.Point;
import java.io.Serializable;

/* This is the base class for all game objects. Contains only position variables. 
 * 
 */

public class Entity implements Serializable { 
	public static int count;
	public String name = "blank entity";
	protected boolean alive = true;
	protected boolean collidable = true; //default to true
	protected float x;
    protected float y;
    
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
    
    public boolean isCollidable() {
		return collidable;
	}

	public void setCollidable(boolean collidable) {
		this.collidable = collidable;
	}

}
