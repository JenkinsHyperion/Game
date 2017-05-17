package sprites;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import animation.*;
import entities.*;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import editing.*;
import engine.*;

public class SpriteStillframe extends Sprite {  // Object with still image

    protected int width;
    protected int height;
    protected BufferedImage image;

    public SpriteStillframe(String path){ // GENERIC CONSTRUCTOR
    	super(path,0,0);
    	
    	initialize(path);
    	
    	visibility = true;
    }
    
    public SpriteStillframe(String path, int offset_x, int offset_y) { 
    	super(path,offset_x,offset_y);

    	initialize(path);
    	
    	visibility = true;
    	this.spriteOffsetX = offset_x; //OPTIMIZE redundancy?
    	this.spriteOffsetY = offset_y;
    }
    
    public SpriteStillframe( String path , byte flag ){ //CONSTRUCTOR ALLOWING FOR CERTAIN AUTOMATIC INITIALIZERS LIKE CENTERING
    	super(path,0,0);
    	initialize(path);
    	
    	if ( flag == 0 ){
    		this.spriteOffsetX = -this.image.getWidth() / 2 ;
    		this.spriteOffsetY = -this.image.getHeight() / 2 ;
    	}
    	else{
    		System.err.println(path+" sprite has invalid parameter");
    	}
    }
    
    private void initialize( String path ){
    	if (!checkPath(System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path)) {
    		fileName = null;
    		image = new MissingIcon().paintMissingSprite();
    		System.err.println("Image file '"+path +"' not found, using placeholder");
    	}
    	else {	    	
	    	loadImage( System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path );
    	}
    }

    @Override
    public void draw(Camera camera , GraphicComposite composite ){

    	AffineTransform entityTransformation = new AffineTransform(); //OPTIMIZE Test making AffineTransform field of Sprite

    	entityTransformation.scale( (double)this.spriteSizePercent , (double)this.spriteSizePercent );
    	entityTransformation.rotate( Math.toRadians(this.spriteAngle) ); 
    	entityTransformation.translate(spriteOffsetX, spriteOffsetY);

    	camera.drawOnCamera( composite, entityTransformation );
    	
    }

    protected void getImageDimensions() { 
        width = image.getWidth(null);
        height = image.getHeight(null); 
    }
    public boolean checkPath(String path) {
    	boolean exists = false;
    	exists = new File(path).exists(); 	
    	return exists;
    }
    public void loadImage(String imageName) { // 
        //ImageIcon ii = new ImageIcon(imageName);
        //image = ii.getImage();
		try {

			image = ImageIO.read(new File(imageName));
			//image = ImageIO.read(new File(System.getProperty("user.dir") + "\\Assets\\" +));

		} catch (IOException e) {;
			e.printStackTrace();
		}
    	
    }
    public void loadImagePlaceHolder(){
    	image = new MissingIcon().paintMissingSprite();
    }

    @Override 
    public Image getImage() { 
        return image;
    }
    
    @Override
    public BufferedImage getBufferedImage() {
    	return image;
    }
    
    public Animation getAnimation(){ // This method is redundant
    	return null;
    }
    
    public void updateSprite(){} //Need to figure out a better way of avoiding this redundancy

	@Deprecated
	public void setResizeFactor( int percent ){
		this.spriteSizePercent = percent;
	}
	
	public void setOffset( int x, int y){
		this.spriteOffsetX = x;
		this.spriteOffsetY = y;
	}

	public void setFileName(String path) {
		fileName = path;
	}
	
}