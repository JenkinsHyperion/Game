package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import engine.*;
import entities.*;
import entityComposites.*;
import misc.DefaultCollisionEvent;
import physics.Collision;
import sprites.RenderingEngine;

public class VisualCollisionDynamicStatic extends Collision implements VisualCollision{
	
	RenderingEngine debugRenderer;
	
	private SeparatingAxisCollector axisCollector;
	
	boolean isComplete = false;
	
	private Force normalForce;
	private Force frictionForce;
	
	private Resolver resolver = new ResolverSAT_1();
	
	private ResolutionEvent resolutionEvent = new ResolutionEvent();
	
	public VisualCollisionDynamicStatic(Collider collidable1, Collider collidable2 , SeparatingAxisCollector axisCollector , RenderingEngine renderer){
		
		super( collidable1 , collidable2 );
		
		this.axisCollector = axisCollector;
		
		entityPrimary = collidable1.getOwnerEntity();
		entitySecondary = collidable2.getOwnerEntity();
		
		collidingPrimary = collidable1; // TAKE COLLIDABLE IN COSNTRUCTOR INSTEAD OF ENTITY
		collidingSecondary = collidable2;
		
		debugRenderer = renderer;
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		
		this.resolutionState = resolutionEvent;
		
		this.normalForce = entityPrimary.getTranslationComposite().addNormalForce( new Vector( 0 , 0 ) );
		this.frictionForce = entityPrimary.getTranslationComposite().addForce( new Vector( 0 , 0 ) );
		//updateCollision(); //Run math for first time OPTIMIZE, Add new code block for first time math

		System.out.println(
				"\n\n=============== Visual Dynamic Collision Start between dynamic ["+entityPrimary + 
				"] and static [" + entitySecondary + "] ==============="
				);
		
		// Things like bullets won't need to go any futher than the initial method
		
		// Later on events will go here (damage, triggering, etc)
	}
	@Override
	public void updateVisualCollision( MovingCamera camera , Graphics2D g2){ 

		Resolution closestResolution = getClosestResolution( camera , g2 );
		
		TranslationComposite dynamicPrimary = entityPrimary.getTranslationComposite(); //OPTIMIZE See about moving to initialization
		AngularComposite angularPrimary = entityPrimary.getAngularComposite();

		final Vector unitNormal = closestResolution.getSeparationVector().unitVector();
		
		final Vector normal = unitNormal.multiply(-0.2);
		
		final double tangentalVelocity = dynamicPrimary.getVelocityVector().projectedOver( normal.normalRight() ).getMagnitude();
		
		final double distanceA = entityPrimary.getPosition().distance(entitySecondary.getPosition()) ;
		
		final double centripetalForce = tangentalVelocity*tangentalVelocity/ ( distanceA -80 );
		
		//testing for centripetal acceleration
		final Vector rotatingNormal = normal.add( unitNormal.multiply( centripetalForce ) );
		
		normalForce.setVector( rotatingNormal );
		
		//System.err.println(dynamicPrimary.getVelocityVector());
		
		if ( 
				
					 closestResolution.getClippingVector().getMagnitude() > 2
	
				
		) { //CLIPPING UPDATING
					
			//entityPrimary.getTranslationComposite().setColliding(false);
			System.out.println( "\n[ "+closestResolution.FeaturePrimary() + " on " + entityPrimary +
					" ] clipping with [ " + closestResolution.FeatureSecondary() + " on " + entitySecondary
					+" ]");
			
			Vector resolution = closestResolution.getClippingVector();
			
			depthX = resolution.getX();
			depthY = resolution.getY();

			/*closestResolution.FeaturePrimary().getEvent().run(
					closestResolution.FeaturePrimary(),
					closestResolution.FeatureSecondary(),
					closestResolution.getSeparationVector().unitVector()
					);*/
			
			//TODO GET NORMAL FROM BOUDNARY FEATURE INSTEAD

			System.out.println("Will clip by "+ depthX +" , "+ depthY + " ... ");
			
			entityPrimary.setPos(
					dynamicPrimary.getDeltaX(entityPrimary) + depthX,
					dynamicPrimary.getDeltaY(entityPrimary) + depthY
					);
			
			//dynamicPrimary.halt();
			dynamicPrimary.setVelocityVector( dynamicPrimary.getVelocityVector().projectedOver(unitNormal.normalLeft()) );


			//normalForce.setVector( 0,-0.2 );
			//normalForce.setVector( new Vector(0,-0.2) );
		}
		
		else { //RESOLVED UPDATING
			
			//entityPrimary.getTranslationComposite().setColliding(true); //MOVE TO RESOLVED UPDATE CLASS JUST LIKE RESOLUTION EVENT
			
			if ( closestResolution.FeatureSecondary().debugIsSide() ){
				Vector slope = ((Side)closestResolution.FeatureSecondary()).getSlopeVector().unitVector();

				frictionForce.setVector( dynamicPrimary.getVelocityVector().projectedOver(slope).inverse().multiply(0.1) );
				
			}
			else if ( closestResolution.FeatureSecondary().debugIsVertex() ){
				
				Vector distance = new Vector(
					entityPrimary.getX() - closestResolution.FeatureSecondary().getP1().getX(),
					entityPrimary.getY() - closestResolution.FeatureSecondary().getP1().getY()
					).unitVector();

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
			
			
			System.out.println("Triggering ["+resolution.FeaturePrimary().getEvent() + 
								"] of ["+ resolution.FeaturePrimary() + "] on [" + entityPrimary+"]" );
			//Trigger Boundary Collision Event on relevant side/vertex
			resolution.FeaturePrimary().getEvent().run( 
					resolution.FeaturePrimary() , 
					resolution.FeatureSecondary(),
					resolution.getSeparationVector().unitVector()
					);
			
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
		
		
		entityPrimary.getTranslationComposite().removeNormalForce(normalForce.getID());              //turn gravity back on
		entityPrimary.getTranslationComposite().removeForce(frictionForce.getID());     //remove friction
		
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
	private Resolution getClosestResolution( MovingCamera camera, Graphics2D g2) { 
		//System.out.println("Checking best resolution"); 
		ArrayList<Resolution> penetrations = new ArrayList<>();
    	
    	 
		//Line2D[] separatingSides = Boundary.getSeparatingSidesBetween( collidingPrimary.getBoundaryDelta() , collidingSecondary.getBoundaryLocal() );
		Line2D[] separatingSides = this.axisCollector.getSeparatingAxes( 
				collidingPrimary.getBoundary(), 
				collidingSecondary.getBoundary(),
				camera,g2
				);
		
		for ( Line2D side : separatingSides ){
		    	penetrations.add( this.resolver.resolveAxis( side , camera, g2 ));
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
		abstract Resolution resolveAxis( Line2D separatingSide , MovingCamera camera, Graphics2D g2);
	}
	
	private class ResolverSAT_1 extends Resolver{
		
		public Resolution resolveAxis( Line2D separatingSide , MovingCamera camera, Graphics2D g2){
			
		    EntityStatic stat = entitySecondary;
		    
		    Boundary statBoundsRelative = collidingSecondary.getBoundary() ;
		    Boundary playerBoundsRelative = collidingPrimary.getBoundary() ;
		    
		    //Boundary statBounds = collidingSecondary.getBoundaryLocal() ;
		    //Boundary playerBounds = collidingPrimary.getBoundaryLocal() ;
		    
		    final double deltaX = entityPrimary.getTranslationComposite().getDeltaX(entityPrimary) ;
		    final double deltaY = entityPrimary.getTranslationComposite().getDeltaY(entityPrimary) ;
		    final Point deltaPosition = new Point( (int)deltaX , (int)deltaY );

			final Line2D axis = BoundaryPolygonal.getSeparatingAxis(separatingSide); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
			
			//final Point2D[] outerPoints = Boundary.getFarthestPointsBetween(playerBounds ,statBounds, axis); 
			
			final Point2D[] outerPoints = Boundary.getFarthestPointsBetween(
					entityPrimary,playerBoundsRelative, entitySecondary,statBoundsRelative, axis
					); 

		    //BoundaryVertex[] nearStatCorner = statBounds.farthestVerticesFromPoint( outerPoints[1] , axis ); 
		    //BoundaryVertex[] nearPlayerCorner = playerBounds.farthestVerticesFromPoint( outerPoints[0] , axis );
		    
	    	//Point2D nearStatPoint = statBoundsRelative.farthestPointFromPoint( outerPoints[1] , axis );
	    	//Point2D nearPlayerPoint = playerBoundsRelative.farthestPointFromPoint( outerPoints[0] , axis ); //OPTIMIZE MERGE INTO CLASS WITH ABOVE
	    	
	    	Point2D nearStatPoint = statBoundsRelative.farthestLocalPointFromPoint( 
	    			entitySecondary.getPosition(), outerPoints[1], axis 
	    			);
	    	Point2D nearPlayerPoint = playerBoundsRelative.farthestLocalPointFromPoint( 
	    			deltaPosition, outerPoints[0], axis 
	    			);
	    	
	    	//BoundaryVertex farStatCorner = statBounds.farthestVerticesFromPoint(nearStatCorner[0] , axis)[0];
	    	//BoundaryVertex farPlayerCorner = playerBounds.farthestVerticesFromPoint(nearPlayerCorner[0] , axis)[0];
	    	
	    	//Point2D centerStat = statOuter[0].getCenter(nearStatCorner[0]);
	    	//Point2D centerPlayer = playerOuter[0].getCenter(nearPlayerCorner[0]);
	    	
	    	//Calculate center of each boundary, on this axis
	    	Point2D centerStat = BoundaryCorner.getCenter(  nearStatPoint , outerPoints[1] ); //OPTIMIZE getCenter() for each boundary
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
	    		penetrationY =  ( playerProjectionY + statProjectionY - centerDistanceY-1 ); 
	    		shiftedY = penetrationY+1;
	    	}else
	    		penetrationY = Math.abs(playerProjectionY) + Math.abs(statProjectionY);
	    	
	    	distX = penetrationX;
	    	distY = penetrationY;
	    	
	    	if ( penetrationX * centerDistanceX < 0 ){ //SIGNS ARE NOT THE SAME
				penetrationX = 0;
	    	}
	    	else{
	    		distX = 0;
	    	}
	    	if ( penetrationY * centerDistanceY < 0 ){
				penetrationY = 0;
	    	}
	    	else{
	    		distY = 0;
	    	}

	    	if ( shiftedX * centerDistanceX > 0 ){ //SIGNS ARE NOT THE SAME
	    		shiftedX = 0;
	    	}
	    	if ( shiftedY * centerDistanceY > 0 ){
	    		shiftedY = 0;
	    	}
		
	    	
	    	g2.setColor(Color.DARK_GRAY);
	    	camera.drawDebugAxis(axis , g2 );   
	    	
	    	camera.drawDebugAxis(statHalf , g2 );
	    	
	    	g2.setColor(Color.GREEN);
	    	camera.drawDebugAxis(playerHalf , g2 );
	    	
	    	camera.drawString( "   Depth: "+penetrationX+","+penetrationY , playerHalf.getP1() , g2);
	    	camera.drawString( "   Depth: "+distX+","+distY , playerHalf.getP2() , g2);
	    	
			//Calculate near Vertices
	    	
	    	BoundaryFeature[] nearStatCorner = statBoundsRelative.farthestFeatureFromPoint( 
		    		entitySecondary.getPosition(), entityPrimary.getPosition(), outerPoints[1] , axis 
		    		); 
		    
	    	BoundaryFeature[] nearPlayerCorner = playerBoundsRelative.farthestFeatureFromPoint( 
		    		entityPrimary.getPosition(), entitySecondary.getPosition(), outerPoints[0] , axis
		    		);

	    	BoundaryFeature featurePrimary = null;
	    	BoundaryFeature featureSecondary = null;
	    	
	    	
	    	featureSecondary = nearStatCorner[0];

	    	if ( nearStatCorner.length > 1 ){ 
	    		featureSecondary = ((BoundaryCorner)nearStatCorner[0]).getSharedSide( ((BoundaryCorner)nearStatCorner[1]) );
	    	}
			

	    	featurePrimary = nearPlayerCorner[0];

	    	if ( nearPlayerCorner.length > 1 ){ //two corners of side are equal distance
	    		featurePrimary = ((BoundaryCorner)nearPlayerCorner[0]).getSharedSide( ((BoundaryCorner)nearPlayerCorner[1]) );
	    	}
	    	else{ //only one corner is closest
	    		featurePrimary = nearPlayerCorner[0];
	    	}


			

			final int square = 3;
			if ( distX*distX + distY*distY > square*square ){  // PENETRATION DISTANCE OUTSIDE THRESHOLD SO END COLLISION
				isComplete = true;
				System.out.println("Collision Dropped by (" +
						distX+ " - " +distX +")");
				
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
						new Vector( axis )
				);
			}
		}
		
		/*
		public Resolution resolveAxis2( Line2D separatingSide){
		
		    EntityStatic stat = entitySecondary;
		    
		    Boundary statBounds = collidingSecondary.getBoundaryLocal() ;
		    Boundary playerBounds = collidingPrimary.getBoundaryDelta() ;
		    
		    final double deltaX = entityPrimary.getTranslationComposite().getDeltaX(entityPrimary) ;
		    final double deltaY = entityPrimary.getTranslationComposite().getDeltaY(entityPrimary) ;
		    
		    //Point2D playerCenterDelta = new Point2D.Double(deltaX, deltaY);
		    //Point2D statCenter = new Point2D.Double(stat.getX(), stat.getY());

			final Line2D axis = BoundaryPolygonal.getSeparatingAxis(separatingSide); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
			
			//BoundaryVertex[] statOuterVertices= statBounds.getFarthestVertices(playerBounds,axis);
	    	//BoundaryVertex[] playerOuterVertices= playerBounds.getFarthestVertices(statBounds,axis);
		    																					// [0] needs to be for loop
		    //BoundaryVertex[] statInnerVertices = statBounds.farthestVerticesFromPoint( statOuterVertices[0] , axis ); 
		    //BoundaryVertex[] playerInnerVertices = playerBounds.farthestVerticesFromPoint( playerOuterVertices[0] , axis );

		    //Point2D[] statOuter = statBounds.getFarthestPoints(playerBounds,axis);
		    //Point2D[] playerOuter = playerBounds.getFarthestPoints(statBounds,axis);
			
			//get singular pair of outermost points between both boundaries ( no duplicates are needed for the collision math )
			final Point2D[] outerPoints = Boundary.getFarthestPointsBetween(playerBounds, statBounds, axis); 
	
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
	    		penetrationX = ( playerProjectionX + statProjectionX - centerDistanceX +2 );
	    		shiftedX = penetrationX-2;
	    	}
	    	else if (centerDistanceX<0){
	    		//centerDistanceX += 1;  //NEEDS HIGHER LEVEL SOLUTION
	    		penetrationX = ( playerProjectionX + statProjectionX - centerDistanceX -2 );
	    		shiftedX = penetrationX+2;
	    	}
	    	else
	    		penetrationX = Math.abs(playerProjectionX) + Math.abs(statProjectionX);

	    	if (centerDistanceY>0){
	    		//centerDistanceY -= 1;
	    		penetrationY = ( playerProjectionY + statProjectionY - centerDistanceY+2 ); 
	    		shiftedY = penetrationY-2;
	    	}
	    	else if (centerDistanceY<0){
	    		//centerDistanceY += 1; 
	    		penetrationY =  ( playerProjectionY + statProjectionY - centerDistanceY-2 ); 
	    		shiftedY = penetrationY+2;
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
			
	    	BoundaryFeature featurePrimary = null;
	    	BoundaryFeature featureSecondary = null;
	    	
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
				
				if ( nearPlayerCorner.length > 1 ){ //two corners of side are equal distance
					featurePrimary = ((BoundaryCorner)nearPlayerCorner[0]).getSharedSide( ((BoundaryCorner)nearPlayerCorner[1]) );
		    	}
				else{ //only one corner is closest
					featurePrimary = nearPlayerCorner[0];
				}
	    	}
	    	else{
	    		System.err.println("Visual Collision Dynamic null corner");
	    	}
			

			final int square = 10;
			if ( penetrationX*penetrationX + penetrationY*penetrationY > square*square ){  // PENETRATION DISTANCE OUTSIDE THRESHOLD SO END COLLISION
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
						new Vector( axis )
				);
			}
		}*/

	}
	
	public String toString(){
		//return String.format("%s",collisionName);
		return "CollisionSAT: "+collisionDebugTag;
	}
	

}
