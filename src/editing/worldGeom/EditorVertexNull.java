package editing.worldGeom;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import engine.*;

public final class EditorVertexNull extends EditorVertexAbstract {

	private static EditorVertexNull editorVertexNull = new EditorVertexNull();

	public EditorVertexNull() {
		// TODO Auto-generated constructor stub
	}
	public static EditorVertexNull getNullVertex() {
		return editorVertexNull;
	}
	
	@Override
	public void draw(Graphics g, Camera camera) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Point getPoint() {
		return new Point();
		// TODO Auto-generated method stub
	}
	public void drawClickableBox(Graphics g, Camera camera) {
	}
	@Override
	public Rectangle getClickableZone() {
		// TODO Auto-generated method stub
		return new Rectangle();
	}
	@Override
	public void translate(Point p) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void translate(int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
