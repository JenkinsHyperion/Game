package testEntities;

import entities.EntityStatic;

public class Ground extends EntityStatic{
	


	public Ground(int x, int y, String path) {
		super(x, y);
			
			initPlatform(x,y,path);
			setBoundingBox(0,0,303,10);
			name = "Ground";
	}

	
	private void initPlatform(int x , int y, String path){
    	loadSprite(path);		
	}
	
	public String toString() {
		return String.format(name);
	}
	
}
