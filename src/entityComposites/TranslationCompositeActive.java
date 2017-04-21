package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import physics.Force;
import physics.PointForce;
import physics.Vector;
import utility.Ticket;

public class TranslationCompositeActive extends TranslationComposite implements UpdateableComposite{
	
	private Ticket updaterSlot;	
	protected double dx=0;
    protected double dy=0;
    protected double accY=0;
    protected double accX=0;
	
	protected TranslationCompositeActive( EntityStatic owner ){
		super(owner);
	}

	protected ArrayList<Force> forces = new ArrayList<>();
	protected ArrayList<PointForce> pointForces = new ArrayList<>();
    
    protected boolean isColliding;

    @Override
    public void update() {
    	
    	owner.x = (float) (owner.x + this.dx) ;
    	owner.y = (float) (owner.y + this.dy) ;
    	
    	dx += accX; 
    	dy += accY;
    	
    }   
    @Override
    public void halt(){
    	dx=0;
    	dy=0;
    	accX=0;
    	accY=0;
    }
    @Override
    public void AccelerateY() {

    	owner.y += 1;
        if (owner.y > 300){
        	owner.y = 0;
        }
    }
    @Override
    public double getDX() {
    	return dx;
    }
    @Override
    public double getDY() {
    	return dy;
    }
    @Override
    public void setDX(double setdx) {
    	dx = setdx;
    }
    @Override
    public void setDY(double setdy) {
    	dy = setdy;
    }
    @Override
    public void setVelocity( Vector vector){
    	dx = (float)vector.getX();
    	dy = (float)vector.getY();
    }
    @Override
    public void addVelocity( Vector vector){
    	dx += (float)vector.getX();
    	dy += (float)vector.getY();
    }
    @Override
    public double getDeltaX(){
    	return (owner.x + dx + accX);
    }
    @Override
    public double getDeltaY(){
    	return (owner.y + dy + accY);
    }
    @Override
    public void clipDX(double clipDX) {
    	if ( dx > 0 ) {
    	    
    		if ( clipDX < 0 ){ 
    			if ( clipDX + dx > 0)
    				dx = (float) (dx + clipDX);
    			else
    				dx = 0;
    		}
    	}
    	else if ( dx < 0 ) {
    		
    		if ( clipDX > 0 ){ 
    			if ( clipDX + dx < 0)
    				dx = (float) (dx + clipDX);
    			else
    				dx = 0;
    		}
    	}
    }
    @Override
    public void clipDY(double clipDY) { 
    	if ( dy > 0 ) {
    
    		if ( clipDY < 0 ){ 
    			if ( clipDY + dy > 0)
    				dy = (float) (dy + clipDY);
    			else
    				dy = 0;
    		}
    	}
    	else if ( dy < 0 ) {
    		
    		if ( clipDY > 0 ){ 
    			if ( clipDY + dy < 0)
    				dy = (float) (dy + clipDY);
    			else
    				dy = 0;
    		}
    	}
    }
    
    @Override
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
    @Override
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
    @Override
    
    public void setAccX(float setAX) {
    	accX = setAX;
    }
    @Override
    public void setAccY(float setAY) {
    	accY = setAY;
    }
    @Override
    public double getAccY() {
    	return accY;
    }
    @Override
    public double getAccX() {
    	return accX;
    }
    @Override
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
    @Override
    public void applyAccelerationX(float accX){
    	accX =+ accX;
    }
    @Override
    public void applyAccelerationY(float accY){
    	accY =+ accY;
    }

    @Override
    public boolean isColliding(){ return isColliding; }
    @Override
    public void setColliding( boolean state ){ isColliding = state;}
    

    @Override
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
    @Override
    public void removeForce(int index){ 
    	System.out.print("Removing Force "+ index+" ... ");

	    for ( int i = index+1 ; i < forces.size() ; i++) {
	    	forces.get(i).indexShift();
	    } 
    	forces.remove(index); 
	    
    }
    @Override
    //MOVE TO ROTATIONAL BODY
    public PointForce addPointForce( Vector vector , Point point ){

    	int indexID = pointForces.size();     	
    	PointForce newForce = new PointForce( vector, point , indexID );
    	pointForces.add( newForce ) ;
    	return newForce;
    }
    @Override
    public void removePointForce(int index){ 
    	
    	pointForces.remove(index); 
	    for ( int i = index ; i < pointForces.size() ; i++) {
	    	pointForces.get(i).indexShift();
	    } 
    }
    
    @Override
    public Vector[] debugForceArrows(){
    	Vector[] returnVectors = new Vector[ forces.size() ];
    	for ( int i = 0 ; i < forces.size() ; i++ ){
    		returnVectors[i] = forces.get(i).getVector() ;
    	}
    	return returnVectors;
    }
    @Override
    public void applyAllForces(){
    	for ( Force force : forces ){

    		Vector acc = force.getLinearForce();
    		accX = (float)acc.getX();
    		accY = (float)acc.getY();
    	}
    }
}
