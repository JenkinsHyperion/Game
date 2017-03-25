package editing.worldGeom;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import engine.Camera;

public class SelectedVertices {
	private ArrayList<EditorVertex> selectedVertices = new ArrayList<>();
	private ArrayList<Point> oldVertexPositions = new ArrayList<>();
	private Camera camera;
	//private Point worldGeomMousePos;
	// vvvv probably won't need
	//private VertexNull vertexNull = VertexNull.getNullVertex();
	public SelectedVertices(Camera camera) {
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
	public boolean contains(EditorVertex editorVertex) {
		if (selectedVertices.contains(editorVertex)) 
			return true;
		else
			return false;
	}
	
	public void drawClickableBox(Graphics g, Camera camera) {
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
