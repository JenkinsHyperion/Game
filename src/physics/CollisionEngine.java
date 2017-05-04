package physics;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import engine.BoardAbstract;
import engine.Overlay;
import engine.OverlayComposite;
import entityComposites.*;

public class CollisionEngine {

	protected BoardAbstract currentBoard;
	
	protected ArrayList<Collider> staticCollidablesList = new ArrayList<>(); 
	protected ArrayList<Collider> dynamicCollidablesList = new ArrayList<>(); 
	
	protected LinkedList<Collision> collisionsList = new LinkedList<Collision>(); 

	public CollisionEngine(BoardAbstract testBoard){
		currentBoard = testBoard;
		
	}
	
	public void degubClearCollidables(){
		staticCollidablesList.clear();
		dynamicCollidablesList.clear();
		for (Collision collision : collisionsList){
			collision.completeCollision();
		}
		collisionsList.clear();
		//dynamicCollidablesList.clear(); //keep player temporarily while scenes are under construction
	}
	
	//check collision list and return true if two entities are already colliding
	protected boolean hasActiveCollision(EntityStatic entity1, EntityStatic entity2){
		    	
		for ( Collision activeCollision : collisionsList){
					
			if ( activeCollision.isActive(entity1, entity2) ) {
				return true;
			}
				
		}
		return false;
	}
	    
	//Update status of collisions, run ongoing commands in collision, and destroy collisions that have completed
	//USE ARRAY LIST ITTERATOR INSTEAD OF FOR LOOP SINCE REMOVING INDEX CHANGES SIZE
	protected void updateCollisions(){
	    	
	    for ( int i = 0 ; i < collisionsList.size() ; i++ ){
	    		
	    	//if collision is complete, remove from active list
	    	if (!collisionsList.get(i).isComplete() ) {
	    		collisionsList.get(i).updateCollision(); //Run commands from inside collision object
	    		
	    	}
	    	else {
	    		collisionsList.get(i).completeCollision();
	    		collisionsList.remove(i);	
    		}
	  		
    	}
	    	
    }
	
	public int addStaticCollidable( Collider collidable ){ //returns hashID index to collider composite
		staticCollidablesList.add(collidable);
		return -( staticCollidablesList.size() );
	}
	
	public int addDynamicCollidable( Collider collidable ){
		dynamicCollidablesList.add(collidable);
		return dynamicCollidablesList.size();
	}
	/**Removes collider from collision engine. Positive index removes from dynamic colliders, negative removes from
	 * static colliders
	 * @param engineHashID
	 */
	public void removeCollidable( int engineHashID){
		if (engineHashID<0){
			staticCollidablesList.remove( (-engineHashID) - 1 );
		}
		else if (engineHashID>0){
			dynamicCollidablesList.remove( engineHashID-1 );
		}
		else
			System.err.println("0 is not a valid index for removing Collider from engine.");
	}
	    
    //THIS IS THE MAIN BODY OF THE COLLISION ENGINE
    public void checkCollisions() { 
	    	
    	for ( int i = 0 ; i < dynamicCollidablesList.size() ; i++ ){
    		
    		Collider dynamicCollidablePrimary = dynamicCollidablesList.get(i);
    		
    		for ( int j = 0 ; j < staticCollidablesList.size(); j++ ){
    			
    			Collider staticCollidable = staticCollidablesList.get(j);
    			
    			staticCollidable.checkForInteractionWith( dynamicCollidablePrimary , CollisionCheck.SAT, this);
    		}
    		
    		for ( int k = i+1 ; k < dynamicCollidablesList.size(); k++ ){
    			
    			Collider dynamicCollidableSecondary = dynamicCollidablesList.get(k);
    			
    			dynamicCollidableSecondary.checkForInteractionWith( dynamicCollidablePrimary , CollisionCheck.SAT, this);
    		}
    		
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
    
    
    public void registerCollision( boolean bool , Collider collidable1 , Collider collidable2){
    	
    	if ( bool ) { 
		 //check to see if collision isn't already occurring
    		if (!hasActiveCollision(collidable1.getOwnerEntity(),collidable2.getOwnerEntity())) { 
			// if not, add new collision event
			//int index = currentBoard.getStaticEntities().size() + 1 ;
    			//System.out.println( "Collision detected" );
    			collisionsList.add(new VisualCollisionDynamicStatic( collidable1 , collidable2 , this ) ); 
			
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
    
    
 
   
    //#### EDITOR METHODS ###################################
    

    
    /*private Vector[] getSATVectors( EntityStatic entityA, EntityStatic entityB ) {
    	
    	Line2D[] axes = entityA.getBoundary().getSpearatingSidesBetween( entityB.getBoundary() );
    	Vector[] vectors = new Vector[axes.length];
    	
    	for ( int j = 0; j < vectors.length ; j++ ) { //index through all separating axes and get projection distance 
			
			vectors[j] = getDistanceSAT2( axes[j], entityA, entityB );

			if ( !vectors[j].isShorterThan(20) ) { return new Vector[0]; }//return new Vector[0]; }
			//System.out.println( vectors[j].getX() + " - " + vectors[j].getY());
		}

    	return vectors;
    	
    }*/
    

    
    
	    

    public void debugPrintCollisionList( int x, int y ,Graphics g){
    	
    	for ( int i = 0 ; i < this.collisionsList.size() ; i++ ) {
	    	
	    	g.drawString( collisionsList.get(i).toString() , x , y+(10*i) );
	    }
    	
    }
    
    protected BoardAbstract getBoard(){ return currentBoard; }

    public int debugNumberofStaticCollidables(){ 
    	return this.staticCollidablesList.size();
    }
    
    public int debugNumberofDynamicCollidables(){ 
    	return this.dynamicCollidablesList.size();
    }
    
    public int debugNumberOfCollisions(){
    	return this.collisionsList.size();
    }
    
    public Collider[] debugListActiveColliders(){
    	
    	Collider[] activeColliders = new Collider[ this.staticCollidablesList.size() + this.dynamicCollidablesList.size() ];
    	
    	int index;
    	
    	for ( index=0 ; index < staticCollidablesList.size() ; index++){
    		activeColliders[index] = staticCollidablesList.get(index);
    	}
    	
    	for ( int i = 0 ; i < dynamicCollidablesList.size() ; i++){
    		activeColliders[index] = dynamicCollidablesList.get(i);
    		index++;
    	}
    	
    	return activeColliders;
    }
	    
}
	   


