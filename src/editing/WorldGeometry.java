package editing;

import engine.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
	private ArrayList<Point> vertices = new ArrayList<>();
	private ArrayList<WorldGeometry> worldGeometryEntities = new ArrayList<>();
	private BufferedImage ghostVertex;
	
	public WorldGeometry(EditorPanel editorPanelRef, Board board) { 
		createGhostVertex();
		
	}
	

	public Image createGhostVertex() {
		ghostVertex = new BufferedImage(5,5, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = ghostVertex.createGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, 5, 5);
		g2.dispose();
		return ghostVertex;
	}
	public Image getGhostVertext() {
		return ghostVertex;
	}
}
