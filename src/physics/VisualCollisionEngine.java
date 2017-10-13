package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import engine.BoardAbstract;
import engine.ReferenceFrame;
import engine.MovingCamera;
import engine.Overlay;
import engine.OverlayComposite;
import entityComposites.Collider;
import entityComposites.EntityStatic;
import sprites.RenderingEngine;
import utility.DoubleLinkedList;
import utility.ListNodeTicket;

public class VisualCollisionEngine extends CollisionEngine implements Overlay{

	private RenderingEngine renderer;
	private Graphics2D gOverlay;
	private MovingCamera camera;
	
	private final Runnable drawingState = new DrawState();
	private final Runnable hiddenState = new HiddenState();
	private Runnable currentDrawingState = hiddenState;
	
	private OverlayComposite overlayComposite;
	
	private ArrayList<Line2D> linesList = new ArrayList<Line2D>();
	
	public VisualCollisionEngine(BoardAbstract testBoard , RenderingEngine renderer) {
		super(testBoard);

		this.renderer = renderer;
		this.overlayComposite = renderer.addOverlay(this);
		
		this.gOverlay = renderer.debugGetOverlayGraphics();

	}
	
	public void toggleCalculationDisplay(){
		if ( currentDrawingState == hiddenState){
			currentDrawingState = drawingState;
		}
		else{
			currentDrawingState = hiddenState;
			gOverlay.dispose();
			gOverlay = renderer.debugGetOverlayGraphics();
			camera = renderer.getCamera();
		}
	}
	
	@Override
	public void checkCollisions() { 
    	
		this.currentDrawingState.run();   
        
    }
	
	private class HiddenState implements Runnable{
		@Override
		public void run() {
			while ( activeCheckingPairs.hasNext() ){
				activeCheckingPairs.get().check();
			}
	    	updateCollisions();    
		}
	}
	
	private class DrawState implements Runnable{
		@Override
		public void run() {
			gOverlay.dispose();
			gOverlay = renderer.debugGetOverlayGraphics();
			camera = renderer.getCamera();
			
			while ( activeCheckingPairs.hasNext() ){
				activeCheckingPairs.get().visualCheck( camera , gOverlay);
			}
			updateVisualCollisions();    
		}
	}
	
	private void updateVisualCollisions(){
    	
	    for ( int i = 0 ; i < runningCollisionsList.size() ; i++ ){
	    		
	    	//if collision is complete, remove from active list
	    	if (!runningCollisionsList.get(i).isComplete() ) {
	    		runningCollisionsList.get(i).updateVisualCollision( camera , gOverlay ); //Run commands from inside collision object
	    		
	    	}
	    	else {
	    		runningCollisionsList.get(i).completeCollision();
	    		runningCollisionsList.get(i).notifyEntitiesOfCollisionCompleteion();
	    		runningCollisionsList.remove(i);	
    		}	
    	}	
    }
	
	@Override
	public void registerDynamicStaticCollision( boolean bool , Collider collidable1 , Collider collidable2 , CollisionCheck checkType){
    	
    	if ( bool ) { 
		 //check to see if collision isn't already occurring
    		if (!hasActiveCollision(collidable1.getOwnerEntity(),collidable2.getOwnerEntity())) { 
			// if not, add new collision event
			//int index = currentBoard.getStaticEntities().size() + 1 ;
    			//System.out.println( "Collision detected" );
    			runningCollisionsList.add(new CollisionRigidDynamicStatic( 
    					collidable1 , collidable2 , 
    					((VisualCollisionCheck)checkType).getCollector()
    					)); 
			} 	
    	}
    	//else System.out.println("TEST");
    	
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
	
	public Overlay createForcesOverlay(){
		return new ForcesOverlay();
	}
	
	public class ForcesOverlay implements Overlay{

		@Override
		public void paintOverlay(Graphics2D g2, MovingCamera cam) {
			
			g2.drawString( " VISUAL COLLISION ENGINE OVERLAY ", 20, 20 );
			g2.drawString( " dynamic collider ", 20, 35 );
			g2.drawString( activeCheckingPairs.size() + " active collider pairs", 20, 50 );
			
			g2.drawString( colliderGroups.size() + " groups:", 20, 65 );
			
			int y = 80;

			String[] entitiesInGroup = ungrouped.debugListGroupedStatics();
			
			g2.drawString( "Ungrouped Entities :", 35, y );
			y = y + 15;
			for( String entity : entitiesInGroup ){
				g2.drawString( "-  "+entity+" (static)", 50, y );
				y = y + 15;
			}
			entitiesInGroup = ungrouped.debugListGroupedDynamics();
			
			for( String entity : entitiesInGroup ){
				g2.drawString( "-  "+entity+" (dynamic)", 50, y );
				y = y + 15;
			}
			
			for ( ColliderGroup group : colliderGroups ){
				
				entitiesInGroup = group.debugListGroupedStatics();
				
				g2.drawString( group.toString() + " containing :", 35, y );
				y = y + 15;
				for( String entity : entitiesInGroup ){
					g2.drawString( "-  "+entity+" (static)", 50, y );
					y = y + 15;
				}

				entitiesInGroup = group.debugListGroupedDynamics();

				for( String entity : entitiesInGroup ){
					g2.drawString( "-  "+entity+" (dynamic)", 50, y );
					y = y + 15;
				}
			}
			
			/*for ( GroupPair pair : groupPairs ){
				g2.drawString( "Pair - "+pair.pairedGroupToString(0)+" - "+pair.pairedGroupToString(1)+" - "+pair.debugBuilderToString(), 50, y );
				y = y + 15;
			}*/

			g2.drawString( runningCollisionsList.size() + " current collisions", 20, y );

			/*for ( ActiveCollider dynamic : dynamicCollidables ){

				EntityStatic entity = dynamic.collider.getOwnerEntity();

				for ( Vector line : entity.getTranslationComposite().debugForceArrows() ){

					cam.draw(line.multiply(400).toLine( entity.getPosition() ));
				}
			}*/
			
		}
	}

}
