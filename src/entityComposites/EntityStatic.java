package entityComposites;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;

import animation.Animation;
import engine.BoardAbstract;
import engine.MovingCamera;
import engine.ReferenceFrame;
import engine.Scene;
import entityComposites.*;
import physics.Boundary;
import physics.BoundaryPolygonal;
import physics.CollisionEngine;
import physics.Vector;
import sprites.SpriteAnimated;
import utility.DoubleLinkedList;
import utility.ListNodeTicket;
import sprites.RenderingEngine;
import sprites.Sprite;

/*
 * Static Entity class, for unmoving sprites. Has graphic that can be either still image or animation.
 */
public class EntityStatic extends Entity{

	private Scene ownerScene;
	private Integer sceneIndex;
	
	private static final NullTicket nullTicket = new NullTicket();
	private ListNodeTicket updaterSlot = nullTicket;
	//COMPOSITE VARIABLES, LATER TO BE LIST OF COMPOSITES
	protected TranslationComposite translationComposite = TranslationComposite.nullTranslationComposite();
	protected DynamicRotationComposite rotationalComposite = new DynamicRotationComposite(this);
	protected GraphicComposite graphicsComposite = GraphicComposite.nullGraphicsComposite(); 
	protected Collider colliderComposite = ColliderNull.nullColliderComposite();
	protected AngularComposite angularComposite = AngularComposite.getFixedAngleSingleton();
	protected Rigidbody rigidbodyComposite = Rigidbody.nullSingleton();
	
	protected ParentComposite parentComposite = ParentComposite.nullParentComposite();
	protected ChildComposite childComposite = ChildComposite.nullChildComposite();
	
	protected ArrayList<UpdateableComposite> updateablesList = new ArrayList<UpdateableComposite>();

	public EntityStatic(int x, int y) {

    	super(x,y);
    	//isSelected = false;
    	init();
    }  
	
	//COMPOSITE CONTRUCTION
	public EntityStatic( String name , int x, int y) {
    	super(x,y);
    	this.name = name;
    	init();
    }
	
	public EntityStatic( String name , Point position ) {
    	super( position.x , position.y );
    	this.name = name;
    	init();
    }

	public EntityStatic(Point entityPosition) {
		super( entityPosition.x , entityPosition.y );
		init();
	}
	
	private void init(){
		 
	}
	
	protected int addUpdateableCompositeToEntity( UpdateableComposite updateable){
		
		int index = updateablesList.size();
		
		updateablesList.add(updateable);
		
		return index;
	}
	
	protected void removeUpdateableCompositeFromEntity( int index ){
		try {
			while( ownerScene.isWorking() ){
	
					wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("WAITED AND NOW REMOVING "+index+" size "+updateablesList.size());
		updateablesList.remove(index);
		for ( int i = index ; i < updateablesList.size() ; i++){
			updateablesList.get(i).decrementIndex();
		}
		
	}
	
	public void addUpdateableEntityToUpdater( BoardAbstract board ){
		this.updaterSlot = board.addEntityToUpdater(this);
	}
	
	public void removeUpdateableEntityToUpdater(){
		this.updaterSlot.removeSelfFromList();
		this.updaterSlot = nullTicket;
	}
	
	/**BE ADVISED: WHEN OVERRIDING IN A SUBCLASS, ALWAYS CALL super.updateComposite() which contains core composite updater functionality.
	 * 
	 */
	public void updateEntity() {
		
		for ( UpdateableComposite composite : updateablesList ){
			composite.updateEntityWithComposite(this);
		}
	}
	
	/*######################################################################################################################
	 * 		COMPOSITE GET/SETTERS    
	 * Getters are public, Setters are protected to be used by CompositeFactory
	 * #####################################################################################################################
	 */
	
	protected void setGraphicComposite(GraphicComposite spriteType){ 
		this.graphicsComposite = spriteType; 
		}
	public GraphicComposite getGraphicComposite(){ 	
		return this.graphicsComposite; 
	}
	
	
	protected void setTranslationComposite( TranslationComposite translationType ){ 
		this.translationComposite = translationType; 
	}
	public TranslationComposite getTranslationComposite(){
		return this.translationComposite;
	}
	
	
	protected void setRotationComposite( DynamicRotationComposite rotationType ){ 
		this.rotationalComposite = rotationType; 
	}
	public DynamicRotationComposite getRotationComposite(){
		return this.rotationalComposite;
	}
	
	
	protected void setAngularComposite( AngularComposite angularType ){ 
		this.angularComposite = angularType; 
	}
	public AngularComposite getAngularComposite(){
		return this.angularComposite;
	}
	
	
	protected void setCollisionComposite(Collider collisionType){ 
		this.colliderComposite = collisionType; 
	}
	public Collider getColliderComposite(){
		return this.colliderComposite;			
	}
	
	
	protected void setRigidbody( Rigidbody rigidbody){ 
		this.rigidbodyComposite = rigidbody; 
	}
	public Rigidbody getRigidbody(){
		return this.rigidbodyComposite;			
	}
	
	
	protected void addParentComposite( ParentComposite parentComposite ){
		this.parentComposite = parentComposite;
	}
	
	protected ParentComposite getParentComposite(){
		return this.parentComposite;
	}
	
	protected void setChildComposite( ChildComposite childComposite ){
		this.childComposite = childComposite;
	}
	
	protected ChildComposite getChildComposite(){
		return this.childComposite;
	}
	
	/* #########################################################################################################################
	 *		CompositeFactory utility methods
	 * #########################################################################################################################
	 */
	
	public AngularComposite addAngularComposite(){
		return CompositeFactory.addAngularComposite(this);
	}

	public TranslationComposite addTranslationTo(){
		return CompositeFactory.addTranslationTo(this);
	}
	
	public Collider addInitialColliderTo( Boundary bounds ){ //FIXME AUTOMATICALLY CHECK IF ENTITIY IS ADDED
		return CompositeFactory.addInitialColliderTo(this, bounds);
	}
	
	public Collider addColliderTo( Boundary bounds, BoardAbstract board ){
		return CompositeFactory.addColliderTo(this, bounds, board);
	}
	
	public Collider addRotationalColliderTo( AngularComposite angularComposite , Boundary bounds ){
		return CompositeFactory.addRotationalColliderTo(this, bounds, angularComposite );
	}
	
	public DynamicRotationComposite addDynamicRotationTo(){
		return CompositeFactory.addDynamicRotationTo(this);
	}
	
	public GraphicComposite addGraphicTo( Sprite sprite ){
		return CompositeFactory.addGraphicTo(this,sprite);
	}
	
	public void addRigidbodyTo(){
		CompositeFactory.addRigidbodyTo(this);
	}
	
	
	/* #########################################################################################################################
	 *		Composite Nullifier Methods
	 * #########################################################################################################################
	 */
	
	protected void nullifyGraphicsComposite(){
		this.graphicsComposite = GraphicComposite.nullGraphicsComposite();
	}
	
	protected void nullifyTranslationComposite(){
		this.translationComposite = TranslationComposite.nullTranslationComposite();
	}
	@Deprecated
	public void removeTranslationComposite(){
		
		//this.nullifyTranslationComposite();
		
		for ( EntityStatic child : this.parentComposite.getChildrenEntities() ){
			child.nullifyTranslationComposite();
		}
	}
	
	protected void nullifyColliderComposite(){
		this.colliderComposite = ColliderNull.nullColliderComposite();
	}
	
	protected void nullifyAngularComposite(){
		this.angularComposite = AngularComposite.getFixedAngleSingleton();
	}
	
	protected void nullifyRigidbodyComposite(){
		this.rigidbodyComposite = Rigidbody.nullSingleton();
	}
	
	protected void nullifyRotationComposite(){
		this.rotationalComposite = DynamicRotationComposite.nullSingleton();
	}
	
	protected void nullifyParentComposite(){
		this.parentComposite = ParentComposite.nullParentComposite();
	}
	
	protected void nullifyChildComposite(){
		this.childComposite = ChildComposite.nullChildComposite();
	}
	
	public void setCompositedPos( double x , double y ){
		this.x = x;
		this.y = y;
		this.parentComposite.setCompositedPosition( x, y);
	}
	
	
	public double getDX(){
		return this.translationComposite.getDX();
	}
	public double getDY(){
		return this.translationComposite.getDY();
	}
	
	public double getDeltaX(){
		return this.translationComposite.getDX()+this.getX();
	}
	public double getDeltaY(){
		return this.translationComposite.getDY()+this.getY();
	}
	public Point getDeltaPosition(){
		return new Point(
			(int) (this.translationComposite.getDX()+this.getX()),
			(int) (this.translationComposite.getDY()+this.getY())
		);
	}
	
	// ----------------
	
	
	public Vector getOrientationVector(){
		return this.angularComposite.getOrientationVector();
	}
	
	@Deprecated
    public void loadSprite(String path){ // needs handling if failed. Also needs to be moved out of object class into sprites

		CompositeFactory.addGraphicTo( this , new Sprite.Stillframe( path ) );
    	
    }

    @Deprecated
    public Sprite getEntitySprite(){ // gets the Object's sprite, still image or animation MOVE TO SPRITEPROPERTY
    	return ((GraphicComposite)this.graphicsComposite).getSprite();
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
		
	    return ((Collider)colliderComposite).getBoundary();
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
		
		this.graphicsComposite.disableComposite();
		this.graphicsComposite = null;
		
		this.colliderComposite.disableComposite();
		this.colliderComposite = null;		
		
		this.updaterSlot.removeSelfFromList();
		
		this.ownerScene.removeEntity(this.sceneIndex);

		System.out.println("done");
	}

	// PARENT CHILD METHODS
	public boolean isParent(){
		return this.parentComposite.isParent();
	}
	public EntityStatic[] getChildrenEntities(){
		return this.parentComposite.getChildrenEntities();
	}
	public boolean isChild(){
		return this.childComposite.isChild();
	}
	public EntityStatic getParentEntity(){
		return this.childComposite.getParentEntity();
	}
	
	//GET COMPOSITE METHODS
	public boolean hasTranslation() {
		return this.translationComposite.exists();
	}
	
	public boolean hasRotation() {
		return this.rotationalComposite.exists();
	}
	
	public boolean hasCollider() {
		return this.colliderComposite.exists();
	}
	
	public boolean hasUpdateables(){
		if ( updateablesList.size() > 0)
			return true;
		else
			return false;
	}
	public int numberOfUpdateableComposites(){
		return updateablesList.size();
	}
	
	public UpdateableComposite[] getUpdateables(){
		UpdateableComposite[] returnArray = new UpdateableComposite[ this.updateablesList.size() ];
		this.updateablesList.toArray(returnArray);
		return returnArray;
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
	
	public Vector getRelativeTranslationalVectorOf(EntityStatic entity){
		
		Point returnPoint = this.getRelativeTranslationalPositionOf(entity);
		
		Vector returnVector = new Vector( returnPoint.getX(), returnPoint.getY());
		
		return returnVector;
	}
	/**Calculates the position of the input relative to (or as "seen" by) this entity. This includes both relative translation and rotation.
	 * Ensure that this is necessary for the application.
	 * <p>For example, Take the input position (0,100):
	 * <p>An entity at position (0,0) facing 0 degrees returns the unchanged (0,100)
	 * <p>An entity at the input point (0,100) facing any angle returns (0,0)
	 * <p>An entity at position (0,0) facing 45 degrees returns ( 100cos(45) , 100sin(45) )
	 * @param entity
	 * @return
	 */
	public Point getFullRelativePositionOf(Entity entity){
		
		Point returnPoint = this.getRelativeTranslationalPositionOf(entity);

		returnPoint = this.angularComposite.getRotationalRelativePositionOf(returnPoint);
		
		return returnPoint;
	}
	
	public Vector getFullRelativeVectorDistanceOf(EntityStatic entity){
		
		Point returnPoint = this.getRelativeTranslationalPositionOf(entity);

		returnPoint = this.angularComposite.getRotationalRelativePositionOf(returnPoint);
		
		Vector returnVector = new Vector( returnPoint.getX(), returnPoint.getY());
		
		return returnVector;
	}
	
	public Point getFullRelativePositionOf(Point point_on_entity ){
		
		Point returnPoint = this.getRelativeTranslationalPositionOf( point_on_entity );
		
		returnPoint = this.angularComposite.getRotationalRelativePositionOf(returnPoint);
		
		return returnPoint;
	}
	
	public Point getAbsolutePositionOf(Point relativePoint ){
		
		Point returnPoint = this.angularComposite.getRotationalAbsolutePositionOf(relativePoint);
		
		returnPoint = this.getTranslationalAbsolutePositionOf( returnPoint );
		
		return returnPoint;
	}
	
	public Point getAbsolutePositionOf(Entity entity ){
		
		return this.getAbsolutePositionOf( entity.getPosition() ); //see directly above
	}
	
	public Point getAbsolutePositionOf(Point2D relativePoint ){
		
		Point returnPoint = this.angularComposite.getRotationalAbsolutePositionOf(relativePoint);
		
		returnPoint = this.getTranslationalAbsolutePositionOf( returnPoint );
		
		return returnPoint;
	}
	
	private static class NullTicket extends ListNodeTicket{
		 @Override
		public void removeSelfFromList() {
			//System.err.println( " was not in updater");
		}
		 @Override
		public boolean isActive() {
			return false;
		}
	}
	
}
