package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import engine.BoardAbstract;
import engine.ReferenceFrame;
import engine.MovingCamera;
import engine.Overlay;
import entityComposites.Collider;
import sprites.RenderingEngine;
import utility.DoubleLinkedList;
import utility.ListNodeTicket;

public class VisualCollisionEngine extends CollisionEngine implements Overlay{

	private RenderingEngine renderer;
	private Graphics2D gOverlay;
	private MovingCamera camera;
	
	private ArrayList<Line2D> linesList = new ArrayList<Line2D>();
	
	public VisualCollisionEngine(BoardAbstract testBoard , RenderingEngine renderer) {
		super(testBoard);

		this.renderer = renderer;
		renderer.quickAddOverlay(this);
		
		this.gOverlay = renderer.debugGetOverlayGraphics();

	}
	
	@Override
	public void checkCollisions() { 
    	
		this.gOverlay.dispose();
		this.gOverlay = renderer.debugGetOverlayGraphics();
		this.camera = renderer.getCamera();
		
		while ( checkingPairs.hasNext() ){
			checkingPairs.get().visualCheck( camera , gOverlay);
		}

    	updateCollisions();    
        
    }
	
	@Override
	public void registerCollision( boolean bool , Collider collidable1 , Collider collidable2 , CollisionCheck checkType){
    	
    	if ( bool ) { 
		 //check to see if collision isn't already occurring
    		if (!hasActiveCollision(collidable1.getOwnerEntity(),collidable2.getOwnerEntity())) { 
			// if not, add new collision event
			//int index = currentBoard.getStaticEntities().size() + 1 ;
    			//System.out.println( "Collision detected" );
    			collisionsList.add(new VisualCollisionDynamicStatic( 
    					collidable1 , collidable2 , 
    					((VisualCollisionCheck)checkType).getCollector() , 
    					this 
    					)); 
			} 	
    	}
    	//else System.out.println("TEST");
    	
    }
	
	public void draw( Line2D line ){
		linesList.add(line);
	}

	@Override
	public void paintOverlay(Graphics2D g2, MovingCamera cam) {

		/*Line2D[] linesBuffer = new Line2D[linesList.size()];
		linesList.toArray(linesBuffer);
		for ( Line2D line : linesBuffer )
			renderer.getCamera().drawInFrame(line);
		
		linesList.clear();*/
		
		//this.gOverlay.dispose();
		
	}
	
	
	
	
	public Line2D getVisualAxis(Line2D line , Point intersect){
		
		int xMax = this.currentBoard.getWidth();
		int yMax = this.currentBoard.getHeight();
		
		if ( line.getP1().getX() == line.getP2().getX() ) { //line is vertical
				
				return new Line2D.Double( intersect.x , 0 , intersect.x , yMax ) ; //return normal line which is horizontal with slope 0
			}
		else {// line is not vertical, so it has a defined slope and can be in form y=mx+b
	
			//return normal line, whose slope is inverse reciprocal of line.   -(1/slope)
			double m = ( line.getY1() - line.getY2() )/( line.getX1() - line.getX2() );
			int b = (int)( line.getY1() - ( m*line.getX1() ) );
			
			// y = mx + b
			int yStart = (int)( m*-intersect.x + intersect.y  );
			int yEnd = (int)( m*(xMax-intersect.x) + intersect.y );
				
			return new Line2D.Double( 0 , 
				yStart , // mx + b
				xMax, // 
				yEnd
						
			);
			
		}
		
	}

}
