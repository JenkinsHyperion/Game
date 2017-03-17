package entityComposites;

import entities.EntityStatic;
import sprites.*;
import utility.Ticket;

public class SpriteComposite extends SpriteProperty{
	
	Sprite currentSprite;
	private Ticket rendererSlot;
	
	public SpriteComposite( Sprite current , EntityStatic ownerEntity ){
		
		this.currentSprite = current;
	}
	
	public Sprite getSprite(){
		return currentSprite;
	}
	
	public void setSprite(Sprite sprite){
		this.currentSprite = sprite;
	}
	
	
	public void addCompositeToRenderer( RenderingEngine engine ){
		rendererSlot = engine.addSpriteComposite( this );
	}
	
}
