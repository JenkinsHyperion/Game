package editing.worldGeom;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import engine.MovingCamera;

public class SelectionRectangleNull implements SelectionRectangleAbstract {

	private static SelectionRectangleNull selectionRectangleNull= new SelectionRectangleNull();

	public SelectionRectangleNull() {
	}
	public static SelectionRectangleNull getNullSelectionRectangle() {
		return selectionRectangleNull;
	}
	
	@Override
	public Rectangle getWrekt() {
		// TODO Auto-generated method stub
		return new Rectangle();
	}

	@Override
	public void draw(Graphics g, MovingCamera camera) {
		// TODO Auto-generated method stub
		
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
	@Override
	public void setInitialRectPoint() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void translateEndPoint(Point worldGeomMousePosRef) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void resetRect() {
		// TODO Auto-generated method stub
		
	}

}
