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

public class MovingCamera extends EntityDynamic implements ReferenceFrame{
	
	BoardAbstract currentBoard;
	Graphics2D graphics;
	ImageObserver observer;
	
	double zoomFactor = 1;
	
	final static int boardHalfWidth = BoardAbstract.B_WIDTH/2;
	final static int boardHalfHeight = BoardAbstract.B_HEIGHT/2;
	
	final static int MAX_X = BoardAbstract.B_WIDTH;
	final static int MAX_Y = BoardAbstract.B_HEIGHT;
	
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
		this.x = (int) position.getX();
		this.y = (int) position.getY();

		this.dx=0;	//halt velocity		
		this.dy=0;
	}
	public void setFocusForEditor(double distFromOriginalX, double distFromOriginalY) {
		this.x = (int)(distFromOriginalX);
		this.y = (int)(distFromOriginalY);
		//System.out.println("DeltaX: " + distFromOriginalX + " DeltaY: " + distFromOriginalY);
	} 
	
	/**
	 * Use this method for updating old pre-camera methods and for other debugging
	 * @param position - Camera.ORIGIN for camera cornered at 0,0 (classic)
	 */
	public void lockAtPosition(Point position){ //CHANGE TO INT INSTEAD OF FLOAT LATER
		this.x = (int) position.getX();
		this.y = (int) position.getY();

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
	
	public void addZoom(double dz){
		this.zoomFactor += dz;
	}
	
	public void setZoomLevel( double factor ){
		this.zoomFactor = factor;
	}
	
	public void resetZoom(){
		this.zoomFactor = 1;
	}
	
	public void drawVertex(EditorVertexAbstract vertex, Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		g2.drawImage(EditorVertex.vertexPicture, 
				getRelativeX( vertex.getPoint().x-3 ),
				getRelativeY( vertex.getPoint().y-3 ), null);				 
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
	
		cameraTransform.translate( 
				this.getRelativeX( sprite.ownerEntity().getX()) , 
				this.getRelativeY( sprite.ownerEntity().getY()) );
		
		cameraTransform.scale( zoomFactor , zoomFactor);
		
		cameraTransform.concatenate(entityTransform);
		
		this.graphics.drawImage(sprite.getSprite().getBufferedImage(), 
				cameraTransform,
				this.observer);
	}

	public void draw(Image image , int worldX, int worldY ){
		
		this.graphics.drawImage(image, 
				getRelativeX(worldX) , 
				getRelativeY(worldY) , 
				this.observer);
	}
	@Override
	public void debugDrawPolygon( Shape polygon, Color color, Point position , AffineTransform entityTransform, float alpha){ //OPTIMIZE 
		
		AffineTransform cameraTransform = new AffineTransform();
		Graphics2D g2Temp = (Graphics2D) this.graphics.create();
		
		this.graphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		cameraTransform.translate( this.getRelativeX( position.x) , this.getRelativeY( position.y) );
		//cameraTransform.concatenate(entityTransform);
		
		g2Temp.transform(cameraTransform);
		g2Temp.draw(polygon);
		g2Temp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2Temp.setColor(color);
		g2Temp.fill( polygon );
		g2Temp.dispose();
	}

	
	public void draw(Image image , Point world_position ){
		
		this.graphics.drawImage(image, 
				getRelativeX(world_position.x) , 
				getRelativeY(world_position.y) , 
				this.observer);
	}
	
	/**
	 * Draws Line2D relative to camera position
	 * @param line
	 * @param g
	 */
	public void draw(Line2D line){
		
		this.graphics.drawLine( 
				getRelativeX(line.getX1()) ,  
				getRelativeY(line.getY1()) ,  
				getRelativeX(line.getX2()) ,  
				getRelativeY(line.getY2()) 
		);
		
	}
	
	public void debugDraw(Line2D line , Graphics2D g2){
		
		g2.drawLine( 
				getRelativeX(line.getX1()) ,  
				getRelativeY(line.getY1()) ,  
				getRelativeX(line.getX2()) ,  
				getRelativeY(line.getY2())  
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
	
	public void drawString( String string , Point pos ) {
		graphics.drawString( string,
				getRelativeX(pos.x), 
				getRelativeY(pos.y)
			);
	}
	
	public void drawString( String string , Point2D pos , Graphics2D g2 ) {
		g2.drawString( string,
				getRelativeX(pos.getX()) , 
				getRelativeY(pos.getY())
			);
	}
	
	public void drawString( String string , int x, int y ) {
		graphics.drawString( string, 
				getRelativeX(x), 
				getRelativeY(y)
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
		return (int) ((x_relative_to_camera - boardHalfWidth )/zoomFactor + this.x  ) ;
	}
	public int getLocalX( double x_relative_to_camera){
		return (int) ((x_relative_to_camera - boardHalfWidth )/zoomFactor + this.x  ) ;
	}
	
	/**
	 * Takes ordinate relative to the camera screen and returns the local ordinate in the world
	 * @param y_relative_to_camera
	 * @return the ordinate relative to the board/world 
	 */
	public int getLocalY( int y_relative_to_camera){
		return (int) ( (y_relative_to_camera - boardHalfHeight)/zoomFactor + this.y  );
	}
	public int getLocalY( double y_relative_to_camera){
		return (int) ( (y_relative_to_camera - boardHalfHeight)/zoomFactor + this.y  );
	}
	
	/**
	 * Takes coordinates relative to the camera screen and returns the local coordinates in the world
	 * @param position_relative_to_camera
	 * @return the coordinates relative to the board/world 
	 */
	public Point getWorldPosition( Point position_relative_to_camera){
		return new Point(
				getLocalX( position_relative_to_camera.getX() ) ,
				getLocalY( position_relative_to_camera.getY() )
				);
	}
	
	public int getRelativeX( int x_relative_to_world){
		return (int) ((x_relative_to_world -  this.x)*zoomFactor + boardHalfWidth )  ;
	}
	public int getRelativeX( double  x_relative_to_world){
		return (int)(( x_relative_to_world -  (int)this.x  )*zoomFactor + boardHalfWidth ) ;
	}
	
	public int getRelativeY( int y_relative_to_world){
		return (int) ((y_relative_to_world - this.y  )*zoomFactor + boardHalfHeight) ;
	}
	public int getRelativeY( double  y_relative_to_world){
		return (int) (( y_relative_to_world -  (int)this.y )*zoomFactor + boardHalfHeight ) ;
	}
	
	public Point getRelativePoint( Point point_in_world ){
		return new Point( 
			getRelativeX( point_in_world.x ),
			getRelativeY( point_in_world.y ) 
		);
	}
	
	public Point getRelativePoint( Point2D point_in_world ){
		return new Point( 
			getRelativeX( point_in_world.getX() ),
			getRelativeY( point_in_world.getY() ) 
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

	public void drawVerticalLine( int world_x , String s , Graphics2D g2){
		g2.drawLine( getRelativeX(world_x) , 0, getRelativeX(world_x), MAX_Y);
		g2.drawString(s, getRelativeX(world_x), 20);
	}
	
	public void drawOverlayGrid( int square ,Color grid, Color axisColor){
		
		Point pos = this.getPosition();
		
		int s = square; //square dimension
		
		//X AXIS
		for ( int i = (pos.x - boardHalfWidth+40 ) / s ; i < ( (pos.x + boardHalfWidth/2 ) / s ) ; i++ ){ //OPTIMIZE remove testing offset when done
			this.graphics.setColor(grid);
			this.graphics.drawLine( getRelativeX(i*s) , 0, getRelativeX(i*s), MAX_Y);
			this.graphics.setColor(axisColor);
			this.graphics.drawString( ""+i*s, getRelativeX(i*s), 20);
		}
		//Y AXIS
		for ( int i = (pos.y - boardHalfHeight ) / s ; i < ( (pos.y + boardHalfHeight ) / s ) ; i++ ){ //OPTIMIZE remove testing offset when done
			this.graphics.setColor(grid);
			this.graphics.drawLine( 0, getRelativeY(i*s) , MAX_X, getRelativeY(i*s));
			this.graphics.setColor(axisColor);
			this.graphics.drawString( ""+i*s, 5,getRelativeY(i*s));
		}

		
	}

	public double localDistance( double d ) {
		return d / this.zoomFactor;
	}

	
}
