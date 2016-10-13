package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import entities.EntityDynamic;
import entities.EntityStatic;
import entityComposites.Collidable;

public class CollisionPlayerStaticSAT extends Collision {
	
	private EntityDynamic entityPrimary; //redundant, parent has protected
	private EntityStatic entitySecondary;//redundant, parent has protected
	
	private Collidable collisionPrimary;
	private Collidable collisionSecondary;

	public CollisionPlayerStaticSAT(Collidable collidable1, Collidable collidable2){
		
		super( (EntityDynamic)collidable1.getOwner() , collidable2.getOwner());
		
		entityPrimary = (EntityDynamic)collidable1.getOwner();
		entitySecondary = collidable2.getOwner();
		
		collisionPrimary = collidable1; // TAKE COLLIDABLE IN COSNTRUCTOR INSTEAD OF ENTITY
		collisionSecondary = collidable2;
		
		collisionName = collidable1.getOwner().name + " + " + collidable2.getOwner().name;
		
		//Put this collision into each entity's list of interactions and receive the index where it was put
		//System.out.println("Indexed collisions "+ toString() + " " +entityPairIndex[0] +  " " + entityPairIndex[1]);
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		
		//updateCollision(); //Run math for first time 

		
		// Things like bullets won't need to go any futher than the initial method
		
		// Later on events will go here (damage, triggering, etc)
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	@Override
	public void updateCollision(){ 
		
		Point resolution = getClosestResolution();
		
		if (resolution == null){ //Primary entity is at surface with resolution of 0,0

			depthX = 0;
			depthY = 0;
			
			//System.out.println("euqalized" );

			
			entityPrimary.setColliding(true);
			//entityPrimary.setDX(0);
			//entityPrimary.setAccY(0);
			//entityPrimary.setDY(0);
			
			entityPrimary.setDampeningX(0.1f);
			
			
		}
		else { //Primary Entity is clipping with closest resolution of vector
			

			
			depthX = (int) resolution.getX();
			depthY = (int) resolution.getY();
			
			
			//Resolution will resolve
			
			//Resolution wont resolve
			
				entityPrimary.setX( entityPrimary.getDeltaX() + depthX );
				entityPrimary.setY( entityPrimary.getDeltaY() + depthY );
				
				entityPrimary.setAccX(0);
				entityPrimary.clipDX((int) ( -resolution.getX() ) );
				//System.out.println(" Resultant DX "+  entityPrimary.getDX() );
        
				entityPrimary.setAccY(0);
				entityPrimary.clipDY((int) ( -resolution.getY() ) );
				//System.out.println(" Resultant DY "+  entityPrimary.getDY() );
				
			//}
				
				
		}
	    
		 
	}
		
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		entityPrimary.setColliding(false); // unset entity collision flag. 
		entityPrimary.setAccY(0.2f); //turn gravity back on
		//entityPrimary.setDY(2);
		entityPrimary.setAccX(0); //remove friction
		
		//Remove collision from involved entities lists
		collisionPrimary.removeCollision( entityPairIndex[0] );
		collisionSecondary.removeCollision(entityPairIndex[1] );
	}
	
	/* ######################
	 * # CORE FUNCTIONALITY #
	 * ######################
	 */
	
	//Completion Condition
	@Override
	public boolean isComplete(){ // Check if entities are no longer colliding //OPTIMIZATION - HANDLE AS EVENT RATEHR THAN CHECK
		//CHECK FOR COLLISIONS IS BEING DOUBLE CHECKED IN COLLISION ENGINE
		
		//if (CollisionEngine.checkForCollisionsSAT(entityPrimary, entitySecondary) ) )
		
		if ( isComplete ) { // entities are no longer colliding
			completeCollision(); // run final commands
			return true; // return true for scanning loop in Board to delete this collision
		}
		else 
			return false;
	}
	
	//Resolution calculation
	private Point getClosestResolution() {
		//System.out.println("Checking best resolution"); 
		ArrayList<Point> penetrations = new ArrayList<>();

    	
    	// Get penetration vectors along all separating axes for primary entity and add to list
    	for (int i = 0 ; i < collisionPrimary.getBoundaryLocal().getSeparatingSides().length ; i++ ){
    		
    		if (getSeparationDistance(collisionPrimary.getBoundaryLocal().getSeparatingSides()[i]) != null){
    			
    			penetrations.add( getSeparationDistance(collisionPrimary.getBoundaryLocal().getSeparatingSides()[i]) );
    			
    		}
    	}
    	
    	// Get penetration vectors along all separating axes for other collision and add to list
    	//research if boundarylocal is needed
    	for ( EntityStatic entitySecondary : collisionPrimary.getCollidingPartners()){
    		
	    	for (int i = 0 ; i < ((Collidable) entitySecondary.collidability()).getBoundaryLocal().getSeparatingSides().length ; i++ ){
	    		
	    		if (getSeparationDistance(((Collidable) entitySecondary.collidability()).getBoundaryLocal().getSeparatingSides()[i]) != null){
	    			
	    			penetrations.add( getSeparationDistance(((Collidable) entitySecondary.collidability()).getBoundaryLocal().getSeparatingSides()[i]) );
	    			
	    		}
	    	}
		}
    	 
    	
    	int penetrationX = 0;
    	int penetrationY = 0;
    	
    	//RESOLUTION LOGIC checks all penetration vectors and finds best resolution (currently lowest)
    	if (penetrations.size() != 0){
    		
    		penetrationX = (int) ( penetrations.get(0).getX() );
    		penetrationY = (int) ( penetrations.get(0).getY() );
    		//System.out.println("Start "+penetrationX+" "+penetrationY + " -- "+entityPrimary.getX() + entitySecondary.getX());		
	    	for ( Point vector : penetrations ){
	    		//System.out.println("Proposed resolution "+ vector.getX() + " , " + vector.getY() );
	    		if ( vectorOpposesVelocity( vector ) ) {
	    		
	    			//Keep lowest
		    		if ( (vector.getX()*vector.getX() + vector.getY()*vector.getY())
		    				< ( penetrationX*penetrationX + penetrationY*penetrationY )
		    			){
		    			//System.out.println("Accepted Lowest");
		    			penetrationX = (int) (vector.getX());
		    			penetrationY = (int) (vector.getY()); 
		    		}
		    		else {
		    			//System.out.println("Rejected Greater");
		    		}
	    		}
	    		else {
	    			//System.out.println("Violates velocity "+ entityPrimary.getDX() + " , " + entityPrimary.getDY() );
	    		}
	    	}
    	}
    	
    	
    	if (penetrationX == 0 && penetrationY == 0 ){ //Passed to updateCollision() for collisions where side is flush
    		//System.out.println(" Null resolution 0,0 ");
    		return null;
    	}
    	else {
    		//System.out.println(" Accepted lowest resolution "+ penetrationX + " , " + penetrationY);
    		return new Point(penetrationX,penetrationY); //return chosen best resolution
    	}

	}
	

	
	private boolean vectorOpposesVelocity(Point vector) {
		
		if ( entityPrimary.getDX() > 0 ) {
			if ( vector.getX() > entityPrimary.getDX() ){
				return false;
			}
		}
		else if ( entityPrimary.getDX() < 0 ) {
			if ( vector.getX() < entityPrimary.getDX() ){
				return false;
			}
		}
		else { // if velocity is zero reject any non 0 vector 
			//if ( vector.getX() != 0)
				//return false;
		}
		
		if ( entityPrimary.getDY() > 0 ) {
			if ( vector.getY() > entityPrimary.getDY() ){
				return false;
			}
		}
		else if ( entityPrimary.getDY() < 0 ) {
			if ( vector.getY() < entityPrimary.getDY() ){
				return false;
			}
		}
		else {
			//if ( vector.getY() != 0)
				//return false;
		}
		return true;
	}

	/**
	 * 
	 * @param separatingSide
	 * @return Depth of intersection along given axis of separation.
	 * 
	 */
	private Point getSeparationDistance( Line2D separatingSide ){
		//This is the ejection algorithm that pushes a clipping boundary out of another boundary
		
	    EntityStatic stat = entitySecondary;
	    
	    Boundary bounds = collidingSecondary.getBoundaryLocal() ;
	    Boundary playerBounds = entityPrimary.getBoundaryDelta();
	    
	    int deltaX = (int) (entityPrimary.getDeltaX() );
	    int deltaY = (int) (entityPrimary.getDeltaY() );
	    
	    Point2D playerCenter = new Point2D.Double(deltaX, deltaY);
	    Point2D statCenter = new Point2D.Double(stat.getX(), stat.getY());
		
		
		Line2D axis = bounds.getSeparatingAxis(separatingSide); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
	    
	    Line2D centerDistance = new Line2D.Float(deltaX , deltaY,
	    		stat.getX() , stat.getY() );
	    Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);
	 
	    
	    Point2D nearStatCorner = bounds.farthestPointFromPoint( bounds.getFarthestPoints(playerBounds,axis)[0] , axis );
	      
	    Point2D nearPlayerCorner = playerBounds.farthestPointFromPoint( playerBounds.getFarthestPoints(bounds,axis)[0] , axis );


	    
	    Line2D playerHalf = new Line2D.Float( 
				playerBounds.getProjectionPoint(playerCenter,axis) ,
				playerBounds.getProjectionPoint(nearPlayerCorner,axis)
						);
		Line2D statHalf = new Line2D.Float( 
				bounds.getProjectionPoint(statCenter,axis) ,
				bounds.getProjectionPoint(nearStatCorner,axis)
						);
		
		
		int centerDistanceX = (int)(centerProjection.getX1() -  centerProjection.getX2()  );
		int centerDistanceY = (int)(centerProjection.getY1() -  centerProjection.getY2()  );
		
		if (centerDistanceX>0){ centerDistanceX -= 1; } 
		else if (centerDistanceX<0){ centerDistanceX += 1; } //NEEDS HIGHER LEVEL SOLUTION merge with later checks
		
		if (centerDistanceY>0){ centerDistanceY -= 1; } 
		else if (centerDistanceY<0){ centerDistanceY += 1; }
		
		int playerProjectionX = (int)(playerHalf.getX1() -  playerHalf.getX2());
		int playerProjectionY = (int)(playerHalf.getY1() -  playerHalf.getY2());
		
		int statProjectionX = (int)(statHalf.getX2() -  statHalf.getX1());
		int statProjectionY = (int)(statHalf.getY2() -  statHalf.getY1());
		
		int penetrationX = 0;
		int penetrationY = 0;
		
		// Get penetration vector
		penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
		penetrationY = playerProjectionY + statProjectionY - centerDistanceY ;
		


/*
 * ##############################################################################################33
 * 		CUT THIS CHECK AND FOLLOWING CODE INT SEPERATE METHODS FOR DISTANCE AND PENETRATION
 */

		if ( penetrationY * centerDistanceY < 0  ){
			//System.out.println("isn't contacting");
			isComplete=true;
		}
		else if ( penetrationX * centerDistanceX < 0  ){
			//System.out.println("isn't contacting");
			isComplete=true;
		}
		
		if ( penetrationX * centerDistanceX < 0  || penetrationY * centerDistanceY < 0  ) //SIGNS ARE NOT THE SAME
			{
				penetrationX = 0;
				penetrationY = 0;
				
			}
		

		// Handling of exception where centered collisions always have penetration of 0
		if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){ //LOOK INTO BETTER CONDITIONALS
			penetrationX = -(playerProjectionX + statProjectionX) ;
		}
		if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){ //Merge with above checks
			penetrationY = -(playerProjectionY + statProjectionY) ;
		}
		

			return new Point( penetrationX , penetrationY );

	}
	
	/*public Vector getPenetrationDepth(){   OPTIMIZATION - SEPARATE DISTANCE INTO PENETRATION AND ABSOLUTE DISTANCE

		return 
		
	}*/
	

	
	// Finds the actual contacting surface of two contacting sides. As in, if a side is overhanging off a ledge, only the 
	// part that is actually on the ledge is returned
	private void getContactPoints(Line2D side1, Line2D side2) {
		
		contactPoints[0]=null; contactPoints[1]=null;

		if ( pointIsOnSegment(side1.getP1(), side2) ) {
			if (contactPoints[0]==null)  { contactPoints[0] = side1.getP1(); } 
			else  { contactPoints[1] = side1.getP1(); return; }
		}
		if ( pointIsOnSegment(side1.getP2(), side2) ) {
			if (contactPoints[0]==null)  { contactPoints[0] = side1.getP2(); } 
			else  { contactPoints[1] = side1.getP2(); return; }
		}
		if ( pointIsOnSegment(side2.getP1(), side1) ) {
			if (contactPoints[0]==null)  { contactPoints[0] = side2.getP1(); } 
			else  { contactPoints[1] = side2.getP1(); return; }
		}
		if ( pointIsOnSegment(side2.getP2(), side1) ) {
			if (contactPoints[0]==null)  { contactPoints[0] = side2.getP2(); } 
			else  { contactPoints[1] = side2.getP2(); return; }
		}

	}
	

	
	public Point getDepth(){
		return new Point( depthX , depthY );
	}
	
	public String toString(){
		return String.format("%s",collisionName);
	}
	

}
