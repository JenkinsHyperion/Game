package engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;

import javax.swing.event.MouseInputAdapter;

import Input.InputController;
import Input.KeyBinding;
import Input.KeyCommand;
import Input.MouseCommand;
import engine.Board.MouseHandlerClass;
import engine.BoardAbstract.DiagnosticsOverlay;
import entityComposites.*;
import misc.CollisionEvent;
import physics.Boundary;
import physics.BoundaryCircular;
import physics.BoundaryFeature;
import physics.BoundaryPolygonal;
import physics.BoundarySingular;
import physics.CollisionEngine;
import physics.Force;
import physics.Vector;
import physics.VisualCollisionEngine;
import sprites.*;

public class BoardPhysicsTesting extends BoardAbstract{

	EntityStatic followerEntity = new EntityStatic("Circle",0,-300);
	EntityStatic rotateTest;
	final FollowerAI followerSleep = new Sleep();
	FollowerAI currentFollowerAI = followerSleep;
	private Force gravity;
	
	private InputController inputController = new InputController("Main editor controller");
	
	public BoardPhysicsTesting( int width, int height) {
		super(width,height);
		
		renderingEngine = new RenderingEngine(this);
		this.camera = renderingEngine.getCamera(); 
    	this.diagnosticsOverlay = renderingEngine.addOverlay( new DiagnosticsOverlay() );
    	
    	collisionEngine = new VisualCollisionEngine( this , renderingEngine );
    	
    	//inputController.createKeyBinding( KeyEvent.VK_R, new UNMASKED_R() );
    	//inputController.createKeyBinding( KeyEvent.VK_E, new UNMASKED_R() );
    	//inputController.createKeyBinding( KeyEvent.CTRL_MASK , KeyEvent.VK_R, new CTR_R() );
    	
    	//inputController.createMouseBinding( MouseEvent.BUTTON1 , new MOUSETRIGGER() );
    	//inputController.createMouseBinding( MouseEvent.BUTTON3 , new MOUSETRIGGER() );
    	//inputController.createMouseBinding( MouseEvent.CTRL_MASK , MouseEvent.BUTTON1 , new MOUSETRIGGER() );

    	//CompositeFactory.addColliderTo( followerEntity , new BoundaryPolygonal.Box(60, 60, -10, -10) );
    	CompositeFactory.addColliderTo(followerEntity, new BoundaryCircular(40,followerEntity) );
    	//CompositeFactory.addColliderTo(followerEntity, new BoundarySingular() );
    	//CompositeFactory.addColliderTo(followerEntity, new BoundaryPolygonal.Box(60, 60, -30, -30) );

    	//CompositeFactory.addGraphicFromCollider( followerEntity , followerEntity.getColliderComposite() );
    	CompositeFactory.addGraphicTo(followerEntity, new SpriteStillframe("box.png") );
    	CompositeFactory.addTranslationTo(followerEntity);
    	
    	gravity = followerEntity.getTranslationComposite().addForce(new Vector( 0,0.2 ) );
    	this.currentScene.addEntity( followerEntity );
    	
    	MouseHandlerClass myMouseHandler = new MouseHandlerClass();
  		addMouseListener(myMouseHandler);
  		addMouseMotionListener(myMouseHandler);
  		addMouseWheelListener(myMouseHandler);
  		addKeyListener(this);
        setFocusable(true);
        
        setBackground(Color.BLACK);
        
        camera.setTarget( currentScene.listEntities()[0] );
		this.camera.lockAtCurrentPosition();
		
		
		
		rotateTest = new EntityStatic("Test Ground 01",0,0);     
		
        CompositeFactory.addColliderTo( rotateTest , new BoundaryPolygonal.Box(446,100,-223,-50 ) );
    	//CompositeFactory.addColliderTo(rotateTest, new BoundaryCircular(40,rotateTest) );
        
        CompositeFactory.addGraphicTo(rotateTest, new SpriteStillframe("ground_1.png" , -223 , -53 ) );
        
    	//CompositeFactory.addRotationTo(rotateTest);
    	//rotateTest.getRotationComposite().setAngleInDegrees(30);
    	//rotateTest.getRotationComposite().setAngularVelocity(0.1);
    	currentScene.addEntity( rotateTest );
		
        EntityStatic orbiter = new EntityStatic("Orbiter",-300,0); 
        CompositeFactory.addGraphicTo( orbiter, new SpriteStillframe("box.png" , Sprite.CENTERED ) );
        //CompositeFactory.addColliderTo(orbiter, new BoundaryPolygonal.Box(20,20,-10,-10 ) );
        //CompositeFactory.addTranslationTo(child);
        currentScene.addEntity( orbiter );
        
        EntityStatic parent = new EntityStatic("parent",-300,100); 
        CompositeFactory.addGraphicTo( parent, new SpriteStillframe("box.png" , Sprite.CENTERED ) );
        CompositeFactory.addRotationTo(parent);
        parent.getRotationComposite().setAngularVelocity(0.1);
        currentScene.addEntity( parent );
        
        
        
        camera.setFocus( rotateTest.getPosition() );

        //SPACESHIP TEST
        
        
        
        EntityStatic spaceship = new EntityStatic("ship",-500,-500);
        
        	SpriteAnimated explosionSprite = new SpriteAnimated("boom.png", -200, -150, 26, 0, 400, 300, 1);
        
        CompositeFactory.addGraphicTo(spaceship, new SpriteStillframe("spaceship01.png" , Sprite.CENTERED) ); 
        
        CompositeFactory.addTranslationTo(spaceship);
        
        CompositeFactory.addRotationTo(spaceship);
        
        //CompositeFactory.addColliderTo(spaceship, new BoundaryPolygonal.Box(10, 10, -5, -5));
        CompositeFactory.addColliderTo(spaceship, new BoundaryCircular( 10 , spaceship) ); 	//Add collider
        //CompositeFactory.addColliderTo(spaceship, new BoundarySingular() ); 	//Add collider

        CompositeFactory.addScriptTo(spaceship, new EntityScript(){
        	
        	private EntityStatic target = followerEntity;
        	private float VELOCITY = 4;
        	
			@Override
			protected void updateOwner(EntityStatic ownerEntity) {
				
				Vector targetVector =  new Vector( ownerEntity.getPosition() ,  target.getPosition()  );
				
				//ownerEntity.getRotationComposite().setAngleFromVector( targetVector );
				Vector currentVector = ownerEntity.getRotationComposite().getOrientationVector();
				
				final int sign = currentVector.sign(targetVector); //1 is clockwise, -1 is counterclockwise

				ownerEntity.getRotationComposite().addAngleInRadians( Math.PI*sign/120f );

				ownerEntity.getTranslationComposite().setVelocityVector( 
						ownerEntity.getRotationComposite().getOrientationVector().multiply(VELOCITY)
				);
				
			}
			
			@Override
			protected void updateScript() {
			}
			
        });

        //currentScene.addEntity(spaceship);

        
        // TESTING OF SPACESHIP ENGINE PARTICLE EFFECT
        
        EntityStatic testParticleSpawner = new ParticleEmitter(0,0);
        //currentScene.addEntity( testParticleSpawner );
        
        //CompositeFactory.makeChildOfParentUsingPosition(testParticleSpawner, spaceship , this);

        CompositeFactory.makeChildOfParentUsingPosition(orbiter, parent , this);
        
        spaceship.getColliderComposite().setCollisionEvent( new CollisionEvent(){ 			//Make anonymous collision event to maek explosion
			@Override
			public void run(BoundaryFeature source, BoundaryFeature collidingWith) { //TAKE OUT OF EVENT
				
				EntityStatic explosion = new EntityStatic("boom", spaceship.getPosition() );
				CompositeFactory.addGraphicTo(explosion, explosionSprite );
				explosion.getGraphicComposite().getSprite().setSizeFactor( 20 );
				CompositeFactory.addLifespanTo(explosion , 52 );
				
				explosionSprite.getAnimation().restart();
				
				explosion.setPos( spaceship.getPosition() );
				spaceship.setPos( -500 , -500);
				testParticleSpawner.setPos( -500, -500 );
				currentScene.addEntity(explosion);
			}
        });
        
        
    	initializeBoard();
	}

	//INPUT CONTROL
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		inputController.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		inputController.keyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void graphicsThreadPaint(Graphics g) {
		// TODO Auto-generated method stub
		//renderingEngine.getCamera().repaint(g);
		final Point margin = new Point( 50,50 );
		
		this.renderingEngine.render( (Graphics2D)g );
		editorPanel.render( g ); 
		
		//camera.drawOverlayGrid( 100, Color.DARK_GRAY , Color.WHITE );
		
		g.setColor(Color.CYAN);

		g.drawString( this.followerEntity.getTranslationComposite().getDY() + " dy" , margin.x, 35);
		g.drawString( this.currentScene.listEntities().length + " entities" , margin.x, 50);
		g.drawString( collisionEngine.debugNumberofStaticCollidables() + " static collidables" , margin.x, 65);
		g.drawString( collisionEngine.debugNumberofDynamicCollidables() + " dynamic collidables" , margin.x, 80);
		g.drawString( collisionEngine.debugNumberOfCollisions() + " collisions" , margin.x, 95);
		g.drawString( this.followerEntity.getTranslationComposite().getAccY() + " accY" , margin.x, 110);
		g.drawString( this.updateableEntities() + " updateable entities" , margin.x, 180);
		g.drawString( this.updateableComposites() + " dynamic composites" , margin.x, 195);
		
		for ( Vector force : followerEntity.getTranslationComposite().debugForceArrows() ){
			camera.draw( force.toLine(followerEntity.getPosition() ) );
		}
		
		inputController.debugPrintInputList(100, 300, g);
		/*g.setColor(Color.ORANGE);
		for (Collider collider : this.collisionEngine.debugListActiveColliders() ){
			collider.debugDrawBoundaryDelta( renderingEngine.getCamera(), (Graphics2D) g);
		}*/
		g.setColor(Color.CYAN);
		for (Collider collider : this.collisionEngine.debugListActiveColliders() ){
			
			//if ( collider.getBoundary() instanceof BoundaryPolygonal ){
			//	camera.debugDrawPolygon( Boundary.getPolygonFromBoundary( collider.getBoundary(), collider.getOwnerEntity() ) , Color.CYAN, collider.getOwnerEntity() , new AffineTransform() );
			//}
			collider.debugDrawBoundary( renderingEngine.getCamera(), (Graphics2D) g);
		}
		
		//VORONI REIGON TESTING
		
		// y = ( 300 / 1000 )x + b
		// y = ( -1000 / 400 )x + b
		
		Point entityOnFrame = new Point( 
				camera.getRelativeX( followerEntity.getX() ),
				camera.getRelativeY( followerEntity.getY() )
				);
		
		double m = 8.0 / 1.0 ;
		double y =  (m * camera.getRelativeX( followerEntity.getX() ) + 0);
		double x =  (( y - 0 ) / m) ;
		Point point = new Point( (int)x , (int)y);
		
		double m2 = -4.0 / 10.0 ;
		double y2 =  (m2 * camera.getRelativeX( followerEntity.getX() ) + 600);
		double x2 =  (( y2 - 600 ) / m2) ;
		Point point2 = new Point( (int)x2 , (int)y2);
		
		int dist = Math.abs( entityOnFrame.y - point.y + entityOnFrame.y - point2.y  ) ; //OPTIMIZE into square check
		int range = Math.abs( point.y - point2.y  ) ;
		
		if ( dist - range > 0 ){ //OPTIMIZE TEST IF FASTER THAN DOUBLE CONDITIONAL
			g.setColor(Color.CYAN);
		}else
			g.setColor(Color.RED);
		
		//camera.drawCrossInFrame( point );
		//camera.drawCrossInFrame( point );
		//camera.drawCrossInFrame( camera.getRelativePoint( followerEntity.getPosition() ) );
		
	}

	@Override
	protected void entityThreadRun() {
		// TODO Auto-generated method stub
		camera.updatePosition();
		currentFollowerAI.update();
    	collisionEngine.checkCollisions();
	}
	
	private interface FollowerAI{
		public void update();
	}
	
	private class Sleep implements FollowerAI{
		public void update(){ /*Sleeping*/ }
	}
	
	private class Follow implements FollowerAI{
		
		private Point target;
		
		public Follow(Point target){
			this.target = target;
		}
		
		public void update(){
			
			float distX =  (float)(-camera.getRelativeX( followerEntity.getX() ) + target.getX()) ;
			float distY =	(float)(-camera.getRelativeY( followerEntity.getY() ) + target.getY()) ;
			
  			Vector followVelocity = new Vector(
  					Math.signum(distX)*(distX*distX)/50000  +  distX/20 ,    //like Linear Follow, this is a Quadratic Follow
  					Math.signum(distY)*(distY*distY)/50000  +  distY/20
  			);
  			followerEntity.getTranslationComposite().setVelocityVector( followVelocity );
		}
	}
	
	private Point mouseOrigin;
	
 	protected class MouseHandlerClass extends MouseInputAdapter implements MouseWheelListener { 		
  	    /*public int clickPositionXOffset;
  	    public int clickPositionYOffset;*/

  		@Override
  		public void mousePressed(MouseEvent e)
  		{  	
  			if (e.getButton() == MouseEvent.BUTTON2){
  				currentFollowerAI = new Follow( e.getPoint() );
  			}
  			
  			if (e.getButton() == MouseEvent.MOUSE_WHEEL){
  				System.err.println("WHEELW");
  			}
  			
  			editorPanel.mousePressed(e);

  			inputController.mousePressed(e);
  			
  			mouseOrigin = e.getPoint();
  			
  		}
  		@Override
  		public void mouseDragged(MouseEvent e) 
  		{ 		
  			editorPanel.mouseDragged(e);
  			
  			
  			
  			if (e.getButton() == MouseEvent.BUTTON2){
  				currentFollowerAI = new Follow( e.getPoint() );
  			}
  			//gravity.setVector( 0,0);
  			//rotateTest.getRotationComposite().setAngleInDegrees( new Vector( e.getX()-mouseOrigin.x , e.getY()-mouseOrigin.y ).angleFromVectorInDegrees() );
  			
  		}
  		@Override
  		public void mouseMoved(MouseEvent e){
  			editorPanel.mouseMoved(e);
  			//editorPanel.getWorldGeom().mouseMoved(e);
  		}
  		@Override
  		public void mouseReleased(MouseEvent e) 
  		{	
  			
  			inputController.mouseReleased(e);
  			
  			//followerEntity.getTranslationComposite().setVelocity( new Vector( 0,0 ) );
  			currentFollowerAI = followerSleep;
  			//gravity.setVector( 0,0.2);
  			//player.inputController.mouseReleased(e);

  			//editorPanel.getWorldGeom().mouseReleased(e);
  			
  			editorPanel.mouseReleased(e);
  		}
  		
  	}
 	
 	private class CTR_R implements KeyCommand {

		@Override
		public void onPressed() {
			
		}

		@Override
		public void onReleased() {
			
		}

		@Override
		public void onHeld() {
			
		}
 		
 	}
 	
 	private class UNMASKED_R implements KeyCommand {

		@Override
		public void onPressed() {
			
		}

		@Override
		public void onReleased() {
			
		}

		@Override
		public void onHeld() {
			
		}
 		
 	}
 	
 	private class MOUSETRIGGER implements MouseCommand {

		@Override
		public void mousePressed() {
			// TODO Auto-generated method stub
			//System.err.println("CLICK in board");
		}

		@Override
		public void mouseDragged() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased() {
			// TODO Auto-generated method stub
			
		}
 		
 	}
	
	
}
