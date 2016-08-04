package engine;

import javax.swing.*;
import java.awt.*;


public class SidePanel extends JPanel {
	public static JLabel label1;
	public static JLabel selectedEntityName;
	public JLabel clickedEntity;
	//public Point clickPosition;
	private JButton button1;

	public SidePanel() {
		setBackground(Color.GRAY);
		setSize(new Dimension(200, 300));
		setPreferredSize(new Dimension(200, 300));
		setMinimumSize(new Dimension(200,300));
		label1 = new JLabel("Here's some text");
		selectedEntityName = new JLabel("Nothing selected");
		//item1.setHorizontalAlignment(JLabel.EAST);
		add(label1, BorderLayout.EAST);
		add(selectedEntityName, BorderLayout.EAST);

		button1 = new JButton("Button1");
		//item2.setHorizontalAlignment(JCheckBox.WEST);
		//item2.setVerticalAlignment(JCheckBox.SOUTH);
		//add(checkBox1, BorderLayout.WEST);
	}
	
	public void setLabel1(String text){
		label1.setText(text);
	}
	
	

}
