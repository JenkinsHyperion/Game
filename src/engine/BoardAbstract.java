package engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.JPanel;

import Input.InputController;
import Input.KeyCommand;
import editing.EditorPanel;
import entityComposites.EntityStatic;
import entityComposites.UpdateableComposite;
import misc.PaintOverlay;
import physics.CollisionEngine;
import saving_loading.EntityData;
import sprites.RenderingEngine;
import utility.DoubleLinkedList;
import utility.ListNodeTicket;

public abstract class BoardAbstract extends JPanel implements KeyListener{

    public static int B_WIDTH;// = 400;	
    public static int B_HEIGHT;// = 300;
    
    private final EntityUpdater activeUpdater = new EntityUpdater();
    private final InactiveEntityUpdater inactiveUpdater = new InactiveEntityUpdater();
    private EntityUpdater currentState = activeUpdater;

	Timer updateEntitiesTimer;
	
	private final DoubleLinkedList<UpdateableComposite> updateablesList = new DoubleLinkedList<UpdateableComposite>();
	
	private final DoubleLinkedList<UpdateableComposite> updateableEntitiesList = new DoubleLinkedList<UpdateableComposite>();
	
	int counter;
	Canvas mainCanvas;
	
	protected OverlayComposite diagnosticsOverlay;
	
	//protected ArrayList<EntityStatic> entitiesList; 
	
	public RenderingEngine renderingEngine;
	protected MovingCamera camera;
	
	private final Console console;
	private final ConsoleActive consoleActive = new ConsoleActive();
	private final ConsoleDisabled consoleDisabled = new ConsoleDisabled();
	private ConsoleState currentConsoleState = consoleDisabled; 
	
	public CollisionEngine collisionEngine; 
	
	protected Scene currentScene =  new Scene(this);
	
	protected int[] speedLogDraw = new int[300];
	protected int[] speedLog = new int[300];
    
	protected EditorPanel editorPanel;
	
	protected InputController inputController = new InputController("Abstract Board Input Controller");
	
	public BoardAbstract( int width , int height ){
		
		B_WIDTH = width;
	    B_HEIGHT = height;
	    
	    console = new Console( 20 , B_HEIGHT-200 , this);
	    inputController.createKeyBinding( 192 , new KeyCommand(){ // 192 is grave accent / tilde key 
	    	
	    	private boolean consoleIsVisible = false;
	    	public void onPressed(){
	    		diagnosticsOverlay.toggle();
	    		if (consoleIsVisible){
	    			currentConsoleState = consoleDisabled;
	    			consoleIsVisible=false;
	    		}else{
	    			currentConsoleState = consoleActive;
	    			consoleIsVisible=true;
	    		}
	    	}
	    });
	    inputController.createKeyBinding(KeyEvent.VK_PAUSE, new KeyCommand(){
	    	private boolean isPaused = false;
	    	public void onPressed(){
	    		if (isPaused){
	    			activateUpdater();
	    			isPaused=false;
	    		}else{
	    			pauseUpdater();
	    			isPaused=true;
	    		}
	    	}
	    });
	    
	    this.setIgnoreRepaint(true);
	    
	}
	
	protected void postInitializeBoard(){
		
		updateEntitiesTimer = new java.util.Timer();
	     TimerTask updateEntitiesTask = new TimerTask() {
	     	
	    	 private long time = 0;
	    	 private long deltaTime = 0;
	    	 private long speed = 0;
	    	 
	     	 @Override
	     	 public void run(){
	     		
	     		time = System.nanoTime();

	     		currentState.update();
	     		
	     		deltaTime = System.nanoTime() ;
	     		speed = deltaTime - time;

				speedLog[counter] = (int)(speed/1000);
	     		
	     		if (counter < 299)
	     			counter++;
	     		else 
	     			counter = 0;
	     	 }
	     };      
	     updateEntitiesTimer.scheduleAtFixedRate( updateEntitiesTask , 0 , 16);
	     
	     // PAINT RENDERING THREAD #############################################
	     
	     ActionListener repaintUpdateTaskSwing = new ActionListener() {
	        	
	        	private long time = 0;
	            private long deltaTime = 0;
	            private long speed = 0;
	            
	        	@Override
	        	public void actionPerformed(ActionEvent e) {
	        		time = System.nanoTime();
	        		
	        		repaint();
	        		
	        		deltaTime = System.nanoTime() ;
	        		speed = deltaTime - time;
	        		
	        		speedLogDraw[counter] = (int)(speed/1000);
	        	}
	     };
	     javax.swing.Timer repaintTimer = new javax.swing.Timer(16, repaintUpdateTaskSwing);
	     repaintTimer.setRepeats(true);
	     repaintTimer.start();
	     
	}
	
	protected void paintFrame(){
		
	}
	
	protected void pauseUpdater(){ 
		this.currentState = this.inactiveUpdater;
	}
	protected void activateUpdater(){
		this.currentState = this.activeUpdater;
	}
	
	
	@Override
	public void paintComponent(Graphics g) {  
        super.paintComponent(g);
        
        camera.repaint(g);
        
        this.currentConsoleState.render(g);
        //camera.overlay.drawOverlay();
        g.drawString("ENTITIES UPDATED: "+this.updateableEntitiesList.size() , 20,20 );
        //diagnostics.paintOverlay( (Graphics2D)g, camera);
    }
	
	private interface ConsoleState{
		abstract void render(Graphics g );
		abstract void keyPressed(KeyEvent e);
	}
	private class ConsoleDisabled implements ConsoleState{
		public void render(Graphics g){
	        graphicsThreadPaint(g);  
		}
		public void keyPressed(KeyEvent e){
			
		}
	}
	private class ConsoleActive implements ConsoleState{
		public void render(Graphics g ){
	        graphicsThreadPaint(g);    
	        console.drawConsole( (Graphics2D) g );
		}
		public void keyPressed(KeyEvent e){
			console.inputEvent(e);
		}
	}
	
	protected abstract void graphicsThreadPaint( Graphics g);
	
	protected abstract void entityThreadRun();

	public EntityStatic[] listCurrentSceneEntities(){
		return this.currentScene.listEntities();
	}
	
	public MovingCamera getCamera() {
		return this.camera;
	}
	
	public void transferEditorPanel(EditorPanel instance){
		this.editorPanel = instance; 
	}
	
	public void createNewScene(Scene scene){
		this.currentScene = scene;
	}
	
	
	public void addStaticEntity(EntityStatic entity) {
		this.currentScene.addEntity( entity );
	}
	
	public ListNodeTicket addCompositeToUpdater( UpdateableComposite updateable ){
		return updateablesList.add(updateable);
	}
	
	public ListNodeTicket addEntityToUpdater( UpdateableComposite entity ){
		return updateableEntitiesList.add(entity);
	}

	protected int updateableEntities(){
		return updateableEntitiesList.size();
	}
	protected int updateableComposites(){
		return updateablesList.size();
	}
	
	protected class DiagnosticsOverlay implements Overlay{
		
		@Override
		public void paintOverlay(Graphics2D g2 , MovingCamera cam) {

			g2.setColor(new Color(0, 0, 0, 150));
	        g2.fillRect(0, 0, B_WIDTH, B_HEIGHT);
		    //DIAGNOSTIC GRAPH
	        g2.setColor(Color.CYAN);
		    for (int i = 0 ; i < 320 ; i += 20){
		    	g2.drawLine(45, 500-i , 1280, 500-i); 
		    	g2.drawString( i/20 + " ms" , 10,603-i);
	    	}	
	    
		    for (int i = 0 ; i < speedLog.length ; i++){
		    	
		    		int speed = speedLogDraw[i]/50; //1ms = 20px
		    		g2.setColor(Color.MAGENTA);
		    		g2.drawLine(3*i + 50 , 500 , 3*i + 50 , 500 - speed );
		    		g2.setColor(Color.CYAN);
		    		g2.drawLine(3*i + 50 , 500 - speed , 3*i + 50 , 500 - speed-(speedLog[i]/50) );
		    }
		    
		}
		
	}
	
	
	private class EntityUpdater{
		
		public void update(){
			
     		while ( updateablesList.hasNext() ){
     			updateablesList.get().updateComposite();
     		}
     		
     		while ( updateableEntitiesList.hasNext() ){
     			updateableEntitiesList.get().updateComposite();
     		}
     		
     		entityThreadRun(); 
		}
		
	}
	
	private class InactiveEntityUpdater extends EntityUpdater{
		
		public void update(){
			//DO NOTHING
		}	
	}
	@Override
	public void keyPressed(KeyEvent e) {
		this.inputController.keyPressed(e);
		this.currentConsoleState.keyPressed(e);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		this.inputController.keyReleased(e);
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	protected int updateableEnttiiesNumber(){
		return this.updateableEntitiesList.size();
	}
	
}
