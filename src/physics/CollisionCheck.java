package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import entities.EntityDynamic;
import entities.EntityStatic;
import entityComposites.Collidable;
import physics.Collision.Resolution;

public class CollisionCheck {
	
	public static CollisionCheck SAT = new CollisionCheck();
	
	private CollisionCheck( ){
		
	}
	
	public static CollisionCheck SAT(){
		
		return SAT;
	}
	
	
	public boolean check( Collidable collidablePrimary , Collidable collidableSecondary ) {
	        
		
		
			for ( Line2D separatingSide : collidablePrimary.getBoundary().getSpearatingSidesBetween(collidableSecondary.getBoundary()) ){
				
				Point distanceVector = getDistanceSAT( separatingSide , collidablePrimary , collidableSecondary );
		    	
				//System.out.println(distanceVector.getX() + " " + distanceVector.getY() );
				
		    	if ( distanceVector.getX() == 0 && distanceVector.getY() == 0 ){
		    		
		    		//If ONLY ONE axis is separated, the entity is NOT COLLIDING 
		    		return false;
		    	}
				
			}
		    return true;
	    	
	    }
	
	
	 private Point getDistanceSAT( Line2D separatingSide , Collidable primary , Collidable stat ){
		    
		    
		    Boundary statBounds = stat.getBoundaryLocal() ;
		    Boundary playerBounds = primary.getBoundaryDelta();
		    
		    double deltaX = primary.getOwnerEntity().getDeltaX() ;
		    double deltaY = primary.getOwnerEntity().getDeltaY() ;
		    
		    Point2D playerCenterDelta = new Point2D.Double(deltaX, deltaY);
		    Point2D statCenter = new Point2D.Double(stat.getOwnerEntity().getX(), stat.getOwnerEntity().getY());
			
			
			Line2D axis = statBounds.getSeparatingAxis(separatingSide); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
		    
		    
	    	Vertex[] statOuterVertices= statBounds.getFarthestVertices(playerBounds,axis);
	    	Vertex[] playerOuterVertices= playerBounds.getFarthestVertices(statBounds,axis);
	    	
		    																					// [0] needs to be for loop
		    Vertex[] statInnerVertices = statBounds.farthestVerticesFromPoint( statOuterVertices[0].toPoint() , axis );
		    Vertex[] playerInnerVertices = playerBounds.farthestVerticesFromPoint( playerOuterVertices[0].toPoint() , axis );

		    
		    Vertex[] statOuter= statBounds.getFarthestVertices(playerBounds,axis);

	    	Vertex[] nearStatCorner = statBounds.farthestVerticesFromPoint( statOuter[0].toPoint() , axis ); //merge below
	    	Vertex[] nearPlayerCorner = playerBounds.farthestVerticesFromPoint( statOuter[1].toPoint() , axis );
	    	
	    	Vertex farStatCorner = statBounds.farthestVerticesFromPoint(nearStatCorner[0].toPoint(), axis)[0];
	    	Vertex farPlayerCorner = playerBounds.farthestVerticesFromPoint(nearPlayerCorner[0].toPoint(), axis)[0];
	    	
	    	Point2D centerStat = farStatCorner.getCenter(nearStatCorner[0]);
	    	Point2D centerPlayer = farPlayerCorner.getCenter(nearPlayerCorner[0]);

	    	Line2D centerDistance = new Line2D.Double( centerPlayer , centerStat );
	    	Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);
		    
		    //CLOSEST FEATURE
		    
		    
		    
		    //
		    
	    	Line2D playerHalf = new Line2D.Float( 
	    			playerBounds.getProjectionPoint(centerPlayer,axis) ,
	    			playerBounds.getProjectionPoint(nearPlayerCorner[0].toPoint(),axis)
	    			);
	    	Line2D statHalf = new Line2D.Float( 
	    			statBounds.getProjectionPoint(centerStat,axis) ,
	    			statBounds.getProjectionPoint(nearStatCorner[0].toPoint(),axis) 
	    			);
			
			
			double centerDistanceX = centerProjection.getX1() -  centerProjection.getX2()  ;
			double centerDistanceY = centerProjection.getY1() -  centerProjection.getY2()  ;
			
			double playerProjectionX = (playerHalf.getX1() -  playerHalf.getX2());
			double playerProjectionY = (playerHalf.getY1() -  playerHalf.getY2());
			
			double statProjectionX = (statHalf.getX2() -  statHalf.getX1());
			double statProjectionY = (statHalf.getY2() -  statHalf.getY1());
			
			double penetrationX = 0;
			double penetrationY = 0;

			
			if (centerDistanceX>0){
	    		centerDistanceX -= 1;  
				penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
			}
	    	else if (centerDistanceX<0){
	    		centerDistanceX += 1;  //NEEDS HIGHER LEVEL SOLUTION
	    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
	    	}
	    	else 
	    		penetrationX = playerProjectionX - statProjectionX;

	    	if (centerDistanceY>0){
	    		centerDistanceY -= 1;
	    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY ;	
	    	}
	    	else if (centerDistanceY<0){
	    		centerDistanceY += 1; 
	    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY ;	
	    	}
	    	else 
	    		penetrationY = playerProjectionY - statProjectionY;
	    	
			
			if ( penetrationX * centerDistanceX < 0 ) //
					penetrationX = 0;
			if ( penetrationY * centerDistanceY < 0 ) // 
					penetrationY = 0;
		
			

			return new Point( (int)penetrationX , (int)penetrationY );

		}
	
	
}
