package entityComposites;

public class ParentTranslatableComposite extends ParentComposite {
	protected String compositeName = "ParentRotateableComposite";
	@Override
	public void manipulateChildren() {
		
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
	public void setCompositedPosition(double x, double y) {
		// TODO Auto-generated method stub
		
	}
}
