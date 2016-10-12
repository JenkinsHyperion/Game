package sprites;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import animation.*;
import entities.*;
import editing.*;

public class SpriteStillframe extends Sprite {  // Object with still image

    protected int width;
    protected int height;
    protected transient Image image;

    public SpriteStillframe(String path, EntityStatic owner) { 
    	if (!checkPath(path)) {
    		fileName = null;
    		image = new MissingIcon().paintMissingSprite();
    		System.err.println("Image file not found; using placeHolder");
    	}
    	else {
	    	fileName = path;	    	
	    	loadImage(fileName);
    	}
    	this.owner = owner;
    	visibility = true;
    }
    //another constructor only for owner-less sprite (such as the ghostSprite in Editor)
    public SpriteStillframe(String path){
    	if (!checkPath(path)) {
    		fileName = null;
    		image = new MissingIcon().paintMissingSprite();
    		System.err.println("Image file not found; using placeHolder");
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
    		System.err.println("Image file not found; using placeHolder");
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
    
    @Override
    public void drawSprite(Graphics g){
    	g.drawImage(this.getImage(), this.owner.getX() + spriteOffsetX, this.owner.getY() + spriteOffsetY, null); //null is observer
    }
    @Override
    public void editorDraw(Graphics g, Point pos){
    	float opacity = 0.5f;
    	Graphics2D g2 = (Graphics2D) g.create();
    	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
    	g2.drawImage(this.getImage(), pos.x, pos.y, null);
    	g2.dispose();
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

        ImageIcon ii = new ImageIcon(imageName);
        image = ii.getImage();
    }
    public void loadImagePlaceHolder(){
    	image = new MissingIcon().paintMissingSprite();
    }

    @Override 
    public Image getImage() { 
        return image;
    }
    
    public Animation getAnimatedSprite(){ // This method is redundant
    	return null;
    }
    
    public void updateSprite(){} //Need to figure out a better way of avoiding this redundancy

	@Override
	public boolean hasSprite() {
		return true;
	}
    
	public void setFileName(String path) {
		fileName = path;
	}
}