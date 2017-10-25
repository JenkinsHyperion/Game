package engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.util.Observer;

import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import sprites.Sprite;

public class ParallaxFrame implements ReferenceFrame{
	
	private int x = 0;
	private int y = 0;
	Graphics2D graphics;
	ImageObserver observer;
	
	public void setPosition( int x , int y , Graphics2D g2, ImageObserver observer ){
		this.x = x;
		this.y = y;
		this.graphics = g2;
		this.observer = observer;
	}
	
	@Override
	public int getOriginX() { return x; }
	@Override
	public int getOriginY() { return y; }

	@Override
	public void drawOnCamera(GraphicComposite.Active sprite, AffineTransform entityTransform) {
		
		AffineTransform cameraTransform = new AffineTransform();
		this.graphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		
		cameraTransform.concatenate(entityTransform);
		
		//cameraTransform.translate(-this.x , -this.y );
		
		this.graphics.drawImage(sprite.getSprite().getBufferedImage(), 
				cameraTransform,
				this.observer);
		
	}

	@Override
	public void debugDrawPolygon(Shape polygon, Color color, Point point, AffineTransform entityTransform, float alpha) {
	}

	@Override
	public ImageObserver getObserver() {
		return this.observer;
	}

	@Override
	public Graphics2D getGraphics() {
		return this.graphics;
	}

	@Override
	public void drawString(String string, int world_x, int world_y) {
		// TODO Auto-generated method stub
		
	}

}
