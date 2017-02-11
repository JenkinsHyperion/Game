package engine;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import editing.EditorPanel;
import editing.PropertiesList;
import entities.EntityStatic;
import misc.PaintOverlay;

public abstract class BoardAbstract extends JPanel {

	Timer updateEntitiesTimer;
	int counter;
	
	private ArrayList<EntityStatic> entitiesList; 
	
	protected int[] speedLogDraw = new int[300];
	protected int[] speedLog = new int[300];

    private  PaintOverlay p;
    
	protected EditorPanel editorPanel;
	
	public BoardAbstract(){

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
	     		
	     		entityThreadRun();
	     		
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
	
	@Override
	public void paintComponent(Graphics g) {  
        super.paintComponent(g);
        
        graphicsThreadPaint(g);
    }
	
	protected abstract void graphicsThreadPaint( Graphics g);
	
	protected abstract void entityThreadRun();

	public ArrayList<EntityStatic> getEntities(){
		return entitiesList;
	}
	
	public Camera getCamera() {
		System.err.println(" ERROR: Concrete Board does not have a getCamera(). Please override. ");
		return null;
	}

	public ArrayList<EntityStatic> getStaticEntities() {
		return entitiesList;
	}
	
	public void transferEditorPanel(EditorPanel instance){
		this.editorPanel = instance; 
	}
	
}
