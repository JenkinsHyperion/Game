package entities;


import entityComposites.Collidable;
import physics.Boundary;

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

    public float getDX() {
    	return dx;
    }
    
    public float getDY() {
    	return dy;
    }
    
    public void setDX(float setdx) {
    	dx = setdx;
    }
    
    public void setDY(float setdy) {
    	dy = setdy;
    }
    @Override
    public float getDeltaX(){
    	return (x + dx + accX);
    }
    @Override
    public float getDeltaY(){
    	return (y + dy + accY);
    }
    
    public void clipDX(float clipDX) {
    	if ( dx > 0 ) {
    	    
    		if ( clipDX < 0 ){ 
    			if ( clipDX + dx > 0)
    				dx = (dx + clipDX);
    			else
    				dx = 0;
    		}
    	}
    	else if ( dx < 0 ) {
    		
    		if ( clipDX > 0 ){ 
    			if ( clipDX + dx < 0)
    				dx = dx + clipDX;
    			else
    				dx = 0;
    		}
    	}
    }
    
    public void clipDY(float clipDY) { 
    	if ( dy > 0 ) {
    
    		if ( clipDY < 0 ){ 
    			if ( clipDY + dy > 0)
    				dy = dy + clipDY;
    			else
    				dy = 0;
    		}
    	}
    	else if ( dy < 0 ) {
    		
    		if ( clipDY > 0 ){ 
    			if ( clipDY + dy < 0)
    				dy = (dy + clipDY);
    			else
    				dy = 0;
    		}
    	}
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
    
    public void setDampeningX(float decceleration) { 
    	if (dx > (0.1))
    	{
    		applyAccelerationX( -decceleration );
    	}
    	else if (dx < (-0.1))
    	{
    		applyAccelerationX( decceleration );
    	}
    	else
    	{
    		accX=0;
    		dx=0;
    	}
    }
    
    public void applyAccelerationX(float accX){
    	accX =+ accX;
    }
    
    public void applyAccelerationY(float accY){
    	accY =+ accY;
    }

    
    public boolean isColliding(){ return isColliding; }
    public void setColliding( boolean state ){ isColliding = state;}
    

    
    
    
}
