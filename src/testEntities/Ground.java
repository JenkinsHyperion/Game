package testEntities;

import entities.EntityStatic;

public class Ground extends EntityStatic{
	


	public Ground(int x, int y, String path) {
		super(x, y);
    	offsetX = -152;
    	offsetY = -5;
    	loadSprite(path,offsetX,offsetY);	
		setBoundingBox(this.offsetX,this.offsetY,304,10);
		name = "Ground"+ count;
	}
	public Ground(int x, int y, int offsetX, int offsetY, String path) {
		super(x, y);
    	this.offsetX = offsetX;
    	this.offsetY = offsetY;
    	loadSprite(path,offsetX,offsetY);	
		setBoundingBox(this.offsetX,this.offsetY,304,10);
		name = "Ground"+ count;
	}

	
	public String toString() {
		return String.format(name);
	}
	
}
