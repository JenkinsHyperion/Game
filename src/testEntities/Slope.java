package testEntities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import entities.EntityStatic;
import physics.Boundary;

public class Slope extends EntityStatic{
	
	public Slope(int x, int y) {
		super(x, y);
		
		
		boundary = new Boundary(new Line2D.Double( new Point2D.Double(0,0), new Point2D.Double(50,20)) );
		
		loadSprite("bullet");
		
		
		name = "Platform"+count;
	}

	
	public String toString() {
		return String.format(name);
	}
}
