package entityComposites;

import engine.BoardAbstract;
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
	public void updateEntity(EntityStatic entity) {
		this.updateOwner(entity);
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
	public void removeUpdateable() {
		this.updaterSlot.removeSelf();
	}
	
}
