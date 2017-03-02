package editing.worldGeom;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import engine.Camera;

public class Vertex extends VertexAbstract {
	
	private Rectangle clickableZone; // the zone you can actually click
	private Point vertexPoint; // the location of the vertex point
	public static Image vertexPicture = Vertex.createVertexPic(1); // the drawn little square to represent the vertex
	public int offsetX;
	public int offsetY;
	//private Camera camera;
	/*public Vertex(int x, int y) {
		vertexPoint = new Point(x,y);
		clickableVertex = new Rectangle(vertexPoint.x-5, vertexPoint.y-5, 10, 10);
		// TODO Auto-generated constructor stub
	}*/
	public Vertex(int x, int y){
		offsetX = offsetY = 7;
		vertexPoint = new Point(x,y);
		clickableZone = new Rectangle(vertexPoint.x-offsetX, vertexPoint.y-offsetY, 13, 13);
		//this.camera = camera;
	}
	
	public static Image createVertexPic(float opacity) {
		BufferedImage temp = new BufferedImage(6,6, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = temp.createGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, 6, 6);
		g2.dispose();
		return temp;
	}
	
	@Override
	public void draw(Graphics g, Camera camera) {
		// TODO Auto-generated method stub
		camera.drawVertex(this, g); 
		
	}
	public void drawClickableBox(Graphics g, Camera camera) {
		camera.drawVertexClickableBox(this, g);
	}
	@Override
	public Point getPoint() {
		// TODO Auto-generated method stub
		return vertexPoint;
	}

	@Override
	public Rectangle getClickableZone() {
		// TODO Auto-generated method stub
		return clickableZone;
	}

	@Override
	public void translate(Point p) {
		// TODO Auto-generated method stub
		this.vertexPoint.setLocation(p);
		this.clickableZone.setLocation(vertexPoint.x-offsetX, vertexPoint.y-offsetY);
	}

	@Override
	public void translate(int x, int y) {
		// TODO Auto-generated method stub
		this.vertexPoint.setLocation(x, y);
		this.clickableZone.setLocation(vertexPoint.x-offsetX, vertexPoint.y-offsetY);
		
	}

}
