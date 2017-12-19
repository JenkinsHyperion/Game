package editing.worldGeom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;


import engine.MovingCamera;

public class SelectionRectangle implements SelectionRectangleAbstract{
	private Rectangle selectionRectangle;
	private Point initClickPoint;
	//private Point worldGeomMousePos;
	private MovingCamera camera;
	private Color outlineColor;
	private Color fillColor;

	public SelectionRectangle(Color outlineColor, Color fillColor, MovingCamera camera, Point initClickPointRef) {
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
		return selectionRectangle;
	}

	@Override
	public void draw(Graphics g, MovingCamera camera) {
		//camera.drawRect(selectionRectangle, g, outlineColor, fillColor, .3f);
		//camera.debugDrawPolygon(selectionRectangle, fillColor, camera.getRelativePoint(initClickPoint), new AffineTransform(), .3f);
		//camera.drawShapeInWorld(new Rectangle((int)selectionRectangle.getWidth(),(int)selectionRectangle.getHeight()), selectionRectangle.getLocation());
		camera.drawShapeInWorldSelectionRect(new Rectangle((int)selectionRectangle.getWidth(),(int)selectionRectangle.getHeight()), 
				selectionRectangle.getLocation(), outlineColor, fillColor, .3f, true);
/*
		Point point = camera.getFullRelativePositionOf(initClickPoint);
		g.drawRect( (point.x) , (point.y) ,(int)selectionRectangle.getWidth(),(int)selectionRectangle.getHeight() );
		*/
		camera.drawCrossInWorld(initClickPoint);
		g.drawRect(selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
		//Polygon shape = camera.convertScreenPolygonToWorldPolygon(selectionRectangle); //debugging making sure the math of the box is where its supposed to be
		//camera.drawShapeInWorld( shape, new Point() );
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
	}
	@Override
	public void setInitialY(int newInitialY) {
	}

	@Override
	public void setFinalX(int newFinalX) {
	}

	@Override
	public void setFinalY(int newFinalY) {
	}

}
