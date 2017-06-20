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
	
	private final NullTicket nullTicket = new NullTicket();
	private ListNodeTicket updaterSlot = nullTicket;
	//COMPOSITE VARIABLES, LATER TO BE LIST OF COMPOSITES
	protected TranslationComposite translationType = new TranslationComposite();
	protected DynamicRotationComposite rotationType = new DynamicRotationComposite(this);
	protected GraphicComposite graphicsComposite = GraphicCompositeNull.getNullSprite(); 
	protected Collider collisionType = ColliderNull.getNonCollidableSingleton();
	protected AngularComposite angularType = new AngularComposite.AngleComposite(this);
	
	protected ParentChildRelationship[] family = new ParentChildRelationship[0];
	
	protected ArrayList<UpdateableComposite> updateablesList = new ArrayList<UpdateableComposite>();
	protected ArrayList<TranslatableComposite> translatablesList = new ArrayList<TranslatableComposite>();
	
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
	
	protected void addUpdateable( UpdateableComposite updateable){
		updateablesList.add(updateable);
	}
	/**BE ADVISED: ALWAYS CALL super.updateComposite() WHEN SUBCLASSING ENTITYSTATIC
	 * 
	 */
	@Override
	public void updateComposite(){ //MAKE OWN COMPOSITE
		
		for ( UpdateableComposite composite : updateablesList ){
			composite.updateEntity(this);
		}
	}
	@Override
	public void updateEntity(EntityStatic entity) { 
		System.err.println("ENTITY STATIC WARNING");
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
	
	protected void setRotationComposite( DynamicRotationComposite rotationType ){ 
		this.rotationType = rotationType; 
	}
	
	public DynamicRotationComposite getRotationComposite(){
		return this.rotationType;
	}
	
	public AngularComposite getAngularComposite(){
		return this.angularType;
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
	public ParentChildRelationship[] getParentChildRelationship() {
		return this.family;
	}
	public void setCompositedPos( double x , double y ){
		this.x = x;
		this.y = y;
		this.manipulateChildren();
	}//
	
	public void compositedTranslate( double x, double y){
		this.x = this.x + x;
		this.y = this.y + y;
	
	}
	
	protected void manipulateChildren( ){
		for ( ParentChildRelationship parentComposite : family ){
			parentComposite.manipulateChildren();
		}
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

		x=x+(int)distance.getX();
		y=y+(int)distance.getY();
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
		if ( !updaterSlot.isActive() ){
			this.updaterSlot = board.addEntityToUpdater(this);
			System.out.println("Added "+this.name+" to updater");
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
		if ( updateablesList.size() > 0)
			return true;
		else
			return false;
	}
	
	public UpdateableComposite[] getUpdateables(){
		UpdateableComposite[] returnArray = new UpdateableComposite[ this.updateablesList.size() ];
		this.updateablesList.toArray(returnArray);
		return returnArray;
	}
	
	@Override
	public void removeUpdateable() {
		this.updaterSlot.removeSelf();
		this.updaterSlot = nullTicket;
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
	
	public Point getRelativePositionOf(Entity entity){
		
		Point returnPoint = this.getRelativeTranslationalPositionOf(entity);

		returnPoint = this.angularType.getRotationalPositionRelativeTo(returnPoint);
		
		return returnPoint;
	}
	
	public Point getRelativePositionOf(Point point_on_entity ){
		
		
		Point returnPoint = this.angularType.getRotationalPositionRelativeTo(point_on_entity);
		returnPoint = this.getRelativeTranslationalPositionOf( returnPoint );
		return returnPoint;
	}
	
	private static class NullTicket extends ListNodeTicket{
		 @Override
		public void removeSelf() {
			//System.err.println( " was not in updater");
		}
		 @Override
		public boolean isActive() {
			return false;
		}
	}
	
}
