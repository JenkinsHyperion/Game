package engine;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Line2D;

import entities.Entity;
import entities.EntityDynamic;
import misc.*;
import sprites.Sprite;

public class Camera extends EntityDynamic{
	
	Board currentBoard;
	final static int boardHalfWidth = Board.B_WIDTH/2;
	final static int boardHalfHeight = Board.B_HEIGHT/2;
	
	final static Point ORIGIN = new Point(boardHalfWidth,boardHalfHeight);
	
	EntityDynamic target;
	MovementBehavior behaviorCurrent;
	MovementBehavior behaviorActive;
	
	private boolean lockState = false;
	
	public Camera(Board currentBoard){
		super(boardHalfWidth,boardHalfHeight);
		
		this.currentBoard = currentBoard;
		target = currentBoard.player;
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
	/**
	 * Draws sprite image relative to camera position
	 * @param sprite
	 * @param g
	 */
	public void draw(Sprite sprite , Graphics g){
		
		g.drawImage(sprite.getImage(), 
				sprite.owner.getX() + sprite.getOffsetX() - (int)this.x + boardHalfWidth , 
				sprite.owner.getY() + sprite.getOffsetY() - (int)this.y + boardHalfHeight , 
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
	
	/**
	 * Takes ordinate relative to the camera screen and returns the local ordinate in the world
	 * @param x_relative_to_camera
	 * @return the ordinate relative to the board/world 
	 */
	public int getLocalX( int x_relative_to_camera){
		return x_relative_to_camera - (int)this.x + boardHalfWidth;
	}
	
	/**
	 * Takes ordinate relative to the camera screen and returns the local ordinate in the world
	 * @param y_relative_to_camera
	 * @return the ordinate relative to the board/world 
	 */
	public int getLocalY( int y_relative_to_camera){
		return y_relative_to_camera - (int)this.y + boardHalfHeight;
	}
	
	/**
	 * Takes coordinates relative to the camera screen and returns the local coordinates in the world
	 * @param position_relative_to_camera
	 * @return the coordinates relative to the board/world 
	 */
	public Point getLocalPosition( Point position_relative_to_camera){
		return new Point(
				(int)(position_relative_to_camera.getX() - (int)this.x + boardHalfWidth),
				(int)(position_relative_to_camera.getY() - (int)this.y + boardHalfHeight)
				);
	}
}
