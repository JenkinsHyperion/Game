package testEntities;

import entities.EntityStatic;

public class Ground extends EntityStatic{
	


	public Ground(int x, int y, String path) {
		super(x, y);

    	offsetX = -223;
    	offsetY = -48;
    	loadSprite(path,offsetX,offsetY);	
		setBoundingBox(this.offsetX,this.offsetY+5,446,100);
		name = "Ground"+ count;
	}
	public Ground(int x, int y, int offsetX, int offsetY, String path) {
		super(x, y);
    	this.offsetX = offsetX;
    	this.offsetY = offsetY;
    	loadSprite(path,offsetX,offsetY);	
		setBoundingBox(-223,-48,446,100);
		name = "Ground"+ count;
	}

	
	public String toString() {
		return String.format(name);
	}
	
}
