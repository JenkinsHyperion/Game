package sprites;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import animation.*;
import entities.*;
import editing.*;
import engine.*;

public class SpriteStillframe extends Sprite {  // Object with still image

    protected int width;
    protected int height;
    protected BufferedImage image;

    public SpriteStillframe(String path, EntityStatic owner) { 
    	if (!checkPath(path)) {
    		fileName = null;
    		image = new MissingIcon().paintMissingSprite();
    		System.err.println("Image file '"+path +"' not found; using placeholder");
    	}
    	else {
	    	fileName = path;	    	
	    	loadImage(fileName);
    	}
    	this.owner = owner;
    	}
    //another constructor only for owner-less sprite (such as the ghostSprite in Editor)
    public SpriteStillframe(String path){
    	if (!checkPath(path)) {
    		fileName = null;
    		image = new MissingIcon().paintMissingSprite();
    		System.err.println("Image file '"+path +"' not found; using placeholder");
    	}
    	else {
	    	fileName = path;	    	
	    	loadImage(fileName);
    	}
    	visibility = true;
    }
    public SpriteStillframe(String path, int offset_x, int offset_y , EntityStatic owner) { 
    	if (!checkPath(path)) {
    		fileName = null;
    		image = new MissingIcon().paintMissingSprite();
    		System.err.println("Image file '"+path +"' not found for '"+owner.name+"', using placeholder");
    	}
    	else {
	    	fileName = path;	    	
	    	loadImage(fileName);
    	}
    	this.owner = owner;
    	visibility = true;
    	this.spriteOffsetX = offset_x;
    	this.spriteOffsetY = offset_y;
    }
    
    public SpriteStillframe(String path, int offset_x, int offset_y) { 
    	if (!checkPath(path)) {
    		fileName = null;
    		image = new MissingIcon().paintMissingSprite();
    		System.err.println("Image file '"+path +"' not found for '"+owner.name+"', using placeholder");
    	}
    	else {
	    	fileName = path;	    	
	    	loadImage(fileName);
    	}
    	visibility = true;
    	this.spriteOffsetX = offset_x;
    	this.spriteOffsetY = offset_y;
    }
    
    @Override @Deprecated
    public void drawSprite(){
    	//DONT USE
    }
    @Override
    public void drawSprite(Camera camera){

    	AffineTransform entityTransformation = new AffineTransform();

    	entityTransformation.scale( (double)this.spriteSizePercent/100 , (double)this.spriteSizePercent/100 );
    	entityTransformation.rotate( Math.toRadians(this.spriteAngle) ); 
    	entityTransformation.translate(spriteOffsetX, spriteOffsetY);

    	camera.draw(this, entityTransformation );
    	
    }
    @Override
    public void editorDraw( Point pos){ //FIXME TODO 
    	/*float opacity = 0.5f;
    	Graphics2D g2 = (Graphics2D) g.create();
    	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
    	g2.drawImage(this.getImage(), pos.x, pos.y, null);
    	g2.dispose();*/
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

	@Override
	public boolean hasSprite() {
		return true;
	}
	
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