package editing;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.*;

import editing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import entities.*;
import engine.*;


//TASK LIST:
// 1) need to add a function that can re-assign the list of entities in case they are added/removed from board.
//		--currently it is only being assigned once in this constructor.
// 2) create a function that will contain a properties list that is assembled every time the info button is pushed (while an entity is selected)

@SuppressWarnings("serial")
/**
 * @author Dave 
 */
public class EditorPanel extends JPanel {
	// ### important fields ###
	public final Dimension minimizedSize = new Dimension(200,20);
	public final Dimension propPanelDefaultSize = new Dimension(215,125);
	protected int currentEntIndex;
	public boolean testFlag;
	private Board board;
	protected ArrayList<PropertiesList> listOfPropLists;
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
	//Panels
	private JPanel labelsPanel;
	private JPanel buttonPanel;
	private JPanel propertyPanelTest;

	public FlowLayout layout;
    //private JList entitiesJList;


	public EditorPanel( Board boardInstance) {
		testFlag = true;
		this.board = boardInstance;
		//set default selected entity so it's not null
		setSelectedEntityThruEditor(board.getStaticEntities().get(0)); 
		
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

		// inline panel for button
		buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.GRAY);
		buttonPanel.setBorder(BorderFactory.createTitledBorder("buttonPanelTest"));
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setPreferredSize(new Dimension(190, 50));		
		buttonPanel.add(editPropertiesButton);

		// ## The drop down box for the list of all entities in board ###	
		allEntitiesComboBox = new JComboBox<>(staticEntityStringArr);
		allEntitiesComboBox.setFocusable(false);
		allEntitiesComboBox.setSelectedIndex(0); //give it a default value
		// ## Handling when you select an Entity from the drop down box: 
		allEntitiesComboBox.addItemListener(new ItemListener(){ 
			public void itemStateChanged(ItemEvent e){
				if (e.getStateChange() == ItemEvent.SELECTED) 
				{
					restorePanels();
					//String testString = (String)allEntitiesComboBox.getSelectedItem();
					//System.out.println(testString);
					//allEntitiesComboBox.addItem
					currentEntIndex = allEntitiesComboBox.getSelectedIndex();
					System.out.println(currentEntIndex);
					System.out.println("Test area. e.getItem(): " + e.getItem());

					try{					
						board.deselectAllEntities();
						editPropertiesButton.setEnabled(true);
						
						//sets Board's current entity
						setSelectedEntityThruEditor(board.getStaticEntities().get(currentEntIndex));
						createAndShowPropertiesPanel();
						setSelectedEntityNameLabel("Selected: " + getSelectedEntity().name);
						setEntityCoordsLabel(String.format("Coords of Selected Entity: %s,%s", getSelectedEntity().getX(), getSelectedEntity().getY()));
						//sends code from here over to Board to let it draw this entity's selection box
						board.selectedBox.setSize(getSelectedEntity().getEntitySprite().getImage().getWidth(null),
	  															getSelectedEntity().getEntitySprite().getImage().getHeight(null) );
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
		labelsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelsPanel.setPreferredSize(new Dimension(215, 80));
		labelsPanel.setBackground(Color.GRAY);
		labelsPanel.setBorder(BorderFactory.createEtchedBorder());
		labelsPanel.add(mousePosLabel);
		labelsPanel.add(entityCoordsLabel);
		labelsPanel.add(selectedEntityNameLabel);
		
		propertyPanelTest = new JPanel();
		propertyPanelTest.setPreferredSize(minimizedSize);
		propertyPanelTest.setBackground(Color.GRAY);
		propertyPanelTest.setBorder(BorderFactory.createTitledBorder("propertyPanelTest"));
		
		// #### add everything to the editor
		add(allEntitiesComboBox);
		add(labelsPanel);
		add(buttonPanel);	
		add(propertyPanelTest);
		revalidate();
	} //end of constructor;
	
	//this class will be the stand-in for my current shitty JOptionPane popup.
	// will be created when createPropertiesFrame() is called.
	@Deprecated	
	public void createAndShowPropertiesFrame() {
		PropertiesFrame propFrame = new PropertiesFrame(this);
		propFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		propFrame.setLocationRelativeTo(null);
		propFrame.setVisible(true);
	}
	public void createAndShowPropertiesPanel() {
		propertyPanelTest.removeAll();
		propertyPanelTest.add(new PropertiesPanel(this));
	}
	public void minimizePanels() {
		//buttonPanel.setPreferredSize(minimizedSize);
		propertyPanelTest.setPreferredSize(minimizedSize);
	}
	public void restorePanels() {
		//buttonPanel.setPreferredSize(minimizedSize);
		//labelsPanel.setPreferredSize(minimizedSize);
		propertyPanelTest.setPreferredSize(propPanelDefaultSize);
	}
	/**
	 * <b>Returns the entered property of the current entity's propertyList</b>
	 * <br/> -A helper function to shorten typing listOfProplists.get(currentIndex).getProperty
	 * @param propType Must be of type Property.COL_STATE, .XPOS, .YPOS, etc.
	 * @return the given property in the listOfPropLists arraylist of the currently selected entity.
	 */
	public Property getThisProperty(int propType){
		try{
			return listOfPropLists.get(currentEntIndex).getProperty(propType);
		} catch (Exception e) { e.printStackTrace();return null; } //to handle if there for some reason isn't a current Entity index
	}
	/**
	 * <b>Helper method to return Board's currentSelectedEntity, also runs a null check.</b>
	 * @return Board's current selected Entity
	 */
	public EntityStatic getSelectedEntity(){
		try{
			return board.getCurrentSelectedEntity();
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
		staticEntityStringArr = new String[(board.getStaticEntities().size())];	
		populateArrayFromList(staticEntityStringArr, board.getStaticEntities());
	}
	
	
	public void populateListOfPropLists() {
		ArrayList<EntityStatic> currentEntListCopy = board.getStaticEntities();
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

	public void setSelectedEntityNameLabel(String text){
		selectedEntityNameLabel.setText(text);
	}
	public void setAllEntitiesComboBoxIndex(int index) {
		if (index >= 0 && index < allEntitiesComboBox.getItemCount())
			allEntitiesComboBox.setSelectedIndex(index);
		else
			allEntitiesComboBox.setSelectedIndex(0);
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
