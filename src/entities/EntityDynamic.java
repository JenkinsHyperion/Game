package entities;


import java.awt.Point;
import java.util.ArrayList;

import physics.*;

public class EntityDynamic extends EntityStatic{

	protected float dx;
    protected float dy;
    protected float accY;
    protected float accX;
    
    
	protected ArrayList<Force> forces = new ArrayList<>();
	protected ArrayList<PointForce> pointForces = new ArrayList<>();
    
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
    
    public void halt(){
    	dx=0;
    	dy=0;
    	accX=0;
    	accY=0;
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
    
    public void setVelocity( Vector vector){
    	dx = (float)vector.getX();
    	dy = (float)vector.getY();
    }
    
    public void addVelocity( Vector vector){
    	dx += (float)vector.getX();
    	dy += (float)vector.getY();
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
    
    
    public void clipAccX(float clipAccX) {
    	if ( accX > 0 ) {
    	    
    		if ( clipAccX < 0 ){ 
    			if ( clipAccX + accX > 0)
    				accX = (accX + clipAccX);
    			else
    				accX = 0;
    		}
    	}
    	else if ( accX < 0 ) {
    		
    		if ( clipAccX > 0 ){ 
    			if ( clipAccX + accX < 0)
    				accX = accX + clipAccX;
    			else
    				accX = 0;
    		}
    	}
    }
    
    public void clipAccY(float clipAccY) { 
    	if ( accY > 0 ) {
    
    		if ( clipAccY < 0 ){ 
    			if ( clipAccY + accY > 0)
    				accY = accY + clipAccY;
    			else
    				accY = 0;
    		}
    	}
    	else if ( accY < 0 ) {
    		
    		if ( clipAccY > 0 ){ 
    			if ( clipAccY + accY < 0)
    				accY = (accY + clipAccY);
    			else
    				accY = 0;
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
    

    
	/** Creates new Force on this Collidable out of input Vector, and returns the Force that was added
	 * 
	 * @param vector
	 * @return
	 */
    public Force addForce( Vector vector ){
    	
    	int indexID = forces.size();     	
    	Force newForce = new Force( vector , indexID );
    	forces.add( newForce ) ;
    	System.out.print("Adding Force "+ indexID+" ... ");
    	return newForce;
    }
    
    public void removeForce(int index){ 
    	System.out.print("Removing Force "+ index+" ... ");

	    for ( int i = index+1 ; i < forces.size() ; i++) {
	    	forces.get(i).indexShift();
	    } 
    	forces.remove(index); 
	    
    }
    
    //MOVE TO ROTATIONAL BODY
    public PointForce addPointForce( Vector vector , Point point ){

    	int indexID = pointForces.size();     	
    	PointForce newForce = new PointForce( vector, point , indexID );
    	pointForces.add( newForce ) ;
    	return newForce;
    }
    
    public void removePointForce(int index){ 
    	
    	pointForces.remove(index); 
	    for ( int i = index ; i < pointForces.size() ; i++) {
	    	pointForces.get(i).indexShift();
	    } 
    }
    
    
    public Vector[] debugForceArrows(){
    	Vector[] returnVectors = new Vector[ forces.size() ];
    	for ( int i = 0 ; i < forces.size() ; i++ ){
    		returnVectors[i] = forces.get(i).getVector() ;
    	}
    	return returnVectors;
    }
	
    @Deprecated
    public Vector sumOfForces(){
    	
    	Vector returnVector = new Vector(0,0);
    	for ( Force force : forces ){
    		returnVector = returnVector.add( force.getVector() );
    	}
    	
    	return returnVector;
    }
    
    public void applyAllForces(){
    	for ( Force force : forces ){

    		Vector acc = force.getLinearForce();
    		accX = (float)acc.getX();
    		accY = (float)acc.getY();
    	}
    }
    
    
    
    
}
