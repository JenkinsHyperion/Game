package testEntities;

import entities.EntityStatic;

public class Platform extends EntityStatic{

	public static final String PF1 = "platform.png";
	public static final String PF2 = "platform02.png";
	
	public Platform(int x, int y, String path) {
		super(x, y);
		initPlatform(x,y,path);
		setBoundingBox(-20,-3,40,6);
		name = "Platform"+count;
	}

	
	private void initPlatform(int x , int y, String path){
    	offsetX = -20;
    	offsetY = -3;
		
		loadSprite(path, offsetX, offsetY);		
	}
	
	public String toString() {
		return String.format(name);
	}
}
