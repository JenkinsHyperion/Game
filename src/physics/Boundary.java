package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import entityComposites.Collider;
import misc.*;

public class Boundary {
	
	//protected Shape boundaryShape;
	protected Collider ownerCollidable;
	protected Side[] sides = new Side[1]; 
	private BoundaryVertex[] corners;
	
	protected CollisionEvent defaultCollisionEvent;

	public Boundary( Collider ownerCollidable ){
		sides = new Side[0];
		corners = new BoundaryVertex[0];
		this.ownerCollidable = ownerCollidable;
	} //use cloning instead
	//FOR CLONING ONLY
	private Boundary( Side[] sides , BoundaryVertex[] corners , Collider owner ){
		this.sides = sides;
		this.corners = corners;
		this.ownerCollidable = owner;
	}
	
	public Boundary(Line2D line , Collider owner){
		
		this.ownerCollidable = owner;
		
		sides[0] = new Side(line , this , 0, defaultCollisionEvent); 
		corners = new BoundaryVertex[]{ new BoundaryVertex(line.getP1() , this , 0 , defaultCollisionEvent) 
				, new BoundaryVertex(line.getP2() , this , 1 , defaultCollisionEvent) };

		compileBoundaryMap( new DefaultCollisionEvent( ) );
	}
	
	public Boundary(Side[] bounds , Collider owner) {
		sides = bounds;
		this.ownerCollidable = owner;
		compileBoundaryMap( new DefaultCollisionEvent( ) );
	}
	
	public Boundary(Line2D[] bounds , Collider owner) {
		
		this.ownerCollidable = owner;

		sides = new Side[ bounds.length ];
		
		for ( int i = 0 ; i < bounds.length ; i++ ){
			sides[i] = new Side( bounds[i] , this , i , defaultCollisionEvent);
		}
		compileBoundaryMap( new DefaultCollisionEvent( ) );
	}
	
	public static class Box extends Boundary{

		public Box(int width, int height, int xOffset, int yOffset , Collider owner ){
			super(owner);
			sides = new Side[4];
			
			sides[0] = new Side( new Line2D.Float(xOffset , yOffset , xOffset+width , yOffset ) , this, 0 , defaultCollisionEvent);
			sides[1] = new Side( new Line2D.Float(xOffset+width , yOffset , xOffset+width , yOffset+height ) , this, 1 , defaultCollisionEvent);
			sides[2] = new Side( new Line2D.Float(xOffset+width , yOffset+height , xOffset , yOffset+height ) , this, 2 , defaultCollisionEvent);
			sides[3] = new Side( new Line2D.Float(xOffset , yOffset+height , xOffset , yOffset ) , this, 3 , defaultCollisionEvent);
			compileBoundaryMap( new DefaultCollisionEvent( ) );
		}
		
	}
	
	public static class EnhancedBox extends Boundary{

		public EnhancedBox(int width, int height, int xOffset, int yOffset, CollisionEvent[] eventList , Collider owner){
			
			super(owner);
			
			sides = new Side[4];
			
			sides[0] = new Side( new Line2D.Float(xOffset , yOffset , xOffset+width , yOffset ) , this, 0 , eventList[0]);
			sides[1] = new Side( new Line2D.Float(xOffset+width , yOffset , xOffset+width , yOffset+height ) , this, 1 , eventList[1]);
			sides[2] = new Side( new Line2D.Float(xOffset+width , yOffset+height , xOffset , yOffset+height ) , this, 2 , eventList[2]);
			sides[3] = new Side( new Line2D.Float(xOffset , yOffset+height , xOffset , yOffset ) , this, 3 , eventList[3]);
			compileBoundaryMap( new DefaultCollisionEvent( ) );
		}
		
	}
	
	protected void compileBoundaryMap( CollisionEvent cornerEvent ){
		
		corners = new BoundaryVertex[ sides.length  ];
		corners[0] = new BoundaryVertex( sides[0].getP1() , this , 0 , cornerEvent );
		
		for (int i = 0 ; i < sides.length ; i++) {
			
			int iNext = (i+1) % sides.length;
			
			corners[ iNext ]  = new BoundaryVertex( sides[i].getP2() , sides[i] , sides[iNext] , this , iNext , cornerEvent);
			
			sides[i].setStartPoint( corners[i] ); 
			sides[i].setEndPoint( corners[ iNext ] );

		}	
	}

	private Boundary temporaryClone(){  

		Side[] newSides = new Side[this.sides.length];
		for ( int i = 0 ; i < newSides.length ; i++ ){
			Side oldSide = sides[i];
			newSides[i] = new Side( oldSide.toLine() , this , oldSide.getID() , oldSide.getEvent() );
		}
		
		BoundaryVertex[] newCorners = new BoundaryVertex[this.corners.length];
		for ( int i = 0 ; i < newCorners.length ; i++ ){
			BoundaryVertex oldCorner = corners[i] ;
			newCorners[i] = new BoundaryVertex( oldCorner.toPoint() , this, oldCorner.getID() , oldCorner.getEvent() );
		}
		
		Boundary returnBounds = new Boundary( newSides , newCorners , this.ownerCollidable); // Make clone with identical positions and events
		
		for ( int i = 0 ; i < returnBounds.sides.length ; i++){ //Connect the pointers for the new clone
			
			int iNext = (i+1) % this.sides.length;
			
			returnBounds.corners[iNext].setStartingSide( returnBounds.sides[i] );
			returnBounds.corners[iNext].setEndingSide( returnBounds.sides[iNext] );

			returnBounds.sides[i].setStartPoint( returnBounds.corners[i] );
			returnBounds.sides[i].setEndPoint( returnBounds.corners[iNext] );
		}
		
		return returnBounds;
	}
	
	
	
	public void rotateBoundaryFromTemplate(Point center, double angle , Boundary template){ //OPTIMIZATION TRIG FUNCTIONS ARE NOTORIOUSLY EXPENSIVE Look into performing some trig magic
		// with fast trig approximations
		//THIS IS DOUBLING EVERY VERTEX BY DOING LINES, DO BY VERTEX INSTEAD!!!
		
		for ( int i = 0 ; i < template.corners.length ; i++ ) {
			
			Side side = template.sides[i];
			BoundaryVertex corner = template.corners[i];
			Point origin = new Point(center.x,center.y);
			
			double r = corner.toPoint().distance(origin); 
			double a = -Math.acos( (corner.getX()-center.x) / r );
			if (corner.getY() > center.y){ a = (2*Math.PI) - a ;} //clamp range to 0-2pi
			
			Point2D p1 = new Point2D.Double( 
					Math.round(r * Math.cos( a + angle  ) ) , 
					Math.round(r * Math.sin( a + angle ) )    );
			
			this.corners[i].setPos( p1 );
		}
		
		for (int i = 0 ; i < this.sides.length ; i++){ //Connect the vertex pairs with their corresponding side
			
			int indexNext = (i+1) % template.sides.length; //indexNext wraps to 0 if past array length
			
			this.sides[i].setLine( this.corners[i].toPoint() , this.corners[indexNext].toPoint() );
			
		}

	}
	
	public Point rotateBoundaryFromTemplatePoint(Point center, double angle , Boundary template){ //OPTIMIZATION TRIG FUNCTIONS ARE NOTORIOUSLY EXPENSIVE Look into performing some trig magic

		for ( int i = 0 ; i < template.corners.length ; i++ ) {
			
			Point corner = template.corners[i].toPoint();
			
			double r = corner.distance(center); 
			
			if ( r != 0 ){ // otherwise the new point = the old point and nothing needs to be done
				double a = Math.acos( (corner.getX()-center.x) / r ); 
				//if (corner.getY() > center.y){ a = (2*Math.PI) - a ;} //clamp range to 0-2pi
				
				Point2D p1 = new Point2D.Double( 
						( r * Math.cos( a + angle ) + center.x ) , 
						( r * Math.sin( a + angle ) + center.y  )   );
				
				this.corners[i].setPos( p1 );
				
			}
		}
		
		for (int i = 0 ; i < this.sides.length ; i++){ //Connect the vertex pairs with their corresponding side //CONDENSE WITH ABOVE
			
			int indexNext = (i+1) % template.sides.length; //indexNext wraps to 0 if past array length
			
			this.sides[i].setLine( this.corners[i].toPoint() , this.corners[indexNext].toPoint() );
			
		}
		
		double r = new Point2D.Double(0,0).distance(center); 
		
		double a = Math.acos( ( /* 0 */ - center.x) / r ); 
		//if (corner.getY() > center.y){ a = (2*Math.PI) - a ;} //clamp range to 0-2pi
			
		Point p = new Point( 
				(int)( r * Math.cos( a + angle ) + center.x ) , 
				(int)( r * Math.sin( a + angle ) + center.y  )   );
			
		return p;

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
	
	public Side getSides( int ID){
		return sides[ID];
	}
	
	public BoundaryVertex[] getVertices() {
		return this.corners;
	}
	
	public BoundaryVertex getRawVertex( int ID) {
		return this.corners[ID];
	}
	
	public void constructSides(Side[] sidesC){
		sides = sidesC;
	}
/*
	// Returns boundary shifted to some position, usually the position of the entity that owns the boundary
	public Boundary atPosition2(Point pos) {
		
		Side[] shiftedSides = new Side[sides.length];
		
		for ( int i = 0 ; i < sides.length ; i++ ){
			shiftedSides[i] = new Side (
					new Line2D.Double(sides[i].getX1()+pos.x, sides[i].getY1()+pos.y , 
					sides[i].getX2()+pos.x, sides[i].getY2()+pos.y ) ,
					this ,
					i,
					sides[i].getEvent()
				);
		}

		return new Boundary(shiftedSides , this.ownerCollidable);
		
	};*/
	
	public Boundary atPosition( Point position) {

		Boundary returnBoundary = this.temporaryClone();
		int x = (int)position.x;
		int y = (int)position.y;
		
		for ( int i = 0 ; i < this.sides.length ; i++ ){
			
			Side oldSide = this.sides[i];
			Line2D shiftedLine = new Line2D.Float( oldSide.getX1()+x, oldSide.getY1()+y , oldSide.getX2()+x, oldSide.getY2()+y );
	
			returnBoundary.sides[i].setLine( shiftedLine );
			//returnBoundary.sides[i] = new Side( shiftedLine , oldSide.owner , oldSide.ID , oldSide.getEvent() );
		}
		
		for ( int i = 0 ; i < this.corners.length ; i++ ){
			
			BoundaryVertex oldCorner = this.corners[i];
			Point shiftedPosition = new Point( oldCorner.getX()+x , oldCorner.getY()+y );
			
			returnBoundary.corners[i].setPos( shiftedPosition );
			//returnBoundary.corners[i] = new Vertex( shiftedPosition , oldCorner.getStartingSide() , oldCorner.getEndingSide() , oldCorner.owner , oldCorner.ID , oldCorner.getEvent() );
		}
		
		//No problem with sides[]
		return returnBoundary;
	};
	
	
	public Collider getOwnerCollidable(){
		return this.ownerCollidable;
	}
	
	
	//SEPARATING AXIS THEORM METHODS
	
	public double dotProduct(Line2D line1 , Line2D line2){ //Returns the magnitude of the projection vector
		
		return (line1.getX1() - line1.getX2()) * (line2.getX1() - line2.getX2()) + 
				(line1.getY1() - line1.getY2()) * (line2.getY1() - line2.getY2());
	}
	
	public Point2D  getProjectionPoint( Point2D point, Line2D line){ //Returns the point on the line where the input point is
		//projected on the line
		
		if (line.getX1() == line.getX2()){ //Good ol' undefined slope check
			return new Point2D.Double( line.getX1() , point.getY() ); // if projection base is vertical, y on line is just y of point
		}
		if (line.getY1() == line.getY2()){ //Slope of zero saves some calculation
			return new Point2D.Double( point.getX() , line.getY1() ); // same as above but for x
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
		
		if ( (side.getP1().getX() - side.getP2().getX() > -0.01) &&
			 (side.getP1().getX() - side.getP2().getX() < 0.01) ) {//line is vertical, slope is undefined
			
			for ( int j = 0 ; j < array.size() ; j++){
				if ( (array.get(j).getP1().getX() - array.get(j).getP2().getX() > -0.01) &&
						(array.get(j).getP1().getX() - array.get(j).getP2().getX() < 0.01) ){ // other vertical sides exist
					return true;
				}
			}
			return false;
		}
		
		else { // line has defined slope
			for ( int j = 0 ; j < array.size() ; j++){
				if (array.get(j).getX1() - array.get(j).getX2() != 0) { //discard vertical lines  OPTIMIZATION 
					
					double slope1 = ( side.getY1() - side.getY2()  ) / ( side.getX1() - side.getX2() );
					double slope2 = ( (array.get(j).getY1() - array.get(j).getY2()  ) / ( array.get(j).getX1() - array.get(j).getX2() ) );
					
					if ( ( slope1 - slope2 ) > 0 ){//slope1 is greater
							
						if( Math.abs( slope1 - slope2 ) < 0.11 ) //precision angle is arcSine of this, so ArcSine(0.2) = 11 degrees
							return true;
						else
							return false;
						
					}
					else{ // slope2 is greater
					
						if( Math.abs( slope2 - slope1 ) < 0.1 )
							return true;
						else
							return false;
						
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

			if ( true ){//!duplicateSideExists(sides[i], axes) ){
				axes.add(sides[i].toLine() );
			}
		}
		
		for ( int i = 0 ; i < bounds.getSides().length ; i++ ){ // Sides of target boundary

			if ( true ){//!duplicateSideExists(bounds.getSides()[i], axes ) ){
				axes.add(bounds.getSides()[i].toLine() );
			}
		}
		
		Line2D[] lines = new Line2D[axes.size()]; //compile final array
		for ( int i = 0 ; i < axes.size(); i++){
			lines[i] = axes.get(i);
		}
		return lines;
		
	}

	
	public static Line2D getSeparatingAxis( Line2D separatingSide ){ //OPTIMIZATION CHANGE TO SLOPE ONLY##DONE
		
		if ( separatingSide.getP1().getX() == separatingSide.getP2().getX() ) { //line is vertical
			
				return new Line2D.Double( 0 , separatingSide.getY1() , 100 , separatingSide.getY1() ); //return normal line which is horizontal with slope 0
			}
		else {// line is not vertical, so it has a defined slope and can be in form y=mx+b

			//return normal line, whose slope is inverse reciprocal of line.   -(1/slope)
			return new Line2D.Double( 
					0 , 
					0 , 
					(separatingSide.getY2() - separatingSide.getY1() ), 
					-(separatingSide.getX2() - separatingSide.getX1() )
					);
			
		}
		
	}
	
	
	public static Line2D debugGetSeparatingAxis( Line2D separatingSide , int xMax, int yMax , Point intersect){ //OPTIMIZATION CHANGE TO SLOPE ONLY##DONE
		
		if ( separatingSide.getP1().getX() == separatingSide.getP2().getX() ) { //line is vertical
			
				return new Line2D.Double( 0 , intersect.y , xMax , intersect.y ); //return normal line which is horizontal with slope 0
			}
		else {// line is not vertical, so it has a defined slope and can be in form y=mx+b

			//return normal line, whose slope is inverse reciprocal of line.   -(1/slope)
			double m = ( separatingSide.getY1() - separatingSide.getY2() )/( separatingSide.getX1() - separatingSide.getX2() );
			int b = (int)( separatingSide.getY1() - ( m*separatingSide.getX1() ) );
			
			if ( (m > -.00001) && (m < .00001))
				return new Line2D.Float( intersect.x , 0 , intersect.x , yMax );
	
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
			axes[i] = debugGetSeparatingAxis( getSeparatingSides()[i], xMax, yMax , new Point(20,20) );
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
	public BoundaryVertex[] getCornersVertex(){

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
	
	
	public BoundaryVertex[] getFarthestVertices(Boundary bounds , Line2D axis){
		
		BoundaryVertex[] farthestPoints = new BoundaryVertex[]{ getCornersVertex()[0] , bounds.getCornersVertex()[0] }; //store the first pair ahead
		
		for ( int i = 0 ; i < getCornersPoint().length ; i++ ){
			
			for ( int j = 0 ; j < bounds.getCornersPoint().length ; j++ ){
				
				if (getProjectionPoint( getCornersPoint()[i] , axis ).distance( getProjectionPoint( bounds.getCornersPoint()[j] , axis ) ) 
						> 
					getProjectionPoint( farthestPoints[0].toPoint() , axis ).distance( getProjectionPoint( farthestPoints[1].toPoint() , axis ) ) 
				){
					// points i and j are farther apart than whats stored
					farthestPoints[0] = getCornersVertex()[i];
					farthestPoints[1] = bounds.getCornersVertex()[j];
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
	
	@Deprecated
	private BoundaryVertex[] farthestPointsFromPoint(Point2D origin , Line2D axis){ //TESTING
		
		ArrayList<BoundaryVertex> farthestPoints = new ArrayList<>();
		farthestPoints.add(getCornersVertex()[0]);
		
		for ( int i = 0 ; i < getCornersPoint().length ; i++ ){ //check to start i at 1
			
				Point2D originProjection = getProjectionPoint( origin , axis );
				Point2D cornerProjection = getProjectionPoint( getCornersPoint()[i] , axis ); 
				Point2D farthestPointProjection = getProjectionPoint( farthestPoints.get(0).toPoint(), axis );
				
				if (cornerProjection.distance( originProjection ) < farthestPointProjection.distance( originProjection )  ){
					
				}
				else {
					if ( cornerProjection.distance( originProjection ) == farthestPointProjection.distance( originProjection ) ){
						//duplicate
						farthestPoints.add( getCornersVertex()[i] );
					}
					else {
						farthestPoints.removeAll(farthestPoints);
						farthestPoints.add( getCornersVertex()[i] );
					}
				}
		}
		BoundaryVertex[] returnFarthestPoints = new BoundaryVertex[ farthestPoints.size() ];
		for (int i = 0 ; i < returnFarthestPoints.length ; i++){
			returnFarthestPoints[i] = farthestPoints.get(i);
		}
		return returnFarthestPoints;
	}
	
	private BoundaryVertex[] farthest( Point2D origin , Line2D axis ){	
		
			ArrayList<BoundaryVertex> farthestVertices = new ArrayList<>();
			farthestVertices.add(getCornersVertex()[0]);
		
			for ( int i = 1 ; i < getCornersVertex().length ; i++ ){ //check to start i at 1
				
				Point2D originProjection = getProjectionPoint( origin , axis );
				Point2D cornerProjection = getProjectionPoint( getCornersPoint()[i] , axis ); 
				Point2D farthestPointProjection = getProjectionPoint( farthestVertices.get(0).toPoint() , axis );
				
				double distanceFarthest = farthestPointProjection.distance( originProjection );
				double distanceTest = cornerProjection.distance( originProjection );

				if ( distanceFarthest - distanceTest > 2 ){
						//discard
				}
				else { // within margin of 2
					
					if ( Math.abs( distanceTest - distanceFarthest ) < 1 ) //within 1, add duplicate
						
						farthestVertices.add( getCornersVertex()[i] );
					
					else { // outside of 1, farthest
						
						farthestVertices.removeAll(farthestVertices);
						farthestVertices.add( getCornersVertex()[i] );
					}

				}
					
			}
			BoundaryVertex[] returnFarthestPoints = new BoundaryVertex[ farthestVertices.size() ];
			for (int i = 0 ; i < returnFarthestPoints.length ; i++){
				returnFarthestPoints[i] = farthestVertices.get(i);
			}
			return returnFarthestPoints;
		
	}
	
	public BoundaryVertex[] farthestVerticesFromPoint(BoundaryVertex boundaryVertex , Line2D axis){
		
		return farthest( boundaryVertex.toPoint() , axis);
		
	}
	
	public BoundaryVertex[] farthestVerticesFromPoint(Point2D origin , Line2D axis){ //RETURNING DUPLICATES?
		
		return farthest(origin, axis);
		
	}

	
	public BoundaryVertex[] nearestVerticesFromPoint(Point2D origin , Line2D axis){ //RETURNING DUPLICATES?
		
		ArrayList<BoundaryVertex> farthestVertices = new ArrayList<>();
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
		BoundaryVertex[] returnFarthestPoints = new BoundaryVertex[ farthestVertices.size() ];
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

	public Collider ownerEntity(){
		return ownerCollidable;
	}

	
	

	
}
