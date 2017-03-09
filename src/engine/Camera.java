package engine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import editing.worldGeom.*;
import entities.Entity;
import entities.EntityDynamic;
import entities.EntityStatic;
import misc.*;
import sprites.Sprite;

public class Camera extends EntityDynamic{
	
	BoardAbstract currentBoard;
	final static int boardHalfWidth = Board.B_WIDTH/2;
	final static int boardHalfHeight = Board.B_HEIGHT/2;
	
	public final static Point ORIGIN = new Point(boardHalfWidth,boardHalfHeight);
	
	EntityStatic target;
	MovementBehavior behaviorCurrent;
	MovementBehavior behaviorActive;
	
	private boolean lockState = false;
	
	public Camera(BoardAbstract testBoard  ){
		super(boardHalfWidth,boardHalfHeight);
		
		this.currentBoard = testBoard;
		this.setPos(0, 0);
		behaviorActive = new InactiveBehavior();
		behaviorCurrent = behaviorActive;
	}
	
	public Camera(BoardAbstract testBoard , EntityStatic targetEntity){
		super(boardHalfWidth,boardHalfHeight);
		
		this.currentBoard = testBoard;
		target = targetEntity;
		this.x = target.getX();
		this.y = target.getY();
		behaviorActive = new LinearFollow(this,target);
		behaviorCurrent = behaviorActive;
	}
	
	public void updatePosition(){
		super.updatePosition();	
		behaviorCurrent.updateAIPosition(); //CAMERA MATH	
	}
	
	public Point getFocus(){
		return new Point((int)this.x,(int)this.y);
	}
	/**
	 * Set the current focus of the camera. Useful for panning.
	 * @param position
	 */
	public void setFocus(Point position){ //CHANGE TO INT INSTEAD OF FLOAT LATER
		this.x = (float) position.getX();
		this.y = (float) position.getY();

		this.dx=0;	//halt velocity		
		this.dy=0;
	}
	public void setFocusForEditor(double distFromOriginalX, double distFromOriginalY) {
		this.x = (float)(distFromOriginalX);
		this.y = (float)(distFromOriginalY);
		//System.out.println("DeltaX: " + distFromOriginalX + " DeltaY: " + distFromOriginalY);
	} 
	/**
	 * translate(): for using keyboard to move camera
	 * @param dx
	 * @param dy
	 */
	public void translate(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	/**
	 * Use this method for updating old pre-camera methods and for other debugging
	 * @param position - Camera.ORIGIN for camera cornered at 0,0 (classic)
	 */
	public void lockAtPosition(Point position){ //CHANGE TO INT INSTEAD OF FLOAT LATER
		this.x = (float) position.getX();
		this.y = (float) position.getY();

		this.dx=0;	//halt velocity		
		this.dy=0;
		
		behaviorCurrent = new InactiveBehavior(); //make inactive behavior static singleton later
		lockState = true;
	}
	
	public void lockAtCurrentPosition(){
		
		this.dx=0; //halt velocity
		this.dy=0;
		
		behaviorCurrent = new InactiveBehavior();
		lockState = true;
	}
	
	public boolean isLocked(){
		return lockState;
	}
	
	public void unlock(){
		behaviorCurrent = behaviorActive;
		lockState = false;
	}
	public void drawVertex(VertexAbstract vertex, Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		g2.drawImage(Vertex.vertexPicture, 
				vertex.getPoint().x-3 - (int)this.x + boardHalfWidth,
				vertex.getPoint().y-3 - (int)this.y + boardHalfHeight, null);				 
	}
	public void drawVertexClickableBox(VertexAbstract vertex, Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		g2.drawRect(vertex.getClickableZone().x - (int)this.x + boardHalfWidth,
				vertex.getClickableZone().y - (int)this.y + boardHalfHeight, 
				vertex.getClickableZone().width, vertex.getClickableZone().height);				 
	}
	public void drawRect(Rectangle rect, Graphics g, Color outlineColor, Color fillColor) {
		Color originalColor = g.getColor();
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(outlineColor);
		g2.drawRect(rect.x - (int)this.x + boardHalfWidth, 
					rect.y - (int)this.y + boardHalfHeight, rect.width, rect.height);
		g2.setColor(fillColor);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
		g2.fillRect(rect.x - (int)this.x + boardHalfWidth,
					rect.y - (int)this.y + boardHalfHeight, rect.width-1, rect.height-1);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2.setColor(originalColor);
	}
	/**
	 * Draws sprite image relative to camera position
	 * @param sprite
	 * @param g
	 */
	public void draw(Sprite sprite , Graphics2D g2){
		
		//g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g2.drawImage(sprite.getImage(), 
				sprite.owner.getX() + sprite.getOffsetX() - (int)this.x + boardHalfWidth , 
				sprite.owner.getY() + sprite.getOffsetY() - (int)this.y + boardHalfHeight , 
				//100,
				//100,
				null);
	}
	
	public void drawModded(Sprite sprite , AffineTransform transform , Graphics2D g2){
		
		g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		transform.translate( - (int)this.x + boardHalfWidth  ,  - (int)this.y + boardHalfHeight  );
		
		g2.drawImage(sprite.getImage(), 
				transform,
				null);
	}
	
	public void draw(Image image , Graphics g , int worldX, int worldY ){
		
		g.drawImage(image, 
				worldX - (int)this.x + boardHalfWidth , 
				worldY - (int)this.y + boardHalfHeight , 
				null);
	}
	
	/**
	 * Draws Line2D relative to camera position
	 * @param line
	 * @param g
	 */
	public void draw(Line2D line , Graphics g){
		
		g.drawLine( 
				(int)line.getX1() - (int)this.x + boardHalfWidth ,  
				(int)line.getY1() - (int)this.y + boardHalfHeight ,  
				(int)line.getX2() - (int)this.x + boardHalfWidth ,  
				(int)line.getY2() - (int)this.y + boardHalfHeight   
		);
		
	}
	
	public void drawString( String string , int x, int y , Graphics g) {
		g.drawString(string, 
				x - (int)this.x + boardHalfWidth, 
				y - (int)this.y + boardHalfHeight
		);
	}
	
	public void drawCrossOnCamera( int worldX, int worldY , Graphics g ){
		drawCross( this.getRelativeX(worldX) , this.getRelativeY(worldY) , g);
	}
	
	public void drawCrossOnCamera( Point point , Graphics g ){
		drawCross( (int)this.getRelativeX( point.getX() ) , (int)this.getRelativeY( point.getY() ) , g);
	}
	
	public void drawCrossInWorld( int worldX, int worldY , Graphics g ){
		drawCross( worldX , worldY , g);
	}
	
	public void drawCrossInWorld( Point point , Graphics g ){
		drawCross( (int)point.getX() , (int)point.getY() , g);
	}
	
	/**
	 * Takes ordinate relative to the camera screen and returns the local ordinate in the world
	 * @param x_relative_to_camera
	 * @return the ordinate relative to the board/world 
	 */
	public int getLocalX( int x_relative_to_camera){
		return x_relative_to_camera +  (int)this.x - boardHalfWidth  ;
	}
	public double getLocalX( double x_relative_to_camera){
		return x_relative_to_camera +  (int)this.x - boardHalfWidth  ;
	}
	
	/**
	 * Takes ordinate relative to the camera screen and returns the local ordinate in the world
	 * @param y_relative_to_camera
	 * @return the ordinate relative to the board/world 
	 */
	public int getLocalY( int y_relative_to_camera){
		return y_relative_to_camera +  (int)this.y - boardHalfHeight ;
	}
	public double getLocalY( double y_relative_to_camera){
		return y_relative_to_camera +  (int)this.y - boardHalfHeight ;
	}
	
	/**
	 * Takes coordinates relative to the camera screen and returns the local coordinates in the world
	 * @param position_relative_to_camera
	 * @return the coordinates relative to the board/world 
	 */
	public Point getLocalPosition( Point position_relative_to_camera){
		return new Point(
				(int)(position_relative_to_camera.getX() + (int)this.x - boardHalfWidth),
				(int)(position_relative_to_camera.getY() + (int)this.y - boardHalfHeight)
				);
	}
	
	public int getRelativeX( int x_relative_to_world){
		return x_relative_to_world -  (int)this.x + boardHalfWidth  ;
	}
	public double getRelativeX( double  x_relative_to_world){
		return x_relative_to_world -  (int)this.x + boardHalfWidth  ;
	}
	
	public int getRelativeY( int y_relative_to_world){
		return y_relative_to_world -  (int)this.y + boardHalfHeight  ;
	}
	public double getRelativeY( double  y_relative_to_world){
		return y_relative_to_world -  (int)this.y + boardHalfHeight  ;
	}
	
	
    private void drawCross(int x, int y , Graphics g){
    	g.drawLine( x-3, y-3, x+3, y+3 );
		g.drawLine( x-3, y+3, x+3, y-3 );
    }
	
}
