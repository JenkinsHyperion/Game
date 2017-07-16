package engine;

import javax.swing.*;

import editing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

@SuppressWarnings("serial")
public class MainWindow extends JPanel implements KeyListener, MouseListener{
	private BoardAbstract board;
	private static EditorPanel editorPanel;
	private JSplitPane splitPane;
    private boolean F1pressed = false;
    private Dimension editorPanelMinSize;
    
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
		board = new Board(width,height);
		//board = new TestBoard(width,height);
		
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

		/*EventQueue.invokeLater(new Runnable() { 
			@Override
			public void run() {                
				createAndShowGUI();   
				
			}
		});*/
		createAndShowGUI(); 

		
		/*JFrame frame = new JFrame();
		frame.setIgnoreRepaint(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Canvas mainCanvas = new Canvas();
		mainCanvas.setSize(width, height);
		mainCanvas.setIgnoreRepaint(true);
		
		frame.add(mainCanvas);
		frame.pack();
		frame.setVisible( true );

		mainCanvas.createBufferStrategy(2);
		bufferStrat = mainCanvas.getBufferStrategy();

		//createAndShowGUI(); 
		//showGUI( frame );
		
		Graphics graphics = null;
		
		while ( true ) {
		     // Prepare for rendering the next frame
		     // ...

		     // Render single frame
		     do {
		         // The following loop ensures that the contents of the drawing buffer
		         // are consistent in case the underlying surface was recreated
		         do {
		             // Get a new graphics context every time through the loop
		             // to make sure the strategy is validated
		             graphics = bufferStrat.getDrawGraphics();
		             Graphics2D g2 = (Graphics2D) graphics;
		             
		             // Render to graphics
		             g2.setColor(Color.CYAN);
		             g2.drawString("TEST", 100, 100);
		             
		             //System.out.println("TERSD");
		             // Dispose the graphics
		             graphics.dispose();

		             // Repeat the rendering if the drawing buffer contents
		             // were restored
		         } while (bufferStrat.contentsRestored());

		         // Display the buffer
		         bufferStrat.show();

		         // Repeat the rendering if the drawing buffer was lost
		     } while (bufferStrat.contentsLost());
		 }

		 // Dispose the window
		 //mainCanvas.setVisible(false);
		 //w.dispose();
		*/
		
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
