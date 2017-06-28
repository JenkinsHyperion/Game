package entityComposites;

import java.util.ArrayList;

public abstract class ParentComposite implements EntityComposite {
	protected String compositeName = "ParentComposite";
	protected ArrayList<ChildComposite> children = new ArrayList<ChildComposite>();
	
	public abstract void manipulateChildren();
	
	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub
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
