package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import engine.Board;
import entities.EntityDynamic;
import entities.EntityStatic;
import entityComposites.Collidable;
import physics.Collision;

public class CollisionPlayerStaticSAT extends Collision {
	
	
	public CollisionPlayerStaticSAT(Collidable collidable1, Collidable collidable2){
		
		super( (EntityDynamic)collidable1.getOwnerEntity() , collidable2.getOwnerEntity());
		
		entityPrimary = (EntityDynamic)collidable1.getOwnerEntity();
		entitySecondary = collidable2.getOwnerEntity();
		
		collidingPrimary = collidable1; // TAKE COLLIDABLE IN COSNTRUCTOR INSTEAD OF ENTITY
		collidingSecondary = collidable2;
		
		collisionDebugTag = collidable1.getOwnerEntity().name + " + " + collidable2.getOwnerEntity().name;
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		
		this.resolutionState = new ResolutionEvent();
		
		//updateCollision(); //Run math for first time OPTIMIZE, Add new code block for first time math

		System.out.println("\nBegin Collision");
		
		// Things like bullets won't need to go any futher than the initial method
		
		// Later on events will go here (damage, triggering, etc)
	}
	
	@Override
	public void updateCollision(){ 
		
		Resolution closestResolution = getClosestResolution();
		
		if (closestResolution.Vector() == null){ //Primary entity is at surface with resolution of 0,0
			
			entityPrimary.setColliding(true); //MOVE TO RESOLVED UPDATE CLASS JUST LIKE RESOLUTION EVENT
			entityPrimary.setDampeningX(0.1f); //
			
			triggerResolutionEvent( closestResolution ); 
			
		}
		else { //Primary Entity is clipping by closestResolution.vector()
			System.out.println("");
			System.out.println( ""+closestResolution.FeaturePrimary().toString() + " colliding with " + closestResolution.FeatureSecondary().toString() );
			
			Vector resolution = closestResolution.Vector();
			
			depthX = (int) resolution.getX();
			depthY = (int) resolution.getY();
			
			System.out.println("\nWill clip by "+ depthX+" , "+ depthY );

			
			//Resolution will not resolve

			
			//Resolution will resolve
			
				System.out.println("Clamped DX: "+entityPrimary.getDX() + " and DY: "+entityPrimary.getDY());
			
				entityPrimary.setAccX( 0 );
				entityPrimary.clipDX((int) ( resolution.getX() ) );
    
				entityPrimary.setAccY( 0 );
				entityPrimary.clipDY((int) ( resolution.getY() ) );
			
				System.out.print(" to "+entityPrimary.getDX() + " and DY: "+entityPrimary.getDY());	
	
		}
	    
		 
	}
	
	protected class ResolutionEvent extends ResolutionState{

		@Override
		protected void triggerEvent( Resolution resolution ) { //One time event upon resolution of collision
			
			collisionDebugTag = "("+resolution.FeaturePrimary()+" of "+entityPrimary.toString()+") contacting ("+
					resolution.FeatureSecondary()+" of "+entitySecondary.toString()+")";
			
			System.out.println("\nEvent Triggered: "+resolution.FeaturePrimary().collisionEvent  );

			resolution.FeaturePrimary().collisionEvent.run( resolution.FeaturePrimary() , resolution.FeatureSecondary() );
			
		}

	}
		
	@Override
	protected void triggerResolutionEvent( Resolution resolution ) { 
					
		this.resolutionState.triggerEvent( resolution );
		this.resolutionState = ResolvedState.resolved();
			
	}

	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		
		collidingPrimary.onLeavingCollisionEvent();
		collidingSecondary.onLeavingCollisionEvent();
		
		entityPrimary.setColliding(false); // unset entity collision flag. 
		entityPrimary.setAccY(0.2f); //turn gravity back on
		//entityPrimary.setDY(2);
		entityPrimary.setAccX(0); //remove friction
		
		//Remove collision from involved entities lists
		collidingPrimary.removeCollision( entityPairIndex[0] );
		collidingSecondary.removeCollision(entityPairIndex[1] );
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
	private Resolution getClosestResolution() {
		//System.out.println("Checking best resolution"); 
		ArrayList<Resolution> penetrations = new ArrayList<>();
    	
    	// Get penetration vectors along all separating axes for primary entity and add to list
    	for (int i = 0 ; i < collidingPrimary.getBoundaryLocal().getSeparatingSides().length ; i++ ){
    		
    		if (getSeparationDistance(collidingPrimary.getBoundaryLocal().getSeparatingSides()[i]) != null){
    			
    			penetrations.add( getSeparationDistance(collidingPrimary.getBoundaryLocal().getSeparatingSides()[i]) );
    			
    		}
    	}
    	
    	// Get penetration vectors along all separating axes for other collision and add to list
    	//research if boundarylocal is needed
    	for ( EntityStatic entitySecondary : collidingPrimary.getCollidingPartners()){
    		
	    	for (int i = 0 ; i < ((Collidable) entitySecondary.getCollisionType()).getBoundaryLocal().getSeparatingSides().length ; i++ ){
	    		
	    		if (getSeparationDistance(((Collidable) entitySecondary.getCollisionType()).getBoundaryLocal().getSeparatingSides()[i]) != null){
	    			
	    			penetrations.add( getSeparationDistance(((Collidable) entitySecondary.getCollisionType()).getBoundaryLocal().getSeparatingSides()[i]) );
	    			
	    		}
	    	}
		}
    	 
    	
    	int penetrationX = 0;
    	int penetrationY = 0;
    	Resolution closestResolution = null;
    	
    	//RESOLUTION LOGIC checks all penetration vectors and finds best resolution (currently lowest)
    	if (penetrations.size() > 0){
    		
    		penetrationX = (int) ( penetrations.get(0).Vector().getX() ); //condense
    		penetrationY = (int) ( penetrations.get(0).Vector().getY() ); //<
    		closestResolution = penetrations.get(0); 					  //<
    			
	    	for ( int i = 0 ; i < penetrations.size() ; i++ ){
	    		Vector vector = penetrations.get(i).Vector(); //vector component of resolution

	    		if ( vectorOpposesVelocity( vector ) ) {
	    		
	    			//Keep lowest
		    		if ( (vector.getX()*vector.getX() + vector.getY()*vector.getY())
		    				< ( penetrationX*penetrationX + penetrationY*penetrationY )
		    			){
		    			//System.out.println("Accepted Lowest");
		    			penetrationX = (int) (vector.getX()); //obsolete
		    			penetrationY = (int) (vector.getY()); //obsolete
		    			closestResolution = penetrations.get(i);
		    		}
		    		else {
		    			//System.out.println("Rejected Greater");
		    		}
	    		}
	    		//else {
	    			//System.out.println("Violates velocity "+ entityPrimary.getDX() + " , " + entityPrimary.getDY() );
	    		//}
	    	}
    	}
    	
    	
    	if (penetrationX == 0 && penetrationY == 0 ){ //Passed to updateCollision() for collisions where side is flush
    		//System.out.println(" Null resolution 0,0 ");
    		return new Resolution(
    					closestResolution.FeaturePrimary(),
    					closestResolution.FeatureSecondary(),
    					null
    				);
    	}
    	else {
    		//return new Point(penetrationX,penetrationY); //return chosen best resolution
    		return closestResolution;
    		
    	}

	}
	

	
	private boolean vectorOpposesVelocity(Vector vector) {
		
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
	private Resolution getSeparationDistance( Line2D separatingSide ){
		
	    EntityStatic stat = entitySecondary;
	    
	    Boundary bounds = collidingSecondary.getBoundaryLocal() ;
	    Boundary playerBounds = collidingPrimary.getBoundaryDelta();
	    
	    int deltaX = (int) (entityPrimary.getDeltaX() );
	    int deltaY = (int) (entityPrimary.getDeltaY() );
	    
	    Point2D playerCenter = new Point2D.Double(deltaX, deltaY);
	    Point2D statCenter = new Point2D.Double(stat.getX(), stat.getY());
		
		
		Line2D axis = bounds.getSeparatingAxis(separatingSide); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
	    
	    Line2D centerDistance = new Line2D.Float(deltaX , deltaY,
	    		stat.getX() , stat.getY() );
	    Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);
	    
    	Point2D[] statOuterVertices= bounds.getFarthestPoints(playerBounds,axis);
    	Point2D[] playerOuterVertices= playerBounds.getFarthestPoints(bounds,axis);
    	
	    																					// [0] needs to be for loop
	    Vertex[] statInnerVertices = bounds.farthestVerticesFromPoint( statOuterVertices[0] , axis );
	      
	    Vertex[] playerInnerVertices = playerBounds.farthestVerticesFromPoint( playerOuterVertices[0] , axis );

	    //CLOSEST FEATURE
	    
	    
	    
	    //
	    
	    Line2D playerHalf = new Line2D.Float( 
				playerBounds.getProjectionPoint(playerCenter,axis) ,
				playerBounds.getProjectionPoint(playerInnerVertices[0].toPoint(),axis)
						);
		Line2D statHalf = new Line2D.Float( 
				bounds.getProjectionPoint(statCenter,axis) ,
				bounds.getProjectionPoint(statInnerVertices[0].toPoint(),axis)
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
		
		BoundaryFeature featurePrimary = playerInnerVertices[0];
		BoundaryFeature featureSecondary = statInnerVertices[0];
		
		if ( playerInnerVertices.length > 1 ){ 
			featurePrimary = playerInnerVertices[0].getSharedSide(playerInnerVertices[1]);
	    }
		
		if ( statInnerVertices.length > 1 ){ 
			featureSecondary = statInnerVertices[0].getSharedSide(statInnerVertices[1]);
	    }
		
			//System.out.println(""+featurePrimary.toString()+" : "+featureSecondary.toString() );
			return new Resolution( 
					featurePrimary, //construct sides
					featureSecondary,
					new Vector( penetrationX , penetrationY )
			);
			
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
		//return String.format("%s",collisionName);
		return "CollisionSAT: "+collisionDebugTag;
	}
	

}
