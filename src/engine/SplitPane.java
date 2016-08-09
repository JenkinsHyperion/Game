package engine;

import javax.swing.*;

import testEntities.Particle;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

@SuppressWarnings("serial")
public class SplitPane extends JPanel {
	protected static Board board;
	protected static SidePanel sidePanel;
	private JSplitPane splitPane;
    public boolean sidePanelOn = false;

	public SplitPane() {
		board = new Board();
		board.setPreferredSize(new Dimension(Board.B_WIDTH, Board.B_HEIGHT));
		board.setMinimumSize(new Dimension(Board.B_WIDTH, Board.B_HEIGHT));
		
		sidePanel = new SidePanel();
		sidePanel.setSize(new Dimension(200, 300));
		sidePanel.setPreferredSize(new Dimension(200, 300));
		sidePanel.setMinimumSize(new Dimension(200,300));
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidePanel, board);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(sidePanel.getWidth());

		
		splitPane.setSize(new Dimension(board.getWidth() + sidePanel.getWidth(), 300));
		splitPane.setMinimumSize(new Dimension(board.getWidth() + sidePanel.getWidth(), 300));
		System.out.println("Board.B_WIDTH: " + Board.B_WIDTH);
		System.out.println("sidePanel.getWidth(): " + sidePanel.getWidth());
		
		splitPane.addKeyListener(new KeyListener() {
			@Override
	        public void keyPressed(KeyEvent e) {
	        }
			@Override
	        public void keyReleased(KeyEvent e) {

	        }
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		
		sidePanel.button1.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("Clicked the button");
			}
				
		});
	}
	public JSplitPane getSplitPane() {
		return splitPane;
	}
	
	public static void createAndShowGUI() {
		//Create and set up the window
		JFrame frame = new JFrame(System.getProperty("user.dir").replace( "\\", "//" ));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SplitPane splitPaneInstance = new SplitPane();
		frame.setMinimumSize(new Dimension(splitPaneInstance.board.getWidth() + splitPaneInstance.sidePanel.getWidth(), 300));
		frame.getContentPane().add(splitPaneInstance.getSplitPane());
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.pack();
	}
}
