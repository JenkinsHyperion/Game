package engine;

import javax.swing.*;

import editing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MainWindow implements KeyListener, MouseListener{
	private static BoardAbstract board;
	private static EditorPanel editorPanel;
	private JSplitPane splitPane;
    private boolean F1pressed = false;
    private Dimension editorPanelMinSize;
    
    private static boolean running = false;
    
    private static int width;
    private static int height;

    private static BufferStrategy bufferStrat;
    
	public MainWindow() {
		
		//TESTING window size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		System.out.println("Resolution set to "+ width + " by " + height);
		
		editorPanelMinSize = new Dimension(220,300);
		//board = new BoardPhysicsTesting(width,height);
		//board = new Board(width,height);
		board = new TestBoard(width,height);
		
		board.setPreferredSize(new Dimension(BoardAbstract.B_WIDTH, BoardAbstract.B_HEIGHT));
		board.setMinimumSize(new Dimension(BoardAbstract.B_WIDTH, BoardAbstract.B_HEIGHT));
		
		editorPanel = new EditorPanel(board);
		editorPanel.setSize(new Dimension(240, 300));
		editorPanel.setPreferredSize(new Dimension(240, 300));
		JScrollPane editorPanelScrollPane = new JScrollPane(editorPanel);
		editorPanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//editorPanel.setMinimumSize(editorPanelMinSize);
		board.transferEditorPanel(editorPanel);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanelScrollPane, board); // 
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(editorPanel.getWidth());
		//board.setFocusable(true);
		
		splitPane.setSize(new Dimension(board.getWidth() + editorPanel.getWidth(), 300));
		splitPane.setMinimumSize(new Dimension(board.getWidth() + editorPanel.getWidth(), 300));
		
		
	}
	public JSplitPane getSplitPane() {
		return splitPane;
	}
	
	@Deprecated
	//Not being used anymore
	public static void showGUI(JFrame frame){
		
		MainWindow splitPaneInstance = new MainWindow();

		frame.addFocusListener(new FocusListener() {
	        private final KeyEventDispatcher altDisabler = new KeyEventDispatcher() {
	            @Override
	            public boolean dispatchKeyEvent(KeyEvent e) {
	                return e.getKeyCode() == 18;
	            }
	        };

	        @Override
	        public void focusGained(FocusEvent e) {
	            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(altDisabler);
	        }

	        @Override
	        public void focusLost(FocusEvent e) {
	            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(altDisabler);
	        }
	    });
		frame.add(splitPaneInstance.getSplitPane());
		frame.setPreferredSize(new Dimension(width,height)); //TESTING SCREEN RESOLUTION was 300,600
		//frame.setLocationRelativeTo();
		frame.addKeyListener(splitPaneInstance);
		frame.setFocusable(true);
		
		frame.setVisible(true);
		frame.pack();
		
	}

	public static void createAndShowGUI() {
		//Create and set up the window
		JFrame frame = new JFrame(System.getProperty("user.dir"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		MainWindow splitPaneInstance = new MainWindow();

		frame.addFocusListener(new FocusListener() {
	        private final KeyEventDispatcher altDisabler = new KeyEventDispatcher() {
	            @Override
	            public boolean dispatchKeyEvent(KeyEvent e) {
	                return e.getKeyCode() == 18;
	            }
	        };

	        @Override
	        public void focusGained(FocusEvent e) {
	            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(altDisabler);
	        }

	        @Override
	        public void focusLost(FocusEvent e) {
	            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(altDisabler);
	        }
	    });
		frame.add(splitPaneInstance.getSplitPane());
		frame.setPreferredSize(new Dimension(width,height)); //TESTING SCREEN RESOLUTION was 300,600
		//frame.setLocationRelativeTo();
		frame.addKeyListener(splitPaneInstance);
		frame.setFocusable(true);
		
		frame.setVisible(true);
		frame.pack();
	}
	
	public static void main(String[] args) {
		
		createAndShowGUI(); 
		//runActiveRenderLoop();
		
	}
	
	
	
	private static void runActiveRenderLoop(){
		
		// Create game window...
	    JFrame frame = new JFrame();
	    System.err.println(Thread.currentThread().getName());

	    frame.setIgnoreRepaint( true );
	    
	    MainWindow mainWindow = new MainWindow(); //FIXME BOARD IS FUCKING THIS ALL UP
	    //frame.add(board);
	    
	    Point resolution = new Point( board.B_WIDTH , board.B_HEIGHT );
	    
	    frame.addKeyListener(mainWindow);
	    frame.addKeyListener(board);
	    frame.addMouseListener(mainWindow);
	    
	    frame.setUndecorated( true );
	    
	    frame.setResizable(false);
	    frame.setVisible(true);
	    frame.pack();
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 

	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 

	    
	    frame.addKeyListener( new KeyAdapter() { //Anon listener for exiting on escape
	      public void keyPressed( KeyEvent e ) {
	        if( e.getKeyCode() == KeyEvent.VK_ESCAPE )
	            running = false;
	          }
	    });
	                
	    // Get graphics configuration...
	    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
	    GraphicsConfiguration graphicsConfig = graphicsDevice.getDefaultConfiguration();
	    
	    graphicsDevice.setFullScreenWindow( frame ); // Set to fullscreen
	    
	    if( graphicsDevice.isDisplayChangeSupported() ) {
	    	
	    	DisplayMode mode = new DisplayMode( resolution.x, resolution.y, 32, DisplayMode.REFRESH_RATE_UNKNOWN );
	    	graphicsDevice.setDisplayMode( mode );

	    }
	    
	    Graphics paintImage = null;
	   
	    Color background = Color.BLACK;
	    Graphics2D g2 = null;    
	    // Variables for counting frames per seconds
	    System.err.println("Frame displayable?" + frame.isDisplayable());
	    frame.createBufferStrategy( 2 );
	    BufferStrategy bufferStrategy = frame.getBufferStrategy();
	    
	    BufferedImage bufferedImage = graphicsConfig.createCompatibleImage( resolution.x, resolution.y );
	    Graphics2D g = bufferedImage.createGraphics();
	    
	    running = true;
	    while( running ) { //OPTIMIZE INTO DO WHILE LOOPS FOR RESTORED CONTENTS
	    	
	    	try{

		        g2 = bufferedImage.createGraphics(); //Clear back buffer image
		        g2.setColor( background );

		        g2.fillRect( 0, 0, resolution.x, resolution.y );

		        
		        board.activeRender(g2); //Render game onto back buffer
		        
		        paintImage = bufferStrategy.getDrawGraphics(); 
		        paintImage.drawImage( bufferedImage, 0, 0, null );
		                                
		        if( !bufferStrategy.contentsLost() ) //Blit and page-flip
		        	bufferStrategy.show();
		        
	    	} finally {
	            // release resources
	            if( g2 != null ) 
	            	g2.dispose();
	            if( paintImage != null ) 
	            	paintImage.dispose();
	          }
		    	
	    	//try { Thread.sleep(1000); } catch (Exception e) {}
	    	
	    }
	    
	    
	                
	    graphicsDevice.setFullScreenWindow( null );
	    System.exit(0);
		
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		board.keyPressed(e);
		editorPanel.keyPressed(e);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		board.keyReleased(e);
		editorPanel.keyReleased(e);
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	


}
