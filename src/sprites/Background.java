package sprites;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import animation.Animation;
import engine.ReferenceFrame;
import engine.MovingCamera;
import entityComposites.GraphicComposite;


public class Background extends Sprite{

	private ArrayList<BufferedImage> tiles = new ArrayList<BufferedImage>();
	
	BufferedImage[][] tilesGrid;

    protected Image image;
    protected String filename;
    
    private int tileWidth = 50;
    private int tileHeight = 50;
    
    private int horizontalTiles = 1;
    private int verticalTiles = 1;
    
    private int boardWidth;
    private int boardHeight;
    
    private float xScroll = 0;
    private float yScroll =0;
    private int xOffset = 0;
    private int yOffset = 0;
	
	private Background(String name){
		super(name , 0 , 0);
		try {
			splitBackground(name);
		} catch (IOException e) {
			//TODO HANDLE WITH MISSING SPRITE
			e.printStackTrace();
		}
		
	}
	
	public Background(String name, int boardW, int boardH , float xScroll ,float yScroll){
		super(name,0,0);
		this.boardWidth = boardW;
		this.boardHeight = boardH;
		
		try {
			splitBackground(name);
		} catch (IOException e) {
			//TODO HANDLE WITH MISSING SPRITE
			e.printStackTrace();
		}
		this.yScroll = yScroll;

	}
	
	public void loadBackgroundImage(String path){
		
	}
	@Override
	public void draw( ReferenceFrame camera , GraphicComposite.Active composite ){ 

		int cameraPositionX = camera.getOriginX() + xOffset;
		int cameraPositionY = camera.getOriginY() + yOffset;
		
		int positionModuloX = cameraPositionX % tileWidth ; //Clamps camera position to repeating intervals of 0 - 191, the distance of one BG tile
		int positionModuloY = cameraPositionY % tileHeight ; // % ( Modulo ) simply returns the remainder of the division of two numbers. As such its
													// range is 0 - (the divisor - 1)
		
		for ( int i = 0  ; i < horizontalTiles+1 ; i++ ){ 
			//convert to automatic parameterization of i later
			int xPosition = -tileWidth + (i*tileWidth) -  positionModuloX  ;
			
			int index =  ( ( ( (cameraPositionX + (tileWidth*i) ) /tileWidth ) % 9 ) + 9 ) % 9 ; //Clamps camera position to 0-11, the number of tiles BG was divided into
		// Adding (192*i) to the camera position offsets the whole equation output by i, and since it is performed before modulo,
		// index will wrap instead of going out of bounds. (so 4 iterations of index starting at 10 would be 10, 11, 0, 1 ) 
		//the %12 +12 %12 is some magical crap that I didn't know about but it returns absolute value without using abs()
			
			for ( int j = 0 ; j < verticalTiles+1 ; j++){
				int yPosition = -tileHeight + (j*tileHeight) - positionModuloY ;
			
				int indexY =  ( ( ( (cameraPositionY + (tileHeight*j) ) /tileHeight ) % 9 ) + 9 ) % 9 ;

				camera.getGraphics().drawImage( tilesGrid[index][indexY], xPosition, yPosition, camera.getObserver() );
			//change constant to variable camera halfwidth
			// +200 is constant offset, the farthest left a tile will appear to go before modlulo shifts the indexes and it disappears  
			// +(i*192) is the position of each consecutive tile, 192 is their width so the for loop draws them next to each other
			// -camera.getLocalX( positionModulo ) is the position in the world where the first tile is drawn. 
			// From above, positionModulo is 0 - 191 only, limiting the tiles position to a range on screen
			
			//Added together, the loop draws the series of four tiles, side by side, restricts their movement, and cycles through
			//the indexes in a circular fashion.
			}
		}
		xOffset = (int)(xOffset + xScroll);
		yOffset = (int)(yOffset + yScroll);	
	}
	
	private void splitBackground( String path ) throws IOException{
		
		System.out.print("Loading Background "+path);
		File file = new File( System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path +" ");
		
		try {
		
		BufferedImage bimg = ImageIO.read( file );
		int width = bimg.getWidth();
		int height = bimg.getHeight();
		
		tileWidth = (int) Math.floor(width/9); //TODO change to set 100x100 tiles and calculate number with that 
		tileHeight = (int) Math.floor(height/9); 
		
		horizontalTiles = (int)Math.ceil(boardWidth/tileWidth);
		verticalTiles = (int)Math.ceil(boardHeight/tileHeight);
		
		System.out.print(": "+horizontalTiles+" "+verticalTiles+" Tiles");
		System.out.print(" of size "+tileWidth+" x "+tileHeight+"px ");
		
		tilesGrid = new BufferedImage[9][9];
		
			for ( int i = 0 ; i < 9 ; i++ ){
				
				for ( int j = 0 ; j < 9 ; j++ ) {
					
					Rectangle sourceRegion = new Rectangle( i*tileWidth, j*tileHeight, tileWidth, tileHeight );
					
	
						ImageInputStream stream = ImageIO.createImageInputStream(file);
						Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
						
						if (readers.hasNext()) {
						    ImageReader reader = readers.next();
						    reader.setInput(stream);
			
						    ImageReadParam param = reader.getDefaultReadParam();
						    
						    param.setSourceRegion(sourceRegion); // Set region
			
						    //tiles.add( reader.read(0, param) ); // Will read only the region specified
						    tilesGrid[i][j] = reader.read(0, param);
						    
						    System.out.print(".");
						}	
				}		
			}
		} catch (IOException e) {
			System.err.println("Failed to load" );
			e.printStackTrace();
		} // File or input stream
		System.out.println("Background Loaded");
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedImage getBufferedImage() {
		// TODO Auto-generated method stub
		return null;
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
