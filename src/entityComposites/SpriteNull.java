package entityComposites;

public class SpriteNull extends SpriteProperty{
	
	private final static SpriteNull nullSprite = new SpriteNull();
	
	//constructor
	private SpriteNull() {
		
	}
	
	//OPTIMIZATION - Look into better handling, this is a static factory that returns the static singleton nullSprite, which
	//apparently is difficult to substitute test without breaking everything
	public static SpriteNull getNullSprite(){ 
		return nullSprite;
	}
	
	
	
	
}
