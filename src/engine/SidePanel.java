package engine;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import entities.*;
import Editing.*;
import engine.*;


//TASK LIST:
// 1) need to add a function that can re-assign the list of entities in case they are added/removed from board.
//		--currently it is only being assigned once in this constructor.
// 2) create a function that will contain a properties list that is assembled every time the info button is pushed (while an entity is selected)

@SuppressWarnings("serial")
/**
 * @author Dave 
 */
public class SidePanel extends JPanel {
	private Board board;
	private ArrayList<PropertiesList> listOfPropLists;
	private String[] propListAsString; //will be initialized in its own updating/populating function, just like entities list has.
    private String[] staticEntityStringArr;
    //private String[] dynamicEntityStringArr;
    //private String[] physicsEntityStringArr;  will use these later, it won't be hard. 
	
    // ###### COMPONENTS
	private JLabel mousePosLabel;
	private JLabel entityCoordsLabel;
	private JLabel selectedEntityNameLabel;
	//private JLabel entTypeLabel;
	protected JComboBox<String> allEntitiesComboBox;
	//private JComboBox<String> propertiesListComboBox;  for now, moved this to PropertiesFrame
	protected JButton editPropertiesButton;

	public FlowLayout layout;
    //private JList entitiesJList;

	private int currentEntIndex;

	public SidePanel( Board boardInstance) {
	
		this.board = boardInstance;
		//set default selected entity so it's not null
		setSelectedEntityThruEditor(board.staticEntitiesList.get(0)); 
		
		//set the editor's layout
		layout = new FlowLayout(FlowLayout.LEADING, 3, 3);
		setLayout(layout);
		setBackground(Color.GRAY);
		
		//there will be as many property lists as there are entities, and they will directly correspond.
		//make sure updateEntityStringArr() is called whenever entities are added or removed. 
		// populateListOfPropLists will only be called once, right here.
		updateEntityStringArr(); //populates the entity string array representation with elements from Board's static entity arraylist
		populateListOfPropLists();
		
		mousePosLabel = new JLabel("Mouse Click: ");
		entityCoordsLabel = new JLabel("Coords of selected entity: ");
		selectedEntityNameLabel = new JLabel("Nothing Selected");
		
		editPropertiesButton = new JButton("Edit Properties");
		editPropertiesButton.setActionCommand("EDIT_PROPERTIES");
		editPropertiesButton.setFont(new Font("Serif",Font.PLAIN,10));
		editPropertiesButton.setEnabled(false);
		editPropertiesButton.setFocusable(false);
		// ###### For when the Edit Properties button is clicked		
		editPropertiesButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {			
				///*
				// **all this code should be replaced with a single function, along the lines of createPropertiesWindow(some parameters);	
					createAndShowPropertiesFrame();	
			} 		
		});
				
		// ## The drop down box for the list of all entities in board ###	
		allEntitiesComboBox = new JComboBox<>(staticEntityStringArr);
		allEntitiesComboBox.setFocusable(false);
		allEntitiesComboBox.setSelectedIndex(0); //give it a default value
		// ## Handling when you select an Entity from the drop down box: 
		allEntitiesComboBox.addItemListener(new ItemListener(){ 
			public void itemStateChanged(ItemEvent e){
				if (e.getStateChange() == ItemEvent.SELECTED) 
				{
					//String testString = (String)allEntitiesComboBox.getSelectedItem();
					//System.out.println(testString);
					//allEntitiesComboBox.addItem
					currentEntIndex = allEntitiesComboBox.getSelectedIndex();
					System.out.println(currentEntIndex);
					try{					
						board.deselectAllEntities();
						editPropertiesButton.setEnabled(true);
						//sets Board's current entity
						setSelectedEntityThruEditor(board.staticEntitiesList.get(currentEntIndex));
						//board.staticEntitiesList.get(currentEntIndex).isSelected = true;
						setSelectedEntityName("Selected: " + getSelectedEntity().name);
						setEntityCoordsLabel(String.format("Coords of Selected Entity: %s,%s", getSelectedEntity().getX(), getSelectedEntity().getY()));
						//sends code from here over to Board to let it draw this entity's selection box
						board.selectedBox.setSize(getSelectedEntity().getObjectGraphic().getImage().getWidth(null),
	  															getSelectedEntity().getObjectGraphic().getImage().getHeight(null) );
					}
					catch (NullPointerException exception){
						exception.printStackTrace();
						System.err.println("nullpointerexception"); 
					}
				}
			}
		});	
		
		// ###### adding the components to the Editor window		
		//inline panel for text messages
		JPanel labelsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelsPanel.setPreferredSize(new Dimension(215, 100));
		labelsPanel.setBackground(Color.GRAY);
		labelsPanel.setBorder(BorderFactory.createEtchedBorder());
		labelsPanel.add(mousePosLabel);
		labelsPanel.add(entityCoordsLabel);
		labelsPanel.add(selectedEntityNameLabel);
		add(labelsPanel);

		add(allEntitiesComboBox);
		
		// inline panel for button
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setPreferredSize(new Dimension(190, 30));
		buttonPanel.setBackground(Color.GRAY);
		buttonPanel.add(editPropertiesButton);
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		add(buttonPanel);
		layout.layoutContainer(this);
	} //end of constructor;
	
	//this class will be the stand-in for my current shitty JOptionPane popup.
	// will be created when createPropertiesFrame() is called.
	public class PropertiesFrame extends JFrame implements ActionListener,ItemListener{
		//create some components
		JComboBox<String> propertiesListComboBox;
		JTextField xPosTextField;
		JTextField yPosTextField;
		JTextField entNameTextField;
		JPanel cardsPanel; // this will be the CardLayout panel holder
		// this will show the current state of the property, 
		//and will update whenever the property is changed.
		JLabel collisionStatusLabelcursed;
		JLabel collisionStatusLabel;
		JLabel xPosStatusLabel; //testing out this displaying in the radioPanel
		JLabel yPosStatusLabel;
		JLabel entNameStatusLabel;
		//figure out a temporary plan for entTypeStatus later, since it won't be editable
		//you don't need to change it here.
		
		//the panels
		JPanel radioPanel;
		JPanel xPosPanel;
		JPanel yPosPanel;
		JPanel entityNamePanel;
		JPanel entityTypePanel;
		
		public PropertiesFrame() { 		 //constructor 
			super("Edit Properties");
			collisionStatusLabelcursed = new JLabel("N/A"); //initializing these so they won't cause null problems
			collisionStatusLabel = new JLabel("cursed?");
			xPosStatusLabel = new JLabel("N/A");
			yPosStatusLabel = new JLabel("N/A");
			entNameStatusLabel = new JLabel("N/A");
			
			radioPanel = createRadioPanel();
			xPosPanel = createXPosPanel();
			yPosPanel = createYPosPanel();
			entityNamePanel = createEntNamePanel();
			entityTypePanel = createEntTypePanel();
			add(createMainPropertiesPanel());
			setSize(new Dimension(300,300));
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);		
			pack();
			setVisible(true);
		}
		//this panel will act as the content pane for PropertiesFrame
		protected JPanel createMainPropertiesPanel() {
			
			//int thisPropertyType;   // will be of type Property.STATE, Property.POS, or Property.TEXT, etc.
			//mainPropertiesPanel will be the main panel inside of PropertiesFrame
			JPanel mainPropertiesPanel = new JPanel();
			mainPropertiesPanel.setPreferredSize(new Dimension(200,150));	
			//initialize drop down box for list of Properties
			propertiesListComboBox = new JComboBox<>(listOfPropLists.get(currentEntIndex).getPropertiesAsString());
			propertiesListComboBox.setEditable(false);
			propertiesListComboBox.setSelectedIndex(0);
			propertiesListComboBox.addItemListener(this);
			//initialize currentStatusLabel

			//make a panel to hold the combobox
			JPanel comboBoxPanel = new JPanel();
			comboBoxPanel.add(propertiesListComboBox);
			
			//create the tabs for each type of property to be set (radio, text, etc.)
			//would create them here, but I already made functions for them:
			// "createRadioPanel(), createTextPanel(), etc.
			
			
			cardsPanel = new JPanel(new CardLayout()); 
			cardsPanel.add(radioPanel, propertiesListComboBox.getItemAt(0));
			cardsPanel.add(xPosPanel, propertiesListComboBox.getItemAt(1));
			cardsPanel.add(yPosPanel, propertiesListComboBox.getItemAt(2));
			cardsPanel.add(entityNamePanel, propertiesListComboBox.getItemAt(3));
			cardsPanel.add(entityTypePanel, propertiesListComboBox.getItemAt(4));
			//JOptionPane.showMessageDialog(null, radioPanel);
			mainPropertiesPanel.add(comboBoxPanel, BorderLayout.PAGE_START);
			mainPropertiesPanel.add(cardsPanel, BorderLayout.CENTER);
			return mainPropertiesPanel;
		} // end of createMainPropertiesPanel()
		
		public void itemStateChanged(ItemEvent e) {
			CardLayout cl = (CardLayout)(cardsPanel.getLayout());
			cl.show(cardsPanel, (String)e.getItem());
			//updateCurrentStatusLabel(propertiesListComboBox.getSelectedIndex());
		}
	
		//this will handle the radio buttons and text fields
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			//for the radio button panels
			if (command.equals("COL_TRUE")) {
				getThisProperty(Property.COL_STATE).setEntityCollidableState(getSelectedEntity(), true);
				updateCurrentStatusLabel(Property.COL_STATE);
			}
			else if(command.equals("COL_FALSE")) {
				getThisProperty(Property.COL_STATE).setEntityCollidableState(getSelectedEntity(), false);
				updateCurrentStatusLabel(Property.COL_STATE);
			}
			else if(e.getSource() == xPosTextField){
				/*
				 * Here, set the xPosition field of the current propertyList's xPos property */
				getThisProperty(Property.XPOS).setEntityXpos(getSelectedEntity(), Integer.parseInt(command));
				updateCurrentStatusLabel(Property.XPOS);
			}
			else if(e.getSource() == yPosTextField){
				/*
				 * Here, set the yPosition field of the current propertyList's yPos property */
				getThisProperty(Property.YPOS).setEntityYpos(getSelectedEntity(), Integer.parseInt(command));
				updateCurrentStatusLabel(Property.YPOS);
			}
			else if(e.getSource() == entNameTextField){
				/*
				 * Here, set the name field of the current propertyList's yPos property */
				getThisProperty(Property.ENTNAME).setEntityName(getSelectedEntity(), command);
				updateCurrentStatusLabel(Property.ENTNAME);
			}
			/*
			invalidate();
			validate();
			repaint(); */
		}
		protected void updateCurrentStatusLabel(int propType) {
			if (propType == Property.COL_STATE) {
				System.out.println("was able to reach upDateCurrentStatusLabel. Attempting to set collisionStatusLabel again...");
				collisionStatusLabel.setText(String.valueOf(getThisProperty(propType).getEntityCollidableState(getSelectedEntity())) );
			}
			else if (propType == Property.XPOS)
				xPosStatusLabel.setText(Integer.toString(getThisProperty(propType).getEntityXpos(getSelectedEntity())) );
			else if (propType == Property.YPOS)
				yPosStatusLabel.setText(Integer.toString(getThisProperty(propType).getEntityYpos(getSelectedEntity())) );
			else if (propType == Property.ENTNAME)
				entNameStatusLabel.setText(getThisProperty(propType).getEntityName(getSelectedEntity()) );
		}
	// ###### Panel creation section  ###########
		// will create true/false (radiobutton), text field, and others here:
		protected JPanel createRadioPanel(){
			JPanel radioPanel = new JPanel(new BorderLayout());
			ButtonGroup buttonGroup = new ButtonGroup();			
			JRadioButton rbTrue = new JRadioButton("True");
			JRadioButton rbFalse = new JRadioButton("False");
			buttonGroup.add(rbTrue);
			buttonGroup.add(rbFalse);
			rbTrue.setActionCommand("COL_TRUE");
			rbTrue.addActionListener(this);		
			rbFalse.setActionCommand("COL_FALSE");
			rbFalse.addActionListener(this);	
			//initial check to correctly choose true or false for property
			if (getThisProperty(Property.COL_STATE).getEntityCollidableState(getSelectedEntity()) == true) {
				rbTrue.setSelected(true);
				updateCurrentStatusLabel(Property.COL_STATE);
			}
			else {
				rbFalse.setSelected(true);
				updateCurrentStatusLabel(Property.COL_STATE);
			}
			JPanel labelPanel = new JPanel(new BorderLayout());
			labelPanel.add(collisionStatusLabel);
			radioPanel.add(rbTrue, BorderLayout.WEST);
			radioPanel.add(rbFalse, BorderLayout.CENTER);
			radioPanel.add(labelPanel, BorderLayout.PAGE_END);
			return radioPanel;
		}
		protected JPanel createXPosPanel(){
			updateCurrentStatusLabel(Property.XPOS);
			JPanel xPosPanel = new JPanel(new BorderLayout());
			xPosTextField = new JTextField(10);
			xPosTextField.addActionListener(this);
			JPanel labelPanel = new JPanel();
			labelPanel.add(xPosStatusLabel);
			xPosPanel.add(xPosTextField,BorderLayout.NORTH);
			xPosPanel.add(labelPanel, BorderLayout.PAGE_END);
			return xPosPanel;
		}
		protected JPanel createYPosPanel(){
			updateCurrentStatusLabel(Property.YPOS);
			JPanel yPosPanel = new JPanel(new BorderLayout());
			yPosTextField = new JTextField(10);
			yPosTextField.addActionListener(this);
			JPanel labelPanel = new JPanel();
			labelPanel.add(yPosStatusLabel);
			yPosPanel.add(yPosTextField, BorderLayout.NORTH);
			yPosPanel.add(labelPanel, BorderLayout.PAGE_END);
			return yPosPanel;
		}
		protected JPanel createEntNamePanel(){
			updateCurrentStatusLabel(Property.ENTNAME);
			JPanel entNamePanel = new JPanel(new BorderLayout());
			entNameTextField = new JTextField(15);
			entNameTextField.addActionListener(this);
			JPanel labelPanel = new JPanel();
			labelPanel.add(entNameStatusLabel);
			entNamePanel.add(entNameTextField, BorderLayout.NORTH);
			entNamePanel.add(labelPanel, BorderLayout.PAGE_END);
			return entNamePanel;
		}
		protected JPanel createEntTypePanel(){
			JPanel entTypePanel = new JPanel();
			collisionStatusLabelcursed = new JLabel();
			String entTypeString = getThisProperty(Property.ENTTYPE).getEntityType(getSelectedEntity());
			entTypePanel.add(new JLabel(entTypeString), BorderLayout.PAGE_END);
			return entTypePanel;
		}
	} // end of PropertiesFrame class
	
	public void createAndShowPropertiesFrame() {
		PropertiesFrame propFrame = new PropertiesFrame();
		propFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		propFrame.setLocationRelativeTo(null);
		propFrame.setVisible(true);
	}
	/**
	 * <b>Returns the entered property of the current entity's propertyList</b>
	 * <br/> -A helper function to shorten typing listOfProplists.get(currentIndex).getProperty
	 * @param propType Must be of type Property.COL_STATE, .XPOS, .YPOS, etc.
	 * @return the given property in the listOfPropLists arraylist.
	 */
	private Property getThisProperty(int propType){
		try{
			return listOfPropLists.get(currentEntIndex).getProperty(propType);
		} catch (Exception e) { e.printStackTrace();return null; } //to handle if there for some reason isn't a current Entity index
	}
	/**
	 * <b>Helper method to return Board's currentSelectedEntity, also runs a null check.</b>
	 * @return Board's current selected Entity
	 */
	private EntityStatic getSelectedEntity(){
		try{
			return board.currentSelectedEntity;
		}catch (Exception e) {
			
			e.printStackTrace();
			return null; 
		}
	}
	/**
	 * Helper method to set Board's currentSelectedEntity
	 * @param newSelectedEntity
	 */
	private void setSelectedEntityThruEditor(EntityStatic newSelectedEntity){
		board.setCurrentSelectedEntity(newSelectedEntity);
	}
	//helper function to transfer data from ArrayList into a regular array
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
		catch(Exception e) {
		}
	}
	
	private void updateEntityStringArr() {
		staticEntityStringArr = new String[(board.staticEntitiesList.size())];	
		populateArrayFromList(staticEntityStringArr, board.staticEntitiesList);
	}
	
	
	public void populateListOfPropLists() {
		ArrayList<EntityStatic> currentEntListCopy = board.staticEntitiesList;
		listOfPropLists = new ArrayList<PropertiesList>(currentEntListCopy.size());
		for (EntityStatic ent : currentEntListCopy){
			//will create a new propertyList array corresponding to each staticEntity.
			listOfPropLists.add(new PropertiesList(ent));
		}
	}
	public void addEntryToListOfPropLists(PropertiesList pl){
		//will add to the end the of the listofproplists array, which will work just fine assuming
		//that when entities are added to their list, it will also be added to the very end.
		// ( ArrayList<> list.add(object) will append to the end of list.)
		listOfPropLists.add(pl);
	}
	public void removeEntryFromListOfPropLists(PropertiesList pl) {
		try {  //object must exist inside of listOfPropLists, or else returns exception
			//removes corresponding propertyList object from entity that was removed.
			listOfPropLists.remove(pl);  
			//can also use listOfPropLists.remove(int index) as a safer option.
		}
		catch(Exception e) {e.printStackTrace();}
	}
	public void setMousePosLabel(String text){
		mousePosLabel.setText(text);
	}
	public void setEntityCoordsLabel(String text){
		entityCoordsLabel.setText(text);
	}

	protected void setSelectedEntityName(String text){
		selectedEntityNameLabel.setText(text);
	}
	/**
	 * Makes "Edit Properties" button enabled and clickable.
	 * @param choice
	 */
	public void enableEditPropertiesButton(boolean choice){
		if (choice == true)
			editPropertiesButton.setEnabled(true);
		else if(choice == false)
			editPropertiesButton.setEnabled(false);
	}

}
