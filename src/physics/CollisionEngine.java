package physics;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import engine.Board;
import entities.EntityDynamic;
import entities.EntityStatic;
import entities.Player;
import entityComposites.*;

public class CollisionEngine implements Serializable{

	private Board currentBoard;
    private LinkedList<Collision> collisionsList = new LinkedList<Collision>(); 
	
	public CollisionEngine(Board board){
		currentBoard = board;
		
	}
	
	//check collision list and return true if two entities are already colliding
	private boolean hasActiveCollision(EntityStatic entity1, EntityStatic entity2){
		    	
		for ( Collision activeCollision : collisionsList){
					
			if ( activeCollision.isActive(entity1, entity2) ) {
				return true;
			}
				
		}
		return false;
	}
	    
	//Update status of collisions, run ongoing commands in collision, and destroy collisions that have completed
	//USE ARRAY LIST ITTERATOR INSTEAD OF FOR LOOP SINCE REMOVING INDEX CHANGES SIZE
	private void updateCollisions(){
	    	
	    for ( int i = 0 ; i < collisionsList.size() ; i++ ){
	    		
	    	//if collision is complete, remove from active list
	    	if (collisionsList.get(i).isComplete() ) {
	    		collisionsList.remove(i);
	    	}
	    	else {
	    			
    			collisionsList.get(i).updateCollision(); //Run commands from inside collision object
	    			
    		}
	  		
    	}
	    	
    }
	    
    //THIS IS THE MAIN BODY OF THE COLLISION ENGINE
    public void checkCollisions() { 
	    	
    	Player player = currentBoard.getPlayer();
	    	
        //Rectangle r0 = currentBoard.getPlayer().getBoundingBox(); // get bounding box of player first
	 
        //make larger box to represent distance at which a new collision will be opened 
        //Rectangle r3 = new Rectangle(r0.x - 1 , r0.y - 1, r0.width + 2, r0.height + 2); 

        //KILL PLAYER AT BOTTOM OF SCREEN
        /*if (player.getX() > currentBoard.getboundaryY()) {  
	        	
        	//could be teleport(x,y) or reposition(x,y) method in either player or parent entity classes
        	player.setX(currentBoard.ICRAFT_X);
        	player.setY(currentBoard.ICRAFT_Y);
        	player.setDX(0);
        	player.setDY(0);
        	player.setAccX(0);
        	player.setAccY(0.1f);
	        	
        }//*/
	        
	        
        // Check collisions between player and static objects
        for ( EntityStatic statS : currentBoard.getStaticEntities() ) {    
        
        	statS.getCollisionType().checkForInteractionWith(player.getCollisionType() , CollisionCheck.SAT, this);
        	
        }
        
        for ( EntityStatic statS : currentBoard.getPhysicsEntities() ) {    
            
        	statS.getCollisionType().checkForInteractionWith(player.getCollisionType() , CollisionCheck.SAT, this);
        	
        }
	        
	        // TEST LASER COLLISION 
	        /*for (EntityStatic stat : currentBoard.getStaticEntities()) {                	
	        	if ( currentBoard.laser.getBoundary().boundaryIntersects( ((Collidable) stat.collidability()).getBoundaryLocal() ) ) {
		            	
		            //OPEN COLLISION
		            if (!hasActiveCollision(currentBoard.laser,stat)) { //check to see if collision isn't already occurring
		            	collisionsList.add(new CollisionPositioning(currentBoard.laser, stat)); // if not, add new collision event
		            } 		            
		   		}
	        }*/
	        
	    
        
        // END OF CHECKS, update and remove collisions that completed;
    	updateCollisions();    
        
    }
    
    
    public void registerCollision( boolean bool , Collidable collidable1 , Collidable collidable2){
    	
    	if ( bool ) { 
		 //check to see if collision isn't already occurring
    		if (!hasActiveCollision(collidable1.getOwnerEntity(),collidable2.getOwnerEntity())) { 
			// if not, add new collision event
			//int index = currentBoard.getStaticEntities().size() + 1 ;
    			//System.out.println( "Collision detected" );
    			collisionsList.add(new CollisionPlayerStaticSAT( collidable1 , collidable2 ) ); 
			
			} 	
    	}
    	//else System.out.println("TEST");
    	
    }
    
    /*protected boolean checkForCollisionsSAT( EntityDynamic entityPrimary , EntityStatic entitySecondary ) {
        
	    for ( Line2D separatingSide : ((Collidable)entityPrimary.collidability()).getBoundaryLocal().getSeparatingSides() ){
	    	
	    	Point distanceVector = getDistanceSAT( separatingSide , entityPrimary , entitySecondary );
	    	
	    	if ( distanceVector.getX() == 0 && distanceVector.getY() == 0 ){
	    		//If ONLY ONE axis is separated, the entity is NOT COLLIDING OPTIMIZATION MERGE TWO LOOPS INTO ONE GET SEPARATING
	    		return false;
	    	}
	    }
	    
	    for ( Line2D separatingSide : entitySecondary.getBoundary().getSeparatingSides() ){
	    	
	    	Point distanceVector = getDistanceSAT( separatingSide , entityPrimary , entitySecondary );
	    	
	    	if ( distanceVector.getX() == 0 && distanceVector.getY() == 0 ){
	    		//If ONLY ONE axis is separated, the entity is NOT COLLIDING
	    		return false;
	    	}
	    }
	    

	    return true;
    	
    }*/
    
    
    private Point getDistanceSAT( Line2D separatingSide , Collidable entityPrimary , Collidable stat ){
	    
	    Boundary bounds = stat.getBoundaryLocal() ;
	    Boundary playerBounds = entityPrimary.getBoundaryDelta();
	    //Boundary playerBounds = entityPrimary.getBoundaryLocal();
	    
	    int deltaX = (int) (entityPrimary.getOwnerEntity().getDeltaX() );
	    int deltaY = (int) (entityPrimary.getOwnerEntity().getDeltaY() );
	    
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
    
    
    //### REDUNDANCY FOR TESTING
    /**
     * 
     * @param separatingSide
     * @param entityA
     * @param entityB
     * @return Returns vector to closest separating side in range, or NULL if entity is out of range
     */
    private Vector getDistanceSAT2( Line2D separatingSide , EntityStatic entityA , EntityStatic entityB ){
	    
	    Boundary boundsB = ((Collidable) entityB.getCollisionType()).getBoundaryLocal(); 
	    Boundary boundsA = ((Collidable) entityA.getCollisionType()).getBoundaryLocal();
	    
	    Point2D centerA = new Point2D.Double(entityA.getX(), entityA.getY());
	    Point2D centerB = new Point2D.Double(entityB.getX(), entityB.getY());
		
		Line2D axis = boundsB.getSeparatingAxis(separatingSide); //OPTIMIZE TO SLOPE ONLY CALCULATIONS
	    
	    Line2D centerDistance = new Line2D.Float(entityA.getPos() , entityB.getPos() );
	    Line2D centerProjection = boundsA.getProjectionLine(centerDistance, axis);
	    
	    Point2D nearStatCorner = boundsB.farthestPointFromPoint( boundsB.getFarthestPoints(boundsA,axis)[0] , axis );
	    Point2D nearPlayerCorner = boundsA.farthestPointFromPoint( boundsA.getFarthestPoints(boundsB,axis)[0] , axis );


	    
	    Line2D playerHalf = new Line2D.Float( 
				boundsA.getProjectionPoint(centerA,axis) ,
				boundsA.getProjectionPoint(nearPlayerCorner,axis)
						);
		Line2D statHalf = new Line2D.Float( 
				boundsB.getProjectionPoint(centerB,axis) ,
				boundsB.getProjectionPoint(nearStatCorner,axis)
						);
		
		
		int centerDistanceX = (int)(centerProjection.getX1() -  centerProjection.getX2()  );
		int centerDistanceY = (int)(centerProjection.getY1() -  centerProjection.getY2()  );
		
		int playerProjectionX = (int)(playerHalf.getX1() -  playerHalf.getX2());
		int playerProjectionY = (int)(playerHalf.getY1() -  playerHalf.getY2());
		
		int statProjectionX = (int)(statHalf.getX2() -  statHalf.getX1());
		int statProjectionY = (int)(statHalf.getY2() -  statHalf.getY1());
		
		int penetrationX = 0;
		int penetrationY = 0;
		
		// Get penetration vector
		
		
		penetrationX = playerProjectionX + statProjectionX - centerDistanceX ;
		penetrationY = playerProjectionY + statProjectionY - centerDistanceY ;

		if (centerDistanceX>0){ 
			penetrationX += 1; 
				//if ( penetrationX > 0 )
				//	penetrationX = 0;
			} 
		else if (centerDistanceX<0){ 
			penetrationX -= 1; 
				//if ( penetrationX < 0 )
				//	penetrationX = 0;
			} //NEEDS HIGHER LEVEL SOLUTION merge with later checks
		
		if (centerDistanceY>0){ 
			penetrationY += 1; 
				//if ( penetrationY > 0 )
				//penetrationY = 0;
			} 
		else if (centerDistanceY<0){ 
			penetrationY -= 1; 
				//if ( penetrationY < 0 )
				//	penetrationY = 0;
			}
		
		
		

		return new Vector( penetrationX , penetrationY ); 

	}
    
   
    //#### EDITOR METHODS ###################################
    
    /** Returns the distance to the closest side of the closest entity, or null if out of range. 
     * 
     * @param entityA - the entity being checked. This will be the entity being dragged
     * @param list - the entity list from board that you want entityA to snap to
     * @param range - the range at which this method will return distance instead of NULL. Positive only
     * @return Point(x,y) of distance to surface of closest entity. NULL if out of range of any entities
     */
    public Vector getClosestSnap( EntityStatic entityA , ArrayList<EntityStatic> list , int range){ //change to side index or later side object
    	
    	//OPTIMIZATION - MERGE THIS AND GETSATVECTORS INTO CLASS

    	//for ( EntityStatic entityB : currentBoard.getStaticEntities() ) { // index all static entities
    	EntityStatic entityB = currentBoard.getStaticEntities().get(0);
    	
    	Vector[] vectors = getSATVectors(entityA, entityB);
    			
    			

    	//}
    		
    	return new Vector( 0,0 );	
    	
    }
    
    private Vector[] getSATVectors( EntityStatic entityA, EntityStatic entityB ) {
    	
    	Line2D[] axes = entityA.getBoundary().getSpearatingSidesBetween( entityB.getBoundary() );
    	Vector[] vectors = new Vector[axes.length];
    	
    	for ( int j = 0; j < vectors.length ; j++ ) { //index through all separating axes and get projection distance 
			
			vectors[j] = getDistanceSAT2( axes[j], entityA, entityB );

			if ( !vectors[j].isShorterThan(20) ) { return new Vector[0]; }//return new Vector[0]; }
			//System.out.println( vectors[j].getX() + " - " + vectors[j].getY());
		}

    	return vectors;
    	
    }
    

    
    
	    

    public void debugPrintCollisionList( int x, int y ,Graphics g){
    	
    	for ( int i = 0 ; i < this.collisionsList.size() ; i++ ) {
	    	
	    	g.drawString( collisionsList.get(i).toString() , x , y+(10*i) );
	    }
    	
    }
	    
}
	   


