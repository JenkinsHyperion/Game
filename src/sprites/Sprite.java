package sprites;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import animation.Animation;
import editing.MissingIcon;
import engine.ReferenceFrame;
import entityComposites.GraphicComposite;
import physics.Vector;
import utility.UtilityMath;

/*
 * This is the base Sprite class. 
 */
public abstract class Sprite implements Graphic{
	
	public static final Missing missingSprite = new Missing();
	public static final Stillframe entityMarker = new Stillframe("EntityMarker.png");
	protected double alpha = 1.0;
    protected boolean visibility;
    protected int spriteOffsetX = 0;
    protected int spriteOffsetY = 0;
    protected double spriteSizePercent = 1;
    protected double spriteAngle = 0;
    
    protected int width;
    protected int height;
    protected int tileSize;
    
    public static final byte CENTERED = 0;
    public static final byte CENTERED_BOTTOM = 1;
    public static final byte CENTERED_TOP = 2;
    
    
    
	AffineTransform spriteTransform = new AffineTransform();

	protected Sprite( int xOffset, int yOffset  ){
		this.spriteOffsetX = xOffset;
		this.spriteOffsetY = yOffset;
	}
	
//ABSTRACT FUNCTIONS 
	//This is a getImage() that works for both still and animated sprites, so draw functions in Board 
    //can call a generalized format.
    //public abstract void draw(Camera camera);
    //public abstract void draw();
	@Deprecated
	public abstract Image getImage(); 
	
	public abstract BufferedImage[][] getBufferedImage();

	
	public abstract Animation getAnimation(); 
	
//PUBLIC FUNCTIONS	
	public void setSprite(Animation a){}
	//public void setSprite()
	
    public boolean isVisible() {
        return visibility;
    }

    public void setVisible(Boolean visible) {
        visibility = visible;
    }

    public int getOffsetX(){
    	return spriteOffsetX;
    }
    
    public int getOffsetY(){
    	return spriteOffsetY;
    }
    public Point getOffsetPoint() {
    	return new Point(spriteOffsetX, spriteOffsetY);
    }
    public void setOffset(int x , int y){
    	spriteOffsetX = x;
    	spriteOffsetY = y;
    }
    public Vector getRelativePoint(Vector worldVector) {
    	double newX = worldVector.getX() * (Math.cos(Math.toRadians(spriteAngle)));
    	double newY = worldVector.getY() * (Math.sin(Math.toRadians(spriteAngle)));
    	return new Vector(newX, newY);
    }
    
    public void setAngle( double angle){
		this.spriteAngle = angle;
	}
    public double getAngle() {
    	return this.spriteAngle;
    }
    public void setAlpha(double newAlpha) {
    	if (newAlpha > 0.00001 && newAlpha <= 1.0000001)
    		this.alpha = newAlpha;
    	else
    		this.alpha = 0.0;
    }
    public double getAlpha() {
    	return this.alpha;
    }

	public void setSizeFactor(double factor) {
		this.spriteSizePercent = factor;
	}
	public double getSizeFactor() {
		return spriteSizePercent;
	}
    
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public int getTileDimension(){
		return tileSize;
	}
	
	
	
	
	public Shape getGraphicAbsoluteRotationalBounds( double graphicsSizeFactorX, double graphicsSizeFactorY, double angleRadians, Point ownerEntityPosition ){
		
		Rectangle box = new Rectangle();

		box.setSize(
				(int) ( this.getImage().getWidth(null)  * graphicsSizeFactorX * this.getSizeFactor()), 
				(int) ( this.getImage().getHeight(null) * graphicsSizeFactorY* this.getSizeFactor() )
				);
		
		box.setLocation( 
				(int)( this.getOffsetX() * graphicsSizeFactorX * this.getSizeFactor()) , 
				(int)( this.getOffsetY() * graphicsSizeFactorY * this.getSizeFactor()) 
				);
		
		Point[] cornersArray = new Point[]{
				new Point( (int)box.getMinX() , (int)box.getMinY() ),
				new Point( (int)box.getMaxX() , (int)box.getMinY() ),
				new Point( (int)box.getMaxX() , (int)box.getMaxY() ),
				new Point( (int)box.getMinX() , (int)box.getMaxY() )
			};
		
		int[] newCornersX = new int[cornersArray.length];
		int[] newCornersY = new int[cornersArray.length];
		
		for ( int i = 0 ; i < cornersArray.length ; ++i ){
			
			Point rotatedCorner = UtilityMath.getRotationalAbsolutePositionOf(cornersArray[i], angleRadians);
			newCornersX[i] = rotatedCorner.x + ownerEntityPosition.x;
			newCornersY[i] = rotatedCorner.y + ownerEntityPosition.y;
		}
		
		Shape rotatedRectangle = new Polygon( newCornersX, newCornersY, newCornersX.length );
		
		box.setLocation( 
				(int)(ownerEntityPosition.getX() + box.getLocation().getX() * graphicsSizeFactorX * this.getSizeFactor()) , 
				(int)(ownerEntityPosition.getY() + box.getLocation().getY() * graphicsSizeFactorY * this.getSizeFactor()) 
				);
		
		return rotatedRectangle;
	}
	
	public Shape getGraphicRelativeRotationalBounds( double graphicsSizeFactorX, double graphicsSizeFactorY, double angleRadians, int areaExtender ){
		Rectangle box = new Rectangle();
		
		int scaledWidth = (int)(this.getImage().getWidth(null) * graphicsSizeFactorX * this.getSizeFactor());
		int sclaedHeight = (int)(this.getImage().getHeight(null)* graphicsSizeFactorY * this.getSizeFactor()) ;
		
		box.setSize(
				scaledWidth + areaExtender, 
				sclaedHeight + areaExtender);
		
		box.setLocation( 
				(int) ( (-areaExtender/2 + this.getOffsetX() ) * graphicsSizeFactorX * this.getSizeFactor()), 
				(int) ( (-areaExtender/2 + this.getOffsetY() ) * graphicsSizeFactorY * this.getSizeFactor())
			);
		
		Point[] cornersArray = new Point[]{
				new Point( (int)box.getMinX() , (int)box.getMinY() ),
				new Point( (int)box.getMaxX() , (int)box.getMinY() ),
				new Point( (int)box.getMaxX() , (int)box.getMaxY() ),
				new Point( (int)box.getMinX() , (int)box.getMaxY() )
			};
			
			int[] newCornersX = new int[cornersArray.length];
			int[] newCornersY = new int[cornersArray.length];
			
			for ( int i = 0 ; i < cornersArray.length ; ++i ){
				
				Point rotatedCorner = UtilityMath.getRotationalAbsolutePositionOf(cornersArray[i], angleRadians);
				newCornersX[i] = rotatedCorner.x;
				newCornersY[i] = rotatedCorner.y;
			}
			
			Shape rotatedRectangle = new Polygon( newCornersX, newCornersY, newCornersX.length );
			
		
			box.setLocation( 
				(int) ( (-areaExtender/2 + this.getOffsetX() ) * graphicsSizeFactorX * this.getSizeFactor()), 
				(int) ( (-areaExtender/2 + this.getOffsetY() ) * graphicsSizeFactorY * this.getSizeFactor())
			);
		return rotatedRectangle;
	}
	
	public Shape getGraphicAbsoluteTranslationalBounds( double graphicsSizeFactorX, double graphicsSizeFactorY, Point ownerEntityPosition ){
		
		Rectangle box = new Rectangle();

		box.setSize(
				(int) ( this.getImage().getWidth(null)  * graphicsSizeFactorX * this.getSizeFactor()), 
				(int) ( this.getImage().getHeight(null) * graphicsSizeFactorY* this.getSizeFactor() )
				);

		box.setLocation( 
				(int)(ownerEntityPosition.getX() + this.getOffsetX() * graphicsSizeFactorX * this.getSizeFactor()) , 
				(int)(ownerEntityPosition.getY() + this.getOffsetY() * graphicsSizeFactorY * this.getSizeFactor()) 
				);
		
		return box;
	}
	
	public Shape getGraphicRelativeTranslationalBounds( double graphicsSizeFactorX, double graphicsSizeFactorY, int areaExtender ){
		Rectangle returnBox = new Rectangle();
		
		int scaledWidth = (int)(this.getImage().getWidth(null) * graphicsSizeFactorX * this.getSizeFactor());
		int sclaedHeight = (int)(this.getImage().getHeight(null)* graphicsSizeFactorY * this.getSizeFactor()) ;
		
		returnBox.setSize(
				scaledWidth + areaExtender, 
				sclaedHeight + areaExtender);
		returnBox.setLocation( 
				(int) ( (-areaExtender/2 + this.getOffsetX() ) * graphicsSizeFactorX * this.getSizeFactor()), 
				(int) ( (-areaExtender/2 + this.getOffsetY() ) * graphicsSizeFactorY * this.getSizeFactor())
			);
		return returnBox;
	}
	
	
	
    
    public static class Stillframe extends Sprite {  // Object with still image

        protected String fileName;
    	
        protected BufferedImage[][] image;
        
        
        public Stillframe(String path){ // GENERIC CONSTRUCTOR
        	super(0,0);
        	
        	initialize(path);
        	
        	visibility = true;
        }
        
        public Stillframe(String path, int offset_x, int offset_y) { 
        	super(offset_x,offset_y);

        	initialize(path);
        	
        	visibility = true;
        	this.spriteOffsetX = offset_x; //OPTIMIZE redundancy?
        	this.spriteOffsetY = offset_y;
        }
        
        public Stillframe( String path, int offset_x, int offset_y , byte flag ){ //CONSTRUCTOR ALLOWING FOR CERTAIN AUTOMATIC INITIALIZERS LIKE CENTERING
        	super(0,0);
        	initialize(path,flag);
        	
        	this.spriteOffsetX += offset_x;
    		this.spriteOffsetY += offset_y;
        }
        
        public Stillframe( String path, int offset_x, int offset_y , byte flag, int sizePercent ){ //CONSTRUCTOR ALLOWING FOR CERTAIN AUTOMATIC INITIALIZERS LIKE CENTERING
        	super(0,0);
        	initialize(path,flag);
        	
        	this.setSizeFactor(sizePercent/100.0);
        	
        	this.spriteOffsetX += offset_x;
    		this.spriteOffsetY += offset_y;
        }
        
        public Stillframe( String path , byte flag ){ //CONSTRUCTOR ALLOWING FOR CERTAIN AUTOMATIC INITIALIZERS LIKE CENTERING
        	super(0,0);
        	initialize(path,flag);
        	
        }
        
        private boolean initFlags( BufferedImage image , byte flag ){
        	if ( flag == Sprite.CENTERED ){
        		this.spriteOffsetX = -image.getWidth() / 2 ;
        		this.spriteOffsetY = -image.getHeight() / 2 ;
        	}
        	else if ( flag == Sprite.CENTERED_BOTTOM ){
        		this.spriteOffsetX = -image.getWidth() / 2 ;
        		this.spriteOffsetY = -image.getHeight();
        	}
        	else if ( flag == Sprite.CENTERED_TOP ){
        		this.spriteOffsetX = -image.getWidth() / 2 ;
        		this.spriteOffsetY = 0;
        	}
        	else{
        		return false;
        	}
        	return true;
        }
        
        private void initialize( String path , byte...flag){
        	
        	this.fileName = path;
        	
        	if (!checkPath(System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path)) {
        		fileName = null;
        		image = new BufferedImage[1][1];
        		image[0][0] = new MissingIcon().getMissingSprite();
        		System.err.println("Image file '"+path +"' not found, using placeholder");
        	}
        	else {	  
        		
        		//load temporary whole image
        		BufferedImage wholeImage = loadImage( System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path );
        		
        		if ( flag.length == 1 ){
	            	if ( !initFlags(wholeImage,flag[0]) ){ //apply offsetting flags
	            		System.err.println(path+" sprite has invalid parameter");
	            	}
        		}
        		
        		if ( wholeImage.getWidth() < 200 || wholeImage.getHeight() < 200 ){ 
        			image = new BufferedImage[1][1];
        			image[0][0] = wholeImage;
        		}
        		else{

					image = splitImage(wholeImage);
					
            		System.err.println("Image was split successfully");
        		}
        	}
        }

        public String getPathName(){
        	return this.fileName;
        }
        
        @Override
        public void draw(ReferenceFrame camera , GraphicComposite.Static composite ){

        	AffineTransform entityTransformation = new AffineTransform(); //OPTIMIZE Test making AffineTransform field of Sprite

        	entityTransformation.rotate( Math.toRadians(this.spriteAngle)+ composite.getGraphicAngle() ); 
        	entityTransformation.scale( (double)(this.spriteSizePercent*composite.getGraphicsSizeX()) , (double)(this.spriteSizePercent*composite.getGraphicsSizeY()) );
        	
        	entityTransformation.translate(spriteOffsetX, spriteOffsetY);

        	camera.drawOnCamera( composite, entityTransformation );
        	
        }

        protected void getImageDimensions() { 
            width = image[0][0].getWidth(null);
            height = image[0][0].getHeight(null); 
        }
        public boolean checkPath(String path) {
        	boolean exists = false;
        	exists = new File(path).exists(); 	
        	return exists;
        }
        public BufferedImage loadImage(String imageName) { // 
            //ImageIcon ii = new ImageIcon(imageName);
            //image = ii.getImage();
        	BufferedImage returnImage = null;
        	
    		try {

    			returnImage = ImageIO.read(new File(imageName));
    			//image = ImageIO.read(new File(System.getProperty("user.dir") + "\\Assets\\" +));

    		} catch (IOException e) {;
    			e.printStackTrace();
    		}
    		
    		return returnImage;
        }
        public void loadImagePlaceHolder(){
        	image[0][0] = new MissingIcon().getMissingSprite();
        }

        @Override 
        public Image getImage() { 
            return image[0][0];
        }
        
        @Override
        public BufferedImage[][] getBufferedImage() {
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
    
    private static class Missing extends Stillframe{

		public Missing() {
			super("missing_sprite");
		}

    }
    
    
	protected BufferedImage[][] splitBufferedImage( String path ) throws IOException{
		
		System.out.print("Loading Background "+path);
		File file = new File( System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path +" ");
		
		BufferedImage[][] returnGrid = new BufferedImage[9][9];
		
		try {
		
		BufferedImage bimg = ImageIO.read( file );
		int width = bimg.getWidth();
		int height = bimg.getHeight();
		
		int tileWidth = (int) Math.floor(width/3); //TODO change to set 100x100 tiles and calculate number with that 
		int tileHeight = (int) Math.floor(height/3); 

		System.out.print(" of size "+tileWidth+" x "+tileHeight+"px ");
		tileSize = tileWidth;		
		
			for ( int i = 0 ; i < 3 ; i++ ){
				
				for ( int j = 0 ; j < 3 ; j++ ) {
					
					Rectangle sourceRegion = new Rectangle( i*tileWidth, j*tileHeight, tileWidth, tileHeight );
					
	
						ImageInputStream stream = ImageIO.createImageInputStream(file);
						Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
						
						if (readers.hasNext()) {
						    ImageReader reader = readers.next();
						    reader.setInput(stream);
			
						    ImageReadParam param = reader.getDefaultReadParam();
						    
						    param.setSourceRegion(sourceRegion); // Set region
			
						    //tiles.add( reader.read(0, param) ); // Will read only the region specified
						    returnGrid[i][j] = reader.read(0, param);
						    
						    System.out.print(".");
						}	
				}		
			}

		} catch (IOException e) {
			System.err.println("Failed to load Split Image" );
			e.printStackTrace();
			
		} // File or input stream
		System.out.println("Split Image Loaded");
		return returnGrid;
	}
	
	
	
	protected BufferedImage[][] splitImage( BufferedImage image ){
		
		int rows = 3; //You should decide the values for rows and cols variables
        int columns = 3;

        int chunkWidth = image.getWidth() / columns; // determines the chunk width and height
        int chunkHeight = image.getHeight() / rows;

        tileSize = chunkWidth;
        
        BufferedImage imgs[][] = new BufferedImage[rows][columns]; //Image array to hold image chunks
        
        for (int i = 0; i < imgs.length; ++i) {
            for (int j = 0; j < imgs[i].length; ++j) {
            	imgs[i][j] = image.getSubimage(image.getWidth() / columns * i, image.getHeight() / rows * j,
            			image.getWidth() / columns, image.getHeight() / rows);
            }
        }
        
        System.err.println("Split cuccessful "+imgs.length + " "+imgs[0].length);
        return imgs;
	}
    
    
}
