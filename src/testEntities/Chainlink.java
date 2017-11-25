package testEntities;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

import com.sun.javafx.geom.transform.GeneralTransform3D;

import engine.ReferenceFrame;
import entityComposites.CompositeFactory;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import physics.Boundary;
import physics.Vector;
import sprites.Sprite;
import engine.MovingCamera;

public class Chainlink extends EntityStatic{

	private Point2D.Double[] relativeEndpoints;

	private final int LINK_LENGTH;
	private final int LINK_AMOUNT;
	
	public Chainlink( int linkAmount, int linkLength ) {
		super(0, 0);
		LINK_LENGTH = linkLength;
		LINK_AMOUNT = linkAmount;
		init();
	}
	
	public Chainlink( String name, Point p, int linkAmount, int linkLength) {
		super( name , p.x, p.y);
		LINK_AMOUNT = linkAmount;
		LINK_LENGTH = linkLength;
		init();
	}
	
	private void init(){

		relativeEndpoints = new Point2D.Double[LINK_AMOUNT];
		for ( int i = 0 ; i < relativeEndpoints.length ; ++i){
			relativeEndpoints[i] = new Point2D.Double(LINK_LENGTH,0);
		}
		//this.addAngularComposite();
		CompositeFactory.addAnonymousGraphicTo(this, new GraphicComposite.Static(this){
			
			@Override
			public void draw(ReferenceFrame camera) {
				
				Point2D origin = getPosition();											//start drawing at entity position
				Point2D endpoint = getTranslationalAbsolutePositionOf(relativeEndpoints[0]); //draw to first relative end-point
				
				for ( int i = 1 ; i < relativeEndpoints.length ; ++i ){
				
					camera.drawLine( origin, endpoint );
					
					origin.setLocation(endpoint); // start next draw cycle at this end-point
					
					endpoint.setLocation( // set next end-point, accumulating all relative offsets
							endpoint.getX() + relativeEndpoints[i].getX(),
							endpoint.getY() + relativeEndpoints[i].getY()
							);

				}

			}
		});
	}

	//Overriding setPosition method in EntityStatic, does normal movement but catches the old Position first to be usec
	// to calculate the delta position without needing a translation composite.
	@Override
	protected void setInternalPosition(double x, double y) { 
		
		double oldX = this.x; //get old attachment point position before updating, used to calculate delta position below
		double oldY = this.y; //in this case attachment point is just the this Chainlink entity's position

		super.setInternalPosition(x, y); //set new position of attachment point as usual

		double dx = this.x - oldX; // calculate delta position, how far the attachment point has moved since last frame
		double dy = this.y - oldY; 
		
		Point2D.Double oldEndpoint = relativeEndpoints[0]; //store old end-point position
		
		relativeEndpoints[0] = calculateEndpoint(oldEndpoint, dx, dy ); //calculate new end-point position with delta position

		for ( int i = 1 ; i < relativeEndpoints.length ; ++i ){ //do the same over all subsequent links
		
			dx = relativeEndpoints[i-1].getX() - oldEndpoint.getX() + dx; //
			dy = relativeEndpoints[i-1].getY() - oldEndpoint.getY() + dy; //all previous deltas are accumulated (might be causing the rigidness)
	
			oldEndpoint = relativeEndpoints[i]; //store old end-point position
			
			relativeEndpoints[i] = calculateEndpoint(oldEndpoint, dx, dy );//calculate new end-point position, rinse and repeat
		}
		
	}

	private Point2D.Double calculateEndpoint(Point2D oldEndpoint, double dx, double dy ){ //Actual individual link math
		
		//FIXME add smoothing algorithm to this
		
		Vector localArmVector = new Vector( new Point2D.Double(dx,dy) , oldEndpoint ); 
		
		return localArmVector.ofMagnitude(LINK_LENGTH).toPointDouble();
	}

	
}
