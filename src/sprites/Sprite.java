package sprites;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import animation.Animation;
import editing.MissingIcon;
import engine.ReferenceFrame;
import entityComposites.GraphicComposite;
import physics.Vector;

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

	public abstract Image getImage(); 
	public abstract BufferedImage getBufferedImage();
	
	public abstract Animation getAnimation(); 
	
	public abstract void updateSprite();
	
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
    
    
    
    
    public static class Stillframe extends Sprite {  // Object with still image

        protected String fileName;
    	
        protected int width;
        protected int height;
        protected BufferedImage image;
        
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
        	initialize(path);
        	
        	if ( !initFlags(flag) ){
        		System.err.println(path+" sprite has invalid parameter");
        	}
        	
        	this.spriteOffsetX += offset_x;
    		this.spriteOffsetY += offset_y;
        }
        
        public Stillframe( String path , byte flag ){ //CONSTRUCTOR ALLOWING FOR CERTAIN AUTOMATIC INITIALIZERS LIKE CENTERING
        	super(0,0);
        	initialize(path);
        	
        	if ( !initFlags(flag) ){
        		System.err.println(path+" sprite has invalid parameter");
        	}
        }
        
        private boolean initFlags( byte flag ){
        	if ( flag == Sprite.CENTERED ){
        		this.spriteOffsetX = -this.image.getWidth() / 2 ;
        		this.spriteOffsetY = -this.image.getHeight() / 2 ;
        	}
        	else if ( flag == Sprite.CENTERED_BOTTOM ){
        		this.spriteOffsetX = -this.image.getWidth() / 2 ;
        		this.spriteOffsetY = -this.image.getHeight();
        	}
        	else if ( flag == Sprite.CENTERED_TOP ){
        		this.spriteOffsetX = -this.image.getWidth() / 2 ;
        		this.spriteOffsetY = 0;
        	}
        	else{
        		return false;
        	}
        	return true;
        }
        
        private void initialize( String path ){
        	
        	this.fileName = path;
        	
        	if (!checkPath(System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path)) {
        		fileName = null;
        		image = new MissingIcon().getMissingSprite();
        		System.err.println("Image file '"+path +"' not found, using placeholder");
        	}
        	else {	    	
    	    	loadImage( System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path );
        	}
        }

        public String getPathName(){
        	return this.fileName;
        }
        
        @Override
        public void draw(ReferenceFrame camera , GraphicComposite.Active composite ){

        	AffineTransform entityTransformation = new AffineTransform(); //OPTIMIZE Test making AffineTransform field of Sprite

        	entityTransformation.rotate( Math.toRadians(this.spriteAngle)+ composite.getGraphicAngle() ); 
        	entityTransformation.scale( (double)(this.spriteSizePercent*composite.getGraphicsSizeX()) , (double)(this.spriteSizePercent*composite.getGraphicsSizeY()) );
        	
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
        	image = new MissingIcon().getMissingSprite();
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
    
    private static class Missing extends Stillframe{

		public Missing() {
			super("missing_sprite");
		}

    }
    
    
}
