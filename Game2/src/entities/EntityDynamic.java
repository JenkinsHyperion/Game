package entities;

public class EntityDynamic extends EntityStatic{

	protected float dx;
    protected float dy;
    protected float accY;
    protected float accX;
    
    protected boolean isColliding;
	
    public EntityDynamic(int x, int y) {
    	super(x,y);
        //this.x = x;
        //this.y = y;
        //visibility = true;
    	//setX(x);
    	//setY(y);
    }
    
    public void updatePosition() {
    	
    	x += dx;
    	y += dy;
    	
    	dx += accX;
    	dy += accY;
    	
    }   
    
    public void AccelerateY() {

        y += 1;
        if (y > 300){
        	y = 0;
        }
    }

    public double getDX() {
    	return dx;
    }
    
    public double getDY() {
    	return dy;
    }
    
    public void setDX(float setdx) {
    	dx = setdx;
    }
    
    public void setDY(float setdy) {
    	dy = setdy;
    }
    
    public void setAccX(float setAX) {
    	accX = setAX;
    }
    
    public void setAccY(float setAY) {
    	accY = setAY;
    }
    
    public float getAccY() {
    	return accY;
    }
    
    public float getAccX() {
    	return accX;
    }
    
    public void setDampeningX() { 
    	if (dx > 0.1)
    	{
    		accX = (float) -0.1;
    	}
    	else if (dx < -0.1)
    	{
    		accX = (float) 0.1;
    	}
    	else
    	{
    		dx = 0;
    		accX = 0;
    	}
    }
    
    public boolean isColliding(){ return isColliding; }
    public void setColliding( boolean state ){ isColliding = state;}
    
}
