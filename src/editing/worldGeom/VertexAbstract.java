package editing.worldGeom;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import engine.MovingCamera;

public abstract class VertexAbstract {
	
	
	public abstract Point getPoint();

	public abstract void draw(Graphics g, MovingCamera camera);
	
	public abstract void drawClickableBox(Graphics g, MovingCamera camera);
	
	public abstract Rectangle getClickableZone();
	
	public abstract void translate(Point p);
	public abstract void translate(int x, int y);
}
