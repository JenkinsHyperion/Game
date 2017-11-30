package editing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.logging.Logger;

import entityComposites.*;
import physics.Boundary;
import sprites.Sprite;
import engine.MovingCamera;
/**
 *A wrapper class that handles the concept of entities being selected. Stores references to the selected entities
 *and the camera to which they are relative.
 *
 */
public class SelectedEntities {
	private ArrayList<EntityStatic> selectedEntities = new ArrayList<>();
	private ArrayList<Point> oldEntityPositions = new ArrayList<>();
	private MovingCamera camera;
	/**@param camera the relative camera. Needed to determine the relative point of the selected entities.
	 * {@ SelectedEntities#camera camera} and other text
	 *  */
	public SelectedEntities(MovingCamera camera) {
		this.camera = camera;
		//this.worldGeomMousePos = worldGeomMousePosRef;
	}
	/** @see SelectedEntities#clearSelectedEntities()
	 * 
	 */
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
			/*if (entity.getGraphicComposite().exists()) {
				Rectangle rect = new Rectangle();
				//rect.setLocation(entity.getXRelativeTo(camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(camera) + entity.getSpriteOffsetY());
				rect.setLocation(entity.getX() + entity.getGraphicComposite().getSprite().getOffsetX(), 
						entity.getY() + entity.getGraphicComposite().getSprite().getOffsetY());
				rect.setSize(entity.getGraphicComposite().getSprite().getBufferedImage().getWidth(),
							 entity.getGraphicComposite().getSprite().getBufferedImage().getHeight());
				camera.drawRect(rect, g, Color.BLUE, Color.CYAN, .2f);
				//camera.debugDrawPolygon(rect, Color.CYAN, entity.getPosition(), new AffineTransform());
			} */
			if (entity.getGraphicComposite().exists()) {
				int areaExtender = 0;
			// vvvvv Code I copied over that should be sufficient to replace the above ^^^^^ 
				Shape clickableRect = entity.getGraphicComposite().getGraphicRelativeBounds(areaExtender);

				camera.debugDrawPolygon(clickableRect, Color.CYAN, entity.getPosition(), new AffineTransform(), .2f);
			}
			// if entity has no graphics 
			else if (entity.getColliderComposite().exists()) {
				//float tempAngle = entity.getRotationComposite().getAngle();
				//AffineTransform xform = new AffineTransform();
				//xform.rotate((double)Math.toRadians(tempAngle));
				Polygon poly = entity.getColliderComposite().getBoundary().getLocalPolygonBounds();
				Rectangle rect = poly.getBounds();
				//camera.drawRect(rect, g, Color.BLUE, Color.CYAN, .2f);
				camera.debugDrawPolygon(poly, Color.CYAN, entity.getPosition(), new AffineTransform(), 0.2f);
			}
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
	public void printSelectedEntitiesToConsole() {
		for (EntityStatic ent: selectedEntities) {
			System.out.println(ent.name + ", ");
		}
	}
	public String printSelectedEntitiesAsString() {
		String finalStringOfNames = "";
		for (EntityStatic ent: selectedEntities) {
			if (selectedEntities.size() > 1)
				finalStringOfNames += ent.name + ", ";
			else
				finalStringOfNames += ent.name;
		}
		return finalStringOfNames;
	}
	public void translate(Point initClickPoint, Point currentClickPoint) {
		int deltaX = initClickPoint.x - currentClickPoint.x;
		int deltaY = initClickPoint.y - currentClickPoint.y;
		for (int i = 0; i < selectedEntities.size(); i++) {
			
			Point oldEntityPosition = new Point(
					oldEntityPositions.get(i).x - deltaX ,
					oldEntityPositions.get(i).y - deltaY
					);
			
			Point localMousePosition = camera.getWorldPos(oldEntityPosition);
			
			selectedEntities.get(i).setPos(localMousePosition.x,localMousePosition.y);
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
