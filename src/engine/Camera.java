package engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import sprites.Sprite;


public interface Camera {

	public void drawOnCamera(GraphicComposite sprite , AffineTransform entityTransform);
	public void debugDrawPolygon( Shape polygon, Color color, Point point , AffineTransform entityTransform );
	
	public int getOriginX();
	public int getOriginY();
	
	public ImageObserver getObserver();
	public Graphics2D getGraphics();
}


