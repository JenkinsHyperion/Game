package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Boundary implements Serializable {
	
	//protected Shape boundaryShape;
	
	protected Line2D[] sides = new Line2D[1]; 

	public Boundary() {
	}
	
	public Boundary(Line2D line){
		sides[0] = line; 
	}
	
	public Boundary(Line2D[] bounds) {
		sides = bounds;
	}
	
	public static class Box extends Boundary{
		
		public Box(int width, int height, int xOffset, int yOffset){
			
			sides = new Line2D[4];
			
			sides[0] = new Line2D.Float(xOffset , yOffset , xOffset+width , yOffset );
			sides[1] = new Line2D.Float(xOffset+width , yOffset , xOffset+width , yOffset+height );
			sides[2] = new Line2D.Float(xOffset+width , yOffset+height , xOffset , yOffset+height );
			sides[3] = new Line2D.Float(xOffset , yOffset+height , xOffset , yOffset );

		}
		
	}
	
	
	public boolean checkForInteractionOLD(Boundary bounds){
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
	
	/*public boolean checkForCollision(Boundary bounds) {
		if (  ){
			
		}
		else {
			return false;
		}
	}*/
	
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
		
			for (int j = 0 ; j < intersectingSidesA.size() ; j++) { // compile arrayList pairs into regular array
				intersectingSides[j] = intersectingSidesA.get(j);
			}
			intersectingSidesA = null; //or delete from memory
			return intersectingSides;
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

	
	//Finds point of intersection between two sides. This is important for finding depth of clipping 
	public Point getIntersectionPoint(Line2D line1, Line2D line2){ 
		double m1; // break lines into slope - intercept forms    y = mx + b
		double m2;
		double b1;
		double b2;
		double intersectX; // intersectY is  m*intersectX + b  and is calculated at the end rather than a variable 
		
		if ( line1.getP1().getX() == line1.getP2().getX() ) { //Line1 is vertical, slope is undefined
			
			if ( line2.getP1().getX() != line2.getP2().getX() ) { 
				// Line1 is vertical, so x intersect is simply x1 on vertical line 1
				intersectX = line1.getP1().getX() ;
				
				// 
				m2 = ( line2.getP1().getY() - line2.getP2().getY()  ) / ( line2.getP1().getX() - line2.getP2().getX() );
				b2 = line2.getP1().getY() - ( m2 * line2.getP1().getX() );
				return new Point( (int)intersectX , (int)( (m2 * intersectX) + b2 ) );
			}
			else {
				//System.out.println("Identical line"  + line1.getX1());
				return new Point( (int) line1.getX1() , (int)line1.getY1() );
			}
			
		}
		else if ( line2.getP1().getX() == line2.getP2().getX() ){
			
			if ( line1.getP1().getX() != line1.getP2().getX() ) {
				//line2 is vertical, same as above
				intersectX = line2.getP1().getX() ;
				m1 = ( line1.getP1().getY() - line1.getP2().getY()  ) / ( line1.getP1().getX() - line1.getP2().getX() );
				b1 = line1.getP1().getY() - ( m1 * line1.getP1().getX() );
				return new Point( (int)intersectX , (int) ( (m1 * intersectX) + b1 ) );
			}
			else {
				//System.out.println("Identical line" + line1.getX1());
				return new Point( (int) line1.getX1() , (int)line1.getY1() );
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
			
				if (m2*m2 - m1*m1 == 0 ){
					//System.out.println( "identical line" ); 
					return new Point( (int)line1.getX1() , (int)line1.getY1() );
				}
				else {
					intersectX = (b2-b1) / (m1-m2) ; // y1=y2 and x1=x2 at intersection, so m1*x + b1 = m2*x + b2  solved for x
					return new Point( (int) intersectX , (int)( (m1 * intersectX) + b1 )); // intersectY = m*interceptX + b for either line 
				}
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
	
	
	
	//SEPARATING AXIS THEORM METHODS
	
	public double dotProduct(Line2D line1 , Line2D line2){ //Returns the magnitude of the projection vector
		
		return (line1.getX1() - line1.getX2()) * (line2.getX1() - line2.getX2()) + 
				(line1.getY1() - line1.getY2()) * (line2.getY1() - line2.getY2());
	}
	
	public Point2D  getProjectionPoint( Point2D point, Line2D line){ //Returns the point on the line that would be the 
		//start of a normal that is perpendicular to the input line and also intersects the input point. This is also
		//always the one point on the line that is closest in distance to the input point 
		
		if (line.getX1() == line.getX2()){ //Good ol' undefined slope check
			return new Point2D.Double( line.getX1() , point.getY() );
		}
		if (line.getY1() == line.getY2()){ //Slope of zero saves some calculation
			return new Point2D.Double( point.getX() , line.getY1() );
		}
		
		double m1 = (line.getY1() -  line.getY2())/(line.getX1() -  line.getX2());
		double m2 = -1/m1; //normal slope
		
		double b1 = line.getP1().getY() - ( m1 * line.getP1().getX() );	//trash intercept variables, they are not useful
		double b2 = point.getY() - ( m2 * point.getX() );		
		
		double intersectX = (b2-b1) / (m1-m2) ; 
		return new Point2D.Double( intersectX , (m1 * intersectX) + b1 );
		
	}
	
	public Line2D getProjectionLine( Line2D line1 , Line2D line2 ){
		
		double Xproj = ( dotProduct( line1 , line2 ) / (
							(line2.getX1() - line2.getX2()) * (line2.getX1() - line2.getX2()) + 
							(line2.getY1() - line2.getY2()) * (line2.getY1() - line2.getY2()) )
				) * ( line2.getX1() - line2.getX2() ) ;
		
		double Yproj = ( dotProduct( line1 , line2 ) / (
				(line2.getX1() - line2.getX2()) * (line2.getX1() - line2.getX2()) + 
				(line2.getY1() - line2.getY2()) * (line2.getY1() - line2.getY2()) )
	) * ( line2.getY1() - line2.getY2() ) ;
		
		Point2D projectedPoint = getProjectionPoint( line1.getP1() , line2);
		
		return new Line2D.Double(projectedPoint.getX() , projectedPoint.getY() , 
				projectedPoint.getX()-Xproj , projectedPoint.getY()-Yproj );
		
	}
	
	private boolean duplicateSideExists( Line2D side , Line2D[] array, int currentIndex ){
		//checks if axis already exists in previous array indexes before adding a new one
		
		if ( side.getP1().getX() - side.getP2().getX() == 0 ) {//line is vertical, slope is undefined
			for ( int j = 0 ; j < currentIndex ; j++){
				if (array[j].getP1().getX() - array[j].getP2().getX() == 0){ // other vertical sides exist
					return true;
				}
			}
			return false;
		}
		else { // line has defines slope
			for ( int j = 0 ; j < currentIndex ; j++){
				
				if ( 
					Math.abs(
						( ( side.getY1() - side.getY2()  ) / ( side.getX1() - side.getX2() ) )
						-
						( (array[j].getY1() - array[j].getY2()  ) / ( array[j].getX1() - array[j].getX2() ) )
					)
						< 0.1){ //error
					return true;
				}
			}
			return false;
		}
		
	}
	
	public Line2D[] getSeparatingSides(){ 
		
		ArrayList<Line2D> axes = new ArrayList<>();
		
		for ( int i = 0 ; i < sides.length ; i++ ){ // gets unique sides to be used for separating axes

				if ( !duplicateSideExists(sides[i], sides, i) ){
					axes.add(sides[i]);
				}
		}
		
		Line2D[] lines = new Line2D[axes.size()]; //compile final array
		for ( int i = 0 ; i < axes.size(); i++){
			lines[i] = axes.get(i);
		}
		return lines;
		
	}
	
	public Line2D[] getSpearatingSidesBetween(Boundary bounds){
		ArrayList<Line2D> axes = new ArrayList<>();
		
		for ( int i = 0 ; i < sides.length ; i++ ){ // Sides of primary boundary

			if ( !duplicateSideExists(sides[i], sides, i) ){
				axes.add(sides[i]);
			}
		}
		
		for ( int i = 0 ; i < bounds.getSides().length ; i++ ){ // Sides of target boundary

			if ( !duplicateSideExists(bounds.getSides()[i], bounds.getSides(), i) ){
				axes.add(bounds.getSides()[i]);
			}
		}
		
		Line2D[] lines = new Line2D[axes.size()]; //compile final array
		for ( int i = 0 ; i < axes.size(); i++){
			lines[i] = axes.get(i);
		}
		return lines;
		
	}

	
	public Line2D getSeparatingAxis( Line2D separatingSide ){ //OPTIMIZATION CHANGE TO SLOPE ONLY##DONE
		
		if ( separatingSide.getP1().getX() == separatingSide.getP2().getX() ) { //line is vertical
			
				return new Line2D.Double( 0 , 0 , 100 , 0 ); //return normal line which is horizontal with slope 0
			}
		else {// line is not vertical, so it has a defined slope and can be in form y=mx+b

			//return normal line, whose slope is inverse reciprocal of line.   -(1/slope)
			return new Line2D.Double( 0 , 0 , 
					- (separatingSide.getP1().getY() - separatingSide.getP2().getY() ), 
						separatingSide.getP1().getX() - separatingSide.getP2().getX() 
					);
			
		}
		
	}
	
	
	public Line2D debugGetSeparatingAxis( Line2D separatingSide , int xMax, int yMax ){ //OPTIMIZATION CHANGE TO SLOPE ONLY##DONE
		
		if ( separatingSide.getP1().getX() == separatingSide.getP2().getX() ) { //line is vertical
			
				return new Line2D.Double( 0 , 20 , xMax , 20 ); //return normal line which is horizontal with slope 0
			}
		else {// line is not vertical, so it has a defined slope and can be in form y=mx+b

			//return normal line, whose slope is inverse reciprocal of line.   -(1/slope)
			double m = ( separatingSide.getY1() - separatingSide.getY2() )/( separatingSide.getX1() - separatingSide.getX2() );
			int b = (int)( separatingSide.getY1() - ( m*separatingSide.getX1() ) );
			
			if (m==0)
				return new Line2D.Float( 20 , 0 , 20 , yMax );
	
			else { // y=mx+b    y = m*(x - Xoffset ) + yOffset    (y-b)/( x-Xoffset )
				
				return new Line2D.Double( 0 , 
						( -(1/m) * (-(xMax/2) ) ) + (yMax/2) ,
						xMax ,
						( -(1/m) * (xMax/2) ) + (yMax/2)
						);
			}
		}
		
	}
	
	public Line2D[] debugSeparatingAxes(int xMax , int yMax){ 
		
		Line2D[] axes = new Line2D[getSeparatingSides().length];
		
		for ( int i = 0; i < getSeparatingSides().length ; i++){
			axes[i] = debugGetSeparatingAxis( getSeparatingSides()[i], xMax, yMax);
		}
		return axes;
	}
	
	public Point2D[] getCorners(){
		Point2D[] corners = new Point2D[sides.length];
		for (int i = 0 ; i < sides.length ; i++){
			corners[i] = sides[i].getP1();
		}
		return corners;
	}
	
	
	public Point2D getOppositePoint( Point2D center , Line2D axis){
		
		Point2D oppositePoint = getCorners()[0] ; //store the first pair ahead
		
		for ( int i = 0 ; i < getCorners().length ; i++ ){
				
				if (getProjectionPoint( getCorners()[i] , axis ).distance( getProjectionPoint( center , axis ) ) 
						> 
					getProjectionPoint( oppositePoint , axis ).distance( getProjectionPoint( center , axis ) ) 
				){
					// points i and j are farther apart than whats stored
					oppositePoint = getCorners()[i];
				}
		}
		return oppositePoint;
	}
	
	
	public Point2D[] getFarthestPoints(Boundary bounds , Line2D axis){
		
		Point2D[] farthestPoints = new Point2D[]{ getCorners()[0] , bounds.getCorners()[0] }; //store the first pair ahead
		
		for ( int i = 0 ; i < getCorners().length ; i++ ){
			
			for ( int j = 0 ; j < bounds.getCorners().length ; j++ ){
				
				if (getProjectionPoint( getCorners()[i] , axis ).distance( getProjectionPoint( bounds.getCorners()[j] , axis ) ) 
						> 
					getProjectionPoint( farthestPoints[0] , axis ).distance( getProjectionPoint( farthestPoints[1] , axis ) ) 
				){
					// points i and j are farther apart than whats stored
					farthestPoints[0] = getCorners()[i];
					farthestPoints[1] = bounds.getCorners()[j];
				}
				
			}
		}
		return farthestPoints;
	}
	
public Point2D farthestPointFromPoint(Point2D origin , Line2D axis){
		
		Point2D farthestPoint = getCorners()[0]; //store the first pair ahead
		
		for ( int i = 0 ; i < getCorners().length ; i++ ){ //check to start i at 1
				
				if (getProjectionPoint( getCorners()[i] , axis ).distance( getProjectionPoint( origin , axis ) ) 
						> 
					getProjectionPoint( farthestPoint , axis ).distance( getProjectionPoint( origin , axis ) ) 
				){
					farthestPoint = getCorners()[i];
				}
		}
		return farthestPoint;
	}
	
	public Point2D[] getNearestPoints(Boundary bounds , Line2D axis){ //same deal as above just witht he closest points
		
		Point2D[] nearestPoints = new Point2D[]{ getCorners()[0] , bounds.getCorners()[0] }; //store the first pair ahead
		
		for ( int i = 0 ; i < getCorners().length ; i++ ){
			
			for ( int j = 0 ; j < bounds.getCorners().length ; j++ ){
				
				if (getProjectionPoint( getCorners()[i] , axis ).distance( getProjectionPoint( bounds.getCorners()[j] , axis ) ) 
						< 
					getProjectionPoint( nearestPoints[0] , axis ).distance( getProjectionPoint( nearestPoints[1] , axis ) ) 
				){
					// points i and j are farther apart than whats stored
					nearestPoints[0] = getCorners()[i];
					nearestPoints[1] = bounds.getCorners()[j];
				}
				
			}
		}
		return nearestPoints;
	}
	
	public Line2D getTestSide(int i){
		return sides[i];
	}

	

	
}
