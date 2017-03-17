package saving_loading;

import java.awt.Point;
import java.io.Serializable;

public class EntityData implements Serializable{
	
	private int xPos;
	private int yPos;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ColliderData colliderData = null;
	
	protected EntityData( Point position , ColliderData colliderData ){
		
		this.xPos = position.x;
		this.yPos = position.y;
		this.colliderData = colliderData;
		
	}
	
	public Point getEntityPosition(){
		return new Point( xPos , yPos );
	}
	
	public ColliderData getColliderData(){
		return this.colliderData;
	}
	
}
