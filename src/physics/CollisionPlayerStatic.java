package physics;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.plaf.SliderUI;

import entities.EntityDynamic;
import entities.EntityStatic;

public class CollisionPlayerStatic extends Collision {
	
	private EntityDynamic entityPrimary;
	private EntityStatic entitySecondary;
	
	private boolean xequilibrium = false;
	private boolean yequilibrium = false;
	
	public CollisionPlayerStatic(EntityDynamic entity1, EntityStatic entity2){
		
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
		
		Line2D[] contactingSides = entityPrimary.getLocalBoundary().getContactingSides(entitySecondary.getLocalBoundary());
		
		//CHECK FOR CONTACTING SIDES
		if ( contactingSides != null ){ 

			getContactPoints(contactingSides[0],contactingSides[1]);		

				contactingSide1 = contactingSides[0];
				contactingSide2 = contactingSides[1]; 
				
				yequilibrium = true;
				entityPrimary.setDY(0);
				entityPrimary.setAccY(0);
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
			Line2D[][] intersectingSides = entityPrimary.getDeltaBoundary().getIntersectingSides(entitySecondary.getLocalBoundary());
			
			if ( intersectingSides != null ) { // Boundary will clip next frame
				
				yequilibrium = false; //flag collision for activity

				//GET INTERSECTION DEPTH - Put in separate method
				int depth=0;
				int maxDepth = 0;
				for ( Line2D[] pair : intersectingSides ) { //first line of pair is THIS boundary, second is other boundary
	
					Point2D intersect = getIntersectionPoint(pair[0], pair[1]);
					
							//Split the side into two at the intersection point, and get the smaller piece
							//The smaller piece determines which direction the side is clipping from 
							if ( intersect.distance(pair[0].getP1()) > intersect.distance(pair[0].getP2()) ) {
								depth = (int) ( intersect.distance(pair[0].getP2()) );
								System.out.println( "Intersection ( " + intersect.getX() + " , " + intersect.getY() + ") at depth "+depth);
								
							}
							else {
								depth = (int) ( intersect.distance(pair[0].getP1()) );
								System.out.println( "Intersection ( " + intersect.getX() + " , " + intersect.getY() + ") at depth "+depth);
							}
							
							if (depth > maxDepth) {maxDepth = depth;} 
							
							//Get normal force 1/slope  //slope = (y-y2) / (x-x2)  //  rise over run
							

							
				}
				
				//System.out.println( " Depth" + max );
				//Take maxDepth, the distance of clipping, and subtract from deltaPosition, meaning next frame the entity
				//will not clip and will instead be at surface.
				
				//THIS WILL BE CHANGED from only pushing upwards to pushing whichever direction the clipping is happening in
					entityPrimary.setY( (entityPrimary.getY() + (int) entityPrimary.getDY() - maxDepth - 1) ); 
					entityPrimary.setDY( 0 ); 
				
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
			else //BOTH LINES ARE VERITCAL - THIS OCCURS WHEN STANDING DIRECTLY ON EDGE AND IS A VERY IMPORTANT CASE
			{
				// This needs to be handled better
				if( line1.getP1().distance(line2.getP1()) < 1 ){ return line1.getP1() ; }
				else if( line1.getP1().distance(line2.getP2()) < 1 ){ return line1.getP1() ; }
				else if( line1.getP2().distance(line2.getP1()) < 1 ){ return line1.getP2() ; }
				else if( line1.getP2().distance(line2.getP2()) < 1 ){ return line1.getP2() ; }
				else {
					//Lines are not intersecting, but lines are known to be intersecting so this should never occur.
					//Devise better failsafe anyway
					System.out.println( "Bad Edge Intersection" ); 
					return line1.getP1() ;
				}
			}
			
		}
		else // Neither line is vertical, so both have defined slopes and can be in form y=mx+b
		{
			// m = (y1-y2)/(x1-x2)
				m1 = ( line2.getP1().getY() - line2.getP2().getY()  ) / ( line2.getP1().getX() - line2.getP2().getX() );
				m2 = ( line2.getP1().getY() - line2.getP2().getY()  ) / ( line2.getP1().getX() - line2.getP2().getX() );
			// b = y - mx
				b1 = line2.getP1().getY() - ( m2 * line2.getP1().getX() );			
				b2 = line2.getP1().getY() - ( m2 * line2.getP1().getX() );		
			
			intersectX = (b2-b1) / (m1-m2) ; // y1=y2 and x1=x2 at intersection, so m1*x + b1 = m2*x + b2  solved for x
			
			return new Point2D.Double( intersectX , (m1 * intersectX) + b1 ); // intersectY = m*interceptX + b for either line 
			
		}

	}
	
	public String toString(){
		return String.format("%s",collisionName);
	}
	

}
