package entityComposites;

import entities.EntityStatic;
import sprites.*;

public class SpriteComposite extends SpriteProperty{
	
	Sprite currentSprite;
	
	public SpriteComposite( Sprite current , EntityStatic ownerEntity ){
		
		this.currentSprite = current;
	}
	
	public Sprite getSprite(){
		return currentSprite;
	}
	
	public void setSprite(Sprite sprite){
		this.currentSprite = sprite;
	}
	
}
