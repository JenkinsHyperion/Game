package engine;

import javax.swing.*;

import testEntities.Particle;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

@SuppressWarnings("serial")
public class SplitPane extends JPanel {
	private static Board board;
	private static SidePanel sidePanel;
	private JSplitPane splitPane;
    public boolean sidePanelOn = false;
    private boolean F1pressed = false;
    private Dimension sidePanelMinSize;
    


	public SplitPane() {
		
		sidePanelMinSize = new Dimension(220,300);
		board = new Board();
		board.setPreferredSize(new Dimension(Board.B_WIDTH, Board.B_HEIGHT));
		board.setMinimumSize(new Dimension(Board.B_WIDTH, Board.B_HEIGHT));
		
		sidePanel = new SidePanel();
		sidePanel.setSize(new Dimension(220, 300));
		sidePanel.setPreferredSize(new Dimension(220, 300));
		sidePanel.setMinimumSize(sidePanelMinSize);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidePanel, board);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(sidePanel.getWidth());
		board.setFocusable(true);
		
		splitPane.setSize(new Dimension(board.getWidth() + sidePanel.getWidth(), 300));
		splitPane.setMinimumSize(new Dimension(board.getWidth() + sidePanel.getWidth(), 300));
		
		splitPane.addKeyListener(new KeyListener() {
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
		});
		
	}
	public JSplitPane getSplitPane() {
		return splitPane;
	}
	
	public static Board getBoard(){
		return board;
	}
	public static SidePanel getSidePanel() {
		return sidePanel;
	}
	
	public static void createAndShowGUI() {
		//Create and set up the window
		JFrame frame = new JFrame(System.getProperty("user.dir").replace( "\\", "//" ));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SplitPane splitPaneInstance = new SplitPane();
		//frame.setMinimumSize(new Dimension(splitPaneInstance.board.getWidth() + splitPaneInstance.sidePanel.getWidth(), 300));
		frame.add(splitPaneInstance.getSplitPane());
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.pack();
	}
}
