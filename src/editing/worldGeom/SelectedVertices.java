package editing.worldGeom;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import engine.MovingCamera;

public class SelectedVertices {
	private ArrayList<EditorVertex> selectedVertices = new ArrayList<>();
	private ArrayList<Point> oldVertexPositions = new ArrayList<>();
	private MovingCamera camera;
	//private Point worldGeomMousePos;
	// vvvv probably won't need
	//private VertexNull vertexNull = VertexNull.getNullVertex();
	public SelectedVertices(MovingCamera camera) {
		this.camera = camera;
		//this.worldGeomMousePos = worldGeomMousePosRef;
	}
	public void clearSelectedVertices() {
		selectedVertices.clear();
	}
	public ArrayList<EditorVertex> getVertices(){
		return this.selectedVertices;
	}
	public void updateOldVertexPositions(){
		oldVertexPositions.clear();
		for (EditorVertex editorVertex: selectedVertices) {
			oldVertexPositions.add(new Point(editorVertex.getPoint()));
		}
	}
	public void alignToXAxis() {
		if (selectedVertices.size() > 1) {
			int xMin = selectedVertices.get(0).getPoint().x;
			int xMax = selectedVertices.get(selectedVertices.size()-1).getPoint().x;
			//find min and max first
			for (int i = 0; i < selectedVertices.size(); i++) {
				if (selectedVertices.get(i).getPoint().x < xMin)
					xMin = selectedVertices.get(i).getPoint().x;
				if (selectedVertices.get(i).getPoint().x > xMax)
					xMax = selectedVertices.get(i).getPoint().x;
			}
			int average = Math.abs( (xMax - xMin) / 2);
			int newMidPoint = xMin + average;
			for (int i = 0; i < selectedVertices.size(); i++) {
				selectedVertices.get(i).translate(newMidPoint, selectedVertices.get(i).getPoint().y);
			}
			clearSelectedVertices();
		}
	}
	public void alignToYAxis() {
		if (selectedVertices.size() > 1) {
			int yMin = selectedVertices.get(0).getPoint().y;
			int yMax = selectedVertices.get(selectedVertices.size()-1).getPoint().y;
			//find min and max first
			for (int i = 0; i < selectedVertices.size(); i++) {
				if (selectedVertices.get(i).getPoint().y < yMin)
					yMin = selectedVertices.get(i).getPoint().y;
				if (selectedVertices.get(i).getPoint().y > yMax)
					yMax = selectedVertices.get(i).getPoint().y;
			}
			int average = (yMax - yMin) / 2;
			int newMidPoint = yMin + average;
			for (int i = 0; i < selectedVertices.size(); i++) {
				selectedVertices.get(i).translate(selectedVertices.get(i).getPoint().x, newMidPoint);
			}
			clearSelectedVertices();
		}
	}
	public boolean contains(EditorVertex editorVertex) {
		if (selectedVertices.contains(editorVertex)) 
			return true;
		else
			return false;
	}
	
	public void drawClickableBox(Graphics g, MovingCamera camera) {
		// should only run if there are any items inside the array
		for(EditorVertex editorVertex: selectedVertices) {
			editorVertex.drawClickableBox(g, camera);
		}
		/*for (int i = 0; i < selectedVertices.size(); i++){
			selectedVertices.get(i).drawClickableBox(g, camera);
		}*/
	}
	public int size() {
		return selectedVertices.size();
	}
	public void addSelectedVertex(EditorVertex editorVertex) {
		if (!selectedVertices.contains(editorVertex))
			selectedVertices.add(editorVertex);
	}
	
	public void removeSelectedVertex (EditorVertex editorVertex) {
		selectedVertices.remove(editorVertex);
	}
	
	public void translate(Point initClickPoint, Point worldGeomMousePos) {
		int deltaX = initClickPoint.x - worldGeomMousePos.x;
		int deltaY = initClickPoint.y - worldGeomMousePos.y;
		for (int i = 0; i < selectedVertices.size(); i++) {
			selectedVertices.get(i).translate(camera.getLocalX(oldVertexPositions.get(i).x - deltaX), 
											 camera.getLocalY(oldVertexPositions.get(i).y - deltaY));
		}
	}
	public void removeSelectedVertex(int i) {
		if (i >= 0 && i <= selectedVertices.size()-1 )
			selectedVertices.remove(i);
	}
	
}
