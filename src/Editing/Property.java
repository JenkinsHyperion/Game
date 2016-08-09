package Editing;

import java.lang.reflect.Method;

import entities.Entity;

public class Property {

	/* This class determines what variables and methods can be accessed by the editor and under what circumstances.
	 * To be put into properties list, probably another class 'Properties' that will be made for each type of entity.
	 * I'm not sure where this "properties" object would go, definitely not in each instance of every entity since it belongs
	 * to the class as a whole, not individual objects. 
	 * It might actually just go in the editor system.
	 */
	
	private String propertyname;
	private Method getter;
	private Method setter;
	
	//I'm leaving Class<> in case something other than entity needs properties, otherwise just Entites get properties for now
	public Property(Class<Entity> owner , String name , Method getMethod , Method setMethod ) {
		
		/* I didn't even know this was possible and you or me should probably check if theres a better way to set this up.
		 *  But as I see it each property should go through the get and set methods of the entity, especially the set method 
		 *  to avoid the Editor being able to give invalid values. Maybe the property object can hold additional parameters like 
		 *  what value range is allowed 
		 *  
		 */
		getter = getMethod;
		setter = setMethod;
		
		propertyname = name;
		
		initProperty();
	}
	
	private void initProperty(){
		
	}

	
	public String getPropertyName(){
		return propertyname;
	}
	
}
