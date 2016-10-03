package testEntities;

import entities.EntityStatic;

public class Ground extends EntityStatic{
	


	public Ground(int x, int y, String path) {
		super(x, y);

		initPlatform(x,y,path);
		setBoundingBox(-152,-5,304,10);
		name = "Ground"+ count;
	}

	
	private void initPlatform(int x , int y, String path){
    	offsetX = -152;
    	offsetY = -5;
    	loadSprite(path,offsetX,offsetY);		
	}
	
	public String toString() {
		return String.format(name);
	}
	
}
