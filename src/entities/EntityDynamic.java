package entities;


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
    
    public void clipDX(float clipDX) {
    	if ( dx > 0 ) {
    		
    		if (clipDX < dx){ dx = dx - clipDX ; }
    		else { dx = 0 ; }
    	}
    	else if ( dx < 0 ) {
    		
    		if (clipDX > dx){ dx =  dx - clipDX ; }
    		else { dx = 0 ; }
    	}
    }
    
    public void clipDY(float clipDY) {
    	if ( dy > 0 ) {
    
    		if (clipDY < dy){ dy = dy - clipDY  ; }
    		else { dy = 0 ; }
    	}
    	else if ( dy < 0 ) {
    		
    		if (clipDY > dy){ dy = clipDY - dy  ; }
    		else { dy = 0 ; }
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
    
    public void setDampeningX(float offsetDX) { 
    	if (dx > (0.1))
    	{
    		accX = -0.1f;
    	}
    	else if (dx < (-0.1))
    	{
    		accX = 0.1f;
    	}
    	else
    	{
    		dx = offsetDX;
    		accX = 0;
    	}
    }
    
	public Boundary getBoundaryDelta(){
		return boundary.atPosition( (int) (x+dx), (int) (y+dy ));
	}
    
    public boolean isColliding(){ return isColliding; }
    public void setColliding( boolean state ){ isColliding = state;}
    

    
    
    
}
