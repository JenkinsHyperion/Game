package physics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import engine.BoardAbstract;
import engine.MovingCamera;
import engine.Overlay;
import engine.OverlayComposite;
import entityComposites.*;
import utility.DoubleLinkedList;
import utility.ListNodeTicket;

public class CollisionEngine {

	protected BoardAbstract currentBoard;
	
	protected ArrayList<Collider> staticCollidablesList = new ArrayList<>(); 
	protected ArrayList<Collider> dynamicCollidablesList = new ArrayList<>(); 
	
	protected ArrayList<ActiveCollider> staticCollidables = new ArrayList<ActiveCollider>(); 
	protected ArrayList<ActiveCollider> dynamicCollidables = new ArrayList<ActiveCollider>(); 
	
	protected LinkedList<Collision> collisionsList = new LinkedList<Collision>(); 
	
	protected DoubleLinkedList<CheckingPair> checkingPairs = new DoubleLinkedList<CheckingPair>();

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
	
	//COLLIDER ADDITION METHODS
	
	public ActiveCollider addStaticCollidable( Collider collidable ){ //returns hashID index to collider composite
		
		ActiveCollider newStatic = new ActiveCollider(collidable);
		staticCollidables.add( newStatic );
		//Create pairs with dynamics
		for ( ActiveCollider dynamic : dynamicCollidables ){

			if ( dynamic.collider.getBoundary() instanceof BoundaryCircular ){
				
				if ( newStatic.collider.getBoundary() instanceof BoundaryCircular ){ //both bolliders are circles
					VisualCollisionCheck check = VisualCollisionCheck.circleCircle( dynamic.collider.getOwnerEntity(), newStatic.collider.getOwnerEntity() ) ;
					this.addPair( new CheckingPair( dynamic , newStatic , check ) );
				}
				else{
					VisualCollisionCheck check = VisualCollisionCheck.circlePoly( 
							dynamic.collider.getOwnerEntity(), 
							newStatic.collider.getOwnerEntity(),
							(BoundaryPolygonal)newStatic.collider.getBoundary() 
					);
					this.addPair( new CheckingPair( dynamic , newStatic , check ) );
				}
			}
			else {
				VisualCollisionCheck check = VisualCollisionCheck.polyPoly() ;
				this.addPair( new CheckingPair( dynamic , newStatic , check ) );
			}
		}
		
		return newStatic;
		
	}
	
	public ActiveCollider addDynamicCollidable( Collider collidable ){

		ActiveCollider newDynamic = new ActiveCollider(collidable);

		for ( ActiveCollider stat : staticCollidables ){
			
			if ( collidable.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
				
				if ( stat.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
					
					this.addPair( new CheckingPair( newDynamic , stat , 
							VisualCollisionCheck.circleCircle(
									newDynamic.collider.getOwnerEntity(),
									stat.collider.getOwnerEntity()
							)
					));
					System.out.println( " circle-circle" );
					
				}
				else if ( stat.collider.getBoundary().getTypeCode() == Boundary.POLYGONAL ){
					
					this.addPair( new CheckingPair( newDynamic , stat , 
							VisualCollisionCheck.circlePoly(
									newDynamic.collider.getOwnerEntity(),
									stat.collider.getOwnerEntity(),
									(BoundaryPolygonal) stat.collider.getBoundary() 
							)
					));
					System.out.println( " circle-polygon" );
				}
				else{
					System.err.println( stat.collider.getOwnerEntity() + " / "+ collidable.getOwnerEntity() +" could not be paired " );
				}
				
			}
			else {
				System.err.println( stat.collider.getOwnerEntity() + " / "+ collidable.getOwnerEntity() +" could not be paired " );
			}

			
			
		}
		
		//DYNAMIC - DYNAMIC COLLISION PAIRS
		
		for ( ActiveCollider dynamic : dynamicCollidables ){
			
			if ( dynamic.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
				
				if ( collidable.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
					
					this.addPair( new CheckingPair( newDynamic , dynamic , 
							VisualCollisionCheck.circleCircle(
									newDynamic.collider.getOwnerEntity(),
									dynamic.collider.getOwnerEntity()
							)
					));
					System.out.println( " circle-circle" );
				}
				
			}
			else if ( dynamic.collider.getBoundary().getTypeCode() == Boundary.POLYGONAL ){
				
				if ( collidable.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
					
					this.addPair( new CheckingPair( newDynamic , dynamic , 
							VisualCollisionCheck.circlePoly(
									newDynamic.collider.getOwnerEntity(),
									dynamic.collider.getOwnerEntity(),
									(BoundaryPolygonal) dynamic.collider.getBoundary()
							)
					));
					System.out.println( " poly-circle" );
				}
				
			}
			else{
				System.err.println( dynamic.collider.getOwnerEntity() + " / "+ collidable.getOwnerEntity() +" could not be paired " );
			}
			
		}
		dynamicCollidables.add( newDynamic );
		return newDynamic;
	}
	
	private void addPair( CheckingPair pair ){
		pair.listSlot = checkingPairs.add(pair);
	}
	    
	
    //COLLISION ENGINE MAIN LOOP METHODS
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

        // END OF CHECKS, update and remove collisions that completed;
    	updateCollisions();    
        
    }
    
    
    public void registerCollision( boolean bool , Collider collidable1 , Collider collidable2, CollisionCheck check){ //OPTIMIZE remove outdated boolean
    	
    	if ( bool ) { 
		 //check to see if collision isn't already occurring
    		if (!hasActiveCollision(collidable1.getOwnerEntity(),collidable2.getOwnerEntity())) { 
			// if not, add new collision event
			//int index = currentBoard.getStaticEntities().size() + 1 ;
    			//System.out.println( "Collision detected" );
    			collisionsList.add(new VisualCollisionDynamicStatic( 
    					collidable1 , collidable2 , 
    					((VisualCollisionCheck)check).axisCollector ,
    					this 
    					)); 
			
			} 	
    	}
    	//else System.out.println("TEST");
    	
    }  

    public void debugPrintCollisionList( int x, int y ,Graphics g){
    	
    	for ( int i = 0 ; i < this.collisionsList.size() ; i++ ) {
	    	
	    	g.drawString( collisionsList.get(i).toString() , x , y+(10*i) );
	    }
    	
    }
    
    protected BoardAbstract getBoard(){ return currentBoard; }

    public int debugNumberofStaticCollidables(){ 
    	return this.staticCollidables.size();
    }
    
    public int debugNumberofDynamicCollidables(){ 
    	return this.dynamicCollidables.size();
    }
    
    public int debugNumberOfCollisions(){
    	return this.collisionsList.size();
    }
    
    public Collider[] debugListActiveColliders(){
    	
    	Collider[] activeColliders = new Collider[ this.staticCollidables.size() + this.dynamicCollidables.size() ];
    	
    	int index;
    	
    	for ( index=0 ; index < staticCollidables.size() ; index++){
    		activeColliders[index] = staticCollidables.get(index).collider;
    	}
    	
    	for ( int i = 0 ; i < dynamicCollidables.size() ; i++){
    		activeColliders[index] = dynamicCollidables.get(i).collider;
    		index++;
    	}
    	
    	return activeColliders;
    }
    
    
	public class ActiveCollider{
		protected Collider collider;
		private CheckingPair[] pairs;
		
		public ActiveCollider( Collider collider ){
			this.collider = collider;
		}

		public void removeSelf() {
			//TODO
		}
		
	}
	
	protected class CheckingPair{
		private ListNodeTicket listSlot;
		
		private ActiveCollider primary;
		private ActiveCollider secondary;
		
		private CollisionCheck check;

		public CheckingPair( ActiveCollider collider1 , ActiveCollider collider2 , CollisionCheck check ){
			this.primary = collider1;
			this.secondary = collider2;
			this.check = check;
		}
		
		public void addToList( DoubleLinkedList<CheckingPair> list ){
			this.listSlot = list.add( this );
		}
		
		public void check(){
			registerCollision(check.check(primary.collider, secondary.collider), primary.collider , secondary.collider , this.check);
		}
		
		public void visualCheck( MovingCamera cam, Graphics2D g2 ){
			registerCollision(((VisualCollisionCheck)check).check(primary.collider, secondary.collider, cam , g2), 
					primary.collider , secondary.collider , this.check);
		}
		
	}
    
    
	    
}
	   


