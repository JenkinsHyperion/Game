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
import entityComposites.TranslationComposite;
import entityComposites.TranslationCompositeActive;
import misc.DefaultCollisionEvent;
import physics.Collision;
import sprites.RenderingEngine;

public class VisualCollisionDynamicStatic extends Collision {
	
	RenderingEngine debugRenderer;
	
	private SeparatingAxisCollector axisCollector;
	
	boolean isComplete = false;
	
	private Force normalForce;
	private Force friction;
	
	private Resolver resolver = new ResolverSAT_1();
	
	private ResolutionEvent resolutionEvent = new ResolutionEvent();
	
	public VisualCollisionDynamicStatic(Collider collidable1, Collider collidable2 , SeparatingAxisCollector axisCollector , CollisionEngine ownerEngine){
		
		super( collidable1.getOwnerEntity() , collidable2.getOwnerEntity() , ownerEngine);
		
		this.axisCollector = axisCollector;
		
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
		
		this.resolutionState = resolutionEvent;
		
		this.normalForce = entityPrimary.getTranslationComposite().addForce( new Vector( 0 , 0 ) );
		this.friction = entityPrimary.getTranslationComposite().addForce( new Vector( 0 , 0 ) );
		//updateCollision(); //Run math for first time OPTIMIZE, Add new code block for first time math

		System.out.println(
				"\n\n=============== Collision Start between dynamic ["+entityPrimary + 
				"] and static [" + entitySecondary + "]s ==============="
				);
		
		// Things like bullets won't need to go any futher than the initial method
		
		// Later on events will go here (damage, triggering, etc)
	}
	
	@Override
	public void updateCollision(){ 

		Resolution closestResolution = getClosestResolution();
		TranslationComposite dynamic = entityPrimary.getTranslationComposite();
		
		if ( //TODO
				(	/*(int)closestResolution.getDistanceVector().getY()-(int)entityPrimary.getDY() != 0  
					&&*/ ( closestResolution.getClippingVector().getY() ) != 0 
				)
				||
				(	/*(int)closestResolution.getDistanceVector().getX()-(int)entityPrimary.getDX() != 0
					&&*/ closestResolution.getClippingVector().getX() != 0 
				)
				
		) { //Primary Entity is clipping by closestResolution.vector() 
					
			entityPrimary.getTranslationComposite().setColliding(false);
			System.out.println( "\n[ "+closestResolution.FeaturePrimary() + " on " + entityPrimary +
					" ] clipping with [ " + closestResolution.FeatureSecondary() + " on " + entitySecondary
					+" ]");
			
			Vector resolution = closestResolution.getClippingVector();
			
			depthX = resolution.getX();
			depthY = resolution.getY();
			
			//System.out.print("Snapping entity by "+ depthX +" , "+ depthY + " ... ");
			
			//entityPrimary.setX( entityPrimary.getDeltaX() + (int)depthX  );
			//entityPrimary.setY( entityPrimary.getDeltaY()  + (int)depthY );
			

			closestResolution.FeaturePrimary().getEvent().run(closestResolution.FeaturePrimary(), closestResolution.FeatureSecondary() );
			
			//TODO GET NORMAL FROM BOUDNARY FEATURE INSTEAD
			
			if ( closestResolution.FeatureSecondary().debugIsSide() ){
				Vector slope = ((Side)closestResolution.FeatureSecondary()).getSlopeVector().normalLeft();
				Vector test = new Vector(0,-0.2).projectedOver(slope);
				normalForce.setVector( test );
			}
			else if ( closestResolution.FeatureSecondary().debugIsVertex() ){
				
				Vector distance = new Vector(
					entityPrimary.getX() - closestResolution.FeatureSecondary().getP1().getX(),
					entityPrimary.getY() - closestResolution.FeatureSecondary().getP1().getY()
					).unitVector();
				
				normalForce.setVector( 0,-0.2 );
			}

				
				System.out.println("Will clip by "+ depthX +" , "+ depthY + " ... ");
				
				dynamic.clipDX(depthX);
				dynamic.clipDY(depthY);
			
		}
		
		else { 
			
			//entityPrimary.getTranslationComposite().setColliding(true); //MOVE TO RESOLVED UPDATE CLASS JUST LIKE RESOLUTION EVENT

			if ( closestResolution.FeatureSecondary().debugIsSide() ){
				Vector surface = ((Side)closestResolution.FeatureSecondary()).getSlopeVector();
				Vector playerDP = new Vector( entityPrimary.getTranslationComposite().getDX(), entityPrimary.getTranslationComposite().getDY() );
				//friction.setVector(   playerDP.projectedOver( surface.unitVector() ).multiply(0.1).inverse()   );
				double frictionCoefficient = normalForce.force.getLength() * 0.5 ;
				
				//friction.setVector( playerDP.inverse().signumVector().multiply( surface.unitVector().multiply( frictionCoefficient ) ).projectedOver(surface) );
				//Vector velocity = new Vector( dynamic.getDX(),dynamic.getDY() );
				
				//dynamic.setVelocityVector( velocity.projectedOver(surface) );
				
				
			}
			else{
				System.err.println("DROPPED SIDE");
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
				
				Vector test = new Vector(0,-0.2).projectedOver( slope.normalRight() );
				//normalForce.setVector( test );
				//normalForce.setVector( 0,-0.2 );
			}
			else{

				System.out.println("[ Collision Resolved, event '"+resolution.FeaturePrimary().getEvent()+ "' on "+ entityPrimary +" triggered ] "  );
			}
			
			
			System.out.println("Triggering "+resolution.FeaturePrimary().getEvent() + 
								" of "+ resolution.FeaturePrimary() + " on " + entityPrimary );
			//Trigger Boundary Collision Event on relevant side/vertex
			resolution.FeaturePrimary().getEvent().run( resolution.FeaturePrimary() , resolution.FeatureSecondary() );
			
			collidingPrimary.onCollisionEvent();
			collidingSecondary.onCollisionEvent();
			
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
    	
    	 
		//Line2D[] separatingSides = Boundary.getSeparatingSidesBetween( collidingPrimary.getBoundaryDelta() , collidingSecondary.getBoundaryLocal() );
		Line2D[] separatingSides = this.axisCollector.getSeparatingAxes( 
				collidingPrimary.getBoundaryDelta(), 
				collidingSecondary.getBoundaryLocal()
				);
		
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
	
	private class ResolverSAT_1 extends Resolver{
		
		public Resolution resolveAxis( Line2D separatingSide){
		
		    EntityStatic stat = entitySecondary;
		    
		    Boundary statBounds = collidingSecondary.getBoundaryLocal() ;
		    Boundary playerBounds = collidingPrimary.getBoundaryDelta() ;
		    
		    double deltaX = entityPrimary.getTranslationComposite().getDeltaX(entityPrimary) ;
		    double deltaY = entityPrimary.getTranslationComposite().getDeltaY(entityPrimary) ;
		    
		    //Point2D playerCenterDelta = new Point2D.Double(deltaX, deltaY);
		    //Point2D statCenter = new Point2D.Double(stat.getX(), stat.getY());
			
			
			Line2D axis = BoundaryPolygonal.getSeparatingAxis(separatingSide); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
			
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

			
	    	double centerDistanceX = (centerProjection.getX1() -  centerProjection.getX2()  );
	    	double centerDistanceY = (centerProjection.getY1() -  centerProjection.getY2()  );

	    	double playerProjectionX = (playerHalf.getX1() -  playerHalf.getX2());
	    	double playerProjectionY = (playerHalf.getY1() -  playerHalf.getY2());

	    	double statProjectionX = (statHalf.getX2() -  statHalf.getX1());
	    	double statProjectionY = (statHalf.getY2() -  statHalf.getY1()); 

	    	double penetrationX = 0;
	    	double penetrationY = 0;  
	    	
	    	double shiftedX = 0;
	    	double shiftedY = 0;
	    	
	    	double distX = penetrationX;
	    	double distY = penetrationY;

	    	if (centerDistanceX>0){
	    		//centerDistanceX -= 1;
	    		penetrationX = ( playerProjectionX + statProjectionX - centerDistanceX +1 );
	    		shiftedX = penetrationX-1;
	    	}
	    	else if (centerDistanceX<0){
	    		//centerDistanceX += 1;  //NEEDS HIGHER LEVEL SOLUTION
	    		penetrationX = ( playerProjectionX + statProjectionX - centerDistanceX -1 );
	    		shiftedX = penetrationX+1;
	    	}
	    	else
	    		penetrationX = Math.abs(playerProjectionX) + Math.abs(statProjectionX);

	    	if (centerDistanceY>0){
	    		//centerDistanceY -= 1;
	    		penetrationY = ( playerProjectionY + statProjectionY - centerDistanceY+1 ); 
	    		shiftedY = penetrationY-1;
	    	}
	    	else if (centerDistanceY<0){
	    		//centerDistanceY += 1; 
	    		penetrationY =   ( playerProjectionY + statProjectionY - centerDistanceY-1 ); 
	    		shiftedY = penetrationY+1;
	    	}else
	    		penetrationY = Math.abs(playerProjectionY) + Math.abs(statProjectionY);
	    	
	    	distX = penetrationX;
	    	distY = penetrationY;
	    	
	    	if ( penetrationX * centerDistanceX < 0 ){ //SIGNS ARE NOT THE SAME
				penetrationX = 0;
	    	}
	    	if ( penetrationY * centerDistanceY < 0 ){
				penetrationY = 0;
	    	}

	    	if ( shiftedX * centerDistanceX > 0 ){ //SIGNS ARE NOT THE SAME
	    		shiftedX = 0;
	    	}
	    	if ( shiftedY * centerDistanceY > 0 ){
	    		shiftedY = 0;
	    	}
		
			//g2.draw( unshiftedX + " , " + unshiftedY , (int)separatingSide.getX1(), (int)separatingSide.getY1());
			
	    	BoundaryFeature featurePrimary = new BoundaryGenericFeature();
	    	BoundaryFeature featureSecondary = new BoundaryGenericFeature();
	    	
	    	if (nearStatCorner != null){

	    		featureSecondary = nearStatCorner[0];
	
				if ( nearStatCorner.length > 1 ){ 
					featureSecondary = ((BoundaryCorner)nearStatCorner[0]).getSharedSide( ((BoundaryCorner)nearStatCorner[1]) );
		    	}

	    	}
	    	else{
	    		//featureSecondary = new BoundaryGenericFeature();
	    	}
			
	    	if (nearPlayerCorner != null){
				featurePrimary = nearPlayerCorner[0];
				
				if ( nearPlayerCorner.length > 1 ){ 
					featurePrimary = ((BoundaryCorner)nearPlayerCorner[0]).getSharedSide( ((BoundaryCorner)nearPlayerCorner[1]) );
		    	}
	    	}
	    	else{
	    		//featurePrimary = new BoundaryGenericFeature();
	    	}
			

			final int square = 5;
			if ( shiftedX*shiftedX + shiftedY*shiftedY > square*square ){  // PENETRATION DISTANCE OUTSIDE THRESHOLD SO END COLLISION
				isComplete = true;
				System.out.println("Collision Dropped by (" +
						penetrationX+ " - " +penetrationY +")");
				
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
						new Vector( (penetrationX) , (penetrationY ) ),
						new Vector( (distX) , (distY ))
				);
			}
		}

	}
	
	public String toString(){
		//return String.format("%s",collisionName);
		return "CollisionSAT: "+collisionDebugTag;
	}
	

}
