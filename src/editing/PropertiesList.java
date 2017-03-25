package editing;


import java.util.ArrayList;
import entities.*;
import entityComposites.Entity;
public class PropertiesList {
	/* WILL USE THESE LATER ON WHEN THERE ARE MORE PROPERTIES TO HANDLE
	private ArrayList entityStaticPropsList; // make array list
	private ArrayList entityDynamicPropsList;
	private ArrayList entityPhysicsPropsList;
	*/
	
	//private Property prop;
	private ArrayList<Property> propertiesList;
	private String[] propertiesAsString;
	
	public PropertiesList(Entity ent) {
		propertiesList = new ArrayList<>();
		initProperties(ent);
//		propertiesAsString = new String[]{"Collidable","XPosition", "Y-Position", "Name","Entity Type"};
		propertiesAsString = new String[propertiesList.size()];
		//first element
		for (int i = 0; i < propertiesList.size(); i++) {
			propertiesAsString[i] = propertiesList.get(i).getPropertyName();
		}
		/*initStaticProperties();
		initDynamicProperties();
		initPhysicsProperties(); */	
	}


/*
	public ArrayList<Property> getPropertiesOfType(String type) {
		if (type == "EntityStatic"){
			//return entityStaticProperties;
		}
		else if (type == "EntityDynamic"){
			
		}
		else if (type == "EntityPhysics"){
			
		}
		else return null;
	}
*/	
	private void initProperties(Entity ent) {
		//populate propertiesList with blank properties
		propertiesList.add(new Property(ent, Property.COL_STATE, "Collidable"));
		propertiesList.add(new Property(ent, Property.XPOS, "X-Position"));
		propertiesList.add(new Property(ent, Property.YPOS, "Y-Position"));
		propertiesList.add(new Property(ent, Property.ENTNAME, "Entity name"));
		propertiesList.add(new Property(ent, Property.ENTTYPE, "Entity type"));
	}
	
	//will use this entire section later when I can think of
	// more type-related properties these entities would share.
	/*
	private void initStaticProperties() {
		entityStaticPropsList = null;
	}
	private void initDynamicProperties() {
		// TODO Auto-generated method stub
		entityDynamicPropsList = null;
	}
	private void initPhysicsProperties() {
		// TODO Auto-generated method stub
		entityPhysicsPropsList = null;
	} */
	public String[] getPropertiesAsString() {
		return propertiesAsString;
	} 
	/**
	 * 
	* @param propType Must choose Property.BOOLEAN, Property.POS, or Property.TEXT
	*/
	public Property getProperty(int index) {
		/*
		if (i < 0 || i > propertiesList.size())
			throw new ArithmeticException("Chose option out of range of array");
		else
			return this.propertiesList.get(i);
		*/
		//OR, different way of handling this
		try {
			return propertiesList.get(index);
		}
		catch (ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return null;
		}
	}
	public ArrayList<Property> getPropertiesList(){
		return propertiesList;
	}

}
