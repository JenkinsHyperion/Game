package engine;

import javax.swing.*;

import editing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

public class MainWindow implements KeyListener, MouseListener{
	private static BoardAbstract board;
	private static EditorPanel editorPanel;
	private JSplitPane splitPane;
	private JScrollPane editorPanelScrollPane;
    private boolean F1pressed = false;
    private Dimension editorPanelMinSize;
    
    private static boolean running = false;
    
    private static int width;
    private static int height;

    private static JFrame mainFrame;
    
	public MainWindow() {
		//FIXME delete this
		/*try {
			System.err.println("Your current IP address: " + InetAddress.getLocalHost());
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}*/
		try(final DatagramSocket socket = new DatagramSocket()){
			  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			  String ip = socket.getLocalAddress().getHostAddress();
			  System.err.println("current IP address: " + ip);
		}catch (SocketException e) {
			e.printStackTrace();
		}catch (UnknownHostException u) {
			u.printStackTrace();
		}
		//TESTING window size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		System.out.println("Resolution set to "+ width + " by " + height);


		editorPanelMinSize = new Dimension(220,300);
		
		mainFrame = new JFrame(System.getProperty("user.dir"));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//board = new BoardPhysicsTesting(width,height,frame);
		//board = new Board(width,height,frame);
		board = new TestBoard(width,height,mainFrame);
		
		board.setPreferredSize(new Dimension(BoardAbstract.B_WIDTH, BoardAbstract.B_HEIGHT));
		board.setMinimumSize(new Dimension(BoardAbstract.B_WIDTH, BoardAbstract.B_HEIGHT));
		
		/*editorPanel = new EditorPanel(board);
		editorPanel.setSize(new Dimension(240, 300));
		editorPanel.setPreferredSize(new Dimension(240, 300));*/
		//JScrollPane editorPanelScrollPane = new JScrollPane(editorPanel);
		editorPanelScrollPane = new JScrollPane(board.getEditorPanel(),JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		//board.transferEditorPanel(editorPanel);

		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanelScrollPane, board); // 
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(board.getEditorPanel().getWidth());

		//board.setFocusable(true);
		
		splitPane.setSize(new Dimension(board.getWidth() + board.getEditorPanel().getWidth(), 300));
		splitPane.setMinimumSize(new Dimension(board.getWidth() + board.getEditorPanel().getWidth(), 300));
		
		
	}
	public JScrollPane getScrollPane() {
		return editorPanelScrollPane;
	}
	public JSplitPane getSplitPane() {
		return splitPane;
	}
	public BoardAbstract getBoard(){
		return board;
	}
	
	/*@Deprecated
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
		
	}*/

	public static void createAndShowGUI() {
		
		MainWindow splitPaneInstance = new MainWindow();

		mainFrame.addFocusListener(new FocusListener() {
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
		mainFrame.add(splitPaneInstance.getSplitPane());
		//frame.add(splitPaneInstance.getScrollPane());
		mainFrame.setPreferredSize(new Dimension(width,height)); //TESTING SCREEN RESOLUTION was 300,600
		//frame.setLocationRelativeTo();
		mainFrame.addKeyListener(splitPaneInstance);
		mainFrame.setFocusable(true);
		
		mainFrame.setVisible(true);
		mainFrame.pack();

		// Get graphics configuration...
	    //GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    //GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();

	    //runActiveRenderingLoop( mainFrame, graphicsDevice );
	    
	    
	}
	
	public static void main(String[] args) {
		
		createAndShowGUI(); 
		//createFullscrenWindow();
	}
	
	private static void runActiveRenderingLoop( JFrame frame, GraphicsDevice graphicsDevice ){
		
	    GraphicsConfiguration graphicsConfig = graphicsDevice.getDefaultConfiguration();
	    
		frame.createBufferStrategy( 3 );
	    BufferStrategy bufferStrategy = frame.getBufferStrategy();
	    
	    BufferedImage bufferedImage = graphicsConfig.createCompatibleImage( board.B_WIDTH, board.B_HEIGHT );
	    Graphics2D g2 = bufferedImage.createGraphics();
		
		 while( true ) { //OPTIMIZE INTO DO WHILE LOOPS FOR RESTORED CONTENTS
			 


				 g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
				 
				 g2.setColor( Color.BLACK );

				 g2.fillRect( 0, 0, board.B_WIDTH, board.B_HEIGHT );

				 board.activeRender(g2);

				 g2.dispose();

				 if( !bufferStrategy.contentsLost() ) //Blit and page-flip
			        	bufferStrategy.show();
	
		 }
	}
	
	
	private static void createFullscrenWindow(){
		
		// Create game window..
	    
	    MainWindow mainWindow = new MainWindow(); //FIXME BOARD IS FUCKING THIS ALL UP
	    //frame.add(board);
	    mainFrame.setIgnoreRepaint( true );
	    
	    Point resolution = new Point( board.B_WIDTH , board.B_HEIGHT );
	    
	    //mainFrame.addKeyListener(mainWindow);
	    //frame.addKeyListener(board);
	    //mainFrame.addMouseListener(mainWindow);
	    
	    mainFrame.setUndecorated( true );
	    
	    board.setFocusable(true);
	    mainFrame.setResizable(false);
	    mainFrame.setVisible(true);
	    mainFrame.pack();
	    mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 

	    mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 

	    
	    mainFrame.addKeyListener( new KeyAdapter() { //Anon listener for exiting on escape
	      public void keyPressed( KeyEvent e ) {
	        if( e.getKeyCode() == KeyEvent.VK_ESCAPE )
	            running = false;
	          }
	    });
	    
	    // Get graphics configuration...
	    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
	    
	    runActiveRenderingLoop( mainFrame, graphicsDevice );
	    
	                
	    graphicsDevice.setFullScreenWindow( null );
	    System.exit(0);
		
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		board.keyPressed(e);
		board.getEditorPanel().keyPressed(e);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		board.keyReleased(e);
		board.getEditorPanel().keyReleased(e);
		
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
