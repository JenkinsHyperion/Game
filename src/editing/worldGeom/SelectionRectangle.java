package editing.worldGeom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import engine.Camera;

public class SelectionRectangle implements SelectionRectangleAbstract{
	private Rectangle selectionRectangle;
	private Point initClickPoint;
	//private Point worldGeomMousePos;
	private Camera camera;
	private Color outlineColor;
	private Color fillColor;

	public SelectionRectangle(Color outlineColor, Color fillColor, Camera camera, Point initClickPointRef) {
		this.outlineColor = outlineColor;
		this.fillColor = fillColor;
		this.camera = camera;
		this.initClickPoint = initClickPointRef;
		/*this.initClick = initClickRef;
		this.worldGeomMousePos = worldGeomMousePosRef;*/
		this.selectionRectangle = new Rectangle();
	}
	@Override
	public Rectangle getWrekt() {
		// TODO Auto-generated method stub
		return selectionRectangle;
	}

	@Override
	public void draw(Graphics g, Camera camera) {
		camera.drawRect(selectionRectangle, g, outlineColor, fillColor, .3f);
	}

	public void setInitialRectPoint(){
		selectionRectangle.setLocation(initClickPoint);
	}
	public void translateEndPoint(Point worldGeomMousePosRef){
		int width = worldGeomMousePosRef.x - initClickPoint.x;
		int height = worldGeomMousePosRef.y - initClickPoint.y;
		selectionRectangle.setSize(width, height);
	}
	public void resetRect(){
		selectionRectangle.setSize(0, 0);
	}
	@Override
	public void setInitialX(int newInitialX) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInitialY(int newInitialY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFinalX(int newFinalX) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFinalY(int newFinalY) {
		// TODO Auto-generated method stub
		
	}

}
