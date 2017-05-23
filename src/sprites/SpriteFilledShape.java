package sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import animation.Animation;
import engine.ReferenceFrame;
import engine.MovingCamera;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import physics.*;

public class SpriteFilledShape extends Sprite {
	
	private Polygon shape;
	private Color color;
	
	public SpriteFilledShape( Boundary bounds , Color color ){
		super("FilledShape",0,0);
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
	public void draw(ReferenceFrame camera , GraphicComposite composite ) {
		
		AffineTransform entityTransformation = new AffineTransform();

    	entityTransformation.scale( (double)this.spriteSizePercent , (double)this.spriteSizePercent );
    	entityTransformation.rotate( Math.toRadians(this.spriteAngle) ); 

    	camera.debugDrawPolygon(this.shape , this.color , composite.ownerEntity().getPosition() , entityTransformation, 1.0f );
		//camera.drawOnCamera(this, entityTransformation);
	}


	@Override
	public Image getImage() {
		Rectangle r = this.shape.getBounds();
		BufferedImage image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g2 = image.createGraphics();
		g2.translate(-r.x, -r.y);
		g2.draw(shape);
		g2.dispose();
	    return image;
	}
/*	public Image makeImage(Shape s) {
	    Rectangle r = s.getBounds();
	    Image image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_BYTE_BINARY);
	    Graphics2D gr = image.createGraphics();
	    // move the shape in the region of the image
	    gr.translate(-r.x, -r.y);
	    gr.draw(s);
	    gr.dispose();
	    return image;
	}*/
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


}
