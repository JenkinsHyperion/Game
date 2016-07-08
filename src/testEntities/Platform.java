package testEntities;

import entities.EntityStatic;

public class Platform extends EntityStatic{

	public Platform(int x, int y, String path) {
		super(x, y);
		initPlatform(x,y,path);
		setBoundingBox(0,0,32,4);
		name = "Platform"+count;
	}

	
	private void initPlatform(int x , int y, String path){
    	loadSprite(path);		
	}
	
	public String toString() {
		return String.format(name);
	}
}
