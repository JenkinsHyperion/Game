package physics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import engine.BoardAbstract;
import engine.MovingCamera;
import engine.Overlay;
import engine.OverlayComposite;
import entityComposites.*;
import misc.CollisionEvent;
import utility.DoubleLinkedList;
import utility.ListNodeTicket;

public class CollisionEngine {

	protected BoardAbstract currentBoard;
	
	protected ColliderGroup ungrouped = new ColliderGroup("Ungrouped");
	
	protected DoubleLinkedList<CheckingPair<EntityStatic,EntityStatic>> activeCheckingPairs = new DoubleLinkedList<CheckingPair<EntityStatic,EntityStatic>>();
	protected DoubleLinkedList<CheckingPair<EntityStatic,EntityStatic>> inactiveCheckingPairs = new DoubleLinkedList<CheckingPair<EntityStatic,EntityStatic>>();
	
	protected ArrayList<ColliderGroup> colliderGroups = new ArrayList<ColliderGroup>();
	
	protected ArrayList<GroupPair> groupPairs = new ArrayList<GroupPair>();
	
	protected LinkedList<Collision> runningCollisionsList = new LinkedList<Collision>();  

	
	
	public CollisionEngine(BoardAbstract testBoard){
		
		currentBoard = testBoard;
	}
	
	public void degubClearCollidables(){
		
		//FIXME USED TO CLEAR OLD COLLIABLES LIST, NEEDS TO DEAL WITH NEW ONE
		
		for (Collision collision : runningCollisionsList){
			collision.completeCollision();
		}
		runningCollisionsList.clear();
		//dynamicCollidablesList.clear(); //keep player temporarily while scenes are under construction
	}
	
	//check collision list and return true if two entities are already colliding
	protected boolean hasActiveCollision(EntityStatic entity1, EntityStatic entity2){
		    	
		for ( Collision activeCollision : runningCollisionsList){
					
			if ( activeCollision.isActive(entity1, entity2) ) {
				return true;
			}
				
		}
		return false;
	}
	    
	//Update status of collisions, run ongoing commands in collision, and destroy collisions that have completed
	//USE ARRAY LIST ITTERATOR INSTEAD OF FOR LOOP SINCE REMOVING INDEX CHANGES SIZE
	protected void updateCollisions(){
	    	
	    for ( int i = 0 ; i < runningCollisionsList.size() ; i++ ){
	    		
	    	//if collision is complete, remove from active list
	    	if (!runningCollisionsList.get(i).isComplete() ) {
	    		runningCollisionsList.get(i).updateCollision(); //Run commands from inside collision object
	    		
	    	}
	    	else {
	    		runningCollisionsList.get(i).completeCollision();
	    		runningCollisionsList.remove(i);	
    		}
	  		
    	}
	    	
    }
	//CUSTOM COLLIDER PAIR BY GROUPS
	
	public void createColliderGroup( String newGroupName ){
		
		if ( getGroupsByName( newGroupName ).length == 0 ){
			ColliderGroup newGroup = new ColliderGroup(newGroupName);
			newGroup.addToGroupList(colliderGroups);
			System.out.println("Creating group '"+newGroupName+"'");
		}else{
			System.err.println("Group '"+newGroupName+"' already exists");
		}
		
	}
	
	public <E extends EntityStatic> void createColliderGroupOfType( String newGroupName ){
		
		if ( getGroupsByName( newGroupName ).length == 0 ){
			ColliderGroupTyped<E> newGroup = new ColliderGroupTyped<E>( newGroupName );
			newGroup.addToGroupList(colliderGroups);
			System.out.println("Creating group '"+newGroupName+"'");
		}else{
			System.err.println("Group '"+newGroupName+"' already exists");
		}
		
	}
	/**
	 * 
	 * @param strings
	 * @return
	 */
	public ColliderGroup[] getGroupsByName( String...strings ){
		
		ColliderGroup[] returnGroups = new ColliderGroup[ strings.length ];
		
		ArrayList<String> testingFor = new ArrayList<String>(); //Create temporary list of strings to be checking
		for ( int i = 0 ; i < strings.length ; ++i ){
			testingFor.add( strings[i] );
		}
		
		int returnIndex = 0;
		
		for ( ColliderGroup group : colliderGroups ){ //Main iteration through colliders 
			
			for ( int i = 0 ; i < testingFor.size() ; i++ ){
				if ( group.toString().matches( testingFor.get(i) ) ){ //if one of the tests is matched
					returnGroups[returnIndex] = group; //send it to the final array
					testingFor.remove(i); //and remove it from any further testing
					returnIndex++; //index to next spot in return array
					break; //break to stop iteration
				}
			}
			
		}
		
		if ( testingFor.size() != 0 ){ // itteration left unmatched tests, so search failed
			System.err.print( "Collision Engine: Could not find groups: " );
			for( String fails : testingFor )
				System.err.print( "'"+fails+"', " );
			System.err.println("");
			return new ColliderGroup[0];
		}else{
			return returnGroups;
		}
		
	}
	
	public <T extends EntityStatic,V extends EntityStatic>void addTypeCustomCollisionsBetween( ColliderGroupTyped<T> group1, ColliderGroupTyped<V> group2, CollisionBuilder<T,V> customCollisionFactory ){
		
	}
	
	public boolean addCustomCollisionsBetween( String group1, String group2, CollisionBuilder<?,?> customCollisionFactory ){
		
		ColliderGroup[] groups = this.getGroupsByName( group1, group2);
		
		if ( groups.length == 2 ){
			
			ColliderGroup groupPrimary = groups[0];
			ColliderGroup groupSecondary = groups[1];
			
			System.out.println(" COLLISION ENGINE: CUSTOMIZING COLLISIONS BETWEEN '"+groupPrimary+"' AND '"+groupSecondary+"' COLLIDER GROUPS");
			
			this.groupPairs.add( new GroupPair(groupPrimary,groupSecondary,customCollisionFactory) );
			
			while( groupPrimary.dynamicColliders.hasNext() ){			//Pair all colliders between the two groups
				while( groupSecondary.staticColliders.hasNext() ){
					
					DynamicActiveCollider dynamic = groupPrimary.dynamicColliders.get();
					StaticActiveCollider stat = groupSecondary.staticColliders.get();
					
					VisualCollisionCheck check = calculateCheck( dynamic , stat ); 
					
					CheckingPair newPair = new CustomDynamicStaticPair(
							dynamic, 
							stat, 
							customCollisionFactory, 
							check
							);
					
					newPair.addToList(activeCheckingPairs);
				}
			}
			
			return true;
			
		}
		else{ return false; } //search failed
	}

	public void addCollisionEventToGroup( String group1,  String group2 , CollisionEvent event  ){
		
		
	}
	
	public ActiveCollider addStaticCollidable( Collider collidable, String groupName ){
		
		StaticActiveCollider newStatic = new StaticActiveCollider( collidable );
		
		ColliderGroup[] foundGroup = getGroupsByName(groupName);

		if ( foundGroup.length == 1 ){ //group already exists

			newStatic.addToGroup(foundGroup[0]);
			return newStatic;

		}
		
		ColliderGroup newGroup = new ColliderGroup( groupName );
		newGroup.addToGroupList(colliderGroups);

		newStatic.addToGroup(newGroup);
		
		return newStatic;
	}
	
	
	public ActiveCollider addDynamicCollidable( Collider collidable, String groupName ){
		
		DynamicActiveCollider newDynamic = new DynamicActiveCollider( collidable );
		
		for ( ColliderGroup group : colliderGroups){
			if ( group.toString().matches(groupName) ){ //group already exists

				newDynamic.addToGroup(group);
				
				return newDynamic;
			}
			
		}
		
		ColliderGroup newGroup = new ColliderGroup( groupName );
		newGroup.addToGroupList(colliderGroups);

		newDynamic.addToGroup(newGroup);
		
		return newDynamic;
	}
	
	
	//COLLIDER ADDITION METHODS
	
	public ActiveCollider addStaticCollidableToEngineList( Collider collidable ){ //returns engine wrapper to collider composite
		
		ActiveCollider newStatic = new StaticActiveCollider( collidable );
		
		newStatic.addToGroup(ungrouped);
		//Create pairs with dynamics

		createDynamicStaticPairsWithDynamicCollidersInGroup( newStatic , ungrouped.dynamicColliders );

		
		return newStatic;
		
	}
	
	public DynamicActiveCollider addDynamicCollidableToEngineList( Collider collidable ){ 

		DynamicActiveCollider newDynamic = new DynamicActiveCollider( collidable );

		createDynamicStaticPairsWithStaticCollidersInGroup( newDynamic, ungrouped.staticColliders);
		//DYNAMIC - DYNAMIC COLLISION PAIRS
	
		createDynamicDynamicPairsWithCollidersInGroup( newDynamic, ungrouped.dynamicColliders);

		newDynamic.addToGroup(ungrouped);
		
		return newDynamic;
	}
	
	private void createDynamicStaticPairsWithDynamicCollidersInGroup( ActiveCollider newStatic , DoubleLinkedList<DynamicActiveCollider> dynamicsGroup){
		
		while ( dynamicsGroup.hasNext() ){
			
			ActiveCollider active = dynamicsGroup.get();

			if ( newStatic.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
				
				if ( active.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
					
					System.out.print("|      "+" circle-circle" );
					
					this.addActivePair( new DynamicStaticPair( active , newStatic , 
							VisualCollisionCheck.circleCircle(
									newStatic.collider.getOwnerEntity(),
									active.collider.getOwnerEntity()
							)
					));
					return;
				}
				else if ( active.collider.getBoundary().getTypeCode() == Boundary.POLYGONAL ){
					
					System.out.print("|      "+newStatic.collider.getOwnerEntity()+" static circle - dynamic polygon "+active.collider.getOwnerEntity()  );
					
					this.addActivePair( new DynamicStaticPair( active , newStatic , 
							VisualCollisionCheck.circlePoly(
									newStatic.collider.getOwnerEntity(),
									active.collider.getOwnerEntity(),
									(BoundaryPolygonal) active.collider.getBoundary() 
							)
					));
					return;
				}
				
			}

			System.err.println("|      dynamic "+ active.collider.getOwnerEntity() + " / static "+ newStatic.collider.getOwnerEntity() +" could not be paired " );
		}
	}
	
	private void createDynamicStaticPairsWithStaticCollidersInGroup( ActiveCollider newCollidable , DoubleLinkedList<StaticActiveCollider> staticsGroup){
		
		while ( staticsGroup.hasNext() ){
			
			ActiveCollider active = staticsGroup.get();

			if ( newCollidable.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
				
				if ( active.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
					
					System.out.print("|      "+newCollidable.collider.getOwnerEntity()+" dynamic circle - static circle "+active.collider.getOwnerEntity()  );
					
					this.addActivePair( new DynamicStaticPair( newCollidable , active , 
							VisualCollisionCheck.circleCircle(
									newCollidable.collider.getOwnerEntity(),
									active.collider.getOwnerEntity()
							)
					));
					return;
					
				}
				else if ( active.collider.getBoundary().getTypeCode() == Boundary.POLYGONAL ){
					
					System.out.print("|      "+newCollidable.collider.getOwnerEntity()+" dynamic circle - static polygon "+active.collider.getOwnerEntity()  );
					this.addActivePair( new DynamicStaticPair( newCollidable , active , 
							VisualCollisionCheck.circlePoly(
									newCollidable.collider.getOwnerEntity(),
									active.collider.getOwnerEntity(),
									(BoundaryPolygonal) active.collider.getBoundary() 
							)
					));
					
					return;
				}
				
			}

			System.err.println("|        "+ active.collider.getOwnerEntity() + " / "+ newCollidable.collider.getOwnerEntity() +" could not be paired24 " );

		}
	}
	
	private void createDynamicDynamicPairsWithCollidersInGroup( ActiveCollider newDynamic , DoubleLinkedList<DynamicActiveCollider> dynamicsGroup ){
	
		while ( dynamicsGroup.hasNext() ){
	
			ActiveCollider dynamic = dynamicsGroup.get();
			
			if ( dynamic.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
				
				if ( newDynamic.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
					
					System.out.print("|      "+newDynamic.collider.getOwnerEntity()+" dynamic circle - dynamic circle "+dynamic.collider.getOwnerEntity()  );
					this.addActivePair( new DynamicDynamicPair( newDynamic , dynamic , 
							VisualCollisionCheck.circleCircle(
									newDynamic.collider.getOwnerEntity(),
									dynamic.collider.getOwnerEntity() )
					));
					
					return;
				}
				else if ( newDynamic.collider.getBoundary().getTypeCode() == Boundary.POLYGONAL ){
					
					System.out.print("|      "+dynamic.collider.getOwnerEntity()+" dynamic circle - dynamic polygon "+newDynamic.collider.getOwnerEntity()  );
					this.addActivePair( new DynamicDynamicPair( newDynamic , dynamic , 
							VisualCollisionCheck.circlePoly(
									dynamic.collider.getOwnerEntity(),
									newDynamic.collider.getOwnerEntity() ,
									(BoundaryPolygonal) newDynamic.collider.getBoundary() )
					));
					
					return;
				}
				
			}
			else if ( dynamic.collider.getBoundary().getTypeCode() == Boundary.POLYGONAL ){
				
				if ( newDynamic.collider.getBoundary().getTypeCode() == Boundary.CIRCULAR ){
					
					System.out.print( "|      poly-circle" );
					this.addActivePair( new DynamicDynamicPair( newDynamic , dynamic , 
							VisualCollisionCheck.circlePoly(
									newDynamic.collider.getOwnerEntity(),
									dynamic.collider.getOwnerEntity(),
									(BoundaryPolygonal) dynamic.collider.getBoundary()
							)
					));
					return;
				}
				
			}

			System.err.println("|      "+dynamic.collider.getOwnerEntity() + " / "+ newDynamic.collider.getOwnerEntity() +" could not be paired dynamic-dynamic " );

		}
	}
	
	private VisualCollisionCheck calculateCheck( ActiveCollider collider1, ActiveCollider collider2 ){
		
		Boundary boundary1 = collider1.collider.getBoundary();
		Boundary boundary2 = collider2.collider.getBoundary();

		if ( boundary1.getTypeCode() == Boundary.POLYGONAL ){
			if ( boundary2.getTypeCode() == Boundary.POLYGONAL ){
				return VisualCollisionCheck.polyPoly();
			}
			else if ( boundary2.getTypeCode() == Boundary.CIRCULAR ){
				return VisualCollisionCheck.circlePoly(
						collider2.collider.getOwnerEntity(), 
						collider1.collider.getOwnerEntity(), 
						(BoundaryPolygonal)boundary1 
						);
			}
		}
		else if ( boundary1.getTypeCode() == Boundary.CIRCULAR ){
			if ( boundary2.getTypeCode() == Boundary.CIRCULAR ){
				return VisualCollisionCheck.circleCircle(
						collider1.collider.getOwnerEntity(), 
						collider2.collider.getOwnerEntity()
						);
			}
			else if ( boundary2.getTypeCode() == Boundary.POLYGONAL ){
				return VisualCollisionCheck.circlePoly(
						collider1.collider.getOwnerEntity(),
						collider2.collider.getOwnerEntity(), 
						(BoundaryPolygonal)boundary2 
						);
			}
		}
		
		System.err.println("Collision Engine: Failed to create VisualCollisionCheck between "+boundary1+" and "+boundary2);
		return null;

	}
	
	private void addActivePair( CheckingPair pair ){
		pair.listSlot = activeCheckingPairs.add(pair);
	}
	
	private void addInactivePair( CheckingPair pair ){
		pair.listSlot = inactiveCheckingPairs.add(pair);
	}
	
    //COLLISION ENGINE MAIN LOOP METHODS
    public void checkCollisions() { //OPTIMIZE OBSOLETE, SEE VISUAL COLLISION ENGINE

    	updateCollisions();    
        
    }
    
    public void registerCollision( CollisionBuilder<EntityStatic,EntityStatic> factory, Collider collider1 , Collider collider2, VisualCollisionCheck check ){
    
    	if (!hasActiveCollision(collider1.getOwnerEntity(),collider2.getOwnerEntity())) { 
    		Collision newCollision = factory.createVisualCollision(collider1.getOwnerEntity(), collider1, collider2.getOwnerEntity(), collider2, check, this.getBoard().renderingEngine);
			runningCollisionsList.add( newCollision );
			newCollision.initializeCollision();
		} 	
    } 
    
    @Deprecated
    public void registerDynamicStaticCollision( boolean bool , Collider collidable1 , Collider collidable2, CollisionCheck check){ //OPTIMIZE remove outdated boolean
    	
    	if ( bool ) { 
		 //check to see if collision isn't already occurring
    		//FIXME GET RID OF BOOLEAN AND <AKE ACTIVE AND INACTIVE COLLISION ARRAYS INSTEAD OF THIS MESS
    		if (!hasActiveCollision(collidable1.getOwnerEntity(),collidable2.getOwnerEntity())) { 

    			runningCollisionsList.add(new VisualCollisionRigidDynamicStatic( 
    					collidable1 , collidable2 , 
    					((VisualCollisionCheck)check).axisCollector ,
    					this.getBoard().renderingEngine
    					)); 
			} 	
    	}
    }   

    public void debugPrintCollisionList( int x, int y ,Graphics g){
    	
    	for ( int i = 0 ; i < this.runningCollisionsList.size() ; i++ ) {
	    	
	    	g.drawString( runningCollisionsList.get(i).toString() , x , y+(10*i) );
	    }
    	
    }
    
    protected BoardAbstract getBoard(){ return currentBoard; }
    
    public int debugNumberOfCollisions(){
    	return this.runningCollisionsList.size();
    }
    
    
    
    public Collider[] debugListActiveColliders(){
    	
    	ArrayList<Collider> compiledListOfColliders = new ArrayList<Collider>();

    
    	for ( ColliderGroup group : colliderGroups ){

	    	while ( group.staticColliders.hasNext() ){
	    		ActiveCollider stat = group.staticColliders.get();
	    		compiledListOfColliders.add( stat.collider );
	    	}
	    	
	    	while ( group.dynamicColliders.hasNext() ){
	    		ActiveCollider dynamic =  group.dynamicColliders.get();
	    		compiledListOfColliders.add( dynamic.collider );
	    	}
	    	
    	}
    	Collider[] returnColliders = new Collider[ compiledListOfColliders.size() ];
    	compiledListOfColliders.toArray(returnColliders);
    	return returnColliders;
    }
    
    
	public abstract class ActiveCollider{
		protected Collider collider;
		protected ArrayList<CheckingPair<?,?>> pairsList = new ArrayList<CheckingPair<?,?>>();
		protected ArrayList<ColliderGroup> groupsList = new ArrayList<ColliderGroup>();
		protected ArrayList<ListNodeTicket> groupTickets = new ArrayList<ListNodeTicket>();
		
		private Hashtable<Integer[],Integer> linkedListTest = new Hashtable<Integer[],Integer>();
		
		public ActiveCollider( Collider collider ){
			this.collider = collider;
		}

		protected abstract void addToGroup( ColliderGroup group );
		
		public abstract ActiveCollider notifyChangeToStatic(); 
		public abstract ActiveCollider notifyChangeToDynamic();
		
		public abstract void notifySetAngle( double angleDegrees );
		
		protected void dissolveAllPairs(){
			for ( CheckingPair<?,?> obsoletePair : pairsList ){
				obsoletePair.removeSelf();
			}
			pairsList.clear();
		}

		//COLLIDER NOTIFIER METHODS called from Collider 
		
		public void notifyDeactivatedCollider() {
			for ( CheckingPair<?,?> activePairs : pairsList ){
				activePairs.deactivate();
			}
		}
		
		public void notifyActivatedCollider() {
			for ( CheckingPair<?,?> inactivePairs : pairsList ){
				inactivePairs.activate();
			}
		}

		public void notifyBoundaryChange( Boundary newBoundary ) {
			dissolveAllPairs();
			this.remakePairs();
		}
		
		public void notifyRemovedCollider(){
			dissolveAllPairs();
			
			this.removeFromGroups();
		}

		protected void removeFromGroups() {
			for ( ListNodeTicket groupTicket : groupTickets ){
				groupTicket.removeSelfFromList();
			}
			this.groupsList.clear();
		}
		
		protected abstract void remakePairs();
		
	}

	private class StaticActiveCollider extends ActiveCollider{

		public StaticActiveCollider(Collider collider ) {
			super(collider);
		}
		
		protected void addToGroup( ColliderGroup group ){
			groupTickets.add( group.addStaticColliderToThisGroup(this) );
			groupsList.add(group);
		}
		
		public ActiveCollider notifyChangeToStatic(){
			return this;
		}
		
		public ActiveCollider notifyChangeToDynamic(){
			
			dissolveAllPairs(); //FIXME Allow only dynamic/statics to be removed rather than blitzing all pairs

			//removeStaticColliderFromEngineList(this); //FIXME Possible redundancy
			
			//DynamicActiveCollider newDynamic = addDynamicCollidableToEngineList( this.collider );
			
			System.out.println("CHANGING TO DYNAMIC, adding new dynamic to "+groupsList.size()+" groups");
			
			DynamicActiveCollider changedDynamic = new DynamicActiveCollider(this.collider);
			
			for ( ListNodeTicket group : groupTickets ){ //change dynamic on all groups
				group.removeSelfFromList();
			}
			for ( ColliderGroup group : groupsList ){ //change dynamic on all groups
				groupTickets.add( group.addDynamicColliderToThisGroup(changedDynamic) );
			}

			return changedDynamic;
		}

		@Override
		public void notifySetAngle(double angleDegrees) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void remakePairs() {
			
			dissolveAllPairs();
			for ( ColliderGroup group : groupsList ){ //change dynamic on all groups
				
				while ( group.groupPairs.hasNext() ){
					ColliderGroup.GroupPairWrapper pair = group.groupPairs.get();
					
					pair.notifyPairOfAddedStatic( this );
				}
			}
		}
		
	}
	
	private class DynamicActiveCollider extends ActiveCollider{

		public DynamicActiveCollider(Collider collider) {
			super(collider);
		}
		
		protected void addToGroup( ColliderGroup group ){
			groupTickets.add( group.addDynamicColliderToThisGroup(this) );
			groupsList.add(group);
		}
		
		public ActiveCollider notifyChangeToStatic(){
			
			System.err.println("Dynamic Active Collider changing to static");
			
			dissolveAllPairs(); //FIXME Allow only dynamic/statics to be removed rather than blitzing all pairs
			
			return addStaticCollidableToEngineList( this.collider ); //Re-add new static collidable
		}
		
		public ActiveCollider notifyChangeToDynamic(){
			//ALREADY DYNAMIC SO DO NOTHING
			return this;
		}

		@Override
		public void notifySetAngle(double angleDegrees) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void remakePairs() {
			
			dissolveAllPairs();
			for ( ColliderGroup group : groupsList ){ //change dynamic on all groups
				
				while ( group.groupPairs.hasNext() ){
					ColliderGroup.GroupPairWrapper pair = group.groupPairs.get();
					
					pair.notifyPairOfAddedDynamic( this );
				}
			}
		}

	}
	
	protected class DynamicTypedCollider<C extends Collider> extends DynamicActiveCollider{

		public DynamicTypedCollider(Collider collider) {
			super(collider);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	protected abstract class CheckingPair<E1 extends EntityStatic , E2 extends EntityStatic>{
		
		protected ListNodeTicket listSlot;
		protected boolean active = true;
		
		protected CollisionBuilder<EntityStatic,EntityStatic> collisionType;
		
		public void addToList( DoubleLinkedList<CheckingPair<E1,E2>> list ){
			this.listSlot = list.add( this );
		}
		
		public void removeSelf(){
			this.listSlot.removeSelfFromList();
		}

		abstract void visualCheck( MovingCamera cam, Graphics2D g2 );
		
		public void deactivate(){
			if ( active ){
				System.out.println("removing");
				listSlot.removeSelfFromList();
				CollisionEngine.this.addInactivePair( CheckingPair.this );
				active = false;
			}else{
				System.err.println( " CollisionEngine: checking pair already deactivated " );
			}
		}
		
		public void activate(){
			if ( !active ){
				System.out.println("activating");
				listSlot.removeSelfFromList();
				CollisionEngine.this.addActivePair( CheckingPair.this );
				active = true;
			}else{
				System.err.println( " CollisionEngine: checking pair already activated " );
			}
		}
		
	}
	
	protected class DynamicStaticPair<E1 extends EntityStatic , E2 extends EntityStatic> extends CheckingPair<E1,E2>{
		
		protected ActiveCollider dynamic;
		protected ActiveCollider stat;
		
		protected VisualCollisionCheck check;
		
		protected CollisionEvent eventOnDynamic;
		protected CollisionEvent eventOnStat;

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
				
				this.collisionType = CollisionBuilder.DYNAMIC_STATIC;
				System.out.println( " [RIGID dynamic static pair]");
				
			}else{
				this.collisionType = CollisionBuilder.RIGIDLESS_DYNAMIC_STATIC;
				System.out.println(" [FIELD dynamic static pair]");
			}				
		}
		
		public void visualCheck( MovingCamera cam, Graphics2D g2 ){
			
			if ( check.check( dynamic.collider, stat.collider, cam , g2) ){
				registerCollision(collisionType, dynamic.collider, stat.collider, check);
			}
			
			//registerDynamicStaticCollision(((VisualCollisionCheck)check).check(dynamic.collider, stat.collider, cam , g2), 
			//		dynamic.collider , stat.collider , this.check);
		}
		
	}
    
	protected class DynamicDynamicPair<E1 extends EntityStatic , E2 extends EntityStatic> extends CheckingPair<E1,E2>{
		private ListNodeTicket listSlot;
		
		private ActiveCollider dynamic1;
		private ActiveCollider dynamic2;
		
		private VisualCollisionCheck check;

		public DynamicDynamicPair( ActiveCollider dynamicCollider1 , ActiveCollider dynamicCollider2 , VisualCollisionCheck check ){
			this.dynamic1 = dynamicCollider1;
			this.dynamic2 = dynamicCollider2;
			this.check = check;
			dynamicCollider1.pairsList.add(this);
			dynamicCollider2.pairsList.add(this);

			//Check for rigid bodies on both entities
			if ( 
				dynamicCollider1.collider.getOwnerEntity().getRigidbody().exists() 
					&& 
				dynamicCollider2.collider.getOwnerEntity().getRigidbody().exists() 
			){
				
				this.collisionType = CollisionBuilder.DYNAMIC_STATIC;
				System.out.println(" [RIGID dynamic dynamic pair]");
			}else{
				this.collisionType = CollisionBuilder.RIGIDLESS_DYNAMIC_STATIC;
				System.out.println(" [FIELD dynamic dynamic pair]");
			}	
		}

		public void visualCheck( MovingCamera cam, Graphics2D g2 ){
			if ( check.check( dynamic1.collider, dynamic2.collider, cam , g2) ){
				registerCollision(collisionType, dynamic1.collider, dynamic2.collider, check);
			}
		}
		
	}
	
	
	protected class CustomDynamicStaticPair<E1 extends EntityStatic, E2 extends EntityStatic> extends DynamicStaticPair<E1,E2>{

		public CustomDynamicStaticPair( ActiveCollider dynamic , ActiveCollider stat , CollisionBuilder<EntityStatic, EntityStatic> builder ,VisualCollisionCheck check ){
			super(dynamic, stat, check);
				
			this.collisionType = builder;
			
			System.out.println("[CUSTOM]");
		}
		
		public void visualCheck( MovingCamera cam, Graphics2D g2 ){
			
			if ( check.check( dynamic.collider, stat.collider, cam , g2) ){
				registerCollision(collisionType, dynamic.collider, stat.collider, check);
			}
		}
		
	}
	
	
	protected class ColliderGroup{
		
		protected DoubleLinkedList<StaticActiveCollider> staticColliders = new DoubleLinkedList<StaticActiveCollider>();
		protected DoubleLinkedList<DynamicActiveCollider> dynamicColliders = new DoubleLinkedList<DynamicActiveCollider>();

		protected DoubleLinkedList<GroupPairWrapper> groupPairs = new DoubleLinkedList<GroupPairWrapper>();
		
		protected String name;
		protected int index;
		
		public ColliderGroup( String name ){
			this.name = name;
		}

		public boolean willAcceptEntity(EntityStatic entity) {
			return true;
		}

		public void addToGroupList( ArrayList<ColliderGroup> list ){
			index = list.size();
			list.add(this);
		}
		
		public ListNodeTicket addGroupToPair( GroupPair pair, byte primarySecondary ){
			
			return this.groupPairs.add( new GroupPairWrapper( pair , primarySecondary ) );
		}

		public ListNodeTicket addStaticColliderToThisGroup( StaticActiveCollider newStatic ){

			while ( groupPairs.hasNext() ){
				GroupPairWrapper wrappedPair = groupPairs.get();
				wrappedPair.notifyPairOfAddedStatic(newStatic);
			}
			
			return staticColliders.add(newStatic);
		}
		
		public ListNodeTicket addDynamicColliderToThisGroup( DynamicActiveCollider dynamic ){
			
			while ( groupPairs.hasNext() ){
				GroupPairWrapper wrappedPair = groupPairs.get();
				wrappedPair.notifyPairOfAddedDynamic(dynamic);
			}
			
			return dynamicColliders.add(dynamic);
		}
		
		public void deactivateColliderGroup(){
			
		}
		
		public String[] debugListPartnerGroups(){
			String[] returnStrings = new String[ this.groupPairs.size() ];
			int i = 0;
			while( groupPairs.hasNext() ){
				
				GroupPairWrapper wrappedPair = groupPairs.get();
				
				returnStrings[i] = wrappedPair.pair.pairedGroupToString(wrappedPair.primarySecondary) ;
				i++;
			}
			return returnStrings;
		}
		
		public String[] debugListGroupedStatics(){
			String[] returnStrings = new String[ this.staticColliders.size() ];
			int i = 0;
			while( staticColliders.hasNext() ){
				returnStrings[i] = staticColliders.get().collider.getOwnerEntity().name;
				i++;
			}
			return returnStrings;
		}
		
		public String[] debugListGroupedDynamics(){
			String[] returnStrings = new String[ this.dynamicColliders.size() ];
			int i = 0;
			while( dynamicColliders.hasNext() ){
				returnStrings[i] = dynamicColliders.get().collider.getOwnerEntity().name;
				i++;
			}
			return returnStrings;
		}

		public boolean isTypeRestricted() {
			return false;
		}
		
		@Override
		public String toString(){
			return this.name;
		}
		
		protected class GroupPairWrapper{
			private GroupPair pair;
			private byte primarySecondary;
			public GroupPairWrapper( GroupPair pair, byte primarySecondary){
				this.pair = pair;
				this.primarySecondary = primarySecondary;
			}
			public void notifyPairOfAddedStatic( StaticActiveCollider addedStatic ){
				pair.notifyPairOfStaticAddedToGroup(addedStatic, primarySecondary);
			}
			public void notifyPairOfAddedDynamic( DynamicActiveCollider addedDynamic ){
				pair.notifyPairOfDynamicAddedToGroup(addedDynamic, primarySecondary);
			}
		}
		
	}
	
	public class ColliderGroupTyped<E extends EntityStatic> extends ColliderGroup{
		
		public ColliderGroupTyped(String name ) {
			super(name);
		}

		@Override
		public boolean isTypeRestricted() {
			return true;
		}
		
	}
	
	protected class GroupPair{
		
		private ColliderGroup[] groups;
		
		private ListNodeTicket[] groupPosition;
		
		private CollisionBuilder<?,?> builder;
		
		public GroupPair( ColliderGroup group1 , ColliderGroup group2, CollisionBuilder<?,?> collisionBuilder ){
			this.groups = new ColliderGroup[] { group1 , group2 };
			this.builder = collisionBuilder;
			
			groupPosition = new ListNodeTicket[]{ 
					group1.addGroupToPair(this,(byte)0) , 
					group2.addGroupToPair(this,(byte)1) 
					};
			
			init();
			
		}
		
		private void init(){
			
		}
		
		public void notifyPairOfStaticAddedToGroup( StaticActiveCollider addedStatic , byte groupIndex ){

			ArrayList<CheckingPair> returnPairs = new ArrayList<CheckingPair>();

			while ( groups[1-groupIndex].dynamicColliders.hasNext() ){				// (1 - group) is basically boolean 0/1 !int 
				DynamicActiveCollider dynamic = groups[1-groupIndex].dynamicColliders.get();
				
				VisualCollisionCheck check = calculateCheck( dynamic , addedStatic ); 
				
				CheckingPair newPair = new CustomDynamicStaticPair(
						dynamic, 
						addedStatic, 
						this.builder, 
						check 
						);
				
				newPair.addToList(activeCheckingPairs);
			}
		}
		
		public void notifyPairOfDynamicAddedToGroup( DynamicActiveCollider addedDynamic , byte groupIndex ){

			if ( groupIndex == 0 ){  //THIS CHECK IS TO SWITCH PRIMARY SECONDARY BILLING FOR DYNAMIC DYNAMIC COLLISIONS
				
				while ( groups[1].dynamicColliders.hasNext() ){	
					
					DynamicActiveCollider dynamic = groups[1-groupIndex].dynamicColliders.get();
					
					VisualCollisionCheck check = calculateCheck( dynamic , addedDynamic ); 
					
					CheckingPair newPair = new CustomDynamicStaticPair( //DYNAMIC ADDED TO GROUP 0 GOES PRIMARY
							addedDynamic, 
							dynamic, 
							this.builder, 
							check
							);
					
					newPair.addToList(activeCheckingPairs);
				}
				
			}else{ 
			
				while ( groups[0].dynamicColliders.hasNext() ){ //DYNAMIC ADDED TO GROUP 1 GOES SECONDARY

					DynamicActiveCollider dynamic = groups[1-groupIndex].dynamicColliders.get();

					VisualCollisionCheck check = calculateCheck( dynamic , addedDynamic ); 

					CheckingPair newPair = new CustomDynamicStaticPair(
							dynamic, 
							addedDynamic, 
							this.builder, 
							check
							);

					newPair.addToList(activeCheckingPairs);
				}

			}
			
			while ( groups[1-groupIndex].staticColliders.hasNext() ){				//AND THEN STATICS ALL GO SECONDARY
				StaticActiveCollider stat = groups[1-groupIndex].staticColliders.get(); //  (1-groupIndex) is basically boolean ! not

				VisualCollisionCheck check = calculateCheck( stat , addedDynamic ); 	// operation on an int between 0 and 1

				CheckingPair newPair = new CustomDynamicStaticPair(
						addedDynamic, 
						stat, 
						this.builder, 
						check 
						);

				newPair.addToList(activeCheckingPairs);
			}
			
		}
		
		public String pairedGroupToString( int primarySecondary){
			return this.groups[primarySecondary].toString();
		}
		
		public String debugBuilderToString(){
			return this.builder.toString();
		}
		
	}
    
	    
}
	   


