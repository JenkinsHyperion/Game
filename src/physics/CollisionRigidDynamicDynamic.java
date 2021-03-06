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
import entityComposites.TranslationComposite.VelocityVector;
import misc.DefaultCollisionEvent;
import physics.Collision;
import sprites.RenderingEngine;

public class CollisionRigidDynamicDynamic extends Collision.DefaultType implements VisualCollision{
	
	private SeparatingAxisCollector axisCollector;
	
	private double frictionCoefficient;
	
	//protected Force normalForce;
	//private Force frictionForce;
	
	private Resolver resolver = new ResolverSAT_1();
	
	private ResolutionEvent resolutionEvent = new ResolutionEvent();
	
	private TranslationComposite transPrimary;
	private TranslationComposite transSecondary;
	
	private VelocityVector primaryCancelingVector;
	private VelocityVector secondaryCancelingVector;
	
	public CollisionRigidDynamicDynamic(Collider collidable1, Collider collidable2 , SeparatingAxisCollector axisCollector){
		
		super( collidable1 , collidable2 );
		
		this.axisCollector = axisCollector;
		
		entityPrimary = collidable1.getOwnerEntity();
		entitySecondary = collidable2.getOwnerEntity();
		
		transPrimary = collidable1.getOwnerEntity().getTranslationComposite();
		transSecondary = collidable2.getOwnerEntity().getTranslationComposite();
		
		collidingPrimary = collidable1; // TAKE COLLIDABLE IN COSNTRUCTOR INSTEAD OF ENTITY
		collidingSecondary = collidable2;
		
		primaryCancelingVector = transPrimary.registerVelocityVector( new Vector(0,0) );
		secondaryCancelingVector = transSecondary.registerVelocityVector( new Vector(0,0) );
		
	}
	
	/**Runs initial commands when this Collision is started. Override this method to run any special commands or register 
	 * any forces to be used, but ensure any new forces are removed by overriding {@link #internalCompleteCollision()} 
	 * <p> BE ADVISED: Always call super.initializeCollision(), which creates and maintains rigid normal forces.
	 */
	@Override
	public void internalInitializeCollision(){
		
		this.resolutionState = resolutionEvent;

	}
	
	@Override
	protected void updateCollision(){ 
		
		Resolution closestResolution = getClosestResolution();

		final Vector unitNormal = closestResolution.getSeparationVector().unitVector(); 
		
		//frictionForce.setVector( tangentalVelocity.inverse().multiply(0.05) );
		
		//if ( closestResolution.getClippingVector().getMagnitude() > 1 ) { 
			
			//CLIPPING UPDATING	
			/*System.out.println( "\n[ "+closestResolution.FeaturePrimary() + " on " + entityPrimary +
					" ] clipping with [ " + closestResolution.FeatureSecondary() + " on " + entitySecondary
					+" ]");*/
			
			Vector resolution = closestResolution.getClippingVector();
			
			depthX = resolution.getX();
			depthY = resolution.getY();
			
			//System.out.println("Will clip by "+ depthX +" , "+ depthY + " ..."+( Math.sqrt((depthX*depthX)+(depthY*depthY)) ));
			
			entityPrimary.setPos(
					transPrimary.getDeltaX(entityPrimary) + (depthX/2),
					transPrimary.getDeltaY(entityPrimary) + (depthY/2)
					);
			entitySecondary.setPos(
					transSecondary.getDeltaX(entitySecondary) - (depthX/2),
					transSecondary.getDeltaY(entitySecondary) - (depthY/2)
					);
			
			Vector primaryVelocity = transPrimary.getVelocityVector().projectedOver(unitNormal.normalLeft());
			Vector secondaryVelocity = transSecondary.getVelocityVector().projectedOver(unitNormal.normalLeft());
			
			transPrimary.halt();
			transSecondary.halt();
			
			transPrimary.setVelocityVector( primaryVelocity );
			transSecondary.setVelocityVector( secondaryVelocity );
			
			//Vector primaryVelocity = transPrimary.getNetVelocityVector().projectedOver(resolution.inverse());
			//Vector secondaryVelocity = transSecondary.getNetVelocityVector().projectedOver(resolution);
			
			//primaryCancelingVector.setVector( primaryVelocity.inverse() );
			//secondaryCancelingVector.setVector( secondaryVelocity.inverse() );
			
			//System.out.println("primaryCancel "+ primaryVelocity);
			//System.out.println("Will clip by "+ secondaryVelocity );
			
		//}
		
		//else { //RESOLVED UPDATING
		///	System.err.println("RESOLVED" );
		//	triggerResolutionEvent( closestResolution ); 
		//}
		
	}

	
	@Override
	public void updateVisualCollision( MovingCamera camera , Graphics2D g2){ 

		updateCollision();
	}
	
	public void updateBehavior(Vector unitNormal, Vector tangentalVelocity){
		
	}
	
	protected class ResolutionEvent extends ResolutionState{ //ONE TIME EVENT THAT TRIGGERS UPON RESOLUTION
		
		@Override
		protected void triggerEvent( Resolution resolution ) { 
			
			collisionDebugTag = "("+resolution.FeaturePrimary()+" of "+entityPrimary.name+") contacting ("+
					resolution.FeatureSecondary()+" of "+entitySecondary.name+")";

			/*
			System.out.println("Triggering ["+resolution.FeaturePrimary().getEvent() + 
								"] of ["+ resolution.FeaturePrimary() + "] on [" + entityPrimary+"]" );
			//Trigger Boundary Collision Event on relevant side/vertex
			resolution.FeaturePrimary().getEvent().run( 
					collidingSecondary , 
					resolution.FeaturePrimary(),
					resolution.FeatureSecondary(), resolution.getSeparationVector().unitVector()
					);
			
			collidingPrimary.onCollisionEvent();
			collidingSecondary.onCollisionEvent();
			*/
		}

	}
		
	@Override
	protected void triggerResolutionEvent( Resolution resolution ) { 
		this.resolutionState.triggerEvent( resolution );
		this.resolutionState = ResolvedState.resolved();
			
	}
	
	/*private boolean resolutionsAreEqual( Resolution newer, Resolution current ){
		
		
		
	}*/

	/**Runs final commands before this Collision ceases to exist. In any overriding classes, always call super.completeCollision(), 
	 * which removes and maintains Collider references and normal Forces.
	 */
	@Override
	public void internalCompleteCollision(){
		
		collidingPrimary.onLeavingCollisionEvent();
		collidingSecondary.onLeavingCollisionEvent();
		
		transPrimary.removeVelocityVector( primaryCancelingVector );
		transSecondary.removeVelocityVector( secondaryCancelingVector );
		
		//transPrimary.removeNormalForce(normalForce);              //turn gravity back on
		//transPrimary.removeForce(frictionForce.getID());     //remove friction

	}
	
	/* ######################
	 * # CORE FUNCTIONALITY #
	 * ######################
	 */
	
	private Resolution getClosestResolution() { 
		//System.out.println("Checking best resolution"); 
		ArrayList<Resolution> penetrations = new ArrayList<>();
    	
		SeparatingAxisCollector.Axis[] separatingAxes = this.axisCollector.getSeparatingAxes(

				collidingSecondary, entitySecondary.getPosition(),
				collidingSecondary.getBoundary(),
				
				collidingPrimary, entityPrimary.getPosition(), 
				collidingPrimary.getBoundary()
				//,camera, g2
				);
		
		for ( SeparatingAxisCollector.Axis separatingAxis : separatingAxes ){
			
			Line2D axis = separatingAxis.getAxisLine();	
			
		    penetrations.add( this.resolver.resolveAxis( axis ));
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
	    		
	    	}
	    	//System.out.println( "Accepted "+ closestResolution.getClippingVector().getX() + " , " + closestResolution.getClippingVector().getY());
    	}

    	return closestResolution;

	}
	
	//Resolution calculation
	private Resolution getClosestResolution( MovingCamera camera, Graphics2D g2) { 
		//System.out.println("Checking best resolution"); 
		ArrayList<Resolution> penetrations = new ArrayList<>();
    	
		SeparatingAxisCollector.Axis[] separatingAxes = this.axisCollector.getSeparatingAxes(

				collidingSecondary, entitySecondary.getPosition(),
				collidingSecondary.getBoundary(),
				
				collidingPrimary, entityPrimary.getPosition(), 
				collidingPrimary.getBoundary()
				//,camera, g2
				);
		
		for ( SeparatingAxisCollector.Axis separatingAxis : separatingAxes ){
			
			Line2D axis = separatingAxis.getAxisLine();	
			
		    penetrations.add( this.resolver.resolveAxis( axis , camera, g2 ));
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
	    		
	    	}
	    	//System.out.println( "Accepted "+ closestResolution.getClippingVector().getX() + " , " + closestResolution.getClippingVector().getY());
    	}

    	return closestResolution;

	}

	
	private abstract class Resolver{
		abstract Resolution resolveAxis( Line2D separatingAxis );
		abstract Resolution resolveAxis( Line2D separatingAxis , MovingCamera camera, Graphics2D g2);
	}
	
	private class ResolverSAT_1 extends Resolver{
		
		public Resolution resolveAxis( Line2D axis , MovingCamera camera, Graphics2D g2){
			
		    EntityStatic stat = entitySecondary;
		    
		    Boundary statBoundsRelative = collidingSecondary.getBoundary() ;
		    Boundary playerBoundsRelative = collidingPrimary.getBoundary() ;
		    
		    //Boundary statBounds = collidingSecondary.getBoundaryLocal() ;
		    //Boundary playerBounds = collidingPrimary.getBoundaryLocal() ;
		    
		    final double deltaX = entityPrimary.getTranslationComposite().getDeltaX(entityPrimary) ;
		    final double deltaY = entityPrimary.getTranslationComposite().getDeltaY(entityPrimary) ;
		    final Point deltaPosition = new Point( (int)deltaX , (int)deltaY );


		    
			final Point2D[] outerPoints = Boundary.getFarthestPointsBetween(
					entityPrimary,playerBoundsRelative, entitySecondary,statBoundsRelative, axis
					); 

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
	
		
		public Resolution resolveAxis( Line2D axis ){
			
		    EntityStatic stat = entitySecondary;
		    
		    Boundary statBoundsRelative = collidingSecondary.getBoundary() ;
		    Boundary playerBoundsRelative = collidingPrimary.getBoundary() ;
		    
		    //Boundary statBounds = collidingSecondary.getBoundaryLocal() ;
		    //Boundary playerBounds = collidingPrimary.getBoundaryLocal() ;
		    
		    final double deltaX = entityPrimary.getTranslationComposite().getDeltaX(entityPrimary) ;
		    final double deltaY = entityPrimary.getTranslationComposite().getDeltaY(entityPrimary) ;
		    final Point deltaPosition = new Point( (int)deltaX , (int)deltaY );


		    
			final Point2D[] outerPoints = Boundary.getFarthestPointsBetween(
					entityPrimary,playerBoundsRelative, entitySecondary,statBoundsRelative, axis
					); 

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
		

	}
	
	public String toString(){
		//return String.format("%s",collisionName);
		return "CollisionSAT: "+collisionDebugTag;
	}
	

}
