package engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import engine.Board.MouseHandlerClass;
import engine.BoardAbstract.DiagnosticsOverlay;
import entityComposites.*;
import physics.BoundaryCircular;
import physics.BoundaryPolygonal;
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
	
	public BoardPhysicsTesting( int width, int height) {
		super(width,height);
		
		renderingEngine = new RenderingEngine(this);
		this.camera = renderingEngine.getCamera(); 
    	this.diagnosticsOverlay = renderingEngine.addOverlay( new DiagnosticsOverlay() );
    	
    	collisionEngine = new VisualCollisionEngine( this , renderingEngine );

    	//CompositeFactory.addColliderTo( followerEntity , new BoundaryPolygonal.Box(60, 60, -10, -10) );
    	CompositeFactory.addColliderTo(followerEntity, new BoundaryCircular(40,followerEntity) );
    	//CompositeFactory.addGraphicFromCollider( followerEntity , followerEntity.getColliderComposite() );
    	CompositeFactory.addGraphicTo(followerEntity, new SpriteStillframe("gf") );
    	CompositeFactory.addTranslationTo(followerEntity);
    	
    	gravity = followerEntity.getTranslationComposite().addForce(new Vector( 0,0.2 ) );
    	this.currentScene.addEntity( followerEntity );
    	
    	MouseHandlerClass myMouseHandler = new MouseHandlerClass();
  		addMouseListener(myMouseHandler);
  		addMouseMotionListener(myMouseHandler);
  		addKeyListener(this);
        setFocusable(true);
        
        setBackground(Color.BLACK);
        
        camera.setTarget( currentScene.listEntities()[0] );
		this.camera.lockAtCurrentPosition();
		
		
		
		rotateTest = new EntityStatic("Rotation Test Ground 01",0,0);     
        CompositeFactory.addColliderTo( rotateTest , new BoundaryPolygonal.Box(446,100,-223,-50 ) );
        CompositeFactory.addGraphicTo(rotateTest, new SpriteStillframe("ground_1.png" , -223 , -53 ) );
        
    	CompositeFactory.addRotationTo(rotateTest);
    	//rotateTest.getRotationComposite().setAngleInDegrees(30);
    	//rotateTest.getRotationComposite().setAngularVelocity(2);
        
        currentScene.addEntity( rotateTest );
		
        EntityStatic child = new EntityStatic("FollowerChild",-300,0); 
        CompositeFactory.addGraphicTo( child, new SpriteStillframe("box.png" , -20 , -20 ) );
        //CompositeFactory.addColliderTo(child, new BoundaryPolygonal.Box(20,20,-10,-10 ) );
        //CompositeFactory.addTranslationTo(child);
        
        currentScene.addEntity( child );
        camera.setFocus( rotateTest.getPosition() );

        //SPACESHIP TEST
        
        EntityStatic spaceship = new EntityStatic("ship",-300,0);
        CompositeFactory.addGraphicTo(spaceship, new SpriteStillframe("spaceship01.png" , Sprite.CENTERED) ); 
        CompositeFactory.addTranslationTo(spaceship);
        CompositeFactory.addRotationTo(spaceship);
        CompositeFactory.addScriptTo(spaceship, new EntityScript(){
        	
        	private EntityStatic target = followerEntity;
        	
			@Override
			protected void updateOwner(EntityStatic ownerEntity) {
				
				Vector targetVector =  new Vector( ownerEntity.getPosition(), target.getPosition() );
				
				ownerEntity.getRotationComposite().setAngleFromVector( targetVector );

				ownerEntity.getTranslationComposite().setVelocity( targetVector.multiply(0.01) );
				
			}
			
			@Override
			protected void updateScript() {
				
			}
			
        });
        currentScene.addEntity(spaceship);
        
        // TESTING OF SPACESHIP ENGINE PARTICLE EFFECT
        
        EntityStatic testParticleSpawner = new ParticleEmitter(0,0);
        currentScene.addEntity( testParticleSpawner );
        
        CompositeFactory.makeChildOfParentUsingPosition(testParticleSpawner, spaceship , this);

        
    	initializeBoard();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void graphicsThreadPaint(Graphics g) {
		// TODO Auto-generated method stub
		//renderingEngine.getCamera().repaint(g);
		this.renderingEngine.render( (Graphics2D)g );
		editorPanel.render( g ); 
		g.setColor(Color.CYAN);
		g.drawString( this.followerEntity.getTranslationComposite().getDY() + " dy" , 20, 35);
		g.drawString( this.currentScene.listEntities().length + " entities" , 20, 50);
		g.drawString( collisionEngine.debugNumberofStaticCollidables() + " static collidables" , 20, 65);
		g.drawString( collisionEngine.debugNumberofDynamicCollidables() + " dynamic collidables" , 20, 80);
		g.drawString( collisionEngine.debugNumberOfCollisions() + " collisions" , 20, 95);
		g.drawString( this.followerEntity.getTranslationComposite().getAccY() + " accY" , 20, 110);
		
		for ( Vector force : followerEntity.getTranslationComposite().debugForceArrows() ){
			camera.draw( force.toLine(followerEntity.getPosition() ) );
		}
		
		/*g.setColor(Color.ORANGE);
		for (Collider collider : this.collisionEngine.debugListActiveColliders() ){
			collider.debugDrawBoundaryDelta( renderingEngine.getCamera(), (Graphics2D) g);
		}*/
		g.setColor(Color.CYAN);
		for (Collider collider : this.collisionEngine.debugListActiveColliders() ){
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
		//camera.drawCrossInFrame( point2 );
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
  			followerEntity.getTranslationComposite().setVelocity( followVelocity );
		}
	}
	
	private Point mouseOrigin;
	
 	protected class MouseHandlerClass extends MouseInputAdapter  { 		
  	    /*public int clickPositionXOffset;
  	    public int clickPositionYOffset;*/

  		@Override
  		public void mousePressed(MouseEvent e)
  		{  	
  			if (e.getButton() == MouseEvent.BUTTON2){
  				currentFollowerAI = new Follow( e.getPoint() );
  			}
  			
  			editorPanel.mousePressed(e);

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
  			//followerEntity.getTranslationComposite().setVelocity( new Vector( 0,0 ) );
  			currentFollowerAI = followerSleep;
  			//gravity.setVector( 0,0.2);
  			//player.inputController.mouseReleased(e);
  			editorPanel.mouseReleased(e);
  			//editorPanel.getWorldGeom().mouseReleased(e);
  		}
  			
  	}
	
	
}
