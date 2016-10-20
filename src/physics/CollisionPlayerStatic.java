package physics;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import entities.EntityDynamic;
import entities.EntityStatic;
import entityComposites.*;

public class CollisionPlayerStatic extends Collision {
	
	private EntityDynamic entityPrimary;
	private EntityStatic entitySecondary;
	
	private boolean xequilibrium = false;
	private boolean yequilibrium = false;
	
	private int xDepthTemp=0;
	private int yDepthTemp=0;
	private int xDepth=0;
	private int yDepth=0;
	
	public CollisionPlayerStatic(EntityDynamic entity1, EntityStatic entity2){
		
		super(entity1, entity2);
		
		entityPrimary = entity1;
		entitySecondary = entity2;
		
		collidingSecondary = ((Collidable)entity1.collidability());
		collidingPrimary = ((Collidable)entity1.collidability());
		
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
		
		Side[] contactingSides = collidingPrimary.getBoundaryLocal().getContactingSides(collidingSecondary.getBoundaryLocal());
		
		//CHECK FOR CONTACTING SIDES
		if ( contactingSides != null ){ 
		//if (false){
			getContactPoints(contactingSides[0],contactingSides[1]);		

				contactingSide1 = contactingSides[0];
				contactingSide2 = contactingSides[1]; 
				entityPrimary.setColliding(true);
				
				yequilibrium = true;
				//entityPrimary.setDY(0);
				//entityPrimary.setAccY(0);
				entityPrimary.setColliding(true);
				
				if (entityPrimary.getDX() > (0.1))
		    	{
					entityPrimary.setAccX(-0.1f);
		    	}
		    	else if (entityPrimary.getDX() < (-0.1))
		    	{
		    		entityPrimary.setAccX(0.1f);
		    	}
		    	else
		    	{
		    		entityPrimary.setDX(0);
		    		entityPrimary.setAccX(0);
		    	}
			
		}
		else { //NO CONTACTING SIDES
			
			contactingSide1 = null;
			contactingSide2 = null;
			contactPoints[0]=null; 
			contactPoints[1]=null;
			entityPrimary.setColliding(false);

			//CHECK FOR CLIPPING IN NEXT FRAME 
			//Note this checks intersections of delta boundary, where boundary WILL BE next frame ( posX + DX , PosY + DY )
			Side[][] intersectingSides = entityPrimary.getBoundaryDelta().getIntersectingSides(collidingSecondary.getBoundaryLocal());
			
			if ( intersectingSides != null ) { // Boundary will clip next frame
				
				yequilibrium = false; //flag collision for activity
				

				//GET INTERSECTION DEPTH - Put in separate method
				debugIntersectionPoints.clear();
				
				//for ( Line2D[] pair : intersectingSides ) { //first line of pair is THIS boundary, second is other boundary
				for (int i = 0 ; i < intersectingSides.length ; i++){
					
					Side[] pair = intersectingSides[i];
	
					Point2D intersect = getIntersectionPoint(pair[0].toLine() , pair[1].toLine() );
					debugIntersectionPoints.add(intersect);
					
					if (intersect != null){
							//Split the side into two at the intersection point, and get the smaller piece
							//The smaller piece determines which direction the side is clipping from 
							if ( intersect.distance(pair[0].getP1()) > intersect.distance(pair[0].getP2()) ) {
								xDepthTemp = (int) (intersect.getX() - pair[0].getP2().getX());
								yDepthTemp = (int) (intersect.getY() - pair[0].getP2().getY());
								
								//System.out.print( "\nIntersection on Primary Side 1 ("+xDepthTemp+","+yDepthTemp+") ");

								getClipResolution(intersect, pair[0].getP2());
								
							}
							else {
								xDepthTemp = (int) (intersect.getX() - pair[0].getP1().getX());
								yDepthTemp = (int) (intersect.getY() - pair[0].getP1().getY());
								
								//System.out.print( "\nIntersection on Primary Side 2 ("+xDepthTemp+","+yDepthTemp+") ");
								
								getClipResolution(intersect, pair[0].getP1());
							
							}
							
							//TESTING SECONDARY BOUNDARY INTERSECTIONS
							if ( intersect.distance(pair[1].getP1()) > intersect.distance(pair[1].getP2()) ) {
								xDepthTemp = (int) (-intersect.getX() + pair[1].getP2().getX());
								yDepthTemp = (int) (-intersect.getY() + pair[1].getP2().getY());
								//System.out.print( "\nIntersection on Secondary Side 1 ("+xDepthTemp+","+yDepthTemp+") ");
								
								getClipResolution(intersect, pair[1].getP2());
								
							}
							else {
								xDepthTemp = (int) (-intersect.getX() + pair[1].getP1().getX());
								yDepthTemp = (int) (-intersect.getY() + pair[1].getP1().getY());
								//System.out.print( "\nIntersection on Secondary Side 2 ("+xDepthTemp+","+yDepthTemp+") ");
								
								//if (intersect.getX() > pair[1].getP1().getX()){
								
								getClipResolution(intersect, pair[1].getP1());
								
							}			
					}		
				}
				
				
				//System.out.println( "\n Closest acceptable clips: ( "+xDepth+ " , "+ yDepth+" )");
				
				//entityPrimary.setDY( 0 ) ; 
				//entityPrimary.setDX( 0 ) ; 
				//entityPrimary.clipDX( xDepth ) ; 
				//entityPrimary.clipDY( yDepth ) ; 
				//entityPrimary.setX( (int)(entityPrimary.getX() + entityPrimary.getDX() + xDepth ) ); 
				//entityPrimary.setY( (int)(entityPrimary.getY() + entityPrimary.getDY() + yDepth ) ); 

					//entityPrimary.setX( (entityPrimary.getX() + xDepth ) ); 
					//entityPrimary.setY( (entityPrimary.getY() + yDepth ) ); 
				
			}
		
		}//
		
	}
	
	//FINAL COLLISION COMMANDS - Last commands before this collision object self destructs
	@Override
	public void completeCollision(){
		entityPrimary.setColliding(false); // unset entity collision flag. 
		entityPrimary.setAccY(0.1f); //turn gravity back on
		entityPrimary.setAccX(0); //remove friction
	}
	
	private void getClipResolution( Point2D intersection , Point2D endpoint ){
		//This is the ejection algorithm that pushes a clipping boundary out of another boundary
		
		if (depthOpposesVelocity(xDepthTemp, entityPrimary.getDX()) && depthOpposesVelocity(yDepthTemp, entityPrimary.getDY()) ){
			//Reject resolution right away if it would add to velocity, causing the entity to jump ahead			
			
				if (xDepthTemp == 0 && yDepthTemp == 0){
					//System.out.print( " rejected null 0,0 ");
				}
				else {
					
						xDepth = xDepthTemp;
						yDepth = yDepthTemp;
						//System.out.print( " accepted resolution "+ entityPrimary.getDX() + " " + entityPrimary.getDY() );
				
				}

		}
		else {
			//System.out.print( " rejected " + entityPrimary.getDX() + " " + entityPrimary.getDY());
		}
		
		
	}
	
	private boolean depthOpposesVelocity(int depth, float vel){
		
		if (depth > 0 ){
			if (vel > 0){ 
				return false; 
			}
			else { //System.out.print( "true");
				if ( -vel < depth ) { 
					return false; 
				} else {return true;}
			}
		}
		else if (depth < 0 ){
			if (vel < 0){ 
				return false; 
			}
			else {//System.out.print( "true");
			if ( -vel > depth ) { 
				return false;
			} else {return true;}
			}
		}
		else { 
			return true;
		}
		
	}
	
	// Finds the actual contacting surface of two contacting sides. As in, if a side is overhanging off a ledge, only the 
	// part that is actually on the ledge is returned
	private void getContactPoints(Side side1, Side side2) {
		
		contactPoints[0]=null; contactPoints[1]=null;

		if ( pointIsOnSegment(side1.getP1(), side2.toLine()) ) {
			if (contactPoints[0]==null)  { contactPoints[0] = side1.getP1(); } 
			else  { contactPoints[1] = side1.getP1(); return; }
		}
		if ( pointIsOnSegment(side1.getP2(), side2.toLine()) ) {
			if (contactPoints[0]==null)  { contactPoints[0] = side1.getP2(); } 
			else  { contactPoints[1] = side1.getP2(); return; }
		}
		if ( pointIsOnSegment(side2.getP1(), side1.toLine()) ) {
			if (contactPoints[0]==null)  { contactPoints[0] = side2.getP1(); } 
			else  { contactPoints[1] = side2.getP1(); return; }
		}
		if ( pointIsOnSegment(side2.getP2(), side1.toLine()) ) {
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
	
	public String toString(){
		return String.format("%s",collisionName);
	}
	

}
