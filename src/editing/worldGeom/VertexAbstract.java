package editing.worldGeom;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import engine.Camera;

public abstract class VertexAbstract {
	
	
	public abstract Point getPoint();

	public abstract void draw(Graphics g, Camera camera);
	
	public abstract void drawClickableBox(Graphics g, Camera camera);
	
	public abstract Rectangle getClickableZone();
	
	public abstract void translate(Point p);
	public abstract void translate(int x, int y);
}
