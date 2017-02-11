package engine;

import javax.swing.*;

import editing.*;

import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class MainWindow extends JPanel implements KeyListener{
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
		//board.keyPressed(e);
		editorPanel.keyPressed(e);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		//board.keyReleased(e);
		editorPanel.keyReleased(e);
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	

	
    /*@Override //Should go in intermediate "Rendering" class or something 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
            board.drawObjects(g);

    }*/
}
