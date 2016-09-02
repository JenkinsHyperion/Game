package physics;

import java.awt.Rectangle;
import java.awt.geom.Line2D;

public class BoundingBox extends Boundary{
	
	public BoundingBox(Rectangle box){		
		
		sides = new Line2D[4];
		
		sides[0] = new Line2D.Float((int)box.getMinX(),(int)box.getMinY(),(int)box.getMaxX(),(int)box.getMinY());
		sides[1] = new Line2D.Float((int)box.getMaxX(),(int)box.getMinY(),(int)box.getMaxX(),(int)box.getMaxY());
		sides[2] = new Line2D.Float((int)box.getMaxX(),(int)box.getMaxY(),(int)box.getMinX(),(int)box.getMaxY());
		sides[3] = new Line2D.Float((int)box.getMinX(),(int)box.getMaxY(),(int)box.getMinX(),(int)box.getMinY());
		
	}
	
	public boolean boundaryIntersects(Boundary bounds){	
		
		for ( Line2D line : sides ){
			
			for ( Line2D line2 : bounds.getSides() ){ 
				
				if ( line.intersectsLine(line2) ) {
					return true;
				}
				
			}
			
		}
		return false;
	}
	
	

}
