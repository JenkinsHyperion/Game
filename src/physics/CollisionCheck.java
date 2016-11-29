package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import entities.EntityDynamic;
import entities.EntityStatic;
import entityComposites.Collidable;

public class CollisionCheck {
	
	public static CollisionCheck SAT = new CollisionCheck();
	
	private CollisionCheck( ){
		
	}
	
	public static CollisionCheck SAT(){
		
		return SAT;
	}
	
	
	public boolean check( Collidable collidablePrimary , Collidable collidableSecondary ) {
	        
		    /*for ( Line2D separatingSide : collidablePrimary.getBoundaryLocal().getSeparatingSides() ){
		    	
		    	Point distanceVector = getDistanceSAT( separatingSide , collidablePrimary , collidableSecondary );
		    	
		    	if ( distanceVector.getX() == 0 && distanceVector.getY() == 0 ){
		    		//If ONLY ONE axis is separated, the entity is NOT COLLIDING OPTIMIZATION MERGE TWO LOOPS INTO ONE GET SEPARATING
		    		return false;
		    	}
		    }
		    
		    for ( Line2D separatingSide : collidablePrimary.getBoundaryLocal().getSeparatingSides() ){
		    	
		    	Point distanceVector = getDistanceSAT( separatingSide , collidablePrimary , collidableSecondary );
		    	
		    	if ( distanceVector.getX() == 0 && distanceVector.getY() == 0 ){
		    		//If ONLY ONE axis is separated, the entity is NOT COLLIDING
		    		return false;
		    	}
		    }*/
		
		
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
		    
		    Boundary bounds = stat.getBoundaryLocal() ;
		    Boundary playerBounds = primary.getBoundaryDelta();
		    //Boundary playerBounds = entityPrimary.getBoundaryLocal();
		    
		    int deltaX = (int) (primary.getOwnerEntity().getDeltaX() );
		    int deltaY = (int) (primary.getOwnerEntity().getDeltaY() );
		    
		    Point2D playerCenter = new Point2D.Double(deltaX, deltaY);
		    //Point2D playerCenter = new Point2D.Double(entityPrimary.getX(), entityPrimary.getY());
		    
		    Point2D statCenter = new Point2D.Double(stat.getOwnerEntity().getX(), stat.getOwnerEntity().getY());
			
			
			Line2D axis = bounds.getSeparatingAxis(separatingSide); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
		    
		    Line2D centerDistance = new Line2D.Float(deltaX , deltaY,
		    		stat.getOwnerEntity().getX() , stat.getOwnerEntity().getY() );
		    Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);
		 
		    
		    Point2D nearStatCorner = bounds.farthestPointFromPoint( bounds.getFarthestPoints(playerBounds,axis)[0] , axis );
		      
		    Point2D nearPlayerCorner = playerBounds.farthestPointFromPoint( playerBounds.getFarthestPoints(bounds,axis)[0] , axis );


		    
		    Line2D playerHalf = new Line2D.Float( 
					playerBounds.getProjectionPoint(playerCenter,axis) ,
					playerBounds.getProjectionPoint(nearPlayerCorner,axis)
							);
			Line2D statHalf = new Line2D.Float( 
					bounds.getProjectionPoint(statCenter,axis) ,
					bounds.getProjectionPoint(nearStatCorner,axis)
							);
			
			
			int centerDistanceX = (int)(centerProjection.getX1() -  centerProjection.getX2()  );
			int centerDistanceY = (int)(centerProjection.getY1() -  centerProjection.getY2()  );
			
			if (centerDistanceX>0){ centerDistanceX -= 1; } 
			else if (centerDistanceX<0){ centerDistanceX += 1; } //NEEDS HIGHER LEVEL SOLUTION
			
			if (centerDistanceY>0){ centerDistanceY -= 1; } 
			else if (centerDistanceY<0){ centerDistanceY += 1; }
			
			int playerProjectionX = (int)(playerHalf.getX1() -  playerHalf.getX2());
			int playerProjectionY = (int)(playerHalf.getY1() -  playerHalf.getY2());
			
			int statProjectionX = (int)(statHalf.getX2() -  statHalf.getX1());
			int statProjectionY = (int)(statHalf.getY2() -  statHalf.getY1());
			
			int penetrationX = 0;
			int penetrationY = 0;
			
			// Get penetration vector
			penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
			penetrationY = playerProjectionY + statProjectionY - centerDistanceY ;
			
			if ( penetrationX * centerDistanceX < 0  || penetrationY * centerDistanceY < 0  ) //SIGNS ARE NOT THE SAME
			{
				penetrationX = 0;
				penetrationY = 0;
				
			}

			// Handling of exception where centered collisions always have penetration of 0
			if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){ //LOOK INTO BETTER CONDITIONALS
				penetrationX = -(playerProjectionX + statProjectionX) ;
			}
			if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){ //Merge with above checks
				penetrationY = -(playerProjectionY + statProjectionY) ;
			}

			return new Point( penetrationX , penetrationY );

		}
	 
	 
	 
	 public Point debuggetDistanceSAT( Line2D separatingSide , Collidable primary , Collidable stat ){
		    
		    Boundary bounds = stat.getBoundaryLocal() ;
		    Boundary playerBounds = primary.getBoundaryDelta();
		    //Boundary playerBounds = entityPrimary.getBoundaryLocal();
		    
		    int deltaX = (int) (primary.getOwnerEntity().getDeltaX() );
		    int deltaY = (int) (primary.getOwnerEntity().getDeltaY() );
		    
		    Point2D playerCenter = new Point2D.Double(deltaX, deltaY);
		    //Point2D playerCenter = new Point2D.Double(entityPrimary.getX(), entityPrimary.getY());
		    
		    Point2D statCenter = new Point2D.Double(stat.getOwnerEntity().getX(), stat.getOwnerEntity().getY());
			
			
			Line2D axis = bounds.getSeparatingAxis(separatingSide); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
		    
		    Line2D centerDistance = new Line2D.Float(deltaX , deltaY,
		    		stat.getOwnerEntity().getX() , stat.getOwnerEntity().getY() );
		    Line2D centerProjection = playerBounds.getProjectionLine(centerDistance, axis);
		 
		    
		    Point2D nearStatCorner = bounds.farthestPointFromPoint( bounds.getFarthestPoints(playerBounds,axis)[0] , axis );
		      
		    Point2D nearPlayerCorner = playerBounds.farthestPointFromPoint( playerBounds.getFarthestPoints(bounds,axis)[0] , axis );


		    
		    Line2D playerHalf = new Line2D.Float( 
					playerBounds.getProjectionPoint(playerCenter,axis) ,
					playerBounds.getProjectionPoint(nearPlayerCorner,axis)
							);
			Line2D statHalf = new Line2D.Float( 
					bounds.getProjectionPoint(statCenter,axis) ,
					bounds.getProjectionPoint(nearStatCorner,axis)
							);
			
			
			int centerDistanceX = (int)(centerProjection.getX1() -  centerProjection.getX2()  );
			int centerDistanceY = (int)(centerProjection.getY1() -  centerProjection.getY2()  );
			
			if (centerDistanceX>0){ centerDistanceX -= 1; } 
			else if (centerDistanceX<0){ centerDistanceX += 1; } //NEEDS HIGHER LEVEL SOLUTION
			
			if (centerDistanceY>0){ centerDistanceY -= 1; } 
			else if (centerDistanceY<0){ centerDistanceY += 1; }
			
			int playerProjectionX = (int)(playerHalf.getX1() -  playerHalf.getX2());
			int playerProjectionY = (int)(playerHalf.getY1() -  playerHalf.getY2());
			
			int statProjectionX = (int)(statHalf.getX2() -  statHalf.getX1());
			int statProjectionY = (int)(statHalf.getY2() -  statHalf.getY1());
			
			int penetrationX = 0;
			int penetrationY = 0;
			
			// Get penetration vector
			penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
			penetrationY = playerProjectionY + statProjectionY - centerDistanceY ;
			
			if ( penetrationX * centerDistanceX < 0  || penetrationY * centerDistanceY < 0  ) //SIGNS ARE NOT THE SAME
			{
				penetrationX = 0;
				penetrationY = 0;
				
			}

			// Handling of exception where centered collisions always have penetration of 0
			if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){ //LOOK INTO BETTER CONDITIONALS
				penetrationX = -(playerProjectionX + statProjectionX) ;
			}
			if (centerDistanceX*centerDistanceX + centerDistanceY*centerDistanceY == 0){ //Merge with above checks
				penetrationY = -(playerProjectionY + statProjectionY) ;
			}

			return new Point( penetrationX , penetrationY );

		}
	
	
}
