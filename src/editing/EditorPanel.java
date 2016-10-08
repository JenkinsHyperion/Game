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
	public static final int DEFAULT_MODE = 0;
	public static final int ENTPLACEMENT_MODE = 1;
	public static final int WORLDGEOM_MODE = 2;
	
	protected boolean mouseClick = false;
	private Point clickPosition;
	public int clickPositionXOffset;
	public int clickPositionYOffset;
	public int mode;
	private String newEntityPath;
	public boolean entityPlacementMode;
	public final Dimension minimizedSize = new Dimension(200,20);
	public final Dimension propPanelDefaultSize = new Dimension(215,125);
	public final Dimension allEntitiesComboBoxDefSize = new Dimension(120,20);
	protected int currentEntIndex;
	public boolean testFlag;
	protected Board board;
	private Sprite ghostSprite; 
	private Point editorMousePos;
	
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

	//Panels
	private JPanel entitiesComboBoxPanel;
	private JPanel labelsPanel;
	private JPanel buttonPanel;
	private JPanel propertyPanelTest;
	private JPanel iconBar;

	public FlowLayout layout;
    //private JList entitiesJList;

	private SavingLoading saveLoad;

	public EditorPanel( Board boardInstance) {
		//initializing some of the fields
		mode = EditorPanel.DEFAULT_MODE;
		newEntityPath = "";
		//entityPlacementMode = false;
		editorMousePos = new Point();
		ghostSprite = SpriteNull.getNullSprite();
		testFlag = true;
        clickPosition = new Point(0,0);
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
		// inline panel for button
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBackground(Color.GRAY);
	    buttonPanel.setBorder(BorderFactory.createTitledBorder("buttonPanelTest"));
		buttonPanel.setPreferredSize(new Dimension(190, 50));		
		buttonPanel.add(deleteEntButton);

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
		
	    iconBar = new JPanel(new FlowLayout(FlowLayout.LEADING)); 
	    iconBar.setBackground(Color.GRAY);
	    iconBar.setPreferredSize(new Dimension(195,200));
	    iconBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		IconLoader iconLoader = new IconLoader(this, iconBar);

		
		JScrollPane iconBarScrollPane = new JScrollPane(iconBar);
		iconBarScrollPane.setVerticalScrollBarPolicy((JScrollPane.VERTICAL_SCROLLBAR_ALWAYS));
		add(iconBarScrollPane, FlowLayout.TRAILING);
		
		//testing setting the ghostSprite
		//setGhostSprite(ASSET_PATH + PF1 );
		//revalidate();
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
				deselectAllEntities();
				deleteEntButton.setEnabled(true);
				
				//sets Board's current entity
				setSelectedEntityThruEditor(board.getStaticEntities().get(currentEntIndex));
				//board.
				createAndShowPropertiesPanel(board);
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
	} //end of inner class
	
	public void mousePressed(MouseEvent e) {
		if (!mouseClick) {
			clickPosition.setLocation(e.getX(),e.getY());
			mouseClick = true;
			if (mode == EditorPanel.DEFAULT_MODE) 
			{
				deselectAllEntities();


				//MainWindow.getEditorPanel().setEntityCoordsLabel(String.format("Mouse Click: %s, %s", e.getX(), e.getY()));
				setEntityCoordsLabel(String.format("Mouse Click: %s, %s", e.getX(), e.getY()));			
				checkForSelection(clickPosition);  			  		
				if (board.getCurrentSelectedEntity() != null) {  	// there is entity under cursor
					/*if(currentSelectedEntity.isSelected != true) {
	  					currentSelectedEntity.isSelected = true;	  					
	  				}
	  				else{
	  					currentSelectedEntity.isSelected = false;
	  				} */
					/*
	  				selectedBox.setSize(currentSelectedEntity.getEntitySprite().getImage().getWidth(null),
	  									currentSelectedEntity.getEntitySprite().getImage().getHeight(null) ); */	  				
					//SidePanel.setSelectedEntityName("Selected: " + currentSelectedEntity.name);
					setSelectedEntityNameLabel("Selected: " + board.getCurrentSelectedEntity().name);
					setEntityCoordsLabel("Coords. of selected entity: " + board.getCurrentSelectedEntity().getX() + ", " + board.getCurrentSelectedEntity().getY());
					//get offsets
					clickPositionXOffset = e.getX() - board.getCurrentSelectedEntity().getX() ;
					clickPositionYOffset = e.getY() - board.getCurrentSelectedEntity().getY() ;
				}
				// WILL TRIGGER DESELECTING THE CURRENT ENTITY
				// CODE FIRES WHEN YOU CLICK AND NOTHING IS UNDER CURSOR
				else {   				
					setSelectedEntityNameLabel("Nothing Selected");
					setEntityCoordsLabel("Coords. of selected entity: N/A");
				}
			}
			//entity placement mode is ON
			else if (mode == EditorPanel.ENTPLACEMENT_MODE) {
				clickPositionXOffset =( (ghostSprite.getImage().getWidth(null)) / 2);
				clickPositionYOffset =  ( (ghostSprite.getImage().getHeight(null)) / 2);
				addEntity(e.getX(), e.getY(), 0, 0, newEntityPath);
				nullifyGhostSprite();
				//editorPanel.entityPlacementMode = false;
				deselectAllEntities();
			}
		}
	}
	public void mouseDragged(MouseEvent e) {
		if (mode == EditorPanel.DEFAULT_MODE) {
			setMousePosLabel(String.format("Mouse Click: %s, %s", e.getX(), e.getY()));

			if (board.getCurrentSelectedEntity() != null) {
				board.getCurrentSelectedEntity().setX(e.getX() - clickPositionXOffset);
				board.getCurrentSelectedEntity().setY(e.getY() - clickPositionYOffset);
				setEntityCoordsLabel("Coords. of selected entity: " + board.getCurrentSelectedEntity().getX() + ", " + board.getCurrentSelectedEntity().getY());
			}

		}
	}
	public void mouseMoved(MouseEvent e){
		setEditorMousePos(e.getX(), e.getY());
	}
	public void mouseReleased(MouseEvent e) {	
		if ( board.getCurrentSelectedEntity() == null) {
			deselectAllEntities();
		}
		if (mode == EditorPanel.ENTPLACEMENT_MODE)
			mode = EditorPanel.DEFAULT_MODE;
		mouseClick = false;
	}
	
  	public void checkForSelection(Point click) { //redundant
  		board.setCurrentSelectedEntity(clickedOnEntity(click));
  		//currentSelectedEntity = clickedOnEntity(click);

  		if (board.getCurrentSelectedEntity() != null)
  			board.currentDebugEntity = board.getCurrentSelectedEntity();

  	}
  	private EntityStatic clickedOnEntity(Point click) {
  		int counter = 0;
  		for (EntityStatic entity : board.getStaticEntities()) {
  			
	 		if (entity.getEntitySprite().hasSprite()){ //if entity has sprite, select by using sprite dimensions
	  			board.selectedBox.setLocation(entity.getX() + entity.getSpriteOffsetX(), entity.getY() + entity.getSpriteOffsetY());
	  			board.selectedBox.setSize(entity.getEntitySprite().getImage().getWidth(null), entity.getEntitySprite().getImage().getHeight(null) );
	  			if (board.selectedBox.contains(click)) 
	  			{
	  				//entity.isSelected = true;
	  				enableEditPropertiesButton(true); //might not need
	  				restorePanels();
	  				setAllEntitiesComboBoxIndex(counter);
	  	  			setSelectedEntityNameLabel("Selected: " + entity.name);
	  	  			setEntityCoordsLabel("Coords. of selected entity: " + entity.getX() + ", " + entity.getY());
	  				return entity;
	  			}
	  			counter++;
	  			
	 		}
	 		else {
	 			//Entity has no sprite, so selection needs some other method, like by boundary
	 		}
  			
  		}
  		//TESTING BOX SELECTION

  		//nothing was found under cursor: 
  		enableEditPropertiesButton(false);
  		minimizePanels();
  		return null;
  	}
	public void deselectAllEntities() {
  		board.setCurrentSelectedEntity(null);
  		enableEditPropertiesButton(false);
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
	public void createAndShowPropertiesPanel(Board board) {
		propertyPanelTest.removeAll();
		propertyPanelTest.add(new PropertiesPanel(this, board));
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
		deselectAllEntities();
		board.getStaticEntities().remove(index);
		removeEntryFromListOfPropLists(index); 	//must remove corresponding property of deleted entity
		updateAllEntitiesComboBox();
		deselectAllEntities();
		minimizePanels();
	}
	//so many ways I can do this. Will start with overloaded methods
	public void addEntity(int x, int y, int offsetX, int offsetY, String path) {  //default one. Adds test entity
		EntityStatic newEnt;
		if (path.toLowerCase().contains("platform")) {
			newEnt = new Platform(x, y, offsetX, offsetY, path);
		}
		else if (path.toLowerCase().contains("ground")) {
			newEnt = new Ground(x, y, offsetX, offsetY, path);
		}
		else if (path.toLowerCase().contains("grass")) {
			newEnt = new Grass(x, y, offsetX, offsetY, path);
		}
		else {
			newEnt = new ObjectTemplate(x, y, offsetX, offsetY);
		}
		deselectAllEntities();
		board.getStaticEntities().add(newEnt);
		addEntryToListOfPropLists(new PropertiesList(newEnt));
		updateAllEntitiesComboBox();
        allEntitiesComboBox.setSelectedIndex(allEntitiesComboBox.getItemCount()-1);
	}
	//will refresh(create a new one of)staticEntityStringArr, remove old comboBox and then create & add a new updated one
	//PROBLEM AREA, still a problem. Thought was fixed but has an issue when deleting entities
	public void updateAllEntitiesComboBox() {
		System.out.println("item count: "+ allEntitiesComboBox.getItemCount());
		int prevIndex = allEntitiesComboBox.getSelectedIndex();
		int prevMax = allEntitiesComboBox.getItemCount();
		updateEntityStringArr();
		entitiesComboBoxPanel.remove(allEntitiesComboBox);
		allEntitiesComboBox = null;
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
		ghostSprite = new SpriteStillframe(System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path);
	}
	public String getNewEntityPath() {
		return newEntityPath;
	}
	public void setNewEntityPath(String newEntityPath) {
		this.newEntityPath = newEntityPath;
	}
	public void setEditorMousePos(int x, int y){
		editorMousePos.x = x;
		editorMousePos.y = y;
	}
	public Point getEditorMousePos(){
		return editorMousePos;
	}
	

}
