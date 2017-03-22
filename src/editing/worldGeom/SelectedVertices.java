package editing.worldGeom;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import engine.Camera;

public class SelectedVertices {
	private ArrayList<Vertex> selectedVertices = new ArrayList<>();
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
	public ArrayList<Vertex> getVertices(){
		return this.selectedVertices;
	}
	public void updateOldVertexPositions(){
		oldVertexPositions.clear();
		for (Vertex vertex: selectedVertices) {
			oldVertexPositions.add(new Point(vertex.getPoint()));
		}
	}
	public boolean contains(Vertex vertex) {
		if (selectedVertices.contains(vertex)) 
			return true;
		else
			return false;
	}
	
	public void drawClickableBox(Graphics g, Camera camera) {
		// should only run if there are any items inside the array
		for(Vertex vertex: selectedVertices) {
			vertex.drawClickableBox(g, camera);
		}
		/*for (int i = 0; i < selectedVertices.size(); i++){
			selectedVertices.get(i).drawClickableBox(g, camera);
		}*/
	}
	public int size() {
		return selectedVertices.size();
	}
	public void addSelectedVertex(Vertex vertex) {
		if (!selectedVertices.contains(vertex))
			selectedVertices.add(vertex);
	}
	
	public void removeSelectedVertex (Vertex vertex) {
		selectedVertices.remove(vertex);
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
