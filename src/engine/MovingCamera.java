package engine;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
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
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import editing.worldGeom.*;
import entities.EntityDynamic;
import entityComposites.Entity;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import misc.*;
import physics.Boundary;
import physics.BoundaryPolygonal;
import physics.BoundarySide;
import physics.Vector;
import sprites.Sprite;
import utility.UtilityMath;

public class MovingCamera extends EntityStatic implements ReferenceFrame{
	
	private final byte ZOOM_SPEED = 10;
	private final int ZOOM_INCREMENT_PERCENT = 110;
	
	BoardAbstract currentBoard;
	Graphics2D gBoard;
	Graphics2D gOverlay;
	ImageObserver observer;
	double zoomFactor = 1;
	double zoomDelta = 1;
	private double minZoom = 0.5;
	private double maxZoom = 1;

	final static int boardHalfWidth = BoardAbstract.B_WIDTH/2;
	final static int boardHalfHeight = BoardAbstract.B_HEIGHT/2;
	
	final static int MAX_X = BoardAbstract.B_WIDTH;
	final static int MAX_Y = BoardAbstract.B_HEIGHT;
	
	public final static Point ORIGIN = new Point(boardHalfWidth,boardHalfHeight);
	
	EntityStatic target;
	MovementBehavior behaviorCurrent;
	MovementBehavior behaviorActive;
	MovementBehavior behaviorPaused;
	
	private boolean lockState = false;
	
	public MovingCamera(BoardAbstract testBoard , Graphics2D g2 , ImageObserver observer ){
		super(boardHalfWidth,boardHalfHeight);
		
		this.gBoard = g2;
		this.observer = observer;
		this.currentBoard = testBoard;
		this.setPos(0, 0);
		behaviorCurrent = new InactiveBehavior();
		
		init();
	}
	
	/*public MovingCamera(BoardAbstract testBoard , EntityStatic targetEntity , Graphics2D g2 ,ImageObserver observer ){
		super(boardHalfWidth,boardHalfHeight);
		
		this.gOverlay = g2;
		this.gBoard = (Graphics2D) g2.create();
		this.observer = observer;
		this.currentBoard = testBoard;
		target = targetEntity;
		this.x = target.getX();
		this.y = target.getY();
		behaviorActive = new FollowMovement.Linear(this,target);
		behaviorCurrent = behaviorActive;
		
		init();
	}*/
	
	private void init(){
		this.addTranslationComposite();
		this.addAngularComposite();
	}
	
	public void repaint(Graphics g){
		this.gOverlay = (Graphics2D) g;
		this.gBoard = (Graphics2D) g.create();
		this.gBoard.translate(boardHalfWidth,boardHalfHeight );
		this.gBoard.rotate(this.getAngularComposite().getAngleInRadians());
		this.gBoard.translate(-boardHalfWidth,-boardHalfHeight );
	}
	
	public Graphics2D getOverlayGraphics(){
		return this.gOverlay;
	}
	
	public void updatePosition(){
		
		behaviorCurrent.updateAIPosition(); //CAMERA MATH	
		zoomFactor += (zoomDelta-zoomFactor)/ZOOM_SPEED;
	}

	public double getZoom(){
		return this.zoomFactor;
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

		this.getTranslationComposite().halt();	//halt velocity		
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

		this.translationComposite.halt();
		
		behaviorPaused = behaviorCurrent;
		behaviorCurrent = new InactiveBehavior(); //make inactive behavior static singleton later
		lockState = true;
	}
	
	public void lockAtCurrentPosition(){
		
		this.translationComposite.halt();
		
		resetAngle(); 
		behaviorPaused = behaviorCurrent;
		behaviorCurrent = new InactiveBehavior();
		lockState = true;
	}
	
	public boolean isLocked(){
		return lockState;
	}
	
	public void unlock(){
		behaviorCurrent = behaviorPaused;
		lockState = false;
	}

	public void setDX(double setdx) {
		this.translationComposite.setDX(setdx/zoomFactor);
	}

	public void setDY(double setdy) {
		this.translationComposite.setDY(setdy/zoomFactor);
	}

	public void quadupleZoom( double minZoom ){
		if ( this.zoomDelta > this.minZoom ){
			this.zoomDelta = this.zoomDelta/(ZOOM_INCREMENT_PERCENT/100.0);
		}else{
			this.zoomDelta = this.minZoom;
		}
	}
	
	public void quarterZoom( double maxZoom ){
		if ( this.zoomDelta < this.maxZoom ){
			this.zoomDelta = this.zoomDelta*(ZOOM_INCREMENT_PERCENT/100.0);
		}else{
			this.zoomDelta = this.maxZoom;
		}
	}
	
	public void quadupleZoom(){
		
		this.zoomDelta = this.zoomDelta/(ZOOM_INCREMENT_PERCENT/100.0);
	}
	public void quarterZoom(){
		
		this.zoomDelta = this.zoomDelta*(ZOOM_INCREMENT_PERCENT/100.0);
	}
	
	public void setZoomLevel( double factor ){
		this.zoomDelta = factor;
	}
	
	public void resetZoom(){
		this.zoomFactor = 1;
		this.zoomDelta = 1;
	}
	
	public void zoomOutFull( double minZoom){
		this.zoomDelta = minZoom;
	}
	public void zoomInFull( double maxZoom){
		this.zoomDelta = maxZoom;
	}
	
	public void setAngle( double angleRadians ){
		this.angularComposite.setAngleInRadians(angleRadians);
	}
	
	public double getAngle(){
		return this.getAngularComposite().getAngleInRadians();
	}
	public Color getColor() {
		return gBoard.getColor();
	}
	public void resetAngle(){

		this.angularComposite.setAngleInRadians(0);
	}
	
	public void drawVertex(EditorVertexAbstract vertex, Graphics g)
	{
/*		g2.drawImage(EditorVertex.vertexPicture, 
				getRelativeX( vertex.getPoint().x-3 ),
				getRelativeY( vertex.getPoint().y-3 ), null);	*/
		draw(EditorVertex.vertexPicture,new Point(vertex.getPoint().x-3, vertex.getPoint().y-3), new AffineTransform(),1.0f);
	}
	public void drawVertexClickableBox(EditorVertexAbstract vertex, Graphics g)
	{
		/*Graphics2D g2 = (Graphics2D)g;
		g2.drawRect(vertex.getClickableZone().x - (int)this.x + boardHalfWidth,
				vertex.getClickableZone().y - (int)this.y + boardHalfHeight, 
				vertex.getClickableZone().width, vertex.getClickableZone().height);	*/	
	}
	@Deprecated
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
	public void drawOnCamera(GraphicComposite.Static sprite , AffineTransform entityTransform){
		
		AffineTransform cameraTransform = new AffineTransform();
		this.gBoard.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		cameraTransform.translate( 
				this.getRelativeX( sprite.ownerEntity().getX()) , 
				this.getRelativeY( sprite.ownerEntity().getY()) );
		
		cameraTransform.scale( zoomFactor , zoomFactor);
		
		cameraTransform.concatenate(entityTransform);
		Composite compositeBuffer = this.gBoard.getComposite();
		this.gBoard.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)sprite.getSprite().getAlpha()));
		
		BufferedImage[][] tiledImage= sprite.getSprite().getBufferedImage();
		int tileSize = sprite.getSprite().getTileDimension();
	
		cameraTransform.translate(-tileSize, 0);
		
		for ( int i = 0 ; i < tiledImage.length ; ++i ){
			
			cameraTransform.translate(tileSize, 0);
			
			for ( int j = 0 ; j < tiledImage[i].length ; ++j ){

				this.gBoard.drawImage(tiledImage[i][j], 
						cameraTransform,
						this.observer);
				cameraTransform.translate( 0 , tileSize);
			}
			
			cameraTransform.translate( 0 , -3*tileSize);
		}
		
		this.gBoard.setComposite(compositeBuffer);
	}
	@Override
	public void drawLine( Point p1, Point p2){
		
		Color buffer = gBoard.getColor();
		gBoard.setColor(Color.RED);
		
		gBoard.drawLine( 
				this.getRelativeX(p1.x), 
				this.getRelativeY(p1.y), 
				this.getRelativeX(p2.x), 
				this.getRelativeY(p2.y)
				);
		
		gBoard.setColor(buffer);
	}
	@Override
	public void drawLine( Point2D p1, Point2D p2){
		
		Color buffer = gBoard.getColor();
		gBoard.setColor(Color.RED);
		//gBoard.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		gBoard.drawLine( 
				this.getRelativeX(p1.getX()), 
				this.getRelativeY(p1.getY()), 
				this.getRelativeX(p2.getX()), 
				this.getRelativeY(p2.getY())
				);
		
		gBoard.setColor(buffer);
	}

	@Override
	public void debugDrawPolygon( Shape polygon, Color color, Point position , AffineTransform entityTransform, float alpha){ //OPTIMIZE 
		
		AffineTransform cameraTransform = new AffineTransform();
		Graphics2D g2Temp = (Graphics2D) this.gBoard.create();
		
		this.gBoard.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		cameraTransform.translate( this.getRelativeX( position.x) , this.getRelativeY( position.y) );
		//cameraTransform.concatenate(entityTransform);
		cameraTransform.scale(this.zoomFactor, this.zoomFactor);
		
		g2Temp.transform(cameraTransform);
		g2Temp.draw(polygon);
		g2Temp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2Temp.setColor(color);
		g2Temp.fill( polygon );
		g2Temp.dispose();
	}

	@Override
	public void draw(Image image , Point world_position , AffineTransform entityTransform, float alpha ){
		
		AffineTransform cameraTransform = new AffineTransform();
		Graphics2D g2Temp = (Graphics2D) this.gBoard.create();
		g2Temp.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		cameraTransform.translate( 
				this.getRelativeX( world_position.x ) + this.translationComposite.getDX(), 
				this.getRelativeY( world_position.y ) + this.translationComposite.getDX() );
		
		cameraTransform.concatenate(entityTransform);
		
		g2Temp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

		g2Temp.drawImage(image, 
				cameraTransform,
				this.observer);

		g2Temp.dispose();
		
	}
	

	/** NOTE: Must create a new shape for some reason; don't pass a reference to an existing shape. Doubles the translation calculation.**/
	public void drawShapeInWorld( Shape shape , Point worldPosition){
		
		AffineTransform cameraTransform = new AffineTransform();
		Graphics2D g2Temp = (Graphics2D) this.gBoard.create();
		
		cameraTransform.translate( this.getRelativeX( worldPosition.x ) , this.getRelativeY( worldPosition.y ) );
		cameraTransform.scale( zoomFactor , zoomFactor);
		g2Temp.transform(cameraTransform);
		g2Temp.draw( shape );
		g2Temp.dispose();
	}
	
	/**
	 * Draws Line2D relative to camera position
	 * @param line
	 * @param g
	 */
	public void drawLineInBoard( Line2D line, Graphics2D g2 ){
		
		gBoard.drawLine( 
				getRelativeX(line.getX1()) ,  
				getRelativeY(line.getY1()) ,  
				getRelativeX(line.getX2()) ,  
				getRelativeY(line.getY2()) 
		);
	}
	
	public void drawOnOverlay(Line2D line , Graphics2D g2){
		
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


	/** NOTE: Must create a new shape for some reason; don't pass a reference to an existing shape. Doubles the translation calculation.**/
	public void drawShapeInWorldSelectionRect(Shape shape, Point worldPosition, Color outlineColor, Color fillColor, float alpha, boolean isFilled) {
		
		AffineTransform cameraTransform = new AffineTransform();
		Graphics2D g2Temp = (Graphics2D) this.gBoard.create();
		g2Temp.setColor(outlineColor);
		g2Temp.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		cameraTransform.translate( this.getRelativeX( worldPosition.x ) , this.getRelativeY( worldPosition.y ) );
		cameraTransform.scale( zoomFactor , zoomFactor);
		g2Temp.transform(cameraTransform);
		g2Temp.draw( shape );
		if (isFilled) {
			g2Temp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2Temp.setColor(fillColor);
			g2Temp.fill(shape);
		}
		g2Temp.dispose();
	}
	/** NOTE: Must create a new shape for some reason; don't pass a reference to an existing shape. Doubles the translation calculation.**/

	public void drawShapeInWorld( Shape shape , Point worldPosition, Graphics2D g2){
		
		AffineTransform cameraTransform = new AffineTransform();
		Graphics2D g2Temp = (Graphics2D) gBoard.create();

		
		cameraTransform.translate( getRelativeX(worldPosition.x), getRelativeY(worldPosition.y) );
		cameraTransform.scale( zoomFactor , zoomFactor);
		
		g2Temp.transform(cameraTransform);
		
		
		g2Temp.draw( shape );
		g2Temp.dispose();
	}
	
	public void drawString( String string , Point pos ) {
		gBoard.drawString( string,
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
		gBoard.drawString( string, 
				getRelativeX(x), 
				getRelativeY(y)
		);
	}
	
	public void drawCrossInWorld( int worldX, int worldY){
		drawCross( this.getRelativeX(worldX) , this.getRelativeY(worldY) , gBoard);
	}
	
	public void drawCrossInWorld( Point point ){
		drawCross( (int)this.getRelativeX( point.getX() ) , (int)this.getRelativeY( point.getY() ) , gBoard);
	}
	
	public void drawCrossInWorld( Point point , Graphics2D g2){
		drawCross( (int)this.getRelativeX( point.getX() ) , (int)this.getRelativeY( point.getY() ) , gBoard);
	}

	public void drawCrossInWorld( Point2D point, Color color ){
		Color def = gBoard.getColor();
		gBoard.setColor(color);
		drawCross( (int)this.getRelativeX( point.getX() ) , (int)this.getRelativeY( point.getY() ) , gBoard);
		gBoard.setColor(def);
	}
	
	public void drawLineInWorld( Line2D line , Graphics2D g2 ){
		g2.drawLine(
				(int)line.getX1(),
				(int)line.getY1(),
				(int)line.getX2(),
				(int)line.getY2()
				);
		
	}
	
	public void drawCrossInWorldRelativeTo( Point2D point , Point origin , Graphics2D g2){
		drawCross( (int)this.getRelativeX( point.getX() + origin.x ) , (int)this.getRelativeY( point.getY() + origin.y ) , g2);
	}
	
	public void drawCrossInFrame( int worldX, int worldY , Graphics g ){
		drawCross( worldX , worldY , g);
	}
	
	public void drawCrossInFrame( Point point){
		drawCross( (int)point.getX() , (int)point.getY() , this.gBoard);
	}
	
	public void drawCrossInFrame( Point point , Graphics2D g2){
		drawCross( (int)point.getX() , (int)point.getY() , g2);
	}
	
	public void drawCrossInFrame( Point2D point , Graphics2D g2){
		drawCross( (int)point.getX() , (int)point.getY() , g2);
	}

	
	private int getLocalX( int x ){
		

		return (int) (((x - boardHalfWidth)/zoomFactor) + this.x );
	}
	
	private int getLocalY( int y ){

		return (int) (((y - boardHalfHeight)/zoomFactor) + this.y );
	}
	
	
	
	public Polygon convertScreenPolygonToWorldPolygon( Rectangle rect ){
		
		Point[] absoluteBoxCorners = new Point[]{
				this.getAbsolutePositionOfPointOnReferenceFrame( new Point(rect.x 				- boardHalfWidth , rect.y				- boardHalfHeight	)	),
				this.getAbsolutePositionOfPointOnReferenceFrame( new Point(rect.x + rect.width 	- boardHalfWidth , rect.y				- boardHalfHeight	)	),
				this.getAbsolutePositionOfPointOnReferenceFrame( new Point(rect.x + rect.width 	- boardHalfWidth , rect.y + rect.height - boardHalfHeight	)	),
				this.getAbsolutePositionOfPointOnReferenceFrame( new Point(rect.x 				- boardHalfWidth , rect.y + rect.height - boardHalfHeight	) 	)
			};
			
			int[] absCornersX = new int[]{ absoluteBoxCorners[0].x , absoluteBoxCorners[1].x , absoluteBoxCorners[2].x , absoluteBoxCorners[3].x };
			int[] absCornersY = new int[]{ absoluteBoxCorners[0].y , absoluteBoxCorners[1].y , absoluteBoxCorners[2].y , absoluteBoxCorners[3].y };
			
			Polygon absoluteSelectionShape = new Polygon( absCornersX , absCornersY , absoluteBoxCorners.length); //x points, y points, n points
			
			return absoluteSelectionShape;
	}
	/**
	 * 
	 * @param screenPosition
	 * @return
	 */
	public Point getAbsolutePositionOfPointOnReferenceFrame( Point2D screenPosition ){	//Like EntityStatic.getAbsolutePosition( Point p ), but deals with camera specific issues like zoom level and relative frame 
		
		double returnX = screenPosition.getX() / this.zoomFactor;
		double returnY = screenPosition.getY() / this.zoomFactor; 
		
		double cosineTheta = Math.cos( -this.angularComposite.getAngleInRadians() ); 
		double sineTheta = Math.sin( -this.angularComposite.getAngleInRadians() );
		
		Point returnPoint = new Point(
				(int)( returnX*cosineTheta - returnY*sineTheta ),
				(int)( returnX*sineTheta + returnY*cosineTheta )
		);

		returnPoint.setLocation(
			returnPoint.x + this.getX(),
			returnPoint.y + this.getY()
		);
		
		return returnPoint;	
	}
	/**
	 * BE ADVISED: Parameter positions are relative to camera ENTITY, in units of WORLD coordinates. (It ignores reference frame attributes like zoom). If you need the absolute position of 
	 * a point relative to this REFERENCE FRAME (such as a mouse position on screen), use {@link getAbsolutePositionOfPointOnReferenceFrame}
	 */
	@Override @Deprecated
	public java.awt.geom.Point2D.Double getAbsoluteDoublePositionOf(Point2D relativePoint) { return super.getAbsoluteDoublePositionOf(relativePoint); }
	/**
	 * BE ADVISED: Parameter positions are relative to camera ENTITY, in units of WORLD coordinates. (It ignores reference frame attributes like zoom). If you need the absolute position of 
	 * a point relative to this REFERENCE FRAME (such as a mouse position on screen), use {@link getAbsolutePositionOfPointOnReferenceFrame}
	 */
	@Override @Deprecated
	public Point getAbsolutePositionOf(Entity entity) { return super.getAbsolutePositionOf(entity); }
	/**
	 * BE ADVISED: Parameter positions are relative to camera ENTITY, in units of WORLD coordinates. (It ignores reference frame attributes like zoom). If you need the absolute position of 
	 * a point relative to this REFERENCE FRAME (such as a mouse position on screen), use {@link getAbsolutePositionOfPointOnReferenceFrame}
	 */
	@Override @Deprecated
	public Point getAbsolutePositionOf(Point2D relativePoint) { return super.getAbsolutePositionOf(relativePoint); }
	/**
	 * BE ADVISED: Parameter positions are relative to camera ENTITY, in units of WORLD coordinates. (It ignores reference frame attributes like zoom). If you need the absolute position of 
	 * a point relative to this REFERENCE FRAME (such as a mouse position on screen), use {@link getAbsolutePositionOfPointOnReferenceFrame}
	 */
	@Override @Deprecated
	public Point getAbsolutePositionOf(Point relativePoint) { return super.getAbsolutePositionOf(relativePoint); }
	
	/**
	 * Takes coordinates relative to the camera screen and returns the local coordinates in the world
	 * @param position_relative_to_camera
	 * @return the coordinates relative to the board/world 
	 */
	public Point getWorldPos( Point position_relative_to_camera){
//		return new Point(
//				getLocalX( (int) position_relative_to_camera.getX() ) ,
//				getLocalY( (int) position_relative_to_camera.getY() )
//				);
		return getWorldPos2(position_relative_to_camera);
	}
	
	public Point getWorldPos( Point2D position_relative_to_camera){
		
		return getWorldPos2(position_relative_to_camera);
	}
	
	public Point getWorldPos2( Point2D relativeCameraPoint ){

		double returnX = (relativeCameraPoint.getX() - boardHalfWidth ) / zoomFactor;
		double returnY = (relativeCameraPoint.getY() - boardHalfHeight ) / zoomFactor; 
		
		double cosineTheta = Math.cos( -this.getAngularComposite().getAngleInRadians() );
		double sineTheta = Math.sin( -this.getAngularComposite().getAngleInRadians() );
		
		Point returnPoint = new Point(
				(int)( returnX*cosineTheta - returnY*sineTheta )+ this.getX(),
				(int)( returnX*sineTheta + returnY*cosineTheta )+ this.getY()
		);
		
		return returnPoint ;
		
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
		behaviorActive = new FollowMovement.Linear(this,target);
		behaviorCurrent = behaviorActive;
	}
	@Override
	public Graphics2D getGraphics(){
		return this.gBoard;
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
	public void drawDebugVeronoiRegion( BoundarySide side , Graphics2D g2 ){
		
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
		
		int mod1 = (int) ( 1 / (zoomFactor )    );
		
		int mod = mod1;
		
		int s = square; //square dimension

		this.gBoard.setColor(Color.CYAN);
		this.gBoard.drawString("CAMERA ZOOM: "+(int)(zoomFactor*100)+"  "+mod, 50, 50);
		//X AXIS
		
		for ( int i = (pos.x - (int)(boardHalfWidth/zoomFactor)+200 ) / s ; i < ( (pos.x + (int)(boardHalfWidth/2/zoomFactor)-200 ) / s ) ; i++ ){ //OPTIMIZE remove testing offset when done
			this.gBoard.setColor(grid);
			this.gBoard.drawLine( getRelativeX(i*s) , 0, getRelativeX(i*s), MAX_Y);
			this.gBoard.setColor(axisColor);
			this.gBoard.drawString( ""+i*s, getRelativeX(i*s), 20);
		}
		//Y AXIS
		for ( int i = (pos.y - (int)(boardHalfHeight/zoomFactor)+200 ) / s ; i < ( (pos.y + (int)(boardHalfHeight/2/zoomFactor) ) / s ) ; i++ ){ //OPTIMIZE remove testing offset when done
			this.gBoard.setColor(grid);
			this.gBoard.drawLine( 0, getRelativeY(i*s) , MAX_X, getRelativeY(i*s));
			this.gBoard.setColor(axisColor);
			this.gBoard.drawString( ""+i*s, 5,getRelativeY(i*s));
		}

		
	}

	public double localDistance( double d ) {
		return d / this.zoomFactor;
	}
	
	
	public MovementBehavior setBehavior( MovementBehavior behavior ){
		this.behaviorCurrent = behavior;
		return behaviorCurrent;
	}

	public FollowTargetAroundPoint createRotationalCameraBehavior(EntityStatic targetEntity, Point targetPosition, Point rotationalOrigin, Double zoom){
		
		FollowTargetAroundPoint newBehaviorRotational = new FollowTargetAroundPoint( targetEntity, targetPosition ,rotationalOrigin, zoom);
		this.behaviorCurrent = newBehaviorRotational;
		return newBehaviorRotational;
	}
	
	public class FollowTargetAroundPoint extends MovementBehavior{
		
		EntityStatic target;
		Point targetPosition;
		Point rotationalOrigin;
		Double zoom;
		
		public FollowTargetAroundPoint( EntityStatic target , Point targetPosition, Point rotationalOrigin, Double zoom ){
			this.rotationalOrigin = rotationalOrigin;
			this.targetPosition = targetPosition;
			this.target = target;
			this.zoom = zoom;
		}

		
		@Override
		public void updateAIPosition() {	//get delta x and y for rotating coordinates

			Point position = target.getAbsolutePositionOf( UtilityMath.dividePoint(this.targetPosition , zoomFactor) );
			
			MovingCamera.this.setDX( ( position.x - MovingCamera.this.getX() ) / 30.0  );
			MovingCamera.this.setDY( ( position.y - MovingCamera.this.getY() ) / 30.0  );
			
			getAngularComposite().setAngleInRadians( -target.getAngularComposite().getAngleInRadians() );
			
			//zoomFactor = zoom;
		}
		
		@Override
		public Vector calculateVector() {
			return null;
		}
	}

	public void drawFocalPoint() {
		this.drawCrossInWorld((int)this.x, (int)this.y);
	}

	public void setColor(Color color) {
		gBoard.setColor(color);
	}

	
}
