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
	
	protected ArrayList<ArrayList<ActiveCollider>> staticCollidables = new ArrayList<ArrayList<ActiveCollider>>(); 
	protected ArrayList<ArrayList<ActiveCollider>> dynamicCollidables = new ArrayList<ArrayList<ActiveCollider>>(); 
	
	protected LinkedList<Collision> collisionsList = new LinkedList<Collision>(); 
	
	protected DoubleLinkedList<CheckingPair> checkingPairs = new DoubleLinkedList<CheckingPair>();

	public CollisionEngine(BoardAbstract testBoard){
		currentBoard = testBoard;
		
	}
	
	public void degubClearCollidables(){
		
		//FIXME USED TO CLEAR OLD COLLIABLES LIST, NEEDS TO DEAL WITH NEW ONE
		
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
		
		ActiveCollider newStatic = new StaticActiveCollider( collidable , staticCollidables.size() );
		
		final ArrayList<ActiveCollider> newStaticGroup = new ArrayList<ActiveCollider>();
		newStaticGroup.add(newStatic);
		
		staticCollidables.add( newStaticGroup );
		//Create pairs with dynamics
		for ( ArrayList<ActiveCollider> dynamicsGroup : dynamicCollidables ){
			
			createPairsWithStaticCollidersInGroup( newStatic , dynamicsGroup );
		}
		
		return newStatic;
		
	}
	
	public ActiveCollider addDynamicCollidable( Collider collidable ){ //TODO ADD GROUPING 

		ActiveCollider newDynamic = new DynamicActiveCollider( collidable,dynamicCollidables.size() );

		for ( ArrayList<ActiveCollider> staticsGroup : staticCollidables ){
			
			createPairsWithStaticCollidersInGroup( newDynamic, staticsGroup);
		}
		
		//DYNAMIC - DYNAMIC COLLISION PAIRS
		
		for ( ArrayList<ActiveCollider> dynamicsGroup : dynamicCollidables ){
			
			createDynamicDynamicPairsWithCollidersInGroup( newDynamic, dynamicsGroup);
		}
		
		final ArrayList<ActiveCollider> newDynamicsGroup = new ArrayList<ActiveCollider>();
		newDynamicsGroup.add(newDynamic);
		
		dynamicCollidables.add( newDynamicsGroup );
		return newDynamic;
	}
	
	private void createPairsWithStaticCollidersInGroup( ActiveCollider newCollidable , ArrayList<ActiveCollider> staticsGroup){
		
		for ( ActiveCollider active : staticsGroup ){

			if ( newCollidable.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
				
				if ( active.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
					
					this.addPair( new DynamicStaticPair( newCollidable , active , 
							VisualCollisionCheck.circleCircle(
									newCollidable.collider.getOwnerEntity(),
									active.collider.getOwnerEntity()
							)
					));
					System.out.println( " circle-circle" );
					
				}
				else if ( active.collider.getBoundary().getTypeCode() == Boundary.POLYGONAL ){
					
					this.addPair( new DynamicStaticPair( newCollidable , active , 
							VisualCollisionCheck.circlePoly(
									newCollidable.collider.getOwnerEntity(),
									active.collider.getOwnerEntity(),
									(BoundaryPolygonal) active.collider.getBoundary() 
							)
					));
					System.out.println( " circle-polygon" );
				}
				else{
					System.err.println( active.collider.getOwnerEntity() + " / "+ newCollidable.collider.getOwnerEntity() +" could not be paired " );
				}
				
			}
			else {
				System.err.println( active.collider.getOwnerEntity() + " / "+ newCollidable.collider.getOwnerEntity() +" could not be paired24 " );
			}
		}
	}
	
	private void createDynamicDynamicPairsWithCollidersInGroup( ActiveCollider newDynamic , ArrayList<ActiveCollider> dynamicsGroup ){
	
		for ( ActiveCollider dynamic : dynamicsGroup ){
	
			if ( dynamic.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
				
				if ( newDynamic.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
					
					this.addPair( new DynamicDynamicPair( newDynamic , dynamic , 
							VisualCollisionCheck.circleCircle(
									newDynamic.collider.getOwnerEntity(),
									dynamic.collider.getOwnerEntity() )
					));
					System.out.println( " circle-circle" );
				}
				else if ( newDynamic.collider.getBoundary().getTypeCode() == Boundary.POLYGONAL ){
					
					this.addPair( new DynamicDynamicPair( newDynamic , dynamic , 
							VisualCollisionCheck.circlePoly(
									dynamic.collider.getOwnerEntity(),
									newDynamic.collider.getOwnerEntity() ,
									(BoundaryPolygonal) newDynamic.collider.getBoundary() )
					));
				}
				else{
					System.err.println( dynamic.collider.getOwnerEntity() + " / "+ newDynamic.collider.getOwnerEntity() +" could not be paired dynamic-dynamic " );
				}
				
			}
			else if ( dynamic.collider.getBoundary().getTypeCode() == Boundary.POLYGONAL ){
				
				if ( newDynamic.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
					
					this.addPair( new DynamicDynamicPair( newDynamic , dynamic , 
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
				System.err.println( dynamic.collider.getOwnerEntity() + " / "+ newDynamic.collider.getOwnerEntity() +" could not be paired dynamic-dynamic " );
			}
		}
	}
	
	private void addPair( CheckingPair pair ){
		pair.listSlot = checkingPairs.add(pair);
	}
	
    //COLLISION ENGINE MAIN LOOP METHODS
    public void checkCollisions() { //OPTIMIZE OBSOLETE, SEE VISUAL COLLISION ENGINE
	    	/*
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
    		
    	}*/

        // END OF CHECKS, update and remove collisions that completed;
    	updateCollisions();    
        
    }
    
    public void registerCollision( CollisionFactory factory, Collider collider1 , Collider collider2, VisualCollisionCheck check ){
    
    	if (!hasActiveCollision(collider1.getOwnerEntity(),collider2.getOwnerEntity())) { 
			collisionsList.add( factory.createVisualCollision(collider1, collider2, check, this.getBoard().renderingEngine) );
		} 	
    } 
    
    @Deprecated
    public void registerDynamicStaticCollision( boolean bool , Collider collidable1 , Collider collidable2, CollisionCheck check){ //OPTIMIZE remove outdated boolean
    	
    	if ( bool ) { 
		 //check to see if collision isn't already occurring
    		//FIXME GET RID OF BOOLEAN AND <AKE ACTIVE AND INACTIVE COLLISION ARRAYS INSTEAD OF THIS MESS
    		if (!hasActiveCollision(collidable1.getOwnerEntity(),collidable2.getOwnerEntity())) { 

    			collisionsList.add(new VisualCollisionDynamicStatic( 
    					collidable1 , collidable2 , 
    					((VisualCollisionCheck)check).axisCollector ,
    					this.getBoard().renderingEngine
    					)); 
			} 	
    	}
    }   

    public void debugPrintCollisionList( int x, int y ,Graphics g){
    	
    	for ( int i = 0 ; i < this.collisionsList.size() ; i++ ) {
	    	
	    	g.drawString( collisionsList.get(i).toString() , x , y+(10*i) );
	    }
    	
    }
    
    protected BoardAbstract getBoard(){ return currentBoard; }

    public int debugNumberofStaticCollidableGroups(){ 
    	return this.staticCollidables.size();
    }
    
    public int debugNumberofDynamicCollidableGroups(){ 
    	return this.dynamicCollidables.size();
    }
    
    public int debugNumberOfCollisions(){
    	return this.collisionsList.size();
    }
    
    public Collider[] debugListActiveColliders(){
    	
    	Collider[] compiledListOfColliders = new Collider[ this.staticCollidables.size() + this.dynamicCollidables.size() ];
    	
    	int index = 0;
    	
    	for ( ArrayList<ActiveCollider> staticsGroup : staticCollidables ){
    		for ( ActiveCollider stat : staticsGroup ){
    			compiledListOfColliders[index] = stat.collider;
    			index++;
    		}
    	}
    	
    	for ( ArrayList<ActiveCollider> dynamicsGroup : dynamicCollidables ){
    		for ( ActiveCollider dynamic : dynamicsGroup ){
    			compiledListOfColliders[index] = dynamic.collider;
    			index++;
    		}
    	}
    	
    	return compiledListOfColliders;
    }
    
    
    
	public abstract class ActiveCollider{
		protected Collider collider;
		protected ArrayList<CheckingPair> pairsList = new ArrayList<CheckingPair>();
		
		public ActiveCollider( Collider collider ){
			this.collider = collider;
		}

		public void removeSelf() {
			//TODO
		}
		
		public abstract void notifyChangeToStatic(); //OPTIMIZE abstract on a higher level than here
		public abstract void notifyChangeToDynamic();
		public abstract void decrementIndex();
		public abstract int getGroupIndex();
		
		protected void dissolveAllPairs(){
			for ( CheckingPair obsoletePair : pairsList ){
				obsoletePair.removeSelf();
			}
			pairsList.clear();
		}
		
	}

	private class StaticActiveCollider extends ActiveCollider{

		private int staticListIndex;
		
		public StaticActiveCollider(Collider collider, int index ) {
			super(collider);
			this.staticListIndex = index;
		}
		
		public void notifyChangeToStatic(){
			//ALREADY STATIC SO DO NOTHING
		}
		
		public void notifyChangeToDynamic(){
			
			dissolveAllPairs(); //FIXME Allow only dynamic/statics to be removed rather than blitzing all pairs

			staticCollidables.remove(staticListIndex); //Remove from dynamic colliders list
			
			for ( ArrayList<ActiveCollider> activeStaticsGroup : staticCollidables ){
				activeStaticsGroup.get(0).decrementIndex();
			}
			
			addDynamicCollidable( this.collider );
		}
		
		@Override
		public void decrementIndex() {
			this.staticListIndex--;
		}
		
		@Override
		public int getGroupIndex() {
			return this.staticListIndex;
		}
		
	}
	
	private class DynamicActiveCollider extends ActiveCollider{

		private int dynamicListIndex;
		
		public DynamicActiveCollider(Collider collider, int index) {
			super(collider);
			this.dynamicListIndex = index;
		}
		
		public void notifyChangeToStatic(){
			
			dissolveAllPairs(); //FIXME Allow only dynamic/statics to be removed rather than blitzing all pairs

			dynamicCollidables.remove(dynamicListIndex); //Remove from dynamic colliders list and shift indexes
			for ( ArrayList<ActiveCollider> dynamicsGroup : dynamicCollidables ){
				dynamicsGroup.get(0).decrementIndex();
			}
			
			addStaticCollidable( this.collider ); //Re-add new static collidable
		}
		
		public void notifyChangeToDynamic(){
			//ALREADY DYNAMIC SO DO NOTHING
		}
		
		@Override
		public void decrementIndex() {
			this.dynamicListIndex--;
		}
		
		@Override
		public int getGroupIndex() {
			return this.dynamicListIndex;
		}
	}
	
	protected abstract class CheckingPair{
		
		protected ListNodeTicket listSlot;
		
		protected CollisionFactory collisionType;
		
		public void addToList( DoubleLinkedList<CheckingPair> list ){
			this.listSlot = list.add( this );
		}
		
		public void removeSelf(){
			this.listSlot.removeSelfFromList();
		}
		
		abstract void check();
		
		abstract void visualCheck( MovingCamera cam, Graphics2D g2 );
	}
	
	protected class DynamicStaticPair extends CheckingPair{
		
		private ActiveCollider dynamic;
		private ActiveCollider stat;
		
		private VisualCollisionCheck check;

		public DynamicStaticPair( ActiveCollider collider1 , ActiveCollider collider2 , VisualCollisionCheck check ){
			this.dynamic = collider1;
			this.stat = collider2;
			this.check = check;
			collider1.pairsList.add(this);
			collider2.pairsList.add(this);
			
			//Check for rigid bodies on both entities
			if ( 
				dynamic.collider.getOwnerEntity().getRigidbody().exists() 
					&& 
				stat.collider.getOwnerEntity().getRigidbody().exists() 
			){
				
				this.collisionType = CollisionFactory.dynamicStatic();
				
			}else{
				this.collisionType = CollisionFactory.rigidlessDynamicStatic();
			}				
		}
		
		public void check(){
			registerDynamicStaticCollision(check.check(dynamic.collider, stat.collider), dynamic.collider , stat.collider , this.check);
		}
		
		public void visualCheck( MovingCamera cam, Graphics2D g2 ){
			
			if ( check.check( dynamic.collider, stat.collider, cam , g2) ){
				registerCollision(collisionType, dynamic.collider, stat.collider, check);
			}
			
			//registerDynamicStaticCollision(((VisualCollisionCheck)check).check(dynamic.collider, stat.collider, cam , g2), 
			//		dynamic.collider , stat.collider , this.check);
		}
		
	}
    
	protected class DynamicDynamicPair extends CheckingPair{
		private ListNodeTicket listSlot;
		
		private ActiveCollider dynamicCollider1;
		private ActiveCollider dynamicCollider2;
		
		private CollisionCheck check;

		public DynamicDynamicPair( ActiveCollider dynamicCollider1 , ActiveCollider dynamicCollider2 , CollisionCheck check ){
			this.dynamicCollider1 = dynamicCollider1;
			this.dynamicCollider2 = dynamicCollider2;
			this.check = check;
			dynamicCollider1.pairsList.add(this);
			dynamicCollider2.pairsList.add(this);

			this.collisionType = CollisionFactory.rigidlessDynamicStatic();
		}
		
		public void check(){
			registerDynamicStaticCollision(check.check(dynamicCollider1.collider, dynamicCollider2.collider), dynamicCollider1.collider , dynamicCollider2.collider , this.check);
		}
		
		public void visualCheck( MovingCamera cam, Graphics2D g2 ){
			registerDynamicStaticCollision(((VisualCollisionCheck)check).check(dynamicCollider1.collider, dynamicCollider2.collider, cam , g2), 
					dynamicCollider1.collider , dynamicCollider2.collider , this.check);
		}
		
	}
    
	    
}
	   


