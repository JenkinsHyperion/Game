package sprites;

import java.awt.Graphics;
import java.util.ArrayList;

import engine.Camera;
import entities.EntityPhysics;
import entities.EntityStatic;

public class RenderingLayer {

	private final double PARALLAX_X; // 1 = background moves as fast as entities, 100 = background nearly static relative to camera 
	 // negative = parallax moves in opposite direction, 0 = divide by zero error so can you just not
	private final double PARALLAX_Y;
	
	protected ArrayList<EntityStatic> entitiesList = new ArrayList<>(); 
	
	public RenderingLayer( double parallax_x , double parallax_y ) {
		PARALLAX_X = parallax_x;
		PARALLAX_Y = parallax_y;
	}
	
	public void addEntity( EntityStatic entity ){
		entitiesList.add(entity);
	}
	
	public void drawLayer( Graphics g , Camera camera ){ 
		
		for ( EntityStatic entity : entitiesList  ){ 

			camera.draw( entity.getEntitySprite().getImage() , g , 
					(int)( entity.getX() - camera.getRelativeX( camera.getX()/PARALLAX_X ) ), 
					(int)( entity.getY() - camera.getRelativeY( camera.getY()/PARALLAX_Y ) )
			);

		}
			
	}
	
}
