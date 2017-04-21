package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import engine.*;
import entities.*;
import entityComposites.Collider;
import entityComposites.EntityStatic;
import physics.Collision;
import sprites.RenderingEngine;

public class CollisionDynamicStatic extends Collision {
	
	RenderingEngine debugRenderer;
	
	boolean isComplete = false;
	
	private Force normalForce;
	private Force friction;
	
	private Resolver resolver = new ResolverSAT_1();
	private OverlayComposite overlay;
	
	private ResolutionEvent resolutionEvent = new ResolutionEvent();
	
	public CollisionDynamicStatic(Collider collidable1, Collider collidable2 , CollisionEngine ownerEngine){
		
		super( collidable1.getOwnerEntity() , collidable2.getOwnerEntity() , ownerEngine);
		
		entityPrimary = collidable1.getOwnerEntity();
		entitySecondary = collidable2.getOwnerEntity();
		
		collidingPrimary = collidable1; // TAKE COLLIDABLE IN COSNTRUCTOR INSTEAD OF ENTITY
		collidingSecondary = collidable2;
		
		debugRenderer = ownerEngine.getBoard().renderingEngine;
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		
		overlay = ((BoardAbstract)this.ownerEngine.getBoard()).renderingEngine.quickAddOverlay( ((Overlay)resolver) );
		
		this.resolutionState = resolutionEvent;
		
		this.normalForce = entityPrimary.getTranslationComposite().addForce( new Vector( 0 , 0 ) );
		this.friction = entityPrimary.getTranslationComposite().addForce( new Vector( 0 , 0 ) );
		
		//updateCollision(); //Run math for first time OPTIMIZE, Add new code block for first time math

		System.out.print("\n=============== Collision Start between "+entityPrimary + " and " + entitySecondary + " ===============");
		
		// Things like bullets won't need to go any futher than the initial method
		
		// Later on events will go here (damage, triggering, etc)
	}
	
	@Override
	public void updateCollision(){ 
		
		Resolution closestResolution = getClosestResolution();

		if ( //NOT RESOLVING ON SLOPE, PROBLEM IN CLIPPINMG VECTOR
				(	/*(int)closestResolution.getDistanceVector().getY()-(int)entityPrimary.getDY() != 0  
					&&*/ (int)closestResolution.getClippingVector().getY() != 0 
				)
				||
				(	/*(int)closestResolution.getDistanceVector().getX()-(int)entityPrimary.getDX() != 0
					&&*/ (int)closestResolution.getClippingVector().getX() != 0 
				)
				
		) { //Primary Entity is clipping by closestResolution.vector() 
					
			entityPrimary.getTranslationComposite().setColliding(false);
			System.out.println( "\n[ "+closestResolution.FeaturePrimary() + " on " + entityPrimary +
					" ] colliding with [ " + closestResolution.FeatureSecondary() + " on " + entitySecondary
					+" ]");
			
			Vector resolution = closestResolution.getClippingVector();
			Vector rawDistance = closestResolution.getDistanceVector();
			
			depthX = resolution.getX();
			depthY = resolution.getY();
			
			System.out.print("Snapping entity by "+ depthX +" , "+ depthY + " ... ");

			
			entityPrimary.setX( entityPrimary.getTranslationComposite().getDeltaX() + (int)depthX  );
			entityPrimary.setY( entityPrimary.getTranslationComposite().getDeltaY()  + (int)depthY );
			
			// NO NEED TO CHECK VERTEX ANYMORE
			if ( closestResolution.FeaturePrimary().debugIsVertex() && !closestResolution.FeatureSecondary().debugIsVertex()){

				closestResolution.FeaturePrimary().getEvent().run(closestResolution.FeaturePrimary(), closestResolution.FeatureSecondary() );
			}
			
			//###
			
			if ( depthX != 0){ 
				System.out.print("Clamping DX ... ");
				entityPrimary.getTranslationComposite().setDX(0);
				entityPrimary.getTranslationComposite().clipDX((int)depthX);
				//entityPrimary.clipAccX((int)depthX);
			}
			if ( depthY != 0){ 
				System.out.print("Clamping DY ... ");
				entityPrimary.getTranslationComposite().setDY(0);
				//entityPrimary.clipDY((int)depthY);
				//entityPrimary.clipAccY((int)depthX);
			}
			
			
		}
		
		else { 
			
			entityPrimary.getTranslationComposite().setColliding(true); //MOVE TO RESOLVED UPDATE CLASS JUST LIKE RESOLUTION EVENT

			if ( closestResolution.FeatureSecondary().debugIsSide() ){
				Vector surface = ((Side)closestResolution.FeatureSecondary()).getSlopeVector();
				Vector playerDP = new Vector( entityPrimary.getTranslationComposite().getDX(), entityPrimary.getTranslationComposite().getDY() );

				//friction.setVector(   playerDP.projectedOver( surface.unitVector() ).multiply(0.1).inverse()   );
				double frictionCoefficient = normalForce.force.getLength() * 0.5 ;
				
				friction.setVector( playerDP.inverse().signVector().multiply( surface.unitVector().abs().multiply( frictionCoefficient ) ).projectedOver(surface) );

			}
			else{
				//System.out.println("DROPPED SIDE");
			}
			
			
			
			triggerResolutionEvent( closestResolution ); 
		}
	    
		 
	}
	
	protected class ResolutionEvent extends ResolutionState{ //ONE TIME EVENT THAT TRIGGERS UPON RESOLUTION
		
		@Override
		protected void triggerEvent( Resolution resolution ) { 
			
			collisionDebugTag = "("+resolution.FeaturePrimary()+" of "+entityPrimary.name+") contacting ("+
					resolution.FeatureSecondary()+" of "+entitySecondary.name+")";
			
			if ( resolution.FeatureSecondary().debugIsSide() ){ 
				Vector slope = ((Side)resolution.FeatureSecondary()).getSlopeVector();
				Vector normal = slope.normalRight().unitVector().scaledBy( -0.2 );
				
				Vector test = new Vector( 0 , 0.2 ).projectedOver( slope.normalRight().unitVector() ).inverse() ;
				
				System.out.println( " Slope " + slope + " Normal "+test );
				normalForce.setVector( test );
			}
			else{

				System.out.println("[ Collision Resolved, event '"+resolution.FeaturePrimary().getEvent()+ "' on "+ entityPrimary +" triggered ] "  );
			}
			System.out.println("Triggering "+resolution.FeaturePrimary().getEvent() + " of "+ resolution.FeaturePrimary() );
			//Trigger Boundary Collision Event on relevant side/vertex
			resolution.FeaturePrimary().getEvent().run( resolution.FeaturePrimary() , resolution.FeatureSecondary() );
		}

	}
		
	@Override
	protected void triggerResolutionEvent( Resolution resolution ) { 
					
		this.resolutionState.triggerEvent( resolution );
		this.resolutionState = ResolvedState.resolved();
			
	}
	
	/*private boolean resolutionsAreEqual( Resolution newer, Resolution current ){
		
		
		
	}*/

	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		
		collidingPrimary.onLeavingCollisionEvent();
		collidingSecondary.onLeavingCollisionEvent();
		
		
		entityPrimary.getTranslationComposite().removeForce(normalForce.getID());              //turn gravity back on
		entityPrimary.getTranslationComposite().removeForce(friction.getID());     //remove friction
		
		//Remove collision from involved entities lists
		collidingPrimary.removeCollision( entityPairIndex[0] );
		collidingSecondary.removeCollision(entityPairIndex[1] );

		overlay.remove();
	}
	
	/* ######################
	 * # CORE FUNCTIONALITY #
	 * ######################
	 */
	
	//Completion Condition
	@Override
	public boolean isComplete(){ // Check if entities are no longer colliding //OPTIMIZATION - HANDLE AS EVENT RATEHR THAN CHECK
		//CHECK FOR COLLISIONS IS BEING DOUBLE CHECKED IN COLLISION ENGINE
		
		return isComplete;
	}
	
	//Resolution calculation
	private Resolution getClosestResolution() { 
		//System.out.println("Checking best resolution"); 
		ArrayList<Resolution> penetrations = new ArrayList<>();
    	
    	 
		Line2D[] separatingSides = Boundary.getSeparatingSidesBetween(collidingPrimary.getBoundaryDelta() , collidingSecondary.getBoundaryLocal() );
		
		for ( Line2D side : separatingSides ){
			
		    	penetrations.add( this.resolver.resolveAxis( side ));
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

    	return closestResolution;

	}

	
	private abstract class Resolver{
		abstract Resolution resolveAxis( Line2D separatingSide);
	}
	
	private class ResolverSAT_1 extends Resolver implements Overlay{
		
		ArrayList<Line2D> axes = new ArrayList<Line2D>();
		ArrayList<Line2D> playerProj = new ArrayList<Line2D>();
		ArrayList<Line2D> statProj = new ArrayList<Line2D>();
		
		ArrayList<Point2D> unshifted = new ArrayList<Point2D>();
		
		
		ArrayList<Point2D> raw = new ArrayList<Point2D>();
		
		ArrayList<Point2D> crosses = new ArrayList<Point2D>();
		
		
		ArrayList<Point2D> clip = new ArrayList<Point2D>();
		ArrayList<Point2D> clipPos = new ArrayList<Point2D>();
		
		ArrayList<BoundaryFeature> closestSides = new ArrayList<BoundaryFeature>();
		
		public Resolution resolveAxis( Line2D separatingSide){
		
		    EntityStatic stat = entitySecondary;
		    
		    Boundary statBounds = collidingSecondary.getBoundaryLocal() ;
		    Boundary playerBounds = collidingPrimary.getBoundaryDelta() ;
		    
		    double deltaX = entityPrimary.getTranslationComposite().getDeltaX() ;
		    double deltaY = entityPrimary.getTranslationComposite().getDeltaY() ;
		    
		    //Point2D playerCenterDelta = new Point2D.Double(deltaX, deltaY);
		    //Point2D statCenter = new Point2D.Double(stat.getX(), stat.getY());
			
			
			Line2D axis = BoundaryPolygonal.getSeparatingAxis(separatingSide); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
			
			//DRAW AXIS
			axes.add(axis);
	    	//
			
			//BoundaryVertex[] statOuterVertices= statBounds.getFarthestVertices(playerBounds,axis);
	    	//BoundaryVertex[] playerOuterVertices= playerBounds.getFarthestVertices(statBounds,axis);
		    																					// [0] needs to be for loop
		    //BoundaryVertex[] statInnerVertices = statBounds.farthestVerticesFromPoint( statOuterVertices[0] , axis ); 
		    //BoundaryVertex[] playerInnerVertices = playerBounds.farthestVerticesFromPoint( playerOuterVertices[0] , axis );

		    //Point2D[] statOuter = statBounds.getFarthestPoints(playerBounds,axis);
		    //Point2D[] playerOuter = playerBounds.getFarthestPoints(statBounds,axis);
			
			//get singular pair of outermost points between both boundaries ( no duplicates are needed for the collision math )
			Point2D[] outerPoints = Boundary.getFarthestPointsBetween(playerBounds, statBounds, axis); 
	
	    	//BoundaryVertex[] nearStatCorner = statBounds.farthestVerticesFromVertex( statOuter[0] , axis ); //merge below
	    	//BoundaryVertex[] nearPlayerCorner = playerBounds.farthestVerticesFromVertex( playerOuter[0] , axis );
		    
			//with the outer points, calculate the inner corners on each boundary. 
			//Below, duplicates are taken on Vertex corners to detect sides for events.  
		    BoundaryVertex[] nearStatCorner = statBounds.farthestVerticesFromPoint( outerPoints[1] , axis ); 
		    BoundaryVertex[] nearPlayerCorner = playerBounds.farthestVerticesFromPoint( outerPoints[0] , axis );
	    	//Below, duplicates are not needed for math purposes
	    	Point2D nearStatPoint = statBounds.farthestPointFromPoint( outerPoints[1] , axis );
	    	Point2D nearPlayerPoint = playerBounds.farthestPointFromPoint( outerPoints[0] , axis ); //OPTIMIZE MERGE INTO CLASS WITH ABOVE
	    	
	    	crosses.add(nearPlayerPoint);
	    	
	    	//BoundaryVertex farStatCorner = statBounds.farthestVerticesFromPoint(nearStatCorner[0] , axis)[0];
	    	//BoundaryVertex farPlayerCorner = playerBounds.farthestVerticesFromPoint(nearPlayerCorner[0] , axis)[0];
	    	
	    	//Point2D centerStat = statOuter[0].getCenter(nearStatCorner[0]);
	    	//Point2D centerPlayer = playerOuter[0].getCenter(nearPlayerCorner[0]);
	    	
	    	//Calculate center of each boundary, on this axis
	    	Point2D centerStat = BoundaryCorner.getCenter(  nearStatPoint , outerPoints[1] );
	    	Point2D centerPlayer = BoundaryCorner.getCenter( nearPlayerPoint , outerPoints[0] );
	
	    	Line2D centerDistance = new Line2D.Double( centerPlayer , centerStat );
	    	Line2D centerProjection = Boundary.getProjectionLine(centerDistance, axis);

		    //Construct half projection lines for both boundaries. Overlap between these two lines represents the penetration distance
	    	//on this axis.
	    	Line2D playerHalf = new Line2D.Float( 
	    			Boundary.getProjectionPoint( centerPlayer , axis ) ,
	    			Boundary.getProjectionPoint( nearPlayerPoint , axis )
	    			);
	    	Line2D statHalf = new Line2D.Float( 
	    			Boundary.getProjectionPoint( centerStat , axis) ,
	    			Boundary.getProjectionPoint( nearStatPoint , axis) 
	    			);
			
	    	playerProj.add(playerHalf);
	    	statProj.add(statHalf);
			
			double centerDistanceX = centerProjection.getX1() -  centerProjection.getX2()  ;
			double centerDistanceY = centerProjection.getY1() -  centerProjection.getY2()  ;
			
			double playerProjectionX = (playerHalf.getX1() -  playerHalf.getX2());
			double playerProjectionY = (playerHalf.getY1() -  playerHalf.getY2());
			
			double statProjectionX = (statHalf.getX2() -  statHalf.getX1());
			double statProjectionY = (statHalf.getY2() -  statHalf.getY1());
			
			double penetrationX = 0;
			double penetrationY = 0;
			
			// Get penetration vector
	
			double unshiftedX;
			double unshiftedY;
			
			double rawDistanceX;
			double rawDistanceY;
	
			
			if (centerDistanceX>1){
				unshiftedX = playerProjectionX + statProjectionX - centerDistanceX +2;
				penetrationX = unshiftedX-1;
				rawDistanceX = penetrationX;
				
				if (penetrationX < 0)
	    			penetrationX=0;
				if (unshiftedX < 0)
	    			unshiftedX=0;
			}
	    	else if (centerDistanceX<-1){
	    		unshiftedX = playerProjectionX + statProjectionX - centerDistanceX -2;
	    		penetrationX = unshiftedX+1;
				rawDistanceX = -penetrationX;
				
				unshiftedX = -unshiftedX;
				
				if (penetrationX > 0)
	    			penetrationX=0;
				if (unshiftedX < 0)
	    			unshiftedX=0;
				
	    	}
	    	else {
	    		unshiftedX = Math.abs(playerProjectionX) + Math.abs(statProjectionX);
	    		penetrationX = unshiftedX;
	    		rawDistanceX = unshiftedX;
	    	}
	
			// ------------- Y
			
	    	if (centerDistanceY>1){
	    		unshiftedY = playerProjectionY + statProjectionY - centerDistanceY+2;	
	    		penetrationY = unshiftedY-1;
	    		rawDistanceY = unshiftedY;
	    		
	    		if (penetrationY < 0)
	    			penetrationY=0;
	    		if (unshiftedY < 0)
	    			unshiftedY=0;
	    	}
	    	else if (centerDistanceY<-1){ // player on top of platform
	    		unshiftedY = playerProjectionY + statProjectionY - centerDistanceY-2;
	    		penetrationY = unshiftedY+1;
	    		rawDistanceY = -unshiftedY;
	    		
	    		unshiftedY = -unshiftedY;
	    		
	    		if (penetrationY > 0)
	    			penetrationY=0;
	    		if (unshiftedY < 0)
	    			unshiftedY=0;
	    	}
	    	else {
	    		unshiftedY = Math.abs(playerProjectionY) + Math.abs(statProjectionY);
	    		penetrationY = unshiftedY;
	    		rawDistanceY = unshiftedY;
	    	}
			
			
	    	unshifted.add( new Point2D.Double( unshiftedX , unshiftedY ) );
	    	clip.add( new Point2D.Double( unshiftedX , unshiftedY ) );
	    	clipPos.add( playerHalf.getP1() );
	    	

		
			//g2.draw( unshiftedX + " , " + unshiftedY , (int)separatingSide.getX1(), (int)separatingSide.getY1());
			
	    	BoundaryFeature featurePrimary;
	    	BoundaryFeature featureSecondary;
	    	
	    	if (nearStatCorner != null){

	    		featureSecondary = nearStatCorner[0];
	
				if ( nearStatCorner.length > 1 ){ 
					featureSecondary = ((BoundaryCorner)nearStatCorner[0]).getSharedSide( ((BoundaryCorner)nearStatCorner[1]) );
		    	}
	    	}
	    	else{
	    		featureSecondary = new BoundaryGenericFeature();
	    	}
			
	    	if (nearPlayerCorner != null){
				featurePrimary = nearPlayerCorner[0];
				
				if ( nearPlayerCorner.length > 1 ){ 
					featurePrimary = ((BoundaryCorner)nearPlayerCorner[0]).getSharedSide( ((BoundaryCorner)nearPlayerCorner[1]) );
		    	}
	    	}
	    	else{
	    		featurePrimary = new BoundaryGenericFeature();
	    	}
			
			closestSides.add( featurePrimary );
			closestSides.add( featureSecondary );
			

			int square = 0;
			
			if ( (int)unshiftedX==square && (int)unshiftedY==square ){  // PENETRATION DISTANCE OUTSIDE THRESHOLD SO END COLLISION
				isComplete = true;
				System.out.println("Collision Dropped on " + (axis.getY2()-axis.getY1())/(axis.getX2()-axis.getX1()) +
						" "+rawDistanceX+ " - " +rawDistanceY );
				
				return new Resolution( 
						featurePrimary, //construct sides 
						featureSecondary, 
						new Vector( 0 , 0 ), 
						new Vector( 0 , 0 ) 
				);
			}
			else {

				//System.out.println(""+featurePrimary.toString()+" : "+featureSecondary.toString() );
				return new Resolution( 
						featurePrimary, //construct sides
						featureSecondary,
						new Vector( penetrationX , penetrationY ),
						new Vector( rawDistanceX , rawDistanceY )
				);
			}
		}

		@Override
		public void paintOverlay(Graphics2D g2, MovingCamera cam) {
			
			g2.setColor(Color.DARK_GRAY);
			
			Line2D[] buffer = new Line2D[ axes.size() ];
			axes.toArray(buffer);
			
			for ( Line2D axis : buffer )
	    		//cam.drawAxis(axis , new Point(300,300));
			
			axes.clear();
			g2.setColor(Color.GREEN);
			buffer = new Line2D[ playerProj.size() ];
			playerProj.toArray(buffer);
			for (Line2D player : buffer )
				cam.draw(player);
			
			playerProj.clear();
			g2.setColor(Color.RED);
			buffer = new Line2D[ statProj.size() ];
			statProj.toArray(buffer);
			for (Line2D stat : buffer )
				cam.draw(stat);
			
			statProj.clear();
			
			g2.setColor(Color.YELLOW);
			Point2D[] bufferP = new Point2D[ clip.size() ];
			clip.toArray(bufferP);
			for ( int i = 0 ; i < bufferP.length ; i++){
				cam.drawString( (int)bufferP[i].getX() + " , " + (int)bufferP[i].getY() , (int)clipPos.get(i).getX() , (int)clipPos.get(i).getY() );
			}
			
			clip.clear();
			clipPos.clear();
			
			g2.setColor(Color.YELLOW);
			Point2D[] buffer3 = new Point2D[ crosses.size() ];
			crosses.toArray(buffer3);
			for (Point2D statFeature : buffer3 )
				cam.drawCrossInWorld( (int)statFeature.getX() , (int)statFeature.getY() );
			
			crosses.clear();
			
			g2.setColor(Color.YELLOW);
			BoundaryFeature[] buffer4 = new BoundaryFeature[ closestSides.size() ];
			closestSides.toArray(buffer4);
			for (BoundaryFeature statFeature : buffer4 )
				if (statFeature.debugIsSide())
					cam.draw( ((Side)statFeature).toLine() );
			
			crosses.clear();
			
		}

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
