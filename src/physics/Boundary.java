package physics;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Boundary {
	
	//protected Shape boundaryShape;
	
	protected Line2D[] sides = new Line2D[1]; 
	
	protected int x;
	protected int y;
	
	public Boundary() {
	}
	
	public Boundary(Line2D line){
		sides[0] = line; 
	}
	
	public boolean checkForInteraction(Boundary bounds){
		if (boundaryIntersects(bounds)){
			return true;
		}
		else if (boundsHaveContact(bounds)){
			return true;
		}
		else {
			return false;
		}
	}
	
	//Cycle through all sides of two shapes and check for intersections
	public boolean boundaryIntersects(Boundary bounds){ 
		
		for (int i = 0 ; i < sides.length ; i++) {
			
			for ( Line2D line2 : bounds.getSides()) {
				
				if ( sides[i].intersectsLine( line2 ) ) {
					return true;
				}
				
			}
			
		}
		return false;
	}
	
	//Cycle through all sides of two shapes and get the sides that intersect
	public Line2D[][] getIntersectingSides(Boundary bounds){ //returns pairs of sides of this boundary that are intesecting
		
		ArrayList<Line2D[]> intersectingSidesA = new ArrayList<Line2D[]>(); //array of *pairs* of intersecting lines
		
		for (int i = 0 ; i < sides.length ; i++) { // cycle through all sides. OPTIMIZATION NEEDED
			
			for ( int j = 0 ; j < bounds.getSides().length ; j++ ) {
				
				if ( sides[i].intersectsLine( bounds.getSides()[j] ) ) { 
					
					//place intersecting line pair into array
					intersectingSidesA.add( new Line2D[]{ sides[i] , bounds.getSides()[j] } );	
				}
			}
		}
		
		//System.out.println( intersectingSidesA.size() + " intersecting sides found" );
		
		Line2D[][] intersectingSides = new Line2D[intersectingSidesA.size()][2]; // create final regular array
		
		if ( intersectingSidesA.size() == 0 ) { //no pairs found
			intersectingSidesA = null; //or delete from memory
			return null;
		}
		else {
			for (int j = 0 ; j < intersectingSidesA.size() ; j++) { // compile arrayList pairs into regular array
				intersectingSides[j] = intersectingSidesA.get(j);
			}
			intersectingSidesA = null; //or delete from memory
			return intersectingSides;
		}
	}
	
	
	//Cycle through all sides of two shapes and check if any sides are in contact
	public boolean boundsHaveContact(Boundary bounds){ 
		//redundant, use variables produced by getContactingSides()
			
			for (int i = 0 ; i < sides.length ; i++) {
				
				for ( int j = 0 ; j < bounds.getSides().length ; j++ ) {

						if ( pointIsAgainstLine(bounds.getSides()[j].getP1(), sides[i]) ) { 
							if ( pointIsAgainstLine(bounds.getSides()[j].getP2(), sides[i]) ) {
								//sides i and j are flush
								
								if (  sidesHaveContact(bounds.getSides()[j], sides[i]) )  {
									return true;
								}
								
							}
						}

						if ( pointIsAgainstLine(sides[i].getP1(), bounds.getSides()[j]) ) {
							if ( pointIsAgainstLine(sides[i].getP2(), bounds.getSides()[j]) ) {
								//sides i and j are flush
								
								if (  sidesHaveContact(bounds.getSides()[j], sides[i]) ){
									return true;
								}
								
							}
						}	
				}
			}
			return false;
		}

	//Cycle through all sides of two shapes and get the sides that contact
	public Line2D[] getContactingSides(Boundary bounds){ 
		
		Line2D[] contactingSides = new Line2D[2];
		
		for (int i = 0 ; i < sides.length ; i++) {
			
			for ( int j = 0 ; j < bounds.getSides().length ; j++ ) {
	
					if ( pointIsAgainstLine(bounds.getSides()[j].getP1(), sides[i]) ) { 
						if ( pointIsAgainstLine(bounds.getSides()[j].getP2(), sides[i]) ) {
							//sides i and j are flush
							
							if (  sidesHaveContact(bounds.getSides()[j], sides[i])  ){
								contactingSides[0] = bounds.getSides()[j];
								contactingSides[1] = sides[i];
								return contactingSides;
							} 
							
						}
					}
	
					if ( pointIsAgainstLine(sides[i].getP1(), bounds.getSides()[j]) ) {
						if ( pointIsAgainstLine(sides[i].getP2(), bounds.getSides()[j]) ) {
							//sides i and j are flush
							
							if (  sidesHaveContact(bounds.getSides()[j], sides[i])  ){
								contactingSides[0] = bounds.getSides()[j];
								contactingSides[1] = sides[i];
								return contactingSides;
							}
							
						}
					}	
			}
			
		}
		contactingSides[0]=null; contactingSides[1]=null;
		return null;
	}


	public boolean sidesHaveContact(Line2D side1, Line2D side2) { 
		
		Point2D p1 = null;
		Point2D p2 = null;
		Point2D p3 = null;
		
		if (pointIsAgainstSegment(side1.getP1(), side2)) {
			p1 = side1.getP1(); 		
		}
		if (pointIsAgainstSegment(side1.getP2(), side2)) {
			if (p1 == null) {p1 = side1.getP2();} 
			else {p2 = side1.getP2();}
		}
		if (pointIsAgainstSegment(side2.getP1(), side1)) {
			if (p1 == null) {p1 = side2.getP1();} 
			else if (p2 == null) {p2 = side2.getP1();}
			else {p3 = side2.getP1();}
		}
		if (pointIsAgainstSegment(side2.getP2(), side1)) {
			if (p1 == null) {p1 = side2.getP2();} 
			else if (p2 == null) {p2 = side2.getP2();}
			else if (p3 == null) {p3 = side2.getP2();}
			else {} // There shouldn't ever be more than three vertexes in the same collision //CHANGE TO FOUR
		}
		
		if (p1 != null ) {
			if (p2 != null) {
				if (p1.distance(p2) > 2 ) {return true;}
	
				if (p3 != null) {
					if (p3.distance(p2) > 2 ) {return true;} 
					if (p3.distance(p1) > 2 ) {return true;} 
				}
				return false;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
		
	}


	private boolean pointIsAgainstLine(Point2D point, Line2D line){
		double dist = Math.abs( line.ptLineDist( point ) );  
		if ( dist > 0 && dist <= 1 ) {
			return true;			
		}
		else {
			return false;
		}
	}
	
	private boolean pointIsAgainstSegment(Point2D point, Line2D line){
		double dist = Math.abs( line.ptSegDist(point)) ;  	
		if ( dist > 0 && dist <= 1 ) {
			return true;			
		}
		else {
			return false;
		}
	}
	
	
	public Line2D[] getSides(){
		return sides;
	}
	
	public void constructSides(Line2D[] sidesC){
		sides = sidesC;
	}

	// Returns boundary shifted to some position, usually the position of the entity that owns the boundary
	public Boundary atPosition(int x, int y) {

		Boundary shifted = new Boundary();
		Line2D[] shiftedSides = new Line2D[sides.length];
		
		for ( int i = 0 ; i < sides.length ; i++ ){
			shiftedSides[i] = new Line2D.Double(sides[i].getX1()+x, sides[i].getY1()+y , 
					sides[i].getX2()+x, sides[i].getY2()+y );
		}
		
		shifted.constructSides(shiftedSides);	
		return shifted;
		
	};
	
}
