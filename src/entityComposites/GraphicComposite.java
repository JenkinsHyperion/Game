package entityComposites;

import entities.EntityStatic;
import sprites.*;
import utility.Ticket;

public class GraphicComposite{
	
	EntityStatic ownerEntity;
	Sprite currentSprite;
	private Ticket rendererSlot;
	
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
	
}
