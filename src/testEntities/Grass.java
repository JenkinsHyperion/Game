package testEntities;

import entities.EntityStatic;

//generic static sprite like a grass
public class Grass extends EntityStatic{
	
	public Grass(int x, int y, String path) {
		super(x, y);
			collidable = false;	
			this.entitySprite.setOffset(0, 0); 
			loadSprite(path, 0, 0);	
			setBoundingBox(0,0,0,0);
			name = "Grass"+count;
	}
	public Grass(int x, int y, int offsetX, int offsetY, String path) {
		super(x, y);
		collidable = false;	
		this.entitySprite.setOffset(offsetX, offsetY);
		loadSprite(path, offsetX, offsetY);	
		setBoundingBox(0,0,0,0);
		name = "Grass"+count;
	}
	public String toString() {
		return String.format(name);
	}
	

	
}
