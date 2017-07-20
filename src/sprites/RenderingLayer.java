package sprites;

import java.awt.Point;
import java.util.ArrayList;

import engine.ReferenceFrame;
import engine.ParallaxFrame;
import engine.MovingCamera;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import entityComposites.GraphicComposite.Active;

public class RenderingLayer {

	private final double PARALLAX_X; // 1 = background moves as fast as entities, 100 = background nearly static relative to camera 
	 // negative = parallax moves in opposite direction, 0 = divide by zero error so can you just not
	private final double PARALLAX_Y;
	
	private MovingCamera camera1;
	private final ParallaxFrame cam = new ParallaxFrame();
	
	protected ArrayList<GraphicComposite.Active> entitiesList = new ArrayList<>(); 
	
	public RenderingLayer( double parallax_x , double parallax_y , MovingCamera camera) {
		PARALLAX_X = parallax_x;
		PARALLAX_Y = parallax_y;
		this.camera1 = camera;
	}
	
	public void addGraphicToLayer( EntityStatic entity ){
		this.entitiesList.add( (Active) entity.getGraphicComposite() ); //TODO REMOVAL INDEXING
	}

	public void renderLayer( ReferenceFrame camera ){ 
		
		cam.setPosition( (int) ( camera1.getX()/PARALLAX_X ), 
				(int) ( camera1.getY()/PARALLAX_Y ), 
				camera1.getGraphics(), 
				camera1.getObserver()
				);
		
		for ( GraphicComposite.Active comp : entitiesList  ){ //FIXME Redo this entire class

			comp.getSprite().draw(cam, comp);

		}
			
	}
	
	
	
}
