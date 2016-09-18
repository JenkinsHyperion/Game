package testEntities;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import entities.EntityStatic;
import physics.Boundary;

public class Slope extends EntityStatic{
	
	public Slope(int x, int y) {
		super(x, y);
		
		Line2D[] slopeSides = new Line2D[]{
				new Line2D.Float( new Point2D.Double(-25,-10), new Point2D.Double(25,10) ),
				new Line2D.Float( new Point2D.Double(25,10), new Point2D.Double(-25,10) ),
				new Line2D.Float( new Point2D.Double(-25,10), new Point2D.Double(-25,-10) )
		};
		
		boundary = new Boundary( slopeSides );
		
		loadSprite("bullet");
		name = "Slope"+count;
		
		//obsolete
		boundingBox = new Rectangle(25 , 10);
	}

	
	public String toString() {
		return String.format(name);
	}
}
