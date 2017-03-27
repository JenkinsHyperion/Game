package engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.util.Observer;

import entityComposites.EntityStatic;
import sprites.Sprite;

public class CameraParallax implements Camera{
	
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
	public void drawOnCamera(Sprite sprite, AffineTransform entityTransform) {
		
		AffineTransform cameraTransform = new AffineTransform();
		this.graphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		cameraTransform.translate( -this.x + sprite.ownerComposite().ownerEntity().getX() , 
				-this.y + sprite.ownerComposite().ownerEntity().getY() 
				);
		
		cameraTransform.concatenate(entityTransform);
		
		this.graphics.drawImage(sprite.getBufferedImage(), 
				cameraTransform,
				this.observer);
		
	}

	@Override
	public void debugDrawPolygon(Shape polygon, Color color, EntityStatic owner, AffineTransform entityTransform) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ImageObserver getObserver() {
		return this.observer;
	}

	@Override
	public Graphics2D getGraphics() {
		return this.graphics;
	}

}