package entityComposites;

import engine.MovingCamera;
import engine.ReferenceFrame;
import sprites.*;
import utility.ListNodeTicket;

public abstract class GraphicComposite implements EntityComposite{

	private static final GraphicComposite.Null nullSingleton = new Null();
	
	public abstract Sprite getSprite();
	public abstract void setSprite(Sprite sprite);
	public abstract void setGraphicSizeFactor(double factor);
	public abstract void draw( ReferenceFrame camera );
	
	public abstract void setGraphicSizeFactorX( double d );
	public abstract void setGraphicSizeFactorY( double d );
	
	public abstract void setGraphicAngle( double d );
	
	public abstract double getGraphicsSizeX();
	public abstract double getGraphicsSizeY();
	public abstract double getGraphicAngle();
	
	public static GraphicComposite.Null nullSingleton(){
		return nullSingleton;
	}
	
	public static class Active extends GraphicComposite{
		protected String compositeName = "GraphicComposite";
		EntityStatic ownerEntity;
		protected Sprite currentSprite = Sprite.missingSprite;
		private ListNodeTicket rendererSlot;
		
		private double graphicSizePercentX = 1;
		private double graphicSizePercentY = 1;
		private double graphicAngle = 0;
		
		protected Active( Sprite current , EntityStatic ownerEntity ){
			this.ownerEntity = ownerEntity;
			this.currentSprite = current;
		}
		
		protected Active( EntityStatic ownerEntity ){
			this.ownerEntity = ownerEntity;
		}
	
		public EntityStatic ownerEntity(){
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
		
		public double getGraphicAngle(){
			return this.graphicAngle;
		}
		
		public void draw( ReferenceFrame camera ){
			this.currentSprite.draw(camera, this);
		}
		
		public void addCompositeToRenderer( RenderingEngine engine ){
			rendererSlot = engine.addSpriteComposite( this );
		}
	
		@Override
		public boolean exists() {
			return true;
		}
		
		@Override
		public void disableComposite() {
			//more disabling 
			System.out.println("Removing graphics from renderer");
			rendererSlot.removeSelfFromList();
		}
		@Override
		public void setCompositeName(String newName) {
			this.compositeName = newName;
		}
		@Override
		public String getCompositeName() {
			return this.compositeName;		
		}
		@Override
		public String toString() {
			return this.getClass().getSimpleName();
		}
	}
	
	private static class Null extends GraphicComposite{
		protected String compositeName = "GraphicCompositeNull";

		//constructor
		private Null() {
			//this.currentSprite = new SpriteStillframe( new MissingIcon().paintMissingSprite() );//new MissingIcon().paintMissingSprite();
			//this.currentSprite = new SpriteStillframe("missing");
		}
		
		public EntityStatic ownerEntity(){
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
		public String toString() {
			//FIXME come back to later to decide if this should return the current string, or getClass().getSimpleName()
			return "Null Graphics Composite Singleton";
		}

		@Override
		public void setCompositeName(String newName) {
			// TODO Auto-generated method stub
		}

		@Override
		public String getCompositeName() {
			return "Null Graphics Singleton";
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
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setGraphicAngle(double d) {
			System.err.println("Warning: Attempign to set ANgle of null graphics singleton");
		}
	}
	
}
