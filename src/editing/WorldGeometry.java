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
	private ArrayList<WorldGeometry> worldGeometryEntities = new ArrayList<>();
	private BufferedImage ghostVertexPic;
	private BufferedImage vertexPic;
	private int offsetX; //actual point will be within the square's center, so square must be offset.
	private int offsetY;
	
	public WorldGeometry(EditorPanel editorPanelRef, Board board) { 
		ghostVertexPic = (BufferedImage)createVertexPic(0.5f);
		vertexPic = (BufferedImage)createVertexPic(1.0f);
		/* test section adding vertices to list
		 * 
		 */
		vertexPoints.add(new Point(120, 30));
		vertexPoints.add(new Point(30, 40));
		vertexPoints.add(new Point(40, 50));
		vertexPoints.add(new Point(50, 60));
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
	public void drawGhostVertex(Graphics g, Point pos){
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
		g2.drawImage(vertexPic, pos.x, pos.y, null);
		if (vertexPoints.size() > 0) {		
			g2.setColor(Color.PINK);
			g2.draw(new Line2D.Double(vertexPoints.get(vertexPoints.size()-1), pos));
		}
		g2.dispose();
	}
	public void drawVertexPoints(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		for (Point point: vertexPoints) {
			g2.drawImage(vertexPic, point.x-3, point.y-3, null);
		}
	}
	public void drawSurfaceLines(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.MAGENTA);
		for (int i = 0; i < vertexPoints.size()-1; i++) {
			g2.draw(new Line2D.Double(vertexPoints.get(i), vertexPoints.get(i+1)));
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
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		addVertex(e.getX(),e.getY());
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
