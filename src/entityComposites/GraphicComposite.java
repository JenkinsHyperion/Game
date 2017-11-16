package entityComposites;

import engine.MovingCamera;
import engine.ReferenceFrame;
import sprites.*;
import sprites.RenderingEngine.ActiveGraphic;
import utility.ListNodeTicket;

public abstract class GraphicComposite implements EntityComposite{

	private static final GraphicComposite.Null nullSingleton = new Null();
	
	public abstract Sprite getSprite();
	public abstract void setSprite(Sprite sprite);
	public abstract void setGraphicSizeFactor(double factor);
	public abstract void draw( ReferenceFrame camera );
	
	public abstract void setGraphicSizeFactorX( double d );
	public abstract void setGraphicSizeFactorY( double d );

	protected abstract void notifyAngleChangeFromAngularComposite( double angle);

	public abstract void setGraphicAngle( double d );
	
	public abstract double getGraphicsSizeX();
	public abstract double getGraphicsSizeY();
	public abstract double getGraphicAngle();
	
	public abstract void deactivateGraphic();
	public abstract void activateGraphic();
	
	public static GraphicComposite.Null nullGraphicsComposite(){
		return nullSingleton;
	}
	
	public static class Static extends GraphicComposite{
		protected String compositeName;
		EntityStatic ownerEntity;
		protected Sprite currentSprite = Sprite.missingSprite;
		private RenderingEngine.ActiveGraphic rendererSlot;
		
		private double graphicSizePercentX = 1;
		private double graphicSizePercentY = 1;
		private double graphicAngle = 0;
		
		protected Static( Sprite current , EntityStatic ownerEntity ){
			this.ownerEntity = ownerEntity;
			this.currentSprite = current;
			this.compositeName = "Graphics"+this.getClass().getSimpleName();
		}
		
		protected Static( EntityStatic ownerEntity ){
			this.ownerEntity = ownerEntity;
			this.compositeName = "Graphics"+this.getClass().getSimpleName();
		}
	
		public EntityStatic ownerEntity(){
			return getOwnerEntity();
		}
		/** created new method to remove refactoring issues with name inconsistency*/ 
		public EntityStatic getOwnerEntity(){
			return this.ownerEntity;
		}
		@Override
		public Sprite getSprite(){
			return currentSprite;
		}
		@Override
		public void setSprite(Sprite sprite){
			this.currentSprite = sprite;
		}
		@Override
		public void setGraphicSizeFactor(double factor){
			this.graphicSizePercentX = factor;
			this.graphicSizePercentY = factor;
		}
		@Override
		public void setGraphicSizeFactorX(double factorX ){
			this.graphicSizePercentX = factorX;
		}
		@Override
		public void setGraphicSizeFactorY(double factorY ){
			this.graphicSizePercentY = factorY;
		}
		
		public double getGraphicsSizeX(){
			return this.graphicSizePercentX;
		}
		
		public double getGraphicsSizeY(){
			return this.graphicSizePercentY;
		}
		@Override
		public void setGraphicAngle( double angle ){
			this.graphicAngle = angle;
		}
		@Override
		protected void notifyAngleChangeFromAngularComposite( double angle){
			//DO NOTHING
		}
		
		public double getGraphicAngle(){
			return this.graphicAngle;
		}
		
		public void draw( ReferenceFrame camera ){
			this.currentSprite.draw(camera, this);
		}
		
		public void addCompositeToRenderer( RenderingEngine engine ){
			rendererSlot = engine.addGraphicsCompositeToRenderer( this );
		}
	
		@Override
		public boolean exists() {
			return true;
		}
		
		@Override
		public void disableComposite() {
			//more disabling 
			System.out.println("Removing graphics from renderer");
			rendererSlot.deactivateInRenderingEngine();
			rendererSlot = null;
			this.ownerEntity.nullifyGraphicsComposite();
		}

		@Override
		public String toString() {
			return this.compositeName;
		}

		@Override
		public void deactivateGraphic() {
			rendererSlot.deactivateInRenderingEngine();
		}

		@Override
		public void activateGraphic() {
			rendererSlot.activateInRenderingEngine();
		}
	}
	
	public static class Rotateable extends Static{

		protected Rotateable(EntityStatic ownerEntity) {
			super(ownerEntity);
		}
		@Override
		protected void notifyAngleChangeFromAngularComposite( double angle){
			this.setGraphicAngle(angle);
		}
		
	}
	
	private static class Null extends GraphicComposite{
		protected String compositeName;

		//constructor
		private Null() {
			this.compositeName = "GraphicComposite"+this.getClass().getSimpleName();
			//this.currentSprite = new SpriteStillframe( new MissingIcon().paintMissingSprite() );//new MissingIcon().paintMissingSprite();
			//this.currentSprite = new SpriteStillframe("missing");
		}
		
		public EntityStatic ownerEntity(){
			return this.getOwnerEntity();		
		}
		public EntityStatic getOwnerEntity(){
			return null;
		}
		@Override
		public Sprite getSprite(){
			System.err.println("Attempting to get sprite on null graphics composite");
			return Sprite.missingSprite;
		}
		@Override
		public void setSprite(Sprite sprite){
			System.err.println("Attempting to set sprite on null graphics composite");
		}
		@Override
		public void draw(ReferenceFrame camera) {
			System.err.println("Attempting to draw null graphics composite");
		}
		
		@Override
		protected void notifyAngleChangeFromAngularComposite(double angle) {
			//DO NOTHING//
		}
		
		public void addCompositeToRenderer( RenderingEngine engine ){
			System.err.println("Attempting to add null graphics composite to rendering engine");
			//This should catch null composites from getting into the renderer list 
		}
		
		@Override
		public boolean exists() {
			return false;
		}
		
		@Override
		public void disableComposite() {
			System.err.println("No graphics to disable");
		}
		


		@Override
		public void setGraphicSizeFactor(double factor) {
			System.err.println("Attempting to set size factor of null graphics composite");
		}

		@Override
		public double getGraphicsSizeX() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public double getGraphicsSizeY() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public void setGraphicSizeFactorX(double d) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setGraphicSizeFactorY(double d) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public double getGraphicAngle() {
			System.err.println("Warning: Attempign to set ANgle of null graphics singleton");
			return 0;
		}

		@Override
		public void setGraphicAngle(double d) {
			System.err.println("Warning: Attempign to set ANgle of null graphics singleton");
		}
		@Override
		public String toString() {
			//FIXME come back to later to decide if this should return the current string, or getClass().getSimpleName()
			return this.compositeName;
		}

		@Override
		public void deactivateGraphic() {
			System.err.println("Warning: Attemping to deactivate null graphic composite");
		}

		@Override
		public void activateGraphic() {
			// TODO Auto-generated method stub
			
		}
	}
	
}
