package entityComposites;

import java.awt.Point;

import entities.EntityDynamic;
import physics.Vector;
import utility.ListNodeTicket;

public abstract class EntityBehaviorScript implements UpdateableComposite{

	private int updaterIndex;
	protected EntityStatic ownerEntity;
	
	protected abstract void updateBehavior();
	protected abstract void updateOwnerEntity(EntityStatic ownerEntity);
	
	@Override
	public void updateEntityWithComposite(EntityStatic entity) { //renaming method for clarity when making anonymous scripts
		updateOwnerEntity(ownerEntity);
	}
	@Override
	public void updateComposite() { //renaming method for clarity when making anonymous scripts
		updateBehavior();
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

	
	public static class LinearFollow extends EntityBehaviorScript{
		
		private EntityStatic target;

		public LinearFollow(EntityStatic owner, EntityStatic target){
			this.ownerEntity = owner;
			this.target = target;
		}

		@Override
		protected void updateBehavior() {
			
			this.ownerEntity.getTranslationComposite().setDX( (float)( this.target.getX() - this.ownerEntity.getX() ) /30 );
			this.ownerEntity.getTranslationComposite().setDY( (float)( this.target.getY() - this.ownerEntity.getY() ) /30 );
		}

		@Override
		protected void updateOwnerEntity(EntityStatic ownerEntity) {
			
		}
		
	}
	
	public static class QuadraticFollow extends EntityBehaviorScript{
		
		private Double targetPosition;

		public QuadraticFollow(EntityStatic owner, EntityStatic target){
			this.ownerEntity = owner;

			this.targetPosition = new Double(target.x);
		}

		@Override
		protected void updateBehavior() {

			
			//Vector separation = ownerEntity.getSeparationVector( new Point( targetPosition[0].intValue() ,targetPosition[1].intValue() ) );
			//double distance = separation.getMagnitude();
			//ownerEntity.getTranslationComposite().setVelocityVector( separation.multiply( distance/5000 ) );
		}

		@Override
		protected void updateOwnerEntity(EntityStatic ownerEntity) {
			
		}
		
	}
	
}
