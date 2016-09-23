package sprites;

import java.awt.Image;
import javax.swing.ImageIcon;

import animation.Animation;

public class SpriteStillframe extends Sprite {  // Object with still image

    protected int width;
    protected int height;
    protected Image image;

    public SpriteStillframe(String path) { 

    	loadImage(path);
        visibility = true;
    }
    
    public SpriteStillframe(String path, int offset_x, int offset_y) { 

    	loadImage(path);
        visibility = true;
        spriteOffsetX = offset_x;
        spriteOffsetY = offset_y;
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
    
}