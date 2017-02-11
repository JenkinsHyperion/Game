package editing.worldGeom;

import engine.*;
import entities.*;
import Input.*;
import editing.EditorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

/** WorldGeometry.java will contain 
 * 
 * @author Dave
 *
 */
//steps that need to be done
/* 1)import many methods (I think nearly all) from Editor that select entities
 * ---actually, if it can be ascertained that the functionality for clicking vertices and sprites are similar enough,
 * ---just add variability to the parameters that check sprites to include checking vertices too.
 * ---v--> this will be done by the MODE check (world geometry mode or entity placement mode)
 * 
 * ---- Could possibly do a ghostSprite but for the vertices. So in every method that would scan through entities
 * ---- to see if the mouse clicked it, you can instead scan through the arraylist of vertices
 * 
 * 2) A quick method to draw a little square. Not sure if I should preload it first into an image or
 *    have it draw it every time. It will be just like MissingIcon's method
 */

public class WorldGeometry {
	
	private EditorPanel editorPanel;
	private Board board;

	// World Geometry Modes:
	private WorldGeomMode worldGeomMode;
	private final WorldGeomMode vertexPlaceMode = new VertexPlaceMode();
	private final WorldGeomMode vertexSelectMode = new VertexSelectMode();
	
	private ArrayList<Point> vertexList = new ArrayList<>();
	private ArrayList<Line2D.Double> surfaceLines = new ArrayList<>();
	//private ArrayList<WorldGeometry> worldGeometryEntities = new ArrayList<>();
	protected BufferedImage ghostVertexPic;
	protected BufferedImage vertexPic;
	protected boolean keypressALT;
	private Point worldGeomMousePos;
	//private int worldGeomMode;
	/*private static final int VERTEXDRAWING_MODE = 0;
	private static final int VERTEXSELECT_MODE = 1;*/
	// private Point currentSelectedVertex;
	private Point2D currentSelectedVertex;
	private boolean vertexPlacementAllowed;
	
	private int offsetX; //actual point will be within the square's center, so square must be offset.
	private int offsetY;
	
	public WorldGeometry(EditorPanel editorPanelRef, Board boardRef) { 
		this.editorPanel = editorPanelRef;
		this.board = boardRef;
		vertexPlacementAllowed = true;
		// initalize modes for world geometry 
		// default to placement mode
		worldGeomMode = vertexPlaceMode;
		// [deprecated] worldGeomMode = VERTEXDRAWING_MODE;

		updateSurfaceLines();
		worldGeomMousePos = new Point();
		keypressALT = false;
		ghostVertexPic = (BufferedImage)createVertexPic(0.5f);
		vertexPic = (BufferedImage)createVertexPic(1.0f);

	}

	public Image createVertexPic(float opacity) {
		BufferedImage temp = new BufferedImage(6,6, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = temp.createGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, 6, 6);
		g2.dispose();
		return temp;
	}
	
	
/*	
	public void drawVertexPoints(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		for (Point point: vertexList) {
			//g2.drawImage(vertexPic, board.camera.getLocalX(point.x)-3, board.camera.getLocalY(point.y)-3, null);
			board.camera.draw(vertexPic, g, point.x-3, point.y-3);
		}
		g2.setColor(Color.WHITE);
		g2.drawString(Boolean.toString(keypressALT), 50, 50);
		g2.drawString(Boolean.toString(vertexPlacementAllowed),50, 100);
	}*/
	/** True if any intersection is found across all lines in the surfaceLines arrayList<> 
	 */
	public boolean checkIfLinesIntersect(Line2D.Double testLine){
		for (int i = 0; i < surfaceLines.size()-1; i++){
			if (surfaceLines.get(i).intersectsLine(testLine))
				return true;
		}
		return false;
		
	}
	
/*	public void drawSurfaceLines(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.MAGENTA);
		for (int i = 0; i < vertexList.size()-1; i++) {
			Line2D.Double tempLine = new Line2D.Double(vertexList.get(i), vertexList.get(i+1));
			//g2.draw(tempLine);
			board.camera.draw(tempLine, g2);
		}
	}*/
	public void updateSurfaceLines() {
		for (int i = 0; i < vertexList.size()-1; i++) {
			Line2D.Double tempLine = new Line2D.Double(vertexList.get(i), vertexList.get(i+1));
			surfaceLines.add(tempLine);
		}
	}
	public Image getGhostVertexPic() {
		return ghostVertexPic;
	}
	public Image getVertexPic() {
		return vertexPic;
	}
	/**Adds vertex to the arrayList
	 * Should take input from MouseEvent e that is passed in from board into this class's mousePressed(e) method
	 */
	public void addVertex(int x, int y) {
		//deselectAllVertices()   (for when vertices can be selected)
		//vertexPoints.add(new Point(board.camera.getLocalX(worldGeomMousePos.x), board.camera.getLocalY(worldGeomMousePos.y)));
		vertexList.add(new Point(board.camera.getLocalX(x), board.camera.getLocalY(y)));
		//vertexPoints.add(new Point(x,y));
		updateSurfaceLines();
	}
	public void removeVertex(Point selectedPoint) {
		
	}
	public void clearAllVertices() {
		vertexList.clear();
		surfaceLines.clear();
	}
	public void setCurrentSelectedVertex(Point2D newSelectedVertex){
		currentSelectedVertex = newSelectedVertex;
	}
	//VERTEX SELECTION AREA: IDENTICAL TO SPRITE SELECTION BECAUSE THE FUNCTIONALITY WORKS WELL
	/*
	public void checkForSelection(Point click) 
	{
  		setCurrentSelectedEntity(clickedOnEntity(click));
  		//currentSelectedEntity = clickedOnEntity(click);
  		if (currentSelectedEntity != null)
  			board.currentDebugEntity = currentSelectedEntity;
  	}
  	public EntityStatic clickedOnEntity(Point click) {
  		int counter = 0;
  		for (EntityStatic entity : board.getStaticEntities()) 
  		{
  			
	 		if (entity.getEntitySprite().hasSprite())  //if entity has sprite, select by using sprite dimensions
	 		{ 
	  			selectedBox.setLocation(entity.getX() + entity.getSpriteOffsetX(), entity.getY() + entity.getSpriteOffsetY());
	  			selectedBox.setSize(entity.getEntitySprite().getImage().getWidth(null), entity.getEntitySprite().getImage().getHeight(null) );
	  			if (selectedBox.contains(click)) 
	  			{
	  				//entity.isSelected = true;
	  				enableEditPropertiesButton(true); //might not need
	  				restorePanels();
	  				setAllEntitiesComboBoxIndex(counter);
	  	  			setSelectedEntityNameLabel("Selected: " + entity.name);
	  	  			setEntityCoordsLabel("Coords. of selected entity: " + entity.getX() + ", " + entity.getY());
	  				return entity;
	  			}
	  			counter++;	  			
	 		}
	 		else {
	 			//Entity has no sprite, so selection needs some other method, like by boundary
	 		} 			
  		}
  		//nothing was found under cursor: 
  		enableEditPropertiesButton(false);
  		minimizePanels();
  		return null;
  	}
	*/
	//// WORLD GEOM'S MOUSE HANDLING SECTION  ////////////
	public void mousePressed(MouseEvent e) {
		
		this.worldGeomMode.mousePressed(e);
		// TODO Auto-generated method stub
		
		// (idea) would maybe want something like:
		// this.mode.mousePressed(MouseEvent e), 
		//	 where there would be different implementations of the press
	}
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mouseMoved(MouseEvent e) {
		if (keypressALT) {
			if (vertexList.size() > 0)
				worldGeomMousePos.setLocation(e.getX(), vertexList.get(vertexList.size()-1).getY());
			else
				worldGeomMousePos.setLocation(e.getX(), e.getY());
		}
		else //shift isn't held; default running condition
			worldGeomMousePos.setLocation(e.getX(), e.getY());
			
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	//END OF MOUSE HANDLING //////
	
	//KEY EVENTS PASSED IN FROM BOARD
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ALT && !keypressALT) {  // holding ALT when placing vertices will lock the y axis to the last placed point
			keypressALT = true;
			//yClampGate = true;
			//setYClamp(worldGeomMousePos.getY();
			//yClampGate = false;
		}
		if (key == KeyEvent.VK_ESCAPE) {
			resetStates();
        	clearAllVertices();
		}
	}
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ALT && keypressALT)
			keypressALT = false;
	}
	/* public void setYClamp(int yClamp){
		this.yClamp = yClamp;
		yClampGate = false;
	} */
	public void setShiftHeld(boolean state){
		this.keypressALT = state;
	}
	public boolean getShiftHeld() {
		return this.keypressALT;
	}
	//// END OF KEY HANDLING SECTION /////
	public Point getWorldGeomMousePos() {
		return worldGeomMousePos;
	}

	public void setWorldGeomMousePos(Point pos) {
		this.worldGeomMousePos = pos;
	}
	public void resetStates() {
		vertexPlacementAllowed = true;
		keypressALT = false;
	}
	
	public void setMode(WorldGeomMode newMode) {
		this.worldGeomMode = newMode;
	}
	
	//will be implemented by one of the below states
	public void render(Graphics g) {
		worldGeomMode.render(g);
	}
	
/////////   INNER CLASS VERTEXPLACEMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
	public class VertexPlaceMode extends WorldGeomMode {

		
		public VertexPlaceMode() {

		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (vertexPlacementAllowed == true)
				addVertex(e.getX(), e.getY());
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void render(Graphics g) {
			//old drawghostvertex
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
			//the -3 accounts for the offset
			g2.drawImage(vertexPic, worldGeomMousePos.x - 3, worldGeomMousePos.y - 3, null);
			if (vertexList.size() > 0) {	
				//Line2D.Double ghostLine = new Line2D.Double(vertexPoints.get(vertexPoints.size()-1), worldGeomMousePos);
				//offset by a pixel because it was always intersecting with previous line in list
				Line2D.Double ghostLine = new Line2D.Double(vertexList.get(vertexList.size()-1).getX()+3, vertexList.get(vertexList.size()-1).getY(),
							board.camera.getLocalX(worldGeomMousePos.getX()), board.camera.getLocalY(worldGeomMousePos.getY()));
				// if checkForIntersection(ghostLine, new Line2D.Double(vertexPoints(size()-2, vertexPoints(size()-1)
				if (vertexList.size() > 1) { //there exists at least one line already drawn:
					
					if (checkIfLinesIntersect(ghostLine)) {  //one of the lines are crossing
						g2.setColor(Color.RED);
						//canCreateVertices(false) <---do later EDIT: done
						vertexPlacementAllowed = false;
					}
					else {								// nothing's intersecting, ready to place another point
						g2.setColor(Color.PINK);
						vertexPlacementAllowed = true;
					}
				}
				else {
					g2.setColor(Color.PINK);
				}
				//######  first point is the world (local) position, and the second point is the relative position under cursor. #####
				//g2.draw(new Line2D.Double(board.camera.getLocalPosition((Point) ghostLine.getP1()), ghostLine.getP2()));
				//g2.drawLine(board.camera.getRelativeX((int)ghostLine.getX1()), board.camera.getRelativeY((int)ghostLine.getY1()),
						   //board.camera.getRelativeX((int)ghostLine.getX2()), board.camera.getRelativeY((int)ghostLine.getY2()));
				board.camera.draw(ghostLine, g2);
			}
			
			//old drawVertexPoints vvvvvvv
			for (Point point: vertexList) {
				//g2.drawImage(vertexPic, board.camera.getLocalX(point.x)-3, board.camera.getLocalY(point.y)-3, null);
				board.camera.draw(vertexPic, g, point.x-3, point.y-3);
			}
			g2.setColor(Color.WHITE);
			g2.drawString(Boolean.toString(keypressALT), 50, 50);
			g2.drawString(Boolean.toString(vertexPlacementAllowed),50, 100);
			
			// old drawsurfacelines vvvvvv
			g2.setColor(Color.MAGENTA);
			for (int i = 0; i < vertexList.size()-1; i++) {
				Line2D.Double tempLine = new Line2D.Double(vertexList.get(i), vertexList.get(i+1));
				//g2.draw(tempLine);
				board.camera.draw(tempLine, g2);
			}
		}
	}
	// end of inner class
	
	
/////////   INNER CLASS VERTEXPLACEMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
	public class VertexSelectMode extends WorldGeomMode {
		
		//worldGeomRef is inherited
		public VertexSelectMode() {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void render(Graphics g) {
			
			//old drawVertexPoints vvvvvvv
			Graphics2D g2 = (Graphics2D) g;
			for (Point point: vertexList) {
				//g2.drawImage(vertexPic, board.camera.getLocalX(point.x)-3, board.camera.getLocalY(point.y)-3, null);
				board.camera.draw(vertexPic, g, point.x-3, point.y-3);
			}
			g2.setColor(Color.WHITE);
			g2.drawString(Boolean.toString(keypressALT), 50, 50);
			g2.drawString(Boolean.toString(vertexPlacementAllowed),50, 100);
			
			// old drawsurfacelines vvvvvv
			g2.setColor(Color.MAGENTA);
			for (int i = 0; i < vertexList.size()-1; i++) {
				Line2D.Double tempLine = new Line2D.Double(vertexList.get(i), vertexList.get(i+1));
				//g2.draw(tempLine);
				board.camera.draw(tempLine, g2);
			}
		}
	}
	//end of inner class
}
