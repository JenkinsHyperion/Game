package entityComposites;

public interface EntityComposite {

	public boolean exists();
	
	public void disable();
	
	public void setCompositeName(String newName);
	public String getCompositeName();
}
