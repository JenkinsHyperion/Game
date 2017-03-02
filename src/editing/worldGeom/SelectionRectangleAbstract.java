package editing.worldGeom;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import engine.Camera;

public interface SelectionRectangleAbstract {
	public Rectangle getWrekt();
	/**
	 * Will go through camera.drawRect()
	 */
	public void draw(Graphics g, Camera camera);
	public void setInitialRectPoint();
	public void translateEndPoint(Point worldGeomMousePosRef);
	public void setInitialX(int newInitialX);
	public void setInitialY(int newInitialY);
	public void resetRect();
	/**
	 * Should have code inside to set the width
	 * @param newFinalX
	 */
	public void setFinalX(int newFinalX);
	/** 
	 * Should have code inside to set the height
	 * @param newFinalY
	 */
	public void setFinalY(int newFinalY);
}
