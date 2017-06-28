package entityComposites;

import engine.ReferenceFrame;
import sprites.*;
import utility.ListNodeTicket;

public class GraphicComposite implements EntityComposite{
	protected String compositeName = "GraphicComposite";
	EntityStatic ownerEntity;
	Sprite currentSprite = Sprite.missingSprite;
	private ListNodeTicket rendererSlot;
	
	private double graphicSizePercent = 1;
	private double graphicAngle = 0;
	
	protected GraphicComposite( Sprite current , EntityStatic ownerEntity ){
		this.ownerEntity = ownerEntity;
		this.currentSprite = current;
	}
	
	protected GraphicComposite( EntityStatic ownerEntity ){
		this.ownerEntity = ownerEntity;
	}

	public EntityStatic ownerEntity(){
		return this.ownerEntity;
	}
	
	public Sprite getSprite(){
		return currentSprite;
	}
	
	public void setSprite(Sprite sprite){
		this.currentSprite = sprite;
	}
	
	public void setGraphicSizeFactor(double factor){
		this.graphicSizePercent = factor;
	}
	
	public double getGraphicsSize(){
		return this.graphicSizePercent;
	}
	
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
	public void disable() {
		//more disabling 
		System.out.println("Removing graphics from renderer");
		rendererSlot.removeSelf();
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
