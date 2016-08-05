package physics;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

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
	
	public boolean boundaryIntersects(Boundary bounds){ //cycle through all lines of two shapes and check for intersections
		
		for (int i = 0 ; i < sides.length ; i++) {
			
			for ( Line2D line2 : bounds.getSides()) {
				
				if ( sides[i].intersectsLine( line2 ) ) {
					return true;
				}
				
			}
			
		}
		return false;
	}
	
	
	public boolean sideIsFlush(Boundary bounds){ 
		
		for (int i = 0 ; i < sides.length ; i++) {
			
			for ( int j = 0 ; j < bounds.getSides().length ; j++ ) {

					if ( pointIsOnLine(bounds.getSides()[j].getP1(), sides[i]) ) {
						if ( pointIsOnLine(bounds.getSides()[j].getP2(), sides[i]) ) {
							return true;
						}
					}
					if ( pointIsOnLine(sides[i].getP1(), bounds.getSides()[j]) ) {
						if ( pointIsOnLine(sides[i].getP2(), bounds.getSides()[j]) ) {
							return true;
						}
					}
			}
		}
		return false;
	}
	
	
public Line2D[] getFlushSides(Boundary bounds){ 
	
		Line2D[] flushSides = new Line2D[2];
		
		for (int i = 0 ; i < sides.length ; i++) {
			
			for ( int j = 0 ; j < bounds.getSides().length ; j++ ) {

					if ( pointIsOnLine(bounds.getSides()[j].getP1(), sides[i]) ) { 
						if ( pointIsOnLine(bounds.getSides()[j].getP2(), sides[i]) ) {
								flushSides[0] = bounds.getSides()[j];
								flushSides[1] = sides[i];
								return flushSides;
						}
					}

					else if ( pointIsOnLine(sides[i].getP1(), bounds.getSides()[j]) ) {
						if ( pointIsOnLine(sides[i].getP2(), bounds.getSides()[j]) ) {
							
							
							flushSides[0] = bounds.getSides()[j];
							flushSides[1] = sides[i];
							return flushSides;
						}
					}	
			}
			
		}
		return null;
	}

	private boolean pointIsOnLine(Point2D point, Line2D line){
		double dist = Line2D.ptLineDist(line.getX1(), line.getY1(), line.getX2(), line.getY2(), point.getX(), point.getY());  	
		if ( dist > 0.5 && dist < 1.5 ) {
			return true;			
		}
		else {
			return false;
		}
	}
	
	private boolean pointIsOnSegment(Point2D point, Line2D line){
		double dist = line.ptSegDist(point);  	
		if ( dist > 0.5 && dist < 1.5 ) {
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

	public Boundary atPosition(float x, float y) {

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
