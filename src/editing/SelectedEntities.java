package editing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import entityComposites.*;
import physics.Boundary;
import engine.MovingCamera;

public class SelectedEntities {
	private ArrayList<EntityStatic> selectedEntities = new ArrayList<>();
	private ArrayList<Point> oldEntityPositions = new ArrayList<>();
	private MovingCamera camera;
	
	public SelectedEntities(MovingCamera camera) {
		this.camera = camera;
		//this.worldGeomMousePos = worldGeomMousePosRef;
	}
	public void clearSelectedEntities() {
		selectedEntities.clear();
	}
	
	public EntityStatic get(int index) {
		return selectedEntities.get(index);
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
	public void drawClickableBox(Graphics g, MovingCamera camera) {
		for(EntityStatic entity: selectedEntities) {
			//vertex.drawClickableBox(g, camera);'
			if (entity.getGraphicComposite().exists()) {
				Rectangle rect = new Rectangle();
				//rect.setLocation(entity.getXRelativeTo(camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(camera) + entity.getSpriteOffsetY());
				rect.setLocation(entity.getX() + entity.getGraphicComposite().getSprite().getOffsetX(), 
						entity.getY() + entity.getGraphicComposite().getSprite().getOffsetY());
				rect.setSize(entity.getGraphicComposite().getSprite().getBufferedImage().getWidth(),
							 entity.getGraphicComposite().getSprite().getBufferedImage().getHeight());
				camera.drawRect(rect, g, Color.BLUE, Color.CYAN, .2f);
			} 
			// if entity has no graphics 
			//float tempAngle = entity.getRotationComposite().getAngle();
			//AffineTransform xform = new AffineTransform();
			//xform.rotate((double)Math.toRadians(tempAngle));
			/*if (entity.getColliderComposite().exists()) {
				Polygon poly = Boundary.getPolygonFromBoundary(entity.getColliderComposite().getBoundaryLocal(), entity);
				Rectangle rect = poly.getBounds();
				camera.drawRect(rect, g, Color.BLUE, Color.CYAN, .2f);
				//camera.debugDrawPolygon(poly, Color.CYAN, entity, xform);
			}*/
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
	public void translate(Point initClickPoint, Point currentClickPoint) {
		int deltaX = initClickPoint.x - currentClickPoint.x;
		int deltaY = initClickPoint.y - currentClickPoint.y;
		for (int i = 0; i < selectedEntities.size(); i++) {
			selectedEntities.get(i).setPos(camera.getLocalX(oldEntityPositions.get(i).x - deltaX), 
											 camera.getLocalY(oldEntityPositions.get(i).y - deltaY));
		}
	}
	public void updateOldEntityPositions(){
		oldEntityPositions.clear();
		for (EntityStatic entity: selectedEntities) {
			oldEntityPositions.add(new Point(entity.getPosition()));
		}
	}
	public void removeSelectedVertex(int i) {
		if (i >= 0 && i <= selectedEntities.size()-1 )
			selectedEntities.remove(i);
	}
	
}
