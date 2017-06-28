package entityComposites;

public interface RotateableComposite {

	/**
	 * If this Composite has been added to an AngularComposite's rotateables list, this method will be called every time the angle is set.
	 * @param angleRadians
	 */
	public void setAngle( double angleRadians ); 
	
	public void addAngle( double angleRadians );
}
