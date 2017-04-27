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

import editing.EditorPanel;
import entityComposites.EntityStatic;
import entityComposites.UpdateableComposite;
import misc.PaintOverlay;
import physics.CollisionEngine;
import saving_loading.EntityData;
import sprites.RenderingEngine;
import utility.DoubleLinkedList;
import utility.Ticket;

public abstract class BoardAbstract extends JPanel implements KeyListener {

    public static int B_WIDTH;// = 400;	
    public static int B_HEIGHT;// = 300;
	
	Timer updateEntitiesTimer;
	
	private final DoubleLinkedList<UpdateableComposite> updateablesList = new DoubleLinkedList<UpdateableComposite>();
	
	private final DoubleLinkedList<UpdateableComposite> updateableEntitiesList = new DoubleLinkedList<UpdateableComposite>();
	
	int counter;
	Canvas mainCanvas;
	
	protected OverlayComposite diagnosticsOverlay;
	
	//protected ArrayList<EntityStatic> entitiesList; 
	
	public RenderingEngine renderingEngine;
	protected MovingCamera camera;
	
	public CollisionEngine collisionEngine; 
	
	
	
	protected Scene currentScene =  new Scene(this);
	
	protected int[] speedLogDraw = new int[300];
	protected int[] speedLog = new int[300];

    private  PaintOverlay p;
    
	protected EditorPanel editorPanel;
	
	public BoardAbstract( int width , int height ){
		
		B_WIDTH = width;
	    B_HEIGHT = height;

	    this.setIgnoreRepaint(true);
	    
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
	
	protected void initializeBoard(){
		
		updateEntitiesTimer = new java.util.Timer();
	     TimerTask updateEntitiesTask = new TimerTask() {
	     	
	    	 private long time = 0;
	    	 private long deltaTime = 0;
	    	 private long speed = 0;
	    	 
	     	 @Override
	     	 public void run(){
	     		
	     		time = System.nanoTime();

	     		while ( updateablesList.hasNext() ){
	     			updateablesList.get().updateComposite();
	     		}
	     		
	     		entityThreadRun();  
	     		
	     		while ( updateableEntitiesList.hasNext() ){
	     			updateableEntitiesList.get().updateComposite();
	     		}
	     		
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
	     
	}
	
	protected void paintFrame(){
		
	}
	
	@Override
	public void paintComponent(Graphics g) {  
        super.paintComponent(g);
        
        camera.repaint(g);
        
        graphicsThreadPaint(g);
        
        //camera.overlay.drawOverlay();
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
	
	public Ticket addCompositeToUpdater( UpdateableComposite updateable ){
		return updateablesList.add(updateable);
	}
	
	public Ticket addEntityToUpdater( UpdateableComposite updateable ){
		return updateableEntitiesList.add(updateable);
	}

	protected class DiagnosticsOverlay implements Overlay{
		
		@Override
		public void paintOverlay(Graphics2D g2 , MovingCamera cam) {

			g2.setColor(new Color(0, 0, 0, 150));
	        g2.fillRect(0, 0, B_WIDTH, B_HEIGHT);
		    //DIAGNOSTIC GRAPH
		    for (int i = 0 ; i < 320 ; i += 20){
		    	g2.drawLine(45, 600-i , 1280, 600-i); //make better overlay class
		    	g2.drawString( i/20 + " ms" , 10,603-i);
	    	}	
	    
		    for (int i = 0 ; i < speedLog.length ; i++){
		    	
		    		int speed = speedLogDraw[i]/50; //1ms = 20px
		    		g2.setColor(Color.MAGENTA);
		    		g2.drawLine(3*i + 50 , 600 , 3*i + 50 , 600 - speed );
		    		g2.setColor(Color.CYAN);
		    		g2.drawLine(3*i + 50 , 600 - speed , 3*i + 50 , 600 - speed-(speedLog[i]/50) );
		    }
		    
		}
		
	}
	
	
}
