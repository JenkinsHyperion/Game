package entityComposites;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import animation.Animation;
import engine.BoardAbstract;
import engine.Scene;
import entityComposites.*;
import physics.Boundary;
import physics.BoundaryPolygonal;
import physics.CollisionEngine;
import sprites.SpriteAnimated;
import sprites.SpriteStillframe;
import utility.DoubleLinkedList;
import utility.ListNodeTicket;
import sprites.RenderingEngine;
import sprites.Sprite;

/*
 * Static Entity class, for unmoving sprites. Has graphic that can be either still image or animation.
 */
public class EntityStatic extends Entity implements UpdateableComposite{

	private Scene ownerScene;
	private Integer sceneIndex;
	
	private ListNodeTicket updaterSlot;
	//COMPOSITE VARIABLES, LATER TO BE LIST OF COMPOSITES
	protected TranslationComposite translationType = new TranslationComposite();
	protected RotationComposite rotationType = new RotationComposite(this);
	protected GraphicComposite graphicsComposite = GraphicCompositeNull.getNullSprite(); 
	protected Collider collisionType = ColliderNull.getNonCollidable();
	
	protected ParentChildRelationship[] family = new ParentChildRelationship[0];
	
	protected ArrayList<UpdateableComposite> updateables = new ArrayList<UpdateableComposite>();
	
	public EntityStatic(int x, int y) {

    	super(x,y);
    	//isSelected = false;
    }  
	
	//COMPOSITE CONTRUCTION
	public EntityStatic( String name , int x, int y) {
    	super(x,y);
    	this.name = name;
    }
	
	public EntityStatic( String name , Point position ) {
    	super( position.x , position.y );
    	this.name = name;
    }

	public EntityStatic(Point entityPosition) {
		super( entityPosition.x , entityPosition.y );
	}
	@Override
	public void updateComposite(){ //MAKE OWN COMPOSITE
		for ( UpdateableComposite composite : updateables ){
			composite.updateEntity(this);
		}
	}
	@Override
	public void updateEntity(EntityStatic entity) { 
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
	
	protected void setRotationComposite( RotationComposite rotationType ){ 
		this.rotationType = rotationType; 
	}
	
	public RotationComposite getRotationComposite(){
		return this.rotationType;
	}
	
	protected void setCollisionComposite(Collider collisionType){ 
		this.collisionType = collisionType; 
	}

	public Collider getColliderComposite(){
		return this.collisionType;			
	}
	
	protected void addFamilyRole( ParentChildRelationship relationship ){
		ParentChildRelationship[] returnArray = new ParentChildRelationship[family.length+1];
		for ( int i = 0 ; i < family.length ; i++ ){
			returnArray[i] = family[i];
		}
		this.family = null;
		returnArray[ returnArray.length-1 ] = relationship;
		this.family = returnArray;
		
	}
	
	@Deprecated
    public void loadSprite(String path){ // needs handling if failed. Also needs to be moved out of object class into sprites

		CompositeFactory.addGraphicTo( this , new SpriteStillframe( path ) );
    	
    }
    @Deprecated
    public void loadSprite(String path, int offset_x , int offset_y){ // needs handling if failed. Also needs to be moved out of object class into sprites
    	
    	CompositeFactory.addGraphicTo( this , new SpriteStillframe( path ) );
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
	
	public boolean hasGraphics(){
		return !( this.graphicsComposite instanceof GraphicComposite );
	}
	
	public void disable(){
		System.out.println("DISABLING "+this);
		
		this.graphicsComposite.disable();
		this.graphicsComposite = null;
		
		this.collisionType.disable();
		this.collisionType = null;		
		
		this.updaterSlot.removeSelf();
		
		this.ownerScene.removeEntity(this.sceneIndex);

		System.out.println("done");
	}
	
	public void addToUpdater( BoardAbstract board){
		if ( updaterSlot == null ){
			this.updaterSlot = board.addEntityToUpdater(this);
		}else{
			System.err.println("Warning: "+this.name+" is already in updater");
		}

	}
	
	public void removeFromUpdater(){
		this.updaterSlot.removeSelf();
	}

	public boolean hasTranslation() {
		return this.translationType.exists();
	}
	
	public boolean hasRotation() {
		return this.rotationType.exists();
	}
	
	public boolean hasCollider() {
		return this.collisionType.exists();
	}
	
	public boolean hasUpdateables(){
		if ( updateables.size() > 0)
			return true;
		else
			return false;
	}
	
	public UpdateableComposite[] getUpdateables(){
		UpdateableComposite[] returnArray = new UpdateableComposite[ this.updateables.size() ];
		this.updateables.toArray(returnArray);
		return returnArray;
	}
	
	@Override
	public void removeUpdateable() {
		this.updaterSlot.removeSelf();
	}

	@Override
	public boolean addCompositeToUpdater(BoardAbstract board) {
		if ( this.updaterSlot == null ){
    		this.updaterSlot = board.addCompositeToUpdater(this);
    		return true;
    	}
    	else{
    		return false;
    	}
	}
	
	public void addEntityToScene(Scene scene , int index){
		if ( this.sceneIndex == null ){
			this.ownerScene = scene;
			this.sceneIndex = index;
		}else{
			System.err.println("Warning: "+this.name+" is already in a scene and cannot yet be moved");
		}
	}

	public void indexShift(){
		this.sceneIndex--;
	}
	
}
