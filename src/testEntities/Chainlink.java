package testEntities;

import java.awt.Point;
import java.awt.geom.Point2D;

import engine.ReferenceFrame;
import entityComposites.CompositeFactory;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import physics.Vector;
import sprites.Sprite;

public class Chainlink extends EntityStatic{
	
	private Point[] hinges;
	
	
	public Chainlink() {
		super(0, 0);
		
		init();
	}
	
	public Chainlink( String name, Point p) {
		super( name , p.x, p.y);
		
		init();
	}
	
	private void init(){
		
		hinges = new Point[]{new Point(0,0), new Point(100,0)};
		this.addAngularComposite();
		CompositeFactory.addAnonymousGraphicTo(this, new GraphicComposite.Rotateable(this){
			
			@Override
			public void draw(ReferenceFrame camera) {
				
				for ( int i = 0 ; i < hinges.length ; ++i ){
					camera.drawLine( 
							getAbsolutePositionOf( hinges[i] ) , 
							getAbsolutePositionOf( hinges[ (i+1)%hinges.length ] ) );
				}
			}
		});
	}
	
	@Override
	protected void setInternalPosition(double x, double y) {
		
		double oldX = x;
		double oldY = y;
		super.setInternalPosition(x, y);
		
		Vector pointVelocity = new Vector(x-oldX,y-oldY);
		
		
	}
	
}
