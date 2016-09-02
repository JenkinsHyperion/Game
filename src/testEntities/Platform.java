package testEntities;

import entities.EntityStatic;

public class Platform extends EntityStatic{

	public Platform(int x, int y, String path) {
		super(x, y);
		initPlatform(x,y,path);
		setBoundingBox(-20,-3,40,6);
		name = "Platform"+count;
	}

	
	private void initPlatform(int x , int y, String path){
    	loadSprite(path,-20,-3);		
	}
	
	public String toString() {
		return String.format(name);
	}
}
