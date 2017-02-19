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
	private Camera camera;
	private ArrayList<Point> vertexPoints = new ArrayList<>();


	// World Geometry Modes:
	private WorldGeomMode worldGeomMode;
	private final VertexPlaceMode vertexPlaceMode = new VertexPlaceMode();
	private final VertexSelectMode vertexSelectMode = new VertexSelectMode();
	//private final VertexTranslateMode vertexTranslateMode = new VertexTranslateMode();
	
	private ArrayList<Vertex> vertexList = new ArrayList<>();
	
	private ArrayList<Line2D.Double> surfaceLines = new ArrayList<>();
	//private ArrayList<WorldGeometry> worldGeometryEntities = new ArrayList<>();
	protected BufferedImage ghostVertexPic;
	//protected BufferedImage vertexPic;
	protected boolean keypressALT;
	private Point worldGeomMousePos;
	//private int worldGeomMode;
	/*private static final int VERTEXDRAWING_MODE = 0;
	private static final int VERTEXSELECT_MODE = 1;*/
	// private Point currentSelectedVertex;
	private boolean vertexPlacementAllowed;
	
	private int offsetX; //actual point will be within the square's center, so square must be offset.
	private int offsetY;
	

	public WorldGeometry(EditorPanel editorPanelRef, BoardAbstract board2) { 

		this.editorPanel = editorPanelRef;
		this.board = board2;
		this.camera = board2.getCamera();
		vertexPlacementAllowed = true;
		
		
		// ########### initalize modes for world geometry  ##########
		// default to placement mode
		//worldGeomMode = vertexPlaceMode;
		worldGeomMode = vertexSelectMode;
		/* test section */
		addVertex(50, 500);
		addVertex(170, 411);
		addVertex(180, 430);
		addVertex(220, 500); 
		updateSurfaceLines();
		worldGeomMousePos = new Point();
		keypressALT = false;
		ghostVertexPic = (BufferedImage)Vertex.createVertexPic(0.5f);
	}

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
			this.camera.draw(tempLine, g2);
		}
	}*/
	public void updateSurfaceLines() {
		//TODO this might be really flawed going through the entire
		// loop every time this method is called, but might be ok. Check up on this.
		for (int i = 0; i < vertexList.size()-1; i++) {
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
		/* test area 
		if ( !(worldGeomMode instanceof VertexSelectMode) ) {
			this.worldGeomMode = vertexSelectMode;
		} 
		//vertexSelectMode.setCurrentSelectedVertex(vertexList.get(vertexList.size()-1));
		 */
		updateSurfaceLines();
	}
	public void removeVertex(Vertex vertexToRemove) {
		
	}
	public void clearAllVertices() {
		vertexList.clear();
		surfaceLines.clear();
	}
	
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
		this.worldGeomMode.mouseDragged(e);
		
	}
	public void mouseMoved(MouseEvent e) {
		this.worldGeomMode.mouseMoved(e);	
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

		
		public VertexPlaceMode() {
			inputController = new InputController();
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			// vvvv Will be the actual commands I want
			// this.inputController.mousePressed(e);
			
			// vvvv this is the old stuff 
			if (vertexPlacementAllowed == true) {
				//addVertex(e.getX(), e.getY());
				addVertex(camera.getLocalX(e.getX()),
						  camera.getLocalY(e.getY()));
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (keypressALT) {
				if (vertexList.size() > 0)
					worldGeomMousePos.setLocation(e.getX(), vertexList.get(vertexList.size()-1).getPoint().y);
				else
					worldGeomMousePos.setLocation(e.getX(), e.getY());
			}
			else //shift isn't held; default running condition
				worldGeomMousePos.setLocation(e.getX(), e.getY());
			
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
			
			g2.drawImage(ghostVertexPic, worldGeomMousePos.x - 3, worldGeomMousePos.y - 3, null);
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
	}
	// end of inner class
	
	
/////////   INNER CLASS VERTEXSELECTMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
	public class VertexSelectMode extends WorldGeomMode {
		//private WorldGeomMode subMode;
		
		protected VertexAbstract currentSelectedVertex;
		// will use this list for when there's multiple selection possible
		//protected ArrayList<VertexAbstract> currentVertexList = new ArrayList<>();
		private VertexAbstract nullVertex = VertexNull.getNullVertex();
		//worldGeomRef is inherited
		
		public VertexSelectMode() {
			inputController = new InputController();
			currentSelectedVertex = nullVertex;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			/* steps:
			 * run checkForVertex(e)
			 *    ---this will check if there's a vertex under the mouse, and if so, set the current
			 *    ---selected vertex to point to this one
			 *    ---when vertex is selected, will probably draw small box around it.
			 *    ---pre-render this box at the start of method, and just call it and reposition it
			 *    ---for every iteration of the for-loop to check for collision with pointer.
			 */
			checkForVertex(camera.getLocalPosition(e.getPoint()));
			/* test section 
			currentSelectedVertex = vertexList.get(0);
			*/
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			currentSelectedVertex.translate(camera.getLocalPosition(e.getPoint()));
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
			currentSelectedVertex.drawClickableBox(g2, camera);
			g2.setColor(Color.BLUE);
			
		}
		public void setCurrentSelectedVertex(VertexAbstract newSelectedVertex){
			currentSelectedVertex = newSelectedVertex;
		}
		//VERTEX SELECTION AREA: IDENTICAL TO SPRITE SELECTION BECAUSE THE FUNCTIONALITY WORKS WELL
		public void checkForVertex(Point click) {
			for (Vertex vertex: vertexList) 
			{
				if (vertex.getClickableZone().contains(click)) {
					currentSelectedVertex = vertex;
					break;
				// TODO here, would now set substate to VertexTranslateMode I think
				}
				else {
					currentSelectedVertex = nullVertex;
				}
			}
		}
	}
	//end of inner class
	
/////////   INNER CLASS VERTEXTRANSLATEMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
	/*public class VertexTranslateMode extends WorldGeomMode {
		
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			vertexSelectMode.checkForVertex(camera.getLocalPosition(e.getPoint()));
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			//vertexSelectMode.currentSelectedVertex.translate(camera.getLocalPosition(e.getPoint()));
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
			vertexSelectMode.currentSelectedVertex.drawClickableBox(g2, camera);
			g2.setColor(Color.BLUE);
			
		}
	}*/
	// end of inner class
}
// end of entire class
