package entityComposites;

import sprites.*;
import utility.ListNodeTicket;

public class GraphicComposite implements EntityComposite{
	
	EntityStatic ownerEntity;
	Sprite currentSprite;
	private ListNodeTicket rendererSlot;
	
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
		this.currentSprite.setOwner(this);
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
	
}
