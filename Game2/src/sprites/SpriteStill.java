package sprites;

import java.awt.Image;
import javax.swing.ImageIcon;

import animation.Animation;

public class SpriteStill extends Sprite {  // Object with still image

    protected int width;
    protected int height;
    protected boolean visibility;
    protected Image image;

    public SpriteStill(String path) {

    	loadImage(path);
        visibility = true;
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

    
    public boolean isVisible() {
        return visibility;
    }

    public void setVisible(Boolean visible) {
        visibility = visible;
    }
    
}