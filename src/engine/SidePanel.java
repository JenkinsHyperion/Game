package engine;

import javax.swing.*;
import java.awt.*;


@SuppressWarnings("serial")
public class SidePanel extends JPanel {
	private static JLabel label1;
	private static JLabel label2;
	private static JLabel label3;
	private static JLabel selectedEntityName;
	public JLabel clickedEntity;
	//public Point clickPosition;
	protected JButton button1;
	private JButton button2;
	public FlowLayout layout;

	public SidePanel() {
		layout = new FlowLayout(FlowLayout.LEADING, 20, 15);
		setLayout(layout);
		setBackground(Color.GRAY);

		label1 = new JLabel("Here's some text");
		label2 = new JLabel("Coordinates of selected entity: ");
		label3 = new JLabel("default text");
		selectedEntityName = new JLabel("Nothing Selected");

		add(label1);
		add(label2);
		add(label3);
		add(selectedEntityName);

		button1 = new JButton("Button1");
		button1.setFont(new Font("Serif",Font.PLAIN,10));
		button1.setEnabled(true);
		button1.setPreferredSize(new Dimension(50,40));
		button1.setFocusable(false);
		
		button2 = new JButton("Button2");
		button2.setEnabled(true);
		button2.setFocusable(false);
		//item2.setHorizontalAlignment(JCheckBox.WEST);
		//item2.setVerticalAlignment(JCheckBox.SOUTH);
		add(button1);
		add(button2);
		
		layout.layoutContainer(this);
	}
	
	public static void setLabel1(String text){
		label1.setText(text);
	}
	public static void setLabel2(String text){
		label2.setText(text);
	}
	public static void setLabel3(String text){
		label3.setText(text);
	}
	public static void setSelectedEntityName(String text){
		selectedEntityName.setText(text);
	}

}
