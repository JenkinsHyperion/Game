package entityComposites;

public interface EntityComposite {

	public boolean exists();
	
	public void disableComposite();
	public EntityStatic getOwnerEntity();
	public void setCompositeName(String newName);
	public String getCompositeName();

}
