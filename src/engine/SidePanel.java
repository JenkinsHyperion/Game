package engine;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import entities.*;


@SuppressWarnings("serial")
public class SidePanel extends JPanel {
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JLabel selectedEntityName;
	public JLabel clickedEntity;
	//public Point clickPosition;
	protected JButton infoButton;
	private JButton button2;
	public FlowLayout layout;
    private JList entitiesJList;
    private String[] entitiesStringList;
    private int currentIndex;

	public SidePanel() {
		
		layout = new FlowLayout(FlowLayout.LEADING, 20, 15);
		setLayout(layout);
		setBackground(Color.GRAY);
		
		entitiesStringList = new String[(SplitPane.getBoard().staticEntitiesList.size())];		
				
		populateArrayFromList(entitiesStringList, SplitPane.getBoard().staticEntitiesList);
		
		entitiesJList = new JList<String>(entitiesStringList);
		entitiesJList.setVisibleRowCount(5);
		entitiesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane entityScrollPane = new JScrollPane(entitiesJList);
				
		label1 = new JLabel("Here's some text");
		label2 = new JLabel("Coordinates of selected entity: ");
		label3 = new JLabel("default text");
		selectedEntityName = new JLabel("Nothing Selected");
		
		infoButton = new JButton("Info");
		infoButton.setFont(new Font("Serif",Font.PLAIN,10));
		infoButton.setEnabled(false);
		infoButton.setPreferredSize(new Dimension(50,30));
		infoButton.setFocusable(false);
		add(label1);
		add(label2);
		add(label3);
		add(selectedEntityName);
		add(entityScrollPane);
		add(infoButton, BorderLayout.CENTER);
		entitiesJList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent event) {
				infoButton.setEnabled(true);
				currentIndex = entitiesJList.getSelectedIndex();
				try{
					setSelectedEntityName("Selected: " + SplitPane.getBoard().currentSelectedEntity.name);
					SplitPane.getBoard().deselectAllEntities();
					//sets Board's current entity
					SplitPane.getBoard().currentSelectedEntity = SplitPane.getBoard().staticEntitiesList.get(currentIndex);
					SplitPane.getBoard().staticEntitiesList.get(currentIndex).isSelected = true;
					//sends code from here over to Board to let it draw this entity's selection box
					SplitPane.getBoard().selectedBox.setSize(SplitPane.getBoard().currentSelectedEntity.getObjectGraphic().getImage().getWidth(null),
							SplitPane.getBoard().currentSelectedEntity.getObjectGraphic().getImage().getHeight(null) );
				}
				catch (NullPointerException exception){System.err.println("nullpointerexception"); }
			}
		});
		infoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (infoButton.isEnabled()) {
					
					
					String entityName = entitiesStringList[ currentIndex ];
					JPanel infoPanel = new JPanel();
					FlowLayout layout = new FlowLayout();
					layout.setAlignment(FlowLayout.LEFT);
					infoPanel.setLayout(layout);
					infoPanel.setPreferredSize(new Dimension(30,90));
					infoPanel.add(new JLabel("Entity Name: " + entityName), BorderLayout.WEST);
					infoPanel.add(new JLabel("Entity X position: "
									+ SplitPane.getBoard().staticEntitiesList.get(entitiesJList.getSelectedIndex()).getX()) );
					infoPanel.add(new JLabel("Entity Y position: "
							+ SplitPane.getBoard().staticEntitiesList.get(entitiesJList.getSelectedIndex()).getY()) );
					infoPanel.add(new JLabel("Collidable: " 
							+ SplitPane.getBoard().staticEntitiesList.get(entitiesJList.getSelectedIndex()).isCollidable()) );
					JOptionPane.showMessageDialog(null, infoPanel, "test", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			
		});


		/*
		button2 = new JButton("Button2");
		button2.setEnabled(true);
		button2.setFocusable(false);

		//add(button1);
		//add(button2);
		*/
		layout.layoutContainer(this);
	}
	
	private void populateArrayFromList(String[] arr, ArrayList<EntityStatic> arrayList)
	{
		try {
			System.out.println("Array size " + arr.length);
			System.out.println("ArrayList size " + arrayList.size());
			for (int i = 0; i < arrayList.size(); i++)
			{
				arr[i] = arrayList.get(i).name;
			}
		}
		catch(Exception e) {}
	}
	public void setLabel1(String text){
		label1.setText(text);
	}
	public void setLabel2(String text){
		label2.setText(text);
	}
	public void setLabel3(String text){
		label3.setText(text);
	}
	protected void setSelectedEntityName(String text){
		selectedEntityName.setText(text);
	}

}
