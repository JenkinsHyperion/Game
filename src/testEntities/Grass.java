package testEntities;

import entities.EntityStatic;
import entityComposites.GraphicComposite;

//generic static sprite like a grass
public class Grass extends EntityStatic{
	
	public Grass(int x, int y, String path) {
		super(x, y);
			collidable = false;	
			((GraphicComposite)this.spriteType).getSprite().setOffset(0, 0); 
			loadSprite(path, 0, 0);	
			setBoundingBox(0,0,0,0);
			name = "Grass"+count;
	}
	public Grass(int x, int y, int offsetX, int offsetY, String path) {
		super(x, y);
		collidable = false;	
		((GraphicComposite)this.spriteType).getSprite().setOffset(offsetX, offsetY);
		loadSprite(path, offsetX, offsetY);	
		setBoundingBox(0,0,0,0);
		name = "Grass"+count;
	}
	public String toString() {
		return String.format(name);
	}
	

	
}
