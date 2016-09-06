//package engine;
//
//import javax.swing.*;
//import javax.swing.border.BevelBorder;
//import javax.swing.event.*;
//
//import java.awt.*;
//import java.awt.event.*;
//import java.util.ArrayList;
//import entities.*;
//import Editing.*;
//
//
////TASK LIST:
//// 1) need to add a function that can re-assign the list of entities in case they are added/removed from board.
////		--currently it is only being assigned once in this constructor.
//// 2) create a function that will contain a properties list that is assembled every time the info button is pushed (while an entity is selected)
//
//@SuppressWarnings("serial")
///**
// * 
// * @author Dave
// *
// */
//public class SidePanel extends JPanel {
//	
//	private ArrayList<PropertiesList> listOfPropLists;
//	private String[] propListAsString; //will be initialized in its own updating/populating function, just like entities list has.
//    private String[] staticEntityStringArr;
//    //private String[] dynamicEntityStringArr;
//    //private String[] physicsEntityStringArr;  will use these later, it won't be hard. 
//	
//    // ###### COMPONENTS
//	private JLabel mousePosLabel;
//	private JLabel entityCoordsLabel;
//	private JLabel selectedEntityName;
//	private JLabel entTypeLabel;
//	private JComboBox<String> allEntitiesComboBox;
//	private JComboBox<String> propertiesListComboBox;
//	protected JButton editPropertiesButton;
//
//	public FlowLayout layout;
//    //private JList entitiesJList;
//
//    private int currentIndex;
//
//	public SidePanel() {
//		//set default selected entity so it's not null
//		SplitPane.getBoard().currentSelectedEntity = SplitPane.getBoard().staticEntitiesList.get(0); 
//		
//		//set the editor's layout
//		layout = new FlowLayout(FlowLayout.LEADING, 3, 3);
//		setLayout(layout);
//		setBackground(Color.GRAY);
//		
//		//there will be as many property lists as there are entities, and they will directly correspond.
//		//make sure updateEntityStringArr() is called whenever entities are added or removed. 
//		// populateListOfPropLists will only be called once, right here.
//		updateEntityStringArr(); //populates the entity string array representation with elements from Board's static entity arraylist
//		populateListOfPropLists();
//		
//		/* Scratch Pad: for making each little panel based on property type
//		 *  maybe something like:
//		 *  private createTrueFalsePanel(propertyList[i]) 
//		 *  	new JPanel truefalse 
//		 * 	 	
//		 * 
//		 * 
//		 */
//		/*
//		entitiesJList = new JList<String>(entitiesAsString);
//		entitiesJList.setFocusable(false);
//		entitiesJList.setVisibleRowCount(5);
//		entitiesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		
//		JScrollPane entityScrollPane = new JScrollPane(entitiesJList);
//		//entityScrollPane.setFocusable(false);
//		*/		
//		mousePosLabel = new JLabel("Mouse Click: ");
//		entityCoordsLabel = new JLabel("Coordinates of selected entity: ");
//		selectedEntityName = new JLabel("Nothing Selected");
//		
//		editPropertiesButton = new JButton("Edit Properties");
//		editPropertiesButton.setActionCommand("EDIT_PROPERTIES");
//		editPropertiesButton.setFont(new Font("Serif",Font.PLAIN,10));
//		editPropertiesButton.setEnabled(false);
//		editPropertiesButton.setFocusable(false);
//		
//		
//		entTypeLabel = new JLabel("n/a");
//		
//		// ## The panel for Text boxes ##
//		/*
//		 * 
//		 */
//		// ## The drop down box for the list of all entities in board ###	
//		allEntitiesComboBox = new JComboBox<>(staticEntityStringArr);
//		allEntitiesComboBox.setFocusable(false);
//		allEntitiesComboBox.setSelectedIndex(0); //give it a default value
//		// ## Handling when you select an Entity from the drop down box: 
//		allEntitiesComboBox.addItemListener(new ItemListener(){ 
//			public void itemStateChanged(ItemEvent e){
//				if (e.getStateChange() == ItemEvent.SELECTED) 
//				{
//					currentIndex = allEntitiesComboBox.getSelectedIndex();
//					try{					
//						SplitPane.getBoard().deselectAllEntities();
//						editPropertiesButton.setEnabled(true);
//						//sets Board's current entity
//						SplitPane.getBoard().currentSelectedEntity = SplitPane.getBoard().staticEntitiesList.get(currentIndex);
//						SplitPane.getBoard().staticEntitiesList.get(currentIndex).isSelected = true;
//						entTypeLabel.setText(Property.getEntityType(SplitPane.getBoard().currentSelectedEntity));
//						setSelectedEntityName("Selected: " + SplitPane.getBoard().currentSelectedEntity.name);
//						//sends code from here over to Board to let it draw this entity's selection box
//						SplitPane.getBoard().selectedBox.setSize(SplitPane.getBoard().currentSelectedEntity.getObjectGraphic().getImage().getWidth(null),
//								SplitPane.getBoard().currentSelectedEntity.getObjectGraphic().getImage().getHeight(null) );
//					}
//					catch (NullPointerException exception){System.err.println("nullpointerexception"); }
//				}
//			}
//		});		
//
//		// ## For when the Edit Properties button is clicked
//		
//		editPropertiesButton.addActionListener(new ActionListener() {		
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				
//				///*
//				// **all this code should be replaced with a single function, along the lines of createPropertiesWindow(some parameters);
//		
//				if (editPropertiesButton.isEnabled()) {
//					createPropertiesFrame();
//					int thisPropertyType;   // will be of type Property.STATE, Property.POS, or Property.TEXT, etc.
//					JPanel editPropertiesPanel = new JPanel(new BorderLayout());
//					editPropertiesPanel.setPreferredSize(new Dimension(30,90));	
//					//initialize drop down box for list of Properties
//					propertiesListComboBox = new JComboBox<String>(listOfPropLists.get(currentIndex).getPropertiesAsString());
//					propertiesListComboBox.setSelectedIndex(0);
//					propertiesListComboBox.addItemListener(new ItemListener() {
//						@Override
//						public void itemStateChanged(ItemEvent e) {
//
//							if (e.getStateChange() == ItemEvent.SELECTED) {
//								
//								//Rewrite this part: instead of typing this if statement over and over,
//								// acquire the property type at the beginning of this whole actionListener,
//								// and then do a switch case or just a bunch of if statements.
//								//-- Should only be as many If statements as there are types of properties
//								if (propertiesListComboBox.getSelectedIndex() == 0){ // <----- rewrite this part
//									if (listOfPropLists.get(currentIndex).getProperty(0).getPropertyType() == Property.STATE){ 
//										System.out.println("Is Property.BOOLEAN");
//									}
//									//need to add all the different types of panels to dispaly different options
//									// (such as radio buttons for true/false, text fields for positions and names, etc
//								}
//							}
//						}						
//					});					 
//					editPropertiesPanel.add(propertiesListComboBox);
//					editPropertiesPanel.add(new JRadioButton("True",true));
//					editPropertiesPanel.add(new JRadioButton("False",false));
//
//					//in this part I will add the three possible pre-made JPanels to accomodate: text input, radio buttons
//					// e.g. 
//					// if ( (the current selected property for this entity) == Property.BOOL)
//					//		infoPanel.add(trueFalsePanel) 
//					// and same for the other two.
//					// 		
//				
//					//JOptionPane.showMessageDialog(null, editPropertiesPanel, "test", JOptionPane.INFORMATION_MESSAGE);
//				}
//				//*/
//			} 		
//		});
//		
//		// ## adding the components to the Editor window
//		
//		//inline panel for text messages
//		JPanel labelsPanel = new JPanel();
//		labelsPanel.setLayout(new FlowLayout());
//		labelsPanel.setPreferredSize(new Dimension(199, 80));
//		labelsPanel.setBackground(Color.GRAY);
//		labelsPanel.setBorder(BorderFactory.createEtchedBorder());
//		labelsPanel.add(mousePosLabel);
//		labelsPanel.add(entityCoordsLabel);
//		labelsPanel.add(selectedEntityName);
//		add(labelsPanel);
//		
//		add(entTypeLabel);
//		add(allEntitiesComboBox);
//		
//		// inline panel for button
//		JPanel buttonPanel = new JPanel();
//		buttonPanel.setLayout(new BorderLayout());
//		buttonPanel.setPreferredSize(new Dimension(190, 30));
//		buttonPanel.setBackground(Color.GRAY);
//		buttonPanel.add(editPropertiesButton);
//		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
//		add(buttonPanel);
//		layout.layoutContainer(this);
//	} //end of constructor;
//	
//	//this private class will be the stand-in for my current shitty JOptionPane popup.
//	// will be created when createPropertiesFrame() is called.
//	private class PropertiesFrame extends JFrame implements ActionListener,ItemListener{
//
//		private void createPropertiesPanel() {
//			
//		}
//		@Override
//		public void itemStateChanged(ItemEvent e) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			//will handle 
//			
//		}
//		
//	}
//	
//	private void createPropertiesFrame() {
//		JFrame propFrame = new PropertiesFrame();
//		propFrame.setLocationRelativeTo(null);
//		propFrame.setSize(new Dimension(300,300));
//		propFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		propFrame.setVisible(true);
//	}
//	
//	
//	//helper function to transfer data from ArrayList into a regular array
//	private void populateArrayFromList(String[] arr, ArrayList<EntityStatic> arrayList)
//	{
//		try {
//			System.out.println("Array size " + arr.length);
//			System.out.println("ArrayList size " + arrayList.size());
//			for (int i = 0; i < arrayList.size(); i++)
//			{
//				arr[i] = arrayList.get(i).name;
//			}
//		}
//		catch(Exception e) {}
//	}
//	
//	private void updateEntityStringArr() {
//		staticEntityStringArr = new String[(SplitPane.getBoard().staticEntitiesList.size())];	
//		populateArrayFromList(staticEntityStringArr, SplitPane.getBoard().staticEntitiesList);
//	}
//	
//
//	public void populateListOfPropLists() {
//		ArrayList<EntityStatic> currentEntListCopy = SplitPane.getBoard().staticEntitiesList;
//		listOfPropLists = new ArrayList<PropertiesList>(currentEntListCopy.size());
//		for (EntityStatic ent : currentEntListCopy){
//			//will create a new propertyList array corresponding to each staticEntity.
//			listOfPropLists.add(new PropertiesList(ent));
//		}
//	}
//	public void addEntryToListOfPropLists(PropertiesList pl){
//		//will add to the end the of the listofproplists array, which will work just fine assuming
//		//that when entities are added to their list, it will also be added to the very end.
//		// ( ArrayList<> list.add(object) will append to the end of list.)
//		listOfPropLists.add(pl);
//	}
//	public void removeEntryFromListOfPropLists(PropertiesList pl) {
//		try {  //object must exist inside of listOfPropLists, or else returns exception
//			//removes corresponding propertyList object from entity that was removed.
//			listOfPropLists.remove(pl);  
//			//can also use listOfPropLists.remove(int index) as a safer option.
//		}
//		catch(Exception e) {e.printStackTrace();}
//	}
//	public void setMousePosLabel(String text){
//		mousePosLabel.setText(text);
//	}
//	public void setEntityCoordsLabel(String text){
//		entityCoordsLabel.setText(text);
//	}
//
//	protected void setSelectedEntityName(String text){
//		selectedEntityName.setText(text);
//	}
//	/**
//	 * Makes "Edit Properties" button enabled and clickable.
//	 * @param choice
//	 */
//	public void enableEditPropertiesButton(boolean choice){
//		if (choice == true)
//			editPropertiesButton.setEnabled(true);
//		else if(choice == false)
//			editPropertiesButton.setEnabled(false);
//	}
//
//}
