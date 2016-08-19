package Editing;

import java.util.ArrayList;

public class PropertiesList {

	private final ArrayList EntityStaticProperties = null; // make array list
	private Property[] properties;
	
	public PropertiesList() {
		initProperties();
	}

	/**
	 * 
	 * @param type
	 */
	public Property[] getPropertiesOfType(String type) {
		if (type == "EntityStatic"){
			return EntityStaticProperties;
		}
		else return null;
	}
	
	private void initProperties() {

	}
	
}
