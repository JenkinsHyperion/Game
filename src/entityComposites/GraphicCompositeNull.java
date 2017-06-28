package entityComposites;

import editing.MissingIcon;
import sprites.RenderingEngine;
import sprites.Sprite;
import sprites.SpriteStillframe;

public class GraphicCompositeNull extends GraphicComposite{
	protected String compositeName = "GraphicCompositeNull";
	private final static GraphicCompositeNull nullSprite = new GraphicCompositeNull();
	
	//constructor
	private GraphicCompositeNull() {
		super( null );
		//this.currentSprite = new SpriteStillframe( new MissingIcon().paintMissingSprite() );//new MissingIcon().paintMissingSprite();
		this.currentSprite = new SpriteStillframe("missing");
	}
	
	//OPTIMIZATION - Look into better handling, this is a static factory that returns the static singleton nullSprite, which
	//apparently is difficult to substitute test without breaking everything
	public static GraphicComposite getNullSprite(){ 
		return nullSprite;
	}
	
	public EntityStatic ownerEntity(){
		return null;
	}
	
	public Sprite getSprite(){
		System.err.println("Attempting to get sprite on null graphics composite");
		return currentSprite;
	}
	
	public void setSprite(Sprite sprite){
		System.err.println("Attempting to set sprite on null graphics composite");
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
	public void disable() {
		System.err.println("No graphics to disable");
	}
	
	@Override
	public String toString() {
		//FIXME come back to later to decide if this should return the current string, or getClass().getSimpleName()
		return "Null Graphics Composite Singleton";
	}
}
