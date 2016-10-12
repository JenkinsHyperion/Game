package editing;

import engine.*;
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
	private ArrayList<Point> vertexPoints = new ArrayList<>();
	private ArrayList<Line2D.Double> surfaceLines = new ArrayList<>();
	//private ArrayList<WorldGeometry> worldGeometryEntities = new ArrayList<>();
	private BufferedImage ghostVertexPic;
	private BufferedImage vertexPic;
	private boolean keypressSHIFT;
	private Point worldGeomMousePos;
	
	private int offsetX; //actual point will be within the square's center, so square must be offset.
	private int offsetY;
	private int yClamp;
	private boolean vertexPlacementAllowed;
	
	public WorldGeometry(EditorPanel editorPanelRef, Board board) { 
		this.editorPanel = editorPanelRef;
		this.board = board;
		vertexPlacementAllowed = true;
		updateSurfaceLines();
		worldGeomMousePos = new Point();
		keypressSHIFT = false;
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
	public void drawGhostVertex(Graphics g){
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
		//the -3 accounts for the offset
		g2.drawImage(vertexPic, worldGeomMousePos.x - 3, worldGeomMousePos.y - 3, null);
		if (vertexPoints.size() > 0) {	
			//Line2D.Double ghostLine = new Line2D.Double(vertexPoints.get(vertexPoints.size()-1), worldGeomMousePos);
			//offset by a pixel because it was always intersecting with previous line in list
			Line2D.Double ghostLine = new Line2D.Double(vertexPoints.get(vertexPoints.size()-1).getX()+3, vertexPoints.get(vertexPoints.size()-1).getY(),
						worldGeomMousePos.getX(),worldGeomMousePos.getY());
			// if checkForIntersection(ghostLine, new Line2D.Double(vertexPoints(size()-2, vertexPoints(size()-1)
			if (vertexPoints.size() > 1) { //there exists at least one line already drawn:
				
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
			g2.draw(ghostLine);
		}
		g2.dispose();
	}
	public void drawVertexPoints(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		for (Point point: vertexPoints) {
			g2.drawImage(vertexPic, point.x-3, point.y-3, null);
		}
		g2.setColor(Color.WHITE);
		g2.drawString(Boolean.toString(keypressSHIFT), 50, 50);
	}
	/** True if any intersection is found across all lines in the surfaceLines arrayList<> 
	 */
	public boolean checkIfLinesIntersect(Line2D.Double testLine){
		for (Line2D.Double lineIterator: surfaceLines) {
			if (lineIterator.intersectsLine(testLine))
				return true;
		}
		return false;
		
	}
	public void drawSurfaceLines(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.MAGENTA);
		for (int i = 0; i < vertexPoints.size()-1; i++) {
			Line2D.Double tempLine = new Line2D.Double(vertexPoints.get(i), vertexPoints.get(i+1));
			g2.draw(tempLine);
		}
	}
	public void updateSurfaceLines() {
		for (int i = 0; i < vertexPoints.size()-1; i++) {
			Line2D.Double tempLine = new Line2D.Double(vertexPoints.get(i), vertexPoints.get(i+1));
			surfaceLines.add(tempLine);
		}
	}
	public Image getGhostVertexPic() {
		return ghostVertexPic;
	}
	public Image getVertexPic() {
		return vertexPic;
	}
	public void addVertex(int x, int y) {
		//deselectAllVertices()   (for when vertices can be selected)
		vertexPoints.add(new Point(x, y));
		updateSurfaceLines();
	}
	public void removeVertex(Point selectedPoint) {
		
	}
	public void clearAllVertices() {
		vertexPoints.clear();
	}
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if (editorPanel.mode== EditorPanel.WORLDGEOM_MODE) //might be reduntant, leave for now
		{
			if (vertexPlacementAllowed == true)
				addVertex(worldGeomMousePos.x, worldGeomMousePos.y);
		}
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mouseMoved(MouseEvent e) {
		if (keypressSHIFT) {
			if (vertexPoints.size() > 0)
				worldGeomMousePos.setLocation(e.getX(), vertexPoints.get(vertexPoints.size()-1).getY());
			else
				worldGeomMousePos.setLocation(e.getX(), e.getY());
		}
		else //shift isn't held; default running condition
			worldGeomMousePos.setLocation(e.getX(), e.getY());
			
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	//KEY EVENTS PASSED IN FROM BOARD
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_SHIFT && !keypressSHIFT) {
			keypressSHIFT = true;
			//yClampGate = true;
			//setYClamp(worldGeomMousePos.getY();
			//yClampGate = false;
		}
	}
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_SHIFT && keypressSHIFT)
			keypressSHIFT = false;
	}
	/* public void setYClamp(int yClamp){
		this.yClamp = yClamp;
		yClampGate = false;
	} */
	public void setShiftHeld(boolean state){
		this.keypressSHIFT = state;
	}
	public boolean getShiftHeld() {
		return this.keypressSHIFT;
	}
	public Point getWorldGeomMousePos() {
		return worldGeomMousePos;
	}

	public void setWorldGeomMousePos(Point pos) {
		this.worldGeomMousePos = pos;
	}
}
