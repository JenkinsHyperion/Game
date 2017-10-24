package engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Input.InputController;
import Input.KeyCommand;
import editing.EditorPanel;
import entityComposites.Collider;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import entityComposites.UpdateableComposite;
import misc.PaintOverlay;
import misc.SlidingMessagePopup;
import physics.CollisionEngine;
import physics.CollisionEngine.ActiveCollider;
import saving_loading.EntityData;
import sprites.RenderingEngine;
import sprites.Sprite;
import utility.DoubleLinkedList;
import utility.ListNodeTicket;

public abstract class BoardAbstract extends JPanel implements KeyListener, MouseWheelListener{

    public static int B_WIDTH;// = 400;	
    public static int B_HEIGHT;// = 300;

    private final EntityUpdater updatingState = new EntityUpdater();
    private final InactiveEntityUpdater pausedState = new InactiveEntityUpdater();
    private EntityUpdater currentState = updatingState;

	protected Timer updateEntitiesTimer;
	//protected TimerTask updateEntitiesTask;
	
	private boolean isItterating;

	private final DoubleLinkedList<EntityStatic> updateableEntitiesList = new DoubleLinkedList<EntityStatic>();
	
	int counter;
	Canvas mainCanvas;
	
	protected OverlayComposite diagnosticsOverlay;
	
	//protected ArrayList<EntityStatic> entitiesList; 
	
	public RenderingEngine renderingEngine;
	protected MovingCamera camera;
	protected ArrayList<SlidingMessagePopup> slidingMessageQueue;
	//protected ConcurrentHashMap<Integer, SlidingMessagePopup> slidingMessageQueue;
	
	private final Console console;
	private final ConsoleActive consoleActive = new ConsoleActive();
	private final ConsoleDisabled consoleDisabled = new ConsoleDisabled();
	private ConsoleState currentConsoleState = consoleDisabled; 
	
	public CollisionEngine collisionEngine; 
	
	protected Scene currentScene =  new Scene(this);
	
	protected int[] speedLogDraw = new int[300];
	protected int[] speedLog = new int[300];
    
	protected EditorPanel editorPanel;
	protected JFrame mainFrame;

	
	public BoardAbstract( int width , int height, JFrame frame ){
		
		B_WIDTH = width;
	    B_HEIGHT = height;
	    mainFrame = frame;
	    
	    console = new Console( 20 , B_HEIGHT-200 , this);
	    //slidingMessageQueue = new ConcurrentHashMap<>();
	    slidingMessageQueue = new ArrayList<>();
	    frame.addMouseWheelListener(this);
	    frame.addKeyListener(this);
	  /*  editorPanel = new EditorPanel(this);
		editorPanel.setSize(new Dimension(240, 300));
		editorPanel.setPreferredSize(new Dimension(240, 300));*/
	}
	protected TimerTask getTheMainTask() {
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
	     return updateEntitiesTask;
	}
	protected void initializeTimerTasks() {
		updateEntitiesTimer = new java.util.Timer();
		TimerTask mainTask = getTheMainTask();
		updateEntitiesTimer.scheduleAtFixedRate( mainTask , 0 , 16);
	}
	protected void cancelTimerTasks() {
		updateEntitiesTimer.cancel();
	}
	protected void postInitializeBoard(){
		initializeTimerTasks();
		/*Timer updateEntitiesTimer = new java.util.Timer();
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
	     updateEntitiesTimer.scheduleAtFixedRate( updateEntitiesTask , 0 , 16);*/
	     //updateEntitiesTimer.scheduleAtFixedRate(task, delay, period);
	     
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

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
	}
	
	protected abstract void initEditorPanel();

	public boolean isWorking(){
		return isItterating;
	}
	
	public void setMainFrame( JFrame frame ){
		this.mainFrame = frame;
	}
	
	protected void addInputController( InputController inputController ){
		mainFrame.addKeyListener(inputController);
	}

	protected void activeRenderingDraw(){ 
		
	}
	
	protected void paintFrame(){
		
	}
	
	protected InputController getUnpausedInputController(){
		return this.updatingState.inputController;
	}
	protected InputController getPausedInputController(){
		return this.pausedState.inputController;
	}
	protected InputController getCurrentBoardInputController(){
		return this.currentState.inputController;
	}
	
	protected void pauseUpdater(){ 
		this.currentState = this.pausedState;
	}
	protected void activateUpdater(){
		this.currentState = this.updatingState;
	}
	protected void advanceUpdater(){
		//FIXME: Remove this:
		this.updatingState.update();
		
	}
	protected void advanceUpdater( byte frames ){
		for ( int i = 0 ; i < frames ; i++)
			this.updatingState.update();
	}
	
	public void activeRender( Graphics g ){

        this.currentConsoleState.render(g);
	}
	
	public static int randomInt( int min, int max ){

		Random temporaryRandom = new Random();
		int returnInt = temporaryRandom.nextInt( max - min );
		temporaryRandom = null;
		return min + returnInt;
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
	
	public MovingCamera getCamera() throws NullPointerException{
		return this.camera;
	}
	public EditorPanel getEditorPanel() {
		return this.editorPanel;
	}
	/*public ConcurrentHashMap<Integer, SlidingMessagePopup> getSlidingMessageQueue() {
		return this.slidingMessageQueue;
	}*/
	public ArrayList<SlidingMessagePopup> getSlidingMessageQueue() {
		return this.slidingMessageQueue;
	}
	public void transferEditorPanel(EditorPanel instance){
		this.editorPanel = instance; 
	}
	
	public Scene getCurrentScene(){
		return this.currentScene;
	}
	
	public void createNewScene(Scene scene){
		this.currentScene = scene;
	}
	
	//ENTITY ADDING AND NOTIFYING
	
	public void addStaticEntity(EntityStatic entity) {
		this.currentScene.addEntity( entity );
	}
	
	public void notifyGraphicsAddition(GraphicComposite graphicsComposite){
		this.renderingEngine.addGraphicsCompositeToRenderer(graphicsComposite);
	}
	
	public ListNodeTicket addEntityToUpdater( EntityStatic entity ){
		return updateableEntitiesList.add(entity);
	}

	protected int updateableEntities(){
		return updateableEntitiesList.size();
	}
	
	protected class BoundaryOverlay implements Overlay{
		
		private final Color inactiveColor = new Color(255,255,255);
		
		@Override
		public void paintOverlay(Graphics2D g2 , MovingCamera cam) {
			
			g2.setColor(Color.CYAN);
			for ( Collider collider : collisionEngine.debugListActiveColliders() ){
				collider.debugDrawBoundary(cam, g2);
				cam.drawCrossInWorld(collider.getOwnerEntity().getPosition(), g2 );
			}
			
			g2.drawString( "Updateable entities: "+BoardAbstract.this.updateableEntities() , 400, 20);

		}
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
		protected boolean isPaused;
		protected InputController inputController;
		
		protected EntityUpdater( InputController inputController ){
			this.inputController = inputController;
		}
		
		protected EntityUpdater(){
			isPaused = false;
			inputController = new InputController( "Abstract Board Updating Input Controller" ); 
			
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

				public void onPressed(){
					pauseUpdater();
					//setIsPausedAndNotifyThreads(true);
					//test: 
					cancelTimerTasks();
				}
			});

		}
		@Deprecated
		public synchronized void setIsPausedAndNotifyThreads(boolean choice) {
			this.isPaused = choice;
			notifyAll();
		}
		public void update(){
     		
			isItterating = true;
			
     		while ( updateableEntitiesList.hasNext() ){
     			updateableEntitiesList.get().updateEntity();
     		}
     		
     		entityThreadRun(); 
     		
     		isItterating = false;
		}
		
	}
	
	private class InactiveEntityUpdater extends EntityUpdater{
		
		protected InactiveEntityUpdater(){
			super( new InputController("Board Abstract Pauced Input Controller") );
			
			inputController.createKeyBinding(KeyEvent.VK_PAUSE, new KeyCommand(){
		    	public void onPressed(){
		    			activateUpdater();
		    			initializeTimerTasks();
		    	}
		    });
			inputController.createKeyBinding(KeyEvent.VK_DIVIDE, new KeyCommand(){
		    	public void onPressed(){
		    			advanceUpdater();
		    			//getTheMainTask().run();
		    	}
		    });
		}
		
		public void update(){
			//FIXME STOP TIMER RATHER THAN DOING NOTHING at 60FPS
			System.err.println("BoardAbstract#update(): current thread: " + Thread.currentThread()); 
			/*while (isPaused) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		}	
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		this.currentState.inputController.keyPressed(e);
		this.currentConsoleState.keyPressed(e);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		this.currentState.inputController.keyReleased(e);
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	
	protected int updateableEntitiesNumber(){
		return this.updateableEntitiesList.size();
	}
	
}
