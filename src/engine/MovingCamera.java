package engine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import editing.worldGeom.*;
import entities.EntityDynamic;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import misc.*;
import physics.BoundaryPolygonal;
import physics.Side;
import sprites.Sprite;

public class MovingCamera extends EntityDynamic implements Camera{
	
	BoardAbstract currentBoard;
	Graphics2D graphics;
	ImageObserver observer;
	
	final static int boardHalfWidth = BoardAbstract.B_WIDTH/2;
	final static int boardHalfHeight = BoardAbstract.B_HEIGHT/2;
	
	public final static Point ORIGIN = new Point(boardHalfWidth,boardHalfHeight);
	
	EntityStatic target;
	MovementBehavior behaviorCurrent;
	MovementBehavior behaviorActive;
	
	private boolean lockState = false;
	
	public MovingCamera(BoardAbstract testBoard , Graphics2D g2 , ImageObserver observer ){
		super(boardHalfWidth,boardHalfHeight);
		
		this.graphics = g2;
		this.observer = observer;
		this.currentBoard = testBoard;
		this.setPos(0, 0);
		behaviorCurrent = new InactiveBehavior();
	}
	
	public MovingCamera(BoardAbstract testBoard , EntityStatic targetEntity , Graphics2D g2 ,ImageObserver observer ){
		super(boardHalfWidth,boardHalfHeight);
		
		this.graphics = g2;
		this.observer = observer;
		this.currentBoard = testBoard;
		target = targetEntity;
		this.x = target.getX();
		this.y = target.getY();
		behaviorActive = new LinearFollow(this,target);
		behaviorCurrent = behaviorActive;
	}
	
	public void repaint(Graphics g){
		this.graphics = (Graphics2D) g;
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
	public void drawVertex(EditorVertexAbstract vertex, Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		g2.drawImage(EditorVertex.vertexPicture, 
				vertex.getPoint().x-3 - (int)this.x + boardHalfWidth,
				vertex.getPoint().y-3 - (int)this.y + boardHalfHeight, null);				 
	}
	public void drawVertexClickableBox(EditorVertexAbstract vertex, Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		g2.drawRect(vertex.getClickableZone().x - (int)this.x + boardHalfWidth,
				vertex.getClickableZone().y - (int)this.y + boardHalfHeight, 
				vertex.getClickableZone().width, vertex.getClickableZone().height);				 
	}
	public void drawRect(Rectangle rect, Graphics g, Color outlineColor, Color fillColor, float alpha) {
		//Color originalColor = g.getColor();
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(outlineColor);
		g2.drawRect(rect.x - (int)this.x + boardHalfWidth, 
					rect.y - (int)this.y + boardHalfHeight, rect.width, rect.height);
		g2.setColor(fillColor);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2.fillRect(rect.x - (int)this.x + boardHalfWidth,
					rect.y - (int)this.y + boardHalfHeight, rect.width-1, rect.height-1);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		//g2.setColor(originalColor);
	}

	/**
	 * Draws input sprite, first applying camera relative translation, and then calling on input sprite's \n
	 * getBufferedImage()
	 * @param sprite
	 * @param entityTransform
	 */
	@Override
	public void drawOnCamera(GraphicComposite sprite , AffineTransform entityTransform){
		
		AffineTransform cameraTransform = new AffineTransform();
		this.graphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		cameraTransform.translate( this.getRelativeX( sprite.ownerEntity().getX()) , 
				this.getRelativeY( sprite.ownerEntity().getY()) );
		cameraTransform.concatenate(entityTransform);
		
		this.graphics.drawImage(sprite.getSprite().getBufferedImage(), 
				cameraTransform,
				this.observer);
	}

	public void draw(Image image , int worldX, int worldY ){
		
		this.graphics.drawImage(image, 
				worldX - (int)this.x + boardHalfWidth , 
				worldY - (int)this.y + boardHalfHeight , 
				this.observer);
	}
	@Override
	public void debugDrawPolygon( Shape polygon, Color color, Point position , AffineTransform entityTransform ){ //OPTIMIZE 
		
		AffineTransform cameraTransform = new AffineTransform();
		Graphics2D g2Temp = (Graphics2D) this.graphics.create();
		
		this.graphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		cameraTransform.translate( this.getRelativeX( position.x) , this.getRelativeY( position.y) );
		cameraTransform.concatenate(entityTransform);
		
		g2Temp.transform(cameraTransform);
		
		g2Temp.setColor(color);
		g2Temp.fill( polygon );
		g2Temp.dispose();
	}

	
	public void draw(Image image , Point world_position ){
		
		this.graphics.drawImage(image, 
				world_position.x - (int)this.x + boardHalfWidth , 
				world_position.y - (int)this.y + boardHalfHeight , 
				this.observer);
	}
	
	/**
	 * Draws Line2D relative to camera position
	 * @param line
	 * @param g
	 */
	public void draw(Line2D line){
		
		this.graphics.drawLine( 
				(int)line.getX1() - (int)this.x + boardHalfWidth ,  
				(int)line.getY1() - (int)this.y + boardHalfHeight,  
				(int)line.getX2() - (int)this.x + boardHalfWidth,  
				(int)line.getY2() - (int)this.y + boardHalfHeight   
		);
		
	}
	
	public void debugDraw(Line2D line , Graphics2D g2){
		
		g2.drawLine( 
				(int)line.getX1() - (int)this.x + boardHalfWidth ,  
				(int)line.getY1() - (int)this.y + boardHalfHeight,  
				(int)line.getX2() - (int)this.x + boardHalfWidth,  
				(int)line.getY2() - (int)this.y + boardHalfHeight   
		);
		
	}
	
	public void drawDebugAxis(Line2D line, Graphics2D g2){
		
		
		
		/*g2.drawLine( 
				(int)line.getX1() - (int)this.x + boardHalfWidth ,  
				(int)line.getY1() - (int)this.y + boardHalfHeight,  
				(int)line.getX2() - (int)this.x + boardHalfWidth,  
				(int)line.getY2() - (int)this.y + boardHalfHeight   
		);*/

		g2.drawLine(
				getRelativeX( (int)line.getX1() ),  
				getRelativeY( (int)line.getY1() ),  
				getRelativeX( (int)line.getX2() ),  
				getRelativeY( (int)line.getY2() )
		);
		
	}
	
	public void drawShapeInWorld( Shape shape , Point worldPosition){
		
		AffineTransform cameraTransform = new AffineTransform();
		Graphics2D g2Temp = (Graphics2D) this.graphics.create();
		
		cameraTransform.translate( this.getRelativeX( worldPosition.x ) , this.getRelativeY( worldPosition.y ) );
		
		g2Temp.transform(cameraTransform);
		
		g2Temp.draw( shape );
		g2Temp.dispose();
	}
	
	public void drawInFrame(Line2D line){
		
		this.graphics.draw( line );
		
	}
	
	public void drawString( String string , Point pos ) {
		graphics.drawString( string,
				pos.x - (int)this.x + boardHalfWidth, 
				pos.y - (int)this.y + boardHalfHeight
			);
	}
	
	public void drawString( String string , Point2D pos , Graphics2D g2 ) {
		g2.drawString( string,
				(int)pos.getX() - (int)this.x + boardHalfWidth, 
				(int)pos.getY() - (int)this.y + boardHalfHeight
			);
	}
	
	public void drawString( String string , int x, int y ) {
		graphics.drawString( string, 
				x - (int)this.x + boardHalfWidth, 
				y - (int)this.y + boardHalfHeight
		);
	}
	
	public void drawCrossInWorld( int worldX, int worldY){
		drawCross( this.getRelativeX(worldX) , this.getRelativeY(worldY) , graphics);
	}
	
	public void drawCrossInWorld( Point point ){
		drawCross( (int)this.getRelativeX( point.getX() ) , (int)this.getRelativeY( point.getY() ) , graphics);
	}
	
	public void drawCrossInWorld( Point point , Graphics2D g2){
		drawCross( (int)this.getRelativeX( point.getX() ) , (int)this.getRelativeY( point.getY() ) , g2);
	}

	public void drawCrossInWorld( Point2D point , Graphics2D g2){
		drawCross( (int)this.getRelativeX( point.getX() ) , (int)this.getRelativeY( point.getY() ) , g2);
	}
	
	public void drawCrossInFrame( int worldX, int worldY , Graphics g ){
		drawCross( worldX , worldY , g);
	}
	
	public void drawCrossInFrame( Point point){
		drawCross( (int)point.getX() , (int)point.getY() , this.graphics);
	}
	
	public void drawCrossInFrame( Point2D point , Graphics2D g2){
		drawCross( (int)point.getX() , (int)point.getY() , g2);
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
	
	public Point getRelativePoint( Point point_in_world ){
		return new Point( 
			point_in_world.x -  (int)this.x + boardHalfWidth ,
			point_in_world.y -  (int)this.y + boardHalfHeight 
		);
	}
	
	public Point getRelativePoint( Point2D point_in_world ){
		return new Point( 
			(int)point_in_world.getX() -  (int)this.x + boardHalfWidth ,
			(int)point_in_world.getY() -  (int)this.y + boardHalfHeight 
		);
	}
	
	
    private void drawCross(int x, int y , Graphics g){
    	g.drawLine( x-3, y-3, x+3, y+3 );
		g.drawLine( x-3, y+3, x+3, y-3 );
    }

	public void setTarget( EntityStatic targetEntity ) {

		this.target = targetEntity;
		this.x = target.getX();
		this.y = target.getY();
		behaviorActive = new LinearFollow(this,target);
		behaviorCurrent = behaviorActive;
	}
	@Override
	public Graphics2D getGraphics(){
		return this.graphics;
	}
	
	@Deprecated
	public BoardAbstract getOwnerBoard(){
		return this.currentBoard;
	}
	@Override
	public ImageObserver getObserver() {
		return this.observer;
	}

	@Override
	public int getOriginX() {
		return (int)this.x;
	}

	@Override
	public int getOriginY() {
		return (int)this.y;
	}
	@Deprecated
	public void drawDebugVeronoiRegion( Side side , Graphics2D g2 ){
		
		Line2D line = side.toLine();
		
		Line2D drawLine = new Line2D.Double( 
				line.getX1(),
				line.getY1(),
				line.getX1()+(line.getY2() - line.getY1()),
				line.getY1()-(line.getX2() - line.getX1())
			);
		Line2D drawLine2 = new Line2D.Double( 
				line.getX2(),
				line.getY2(),
				line.getX2()+(line.getY2() - line.getY1()),
				line.getY2()-(line.getX2() - line.getX1())
			);
		
		g2.drawLine(
				getRelativeX( (int)drawLine.getX1() ) ,  
				getRelativeY( (int)drawLine.getY1() ),  
				getRelativeX( (int)drawLine.getX2() ),  
				getRelativeY( (int)drawLine.getY2() )	
		);
		g2.drawLine(
				getRelativeX( (int)drawLine2.getX1() ) ,  
				getRelativeY( (int)drawLine2.getY1() ),  
				getRelativeX( (int)drawLine2.getX2() ),  
				getRelativeY( (int)drawLine2.getY2() )	
		);
		
	}
	
	public void drawDebugAxis(double m, double b, Graphics2D g2) {
		
		double yInterceptRelative = getRelativeY( m*getLocalX(0) + b ) ;
		double yEndRelative = getRelativeY( m * getLocalX(BoardAbstract.B_WIDTH) + b ) ; 

		Line2D drawLine = new Line2D.Double(0,yInterceptRelative,BoardAbstract.B_WIDTH,yEndRelative);
		
		g2.draw(drawLine);
	}

	
	public void drawDebugRay(double m, double b, Point2D p , Graphics2D g2) {
		
		Point relativeOrigin = new Point( (int)getRelativeX(p.getX()) , (int)getRelativeY(p.getY()) );
		
		double yInterceptRelative = getRelativeY( m*getLocalX(0) + b ) ;
		double yEndRelative = getRelativeY( m * getLocalX(BoardAbstract.B_WIDTH) + b ) ; 
		
		
		
		Line2D drawLine = new Line2D.Double(relativeOrigin.x , relativeOrigin.y ,BoardAbstract.B_WIDTH,yEndRelative);
		
		g2.draw(drawLine);
	}
	
	public void drawDebugAxisFromLine( Line2D line, Graphics2D g2) {
		
		double m = ( line.getY2() - line.getY1() )/( line.getX2() - line.getX1() );
	
		double yInterceptRelative = ( getRelativeY(line.getY1()) - (m* getRelativeX(line.getX1()))  );
		double yEndRelative = ( m * BoardAbstract.B_WIDTH + yInterceptRelative ) ; 

		Line2D drawLine = new Line2D.Double(0,yInterceptRelative,BoardAbstract.B_WIDTH,yEndRelative);
		
		g2.draw(drawLine);

	}


	
}
