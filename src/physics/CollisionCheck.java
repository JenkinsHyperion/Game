package physics;


import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import entityComposites.*;

public class CollisionCheck {
	
	public static CollisionCheck SAT = new CollisionCheck();
	
	private CollisionCheck( ){
		
	}
	
	public static CollisionCheck SAT(){
		
		return SAT;
	}
	
	
	public boolean check( Collider collidablePrimary , Collider collidableSecondary ) {
	        
		
		Boundary statBounds = collidableSecondary.getBoundaryLocal() ;
	    Boundary playerBounds = collidablePrimary.getBoundaryLocal();
	    
	    Point2D playerCenter = new Point2D.Double(collidablePrimary.getOwnerEntity().getX(), collidablePrimary.getOwnerEntity().getY());
	    Point2D statCenter = new Point2D.Double(collidableSecondary.getOwnerEntity().getX(), collidableSecondary.getOwnerEntity().getY());


	    //for ( Line2D axis : bounds.debugSeparatingAxes(B_WIDTH, B_HEIGHT) ){
	    for ( int i = 0 ; i < statBounds.getSpearatingSidesBetween(playerBounds).length ; i++ ){

	    	Line2D side = statBounds.getSpearatingSidesBetween(playerBounds)[i];

	    	Line2D axis = statBounds.getSeparatingAxis(side);


	    	

	    	BoundaryVertex[] statOuter= statBounds.getFarthestVertices(playerBounds,axis);
	    	BoundaryVertex[] playerOuter= playerBounds.getFarthestVertices(statBounds,axis);

	    	BoundaryVertex[] nearStatCorner = statBounds.farthestVerticesFromPoint( statOuter[0] , axis ); //merge below
	    	BoundaryVertex[] nearPlayerCorner = playerBounds.farthestVerticesFromPoint( playerOuter[0] , axis );
	    	
	    	BoundaryVertex farStatCorner = statBounds.farthestVerticesFromPoint(nearStatCorner[0] , axis)[0];
	    	BoundaryVertex farPlayerCorner = playerBounds.farthestVerticesFromPoint(nearPlayerCorner[0] , axis)[0];
	    	
	    	Point2D centerStat = statOuter[0].getCenter(nearStatCorner[0]);
	    	Point2D centerPlayer = playerOuter[0].getCenter(nearPlayerCorner[0]);

	    	Line2D centerDistance = new Line2D.Double( centerPlayer , centerStat );
	    	Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);

	    	// -----------------

	    	Line2D playerHalf = new Line2D.Float( 
	    			playerBounds.getProjectionPoint(playerCenter,axis) ,
	    			playerBounds.getProjectionPoint(nearPlayerCorner[0].toPoint(),axis)
	    			);
	    	Line2D statHalf = new Line2D.Float( 
	    			statBounds.getProjectionPoint(centerStat,axis) ,
	    			statBounds.getProjectionPoint(nearStatCorner[0].toPoint(),axis) 
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
	    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
	    	}
	    	else if (centerDistanceX<0){
	    		//centerDistanceX += 1;  //NEEDS HIGHER LEVEL SOLUTION
	    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
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
			
	    	if ( penetrationX + penetrationY == 0 ){
	    		return false;
	    	}
	
	    }
	    
	    return true;
	    
	}
}
