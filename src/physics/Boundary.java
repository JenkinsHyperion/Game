package physics;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;

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
	
	public int[] getSidesColliding(Boundary bounds){ //cycle through all lines of two shapes and check for intersections
	
		for (int i = 0 ; i < sides.length ; i++) {
			
			for ( int j = 0 ; j < bounds.getSides().length ; j++ ) {
				
				if ( sides[i].intersectsLine( bounds.getSides()[j] ) ) {				
					
				}
				
			}
			
		}
		return null;
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
