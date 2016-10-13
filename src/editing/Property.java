package editing;

import entities.*;

public class Property {

	/* This class determines what variables and methods can be accessed by the editor and under what circumstances.
	 * To be put into properties list, probably another class 'Properties' that will be made for each type of entity.
	 * I'm not sure where this "properties" object would go, definitely not in each instance of every entity since it belongs
	 * to the class as a whole, not individual objects. 
	 * It might actually just go in the editor system.
	 */
	
	//private String propertyType;
	private final String entityType;
	private int propertyType;
	private String propertyName;
	private String entityName;
	private int xpos;
	private int ypos;
	private boolean collidable;
	
	//IMPORTANT: MAKE AN UPDATER FUNCTION TO SET EACH OF THIS PROPERTY'S COPIES
	// OF THEIR PROPERTY VARIABLES. THIS UPDATER WILL BE CALLED EVERY TIME A SET() METHOD RUNS.
	// MAY BE COMPLICATED WHEN THE SLIDERS ARE USED, KEEP THIS IN MIND
	
	//TO DO: 
	// Make the PropertiesList object permanent while the editor is open
	// and populate it by scanning through every entity,
	// rather than it only being alive while the current entity's window is open.
	
	//set some final constants just like Swing components do.
	//these constants indicate what kind of property this instance is.
	public final static int COL_STATE = 0; //collidable state
	public final static int XPOS = 1;
	public final static int YPOS = 2;
	public final static int ENTNAME = 3;
	public final static int ENTTYPE = 4;
	
	

	//I'm leaving Class<> in case something other than entity needs properties, otherwise just Entites get properties for now
	/**
	 * 
	 * @param ent the current selected entity
	 * @param propType must choose Property.BOOLEAN, Property.POS, or Property.TEXT
	 */
	public Property(Entity ent, int propType, String propName) {
			entityType = ent.getClass().getSimpleName();
			this.propertyName = propName;
			if (propType == COL_STATE){
				this.propertyType = Property.COL_STATE;
				this.collidable = ent.isCollidable();
			}
			else if (propType == XPOS){
				this.propertyType = Property.XPOS;
				this.xpos = ent.getX();
			}
			else if (propType == YPOS){
				this.propertyType = Property.YPOS;
				this.ypos = ent.getY();
			}
			else if (propType == ENTNAME) {
				this.propertyType = Property.ENTNAME;
				this.entityName = ent.name;
			}
			else if (propType == ENTTYPE) {
				this.propertyType = Property.ENTTYPE;
			}
			else {
				throw new ArithmeticException("Did not enter one of the three property type codes");
			}
			/*
			this.collidable = owner.isCollidable();
			this.xpos = owner.getX();
			this.ypos = owner.getY();
			this.entityName = owner.name;
			this.entityType = getEntityType(owner);
			//Integer.class.isInstance(this.xpos);
			*/
		/* I didn't even know this was possible and you or me should probably check if theres a better way to set this up.
		 *  But as I see it each property should go through the get and set methods of the entity, especially the set method 
		 *  to avoid the Editor being able to give invalid values. Maybe the property object can hold additional parameters like 
		 *  what value range is allowed 
		 *  
		 */
		
	}
	public int getPropertyType(){
		return this.propertyType;
	}
	public String getPropertyName(){
		return this.propertyName;
	}
	public String getEntityType(Entity ent) {
		return ent.getClass().getSimpleName();		
	}
	public String getEntityName(Entity ent) {
		return ent.name;
	}
	public int getEntityXpos(Entity ent){
		return ent.getX();
	}
	public int getEntityYpos(Entity ent){
		return ent.getY();
	}
	public boolean getEntityCollidableState(Entity ent) {
		return ent.isCollidable();
	}
	
	
	//(update: ignore this block) 
	// #### SETTERS #####
	//	If one of these properties is set, (currently from the PropertiesList class), 
	//	 then it will set this property's type to that type of value. This works because
	// 	 there should only be one current property held by each property object.
	// -These types will be used in the GUI drawing classes to signify what type of window to show.
	//	 (eg. "pos" for position, will have sliders, "bool" for true/false radio buttons, etc.
	public void setEntityName(Entity ent, String name) {
		ent.name = name;
		this.entityName = name;
		//propertyType = "text";
	}
	/* Propbably won't use this one
	public void setPropertyName( String name) {
		this.propertyName = name;
		//propertyType = "text";
	} */
	public void setEntityXpos(Entity ent, int x){
		ent.setX(x);
		this.xpos = x;	
	}
	public void setEntityYpos(Entity ent, int y){
		ent.setY(y);
		this.ypos = y;
	}
	public void setEntityCollidableState(Entity ent, boolean state) {
		ent.setCollidable(state);
		this.collidable = state;
	}
	public String toString() {
		return this.propertyName;
	}

}
