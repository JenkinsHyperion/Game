package entityComposites;

import java.util.ArrayList;

public abstract class ParentComposite implements EntityComposite {

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

}
