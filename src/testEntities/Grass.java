package testEntities;

import entities.EntityStatic;

//generic static sprite like a grass
public class Grass extends EntityStatic{
	


	public Grass(int x, int y, String path) {
		super(x, y);
			collidable = false;
    		loadSprite(path);		
			setBoundingBox(0,0,0,0);
			name = "Grass"+count;
	}

	public String toString() {
		return String.format(name);
	}
	

	
}
