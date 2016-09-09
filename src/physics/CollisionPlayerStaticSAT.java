package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import entities.EntityDynamic;
import entities.EntityStatic;

public class CollisionPlayerStaticSAT extends Collision {
	
	private EntityDynamic entityPrimary; //redundant, parent has protected
	private EntityStatic entitySecondary;//redundant, parent has protected

	public CollisionPlayerStaticSAT(EntityDynamic entity1, EntityStatic entity2){
		
		super(entity1, entity2);
		
		entityPrimary = entity1;
		entitySecondary = entity2;
		collisionName = entity1.name + " + " + entity2.name;
		
		//Put this collision into each entity's list of interactions and receive the index where it was put

		
		//System.out.println("Indexed collisions "+ toString() + " " +entityPairIndex[0] +  " " + entityPairIndex[1]);
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		
		updateCollision(); //Run math for first time 

		
		// Things like bullets won't need to go any futher than the initial method
		
		// Later on events will go here (damage, triggering, etc)
	}
	
	//CONTINUOUS COLLISION COMMANDS - Ongoing commands during collision like particle effects, sound, etc.
	@Override
	public void updateCollision(){ 
		
		Point resolution = getClosestResolution();
		
		if (getClosestResolution() == null){ //Primary entity is at surface with resolution of 0,0

			depthX = 0;
			depthY = 0;
			
			entityPrimary.setColliding(true);
			//entityPrimary.setDX(0);
			entityPrimary.setAccY(0);
			//entityPrimary.setDY(0);
			
			if (entityPrimary.getDX() > (0.2))
	    	{
				entityPrimary.setAccX(-0.05f);
	    	}
	    	else if (entityPrimary.getDX() < (-0.2))
	    	{
	    		entityPrimary.setAccX(0.05f);
	    	}
	    	else
	    	{
	    		entityPrimary.setDX(0);
	    	}
			
			
		}
		else { //Primary Entity is clipping with closest resolution of vector
			
			depthX = (int) resolution.getX();
			depthY = (int) resolution.getY();

			
				if ( entityPrimary.getDX()*entityPrimary.getDX() < depthX*depthX ){
					entityPrimary.setX( entityPrimary.getX() + depthX);
				}

				//entityPrimary.setAccX(0);
				entityPrimary.clipDX((int) ( -resolution.getX() ) );

			// Y RESOLUTION

				if ( entityPrimary.getDY()*entityPrimary.getDY() < depthY*depthY ){
					entityPrimary.setY( entityPrimary.getY() + depthY);
				}
				
				//entityPrimary.setAccY(0);
				entityPrimary.clipDY((int) ( -resolution.getY() ) );

		}
	    
		 
	}
		
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		entityPrimary.setColliding(false); // unset entity collision flag. 
		entityPrimary.setAccY(0.1f); //turn gravity back on
		entityPrimary.setAccX(0); //remove friction
		
		//Remove collision from involved entities lists
		entityPrimary.removeCollision( entityPairIndex[0] );
		entitySecondary.removeCollision(entityPairIndex[1] );
	}
	
	/* ######################
	 * # CORE FUNCTIONALITY #
	 * ######################
	 */
	
	//Completion Condition
	@Override
	public boolean isComplete(){ // Check if entities are no longer colliding
		
		if (entityPrimary.getBoundaryLocal().boundaryIntersects( entitySecondary.getBoundaryLocal() ) ) {
			return false;
		}
		else if (entityPrimary.getBoundaryLocal().boundsHaveContact( entitySecondary.getBoundaryLocal() ) ) {
			return false;
		}
		else { // entities are no longer colliding
			completeCollision(); // run final commands
			return true; // return true for scanning loop in Board to delete this collision
		}
	}
	
	//Resolution calculation
	private Point getClosestResolution() {
		
		ArrayList<Point> penetrations = new ArrayList<>();

		
		/*// Get penetration vectors along all separating axes for secondary entity and add to list
    	for (int i = 0 ; i < entitySecondary.getBoundary().getSeparatingSides().length ; i++ ){
    		
    		if (getPenetrationDepth(entitySecondary.getBoundary().getSeparatingSides()[i]) != null){
    			
    			penetrations.add( getPenetrationDepth(entitySecondary.getLocalBoundary().getSeparatingSides()[i]) );
    			
    		}
    	}*/
    	
    	// Get penetration vectors along all separating axes for secondary entity and add to list
    	for (int i = 0 ; i < entityPrimary.getBoundary().getSeparatingSides().length ; i++ ){
    		
    		if (getPenetrationDepth(entityPrimary.getBoundary().getSeparatingSides()[i]) != null){
    			
    			penetrations.add( getPenetrationDepth(entityPrimary.getBoundaryLocal().getSeparatingSides()[i]) );
    			
    		}
    	}
    	
    	// Get penetration vectors along all separating axes for other collision and add to list
    	for ( EntityStatic entitySecondary : entityPrimary.getCollidingPartners()){
    		
    		
	    	for (int i = 0 ; i < entitySecondary.getBoundary().getSeparatingSides().length ; i++ ){
	    		
	    		if (getPenetrationDepth(entitySecondary.getBoundary().getSeparatingSides()[i]) != null){
	    			
	    			penetrations.add( getPenetrationDepth(entitySecondary.getBoundaryLocal().getSeparatingSides()[i]) );
	    			
	    		}
	    	}
		}
    	
    	int penetrationX = 0;
    	int penetrationY = 0;
    	
    	//RESOLUTION LOGIC checks all penetration vectors and finds best resolution (currently lowest)
    	if (penetrations.size() != 0){
    		
    		penetrationX = (int) Math.ceil( penetrations.get(0).getX() );
    		penetrationY = (int) Math.ceil( penetrations.get(0).getY() );
    		//System.out.println("Start "+penetrationX+" "+penetrationY + " -- "+entityPrimary.getX() + entitySecondary.getX());		
	    	for ( Point vector : penetrations ){

	    		
		    		if ( (vector.getX()*vector.getX() + vector.getY()*vector.getY())
		    				< ( penetrationX*penetrationX + penetrationY*penetrationY )
		    			){
		    			
		    			//System.out.println("Lower " + vector.getX()+" "+vector.getY());
		    			penetrationX = (int) Math.ceil(vector.getX());
		    			penetrationY = (int) Math.ceil(vector.getY()); 
		    		}

	    	}
    	}
    	
    	
    	if (penetrationX == 0 && penetrationY == 0 ){ //Passed to updateCollision() for collisions where side is flush
    		return null;
    	}
    	else {
    		return new Point(penetrationX,penetrationY); //return chosen best resolution
    	}

	}
	
	
	
	
	
	/**
	 * 
	 * @param separatingSide
	 * @return Depth of intersection along given axis of separation.
	 * 
	 */
	private Point getPenetrationDepth( Line2D separatingSide ){
		//This is the ejection algorithm that pushes a clipping boundary out of another boundary
		
	    EntityStatic stat = entitySecondary;
	    
	    Boundary bounds = stat.getBoundaryLocal() ;
	    Boundary playerBounds = entityPrimary.getBoundaryDelta();
	    
	    int deltaX = (int) (entityPrimary.getX() + entityPrimary.getDX() );
	    int deltaY = (int) (entityPrimary.getY() + entityPrimary.getDY() );
	    
	    Point2D playerCenter = new Point2D.Double(deltaX, deltaY);
	    Point2D statCenter = new Point2D.Double(stat.getX(), stat.getY());
		
		
		Line2D axis = bounds.debugGetAxis(separatingSide,300, 300); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
	    
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
		else if (centerDistanceX<0){ centerDistanceX += 1; } //NEEDS HIGHER LEVEL SOLUTION
		
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
		
		//Constrain X and Y Distances to penetration depths, 0 at surface, maximum at center
		if ( Math.signum(penetrationX) != Math.signum(centerDistanceX)  || 
				Math.signum(penetrationY) != Math.signum(centerDistanceY)  ){
				penetrationX = 0;
				penetrationY = 0;
			}
		
		// Handling of exception where centered collisions always have penetration of 0
		if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){ //LOOK INTO BETTER CONDITIONALS
			penetrationX = -(playerProjectionX + statProjectionX) ;
		}
		if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){
			penetrationY = -(playerProjectionY + statProjectionY) ;
		}
		

			return new Point( penetrationX , penetrationY );

	}
	

	
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
