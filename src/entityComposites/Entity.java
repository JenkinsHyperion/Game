package entityComposites;

import java.awt.Point;
import java.io.Serializable;

import engine.MovingCamera;
import physics.Vector;

/* This is the base class for all game objects. Contains only position variables. 
 * 
 */

public class Entity { 
	//some static fields to indicate the type of entity
	//will be heavily fleshed out later when organizing large amounts of entities becomes an issue
	public static final Point origin = new Point(0,0);
	
	public static final int PLATFORM = 10;
	public static final int GROUND = 11;
	public static final int PLANT = 12;
	public static final int MISC = 13;
	public static final int ENEMY = 14;
	public static int count;
	public String name;
	protected boolean alive = true;
	protected boolean collidable = true; //default to true
	protected double x;
    protected double y;
    protected int entityType;
    
	public Entity(int x, int y){
    	setX(x);
    	setY(y);	
    	name = "blank entity" + count;
    	count++;
	}
    
    
    public void selfDestruct(){
    	alive = false;
    }
    
    public boolean isAlive(){
    	return alive;
    }
    
    public int getX() {
        return (int)x;
    }

    public int getY() {
        return (int)y;
    }
    
    public void setX(int setx) {
        x = setx;
    }
    
    public void setX(double setx) {
        x = setx;
    }

    public void setY(int sety) {
        y = sety;
    }
    
    public void setY(double sety) {
        y = sety;
    }
    
    public Point getPosition(){
    	return new Point( (int)x , (int)y );
    }

    public void setPos(Point p){
    	x = (int) p.getX();
    	y = (int) p.getY();
    }
    
    public void setPos( double x, double y){
    	this.x =  x;
    	this.y =  y;
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
	public int getXRelativeTo(MovingCamera camera) {
        return camera.getRelativeX((int) this.x);
    }

	/**
	 * Returns this entity's Y position relative to the camera screen, rather than the Y position in board
	 * @param camera
	 * @return the ordinate of this entity relative to the camera area
	 */
    public int getYRelativeTo(MovingCamera camera) {
    	return camera.getRelativeY((int) this.y);
    }
    
    /**
     * Returns this entity's position relative to the camera screen, rather than the Y position in board
     * @param camera
     * @return
     */
    public Point getPositionRelativeTo(MovingCamera camera) {
        return new Point(
        			camera.getRelativeX((int) this.x),
        			camera.getRelativeY((int) this.y)
        		);
    }

    public Point getRelativeTranslationalPositionOf( Entity entity ) {
        return new Point(
        			entity.getX() - this.getX(),
        			entity.getY() - this.getY()
        		);
    }
    
    public Point getRelativeTranslationalPositionOf( Point world_position ) {
        return new Point(
        			world_position.x - this.getX(),
        			world_position.y - this.getY()
        		);
    }
    
    public Point getTranslationalAbsolutePositionOf( Point point_on_entity ) {
        return new Point(
        			point_on_entity.x + this.getX(),
        			point_on_entity.y + this.getY()
        		);
    }
    
    public Vector getSeparationVector( Entity partner ){
    	return new Vector( partner.x - this.x  , partner.y - this.y  );
    }
    public Vector getSeparationUnitVector( Entity partner ){
    	return new Vector( partner.x - this.x  , partner.y - this.y ).unitVector();
    }

}
