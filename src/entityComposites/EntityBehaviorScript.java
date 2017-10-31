package entityComposites;

import java.awt.Point;
import java.awt.geom.Point2D;

import entities.EntityDynamic;
import misc.FollowMovement;
import misc.MovementBehavior;
import physics.Vector;
import utility.ListNodeTicket;

public abstract class EntityBehaviorScript implements UpdateableComposite{

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

		private FollowMovement[] movement = new FollowMovement[2];
		
		private Point2D.Double[] targetPositions = new Point2D.Double[2];
		
		private byte currentIndex = 0;
		
		public PatrolBetween( EntityStatic owner , EntityStatic target1, EntityStatic target2){
			movement[0] = new FollowMovement.Linear(owner, target1);
			movement[1] = new FollowMovement.Linear(owner, target2);
			targetPositions[0] = target1.getPositionReference();
			targetPositions[1] = target2.getPositionReference();
		}
		public PatrolBetween( EntityStatic owner , EntityStatic target1, Point target2){
			movement[0] = new FollowMovement.Linear(owner, target1);
			movement[1] = new FollowMovement.Linear(owner, target2);
			targetPositions[0] = target1.getPositionReference();
			targetPositions[1] = new Point2D.Double(target2.x,target2.y);
		}
		

		@Override
		protected void updateOwnerEntity(EntityStatic ownerEntity) {
			
			movement[currentIndex].updateAIPosition();
			
			if( ownerEntity.getSeparationVector( targetPositions[currentIndex] ).getMagnitude() < 10 ){
				
				currentIndex = (byte)( 1 - currentIndex ); //flip between 0 and 1
			}
		}
		
	}
	
	public static class LinearFollow2 extends EntityBehaviorScript{
		
		private EntityStatic target;

		public LinearFollow2(EntityStatic owner, EntityStatic target){
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
		
		private Point2D.Double targetPosition;

		public QuadraticFollow(EntityStatic owner, EntityStatic target){
			this.ownerEntity = owner;

			targetPosition = target.getPositionReference();
		}
		
		public QuadraticFollow(EntityStatic owner, Point target){
			this.ownerEntity = owner;

			targetPosition = new Point2D.Double(target.x,target.y);
		}

		@Override
		protected void updateOwnerEntity(EntityStatic ownerEntity) {
			Vector separation = ownerEntity.getSeparationVector( new Point( (int)targetPosition.getX() ,(int)targetPosition.getY() ) );
			double distance = separation.getMagnitude();
			ownerEntity.getTranslationComposite().setVelocityVector( separation.multiply( distance/5000 ) );
		}
		
	}
	
}
