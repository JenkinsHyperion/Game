package engine;

import javax.swing.*;

import editing.*;

import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class MainWindow extends JPanel implements KeyListener, MouseListener{
	private BoardAbstract board;
	private static EditorPanel editorPanel;
	private JSplitPane splitPane;
    private boolean F1pressed = false;
    private Dimension editorPanelMinSize;
    
    private static int width;
    private static int height;

	public MainWindow() {
		//TESTING window size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		System.out.println("Resolution set to "+ width + " by " + height);
		
		editorPanelMinSize = new Dimension(220,300);
		board = new Board(width,height);
		//board = new TestBoard(width,height);
		board.setPreferredSize(new Dimension(Board.B_WIDTH, Board.B_HEIGHT));
		board.setMinimumSize(new Dimension(Board.B_WIDTH, Board.B_HEIGHT));
		
		editorPanel = new EditorPanel(board);
		editorPanel.setSize(new Dimension(220, 300));
		editorPanel.setPreferredSize(new Dimension(220, 300));
		editorPanel.setMinimumSize(editorPanelMinSize);
		board.transferEditorPanel(editorPanel);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanel, board);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(editorPanel.getWidth());
		//board.setFocusable(true);
		
		splitPane.setSize(new Dimension(board.getWidth() + editorPanel.getWidth(), 300));
		splitPane.setMinimumSize(new Dimension(board.getWidth() + editorPanel.getWidth(), 300));
		
		/*splitPane.addKeyListener(new KeyListener() {
			@Override
	        public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_F1){
					//will never fire until setFocusable is set to true
					//System.out.println("Stuff");
				}
			}
			@Override
	        public void keyReleased(KeyEvent e) {

	        }
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});*/
		

		
	}
	public JSplitPane getSplitPane() {
		return splitPane;
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

		EventQueue.invokeLater(new Runnable() { 
			@Override
			public void run() {                
				createAndShowGUI();                
			}
		});
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
	
	

	
    /*@Override //Should go in intermediate "Rendering" class or something 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
            board.drawObjects(g);

    }*/
}
