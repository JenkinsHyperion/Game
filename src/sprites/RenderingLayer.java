package sprites;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import engine.ReferenceFrame;
import engine.ParallaxFrame;
import engine.BoardAbstract;
import engine.MovingCamera;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import entityComposites.GraphicComposite.Active;

public class RenderingLayer {

	private final double PARALLAX_X; // 1 = background moves as fast as entities, 100 = background nearly static relative to camera 
	 // negative = parallax moves in opposite direction, 0 = divide by zero error so can you just not
	private final double PARALLAX_Y;
	
	private final double ZOOM_SCALE;
	
	private MovingCamera camera1;
	private final ParallaxFrame parralaxFrame = new ParallaxFrame();
	
	protected ArrayList<GraphicComposite.Active> entitiesList = new ArrayList<>(); 
	
	public RenderingLayer( double parallax_x , double parallax_y , double zoomScale, MovingCamera camera) {
		PARALLAX_X = parallax_x;
		PARALLAX_Y = parallax_y;
		ZOOM_SCALE = zoomScale;
		this.camera1 = camera;
	}
	
	public void addGraphicToLayer( EntityStatic entity ){
		this.entitiesList.add( (Active) entity.getGraphicComposite() ); //TODO REMOVAL INDEXING
	}

	public void renderLayer( MovingCamera camera ){ 
		
		parralaxFrame.setPosition( (int) ( camera1.getX()/PARALLAX_X ), 
				(int) ( camera1.getY()/PARALLAX_Y ), 
				camera1.getGraphics(), 
				camera1.getObserver()
				);
		
		for ( GraphicComposite.Active comp : entitiesList  ){ //FIXME Redo this entire class

			//comp.draw(cam);		
			AffineTransform frameTransform = new AffineTransform();
			
			frameTransform.translate( BoardAbstract.B_WIDTH/2 ,  BoardAbstract.B_HEIGHT/2 );
			
			/*frameTransform.translate( 
					camera.getRelativeX( comp.ownerEntity().getX())   , 
					camera.getRelativeY( comp.ownerEntity().getY()) );*/

			frameTransform.scale(1+(ZOOM_SCALE*camera.getZoom()), 1+(ZOOM_SCALE*camera.getZoom()));
			
			frameTransform.translate(-parralaxFrame.getOriginX()*ZOOM_SCALE , -parralaxFrame.getOriginY()*ZOOM_SCALE );
			
			frameTransform.translate( comp.getSprite().getOffsetX() , comp.getSprite().getOffsetY());
			
			parralaxFrame.drawOnCamera(comp, frameTransform);
			
			//comp.draw(parralaxFrame);
		}
			
	}
	
	
	
}
