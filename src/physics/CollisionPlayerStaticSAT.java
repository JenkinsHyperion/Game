package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import engine.Board;
import entities.EntityDynamic;
import entities.*;
import entityComposites.Collidable;
import physics.Collision;

public class CollisionPlayerStaticSAT extends Collision {
	
	Force normalForce = entityPrimary.addForce( new Vector( 0 , 0 ) );
	Force friction = entityPrimary.addForce( new Vector( 0 , 0 ) );
	
	Resolution currentResolution;
	
	ResolutionEvent resolutionEvent = new ResolutionEvent();
	
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
		
		this.resolutionState = resolutionEvent;
		
		//updateCollision(); //Run math for first time OPTIMIZE, Add new code block for first time math

		System.out.println("\n### Collision Start between "+entityPrimary + " and " + entitySecondary + " ===============");
		
		// Things like bullets won't need to go any futher than the initial method
		
		// Later on events will go here (damage, triggering, etc)
	}
	
	@Override
	public void updateCollision(){ 
		
		Resolution closestResolution = getClosestResolution();
		

		if ( 
				(	(int)closestResolution.getDistanceVector().getY()-(int)entityPrimary.getDY() != 0  
					&& (int)closestResolution.getClippingVector().getY() != 0 
				)
				||
				(	(int)closestResolution.getDistanceVector().getX()-(int)entityPrimary.getDX() != 0
					&& (int)closestResolution.getClippingVector().getX() != 0 
				)
				
		) { //Primary Entity is clipping by closestResolution.vector() 
					
			entityPrimary.setColliding(false);
			System.out.println( ""+closestResolution.FeaturePrimary().toString() + " colliding with " + closestResolution.FeatureSecondary().toString() );
			
			Vector resolution = closestResolution.getClippingVector();
			Vector rawDistance = closestResolution.getDistanceVector();
			
			depthX = resolution.getX();
			depthY = resolution.getY();
			
			System.out.println("Clipping resolution chosen "+ depthX +" , "+ depthY );

			
			//Resolution will not resolve

			
			//Resolution will resolve
			
			
			System.out.println("Snapping entity by "+(int)depthX + " , " + (int)depthY );
			entityPrimary.setX( entityPrimary.getDeltaX() + (int)depthX  );
			entityPrimary.setY( entityPrimary.getDeltaY()  + (int)depthY );
	
			
			/*if ( closestResolution.FeaturePrimary().debugIsVertex() ){
				collidingPrimary.applyPointMomentum( 
						new Vector(entityPrimary.getDX(),entityPrimary.getDY()), 
						closestResolution.FeaturePrimary().getP1() 
					);	
			}*/
			
			//TESTING ANGLE SNAPPING
			
			if ( closestResolution.FeaturePrimary().debugIsVertex() ){
				
				double angle =  ((Side)closestResolution.FeatureSecondary()).getSlopeVector().unitVector().calculateAngleFromVector();
				
				System.out.println("Snapping angle to "+(float)(angle*180/Math.PI ));
				((EntityRotationalDynamic)entityPrimary).setAngleInRadians( (float)angle );
			}
			
			//###
			
			if ( depthX != 0){ 
				System.out.println(" Clamping DX");
				entityPrimary.setDX(0);
			}
			if ( depthY != 0){ 
				System.out.println(" Clamping DY");
				entityPrimary.setDY(0);
			}
			
		}
		
		else { 
			
			entityPrimary.setColliding(true); //MOVE TO RESOLVED UPDATE CLASS JUST LIKE RESOLUTION EVENT

			if ( closestResolution.FeaturePrimary().debugIsSide() ){
				Side surface = (Side)closestResolution.FeaturePrimary();
				Vector playerDP = new Vector( entityPrimary.getDX(), entityPrimary.getDY() );
				
					if ( entityPrimary.getDX() + entityPrimary.getDY() != 0 )
						friction.setVector(   surface.unitVector().multiply( playerDP.unitVector().multiply(0.1) )   );

			}
			
			
			
			triggerResolutionEvent( closestResolution ); 
		}
	    
		 
	}
	
	protected class ResolutionEvent extends ResolutionState{
		
		@Override
		protected void triggerEvent( Resolution resolution ) { //One time event upon resolution of collision
			
			collisionDebugTag = "("+resolution.FeaturePrimary()+" of "+entityPrimary.toString()+") contacting ("+
					resolution.FeatureSecondary()+" of "+entitySecondary.toString()+")";
			
			if (resolution.FeatureSecondary().debugIsSide()){ 
				Vector slope = ((Side)resolution.FeatureSecondary()).getSlopeVector();
				Vector normal = slope.normal().unitVector().scaledBy( -0.2 );
				
				Vector test = new Vector( 0 , 0.2 ).projectedOver( slope.normal().unitVector() ).inverse() ;
				
				System.out.println( " Slope " + slope + " Normal "+test );
				normalForce.setVector( test );
			}
			else
				normalForce.setVector( 0 , -0.2 );
			
			System.out.println("\n### Collision Resolved, event on "+ entityPrimary +" triggered: "+resolution.FeaturePrimary().collisionEvent +" =================\n"  );
			
			resolution.FeaturePrimary().collisionEvent.run( resolution.FeaturePrimary() , resolution.FeatureSecondary() );
			
		}

	}
		
	@Override
	protected void triggerResolutionEvent( Resolution resolution ) { 
					
		this.resolutionState.triggerEvent( resolution );
		this.currentResolution = resolution ;
		this.resolutionState = ResolvedState.resolved();
			
	}
	
	/*private boolean resolutionsAreEqual( Resolution newer, Resolution current ){
		
		
		
	}*/

	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		
		collidingPrimary.onLeavingCollisionEvent();
		collidingSecondary.onLeavingCollisionEvent();
		
		entityPrimary.setColliding(false); // unset entity collision flag. 
		entityPrimary.removeForce(normalForce.getID());              //turn gravity back on
		entityPrimary.removeForce(friction.getID());     //remove friction
		
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
	    		else
	    			System.out.println( "Axis Dropped" );

	    	}
	    	
		}
    	 
    	
    	
    	double penetrationX = 0;
    	double penetrationY = 0;

    	Resolution closestResolution = null;
    	
    	//RESOLUTION LOGIC checks all penetration vectors and finds best resolution (currently lowest)
    	if (penetrations.size() > 0){
    		
    		penetrationX =  ( penetrations.get(0).getClippingVector().getX() ); //condense
    		penetrationY =  ( penetrations.get(0).getClippingVector().getY() ); //<
    		closestResolution = penetrations.get(0); 					  //<
    			
	    	for ( int i = 0 ; i < penetrations.size() ; i++ ){ //can start at 1
	    		Vector vector = penetrations.get(i).getClippingVector(); //vector component of resolution

	    		//if ( vectorOpposesVelocity( vector ) ) {
	    		
	    			//Keep lowest
		    		if ( (vector.getX()*vector.getX() + vector.getY()*vector.getY())
		    				< ( penetrationX*penetrationX + penetrationY*penetrationY )
		    			){

		    			penetrationX =  ( penetrations.get(i).getClippingVector().getX() ); //condense
		        		penetrationY =  ( penetrations.get(i).getClippingVector().getY() ); //<

		    			closestResolution = penetrations.get(i);
		    			
		    			//System.out.println( i + " possible lesser "+vector.getX() + " , "+ vector.getY());
		    		}
		    		else {
		    			//System.out.println( i + " rejected greater "+vector.getX() + " , "+ vector.getY());
		    		}
	    		//}
	    		//else {
	    		//	System.out.println( i + " rejected velocity "+vector.getX() + " against " + entityPrimary.getDX() +
	    		//			" , "+ vector.getY() + " against " + entityPrimary.getDY() );
	    					
	    		//}
	    		
	    	}
	    	//System.out.println( "Accepted "+ closestResolution.getClippingVector().getX() + " , " + closestResolution.getClippingVector().getY());
    	}
    	
    	
    	/*if (penetrationX == 0 && penetrationY == 0 ){ //Passed to updateCollision() for collisions where side is flush
    		//System.out.println(" Null resolution 0,0 ");
    		return new Resolution(
    					closestResolution.FeaturePrimary(),
    					closestResolution.FeatureSecondary(),
    					null,
    					new Vector( distanceX , distanceY )
    				);
    	}
    	else {*/
    		//return new Point(penetrationX,penetrationY); //return chosen best resolution
    		return closestResolution;
    		
    	//}

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
	    
	    Boundary statBounds = collidingSecondary.getBoundaryLocal() ;
	    Boundary playerBoundsDelta = collidingPrimary.getBoundaryLocal() ;
	    Boundary playerBounds = collidingPrimary.getBoundaryDelta() ;
	    
	    double deltaX = entityPrimary.getDeltaX() ;
	    double deltaY = entityPrimary.getDeltaY() ;
	    
	    Point2D playerCenterDelta = new Point2D.Double(deltaX, deltaY);
	    Point2D playerCenter = new Point2D.Double(entityPrimary.getX(), entityPrimary.getY());
	    Point2D statCenter = new Point2D.Double(stat.getX(), stat.getY());
		
		
		Line2D axis = statBounds.getSeparatingAxis(separatingSide); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
   
	    
		Vertex[] statOuterVertices= statBounds.getFarthestVertices(playerBounds,axis);
    	Vertex[] playerOuterVertices= playerBounds.getFarthestVertices(statBounds,axis);
	    																					// [0] needs to be for loop
	    Vertex[] statInnerVertices = statBounds.farthestVerticesFromPoint( statOuterVertices[0] , axis ); 
	    Vertex[] playerInnerVertices = playerBounds.farthestVerticesFromPoint( playerOuterVertices[0] , axis );

	    
	    
	    Vertex[] statOuter= statBounds.getFarthestVertices(playerBounds,axis);
    	Vertex[] playerOuter= playerBounds.getFarthestVertices(statBounds,axis);

    	Vertex[] nearStatCorner = statBounds.farthestVerticesFromPoint( statOuter[0] , axis ); //merge below
    	Vertex[] nearPlayerCorner = playerBounds.farthestVerticesFromPoint( playerOuter[0] , axis );
    	
    	Vertex farStatCorner = statBounds.farthestVerticesFromPoint(nearStatCorner[0] , axis)[0];
    	Vertex farPlayerCorner = playerBounds.farthestVerticesFromPoint(nearPlayerCorner[0] , axis)[0];
    	
    	Point2D centerStat = statOuter[0].getCenter(nearStatCorner[0]);
    	Point2D centerPlayer = playerOuter[0].getCenter(nearPlayerCorner[0]);

    	Line2D centerDistance = new Line2D.Double( centerPlayer , centerStat );
    	Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);
	    
	    //CLOSEST FEATURE
	    
	    
	    
	    //
	    
    	Line2D playerHalf = new Line2D.Float( 
    			playerBoundsDelta.getProjectionPoint(centerPlayer,axis) ,
    			playerBoundsDelta.getProjectionPoint(nearPlayerCorner[0].toPoint(),axis)
    			);
    	Line2D statHalf = new Line2D.Float( 
    			statBounds.getProjectionPoint(centerStat,axis) ,
    			statBounds.getProjectionPoint(nearStatCorner[0].toPoint(),axis) 
    			);
		
		
		double centerDistanceX = centerProjection.getX1() -  centerProjection.getX2()  ;
		double centerDistanceY = centerProjection.getY1() -  centerProjection.getY2()  ;
		
		double playerProjectionX = (playerHalf.getX1() -  playerHalf.getX2());
		double playerProjectionY = (playerHalf.getY1() -  playerHalf.getY2());
		
		double statProjectionX = (statHalf.getX2() -  statHalf.getX1());
		double statProjectionY = (statHalf.getY2() -  statHalf.getY1());
		
		double penetrationX = 0;
		double penetrationY = 0;
		
		// Get penetration vector


		
		if (centerDistanceX>0){
			penetrationX = playerProjectionX + statProjectionX - centerDistanceX+1 ;
			

		}
    	else if (centerDistanceX<0){
    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX-1;
    		

    	}
    	else {
    		penetrationX = Math.abs(playerProjectionX) + Math.abs(statProjectionX);
    	}

		
    	if (centerDistanceY>0){
    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY+1 ;	
    		
 
    	}
    	else if (centerDistanceY<0){
    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY-1 ;	
    		

    	}
    	else 
    		penetrationY = Math.abs(playerProjectionY) + Math.abs(statProjectionY);

    	
		double rawDistanceX = penetrationX;
		double rawDistanceY = penetrationY; //Store raw distances before clamping
		
		
		if ( rawDistanceX*Math.signum(centerDistanceX) < -2 || rawDistanceY*Math.signum(centerDistanceY) < -2 )
			this.isComplete = true;
		
		if ( penetrationX * centerDistanceX < 0 ) //
			
				penetrationX = 0;
	
		if ( penetrationY * centerDistanceY < 0 ) // 
			
				penetrationY = 0;
		
		
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
					new Vector( penetrationX , penetrationY ),
					new Vector( rawDistanceX , rawDistanceY )
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
	
	public String toString(){
		//return String.format("%s",collisionName);
		return "CollisionSAT: "+collisionDebugTag;
	}
	

}
