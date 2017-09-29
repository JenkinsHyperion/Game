package entityComposites;

import utility.ListNodeTicket;

public abstract class EntityScript implements UpdateableComposite{

	private ListNodeTicket updaterSlot;
	
	protected abstract void updateOwner( EntityStatic ownerEntity );
	
	protected abstract void updateScript();
	
	@Override
	public void updateComposite() {
		this.updateScript();
	}
	
	@Override
	public void updateEntityWithComposite(EntityStatic entity) {
		this.updateOwner(entity);
	}
	
	@Override
	public boolean addUpdateableCompositeTo(EntityStatic owner) {
		owner.addUpdateableCompositeToEntity(this);
		return true;
	}
	
	@Override
	public void removeThisUpdateableComposite() {
		this.updaterSlot.removeSelfFromList();
	}
	
}
