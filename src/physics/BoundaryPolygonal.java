package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import engine.MovingCamera;
import entityComposites.Collider;
import entityComposites.EntityStatic;
import misc.*;

public class BoundaryPolygonal extends Boundary {
	

	protected Side[] sides = new Side[1]; 
	private BoundaryCorner[] corners;
	protected CollisionEvent defaultCollisionEvent = new DefaultCollisionEvent();

	private BoundaryPolygonal(){
		sides = new Side[0];
		corners = new BoundaryCorner[0];
		//this.ownerCollidable = ownerCollidable;
	} //use cloning instead
	//FOR CLONING ONLY
	private BoundaryPolygonal( Side[] sides , BoundaryCorner[] corners){
		this.sides = sides;
		this.corners = corners;
	}
	
	public BoundaryPolygonal(Line2D line){

		sides[0] = new Side(line , this , 0, defaultCollisionEvent); 
		corners = new BoundaryCorner[]{ new BoundaryCorner(line.getP1() , this , 0 , defaultCollisionEvent) 
				, new BoundaryCorner(line.getP2() , this , 1 , defaultCollisionEvent) };

		connectBoundaryMap( defaultCollisionEvent );
		constructVoronoiRegions();
	}
	
	public BoundaryPolygonal(Side[] bounds ) {
		sides = bounds;
		connectBoundaryMap( defaultCollisionEvent );
		constructVoronoiRegions();
	}
	
	public BoundaryPolygonal(Line2D[] bounds) {
		

		sides = new Side[ bounds.length ];
		
		for ( int i = 0 ; i < bounds.length ; i++ ){
			sides[i] = new Side( bounds[i] , this , i , defaultCollisionEvent);
		}
		connectBoundaryMap( defaultCollisionEvent );
		constructVoronoiRegions();
	}
	
	public static class Box extends BoundaryPolygonal{

		public Box(int width, int height, int xOffset, int yOffset){
			sides = new Side[4];
			
			sides[0] = new Side( new Line2D.Float(xOffset , yOffset , xOffset+width , yOffset ) , this, 0 , defaultCollisionEvent);
			sides[1] = new Side( new Line2D.Float(xOffset+width , yOffset , xOffset+width , yOffset+height ) , this, 1 , defaultCollisionEvent);
			sides[2] = new Side( new Line2D.Float(xOffset+width , yOffset+height , xOffset , yOffset+height ) , this, 2 , defaultCollisionEvent);
			sides[3] = new Side( new Line2D.Float(xOffset , yOffset+height , xOffset , yOffset ) , this, 3 , defaultCollisionEvent);
			connectBoundaryMap( defaultCollisionEvent );
			constructVoronoiRegions();
		}
		
	}
	
	public static class EnhancedBox extends BoundaryPolygonal{

		public EnhancedBox(int width, int height, int xOffset, int yOffset, CollisionEvent[] eventList ){
			
			sides = new Side[4];
			
			sides[0] = new Side( new Line2D.Float(xOffset , yOffset , xOffset+width , yOffset ) , this, 0 , eventList[0]);
			sides[1] = new Side( new Line2D.Float(xOffset+width , yOffset , xOffset+width , yOffset+height ) , this, 1 , eventList[1]);
			sides[2] = new Side( new Line2D.Float(xOffset+width , yOffset+height , xOffset , yOffset+height ) , this, 2 , eventList[2]);
			sides[3] = new Side( new Line2D.Float(xOffset , yOffset+height , xOffset , yOffset ) , this, 3 , eventList[3]);
			connectBoundaryMap( defaultCollisionEvent );
			constructVoronoiRegions();
		}
		
	}
	
	protected void connectBoundaryMap( CollisionEvent cornerEvent ){ //disychains every feature with its adjacent features
		
		corners = new BoundaryCorner[ sides.length  ];
		for (int i = 0 ; i < corners.length ; i++){
			corners[i] = new BoundaryCorner( sides[i].getP1() , this , i , cornerEvent );
		}
		for (int i = 0 ; i < sides.length ; i++) {
			
			int iNext = (i+1) % sides.length;
			
			corners[iNext].setEndingSide( sides[ iNext ] );
			corners[iNext].setStartingSide( sides[i] );
			
			sides[i].setStartPoint( corners[i] ); 
			sides[i].setEndPoint( corners[ iNext ] );

		}	

		
	}
	@Override
	public BoundaryPolygonal temporaryClone(){  

		Side[] newSides = new Side[this.sides.length];
		for ( int i = 0 ; i < newSides.length ; i++ ){
			Side oldSide = sides[i];
			newSides[i] = new Side( oldSide.toLine() , this , oldSide.getID() , oldSide.getEvent() );
		}
		
		BoundaryCorner[] newCorners = new BoundaryCorner[this.corners.length];
		for ( int i = 0 ; i < newCorners.length ; i++ ){
			BoundaryVertex oldCorner = corners[i] ;
			newCorners[i] = new BoundaryCorner( oldCorner.toPoint() , this, oldCorner.getID() , oldCorner.getEvent() );
		}
		
		BoundaryPolygonal returnBounds = new BoundaryPolygonal( newSides , newCorners); // Make clone with identical positions and events
		
		for ( int i = 0 ; i < returnBounds.sides.length ; i++){ //Connect the pointers for the new clone
			
			int iNext = (i+1) % this.sides.length;
			
			returnBounds.corners[iNext].setStartingSide( returnBounds.sides[i] );
			returnBounds.corners[iNext].setEndingSide( returnBounds.sides[iNext] );

			returnBounds.sides[i].setStartPoint( returnBounds.corners[i] );
			returnBounds.sides[i].setEndPoint( returnBounds.corners[iNext] );
		}
		
		return returnBounds;
	}

	
	@Override
	public void rotateBoundaryFromTemplate(Point center, double radians , Boundary template){ //OPTIMIZATION TRIG FUNCTIONS ARE NOTORIOUSLY EXPENSIVE Look into performing some trig magic
		// with fast trig approximations
		//THIS IS DOUBLING EVERY VERTEX BY DOING LINES, DO BY VERTEX INSTEAD!!!
		BoundaryPolygonal templatePolygonal = (BoundaryPolygonal) template;
		
		for ( int i = 0 ; i < templatePolygonal.corners.length ; i++ ) {
			
			Side side = templatePolygonal.sides[i];
			BoundaryVertex tempCorner = templatePolygonal.corners[i];
			
			double r = tempCorner.toPoint().distance(center); 
			double a = -Math.acos( (tempCorner.getX()-center.x) / r );
			if (tempCorner.getY() > center.y){ a = (2*Math.PI) - a ;} //clamp range to 0-2pi
			
			Point2D p1 = new Point2D.Double( 
					Math.round(r * Math.cos( a + radians  ) ) , 
					Math.round(r * Math.sin( a + radians ) )    );
			
			this.corners[i].setPos( p1 );
		}
		
		for (int i = 0 ; i < this.sides.length ; i++){ //Connect the vertex pairs with their corresponding side
			
			int indexNext = (i+1) % templatePolygonal.sides.length; //indexNext wraps to 0 if past array length
			
			this.sides[i].setLine( this.corners[i].toPoint() , this.corners[indexNext].toPoint() );
			
		}

	}
	
	public Point rotateBoundaryFromTemplatePoint(Point center, double angle , BoundaryPolygonal template){ //OPTIMIZATION TRIG FUNCTIONS ARE NOTORIOUSLY EXPENSIVE Look into performing some trig magic

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
	public boolean boundaryIntersects(BoundaryPolygonal bounds){ 
		
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
	public Side[][] getIntersectingSides(BoundaryPolygonal bounds){ //returns pairs of sides of this boundary that are intesecting
		
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
	
	public Side[] getSides(){
		return sides;
	}
	
	public Side getSide( int ID){
		return sides[ID];
	}
	
	public BoundaryVertex[] getVertices() {
		return this.corners;
	}
	
	public BoundaryVertex getRawVertex( int ID) {
		return this.corners[ID];
	}
	
	public Side getRawSide( int ID) {
		return this.sides[ID];
	}
	
	@Override
	public Boundary atPosition( Point position) {

		BoundaryPolygonal returnBoundary = this.temporaryClone();
		int x = (int)position.x;
		int y = (int)position.y;
		
		for ( int i = 0 ; i < this.sides.length ; i++ ){
			
			Side oldSide = this.sides[i];
			Line2D shiftedLine = new Line2D.Float( oldSide.getX1()+x, oldSide.getY1()+y , oldSide.getX2()+x, oldSide.getY2()+y );
	
			returnBoundary.sides[i].setLine( shiftedLine );
		}
		
		for ( int i = 0 ; i < this.corners.length ; i++ ){
			
			BoundaryVertex oldCorner = this.corners[i];
			Point shiftedPosition = new Point( oldCorner.getX()+x , oldCorner.getY()+y );
			
			returnBoundary.corners[i].setPos( shiftedPosition );
		}

		returnBoundary.constructVoronoiRegions(); //OPTIMIZE make move method instead of reconstructing each time
		
		return returnBoundary;
	};
	
	
	//SEPARATING AXIS THEORM METHODS
	
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
	@Override
	public Line2D[] getSeparatingSides(){  //LOOK FOR OPTIMIZATION IN SIDE.TOLINE() also SLOPE ONLY
		
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
	
	private Line2D[] debugGetSeparatingSides(){  //LOOK FOR OPTIMIZATION IN SIDE.TOLINE()
		
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
	
	public Line2D[] debugSeparatingAxes(int xMax , int yMax){ 
		
		Line2D[] axes = new Line2D[debugGetSeparatingSides().length];
		
		for ( int i = 0; i < debugGetSeparatingSides().length ; i++){
			axes[i] = debugGetSeparatingAxis( debugGetSeparatingSides()[i], xMax, yMax , new Point(20,20) );
		}
		return axes;
	}
	/**
	 * Returns array of corners of type (Point2D) for this boundary. If information is needed about sides connected to this point,
	 * use getCornerVertex() instead.
	 * @return
	 */
	@Override
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
	@Override
	public BoundaryCorner[] getCornersVertex(){
		return this.corners;
	}
	
	@Override
	protected Point2D[] getOuterPointsPair( Line2D axis) { //RETURNS THE TWO FARTHEST POINTS ON AXIS OF THIS BOUNDARY
		
		Point2D[] outerPoints = new Point2D[]{ this.getCornersPoint()[0] , this.getCornersPoint()[0] };
		
		for ( int i = 0 ; i < this.getCornersPoint().length ; i++ ){
			
			for ( int j = i ; j < this.getCornersPoint().length ; j++ ){
				
				if (getProjectionPoint( this.getCornersPoint()[i] , axis ).distance( getProjectionPoint( this.getCornersPoint()[j] , axis ) ) 
						> 
					getProjectionPoint( outerPoints[0] , axis ).distance( getProjectionPoint( outerPoints[1] , axis ) ) 
				){
					// points i and j are farther apart on axis than whats stored 
					outerPoints[0] = this.getCornersPoint()[i];
					outerPoints[1] = this.getCornersPoint()[j];
				}
				
			}
		}	
		return outerPoints;
	}
	
	
	private BoundaryCorner[] farthestCorner( Point2D origin , Line2D axis ){	
		
			ArrayList<BoundaryCorner> farthestVertices = new ArrayList<>();
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
			BoundaryCorner[] returnFarthestPoints = new BoundaryCorner[ farthestVertices.size() ];
			for (int i = 0 ; i < returnFarthestPoints.length ; i++){
				returnFarthestPoints[i] = farthestVertices.get(i);
			}
			return returnFarthestPoints;
		
	}
	@Override
	public BoundaryVertex[] farthestVerticesFromPoint(BoundaryVertex boundaryVertex , Line2D axis){
		
		return farthestCorner( boundaryVertex.toPoint() , axis);
		
	}
	@Override
	public BoundaryCorner[] farthestVerticesFromPoint(Point2D origin , Line2D axis){ //RETURNING DUPLICATES?	
		return farthestCorner(origin, axis);
	}
	
	@Override
	protected Point2D farthestPointFromPoint(Point2D boundaryPoint, Line2D axis) {
		return farthestCorner( boundaryPoint , axis )[0].toPoint();
	}
	
	public BoundaryVertex[] nearestVerticesFromPoint(Point2D origin , Line2D axis){ //RETURNING DUPLICATES?
		
		ArrayList<BoundaryCorner> farthestVertices = new ArrayList<>();
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
	@Override
	public void debugDrawBoundary(MovingCamera camera, Graphics2D g, EntityStatic ownerEntity) {
		for ( Side side : this.getSides() ){

			camera.draw( side.toLine() );
			camera.drawString(side.toString(), side.getX1()+(side.getX2()-side.getX1())/2 , side.getY1()+(side.getY2()-side.getY1())/2 );
		}
		//for ( BoundaryCorner corner : corners ){
		//	camera.drawString(corner.toString() , corner.getX() , corner.getY() );
		//}
	}
	@Override
	public void constructVoronoiRegions(){
		
		final VoronoiRegionDefined[] newRegions = new VoronoiRegionDefined[sides.length + corners.length ];//OPTIMIZATION check side/corner ratio guaranteed
		
		// Lay out boundary features in clockwise loop, alternating corners and sides
		for ( int i = 0 ; i < this.sides.length ; i++ ){ 
			newRegions[(2*i)+1] = new VoronoiRegionDefined(sides[i]);
			newRegions[2*i] = new VoronoiRegionDefined(corners[i]); // Orders regions like so: V0 , Side0 , V1, SIde1, V2
	    }
		
		// Itterate over each side and separate it from its adjacent corners
		VoronoiRegionDefined.addSideOuterBounds( newRegions[0] , newRegions[1] , newRegions[2] , sides[0]);
		for ( int i = 3 ; i < newRegions.length ; i=i+2 ){ 
			int iNext = (i+1) % newRegions.length;
			VoronoiRegionDefined.addSideOuterBounds( newRegions[i-1] , newRegions[i] , newRegions[iNext] , sides[i/2]);
	    }
		
		for ( int i = 1 ; i < newRegions.length ; i=i+2 ){ 
			int iNext = (i+2) % newRegions.length;
			VoronoiRegionDefined.splitAdjacentSideRegions( newRegions[i] , newRegions[iNext] );
	    }
		
		VoronoiRegionDefined.splitOpposingSides( newRegions[1] , newRegions[5] );
		
		this.regions = newRegions;
		
	}

}
