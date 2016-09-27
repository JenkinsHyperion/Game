package sprites;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

import animation.Animation;
import entities.EntityStatic;

public class SpriteStillframe extends Sprite {  // Object with still image

    protected int width;
    protected int height;
    protected Image image;

    public SpriteStillframe(String path, EntityStatic owner) { 

    	this.owner = owner;
    	loadImage(path);
        visibility = true;
    }
    
    public SpriteStillframe(String path, int offset_x, int offset_y , EntityStatic owner) { 

    	this.owner = owner;
    	loadImage(path);
        visibility = true;
        this.spriteOffsetX = offset_x;
        this.spriteOffsetY = offset_y;
    }
    
    @Override
    public void draw(Graphics g){
    	g.drawImage(this.getImage(), this.owner.getX() + spriteOffsetX, this.owner.getY() + spriteOffsetY, null); //null is observer
    }

    protected void getImageDimensions() { 

        width = image.getWidth(null);
        height = image.getHeight(null); 
    }

    public void loadImage(String imageName) { // 

        ImageIcon ii = new ImageIcon(imageName);
        image = ii.getImage();
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
    
}