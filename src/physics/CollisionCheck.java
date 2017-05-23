package physics;


import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import entityComposites.*;

public abstract class CollisionCheck {
	
	public static CollisionCheck SAT = new SAT();
	
	protected CollisionCheck(){};
	
	public abstract boolean check( Collider collidablePrimary , Collider collidableSecondary );
	
	public static CollisionCheck SAT(){
		
		return SAT;
	}

	private static class SAT extends CollisionCheck{
		@Override
		public boolean check( Collider collidablePrimary , Collider collidableSecondary ) {
		        
			
			Boundary statBounds = collidableSecondary.getBoundaryLocal() ;
		    Boundary playerBounds = collidablePrimary.getBoundaryLocal();
		    
		    Point2D playerCenter = new Point2D.Double(collidablePrimary.getOwnerEntity().getX(), collidablePrimary.getOwnerEntity().getY());
		    Point2D statCenter = new Point2D.Double(collidableSecondary.getOwnerEntity().getX(), collidableSecondary.getOwnerEntity().getY());
	
	
		   Line2D[] separatingSides = Boundary.getSeparatingSidesBetween(playerBounds,statBounds);
		    for ( int i = 0 ; i < separatingSides.length ; i++ ){
		    	
		    	Line2D axis = BoundaryPolygonal.getSeparatingAxis( separatingSides[i] );
		    	
	
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
		    			Boundary.getProjectionPoint(playerCenter,axis) ,
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
}
