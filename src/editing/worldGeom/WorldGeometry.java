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
import java.awt.geom.Rectangle2D;
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
	private BoardAbstract board;
	protected Camera camera;
	//private ArrayList<Point> vertexPoints = new ArrayList<>();

	// World Geometry Modes:
	private WorldGeomMode worldGeomMode;
	private final VertexPlaceMode vertexPlaceMode;
	private final VertexSelectMode vertexSelectMode;
	
	private ArrayList<Vertex> vertexList = new ArrayList<>();
	private ArrayList<Line2D.Double> surfaceLines = new ArrayList<>();
	//private ArrayList<WorldGeometry> worldGeometryEntities = new ArrayList<>();
	
	protected BufferedImage ghostVertexPic;
	private Point worldGeomMousePos;
	
	private int offsetX; //actual point will be within the square's center, so square must be offset.
	private int offsetY;
	

	public WorldGeometry(EditorPanel editorPanelRef, BoardAbstract board2) { 
		this.worldGeomMousePos = new Point();
		this.editorPanel = editorPanelRef;
		this.board = board2;
		this.camera = board2.getCamera();
		// ########### initalize modes for world geometry  ##########
		// default to placement mode
		//worldGeomMode = vertexPlaceMode;
		vertexPlaceMode = new VertexPlaceMode();
		vertexSelectMode = new VertexSelectMode();
		worldGeomMode = vertexSelectMode;


		//keypressALT = false;
		ghostVertexPic = (BufferedImage)Vertex.createVertexPic(0.5f);
	}

/*	public void drawSurfaceLines(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.MAGENTA);
		for (int i = 0; i < vertexList.size()-1; i++) {
			Line2D.Double tempLine = new Line2D.Double(vertexList.get(i), vertexList.get(i+1));
			//g2.draw(tempLine);
			this.camera.draw(tempLine, g2);
		}
	}*/
	public void updateSurfaceLines(int startingIndex) {
		//TODO this might be really flawed going through the entire
		// loop every time this method is called, but might be ok. Check up on this.
		for (int i = startingIndex; i < vertexList.size()-1; i++) {
			Line2D.Double tempLine = new Line2D.Double(vertexList.get(i).getPoint(), vertexList.get(i+1).getPoint());
			surfaceLines.add(tempLine);
			
		}
	}
	public Image getGhostVertexPic() {
		return ghostVertexPic;
	}

	/**Adds vertex to the arrayList
	 * Should take input from MouseEvent e that is passed in from board into this class's mousePressed(e) method
	 */
	public void addVertex(int x, int y) {
		//deselectAllVertices()   (for when vertices can be selected)
		
		//vertexList.add(new Vertex(this.camera.getLocalX(x), this.camera.getLocalY(y)));
		vertexList.add(new Vertex(x,y));
		if (vertexList.size() > 1) 
			updateSurfaceLines(vertexList.size()-2);
	}
	public void removeVertex(Vertex vertexToRemove) {
		
	}
	public void clearAllVertices() {
		vertexList.clear();
		surfaceLines.clear();
	}
	
	//// WORLD GEOM'S MOUSE HANDLING SECTION  ////////////
	public void mousePressed(MouseEvent e) {
		setWorldGeomMousePos(e.getPoint());
		this.worldGeomMode.mousePressed(e);
		// TODO Auto-generated method stub
		
		// (idea) would maybe want something like:
		// this.mode.mousePressed(MouseEvent e), 
		//	 where there would be different implementations of the press
	}
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		setWorldGeomMousePos(e.getPoint());
		this.worldGeomMode.mouseDragged(e);
		
	}
	public void mouseMoved(MouseEvent e) {
		setWorldGeomMousePos(e.getPoint());
		this.worldGeomMode.mouseMoved(e);	
	}

	public void mouseReleased(MouseEvent e) {
		setWorldGeomMousePos(e.getPoint());
		this.worldGeomMode.mouseReleased(e);
		// TODO Auto-generated method stub
		
	}
	//END OF MOUSE HANDLING //////
	
	//KEY EVENTS PASSED IN FROM BOARD
	public void keyPressed(KeyEvent e){
		this.worldGeomMode.inputController.keyPressed(e);
		//int key = e.getKeyCode();
		/*if (key == KeyEvent.VK_ALT && !keypressALT) {  // holding ALT when placing vertices will lock the y axis to the last placed point
			keypressALT = true;*/
			//yClampGate = true;
			//setYClamp(worldGeomMousePos.getY();
			//yClampGate = false;
		//}
		/*if (key == KeyEvent.VK_ESCAPE) {
			resetStates();
        	clearAllVertices();
		}*/
	}
	public void keyReleased(KeyEvent e) {
		this.worldGeomMode.inputController.keyReleased(e);
		//int key = e.getKeyCode();
		/*if (key == KeyEvent.VK_ALT && keypressALT)
			keypressALT = false;*/
	}
	/* public void setYClamp(int yClamp){
		this.yClamp = yClamp;
		yClampGate = false;
	} */
/*	public void setShiftHeld(boolean state){
		this.keypressALT = state;
	}*/
	/*public boolean getShiftHeld() {
		return this.keypressALT;
	}*/
	//// END OF KEY HANDLING SECTION /////
	public Point getWorldGeomMousePos() {
		return worldGeomMousePos;
	}

	public void setWorldGeomMousePos(Point pos) {
		this.worldGeomMousePos = pos;
	}
	/*public void resetStates() {
		vertexPlacementAllowed = true;
		//keypressALT = false;
	}
	*/
	public void setMode(WorldGeomMode newMode) {
		this.worldGeomMode = newMode;
	}
	public VertexPlaceMode getVertexPlaceMode() {
		return this.vertexPlaceMode;
	}
	public VertexSelectMode getVertexSelectMode() {
		return this.vertexSelectMode;
	}
	//will be implemented by one of the below states
	public void render(Graphics g) {
		worldGeomMode.render(g);
	}
	
/////////   INNER CLASS VERTEXPLACEMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
	public class VertexPlaceMode extends WorldGeomMode {	
		/* Will need to create some booleans states here possibly */
		// area for states, to handle lots of different key presses
		private boolean vertexPlacementAllowed = true;
		public VertexPlaceMode() {
			inputController = new InputController();
			KeyStateNull keyStateNull = new KeyStateNull();
			KeyStateCtrl keyStateCtrl = new KeyStateCtrl();
			//set initial condition for keyState
			keyState = keyStateNull;
			
			
			//TESTING
			inputController.createKeyBinding(KeyEvent.VK_EQUALS, new KeyCommand() {

				@Override
				public void onPressed() {
					Line2D[] lines = new Line2D[surfaceLines.size()]; 
					surfaceLines.toArray( lines );
					System.out.println("adding");
					((Board)board).addStaticEntity( EntityFactory.createEntityFromBoundary(0, 0, lines) );
					
				}
				@Override
				public void onReleased() {
				}
				@Override
				public void onHeld() {
				}
				
			});
			
			// ###### ALIGN TO X-AXIS BUTTON  #######
			inputController.createKeyBinding(KeyEvent.VK_CONTROL, new KeyCommand() {
				@Override
				public void onPressed() {
					keyState = keyStateCtrl;
					if (vertexList.size() > 0) 
						worldGeomMousePos.setLocation(worldGeomMousePos.x, camera.getRelativeY((vertexList.get(vertexList.size()-1).getPoint().y)));
				}
				@Override
				public void onReleased() {
					keyState = keyStateNull;
				}
				@Override
				public void onHeld() {
					worldGeomMousePos.setLocation(worldGeomMousePos.x, camera.getRelativeY((vertexList.get(vertexList.size()-1).getPoint().y)));
					//keyState = keyStateAlt;
				}
			});
			// ####  UNDO BUTTON  #######
			inputController.createKeyBinding(KeyEvent.CTRL_MASK, KeyEvent.VK_Z, new KeyCommand() {
				@Override
				public void onPressed() {
					if (vertexList.size() > 0) {
						vertexList.remove(vertexList.size()-1);
						surfaceLines.remove(surfaceLines.size()-1);
					}
				}
				@Override
				public void onReleased() {
				}
				@Override
				public void onHeld() {
				}
			});
			// #######  left click event  #######
			inputController.createMouseBinding(MouseEvent.BUTTON1, new MouseCommand(){ 
				public void mousePressed() {
					if (vertexPlacementAllowed == true) 
						addVertex(camera.getLocalX(worldGeomMousePos.x), camera.getLocalY(worldGeomMousePos.y));
				}
				public void mouseDragged() {
				}
				public void mouseReleased() {}
			});
			inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON1, new MouseCommand(){ //test drag event
				public void mousePressed() {
					if (vertexPlacementAllowed == true) 
						//addVertex(camera.getLocalX(worldGeomMousePos.x), camera.getLocalY(worldGeomMousePos.y));
					addVertex(camera.getLocalX(worldGeomMousePos.x), vertexList.get(vertexList.size()-1).getPoint().y);
				}
				public void mouseDragged() {}
				public void mouseReleased() {}
			});
		}
		public void mousePressed(MouseEvent e) {
			inputController.mousePressed(e);
			// vvvv Will be the actual commands I want
			// this.inputController.mousePressed(e);
			
		}
		public void mouseDragged(MouseEvent e) {
			inputController.mouseDragged(e);
		}
		public void mouseMoved(MouseEvent e) {
			//functionality for locking the Y axis to draw a flat line
			keyState.mouseMoved(e);
		}
		public void mouseReleased(MouseEvent e) {inputController.mouseReleased(e);}
		public void render(Graphics g) {
			//old drawghostvertex
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
			//the -3 accounts for the offset

			g2.drawImage(ghostVertexPic, worldGeomMousePos.x - 3, worldGeomMousePos.y - 3, null);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			if (vertexList.size() > 0) {	
				//Line2D.Double ghostLine = new Line2D.Double(vertexPoints.get(vertexPoints.size()-1), worldGeomMousePos);
				//offset by a pixel because it was always intersecting with previous line in list
				Line2D.Double ghostLine = new Line2D.Double(vertexList.get(vertexList.size()-1).getPoint().x +3, vertexList.get(vertexList.size()-1).getPoint().y,
						camera.getLocalX(worldGeomMousePos.x), camera.getLocalY(worldGeomMousePos.y));
				// if checkForIntersection(ghostLine, new Line2D.Double(vertexPoints(size()-2, vertexPoints(size()-1)
				if (vertexList.size() > 1) { //there exists at least one line already drawn:

					if (checkIfLinesIntersect(ghostLine)) {  //one of the lines are crossing
						g2.setColor(Color.RED);
						//canCreateVertices(false) <---do later EDIT: done
						
					}
					else {								// nothing's intersecting, ready to place another point
						g2.setColor(Color.PINK);
					}
				}
				else {
					g2.setColor(Color.PINK);
				}
				//######  first point is the world (local) position, and the second point is the relative position under cursor. #####
				//g2.draw(new Line2D.Double(board.camera.getLocalPosition((Point) ghostLine.getP1()), ghostLine.getP2()));
				//g2.drawLine(board.camera.getRelativeX((int)ghostLine.getX1()), board.camera.getRelativeY((int)ghostLine.getY1()),
				//board.camera.getRelativeX((int)ghostLine.getX2()), board.camera.getRelativeY((int)ghostLine.getY2()));
				camera.draw(ghostLine, g2);
			}

			//old drawVertexPoints vvvvvvv
			for (Vertex vertex: vertexList) {
				//g2.drawImage(vertexPic, board.camera.getLocalX(point.x)-3, board.camera.getLocalY(point.y)-3, null);
				vertex.draw(g2, camera);
			}

			// old drawsurfacelines vvvvvv
			g2.setColor(Color.MAGENTA);
			for (int i = 0; i < vertexList.size()-1; i++) {
				Line2D.Double tempLine = new Line2D.Double(vertexList.get(i).getPoint(), vertexList.get(i+1).getPoint());
				//g2.draw(tempLine);
				camera.draw(tempLine, g2);
			}
		}
		/** True if any intersection is found across all lines in the surfaceLines arrayList<> 
		 */
		public boolean checkIfLinesIntersect(Line2D.Double testLine){
			for (int i = 0; i < surfaceLines.size()-1; i++){
				if (surfaceLines.get(i).intersectsLine(testLine)) {
					vertexPlacementAllowed = false;
					return true;	
				}
			}
			vertexPlacementAllowed = true;
			return false;
		}
		
			public class KeyStateCtrl extends KeyState {
				@Override
				public void mouseMoved(MouseEvent e) {
					//implementation of mouseMoved for when ctrl is pressed. Will be triggered
					// by inputController
					if (vertexList.size() > 0) {
						worldGeomMousePos.setLocation(e.getX(), camera.getRelativeY((vertexList.get(vertexList.size()-1).getPoint().y)));
					}
				}
			} // end if inner inner class KeyStateAlt
	}
	// end of inner class VertexPlaceMode
	
	
/////////   INNER CLASS VERTEXSELECTMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
	public class VertexSelectMode extends WorldGeomMode {
		
		//private WorldGeomMode subMode;
		
		//protected VertexAbstract currentSelectedVertex;
		
		protected SelectedVertices selectedVertices;
		protected SelectionRectangleAbstract selectionRectangle;
		protected SelectionRectangleAbstract selectionRectangleState;
		// will use this list for when there's multiple selection possible
		//protected ArrayList<VertexAbstract> currentVertexList = new ArrayList<>();
		//private VertexAbstract nullVertex = VertexNull.getNullVertex();
		private SelectionRectangleAbstract nullSelectionRectangle;
		private Point initClickPoint;
		//worldGeomRef is inherited
		
		public VertexSelectMode() {
			initClickPoint = new Point();
			selectedVertices = new SelectedVertices(camera);
			nullSelectionRectangle = SelectionRectangleNull.getNullSelectionRectangle();
			selectionRectangle = new SelectionRectangle(Color.BLUE, Color.cyan, camera, initClickPoint);
			selectionRectangleState = nullSelectionRectangle;
			
			inputController = new InputController();
			this.inputController.createMouseBinding(MouseEvent.BUTTON1, new VertexSelectLClickEvent());
			this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON3, new CtrlVertexSelectLClickEvent());
			this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON2, new TranslateEvent());
			this.inputController.createMouseBinding(MouseEvent.ALT_MASK, MouseEvent.BUTTON2, new SelectionRectEvent());
			
			this.inputController.createKeyBinding(KeyEvent.VK_ESCAPE, new EscapeEvent());
			
			//this.inputController.createMouseBinding(MouseEvent.ALT_DOWN_MASK, MouseEvent.BUTTON1, new ShiftVertexSelectLClickEvent());
			//this.inputController.createMouseBinding(MouseEvent.ALT_MASK, MouseEvent.BUTTON1, new VertexSelectLClickEvent());
			//this.inputController.createMouseBinding(MouseEvent.BUTTON3, new VertexSelectRClickEvent());
			
			//currentSelectedVertex = nullVertex;
			
		}
		// INPUT HANDLING SECTION
		@Override
		public void mousePressed(MouseEvent e) {
			inputController.mousePressed(e);			
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			inputController.mouseDragged(e);
		}
		public void mouseMoved(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			inputController.mouseReleased(e); }
		@Override
		public void render(Graphics g) {
			Graphics2D g2 = (Graphics2D)g.create();			
			//old drawVertexPoints vvvvvvv
			for (Vertex vertex: vertexList) {
				//g2.drawImage(vertexPic, board.camera.getLocalX(point.x)-3, board.camera.getLocalY(point.y)-3, null);
				//camera.drawVertex(vertex, g);
				vertex.draw(g2, camera);
			}
			// old drawsurfacelines vvvvvv
			g2.setColor(Color.MAGENTA);
			for (int i = 0; i < vertexList.size()-1; i++) {
				Line2D.Double tempLine = new Line2D.Double(vertexList.get(i).getPoint(), vertexList.get(i+1).getPoint());
				// abstract world geometry surface lines later on
				camera.draw(tempLine, g2);
			}
			
			// section to draw selected Vertex (if one is selected)
			g2.setColor(Color.GREEN);
			//currentSelectedVertex.drawClickableBox(g2, camera);
			selectedVertices.drawClickableBox(g2, camera);
			g2.setColor(Color.BLUE);
			// vvvv section to draw selection rectangle
			selectionRectangleState.draw(g2, camera);
			
		}
		/*public void setCurrentSelectedVertex(VertexAbstract newSelectedVertex){
			currentSelectedVertex = newSelectedVertex;
		}*/
		public void checkForVertexShiftClick(Point click) {
			for (Vertex vertex: vertexList) {
				if (vertex.getClickableZone().contains(click)) {
					if (selectedVertices.contains(vertex)) 
						selectedVertices.removeSelectedVertex(vertex);
					else
						selectedVertices.addSelectedVertex(vertex);
				}
			}
		}
		public void checkForVertex(Point click) {
			//boolean atLeastOneVertexFound = false;
			//since this is the regular click method, would want to make sure any selected vertices are deselected first
			//TODO: DON'T USE THIS METHOD WITH THE SELECTION BOX PROCEDURE
			if (selectedVertices.size() > 0)
				selectedVertices.clearSelectedVertices();
			
			for (Vertex vertex: vertexList) {
				if (vertex.getClickableZone().contains(click)) 
				{
					if (selectedVertices.contains(vertex) == false) {
						//atLeastOneVertexFound = true;
						selectedVertices.addSelectedVertex(vertex);
						break;
					}
				}
			}
			/*if (atLeastOneVertexFound == false)
				selectedVertices.clearSelectedVertices();*/
		}
		public void checkForVertexInSelectionRect(Rectangle selectionRect) {
			for (Vertex vertex: vertexList) {
				if (selectionRect.contains(vertex.getClickableZone())){
					if(selectedVertices.contains(vertex) == false) {
						selectedVertices.addSelectedVertex(vertex);
					}
				}
			}
		}
		
		// ***** inner-inner classes for mouse behavior classes specific to vertex selecting
		public class VertexSelectLClickEvent implements MouseCommand{
				public void mousePressed() {
					// TODO Auto-generated method stub
					checkForVertex(camera.getLocalPosition(worldGeomMousePos));
				}
				public void mouseDragged() {
					//currentSelectedVertex.translate(camera.getLocalPosition(worldGeomMousePos));
				}
				public void mouseReleased() {}	
			} // end of VertexSelectLClickEvent inner class
		public class VertexSelectRClickEvent implements MouseCommand{
				public void mousePressed() {
					// TODO Auto-generated method stub
					//checkForVertex(camera.getLocalPosition(e.getPoint()));
					//checkForVertex(camera.getLocalPosition(worldGeomMousePos));
				}
				public void mouseDragged() {
					// TODO Auto-generated method stub
					//currentSelectedVertex.translate(camera.getLocalPosition(worldGeomMousePos));
				}
				public void mouseReleased() {
					// TODO Auto-generated method stub
				}	
			} // end of VertexSelectRClickEvent inner class
		public class CtrlVertexSelectLClickEvent implements MouseCommand{
	
				public void mousePressed() {
					// TODO Auto-generated method stub
					checkForVertexShiftClick(camera.getLocalPosition(worldGeomMousePos));
				}
				public void mouseDragged() {
					// TODO Auto-generated method stub
					//currentSelectedVertex.translate(camera.getLocalPosition(worldGeomMousePos));
				}
				public void mouseReleased() {
					// TODO Auto-generated method stub
				}
				
			} // end of ShiftVertexSelectLClickEvent inner class
		public class TranslateEvent implements MouseCommand{
			
			public void mousePressed() {
				// TODO Auto-generated method stub
				initClickPoint.setLocation(camera.getLocalPosition(worldGeomMousePos));
				selectedVertices.cloneList();
			}
			public void mouseDragged() {
				selectedVertices.translate(initClickPoint, worldGeomMousePos);
			}
			public void mouseReleased() {
				// TODO Auto-generated method stub
			}
			
		}
		public class SelectionRectEvent implements MouseCommand {

			@Override
			public void mousePressed() {
				// TODO Auto-generated method stub
				selectionRectangleState = selectionRectangle;
				initClickPoint.setLocation(camera.getLocalPosition(worldGeomMousePos));
				selectionRectangleState.setInitialRectPoint();
			}

			@Override
			public void mouseDragged() {
				// TODO Auto-generated method stub
				selectionRectangleState.translateEndPoint(camera.getLocalPosition(worldGeomMousePos));
			}

			@Override
			public void mouseReleased() {
				// TODO Auto-generated method stub
				//command to select vertices underneath box
				checkForVertexInSelectionRect(selectionRectangleState.getWrekt());
				selectionRectangleState.resetRect();
				selectionRectangleState = nullSelectionRectangle;
			}
			
		}
		public class EscapeEvent implements KeyCommand {
				@Override
				public void onPressed() {
					// TODO Auto-generated method stub
					selectedVertices.clearSelectedVertices();
				}
				public void onReleased() {}
				public void onHeld() {}
			}
	}
	//end of VertexSelectMode inner class
	
}
// end of entire class
