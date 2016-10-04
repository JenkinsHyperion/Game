package testEntities;

import entities.EntityStatic;

//generic static sprite like a grass
public class Grass extends EntityStatic{
	
	public Grass(int x, int y, String path) {
		super(x, y);
			collidable = false;	
			this.offsetX = 0;
			this.offsetY = 0;
			loadSprite(path, offsetX, offsetY);	
			setBoundingBox(0,0,0,0);
			name = "Grass"+count;
	}
	public Grass(int x, int y, int offsetX, int offsetY, String path) {
		super(x, y);
		collidable = false;	
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		loadSprite(path, offsetX, offsetY);	
		setBoundingBox(0,0,0,0);
		name = "Grass"+count;
	}
	public String toString() {
		return String.format(name);
	}
	

	
}
