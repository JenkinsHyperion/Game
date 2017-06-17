package entityComposites;

public abstract class ParentChildRelationship implements EntityComposite {

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
