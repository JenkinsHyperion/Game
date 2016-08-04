package engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;

@SuppressWarnings("serial")
public class SplitPane extends JPanel {
	private Board board;
	private SidePanel sidePanel;
	private JSplitPane splitPane;

	public SplitPane() {

		board = new Board();
		sidePanel = new SidePanel();
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidePanel, board);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(sidePanel.getWidth());

		
		splitPane.setSize(new Dimension(board.getWidth() + sidePanel.getWidth(), 300));
		splitPane.setMinimumSize(new Dimension(board.getWidth() + sidePanel.getWidth(), 300));
		System.out.println("Board.B_WIDTH: " + Board.B_WIDTH);
		System.out.println("sidePanel.getWidth(): " + sidePanel.getWidth());
		
		//splitPane.setPreferredSize(new Dimension(board.getWidth(), 300));	
	
		/*
		public void draw(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.BLUE);
			g2.draw(new Line2D.Double(p1, p2));	
			//Dimension dim = new Dimension()
			//Rectangle rect = new Rectangle()
		}
		*/
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
