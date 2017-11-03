package misc;

import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import editing.MissingIcon;

import java.awt.*;

import engine.BoardAbstract;
import entities.EntityDynamic;
import entityComposites.EntityStatic;

public class SlidingMessagePopup extends EntityStatic {
	private BoardAbstract currentBoard;
	private SlidingMessageBehavior behavior;
	private Rectangle messageRect;
	private Rectangle imageRect;
	private RoundRectangle2D messageRect2d;
	private Point startingPoint;
	private String currentText;
	private static BufferedImage striderFace;
	private final static int boardHalfWidth = BoardAbstract.B_WIDTH/2;
	private final static int boardHalfHeight = BoardAbstract.B_HEIGHT/2;
	
	public SlidingMessagePopup(int xInitial, int yInitial, BoardAbstract boardRef, String message) {
		super(xInitial,yInitial);
		this.startingPoint = new Point(xInitial,yInitial);
		this.currentText = message;
		this.currentBoard = boardRef;
		//messageRect = new Rectangle(20, boardHalfHeight, 1400, 200);
		imageRect = new Rectangle(120,120);
		messageRect2d = new RoundRectangle2D.Double(startingPoint.x, startingPoint.y,1400,150,40,40);
		behavior = new SlidingMessageBehavior(this, new Point(0, startingPoint.y));
		try {
			if (!checkPath(System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +"face.jpg")) {
        		System.err.println("Image file not found.");
        	}
        	else {	    	
    	    	striderFace = ImageIO.read(new File(System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +"face.jpg"));
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.addTranslationTo();

	}
	public boolean checkPath(String path) {
    	boolean exists = false;
    	exists = new File(path).exists(); 	
    	return exists;
    }
	public void updatePosition() {
		this.getTranslationComposite().updateEntityWithComposite(this);
		this.behavior.updateAIPosition();
	}
	public void draw(Graphics g){ 
		//AffineTransform transform = new AffineTransform();
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//fill outline
		g2.setColor(Color.GREEN);
		g2.fillRoundRect((int)this.x, (int)this.y, (int)messageRect2d.getWidth(),
				(int)messageRect2d.getHeight(), (int)messageRect2d.getArcWidth(), (int)messageRect2d.getArcHeight());
		
		//fill inner frame
		g2.setColor(Color.GRAY);
		g2.fillRoundRect((int)this.x+3, (int)this.y+3, (int)messageRect2d.getWidth()-6,
				(int)messageRect2d.getHeight()-6, (int)messageRect2d.getArcWidth(), (int)messageRect2d.getArcHeight());
		
		//fill optional picture frame
		g2.setColor(Color.WHITE);
		//g2.fillRect((int)this.x + 10, (int)this.y+15, imageRect.width, imageRect.width);
		g2.drawImage(striderFace, (int)this.x+10, (int)this.y+15, 120, 130, null);
		
		//write some text
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("TimesRoman", Font.BOLD, 25));
		g2.drawString(currentText, (int)this.x+140, (int)this.y+30);
		//g2.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		//g2.drawString("Insert face", (int)this.x + 12, (int)this.y+30);
	}
	public void removeSelf() {
		this.currentBoard.getSlidingMessageQueue().remove(this);
	}
	public void setTarget(Point newTarget) {
		this.behavior.setTargetPoint(newTarget);
	}
	public void setBehavior(SlidingMessageBehavior newBehavior) {
		this.behavior = newBehavior;
	}
}
