package entityComposites;

import java.awt.Point;
import java.io.File;
import animation.Animation;
import entityComposites.*;
import physics.Boundary;
import physics.BoundaryPolygonal;
import physics.CollisionEngine;
import sprites.SpriteAnimated;
import sprites.SpriteStillframe;
import sprites.RenderingEngine;
import sprites.Sprite;

/*
 * Static Entity class, for unmoving sprites. Has graphic that can be either still image or animation.
 */
public class EntityStatic extends Entity{

	//COMPOSITE VARIABLES, LATER TO BE LIST OF COMPOSITES
	protected TranslationComposite translationType = new TranslationComposite(this);
	protected GraphicComposite graphicsComposite = GraphicCompositeNull.getNullSprite(); 
	protected Collider collisionType = ColliderNull.getNonCollidable();
	
	public EntityStatic(int x, int y) {

    	super(x,y);
    	//isSelected = false;
    }  
	
	//COMPOSITE CONTRUCTION
	public EntityStatic( String name , int x, int y) {
    	super(x,y);
    	this.name = name;
    	
    }

	public EntityStatic(Point entityPosition) {
		super( entityPosition.x , entityPosition.y );
	}

	protected void setGraphicComposite(GraphicComposite spriteType){ 
		this.graphicsComposite = spriteType; 
		}
	
	public GraphicComposite getGraphicComposite(){ 	
		return this.graphicsComposite; 
	}
	
	protected void setTranslationComposite( TranslationComposite translationType ){ 
		this.translationType = translationType; 
	}
	
	public TranslationComposite getTranslationComposite(){
		return this.translationType;
	}
	
	protected void setCollisionComposite(Collider collisionType){ 
		this.collisionType = collisionType; 
	}

	public Collider getColliderComposite(){
		return this.collisionType;			
	}
	
	
	@Deprecated
    public void loadSprite(String path){ // needs handling if failed. Also needs to be moved out of object class into sprites

		CompositeFactory.addGraphicTo( this , new SpriteStillframe( path , this) );
    	
    }
    @Deprecated
    public void loadSprite(String path, int offset_x , int offset_y){ // needs handling if failed. Also needs to be moved out of object class into sprites
    	
    	CompositeFactory.addGraphicTo( this , new SpriteStillframe( path , this) );
    }



    @Deprecated
    public Sprite getEntitySprite(){ // gets the Object's sprite, still image or animation MOVE TO SPRITEPROPERTY
    	return ((GraphicComposite)this.graphicsComposite).getSprite();
    }
    @Deprecated
    public void setEntitySprite( Sprite entitySprite ){
    	 ((GraphicComposite)this.graphicsComposite).setSprite( entitySprite );
    }
    @Deprecated
    public int getSpriteOffsetX(){
    	return ((GraphicComposite)this.graphicsComposite).getSprite().getOffsetX(); 
    }
    @Deprecated
    public int getSpriteOffsetY() {
    	return ((GraphicComposite)this.graphicsComposite).getSprite().getOffsetY();
    }
    /**@deprecated
     * @param x_offset
     * @param y_offset
     * @param width
     * @param height
     * <b /> Sets the x and y coordinates and width and height for this object's bounding box
     */
    public void setBoundingBox(int x_offset, int y_offset , int width , int height) {
    	
        //boundingBox = new Rectangle(x_offset, y_offset, width , height);
        //boundary = new BoundingBox(boundingBox);
    }
    
    //overloaded function to accept Rectangle that getBounds() will return.
    /**
     * 
     * @param getBounds The rectangle that will be passed any time getBounds() is called [or maybe getBounds2D, gotta try it]
     */
    //public void setBoundingBox(Rectangle getBounds){
    //	boundingBox = getBounds;
    //	boundary = new BoundingBox(getBounds);
    //}
	
	//public Rectangle getBoundingBox(){ //move position to override in dynamic entity since static doesnt need position calc.
	//	return new Rectangle (getX() + boundingBox.x , getY() + boundingBox.y , boundingBox.width , boundingBox.height);
	//}

	//public Boundary getBoundaryLocal(){
		
	//	return ((Collidable)collisionType).getBoundary().atPosition((int)x,(int)y);
	//}
	
	public Boundary getBoundary(){
		
	    return ((Collider)collisionType).getBoundary();
	}

	@Override
	public void selfDestruct(){
		setBoundingBox(0,0,0,0); // This is almost exclusively so the Collision Detector closes collisions with dead entities
		alive = false;
	}
	
	/* #################################################################################
	 * 
	 * 		EVENT METHODS - to be overriden in children classes
	 * 
	 * #################################################################################
	 */
	
	public String toString()
	{
		return name;
	}
	
	public int xRelativeTo( EntityStatic entity ){
		return ( this.getX() - entity.getX() );
	}
	public int yRelativeTo( EntityStatic entity ){
		return ( this.getY() - entity.getY() );
	}
	public Point positionRelativeTo( EntityStatic entity ){
		return new Point( 
				this.getX() - entity.getX() , 
				this.getY() - entity.getY() 
				);
	}

	public void move(Point distance) { //MAKE VECTOR LATER

		x=x+(float)distance.getX();
		y=y+(float)distance.getY();
	}
	
	public boolean hasCollider(){
		return !( this.collisionType instanceof ColliderNull );
	}
	
	public boolean hasGraphics(){
		return !( this.graphicsComposite instanceof GraphicComposite );
	}
	
	public void disable(){
		
		this.graphicsComposite.disable();
		this.collisionType.disable();
		
		this.graphicsComposite = null;
		this.collisionType = null;
		
	}
	
}
