package saving_loading;

import java.awt.Point;
import java.io.Serializable;

public class EntityData implements Serializable{
	private boolean isDynamic;
	private int xPos;
	private int yPos;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ColliderData colliderData = null;
	private GraphicData graphicData = null;
	
	protected EntityData( boolean isDynamic , Point position , ColliderData colliderData ){
		this.isDynamic = isDynamic;
		this.xPos = position.x;
		this.yPos = position.y;
		this.colliderData = colliderData;
		
	}
	public boolean isDynamic(){
		return isDynamic;
	}
	
	public Point getEntityPosition(){
		return new Point( xPos , yPos );
	}
	
	public ColliderData getColliderData(){
		return this.colliderData;
	}
	protected void setGraphicData(GraphicData data){
		this.graphicData = data;
	}
	public GraphicData getGraphicData() {
		return this.graphicData;
	}
	
}
