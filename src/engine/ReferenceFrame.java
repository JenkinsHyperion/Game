package engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;

import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import sprites.Sprite;


public interface ReferenceFrame {

	public void drawOnCamera(GraphicComposite.Static sprite , AffineTransform entityTransform);
	public void drawString(String string , int world_x, int world_y);
	public void debugDrawPolygon( Shape polygon, Color color, Point point , AffineTransform entityTransform, float alpha );
	
	public void draw(Image image , Point world_position	,AffineTransform entityTransform, float alpha );
	
	public int getOriginX();
	public int getOriginY();
	
	public ImageObserver getObserver();
	public Graphics2D getGraphics();
	public void drawLine( Point p1 , Point p2 );
	public void drawLine( Point2D p1 , Point2D p2 );
}


