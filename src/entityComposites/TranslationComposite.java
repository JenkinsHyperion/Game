package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import engine.BoardAbstract;
import physics.Force;
import physics.PointForce;
import physics.Vector;
import utility.ListNodeTicket;

public abstract class TranslationComposite implements EntityComposite{

	public abstract void halt();
    public abstract double getDX();
    public abstract double getDY();
    public abstract void setDX(double setdx);
    public abstract void setDY(double setdy) ;
    public abstract void setVelocityVector( Vector vector);
    public abstract void addVelocity( Vector vector);
    public abstract double getDeltaX( EntityStatic owner );
    public abstract double getDeltaY( EntityStatic owner );
    public abstract void clipDX(double clipDX);
    public abstract void clipDY(double clipDY);;
    public abstract void clipAccX(float clipAccX);
    public abstract void clipAccY(float clipAccY);
    public abstract void setAccX(float setAX);
    public abstract void setAccY(float setAY);
    public abstract double getAccY();
    public abstract double getAccX();
    public abstract void setDampeningX(float decceleration);
    public abstract void applyAccelerationX(float accX);
    public abstract void applyAccelerationY(float accY);
    public abstract Force addForce( Vector vector );
    
    public abstract void removeForce(int index);
    //MOVE TO ROTATIONAL BODY
    public abstract PointForce addPointForce( Vector vector , Point point );
    public abstract void removePointForce(int index);
    public abstract Vector[] debugForceArrows();
	public abstract Vector sumOfForces();
	public abstract void removeUpdateable();
	public abstract Vector getVelocityVector();
	
	private static final TranslationComposite.Null nullSingleton = new Null();
	
	public static TranslationComposite.Null nullSingleton(){
		return nullSingleton;
	}
	
	//CONCRETE TRANSLATION COMPOSITE CLASS ################################################################
	public static class Active extends TranslationComposite implements UpdateableComposite{
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
		
		protected Active(){ 
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
	    public void disable() {
	    	// TODO Auto-generated method stub
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
	
	//NULL SINGLETON CLASS ###############################################################################
	private static class Null extends TranslationComposite{
		protected String compositeName = "Null Singleton Translation Composite";
		protected Null(){
		}
	
		protected ArrayList<Force> forces = new ArrayList<>();
		protected ArrayList<PointForce> pointForces = new ArrayList<>();
	    
	    protected boolean isColliding;
	    
	    public void halt(){
	    	System.err.println("Attempted to halt static");
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
	    
	    public void setVelocityVector( Vector vector){
	    	System.err.println("Attempted to set velocity of static");
	    }
	    
	    public void addVelocity( Vector vector){
	    	System.err.println("Attempted to add velocity of static");
	    }
	    
	    public double getDeltaX( EntityStatic owner ){
	    	return (owner.x);
	    }
	
	    public double getDeltaY( EntityStatic owner ){
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
	
		public Vector sumOfForces() {
			System.err.println("Attempted to sum forces to static");
			return new Vector(0,0);
		}
		
		public void removeUpdateable(){
			System.err.println("Attempted to remove null Translation from updater");
		}
		
		@Override
		public boolean exists(){
			return false;
		}
	
		@Override
		public void disable() {
			//TODO ENSURE THIS NULL COMPOSITE IS DESTROYED
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
	
		public Vector getVelocityVector() {
			return new Vector(0,0);
		}
	}
}