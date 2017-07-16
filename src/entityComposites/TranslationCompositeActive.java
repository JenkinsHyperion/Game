package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import engine.BoardAbstract;
import physics.Force;
import physics.PointForce;
import physics.Vector;
import utility.ListNodeTicket;

public class TranslationCompositeActive extends TranslationComposite implements UpdateableComposite{
	protected String compositeName = "TranslationCompositeActive";
	private ListNodeTicket updaterSlot;	
	protected double dx=0;
    protected double dy=0;
    protected double accY=0;
    protected double accX=0;
    protected double dxT=0;
    protected double dyT=0;
    protected double accYT=0;
    protected double accXT=0;
	
	protected TranslationCompositeActive(){ 
	}

	protected ArrayList<Force> forces = new ArrayList<>();
	protected ArrayList<PointForce> pointForces = new ArrayList<>();
    
    protected boolean isColliding;
  
    @Override
    public void updateComposite() {
 
    	dx += accX; 
    	dy += accY;

    	Vector sum = this.sumOfForces();
    	accX = sum.getX();
    	accY = sum.getY();
 
    	
    }  
    @Override
    public void updateEntity( EntityStatic entity ) {
    	
    	entity.setX( entity.x + this.dx) ; 
    	entity.setY( entity.y + this.dy) ;
    	
    }  
    
    @Override
    public void halt(){
    	dx=0;
    	dy=0;
    	accX=0;
    	accY=0;
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
    public Vector getVelocityVector(){
    	return new Vector( dx , dy );
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
    public void setVelocityVector( Vector vector){
    	dx = vector.getX();
    	dy = vector.getY();
    }
    @Override
    public void addVelocity( Vector vector){
    	dx += vector.getX();
    	dy += vector.getY();
    }
    @Override
    public double getDeltaX( EntityStatic owner ){
    	return (owner.x + dx + accX);
    }
    @Override
    public double getDeltaY( EntityStatic owner ){
    	return (owner.y + dy + accY);
    }
    @Override
    public void clipDX(double clipDX) {
    	
    	double dxTemp = dx;
    	
    	if ( dx > 0 ){
    		if ( clipDX > 0){
    			
    		}
    		else if ( dx + clipDX < 0 ){
    			dx = 0;
    		}else{
    			dx = dx + clipDX;
    		}
    	}
    	else if ( dx < 0 ){
    		if ( clipDX < 0){
    			
    		}
    		else if ( dx + clipDX > 0 ){ 
    			dx = 0;
    		}else{
    			dx = dx + clipDX;
    		}
    	}else{
    		dx = 0;
    	}
    	System.out.println(dxTemp+" ->> "+clipDX+" ->> "+dx);
    	
    }
    @Override
    public void clipDY(double clipDY) { 
    	double dyTemp = dy;
    	if ( dy > 0 ){
    		if ( clipDY > 0){
    			
    		}
    		else if ( dy + clipDY < 0 ){
    			dy = 0;
    		}else{
    			dy = dy + clipDY;
    		}
    	}
    	else if ( dy < 0 ){
    		if ( clipDY < 0){
    			
    		}
    		else if ( dy + clipDY > 0 ){ 
    			dy = 0;
    		}else{
    			dy = dy + clipDY;
    		}
    	}else{
    		dy = 0;
    	}
    	System.out.println(dyTemp+" ->> "+clipDY+" ->> "+dy);
    }
    
    public void clampDX( int clamp ){
    	if ( dx > 0 ){
    		if (clamp < 0){
    			dx = 0;
    		}
    	}
    	else if (dx < 0){
    		if (clamp > 0){
    			dx=0;
    		}
    	}
    }
    
    public void clampDY( int clamp ){
    	if ( dy > 0 ){
    		if (clamp < 0){
    			dy = 0;
    		}
    	}
    	else if (dy < 0){
    		if (clamp > 0){
    			dy=0;
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
    	accXT = setAX;
    }
    @Override
    public void setAccY(float setAY) {
    	accYT = setAY;
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
    	//System.out.print("Adding Force "+ indexID+" ... ");
    	return newForce;
    }
    @Override
    public void removeForce(int index){ 
    	//System.out.print("Removing Force "+ index+" ... ");

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
    public Vector sumOfForces(){
    	
    	Vector returnVector = new Vector(0,0);
    	for ( Force force : forces ){
    		returnVector = returnVector.add( force.getVector() );
    	}
    	
    	return returnVector;
    }
    
    /** Attempts to add this Composite to Board updater thread.
     * 
     * @param board 
     * @return True if this composite was added to updater thread. False if this composite is already in updater thread
     */
    @Override
    public boolean addCompositeToUpdater( BoardAbstract board){ 
    	if ( this.updaterSlot == null ){
    		this.updaterSlot = board.addCompositeToUpdater(this);
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    @Override
    public void removeUpdateable(){
    	this.updaterSlot.removeSelf();
		System.out.println("Removing "+this+" from updater");
    }
    @Override
	public boolean exists(){
		return true;
	}
    @Override
	public void setCompositeName(String newName) {
		this.compositeName = newName;
	}
	@Override
	public String getCompositeName() {
		return this.compositeName;		
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
