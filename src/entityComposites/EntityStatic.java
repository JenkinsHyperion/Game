package entityComposites;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

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
	protected DynamicRotationComposite rotationalComposite = DynamicRotationComposite.nullSingleton();
	protected GraphicComposite graphicsComposite = GraphicComposite.nullGraphicsComposite(); 
	protected Collider colliderComposite = ColliderNull.nullColliderComposite();
	protected AngularComposite angularComposite = AngularComposite.getFixedAngleSingleton();
	protected Rigidbody rigidbodyComposite = Rigidbody.nullSingleton();
	
	protected ParentComposite parentComposite = ParentComposite.nullParentComposite();
	protected ChildComposite childComposite = ChildComposite.nullChildComposite();
	
	protected ArrayList<UpdateableComposite> updateablesList = new ArrayList<UpdateableComposite>();
	protected ArrayList<Integer> modifyUpdateablesList = new ArrayList<Integer>();
	

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
		
//		updateablesList.remove(index);
//		for ( int i = index ; i < updateablesList.size() ; i++){
//			updateablesList.get(i).decrementIndex();
//		}
		//System.out.println("REMOVE UPDATEABLES "+updateablesList.size());
		modifyUpdateablesList.add( index );
		
		
	}
	
	public void addUpdateableEntityToUpdater( BoardAbstract board ){
		if ( this.updaterSlot.isActive() ){
			
			//System.err.println( "EntityStatic: ["+this+"] is already in the updater list");
		}
		else{
			this.updaterSlot = board.addEntityToUpdater(this);
		}
	}
	
	public void removeUpdateableEntityFromUpdater(){
		this.updaterSlot.removeSelfFromList();
		this.updaterSlot = nullTicket;
	}
	
	public int getNumberofUpdateables(){
		return updateablesList.size();
	}
	
	/**BE ADVISED: WHEN OVERRIDING IN A SUBCLASS, ALWAYS CALL super.updateComposite() which contains core composite updater functionality.
	 * 
	 */
	public void updateEntity() {
		
		for ( UpdateableComposite composite : updateablesList ){
			composite.updateEntityWithComposite(this);
		}
	}
	
	public void updateComposites(){
		
		for ( UpdateableComposite composite : updateablesList ){
			composite.updateComposite();
		}
		
//		for ( int index : modifyUpdateablesList ){
//			updateablesList.remove(index);
//			for ( int i = index ; i < updateablesList.size() ; ++i ){
//				updateablesList.get(index).decrementIndex();
//			}
//		}
		
		for( Iterator<Integer> itr = modifyUpdateablesList.iterator(); itr.hasNext(); ){
			
			int index = itr.next();  
			
			//System.out.println("REMOVING "+index+" from "+updateablesList.size());
			
			updateablesList.remove(index);
			//System.out.println("REMOVED UPDATEABLE "+updateablesList.size());
			//for ( int i = index ; i < updateablesList.size() ; ++i ){
			//	updateablesList.get(i).decrementIndex();
			//}
			itr.remove();
			
			if ( updateablesList.size() == 0 ){
				//System.out.println("Updateables emptied ");
				this.removeUpdateableEntityFromUpdater();
			}

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
	
	
	protected void setParentComposite( ParentComposite parentComposite ){
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

	public TranslationComposite addTranslationComposite(){
		return CompositeFactory.addTranslationTo(this);
	}
	
	public Collider addInitialColliderTo( Boundary bounds ){ //FIXME AUTOMATICALLY CHECK IF ENTITIY IS ADDED
		return CompositeFactory.addInitialColliderTo(this, bounds);
	}
	
	public Collider addColliderTo( Boundary bounds, BoardAbstract board ){
		return CompositeFactory.addColliderTo(this, bounds, board);
	}
	
	public Collider addUltralightColliderTo( int radius, BoardAbstract board ){
		return CompositeFactory.addUltralightColliderTo( this, radius , board);
	}
	
	public Collider addRotationalColliderTo( AngularComposite angularComposite , Boundary bounds ){
		return CompositeFactory.addRotationalColliderTo(this, bounds, angularComposite );
	}
	
	public DynamicRotationComposite addDynamicRotationTo(){
		return CompositeFactory.addDynamicRotationTo(this);
	}
	
	public GraphicComposite addGraphicTo( Sprite sprite, boolean isRotateable ){
		return CompositeFactory.addGraphicTo(this,sprite, isRotateable);
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
		setInternalPosition(x,y);
		this.parentComposite.notifyPositionChange( x, y);
	}
	public void setCompositedPos( Point p ){
		setInternalPosition(p.getX(),p.getY());
		this.parentComposite.notifyPositionChange( x, y);
	}
	
	/* #########################################################################################################################
	 *		CONVENIENCE COMPOSITE METHODS
	 * #########################################################################################################################
	 */
	
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
	
	public Vector getOrientationVector(){
		return this.angularComposite.getOrientationVector();
	}
	
	/* #########################################################################################################################
	 *		POSITION SETTING
	 * #########################################################################################################################
	 */

	protected void setInternalPosition(double x, double y){
		this.x = x;
    	this.y = y;
	}
	
    /*@Override
    public void setX(double setx) {
        x = setx;
        position.setLocation(setx,position.getY());
        
        this.getParentComposite().notifyPositionChange(x, y);
    }
    @Override
    public void setY(double sety) {
        y = sety;
        position.setLocation(position.getX(),sety);
        
        this.getParentComposite().notifyPositionChange(x, y);
    }*/
    @Override
    public void setPos(Point p){
    	setInternalPosition( p.getX(), p.getY() );
    	position.setLocation(x,y);
        
        this.getParentComposite().notifyPositionChange(x, y);
    }
    @Override
    public void setPos( int x, int y){
    	setInternalPosition( x, y );
    	position.setLocation(x,y);
        
        this.getParentComposite().notifyPositionChange(this.x, this.y);
    }
    @Override
    public void setPos( double x, double y){
    	setInternalPosition( x, y );
        position.setLocation(x,y);
        
        this.getParentComposite().notifyPositionChange(x, y);
    }
    @Override
	public void setPos(Point2D p) {
    	setInternalPosition( p.getX(), p.getY() );
        position.setLocation(x,y);
        
        this.getParentComposite().notifyPositionChange(x, y);
	}
    
    // BELOW ARE RAW POSITION SETTER METHODS THAT DO NOT NOTIFY CHILDREN
    
    /**Sets this entity's position and notifies any parent of change.
     * @param p */
    public void rawSetPosititon(Point p){
    	this.x = p.getX();
    	this.x = p.getY();
    }
    public void rawSetPosititon(Point2D p){
    	this.x = p.getX();
    	this.x = p.getY();
    }
    public void rawSetPosititon(double x, double y){
    	this.x = x;
    	this.x = y;
    }
    public void rawSetX(double x){ this.x = x; }
    public void rawSetY(double y){ this.y = y; }
    
    
	/* #########################################################################################################################
	 *		GARBAGE METHODS
	 * #########################################################################################################################
	 */
	
	@Deprecated
    public void loadSprite(String path){ // needs handling if failed. Also needs to be moved out of object class into sprites

		CompositeFactory.addGraphicTo( this , new Sprite.Stillframe( path ), false );
    	
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
	
	
	
	public void disable(){
		System.out.println("DISABLING "+this);
		
		removeUpdateableEntityFromUpdater();
		
		this.graphicsComposite.disableComposite();
		
		this.colliderComposite.disableComposite();
		
		this.translationComposite.disableComposite();
		
		this.angularComposite.disableComposite(); 
		
		this.rigidbodyComposite.disableComposite();
		
		ArrayList<UpdateableComposite> removals = new ArrayList<UpdateableComposite>();
		
		for ( UpdateableComposite comp : this.updateablesList){
			removals.add(comp);
		}
		for ( UpdateableComposite remove : removals ){
			System.out.print("REMOVING "+remove+": ");
			if ( !remove.removeThisUpdateableComposite() ){ //Remove math calculations from updater thread
				System.err.println("FAILED");
			}else{
				System.out.println("Removed");
			}
		}
		
		this.parentComposite.disableComposite();
		this.childComposite.disableComposite();
		
		this.updateablesList.clear();
		
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
	public void debugListChildren(){
		this.parentComposite.debugPrintChildren();
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
	
	public Point2D.Double getAbsoluteDoublePositionOf(Point2D relativePoint ){
		
		Point returnPoint = this.angularComposite.getRotationalAbsolutePositionOf(relativePoint);
		
		returnPoint = this.getTranslationalAbsolutePositionOf( returnPoint );
		
		return new Point2D.Double(
				returnPoint.getX(),
				returnPoint.getY()
				);
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
