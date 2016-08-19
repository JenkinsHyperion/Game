package engine;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import entities.*;


//TASK LIST:
// 1) need to add a function that can re-assign the list of entities in case they are added/removed from board.
//		--currently it is only being assigned once in this constructor.
// 2) create a function that will contain a properties list that is assembled every time the info button is pushed (while an entity is selected)

@SuppressWarnings("serial")
public class SidePanel extends JPanel {
	
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JLabel selectedEntityName;
	public JLabel clickedEntity;
	private JComboBox<String> allEntitiesComboBox;
	
	//actually this should only be instantiated when the property list needs to be populated, when an entity is selected
	//   private JComboBox<String> entityPropertyBox;

	protected JButton infoButton;

	public FlowLayout layout;
    //private JList entitiesJList;
    private String[] entityStringArr;
    private int currentIndex;

	public SidePanel() {
		SplitPane.getBoard().currentSelectedEntity = SplitPane.getBoard().staticEntitiesList.get(0); //set default selected entity so it's not null
		layout = new FlowLayout(FlowLayout.LEADING, 5, 15);
		setLayout(layout);
		setBackground(Color.GRAY);
		
		updateEntityStringArr(); //populates the entity string array representation with elements from Board's static entity arraylist
		
		/*
		entitiesJList = new JList<String>(entitiesAsString);
		entitiesJList.setFocusable(false);
		entitiesJList.setVisibleRowCount(5);
		entitiesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane entityScrollPane = new JScrollPane(entitiesJList);
		//entityScrollPane.setFocusable(false);
		*/		
		label1 = new JLabel("Here's some text");
		label2 = new JLabel("Coordinates of selected entity: ");
		label3 = new JLabel("default text");
		selectedEntityName = new JLabel("Nothing Selected");
		
		infoButton = new JButton("Info");
		infoButton.setFont(new Font("Serif",Font.PLAIN,10));
		infoButton.setEnabled(false);
		infoButton.setPreferredSize(new Dimension(50,30));
		infoButton.setFocusable(false);
		
		allEntitiesComboBox = new JComboBox<String>(entityStringArr);
		allEntitiesComboBox.addItemListener(new ItemListener(){ 
			public void itemStateChanged(ItemEvent e){
				if (e.getStateChange() == ItemEvent.SELECTED) 
				{
					infoButton.setEnabled(true);
					currentIndex = allEntitiesComboBox.getSelectedIndex();
					try{					
						SplitPane.getBoard().deselectAllEntities();
						//sets Board's current entity
						SplitPane.getBoard().currentSelectedEntity = SplitPane.getBoard().staticEntitiesList.get(currentIndex);
						SplitPane.getBoard().staticEntitiesList.get(currentIndex).isSelected = true;
						setSelectedEntityName("Selected: " + SplitPane.getBoard().currentSelectedEntity.name);
						//sends code from here over to Board to let it draw this entity's selection box
						SplitPane.getBoard().selectedBox.setSize(SplitPane.getBoard().currentSelectedEntity.getObjectGraphic().getImage().getWidth(null),
								SplitPane.getBoard().currentSelectedEntity.getObjectGraphic().getImage().getHeight(null) );
					}
					catch (NullPointerException exception){System.err.println("nullpointerexception"); }
				}
			}
		});
		add(label1);
		add(label2);
		add(label3);
		add(selectedEntityName);
		// add(entityScrollPane);  //I don't want to use scrollpane anymore, want to use JComboBox
		
		add(allEntitiesComboBox);
		add(infoButton, BorderLayout.CENTER);
		
		/*
		entitiesJList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent event) {
				infoButton.setEnabled(true);
				currentIndex = entitiesJList.getSelectedIndex();
				
				try{					
					SplitPane.getBoard().deselectAllEntities();
					//sets Board's current entity
					SplitPane.getBoard().currentSelectedEntity = SplitPane.getBoard().staticEntitiesList.get(currentIndex);
					SplitPane.getBoard().staticEntitiesList.get(currentIndex).isSelected = true;
					setSelectedEntityName("Selected: " + SplitPane.getBoard().currentSelectedEntity.name);
					//sends code from here over to Board to let it draw this entity's selection box
					SplitPane.getBoard().selectedBox.setSize(SplitPane.getBoard().currentSelectedEntity.getObjectGraphic().getImage().getWidth(null),
							SplitPane.getBoard().currentSelectedEntity.getObjectGraphic().getImage().getHeight(null) );
				}
				catch (NullPointerException exception){System.err.println("nullpointerexception"); }
			}
		});
		*/
		infoButton.addActionListener(new ActionListener() {
			
			
			@Override
			public void actionPerformed(ActionEvent e) {
		
				if (infoButton.isEnabled()) {
					
					
					String entityName = entityStringArr[ currentIndex ];
					JPanel infoPanel = new JPanel();
					FlowLayout layout = new FlowLayout();
					layout.setAlignment(FlowLayout.LEFT);
					infoPanel.setLayout(layout);
					infoPanel.setPreferredSize(new Dimension(30,90));
					infoPanel.add(new JLabel("Entity Name: " + entityName), BorderLayout.WEST);
					infoPanel.add(new JLabel("Entity X position: "
									+ SplitPane.getBoard().staticEntitiesList.get(allEntitiesComboBox.getSelectedIndex()).getX()) );
					infoPanel.add(new JLabel("Entity Y position: "
							+ SplitPane.getBoard().staticEntitiesList.get(allEntitiesComboBox.getSelectedIndex()).getY()) );
					infoPanel.add(new JLabel("Collidable: " 
							+ SplitPane.getBoard().staticEntitiesList.get(allEntitiesComboBox.getSelectedIndex()).isCollidable()) );
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
	
	private void updateEntityStringArr() {
		entityStringArr = new String[(SplitPane.getBoard().staticEntitiesList.size())];	
		populateArrayFromList(entityStringArr, SplitPane.getBoard().staticEntitiesList);
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
