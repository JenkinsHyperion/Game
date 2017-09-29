package entityComposites;

import utility.ListNodeTicket;

public class LifespanComposite implements EntityComposite, UpdateableComposite {
	protected String compositeName = "LifespanComposite";
	ListNodeTicket updaterSlot;
	
	private int lifespan;
	
	private int ownerEntityIndex;
	
	protected LifespanComposite( int lifespan ){
		this.lifespan = lifespan;
	}
	
	@Override
	public void updateEntityWithComposite(EntityStatic entity) {
		if ( lifespan == 0 ){
			entity.disable();
		}
		else{
			lifespan-- ;
		}
	}

	@Override
	public void updateComposite() {
		
	}

	@Override
	public void removeThisUpdateableComposite() {
		this.updaterSlot.removeSelfFromList();
	}

	@Override
	public boolean addUpdateableCompositeTo(EntityStatic owner) {
		owner.addUpdateableCompositeToEntity(this);
		return true;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public void disableComposite() {
		this.updaterSlot.removeSelfFromList();
	}
	@Override
	public void setCompositeName(String newName) {
		this.compositeName = newName;
	}
	@Override
	public String getCompositeName() {
		return this.compositeName;		
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	@Override
	public EntityStatic getOwnerEntity() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setUpdateablesIndex(int index) {
		this.ownerEntityIndex = index;
	}

	@Override
	public void decrementIndex() {
		this.ownerEntityIndex--;
	}

}
