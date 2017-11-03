package entityComposites;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.security.InvalidParameterException;

import entities.EntityDynamic;
import misc.FollowMovement;
import misc.MovementBehavior;
import physics.Vector;
import utility.ListNodeTicket;

public abstract class EntityBehaviorScript implements UpdateableComposite{
	protected Point playerPointRef;
	private int updaterIndex;
	protected EntityStatic ownerEntity;
	
	protected abstract void updateOwnerEntity(EntityStatic ownerEntity);
	
	@Override
	public void updateEntityWithComposite(EntityStatic entity) { //renaming method for clarity when making anonymous scripts
		updateOwnerEntity(entity);
	}
	
	@Override
	public boolean addUpdateableCompositeTo(EntityStatic owner) {
		this.updaterIndex = owner.addUpdateableCompositeToEntity(this);
		return true;
	}
	
	@Override
	public void removeThisUpdateableComposite() {
		ownerEntity.removeUpdateableCompositeFromEntity(updaterIndex);
	}
	
	@Override
	public void decrementIndex() {
		this.updaterIndex-- ;
	}

	@Override
	public void setUpdateablesIndex(int index) {
		this.updaterIndex = index;
	}
	
	public static class PatrolBetween extends EntityBehaviorScript{

		private FollowMovement[] movement;
		
		private Point[] targetPositions;
		
		private byte currentIndex = 0;
		
		private Point input;
		
		public PatrolBetween( EntityStatic owner , EntityStatic target1, EntityStatic target2){
			movement = new FollowMovement[2];
			targetPositions = new Point[2];
			movement[0] = new FollowMovement.Linear(owner, target1);
			movement[1] = new FollowMovement.Linear(owner, target2);
			targetPositions[0] = target1.getPositionReference();
			targetPositions[1] = target2.getPositionReference();
		}
		public PatrolBetween( EntityStatic owner , EntityStatic target1, Point target2){
			movement = new FollowMovement[2];
			targetPositions = new Point[2];
			movement[0] = new FollowMovement.Linear(owner, target1);
			movement[1] = new FollowMovement.Linear(owner, target2);
			targetPositions[0] = target1.getPositionReference();
			targetPositions[1] = new Point(target2.x,target2.y);
		}
		public PatrolBetween( EntityStatic owner, Point...targets){
			if ( targets.length < 2 )
				throw new InvalidParameterException("Patrol must have more than one target Point");
			
			targetPositions = new Point[targets.length];
			for ( int i = 0 ; i < targets.length ; i++ ){
				targetPositions[i] = targets[i];
			}
			
			movement = new FollowMovement[targets.length];
			for ( int i = 0 ; i < targets.length ; i++ ){
				movement[i] =  new FollowMovement.Linear(owner,targets[i]);
			}
		}
		

		@Override
		protected void updateOwnerEntity(EntityStatic ownerEntity) { //TODO add flags for looping / ping/pong paths
			
			movement[currentIndex].updateAIPosition();
			
			if( ownerEntity.getSeparationVector( targetPositions[currentIndex] ).getMagnitude() < 10 ){
				
				if ( currentIndex < movement.length-1 ){
					currentIndex++;
				}else{
					currentIndex=0;
				}
			}
		}
		
	}
	
	public static class LinearFollowBehavior extends EntityBehaviorScript{
		
		private EntityStatic target;

		public LinearFollowBehavior(EntityStatic owner, EntityStatic target){
			this.ownerEntity = owner;
			this.target = target;
		}

		@Override
		protected void updateOwnerEntity(EntityStatic ownerEntity) {
			this.ownerEntity.getTranslationComposite().setDX( (float)( this.target.getX() - this.ownerEntity.getX() ) /30 );
			this.ownerEntity.getTranslationComposite().setDY( (float)( this.target.getY() - this.ownerEntity.getY() ) /30 );
		}
		
	}
	
	public static class QuadraticFollow extends EntityBehaviorScript{
		
		private Point targetPosition;

		public QuadraticFollow(EntityStatic owner, EntityStatic target){
			this.ownerEntity = owner;

			targetPosition = target.getPositionReference();
		}
		
		public QuadraticFollow(EntityStatic owner, Point target){
			this.ownerEntity = owner;

			targetPosition = new Point(target.x,target.y);
		}

		@Override
		protected void updateOwnerEntity(EntityStatic ownerEntity) {
			Vector separation = ownerEntity.getSeparationVector( new Point( (int)targetPosition.getX() ,(int)targetPosition.getY() ) );
			double distance = separation.getMagnitude();
			ownerEntity.getTranslationComposite().setVelocityVector( separation.multiply( distance/5000 ) );
		}
		
	}
	
}
