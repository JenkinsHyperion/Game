package misc;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Color;

public class PaintOverlay {
	private Rectangle border;
	private int xTopLeft;
	private int yTopLeft;
	private int width;
	private int height;
	

	public PaintOverlay(int x, int y, int w, int h){
		border = new Rectangle();
		setxTopLeft(x);
		setyTopLeft(y);
		setWidth(w);
		setHeight(h);
		border.setRect(x, y, w, h);
	}
	
	public int getxTopLeft() {
		return xTopLeft;
	}
	public void setxTopLeft(int xTopLeft) {
		this.xTopLeft = xTopLeft;
	}
	public int getyTopLeft() {
		return yTopLeft;
	}
	public void setyTopLeft(int yTopLeft) {
		this.yTopLeft = yTopLeft;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void drawBorder(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.BLACK);
		g2.draw(border);
		g2.setColor(Color.GRAY);
		g2.fill(border);
		g2.setColor(Color.BLACK);
		g2.drawString("This is the overlay", xTopLeft+20, yTopLeft+20);
	}

}
