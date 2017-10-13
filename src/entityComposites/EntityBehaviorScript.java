package entityComposites;

import utility.ListNodeTicket;

public abstract class EntityBehaviorScript implements UpdateableComposite{

	private int updaterIndex;
	private EntityStatic ownerEntity;
	
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
	
}
