package engine;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import entities.*;
import Editing.*;


//TASK LIST:
// 1) need to add a function that can re-assign the list of entities in case they are added/removed from board.
//		--currently it is only being assigned once in this constructor.
// 2) create a function that will contain a properties list that is assembled every time the info button is pushed (while an entity is selected)

@SuppressWarnings("serial")
public class SidePanel extends JPanel {
	
	private JLabel mousePosLabel;
	private JLabel entityCoordsLabel;
	private JLabel selectedEntityName;
	private JComboBox<String> allEntitiesComboBox;
	private ArrayList<PropertiesList> listOfPropLists;
	private String[] propListAsString; //will be initialized in its own updating/populating function, just like entities list has.
    private String[] staticEntityStringArr;
    //private String[] dynamicEntityStringArr;
    //private String[] physicsEntityStringArr;  will use these later, it won't be hard. use 
	
	//actually this should only be instantiated when the property list needs to be populated, when an entity is selected
	//   private JComboBox<String> entityPropertyBox;

	protected JButton infoButton;

	public FlowLayout layout;
    //private JList entitiesJList;

    private int currentIndex;

	public SidePanel() {
		//set default selected entity so it's not null
		SplitPane.getBoard().currentSelectedEntity = SplitPane.getBoard().staticEntitiesList.get(0); 
		
		//set the editor's layout
		layout = new FlowLayout(FlowLayout.LEADING, 3, 3);
		setLayout(layout);
		setBackground(Color.GRAY);
		
		//there will be as many property lists as there are entities, and they will directly correspond.
		//call both of these methods whenever you add or remove entities from this game. They need to be in sync.
		updateEntityStringArr(); //populates the entity string array representation with elements from Board's static entity arraylist
		updateListOfPropLists();
		
		/* Scratch Pad: for making each little panel based on property type
		 *  maybe something like:
		 *  private createTrueFalsePanel(propertyList[i]) 
		 *  	new JPanel truefalse 
		 * 	 	
		 * 
		 * 
		 */
		/*
		entitiesJList = new JList<String>(entitiesAsString);
		entitiesJList.setFocusable(false);
		entitiesJList.setVisibleRowCount(5);
		entitiesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane entityScrollPane = new JScrollPane(entitiesJList);
		//entityScrollPane.setFocusable(false);
		*/		
		mousePosLabel = new JLabel("Mouse Click: ");
		entityCoordsLabel = new JLabel("Coordinates of selected entity: ");
		selectedEntityName = new JLabel("Nothing Selected");
		
		infoButton = new JButton("Edit Properties");
		infoButton.setFont(new Font("Serif",Font.PLAIN,10));
		infoButton.setEnabled(false);
		infoButton.setFocusable(false);
		
		
		// ## The panel for true/false ##
		/*
		 * 
		 * 
		 */
		
		// ## The panel for Position sliders ##
		/*
		 * 
		 */
		
		// ## The panel for Text boxes ##
		/*
		 * 
		 */
		
		
		// ## The drop down box for the list of all entities in board ###	
		allEntitiesComboBox = new JComboBox<String>(staticEntityStringArr);
		allEntitiesComboBox.setFocusable(false);
		
		allEntitiesComboBox.addItemListener(new ItemListener(){ 
			public void itemStateChanged(ItemEvent e){
				if (e.getStateChange() == ItemEvent.SELECTED) 
				{
					
					currentIndex = allEntitiesComboBox.getSelectedIndex();
					try{					
						SplitPane.getBoard().deselectAllEntities();
						infoButton.setEnabled(true);
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

		infoButton.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
		
				if (infoButton.isEnabled()) {
					
					//PropertiesList propList = new PropertiesList(SplitPane.getBoard().currentSelectedEntity);
					String entityName = staticEntityStringArr[ currentIndex ];
					JPanel infoPanel = new JPanel();
					FlowLayout layout = new FlowLayout();
					layout.setAlignment(FlowLayout.RIGHT);
					infoPanel.setLayout(layout);
					infoPanel.setPreferredSize(new Dimension(30,90));
					/* this will now be a class field instead of being local. will be populated in its own method.
					PropertiesList propertiesList = new PropertiesList(SplitPane.getBoard().currentSelectedEntity);
					*/
					

					// * AM GOING TO RETURN TO THIS PART, KEEP IT
					final JComboBox<String> propertiesListBox = new JComboBox<String>(listOfPropLists.get(currentIndex).getPropertiesAsString());
					propertiesListBox.addItemListener(new ItemListener() {

						@Override
						public void itemStateChanged(ItemEvent e) {

							if (e.getStateChange() == ItemEvent.SELECTED) {
								if (propertiesListBox.getSelectedIndex() == 0){
									if (listOfPropLists.get(currentIndex).getProperty(0).getPropertyType() == Property.BOOLEAN){
										System.out.println("Is Property.BOOLEAN");
									}
									//need to add all the different types of panels to dispaly different options
									// (such as radio buttons for true/false, text fields for positions and names, etc
								}
							}
						}						
					});					 
					infoPanel.add(propertiesListBox);
					//if ()
					infoPanel.add(new JRadioButton("True",true));
					infoPanel.add(new JRadioButton("False",false));

					
					//in this part I will add the three possible pre-made JPanels to accomodate: text input, radio buttons, and sliders.
					// e.g. 
					// if ( (the current selected property for this entity) == Property.BOOL)
					//		infoPanel.add(trueFalsePanel) 
					// and same for the other two.
					//
					// for sliders: 
					//	if ( " " " " == Property.POS )
					// 		
					/*
					infoPanel.add(new JLabel("Entity Name: " + entityName), BorderLayout.WEST);
					infoPanel.add(new JLabel("Entity X position: "
									+ SplitPane.getBoard().staticEntitiesList.get(allEntitiesComboBox.getSelectedIndex()).getX()) );
					infoPanel.add(new JLabel("Entity Y position: "
							+ SplitPane.getBoard().staticEntitiesList.get(allEntitiesComboBox.getSelectedIndex()).getY()) );
					infoPanel.add(new JLabel("Collidable: " 
							+ SplitPane.getBoard().staticEntitiesList.get(allEntitiesComboBox.getSelectedIndex()).isCollidable()) );
					JOptionPane.showMessageDialog(null, infoPanel, "test", JOptionPane.INFORMATION_MESSAGE);
					*/
					JOptionPane.showMessageDialog(null, infoPanel, "test", JOptionPane.INFORMATION_MESSAGE);
				}
			}			
		});
		// ## adding the components to the Editor window
		
		//inline panel for text messages
		JPanel labelsPanel = new JPanel();
		labelsPanel.setLayout(new FlowLayout());
		labelsPanel.setPreferredSize(new Dimension(199, 80));
		labelsPanel.setBackground(Color.GRAY);
		labelsPanel.setBorder(BorderFactory.createEtchedBorder());
		labelsPanel.add(mousePosLabel);
		labelsPanel.add(entityCoordsLabel);
		labelsPanel.add(selectedEntityName);
		add(labelsPanel);
		
		add(allEntitiesComboBox);
		
		// inline panel for button
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setPreferredSize(new Dimension(190, 30));
		buttonPanel.setBackground(Color.GRAY);
		buttonPanel.add(infoButton);
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		add(buttonPanel);
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
		staticEntityStringArr = new String[(SplitPane.getBoard().staticEntitiesList.size())];	
		populateArrayFromList(staticEntityStringArr, SplitPane.getBoard().staticEntitiesList);
	}
	
	//don't really need this vvvv because I won't be dynamically changing amount of properties an object is able to have.
	/*
	private void updatePropListAsString(int propListIndex) {
		propListAsString = new String[(listOfPropLists.get(propListIndex).getPropertiesList().size())];	
		listOfPropLists
	}
	*/
	/*
	public void populatePropertyBox(Entity ent) {
		ArrayList<String> propertiesList = new ArrayList<String>();
		propertiesList.add("X Position");
		//String[] propertiesAsString
	}
	*/

	public void updateListOfPropLists() {
		ArrayList<EntityStatic> currentEntListCopy = SplitPane.getBoard().staticEntitiesList;
		listOfPropLists = new ArrayList<PropertiesList>(currentEntListCopy.size());
		for (EntityStatic ent : currentEntListCopy){
			//will create a new propertyList array corresponding to each staticEntity.
			listOfPropLists.add(new PropertiesList(ent));
		}
	}
	public void setMousePosLabel(String text){
		mousePosLabel.setText(text);
	}
	public void setEntityCoordsLabel(String text){
		entityCoordsLabel.setText(text);
	}

	protected void setSelectedEntityName(String text){
		selectedEntityName.setText(text);
	}
	public void enableEditPropertiesButton(boolean choice){
		if (choice == true)
			infoButton.setEnabled(true);
		else if(choice == false)
			infoButton.setEnabled(false);
	}

}
