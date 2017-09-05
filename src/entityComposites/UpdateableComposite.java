package entityComposites;

import engine.BoardAbstract;

public interface UpdateableComposite {

	/**Entities being updated with this composite, call upon this method's functionality to do so. An example would be a Translation Composite  
	 * applying change in the Entity's position, with it's own dx and dy velocity fields.
	 * @param entity
	 */
	public void updateEntity( EntityStatic entity );
	
	/**Overriding methods in concrete classes will be called by the updater thread every frame if they are in the updater
	 * thread's list of updateables. Be sure that the factory methods adding a concrete updateable composite also adds it to the updater thread
	 * BE ADVISED: CHILDREN ENTITIES SHOULD ALWAYS CALL super.updateComposite().  
	 */
	public void updateComposite();
	
	/**Overriding methods should call on the concrete updateable composite's Ticket.removeSelf() method to remove itself from the updater thread
	 * 
	 */
	public void removeThisUpdateable();

	
	boolean addCompositeToUpdater(BoardAbstract board);
	
	void setUpdateablesIndex( int index );
	
}
