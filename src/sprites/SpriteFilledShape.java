package sprites;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import animation.Animation;
import engine.Camera;
import entities.EntityStatic;
import physics.Boundary;

public class SpriteFilledShape extends Sprite {
	
	private Polygon shape;
	private Color color;
	
	public SpriteFilledShape( Boundary bounds , Color color, EntityStatic owner){
		
		this.owner = owner;
		this.color = color;
		
		int nCorners = bounds.getCornersVertex().length ;
		int[] xCoords = new int[ nCorners ];
		int[] yCoords = new int[ nCorners];
		
		for ( int i = 0 ; i < nCorners ; i++ ){
			
			xCoords[i] = (int) bounds.getCornersPoint()[i].getX();
			yCoords[i] = (int) bounds.getCornersPoint()[i].getY();
		}
		
		this.shape = new Polygon( xCoords , yCoords , nCorners );
	}

	@Override
	public void drawSprite(Camera camera) {
		
		AffineTransform entityTransformation = new AffineTransform();

    	entityTransformation.scale( (double)this.spriteSizePercent/100 , (double)this.spriteSizePercent/100 );
    	entityTransformation.rotate( Math.toRadians(this.spriteAngle) ); 

    	camera.drawPolygon(this.shape , this.color , this.owner , entityTransformation );
		
	}

	@Override
	public void drawSprite() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editorDraw(Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getImage() {
		Rectangle r = this.shape.getBounds();
		BufferedImage image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_BYTE_BINARY);

	    return image;
	}

	@Override
	public BufferedImage getBufferedImage() {
		
		Rectangle r = this.shape.getBounds();
		BufferedImage image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_BYTE_BINARY);

	    return image;
		
	}

	@Override
	public Animation getAnimation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSprite() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasSprite() {
		// TODO Auto-generated method stub
		return false;
	}

}
