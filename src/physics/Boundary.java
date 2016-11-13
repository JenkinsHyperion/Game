package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Boundary implements Serializable {
	
	//protected Shape boundaryShape;
	
	protected Side[] sides = new Side[1]; 
	protected Vertex[] corners;

	@Deprecated
	public Boundary(){
		sides = new Side[0];
		corners = new Vertex[0];
	} //use cloning instead
	
	private Boundary( Side[] sides , Vertex[] corners ){
		this.sides = sides;
		this.corners = corners;
	}
	
	public Boundary(Line2D line){
		sides[0] = new Side(line , 0); 
		corners = new Vertex[]{ new Vertex(line.getP1(),0) , new Vertex(line.getP2(),1) };
		compileBoundaryMap();
	}
	
	public Boundary(Side[] bounds) {
		sides = bounds;
		compileBoundaryMap();
	}
	
	public Boundary(Line2D[] bounds) {
		
		sides = null;
		sides = new Side[ bounds.length ];
		
		for ( int i = 0 ; i < bounds.length ; i++ ){
			sides[i] = new Side( bounds[i] , i );
		}
		compileBoundaryMap();
	}
	
	public static class Box extends Boundary{

		public Box(int width, int height, int xOffset, int yOffset){
			
			sides = new Side[4];
			
			sides[0] = new Side( new Line2D.Float(xOffset , yOffset , xOffset+width , yOffset ) , 0 );
			sides[1] = new Side( new Line2D.Float(xOffset+width , yOffset , xOffset+width , yOffset+height ) , 1 );
			sides[2] = new Side( new Line2D.Float(xOffset+width , yOffset+height , xOffset , yOffset+height ) , 2 );
			sides[3] = new Side( new Line2D.Float(xOffset , yOffset+height , xOffset , yOffset ) , 3 );
			compileBoundaryMap();
		}
		
	}
	
	protected void compileBoundaryMap(){
		
		corners = new Vertex[ sides.length  ];
		corners[0] = new Vertex( sides[0].getP1() , 0 );
		
		for (int i = 0 ; i < sides.length ; i++) {
			
			int iNext = (i+1) % sides.length;
			
			corners[ iNext ]  = new Vertex( sides[i].getP2() , sides[i] , sides[iNext] , iNext );
			
			sides[i].setStartPoint( corners[i] ); 
			sides[i].setEndPoint( corners[ iNext ] );

		}	
	}
	
	protected void linkBoundary(){
		
		sides[0].setStartPoint(corners[0]);
		sides[0].setEndPoint(corners[1]);
		
		for (int i = 1 ; i < sides.length-1 ; i++) {	
			sides[i].setStartPoint( corners[i] ); 
			corners[i].setStartingSide(sides[i]);
			sides[i].setEndPoint( corners[ i+1 ] );
			corners[i].setEndingSide(sides[i-1]);
		}
		
		
	}
	
	@Override
	public Boundary clone(){  

		Boundary returnBounds = new Boundary(this.sides , this.corners);
		
		
		
		return returnBounds;
		
	}
	
	public Boundary rotateBoundaryAround(Point center, double angle){ //OPTIMIZATION TRIG FUNCTIONS ARE NOTORIOUSLY EXPENSIVE Look into performing some trig magic
		// with fast trig approximations
		//THIS IS DOUBLING EVERY VERTEX BY DOING LINES, DO BY VERTEX INSTEAD!!!

		Side[] newSides = new Side[this.getSides().length];
		
		for ( int i = 0 ; i < this.sides.length ; i++ ) {
			
			Side side = this.sides[i];
			Point origin = new Point(center.x,center.y);
			
			double r = side.getP1().distance(origin); 
			double a = Math.acos( (side.getX1()-center.x) / r );
			if (side.getY1() > center.y){ a = (2*Math.PI) - a ;}
			
			Point p1 = new Point( (int)(r * Math.cos( a + angle  )  ) , (int)(r * Math.sin( a + angle ) )    );
			
			double r2 = side.getP2().distance(origin);
			double a2 = Math.acos( (side.getX2()-center.x) / r );
			if (side.getY2() > center.y){ a2 = (2*Math.PI) - a2 ;}
			
			Point p2 = new Point( (int)(r2 * Math.cos( a2 + angle  ) ) , (int)(r2 * Math.sin( a2 + angle  ) )  );
			
			newSides[i] = new Side( new Line2D.Float(p1,p2) , i );
			
		}
		
		return new Boundary(newSides);
		//return returnedBounds;
	}
	
	
	
	//Cycle through all sides of two shapes and check for intersections
	public boolean boundaryIntersects(Boundary bounds){ 
		
		for (int i = 0 ; i < sides.length ; i++) {
			
			for ( Side side : bounds.getSides()) {
				
				if ( sides[i].toLine().intersectsLine( side.toLine() ) ) {
					return true;
				}
				
			}
			
		}
		return false;
	}
	
	//Cycle through all sides of two shapes and get the sides that intersect
	public Side[][] getIntersectingSides(Boundary bounds){ //returns pairs of sides of this boundary that are intesecting
		
		ArrayList<Side[]> intersectingSidesA = new ArrayList<Side[]>(); //array of *pairs* of intersecting lines
		
		for (int i = 0 ; i < sides.length ; i++) { // cycle through all sides. OPTIMIZATION NEEDED
			
			for ( int j = 0 ; j < bounds.getSides().length ; j++ ) {
				
				if ( sides[i].toLine().intersectsLine( bounds.getSides()[j].toLine() ) ) { 
					
					//place intersecting line pair into array
					intersectingSidesA.add( new Side[]{ sides[i] , bounds.getSides()[j] } );	
				}
			}
		}
		
		//System.out.println( intersectingSidesA.size() + " intersecting sides found" );
		
		Side[][] intersectingSides = new Side[intersectingSidesA.size()][2]; // create final regular array
		
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

						if ( pointIsAgainstSide(bounds.getSides()[j].getP1(), sides[i] ) ) { 
							if ( pointIsAgainstSide(bounds.getSides()[j].getP2(), sides[i]) ) {
								//sides i and j are flush
								
								if (  sidesHaveContact(bounds.getSides()[j], sides[i]) )  {
									return true;
								}
								
							}
						}

						if ( pointIsAgainstSide(sides[i].getP1(), bounds.getSides()[j]) ) {
							if ( pointIsAgainstSide(sides[i].getP2(), bounds.getSides()[j]) ) {
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
	public Side[] getContactingSides(Boundary bounds){ 
		
		Side[] contactingSides = new Side[2];
		
		for (int i = 0 ; i < sides.length ; i++) {
			
			for ( int j = 0 ; j < bounds.getSides().length ; j++ ) {
	
					if ( pointIsAgainstSide(bounds.getSides()[j].getP1(), sides[i]) ) { 
						if ( pointIsAgainstSide(bounds.getSides()[j].getP2(), sides[i]) ) {
							//sides i and j are flush
							
							if (  sidesHaveContact(bounds.getSides()[j], sides[i])  ){
								contactingSides[0] = bounds.getSides()[j];
								contactingSides[1] = sides[i];
								return contactingSides;
							} 
							
						}
					}
	
					if ( pointIsAgainstSide(sides[i].getP1(), bounds.getSides()[j]) ) {
						if ( pointIsAgainstSide(sides[i].getP2(), bounds.getSides()[j]) ) {
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

	public boolean sidesHaveContact(Side side1, Side side2) { 
		
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

		double intersectX; // intersectY is  m*intersectX + b  so is calculated at the end
		
		// since these are double, they will never equal eachother because of precision errors - Dave
		if ( line1.getP1().getX() == line1.getP2().getX() ) { //Line is vertical, slope in undefined

			
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
			
				if ( (m2*m2 - m1*m1) > -.00001 && (m2*m2 - m1*m1) < .00001){
					//System.out.println( "identical line" ); 
					return new Point( (int)line1.getX1() , (int)line1.getY1() );
				}
				else {
					intersectX = (b2-b1) / (m1-m2) ; // y1=y2 and x1=x2 at intersection, so m1*x + b1 = m2*x + b2  solved for x
					return new Point( (int) intersectX , (int)( (m1 * intersectX) + b1 )); // intersectY = m*interceptX + b for either line 
				}
		}

	}
	
	
	private boolean pointIsAgainstSide(Point2D point, Side line){
		double dist = Math.abs( line.toLine().ptLineDist( point ) );  
		if ( dist > 0 && dist <= 1 ) {
			return true;			
		}
		else {
			return false;
		}
	}
	
	private boolean pointIsAgainstSegment(Point2D point, Side side){
		double dist = Math.abs( side.toLine().ptSegDist(point)) ;  	
		if ( dist > 0 && dist <= 1 ) {
			return true;			
		}
		else {
			return false;
		}
	}
	
	
	public Side[] getSides(){
		return sides;
	}
	
	public void constructSides(Side[] sidesC){
		sides = sidesC;
	}

	// Returns boundary shifted to some position, usually the position of the entity that owns the boundary
	public Boundary atPosition(Point pos) {

		Side[] shiftedSides = new Side[sides.length];
		
		for ( int i = 0 ; i < sides.length ; i++ ){
			shiftedSides[i] = new Side (
					new Line2D.Double(sides[i].getX1()+pos.x, sides[i].getY1()+pos.y , 
					sides[i].getX2()+pos.x, sides[i].getY2()+pos.y ) ,
					i
				);
		}

		return new Boundary(shiftedSides);
		
	};
	
	public Boundary atPosition(int x , int y) {

		Side[] shiftedSides = new Side[sides.length];
		
		for ( int i = 0 ; i < sides.length ; i++ ){
			shiftedSides[i] = new Side (
					new Line2D.Double(sides[i].getX1()+x, sides[i].getY1()+y , 
					sides[i].getX2()+x, sides[i].getY2()+y ) ,
					i
				);
		}
		
		return new Boundary(shiftedSides);
		
	};
	
	
	
	//SEPARATING AXIS THEORM METHODS
	
	public double dotProduct(Line2D line1 , Line2D line2){ //Returns the magnitude of the projection vector
		
		return (line1.getX1() - line1.getX2()) * (line2.getX1() - line2.getX2()) + 
				(line1.getY1() - line1.getY2()) * (line2.getY1() - line2.getY2());
	}
	
	public Point2D  getProjectionPoint( Point2D point, Line2D line){ //Returns the point on the line where the input point is
		//projected on the line
		
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
	
	private boolean duplicateSideExists( Side side , ArrayList<Line2D> array ){
		//checks if axis already exists in previous array indexes before adding a new one
		
		if ( (side.getP1().getX() - side.getP2().getX() > -.00001) &&
			 (side.getP1().getX() - side.getP2().getX() < .00001) ) {//line is vertical, slope is undefined
			for ( int j = 0 ; j < array.size() ; j++){
				if ( (array.get(j).getP1().getX() - array.get(j).getP2().getX() > -.00001) &&
						(array.get(j).getP1().getX() - array.get(j).getP2().getX() < .00001) ){ // other vertical sides exist
					return true;
				}
			}
			return false;
		}
		
		else { // line has defined slope
			for ( int j = 0 ; j < array.size() ; j++){
				if (array.get(j).getX1() - array.get(j).getX2() != 0) { //discard vertical lines  OPTIMIZATION 
					if ( 
						Math.abs(
							( ( side.getY1() - side.getY2()  ) / ( side.getX1() - side.getX2() ) )
							-
							( (array.get(j).getY1() - array.get(j).getY2()  ) / ( array.get(j).getX1() - array.get(j).getX2() ) )
						)
							< 0.1
					){ //error
						return true;
					}
				}
			}
			return false;
		}
		
	}
	
	public Line2D[] getSeparatingSides(){  //LOOK FOR OPTIMIZATION IN SIDE.TOLINE()
		
		ArrayList<Line2D> axes = new ArrayList<>();
		
		for ( int i = 0 ; i < sides.length ; i++ ){ // gets unique sides to be used for separating axes

				if ( !duplicateSideExists(sides[i], axes) ){
					axes.add(sides[i].toLine() );
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

			if ( !duplicateSideExists(sides[i], axes) ){
				axes.add(sides[i].toLine() );
			}
		}
		
		for ( int i = 0 ; i < bounds.getSides().length ; i++ ){ // Sides of target boundary

			if ( !duplicateSideExists(bounds.getSides()[i], axes ) ){
				axes.add(bounds.getSides()[i].toLine() );
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
			
			if ( (m > -.00001) && (m < .00001))
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
	/**
	 * Returns array of corners of type (Point2D) for this boundary. If information is needed about sides connected to this point,
	 * use getCornerVertex() instead.
	 * @return
	 */
	public Point2D[] getCornersPoint(){
		Point2D[] corners = new Point2D[sides.length];
		for (int i = 0 ; i < sides.length ; i++){
			corners[i] = sides[i].getP1();
		}
		return corners;
	}
	/**
	 * Returns array of corners of type (Vertex) for this boundary, which contains pointers to adjacent sides.
	 * @return
	 */
	public Vertex[] getCornersVertex(){

		return this.corners;
	}
	
	
	public Point2D getOppositePoint( Point2D center , Line2D axis){
		
		Point2D oppositePoint = getCornersPoint()[0] ; //store the first pair ahead
		
		for ( int i = 0 ; i < getCornersPoint().length ; i++ ){
				
				if (getProjectionPoint( getCornersPoint()[i] , axis ).distance( getProjectionPoint( center , axis ) ) 
						> 
					getProjectionPoint( oppositePoint , axis ).distance( getProjectionPoint( center , axis ) ) 
				){
					// points i and j are farther apart than whats stored
					oppositePoint = getCornersPoint()[i];
				}
		}
		return oppositePoint;
	}
	
	
	public Point2D[] getFarthestPoints(Boundary bounds , Line2D axis){
		
		Point2D[] farthestPoints = new Point2D[]{ getCornersPoint()[0] , bounds.getCornersPoint()[0] }; //store the first pair ahead
		
		for ( int i = 0 ; i < getCornersPoint().length ; i++ ){
			
			for ( int j = 0 ; j < bounds.getCornersPoint().length ; j++ ){
				
				if (getProjectionPoint( getCornersPoint()[i] , axis ).distance( getProjectionPoint( bounds.getCornersPoint()[j] , axis ) ) 
						> 
					getProjectionPoint( farthestPoints[0] , axis ).distance( getProjectionPoint( farthestPoints[1] , axis ) ) 
				){
					// points i and j are farther apart than whats stored
					farthestPoints[0] = getCornersPoint()[i];
					farthestPoints[1] = bounds.getCornersPoint()[j];
				}
				
			}
		}
		return farthestPoints;
	}
	
	public Point2D getFarthestPoint( Line2D axis ){
		
		ArrayList<Point2D> outerPoints = new ArrayList<>();
		Point2D outerPoint = this.getCornersPoint()[0];
		double farthestDistances = 0;
	
		for ( int i = 0 ; i < this.getCornersPoint().length ; i++ ){
				
			double distance = this.getCornersPoint()[0].distance( getProjectionPoint( this.getCornersPoint()[i] , axis ) );
			
			if ( distance > farthestDistances ){
				farthestDistances = distance;
				outerPoint = this.getCornersPoint()[i];
			}
				
		}
		return outerPoint;
	}
	
	public Point2D farthestPointFromPoint(Point2D origin , Line2D axis){
		
		Point2D farthestPoint = getCornersPoint()[0]; //store the first pair ahead
		
		for ( int i = 0 ; i < getCornersPoint().length ; i++ ){ //check to start i at 1
				
				if (getProjectionPoint( getCornersPoint()[i] , axis ).distance( getProjectionPoint( origin , axis ) ) 
						> 
					getProjectionPoint( farthestPoint , axis ).distance( getProjectionPoint( origin , axis ) ) 
				){
					farthestPoint = getCornersPoint()[i];
				}
		}
		return farthestPoint;
	}
	
	public Point2D[] farthestPointsFromPoint(Point2D origin , Line2D axis){ //TESTING
		
		ArrayList<Point2D> farthestPoints = new ArrayList<>();
		farthestPoints.add(getCornersPoint()[0]);
		
		for ( int i = 0 ; i < getCornersPoint().length ; i++ ){ //check to start i at 1
			
				Point2D originProjection = getProjectionPoint( origin , axis );
				Point2D cornerProjection = getProjectionPoint( getCornersPoint()[i] , axis ); 
				Point2D farthestPointProjection = getProjectionPoint( farthestPoints.get(0) , axis );
				
				if (cornerProjection.distance( originProjection ) < farthestPointProjection.distance( originProjection )  ){
					
				}
				else {
					if ( cornerProjection.distance( originProjection ) == farthestPointProjection.distance( originProjection ) ){
						//duplicate
						farthestPoints.add( getCornersPoint()[i] );
					}
					else {
						farthestPoints.removeAll(farthestPoints);
						farthestPoints.add( getCornersPoint()[i] );
					}
				}
		}
		Point2D[] returnFarthestPoints = new Point2D[ farthestPoints.size() ];
		for (int i = 0 ; i < returnFarthestPoints.length ; i++){
			returnFarthestPoints[i] = farthestPoints.get(i);
		}
		return returnFarthestPoints;
	}
	
	public Vertex[] farthestVerticesFromPoint(Point2D origin , Line2D axis){ //RETURNING DUPLICATES?
		
		ArrayList<Vertex> farthestVertices = new ArrayList<>();
		farthestVertices.add(getCornersVertex()[0]);
		
		for ( int i = 1 ; i < getCornersPoint().length ; i++ ){ //check to start i at 1
			
				Point2D originProjection = getProjectionPoint( origin , axis );
				Point2D cornerProjection = getProjectionPoint( getCornersPoint()[i] , axis ); 
				Point2D farthestPointProjection = getProjectionPoint( farthestVertices.get(0).toPoint() , axis );
				
				if (cornerProjection.distance( originProjection ) < farthestPointProjection.distance( originProjection )  ){
					//discard this vertex
				}
				else {
					if ( cornerProjection.distance( originProjection ) == farthestPointProjection.distance( originProjection ) ){
						//duplicate
						farthestVertices.add( getCornersVertex()[i] );
					}
					else {
						farthestVertices.removeAll(farthestVertices);
						farthestVertices.add( getCornersVertex()[i] );
					}
				}
		}
		Vertex[] returnFarthestPoints = new Vertex[ farthestVertices.size() ];
		for (int i = 0 ; i < returnFarthestPoints.length ; i++){
			returnFarthestPoints[i] = farthestVertices.get(i);
		}
		return returnFarthestPoints;
	}
	
	
	public Vertex[] nearestVerticesFromPoint(Point2D origin , Line2D axis){ //RETURNING DUPLICATES?
		
		ArrayList<Vertex> farthestVertices = new ArrayList<>();
		farthestVertices.add(getCornersVertex()[0]);
		
		for ( int i = 1 ; i < getCornersPoint().length ; i++ ){ //check to start i at 1
			
				Point2D originProjection = getProjectionPoint( origin , axis );
				Point2D cornerProjection = getProjectionPoint( getCornersPoint()[i] , axis ); 
				Point2D farthestPointProjection = getProjectionPoint( farthestVertices.get(0).toPoint() , axis );
				
				if (cornerProjection.distance( originProjection ) > farthestPointProjection.distance( originProjection )  ){
					//discard this vertex
				}
				else {
					if ( cornerProjection.distance( originProjection ) == farthestPointProjection.distance( originProjection ) ){
						//duplicate
						farthestVertices.add( getCornersVertex()[i] );
					}
					else {
						farthestVertices.removeAll(farthestVertices);
						farthestVertices.add( getCornersVertex()[i] );
					}
				}
		}
		Vertex[] returnFarthestPoints = new Vertex[ farthestVertices.size() ];
		for (int i = 0 ; i < returnFarthestPoints.length ; i++){
			returnFarthestPoints[i] = farthestVertices.get(i);
		}
		return returnFarthestPoints;
	}
	
	
	public Point2D[] getNearestPoints(Boundary bounds , Line2D axis){ //same deal as above just witht he closest points
		
		Point2D[] nearestPoints = new Point2D[]{ getCornersPoint()[0] , bounds.getCornersPoint()[0] }; //store the first pair ahead
		
		for ( int i = 0 ; i < getCornersPoint().length ; i++ ){
			
			for ( int j = 0 ; j < bounds.getCornersPoint().length ; j++ ){
				
				if (getProjectionPoint( getCornersPoint()[i] , axis ).distance( getProjectionPoint( bounds.getCornersPoint()[j] , axis ) ) 
						< 
					getProjectionPoint( nearestPoints[0] , axis ).distance( getProjectionPoint( nearestPoints[1] , axis ) ) 
				){
					// points i and j are farther apart than whats stored
					nearestPoints[0] = getCornersPoint()[i];
					nearestPoints[1] = bounds.getCornersPoint()[j];
				}
				
			}
		}
		return nearestPoints;
	}
	
	public Line2D getTestSide(int i){
		return sides[i].toLine();
	}

	
	
	

	
}
