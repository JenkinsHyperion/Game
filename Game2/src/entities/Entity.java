package entities;

/* This is the base class for all game objects. Contains only position variables. 
 * 
 */

public class Entity { 
	public static int count;
	
	public Entity(int x, int y){
    	setX(x);
    	setY(y);	
    	count++;
    	
	}
	
	protected boolean alive = true;
	protected int x;
    protected int y;
    
    
    public void selfDestruct(){
    	alive = false;
    }
    
    public boolean isAlive(){
    	return alive;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public void setX(int setx) {
        x = setx;
    }

    public void setY(int sety) {
        y = sety;
    }
    
    
}
