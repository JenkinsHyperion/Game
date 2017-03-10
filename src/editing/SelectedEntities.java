package editing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import editing.worldGeom.Vertex;
import engine.Camera;
import entities.EntityStatic;

public class SelectedEntities {
	private ArrayList<EntityStatic> selectedEntities = new ArrayList<>();
	private ArrayList<Point> oldEntityPositions = new ArrayList<>();
	private Camera camera;
	
	public SelectedEntities(Camera camera) {
		this.camera = camera;
		//this.worldGeomMousePos = worldGeomMousePosRef;
	}
	public void clearSelectedEntities() {
		selectedEntities.clear();
	}
	public void updateOldEntityPositions(){
		oldEntityPositions.clear();
		for (EntityStatic entity: selectedEntities) {
			oldEntityPositions.add(new Point(entity.getPos()));
		}
	}
	public ArrayList<EntityStatic> getSelectedEntities () {
		return selectedEntities;
	}
	public boolean contains(EntityStatic entity) {
		if (selectedEntities.contains(entity)) 
			return true;
		else
			return false;
	}
	public void drawClickableBox(Graphics g, Camera camera) {
		for(EntityStatic entity: selectedEntities) {
			//vertex.drawClickableBox(g, camera);
			Rectangle rect = new Rectangle();
			//rect.setLocation(entity.getXRelativeTo(camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(camera) + entity.getSpriteOffsetY());
			rect.setLocation(entity.getX() + entity.getSpriteOffsetX(), entity.getY() + entity.getSpriteOffsetY());
			rect.setSize(entity.getEntitySprite().getImage().getWidth(null),
					entity.getEntitySprite().getImage().getHeight(null) );
			camera.drawRect(rect, g, Color.BLUE, Color.CYAN, .05f);
			
		}
		/*for (int i = 0; i < selectedVertices.size(); i++){
			selectedVertices.get(i).drawClickableBox(g, camera);
		}*/
	}
	public int size() {
		return selectedEntities.size();
	}
	public void addSelectedEntity(EntityStatic entity) {
		if (!selectedEntities.contains(entity))
			selectedEntities.add(entity);
	}
	
	public void removeSelectedEntity (EntityStatic entity) {
		selectedEntities.remove(entity);
	}
	public void printSelectedEntities() {
		for (EntityStatic ent: selectedEntities) {
			System.out.println(ent.name + ", ");
		}
	}
	public void translate(Point initClickPoint, Point worldGeomMousePos) {
		int deltaX = initClickPoint.x - worldGeomMousePos.x;
		int deltaY = initClickPoint.y - worldGeomMousePos.y;
		for (int i = 0; i < selectedEntities.size(); i++) {
			selectedEntities.get(i).setPos(camera.getLocalX(oldEntityPositions.get(i).x - deltaX), 
											 camera.getLocalY(oldEntityPositions.get(i).y - deltaY));
		}
	}
	public void removeSelectedVertex(int i) {
		if (i >= 0 && i <= selectedEntities.size()-1 )
			selectedEntities.remove(i);
	}
	
}
