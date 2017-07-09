package entityComposites;

import engine.BoardAbstract;
import utility.ListNodeTicket;

public class LifespanComposite implements EntityComposite, UpdateableComposite {
	protected String compositeName = "LifespanComposite";
	ListNodeTicket updaterSlot;
	
	private int lifespan;
	
	protected LifespanComposite( int lifespan ){
		this.lifespan = lifespan;
	}
	
	@Override
	public void updateEntity(EntityStatic entity) {
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
	public void removeUpdateable() {
		this.updaterSlot.removeSelf();
	}

	@Override
	public boolean addCompositeToUpdater(BoardAbstract board) {
		if ( this.updaterSlot == null ){
    		this.updaterSlot = board.addCompositeToUpdater(this);
    		return true;
    	}
    	else{
    		return false;
    	}
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public void disable() {
		this.updaterSlot.removeSelf();
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

}
