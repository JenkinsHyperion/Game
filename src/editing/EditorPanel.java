package editing;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.*;

import editing.*;
import sprites.*;
import entities.*;
import engine.*;
import testEntities.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;




//TASK LIST:
// 1) need to add a function that can re-assign the list of entities in case they are added/removed from board.
//		--currently it is only being assigned once in this constructor.
// 2) DONE: create a function that will contain a properties list that is assembled every time the info button is pushed (while an entity is selected)

@SuppressWarnings("serial")
/**
 * @author Dave 
 */
public class EditorPanel extends JPanel {
	// ### important fields ###
	public static final String ASSET_PATH = System.getProperty("user.dir")+ File.separator + "Assets" + File.separator;
	public static final String PF1 = "platform.png";
	public static final String PF2 = "platform02.png";
	public static final String GND = "ground01.png";
	public static final String GRASS1 = "grass01.png";
	public final Dimension minimizedSize = new Dimension(200,20);
	public final Dimension propPanelDefaultSize = new Dimension(215,125);
	public final Dimension allEntitiesComboBoxDefSize = new Dimension(120,20);
	protected int currentEntIndex;
	public boolean testFlag;
	private Board board;
	private Sprite ghostSprite;
	private Point editorMousePos;
	public boolean ESC_ON;
	
	protected ArrayList<PropertiesList> listOfPropLists;
    private String[] staticEntityStringArr;
    //private String[] dynamicEntityStringArr;
    //private String[] physicsEntityStringArr;  will use these later, it won't be hard. 
	
    // ###### COMPONENTS
	private JLabel mousePosLabel;
	private JLabel entityCoordsLabel;
	private JLabel selectedEntityNameLabel;
	protected JComboBox<String> allEntitiesComboBox;
	
	protected JButton loadButton;
	protected JButton saveButton;
	protected JButton deleteEntButton;
	protected JButton addEntButton;
	//Panels
	private JPanel entitiesComboBoxPanel;
	private JPanel labelsPanel;
	private JPanel buttonPanel;
	private JPanel propertyPanelTest;

	public FlowLayout layout;
    //private JList entitiesJList;

	private SavingLoading saveLoad;

	public EditorPanel( Board boardInstance) {
		ESC_ON = false;
		editorMousePos = new Point();
		ghostSprite = SpriteNull.getNullSprite();
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
		
		saveButton = new JButton("Save");
		saveButton.setPreferredSize(new Dimension(40,22));
		saveButton.setMargin(new Insets(0,0,0,0));
		saveButton.setFocusable(false);
		saveButton.setMnemonic(KeyEvent.VK_S);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String levelName = JOptionPane.showInputDialog("Enter level name");
				new SavingLoading(board).writeLevel(board.getStaticEntities(), levelName);
			}
		});
		loadButton = new JButton("Load");
		loadButton.setFocusable(false);
		loadButton.setPreferredSize(new Dimension(40,22));
		loadButton.setMargin(new Insets(0,0,0,0));
		loadButton.setMnemonic(KeyEvent.VK_L);
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String levelName = JOptionPane.showInputDialog("Enter level name to load");
				if (levelName != null)
					new SavingLoading(board).loadLevel(board.getStaticEntities(), levelName);
			}
		});
		// #### This button is useless as of now, but can possibly be repurposed later.
		deleteEntButton = new JButton("Delete");
		deleteEntButton.setFocusable(false);
		deleteEntButton.setEnabled(false);
		deleteEntButton.setMnemonic(KeyEvent.VK_DELETE);
		deleteEntButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {						
				deleteEntity(allEntitiesComboBox.getSelectedIndex());
			} 		
		});
		addEntButton = new JButton("Create");
		addEntButton.setEnabled(true);
		addEntButton.setFocusable(false);
		addEntButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addEntity(20,20);
			}
		});
		// inline panel for button
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBackground(Color.GRAY);
	    buttonPanel.setBorder(BorderFactory.createTitledBorder("buttonPanelTest"));
		buttonPanel.setPreferredSize(new Dimension(190, 50));		
		buttonPanel.add(deleteEntButton);
		buttonPanel.add(addEntButton);

		// ## The drop down box for the list of all entities in board ###	
		allEntitiesComboBox = new JComboBox<>(staticEntityStringArr);
		allEntitiesComboBox.setPreferredSize(allEntitiesComboBoxDefSize);
		allEntitiesComboBox.setFocusable(false);
		allEntitiesComboBox.setSelectedIndex(0); //give it a default value
		allEntitiesComboBox.addActionListener(new EntitiesComboBoxActionHandler());

		// Panel to contain allEntitiesComboBox drop down panel
		entitiesComboBoxPanel = new JPanel(new BorderLayout());
		entitiesComboBoxPanel.setBackground(Color.GRAY);
		entitiesComboBoxPanel.setPreferredSize(allEntitiesComboBox.getPreferredSize());
		entitiesComboBoxPanel.add(allEntitiesComboBox);
		
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
		
		add(entitiesComboBoxPanel);
		add(saveButton);
		add(loadButton);
		add(labelsPanel);
		add(buttonPanel);	
		add(propertyPanelTest);
		
		JToolBar tb = new JToolBar(JToolBar.VERTICAL);
		//toolBarContainer.setPreferredSize(new Dimension(200,300));
		tb.setFloatable(false);
		tb.setFocusable(false);
		tb.add(Box.createGlue());
		/*
		tb.add(new JButton("test"));
		tb.add(new JButton("test2"));
		tb.add(new JButton("test3"));
		tb.add(new JButton("test4"));
		tb.add(new JButton("test4"));
		tb.add(new JButton("test4"));
		tb.add(new JButton("test4"));
		tb.add(new JButton("test4"));
		tb.add(new JButton("test4"));
		tb.add(new JButton("test5"));
		tb.add(new JButton("test5"));
		tb.add(new JButton("test5"));
		tb.add(new JButton("test5"));
		tb.add(new JButton("test5"));
		tb.add(new JButton("test5"));
		*/
		tb.add(Box.createGlue());
		JScrollPane toolBarContainer = new JScrollPane(tb);
		toolBarContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		toolBarContainer.setVerticalScrollBarPolicy((JScrollPane.VERTICAL_SCROLLBAR_ALWAYS));
		toolBarContainer.setPreferredSize(new Dimension(80,300));;
		//toolBarContainer.add(tb);
		//toolBarPanel.add(tb,BorderLayout.PAGE_END);
		add(toolBarContainer);
		
		//testing setting the ghostSprite
		setGhostSprite(ASSET_PATH + PF1 );
		revalidate();
	} //end of constructor;
	
	//Handler for the allEntitiesComboBox drop down panel
	public class EntitiesComboBoxActionHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox)e.getSource();
			//cb.getSelectedIndex());
			restorePanels();
			//String testString = (String)allEntitiesComboBox.getSelectedItem();
			//System.out.println(testString);
			//allEntitiesComboBox.addItem
			currentEntIndex = allEntitiesComboBox.getSelectedIndex();
			System.out.println(currentEntIndex);
			try{					
				board.deselectAllEntities();
				deleteEntButton.setEnabled(true);
				
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
		revalidate();
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
		}catch (Exception e) {	e.printStackTrace();  return null; }
	}
	/**
	 * Helper method to set Board's currentSelectedEntity
	 * @param newSelectedEntity
	 */
	private void setSelectedEntityThruEditor(EntityStatic newSelectedEntity){
		board.setCurrentSelectedEntity(newSelectedEntity);
	}
	//helper function to transfer data from ArrayList into a regular array
	public void populateArrayFromList(String[] arr, ArrayList<EntityStatic> arrayList)
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
	// #### Section for adding or removing actual entities
	public void updateEntityStringArr() {
		staticEntityStringArr = new String[(board.getStaticEntities().size())];	
		populateArrayFromList(staticEntityStringArr, board.getStaticEntities());
	}
	public void deleteEntity(int index) {
		board.deselectAllEntities();
		board.getStaticEntities().remove(index);
		removeEntryFromListOfPropLists(index); 	//must remove corresponding property of deleted entity
		updateAllEntitiesComboBox();
		board.deselectAllEntities();
	}
	//so many ways I can do this. Will start with overloaded methods
	public void addEntity(int x, int y) {  //default one. Adds test entity
		EntityStatic newEnt = new Platform(x,y,"platform02");
		board.deselectAllEntities();
		board.getStaticEntities().add(newEnt);
		addEntryToListOfPropLists(new PropertiesList(newEnt));
		updateAllEntitiesComboBox();
        allEntitiesComboBox.setSelectedIndex(allEntitiesComboBox.getItemCount()-1);
	}
	//will refresh(create a new one of)staticEntityStringArr, remove old comboBox and then create & add a new updated one
	public void updateAllEntitiesComboBox() {
		System.out.println("item count: "+ allEntitiesComboBox.getItemCount());
		int prevIndex = allEntitiesComboBox.getSelectedIndex();
		int prevMax = allEntitiesComboBox.getItemCount();
		updateEntityStringArr();
		entitiesComboBoxPanel.remove(allEntitiesComboBox);
		allEntitiesComboBox = new JComboBox<>(staticEntityStringArr);
		allEntitiesComboBox.setFocusable(false);
		allEntitiesComboBox.addActionListener(new EntitiesComboBoxActionHandler());
		if (prevMax == allEntitiesComboBox.getItemCount())
			allEntitiesComboBox.setSelectedIndex(prevIndex);
		entitiesComboBoxPanel.add(allEntitiesComboBox);
		revalidate();
		repaint();
	}
	// #############
	// #### End of entity add/removal section
	// ----------------------------------------------------------------------------------
	// ### For updating the propertyLists 
	 //##################################
	public void populateListOfPropLists() {
		listOfPropLists = new ArrayList<PropertiesList>(board.getStaticEntities().size());
		for (EntityStatic ent : board.getStaticEntities() ){
			//will create a new propertyList array corresponding to each staticEntity.
			listOfPropLists.add(new PropertiesList(ent));
		}//#####################
	} //########################
	public void addEntryToListOfPropLists(PropertiesList pl){
		//will add to the end the of the listofproplists array, which will work just fine assuming
		//that when entities are added to their list, it will also be added to the very end.
		// ( ArrayList<> list.add(object) will append to the end of list.)
		listOfPropLists.add(pl);
	} // ###############
	public void removeEntryFromListOfPropLists(int index) {
		try {  //object must exist inside of listOfPropLists, or else returns exception
			//removes corresponding propertyList object from entity that was removed.
			listOfPropLists.remove(index);  
			//can also use listOfPropLists.remove(int index) as a safer option.
		}
		catch(Exception e) {e.printStackTrace();}
	} // ###############
	// #### End of section for prop lists
	
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
			deleteEntButton.setEnabled(true);
		else if(choice == false)
			deleteEntButton.setEnabled(false);
	}
	//sets the ghostSprite to "null" using null-object pattern
	public void nullifyGhostSprite(){
		ghostSprite = SpriteNull.getNullSprite();
	}
	public Sprite getGhostSprite(){
		return ghostSprite;
	}
	public void setGhostSprite(String path) {
		ghostSprite = new SpriteStillframe(path);
	}
	public void setEditorMousePos(int x, int y){
		editorMousePos.x = x;
		editorMousePos.y = y;
	}
	public Point getEditorMousePos(){
		return editorMousePos;
	}

}
