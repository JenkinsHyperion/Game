package testEntities;

import entities.EntityComposed;
import entities.EntityStatic;

public class Platform extends EntityStatic{

	public static final String PF1 = "platform.png";
	public static final String PF2 = "platform02.png";
	@Deprecated
	public Platform(int x, int y, String path) {
		super(x, y);
	   	this.offsetX = -20;
    	this.offsetY = -3;		
		loadSprite(path, offsetX, offsetY);	
		setBoundingBox(this.offsetX,this.offsetY,40,6);
		name = "Platform"+count;
		
	}
	@Deprecated
	public Platform(int x, int y, int offsetX, int offsetY, String path) {
		super(x, y);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		loadSprite(path, offsetX, offsetY);	
		setBoundingBox(this.offsetX,this.offsetY,40,6);
		name = "Platform"+count;
	}

	
	public String toString() {
		return String.format(name);
	}
}
