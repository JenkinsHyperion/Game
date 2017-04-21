package physics;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import engine.Camera;
import engine.MovingCamera;
import entityComposites.*;
import sprites.RenderingEngine;

public class VisualCollisionCheck {
	
	public static VisualCollisionCheck SAT = new VisualCollisionCheck();
	
	private VisualCollisionCheck(  ){
		
	}
	
	public static VisualCollisionCheck SAT(){
		return SAT;
	}
	
	
	public boolean check( Collider collidablePrimary , Collider collidableSecondary , MovingCamera camera , Graphics2D g2 ) {
		boolean isColliding = true;
		Boundary statBounds = collidableSecondary.getBoundaryLocal();
	    Boundary playerBounds = collidablePrimary.getBoundaryDelta();
	    
	    /*for ( Side side : ((BoundaryPolygonal)statBounds).sides ){
	    	VoronoiRegion test = VoronoiRegion.getVoronoiRegion(side);
	    	test.debugDrawRegion( collidablePrimary.getOwnerEntity() , camera, g2);
	    }*/
	    ((BoundaryPolygonal)statBounds).drawVoronoiRegions(collidablePrimary.getOwnerEntity(),camera,g2);

	    
	    Point2D playerCenter = new Point2D.Double(collidablePrimary.getOwnerEntity().getX(), collidablePrimary.getOwnerEntity().getY());
	    Point2D statCenter = new Point2D.Double(collidableSecondary.getOwnerEntity().getX(), collidableSecondary.getOwnerEntity().getY());


	    //Line2D[] separatingSides = Boundary.getSeparatingSidesBetween(playerBounds,statBounds);
	    Line2D[] separatingSides = playerBounds.getSeparatingSidesBetween(statBounds);
	    
	    g2.setColor(Color.CYAN);
	    g2.drawString("Axes of Separations: "+separatingSides.length, 300, 20);
	    
	    for ( int i = 0 ; i < separatingSides.length ; i++ ){
	    	
	    	Line2D axis = BoundaryPolygonal.getSeparatingAxis( separatingSides[i] );

	    	//g2.setColor(Color.CYAN);
	    	//g2.draw(separatingSides[i]);
	    	
	    	Point2D[] outerPoints = Boundary.getFarthestPointsBetween(playerBounds, statBounds, axis);

	    	//BoundaryVertex[] statOuter= statBounds.getFarthestVertices(playerBounds,axis);
	    	//BoundaryVertex[] playerOuter= playerBounds.getFarthestVertices(statBounds,axis);

	    	Point2D nearStatCorner = statBounds.farthestPointFromPoint( outerPoints[1] , axis ); //merge below
	    	Point2D nearPlayerCorner = playerBounds.farthestPointFromPoint( outerPoints[0] , axis );
	    	
	    	//BoundaryVertex farStatCorner = statBounds.farthestVerticesFromPoint(nearStatCorner[0] , axis)[0];
	    	//BoundaryVertex farPlayerCorner = playerBounds.farthestVerticesFromPoint(nearPlayerCorner[0] , axis)[0];
	    	
	    	//Point2D centerStat = outerPoints[1].getCenter(nearStatCorner[0]);
	    	//Point2D centerPlayer = outerPoints[0].getCenter(nearPlayerCorner[0]);

	    	Point2D centerStat = BoundaryCorner.getCenter(  nearStatCorner , outerPoints[1] );
	    	Point2D centerPlayer = BoundaryCorner.getCenter( nearPlayerCorner , outerPoints[0] );

	    	Line2D centerDistance = new Line2D.Double( centerPlayer , centerStat );
	    	Line2D centerProjection = Boundary.getProjectionLine(centerDistance, axis);

	    	// -----------------

	    	Line2D playerHalf = new Line2D.Float( 
	    			Boundary.getProjectionPoint(centerPlayer,axis) ,
	    			Boundary.getProjectionPoint(nearPlayerCorner,axis)
	    			);
	    	Line2D statHalf = new Line2D.Float( 
	    			Boundary.getProjectionPoint(centerStat,axis) ,
	    			Boundary.getProjectionPoint(nearStatCorner,axis) 
	    			);

	    	int centerDistanceX = (int)(centerProjection.getX1() -  centerProjection.getX2()  );
	    	int centerDistanceY = (int)(centerProjection.getY1() -  centerProjection.getY2()  );

	    	int playerProjectionX = (int)(playerHalf.getX1() -  playerHalf.getX2());
	    	int playerProjectionY = (int)(playerHalf.getY1() -  playerHalf.getY2());

	    	int statProjectionX = (int)(statHalf.getX2() -  statHalf.getX1());
	    	int statProjectionY = (int)(statHalf.getY2() -  statHalf.getY1()); 

	    	int penetrationX = 0;
	    	int penetrationY = 0;  
	    	

	    	if (centerDistanceX>0){
	    		//centerDistanceX -= 1;
	    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX +1;
	    	}
	    	else if (centerDistanceX<0){
	    		//centerDistanceX += 1;  //NEEDS HIGHER LEVEL SOLUTION
	    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX -1;
	    	}
	    	else
	    		penetrationX = Math.abs(playerProjectionX) + Math.abs(statProjectionX);

	    	if (centerDistanceY>0){
	    		//centerDistanceY -= 1;
	    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY+1; 
	    	}
	    	else if (centerDistanceY<0){
	    		//centerDistanceY += 1; 
	    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY-1; 
	    	}else
	    		penetrationY = Math.abs(playerProjectionY) + Math.abs(statProjectionY);


	    	
	    	if ( penetrationX * centerDistanceX < 0 ) //SIGNS ARE NOT THE SAME
				penetrationX = 0;
	    	if ( penetrationY * centerDistanceY < 0 )
				penetrationY = 0;
			
	    	
	    	g2.setColor(Color.DARK_GRAY);
	    	camera.drawDebugAxis(axis , g2 );    	
	    	
	    	if ( penetrationX + penetrationY == 0 ){
	    		//return false;
	    		isColliding = false;
		    	g2.setColor(Color.YELLOW);
	    	}else{
		    	g2.setColor(Color.CYAN);
	    	}
		    	
	    	camera.drawDebugAxis(statHalf , g2 );
	    	
	    	g2.setColor(Color.GREEN);
	    	camera.drawDebugAxis(playerHalf , g2 );
	    	
	    	camera.drawString( "   Depth: "+penetrationX+","+penetrationY , playerHalf.getP1() , g2);
	    	
	    	camera.drawCrossInWorld(outerPoints[0] , g2);
	    	
	    	//camera.drawCrossInWorld(outerPoints[1] , g2);
	    	
	    	Line2D projCenter = new Line2D.Double( 
	    			playerHalf.getX1() , 
	    			playerHalf.getY1() , 
	    			playerCenter.getX() , 
	    			playerCenter.getY()
	    	);
	    	Line2D projOuter = new Line2D.Double( 
	    			playerHalf.getX2() , 
	    			playerHalf.getY2() , 
	    			nearPlayerCorner.getX() , 
	    			nearPlayerCorner.getY()
	    	);
	    	Line2D projCenterStat = new Line2D.Double( 
	    			statHalf.getX1() , 
	    			statHalf.getY1() , 
	    			centerStat.getX() , 
	    			centerStat.getY()
	    	);
	    	Line2D projOuterStat = new Line2D.Double( 
	    			statHalf.getX2() , 
	    			statHalf.getY2() , 
	    			nearStatCorner.getX() , 
	    			nearStatCorner.getY()
	    	);
	    	
	    	g2.setColor(Color.DARK_GRAY);
	    	camera.drawDebugAxis(projCenter, g2);
	    	camera.drawDebugAxis(projOuter, g2);
	    	camera.drawDebugAxis(projCenterStat, g2);
	    	camera.drawDebugAxis(projOuterStat, g2);
	
	    }
	    return isColliding;
	    //return false;
	    
	}
}
