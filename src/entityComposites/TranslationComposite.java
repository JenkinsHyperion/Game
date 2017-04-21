package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import physics.Force;
import physics.PointForce;
import physics.Vector;
import utility.Ticket;

public class TranslationComposite{

	protected EntityStatic owner;
	
	protected TranslationComposite( EntityStatic owner ){
		this.owner = owner;
	}

	protected ArrayList<Force> forces = new ArrayList<>();
	protected ArrayList<PointForce> pointForces = new ArrayList<>();
    
    protected boolean isColliding;
    
    public void halt(){
    	System.err.println("Attempted to halt static");
    }
    
    public void AccelerateY() {

    	System.err.println("Attempted to accelerate y of static");
    }

    public double getDX() {
    	return 0;
    }
    
    public double getDY() {
    	return 0;
    }
    
    public void setDX(double setdx) {
    	System.err.println("Attempted to set y velocity of static");
    }
    
    public void setDY(double setdy) {
    	System.err.println("Attempted to set x velocity of static");
    }
    
    public void setVelocity( Vector vector){
    	System.err.println("Attempted to set velocity of static");
    }
    
    public void addVelocity( Vector vector){
    	System.err.println("Attempted to add velocity of static");
    }
    
    public double getDeltaX(){
    	return (owner.x);
    }

    public double getDeltaY(){
    	return (owner.y);
    }
    
    public void clipDX(double clipDX) {
    	System.err.println("Attempted to clip x velocity of static");
    }
    
    public void clipDY(double clipDY) { 
    	System.err.println("Attempted to clip y velocity of static");
    }
    
    
    public void clipAccX(float clipAccX) {
    	System.err.println("Attempted to clip x acceleration of static");
    }
    
    public void clipAccY(float clipAccY) { 
    	System.err.println("Attempted to clip y acceleration of static");
    }
    
    
    public void setAccX(float setAX) {
    	System.err.println("Attempted to set x acceleration of static");
    }
    
    public void setAccY(float setAY) {
    	System.err.println("Attempted to set y acceleration of static");
    }
    
    public double getAccY() {
    	return 0;
    }
    
    public double getAccX() {
    	return 0;
    }
    
    public void setDampeningX(float decceleration) { 
    	System.err.println("Attempted to apply x dampening to static");
    }
    
    public void applyAccelerationX(float accX){
    	System.err.println("Attempted to apply x acceleration to static");
    }
    
    public void applyAccelerationY(float accY){
    	System.err.println("Attempted to apply y acceleration to static");
    }

    
    public boolean isColliding(){ return isColliding; }
    public void setColliding( boolean state ){ isColliding = state;}
    

    
	/** Creates new Force on this Collidable out of input Vector, and returns the Force that was added
	 * 
	 * @param vector
	 * @return
	 */
    public Force addForce( Vector vector ){
    	System.err.println("Attempted to add force to static");
    	return null;
    }
    
    public void removeForce(int index){ 
    	System.err.println("Attempted to remove force from static");
    }
    
    //MOVE TO ROTATIONAL BODY
    public PointForce addPointForce( Vector vector , Point point ){
    	System.err.println("Attempted to add force to static");
    	return null;
    }
    
    public void removePointForce(int index){ 
    	System.err.println("Attempted to remove force on static");
    }
    
    
    public Vector[] debugForceArrows(){
    	System.err.println("Attempted to get forces of static");
    	return null;
    }
    
    public void applyAllForces(){
    	System.err.println("Attempted to apply force to static");
    }
	
	
}
