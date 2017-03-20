package entityComposites;

public class GraphicCompositeNull extends GraphicComposite{
	
	private final static GraphicCompositeNull nullSprite = new GraphicCompositeNull();
	
	//constructor
	private GraphicCompositeNull() {
		super( null );
	}
	
	//OPTIMIZATION - Look into better handling, this is a static factory that returns the static singleton nullSprite, which
	//apparently is difficult to substitute test without breaking everything
	public static GraphicComposite getNullSprite(){ 
		return nullSprite;
	}
	
	
	
	
}
