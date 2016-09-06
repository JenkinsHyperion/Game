package physics;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.plaf.SliderUI;

import entities.EntityDynamic;
import entities.EntityStatic;

public class CollisionPlayerStaticSAT extends Collision {
	
	private EntityDynamic entityPrimary;
	private EntityStatic entitySecondary;
	
	private boolean xequilibrium = false;
	private boolean yequilibrium = false;
	
	public CollisionPlayerStaticSAT(EntityDynamic entity1, EntityStatic entity2){
		
		super(entity1, entity2);
		
		entityPrimary = entity1;
		entitySecondary = entity2;
		collisionName = entity1.name + " + " + entity2.name;
		
		//GENERIC COLLISION
		
		initCollision();
		
	}
	
	//INITAL COLLISION COMMANDS - Run once, the first time collision occurs
	@Override
	public void initCollision(){
		
		updateCollision(); //Run math for first time
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
				entityPrimary.setAccX(-0.1f);
	    	}
	    	else if (entityPrimary.getDX() < (-0.2))
	    	{
	    		entityPrimary.setAccX(0.1f);
	    	}
	    	else
	    	{
	    		entityPrimary.setDX(0);
	    		entityPrimary.setAccX(0);
	    	}
			
			
		}
		else { //Primary Entity is clipping with closest resolution of vector
			
			depthX = (int) resolution.getX();
			depthY = (int) resolution.getY();

			
				if ( Math.ceil(entityPrimary.getDX()) < -depthX ){
					entityPrimary.setX( entityPrimary.getX() + depthX);
				}

				//entityPrimary.setAccX(0);
				entityPrimary.clipDX((int) ( -resolution.getX() ) );

			// Y RESOLUTION

				if ( Math.ceil(entityPrimary.getDY()) < -depthY ){
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
	}
	
	
	private Point getClosestResolution() {
		
		ArrayList<Point> penetrations = new ArrayList<>();
    	//for ( Line2D axis : bounds.debugSeparatingAxes(B_WIDTH, B_HEIGHT) ){
    	for (int i = 0 ; i < entitySecondary.getBoundary().getSeparatingSides().length ; i++ ){
    		
    		if (getPenetrationDepth(entitySecondary.getBoundary().getSeparatingSides()[i]) != null){
    			
    			penetrations.add( getPenetrationDepth(entitySecondary.getLocalBoundary().getSeparatingSides()[i]) );
    			
    		}
    	}
    	
    	for (int i = 0 ; i < entityPrimary.getBoundary().getSeparatingSides().length ; i++ ){
    		
    		if (getPenetrationDepth(entityPrimary.getBoundary().getSeparatingSides()[i]) != null){
    			
    			penetrations.add( getPenetrationDepth(entityPrimary.getLocalBoundary().getSeparatingSides()[i]) );
    			
    		}
    	}
    	
    	int penetrationX = 0;
    	int penetrationY = 0;
    	
    	if (penetrations.size() != 0){
    		
    		penetrationX = (int) Math.ceil( penetrations.get(0).getX() );
    		penetrationY = (int) Math.ceil( penetrations.get(0).getY() );
    		System.out.println("Start "+penetrationX+" "+penetrationY + " -- "+entityPrimary.getX() + entitySecondary.getX());		
	    	for ( Point vector : penetrations ){

	    		
		    		if ( (vector.getX()*vector.getX() + vector.getY()*vector.getY())
		    				< ( penetrationX*penetrationX + penetrationY*penetrationY )
		    			){
		    			
		    			System.out.println("Lower " + vector.getX()+" "+vector.getY());
		    			penetrationX = (int) Math.ceil(vector.getX());
		    			penetrationY = (int) Math.ceil(vector.getY()); 
		    		}

	    	}
    	}
    	
    	
    	if (penetrationX == 0 && penetrationY == 0 ){
    		return null;
    	}
    	else {
    		return new Point(penetrationX,penetrationY);
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
	    
	    Boundary bounds = stat.getLocalBoundary() ;
	    Boundary playerBounds = entityPrimary.getDeltaBoundary();
	    
	    int deltaX = (int) (entityPrimary.getX() + entityPrimary.getDX() );
	    int deltaY = (int) (entityPrimary.getY() + entityPrimary.getDY() );
	    
	    Point2D playerCenter = new Point2D.Double(deltaX, deltaY);
	    Point2D statCenter = new Point2D.Double(stat.getX(), stat.getY());
		
		
		Line2D axis = bounds.debugGetAxis(separatingSide,300, 300);
	    
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
		
		penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
		penetrationY = playerProjectionY + statProjectionY - centerDistanceY ;
		
		//Constrain X
		if ( Math.signum(penetrationX) != Math.signum(centerDistanceX)  || 
				Math.signum(penetrationY) != Math.signum(centerDistanceY)  ){
				penetrationX = 0;
				penetrationY = 0;
			}
		
		if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){ //LOOK INTO BETTER CONDITIONALS
			penetrationX = -(playerProjectionX + statProjectionX) ;
		}
		if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){
			penetrationY = -(playerProjectionY + statProjectionY) ;
		}
		
		//return logic
		//if (penetrationX == 0 &&penetrationY == 0){
		//	return null;
		//}
		//else {
			return new Point( penetrationX , penetrationY );
		//}
		
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
	
	
	//Finds point of intersection between two sides. This is important for finding depth of clipping 
	private Point2D getIntersectionPoint(Line2D line1, Line2D line2){ 
		double m1; // break lines into slope - intercept forms    y = mx + b
		double m2;
		double b1;
		double b2;
		double intersectX; // intersectY is  m*intersectX + b  so is calculated at the end
		
		// Check if either line is vertical, in which case slope m is undefined, y is all real numbers, and y=mx+b fails line test
		if ( line1.getP1().getX() == line1.getP2().getX() || line2.getP1().getX() == line2.getP2().getX() ) { 
			
			if ( line1.getP1().getX() == line1.getP2().getX() && line2.getP1().getX() != line2.getP2().getX() ) { 
				
				// line 1 is vertical, so x intersect is simply x for a vertical line 
				intersectX = line1.getP1().getX() ;
				// get y=mx+b for other line and find y intercept
				m2 = ( line2.getP1().getY() - line2.getP2().getY()  ) / ( line2.getP1().getX() - line2.getP2().getX() );
				b2 = line2.getP1().getY() - ( m2 * line2.getP1().getX() );
				return new Point2D.Double( intersectX , (m2 * intersectX) + b2 );
			}
			else if ( line2.getP1().getX() == line2.getP2().getX() && line1.getP1().getX() != line1.getP2().getX() ) {
				
				//line2 is vertical, same as above
				intersectX = line2.getP1().getX() ;
				m1 = ( line1.getP1().getY() - line1.getP2().getY()  ) / ( line1.getP1().getX() - line1.getP2().getX() );
				b1 = line1.getP1().getY() - ( m1 * line1.getP1().getX() );
				return new Point2D.Double( intersectX , (m1 * intersectX) + b1 );
			}
			else{ //BOTH LINES ARE VERITCAL - THIS OCCURS WHEN STANDING DIRECTLY ON EDGE AND IS A VERY IMPORTANT CASE
				//System.out.println( "identical line" ); 
			return null;
				/*
				// This needs to be handled better
				if( line1.getP1().distance(line2.getP1()) < 1 ){ return line1.getP1() ; }
				else if( line1.getP1().distance(line2.getP2()) < 1 ){ return line1.getP1() ; }
				else if( line1.getP2().distance(line2.getP1()) < 1 ){ return line1.getP2() ; }
				else if( line1.getP2().distance(line2.getP2()) < 1 ){ return line1.getP2() ; }
				else {
					//Lines are not intersecting, but lines are known to be intersecting so this should never occur.
					//Devise better failsafe anyway
					System.out.println( "Bad Edge Intersection" ); 
					return null;
				}*/
			}
			
		}
		else // Neither line is vertical, so both have defined slopes and can be in form y=mx+b
		{
			// m = (y1-y2)/(x1-x2)
				m1 = ( line1.getP1().getY() - line1.getP2().getY()  ) / ( line1.getP1().getX() - line1.getP2().getX() );
				m2 = ( line2.getP1().getY() - line2.getP2().getY()  ) / ( line2.getP1().getX() - line2.getP2().getX() );
			// b = y - mx
				b1 = line1.getP1().getY() - ( m1 * line2.getP1().getX() );			
				b2 = line2.getP1().getY() - ( m2 * line2.getP1().getX() );		
			
				if (Math.abs(m2) - Math.abs(m1) < 1 ){
					//System.out.println( "identical line" ); 
					return null;
				}
				else {
					intersectX = (b2-b1) / (m1-m2) ; // y1=y2 and x1=x2 at intersection, so m1*x + b1 = m2*x + b2  solved for x
					return new Point2D.Double( intersectX , (m1 * intersectX) + b1 ); // intersectY = m*interceptX + b for either line 
				}
		}

	}
	
	public Point getDepth(){
		return new Point( depthX , depthY );
	}
	
	public String toString(){
		return String.format("%s",collisionName);
	}
	

}
