package entities;

import java.awt.Point;

/* This is the base class for all game objects. Contains only position variables. 
 * 
 */

public class Entity { 
	public static int count;
	
	protected boolean alive = true;
	protected boolean collidable = false;
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

    public void setY(int sety) {
        y = sety;
    }
    
    public void setY(double sety) {
        y = (float) sety;
    }

    public void setPos(Point p){
    	x = (float) p.getX();
    	y = (float) p.getY();
    }
    
}
