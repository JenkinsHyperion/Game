package sprites;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;

import engine.Camera;


public class Background {

	private ArrayList<BufferedImage> tiles = new ArrayList<BufferedImage>();

    protected Image image;
    protected String filename;
    
    private int tileWidth = 192;
    private int tileHeight = 768;
    
	private final int PARALLAX = 5; // 1 = background moves as fast as entities, 100 = background nearly static relative to camera 
									 // negative = parallax moves in opposite direction, 0 = divide by zero error so can you just not
	
	public Background(String name){
		
		splitBackground(name);
		
	}
	
	public void loadBackgroundImage(String path){
		
	}
	
	public void drawBackground( Graphics g , Camera camera ){ 
		
		int cameraPosition = camera.getX()/PARALLAX ;
		
		int positionModulo = cameraPosition % 192 ; //Clamps camera position to repeating intervals of 0 - 191, the distance of one BG tile
													// % ( Modulo ) simply returns the remainder of the division of two numbers. As such its
													// range is 0 - (the divisor - 1)
		for ( int i = 0  ; i < 8 ; i++ ){ 
			//convert to automatic parameterization of i later
			
			int index =  ( ( ( (cameraPosition + (192*i) ) /192 ) % 12 ) + 12 ) % 12 ; //Clamps camera position to 0-11, the number of tiles BG was divided into
			// Adding (192*i) to the camera position offsets the whole equation output by i, and since it is performed before modulo,
			// index will wrap instead of going out of bounds. (so 4 iterations of index starting at 10 would be 10, 11, 0, 1 ) 
			//the %12 +12 %12 is some magical crap that I didn't know about but it returns absolute value without using abs()
			
			camera.draw( tiles.get( index ) , g , -192 + (i*192) - camera.getLocalX( positionModulo ) , 0 ); //change constant to variable camera halfwidth
			// +200 is constant offset, the farthest left a tile will appear to go before modlulo shifts the indexes and it disappears  
			// +(i*192) is the position of each consecutive tile, 192 is their width so the for loop draws them next to each other
			// -camera.getLocalX( positionModulo ) is the position in the world where the first tile is drawn. 
			// From above, positionModulo is 0 - 191 only, limiting the tiles position to a range on screen
			
			//Added together, the loop draws the series of four tiles, side by side, restricts their movement, and cycles through
			//the indexes in a circular fashion.

		}
			
	}
	
	private void splitBackground( String path ){
		
		System.out.print("Loading Background");
		File file = new File( System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path );

		for ( int i = 0 ; i < 12 ; i++ ){
		
			Rectangle sourceRegion = new Rectangle( i*192, 0, tileWidth, tileHeight ); // The region you want to extract
			
			try {
				ImageInputStream stream = ImageIO.createImageInputStream(file);
				Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
				
				if (readers.hasNext()) {
				    ImageReader reader = readers.next();
				    reader.setInput(stream);
	
				    ImageReadParam param = reader.getDefaultReadParam();
				    param.setSourceRegion(sourceRegion); // Set region
	
				    tiles.add( reader.read(0, param) ); // Will read only the region specified
				    System.out.print(".");
				}
				
			} catch (IOException e) {
				System.err.println("Failed to load background");
				e.printStackTrace();
			} // File or input stream
			
		}
		System.out.println("Background Loaded ");
	}
	
}
