package testEntities;

import entities.EntityStatic;

public class Ground extends EntityStatic{
	


	public Ground(int x, int y, String path) {
		super(x, y);
			
			initPlatform(x,y,path);
			setBoundingBox(-223,-46,446,100);
			name = "Ground";
	}

	
	private void initPlatform(int x , int y, String path){
    	loadSprite(path,-223,-50);		
	}
	
	public String toString() {
		return String.format(name);
	}
	
}
