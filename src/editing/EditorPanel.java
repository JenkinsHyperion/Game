package editing;

import javax.annotation.Generated;
import javax.swing.*;

import Input.*;
import editing.worldGeom.*;
import editing.worldGeom.WorldGeometry.VertexPlaceMode;
import editing.worldGeom.WorldGeometry.VertexSelectMode;
import editing.worldGeom.WorldGeometry.VertexSelectMode.CtrlVertexSelectLClickEvent;
import editing.worldGeom.WorldGeometry.VertexSelectMode.SelectionRectEvent;
import editing.worldGeom.WorldGeometry.VertexSelectMode.TranslateEvent;
import editing.worldGeom.WorldGeometry.VertexSelectMode.VertexSelectLClickEvent;
import sprites.*;
import entities.*;
import saving_loading.SavingLoading;
import engine.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
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
	
//	##### MODES #####
	private EditorSelectMode editorSelectMode;
	private EditorPlaceMode editorPlaceMode;
	private WorldGeometry worldGeomMode;
	private CameraPanEvent cameraPanEvent;
	private SpriteEditorMode spriteEditorMode;
	private BoundaryEditorMode boundaryEditorMode;
	private ModeAbstract editorMode;
	/*@Deprecated
	public static final int DEFAULT_MODE = 0;
	public static final int ENTPLACEMENT_MODE = 1;
	public static final int WORLDGEOM_MODE = 2;
	public static final int CAMERAPAN_MODE = 3;*/
//	Mouse and positioning fields
	protected boolean mouseClick = false;
	//private Point clickPosition;
	private Point editorMousePos;
	private Point oldMousePanPos; // the reference point of last click position for mouse camera panning
	private Point oldCameraPos;
	private Robot automaticMouseReturn;
// oldMousePanPos will only refresh when shift is held, and then mouse is dragged. Purely for the panning
	
// the distance from reference point and current point
	private double mousePanDX = 0f;
	private double mousePanDY = 0f;
	public int clickPositionXOffset;
	public int clickPositionYOffset;
	
	//public int mode; 
	public int modeBuffer; // to store most recent mode a quick-mode change happens (such as shift pressed for panning)
	private String newEntityPath; //**Will definitely need to rework this.
//	currently used as an identifier to indicate which entity should be created,
//	but the system should be more like a factory.

//	Keyboard fields
	private boolean keypressUP = false;
	private boolean keypressDOWN = false;
	private boolean keypressLEFT = false;
	private boolean keypressRIGHT = false;
	private boolean keypressSHIFT = false;
	private float pan_dx = 0.0f;
	private float pan_dy = 0.0f;
	public final Dimension minimizedSize = new Dimension(200,20);
	public final Dimension propPanelDefaultSize = new Dimension(215,125);
	public final Dimension allEntitiesComboBoxDefSize = new Dimension(120,20);
	protected int currentEntIndex;

	protected BoardAbstract board;
	protected Camera camera;
	private Sprite ghostSprite; 

    //protected EntityStatic currentSelectedEntity;
    public Rectangle selectedBox;
    
	
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
	protected JButton worldGeomButton;
	protected JButton entitySelectButton;
	protected JButton vertexPlaceModeButton;
	protected JButton vertexSelectModeButton;
	//protected JButton entidorModeButton;
	protected JButton spriteEditorButton;
	protected JButton boundaryEditorButton;

//	Panels
	private JPanel entitiesComboBoxPanel;
	private JPanel labelsPanel;
	private JPanel buttonPanel;
	private JPanel propertyPanelTest;
	private JPanel iconBar;
    //private JList entitiesJList;
	private SavingLoading saveLoad;
	
// ##################### CONSTRUCTOR #################################################################
// ##################### CONSTRUCTOR #################################################################
	public EditorPanel( BoardAbstract board2) {   
		//initializing some of the fields
	
		this.board = board2;
		this.camera = board.getCamera();
		oldMousePanPos = new Point();
		//worldMousePos = new Point();
		oldCameraPos = new Point();
		editorSelectMode = new EditorSelectMode();
		editorPlaceMode = new EditorPlaceMode();
		cameraPanEvent = new CameraPanEvent();
		worldGeomMode = new WorldGeometry(this, board2);
		boundaryEditorMode = new BoundaryEditorMode();
		this.editorMode = editorSelectMode;
		newEntityPath = "";
		selectedBox = new Rectangle();
		editorMousePos = new Point();
		ghostSprite = null; 							//FIXME
        //clickPosition = new Point(0,0);
        

		//set default selected entity so it's not null

		//setSelectedEntityThruEditor(board.getStaticEntities().get(0)); //NEEDS ZERO ARRAY SIZE CHECK

		//setCurrentSelectedEntity( EntityNull.getNullEntity() ); 
        //setCurrentSelectedEntity( this.board.getStaticEntities().get(0) ); 
		
		//set the editor's layout
		setLayout(new FlowLayout(FlowLayout.LEADING, 3, 3));
		setBackground(Color.GRAY);
		
		//there will be as many property lists as there are entities, and they will directly correspond.
		//make sure updateEntityStringArr() is called whenever entities are added or removed. 
		// populateListOfPropLists will only be called once, right here.
		updateEntityStringArr(); //populates the entity string array representation with elements from Board's static entity arraylist
		//populateListOfPropLists();
		
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
				//new SavingLoading(board).writeLevel(board.getStaticEntities(), levelName);
				new SavingLoading(board).writeLevel( board.listCurrentSceneEntities(), levelName );
				
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
				if (levelName != null){ //TESTING LOADING WORLDS
					
					EntityFactory.deserializeEntityData(  new SavingLoading(board).loadLevelEntities( levelName ) , board );

				}
			}
		});
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

		entitySelectButton = new JButton("Entity Select");
		entitySelectButton.setFocusable(false);
		entitySelectButton.setEnabled(true);
		entitySelectButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {						
				setMode(getEditorSelectMode());
				vertexPlaceModeButton.setEnabled(false);
				vertexSelectModeButton.setEnabled(false);
			} 		
		});
		
		worldGeomButton = new JButton("World Geom");
		worldGeomButton.setFocusable(false);
		worldGeomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setMode(getWorldGeomMode());
				vertexPlaceModeButton.setEnabled(true);
				vertexSelectModeButton.setEnabled(true);
			}
		});
		vertexPlaceModeButton = new JButton("VtxPlace");
		vertexPlaceModeButton.setFocusable(false);
		vertexPlaceModeButton.setEnabled(false);
		vertexPlaceModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				worldGeomMode.setMode(worldGeomMode.getVertexPlaceMode());
			}
		});
		vertexSelectModeButton = new JButton("VtxSelect");
		vertexSelectModeButton.setFocusable(false);
		vertexSelectModeButton.setEnabled(false);
		vertexSelectModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				worldGeomMode.setMode(worldGeomMode.getVertexSelectMode());
			}
		});
		spriteEditorButton = new JButton("SpriteEditor");
		spriteEditorButton.setFocusable(false);
		spriteEditorButton.setEnabled(false);
		spriteEditorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		boundaryEditorButton = new JButton("BoundEditor");
		boundaryEditorButton.setFocusable(false);
		boundaryEditorButton.setEnabled(false);
		boundaryEditorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setMode(getBoundaryEditorMode());
			}
		});
		// inline panel for button
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBackground(Color.GRAY);
	    buttonPanel.setBorder(BorderFactory.createTitledBorder("buttonPanelTest"));
		buttonPanel.setPreferredSize(new Dimension(190, 250));		
		buttonPanel.add(deleteEntButton);
		buttonPanel.add(worldGeomButton);
		buttonPanel.add(entitySelectButton);
		buttonPanel.add(vertexPlaceModeButton);
		buttonPanel.add(vertexSelectModeButton);
		buttonPanel.add(spriteEditorButton);
		buttonPanel.add(boundaryEditorButton);

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
		
	/*	try {
			automaticMouseReturn = new Robot();
		} catch (AWTException e) {}
		*/
		//testing setting the ghostSprite
		//setGhostSprite(ASSET_PATH + PF1 );
		//revalidate();
		
	} // #### end of constructor #### #####################################################################################
	 // #### end of constructor #### #####################################################################################
	
	//Handler for the allEntitiesComboBox drop down panel
	// Out of date because I need to completely rework how this class handles multiple selections

	public class EntitiesComboBoxActionHandler implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			editorMode = editorSelectMode;
			//JComboBox cb = (JComboBox)e.getSource();
			//cb.getSelectedIndex());
			restorePanels();
			//String testString = (String)allEntitiesComboBox.getSelectedItem();
			//System.out.println(testString);
			//allEntitiesComboBox.addItem
			currentEntIndex = allEntitiesComboBox.getSelectedIndex();
			System.out.println(currentEntIndex);
			try{					
				editorSelectMode.selectedEntities.clearSelectedEntities();
				deleteEntButton.setEnabled(true);
				
				//sets Board's current entity
				//setCurrentSelectedEntity(board.getEntities().get(currentEntIndex));
				editorSelectMode.addSelectedEntity(board.listCurrentSceneEntities()[currentEntIndex]);
				//editorSelectMode.addSelectedEntity(board.list);
				createAndShowPropertiesPanel(board);
				setSelectedEntityNameLabel("Selected: " + board.listCurrentSceneEntities()[currentEntIndex].name);
				setEntityCoordsLabel(String.format("Coords of Selected Entity: %s,%s", editorSelectMode.selectedEntities.get(0).getX(), editorSelectMode.getSelectedEntities().get(0).getY()));
			}
			catch (NullPointerException exception){
				exception.printStackTrace();
				System.err.println("nullpointerexception"); 
			}
		}	
	} //end of inner class
	
	
	
	
	public void mousePressed(MouseEvent e) {
		setEditorMousePos(e.getX(), e.getY());
		//clickPosition.setLocation(e.getX(),e.getY());
		this.editorMode.mousePressed(e);
		
	}
	public void mouseDragged(MouseEvent e) {
		setEditorMousePos(e.getX(), e.getY());
		this.editorMode.mouseDragged(e);
	}
	public void mouseMoved(MouseEvent e){
		setEditorMousePos(e.getX(), e.getY());
		this.editorMode.mouseMoved(e);
	}
	public void mouseReleased(MouseEvent e) {	
		setEditorMousePos(e.getX(), e.getY());
		this.editorMode.mouseReleased(e);

	}
	
	// ############ KEY HANDLING SECTION ###########
	public void keyPressed(KeyEvent e) {
		this.editorMode.keyPressed(e);
		//worldGeomMode.keyPressed(e);
		/*int key = e.getKeyCode();
		if (key == KeyEvent.VK_UP && !keypressUP ) {
			keypressUP = true; 
			pan_dy = -8f;
		}
		else if (key == KeyEvent.VK_DOWN && !keypressDOWN) {
			keypressDOWN = true;
			pan_dy = 8f;
		}
		else if (key == KeyEvent.VK_LEFT && !keypressLEFT) {

			keypressLEFT = true; 	
			pan_dx = -8f;
		}
		else if (key == KeyEvent.VK_RIGHT && !keypressRIGHT ) { 

			keypressRIGHT= true;
			pan_dx = 8f;
		}	
		else if (key == KeyEvent.VK_SHIFT && !keypressSHIFT) {
			keypressSHIFT = true;
			//modeBuffer = mode; // save the most recent mode, will switch back when shift is released
			mode = CAMERAPAN_MODE;
		}*/
		
		
		//this.camera.translate(pan_dx, pan_dy);
	}	
	public void keyReleased(KeyEvent e) {
		this.editorMode.keyReleased(e);
		//worldGeomMode.keyReleased(e);
		/*int key = e.getKeyCode();
		if (key == KeyEvent.VK_UP && keypressUP ) {
			keypressUP = false; 
			pan_dy = 0f;
		}
		else if (key == KeyEvent.VK_DOWN && keypressDOWN) {
			keypressDOWN = false;
			pan_dy = 0f;
		}
		else if (key == KeyEvent.VK_LEFT && keypressLEFT) {
			keypressLEFT = false;				
			pan_dx = 0f;
		}
		else if (key == KeyEvent.VK_RIGHT && keypressRIGHT) { 
			keypressRIGHT= false;
			pan_dx = 0f;
		}
		else if (key == KeyEvent.VK_SHIFT && keypressSHIFT) {
			keypressSHIFT = false;
			mode = modeBuffer; //sets mode back to most recent mode before shift was pressed
		}
		this.camera.translate(pan_dx, pan_dy);*/
	}
    //END OF KEYHANDLING SECTION 
	@Deprecated
	public void deselectAllEntities() {
  		//setCurrentSelectedEntity( EntityNull.getNullEntity() );
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
	public void createAndShowPropertiesPanel(BoardAbstract board2) {
		propertyPanelTest.removeAll();
		propertyPanelTest.add(new PropertiesPanel(this, board2));
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
	/*public Property getThisProperty(int propType){
		try{
			return listOfPropLists.get(currentEntIndex).getProperty(propType);
		} catch (Exception e) { e.printStackTrace();return null; } //to handle if there for some reason isn't a current Entity index
	}*/
	/**
	 * <b>Helper method to return Board's currentSelectedEntity, also runs a null check.</b>
	 * @return Board's current selected Entity
	 */
	/*
	public EntityStatic currentSelectedEntity{
		try{
			return currentSelectedEntity;
		}catch (Exception e) {	e.printStackTrace();  return null; }
	} */
	/**
	 * Helper method to set Board's currentSelectedEntity
	 * @param newSelectedEntity
	 */
	
	
	/*
	private void setSelectedEntityThruEditor(EntityStatic newSelectedEntity){
		board.setCurrentSelectedEntity(newSelectedEntity);
	}
///	*/
	//helper function to transfer data from ArrayList into a regular array
	@Deprecated
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
	public void populateArrayNamesFromArray(String[] arr, EntityStatic[] arraySource)
	{
		try {
			/*System.out.println("Array size " + arr.length);
			System.out.println("ArraySource size " + arraySource.length);*/
			for (int i = 0; i < arraySource.length; i++)
			{
				arr[i] = arraySource[i].name;
			}
		}
		catch(Exception e) {
		}
	}
	// #### Section for adding or removing actual entities
	public void updateEntityStringArr() {
		//staticEntityStringArr = new String[(board.getStaticEntities().size())];	
		staticEntityStringArr = new String[(board.listCurrentSceneEntities().length)];	
		//populateArrayFromList(staticEntityStringArr, board.getStaticEntities());
		populateArrayNamesFromArray(staticEntityStringArr, board.listCurrentSceneEntities());
	}
	public void deleteEntity(int index) {
		//deselectAllEntities();
		
		//board.listEntities().remove(index); //CHANGE TO DELETE FUNCTION IN BOARD
		
		//removeEntryFromListOfPropLists(index); 	//must remove corresponding property of deleted entity
		//updateAllEntitiesComboBox();
		//deselectAllEntities();
		minimizePanels();
	}
	//so many ways I can do this. Will start with overloaded methods
	public void addEntity(int x, int y, int offsetX, int offsetY, String path) {  //default one. Adds test entity
		EntityStatic newEnt;
		if (path.toLowerCase().contains("platform")) {
			
			//newEnt = new Platform(x, y, offsetX, offsetY, path);
			//
			//newEnt = Platform01.buildPlatform( x , y , offsetX , offsetY , path );
		}
		else if (path.toLowerCase().contains("ground")) {
			/*newEnt = Platform01.buildStaticEntity(x,  y, Platform01.COLLIDABLE);
			Collidable collidable = new Collidable(newEnt, new Boundary.Box(446,100,-223,-50) );
			newEnt.setCollisionProperties( collidable );
			newEnt.loadSprite("ground_1.png" , -223 , -53 );*/
		}
		else if (path.toLowerCase().contains("grass")) {
			//newEnt = new Grass(x, y, offsetX, offsetY, path);

			// Possibilities:
			/*newEnt = Platform01.buildStaticEntity( x , y , Platform01.INTANGIBLE );
			/*
			newEnt = Platform01.buildStaticEntity( x , y , Platform01.INTANGIBLE );

			newEnt.setCollisionProperties(NonCollidable.getNonCollidable());
			newEnt.loadSprite("grass01.png");*/
		}
		else {
			newEnt = new EntityStatic(x, y);
		}
		//deselectAllEntities();
		//board.getStaticEntities().add(newEnt);
		//addEntryToListOfPropLists(new PropertiesList(newEnt));
		//updateAllEntitiesComboBox();
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
		//allEntitiesComboBox = null;
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
	/*public void populateListOfPropLists() {
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
*/	// #### End of section for prop lists
	
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
		ghostSprite = null; //FIXME
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
		//worldMousePos.x = this.camera.getLocalX(x);
		//worldMousePos.y = this.camera.getLocalY(y);
	} 
	public Point getEditorMousePos(){
		return editorMousePos;
	} 
	public Rectangle getSelectedBox(){
		return selectedBox;
	}
	/*
	public void setCurrentSelectedEntity(EntityStatic newSelectedEntity){
		currentSelectedEntity = newSelectedEntity;
	}
	*/
	/*
	public EntityStatic getCurrentSelectedEntity() {
		return currentSelectedEntity; 
	}
	*/
	public void render(Graphics g) {
		
		this.editorMode.render(g);
		
		//this.getGhostSprite().editorDraw(getEditorMousePos());
		
	}
	public void defaultRender(Graphics g) {
		//will contain a render procedure for modes that certainly don't need their own rendering implementation 
	}
	public void addSelectedEntity(EntityStatic entity) {
		this.getEditorSelectMode().addSelectedEntity(entity);
	}
	public void removeSelectedEntity(EntityStatic entity) {
		this.getEditorSelectMode().removeSelectedEntity(entity);
	}
	public ArrayList<EntityStatic> getSelectedEntities () {
		return this.getEditorSelectMode().getSelectedEntities();
	}
	public void setMode(ModeAbstract newMode) {
		this.editorMode = newMode;
	}
	public EditorSelectMode getEditorSelectMode() {
		return this.editorSelectMode;
	}
	public EditorPlaceMode getEditorPlaceMode() {
		return this.editorPlaceMode;
	}
	public WorldGeometry getWorldGeomMode() {
		return this.worldGeomMode;
	}
	public CameraPanEvent getCameraPanMode() {
		return this.cameraPanEvent;
	}
	public SpriteEditorMode getSpriteEditorMode() {
		return this.spriteEditorMode;
	}
	public BoundaryEditorMode getBoundaryEditorMode() {
		return this.boundaryEditorMode;
	}
	// ######################################### INNER CLASS MODES #######################################
	// ######################################### INNER CLASS MODES #######################################
	
/////////   INNER CLASS EDITORSELECTMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

	public class EditorSelectMode extends ModeAbstract {
		protected SelectedEntities selectedEntities;
		protected SelectionRectangleAbstract selectionRectangle;
		protected SelectionRectangleAbstract nullSelectionRectangle;
		protected SelectionRectangleAbstract selectionRectangleState;
		private Point initClickPoint;
		public EditorSelectMode() {
			initClickPoint = new Point();
			selectedEntities = new SelectedEntities(camera);
			nullSelectionRectangle = SelectionRectangleNull.getNullSelectionRectangle();
			selectionRectangle = new SelectionRectangle(Color.BLUE, Color.cyan, camera, initClickPoint);
			selectionRectangleState = nullSelectionRectangle;
			
			inputController = new InputController();
			this.inputController.createMouseBinding(MouseEvent.BUTTON1, new EntitySelectLClickEvent());
			this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON3, new CtrlEntitySelectLClickEvent());
			this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON2, new TranslateEvent());
			this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON1, new SelectionRectEvent());
			this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());
			
			this.inputController.createKeyBinding(KeyEvent.VK_ESCAPE, new EscapeEvent());
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			inputController.mousePressed(e);
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			inputController.mouseDragged(e);
		}
		@Override
		public void mouseMoved(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {
			inputController.mouseReleased(e);
		}
		@Override
		public void keyPressed(KeyEvent e) {
			inputController.keyPressed(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {inputController.keyReleased(e); }
		@Override
		public void render(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(Color.BLUE);
			selectedEntities.drawClickableBox(g2, camera);
			selectionRectangleState.draw(g2, camera);
		}
		@SuppressWarnings("deprecation")
		public void checkForEntityCtrlClick(Point click) {
			for(EntityStatic entity: board.listCurrentSceneEntities()) {
				Rectangle clickableRect = new Rectangle();
				//clickableRect.setLocation(entity.getXRelativeTo(camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(camera) + entity.getSpriteOffsetY());
				clickableRect.setLocation(entity.getX() + entity.getSpriteOffsetX(), entity.getY() + entity.getSpriteOffsetY());
				clickableRect.setSize(entity.getEntitySprite().getImage().getWidth(null),
						entity.getEntitySprite().getImage().getHeight(null) );
				if (clickableRect.contains(click)) {
					if (selectedEntities.contains(entity))
						selectedEntities.removeSelectedEntity(entity);
					else
						selectedEntities.addSelectedEntity(entity);
				}
				//if (selectedEntities.contains(entity))
					//selectedEntities.add
			}
			if (selectedEntities.size() == 1) {
				spriteEditorButton.setEnabled(true);
				boundaryEditorButton.setEnabled(true);
				System.out.println("reached");
			}
			else {
				spriteEditorButton.setEnabled(false);
				boundaryEditorButton.setEnabled(false);
			}
		}
		@SuppressWarnings("deprecation")
		public void checkForEntity(Point click) {
			//boolean atLeastOneVertexFound = false;
			//since this is the regular click method, would want to make sure any selected entities are deselected first
			if (selectedEntities.size() > 0)
				spriteEditorButton.setEnabled(false);
				boundaryEditorButton.setEnabled(false);
				selectedEntities.clearSelectedEntities();
			
			for (EntityStatic entity: board.listCurrentSceneEntities()) {
				Rectangle clickableRect = new Rectangle();
				//clickableRect.setLocation(entity.getXRelativeTo(camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(camera) + entity.getSpriteOffsetY());
				clickableRect.setLocation(entity.getX() + entity.getSpriteOffsetX(), entity.getY() + entity.getSpriteOffsetY());
				clickableRect.setSize(entity.getEntitySprite().getImage().getWidth(null),
						entity.getEntitySprite().getImage().getHeight(null) );
				if (clickableRect.contains(click))
				{
					if (selectedEntities.contains(entity) == false) {
						//atLeastOneVertexFound = true;
						selectedEntities.addSelectedEntity(entity);
						spriteEditorButton.setEnabled(true);
						boundaryEditorButton.setEnabled(true);
						break;
					}
				}
			}
		}
		@SuppressWarnings("deprecation")
		public void checkForEntityInSelectionRect(Rectangle selectionRect) {
			for (EntityStatic entity: board.listCurrentSceneEntities()) {
				Rectangle clickableRect = new Rectangle();
				//clickableRect.setLocation(entity.getXRelativeTo(camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(camera) + entity.getSpriteOffsetY());
				clickableRect.setLocation(entity.getX() + entity.getSpriteOffsetX(), entity.getY() + entity.getSpriteOffsetY());
				clickableRect.setSize(entity.getEntitySprite().getImage().getWidth(null),
						entity.getEntitySprite().getImage().getHeight(null) );
				//if(selectionRect.contains(clickableRect)) {
				if(selectionRect.intersects(clickableRect)) {
					if(selectedEntities.contains(entity) == false) {
						selectedEntities.addSelectedEntity(entity);
					}
				}
			}
			if (selectedEntities.size() == 1) {
				//TODO return to this once synced with matt's new Sprite composite system
				spriteEditorButton.setEnabled(true);
				boundaryEditorButton.setEnabled(true);
			}
			else {
				spriteEditorButton.setEnabled(false);
				boundaryEditorButton.setEnabled(false);
			}
		}
		public void addSelectedEntity(EntityStatic entity) {
			selectedEntities.addSelectedEntity(entity);
		}
		public void removeSelectedEntity(EntityStatic entity) {
			selectedEntities.removeSelectedEntity(entity);
		}
		public ArrayList<EntityStatic> getSelectedEntities () {
			return selectedEntities.getSelectedEntities();
		}
		public class EntitySelectLClickEvent implements MouseCommand {

			@Override
			public void mousePressed() {
				checkForEntity(camera.getLocalPosition(editorMousePos));
				selectedEntities.printSelectedEntities();
			}

			@Override
			public void mouseDragged() {}

			@Override
			public void mouseReleased() {} 	
		}
		public class CtrlEntitySelectLClickEvent implements MouseCommand {

			@Override
			public void mousePressed() {
				checkForEntityCtrlClick(camera.getLocalPosition(editorMousePos));
			}
			@Override
			public void mouseDragged() {}
			@Override
			public void mouseReleased() {}
		}
		public class TranslateEvent implements MouseCommand{

			public void mousePressed() {
				initClickPoint.setLocation(camera.getLocalPosition(editorMousePos));
				selectedEntities.updateOldEntityPositions();
			}
			public void mouseDragged() {
				selectedEntities.translate(initClickPoint, editorMousePos);
			}
			public void mouseReleased() {}

		}
		public class SelectionRectEvent implements MouseCommand {

			@Override
			public void mousePressed() {
				selectionRectangleState = selectionRectangle;
				initClickPoint.setLocation(camera.getLocalPosition(editorMousePos));
				selectionRectangleState.setInitialRectPoint();
			}

			@Override
			public void mouseDragged() {
				selectionRectangleState.translateEndPoint(camera.getLocalPosition(editorMousePos));
			}

			@Override
			public void mouseReleased() {
				//command to select vertices underneath box
				checkForEntityInSelectionRect(selectionRectangleState.getWrekt());
				selectionRectangleState.resetRect();
				selectionRectangleState = nullSelectionRectangle;
			}
			
		}
		public class EscapeEvent implements KeyCommand {
			@Override
			public void onPressed() {
				selectedEntities.clearSelectedEntities();
				spriteEditorButton.setEnabled(false);
				boundaryEditorButton.setEnabled(false);
			}
			public void onReleased() {}
			public void onHeld() {}
		}
	}  // end of EditorSelectMode inner class
	
/////////   INNER CLASS EDITORPLACEMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

	private class EditorPlaceMode extends ModeAbstract {
		public EditorPlaceMode() {
			inputController = new InputController();
			this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());			
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			//old version: vvvvvv 
			/*//entity placement mode is ON
			else if (mode == EditorPanel.ENTPLACEMENT_MODE) {
				clickPositionXOffset =( (ghostSprite.getImage().getWidth(null)) / 2);
				clickPositionYOffset =  ( (ghostSprite.getImage().getHeight(null)) / 2);
				addEntity(e.getX(), e.getY(), 0, 0, newEntityPath);
				nullifyGhostSprite();
				//editorPanel.entityPlacementMode = false;
				deselectAllEntities();
			}*/
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			//mode = EditorPanel.DEFAULT_MODE;
			setMode(getEditorSelectMode());
		}
		@Override
		public void keyPressed(KeyEvent e) {
			//do this just for escape, when you make the EscapeEvent object
			//editorPanel.mode = EditorPanel.DEFAULT_MODE;
        	//editorPanel.nullifyGhostSprite();
		}
		@Override
		public void keyReleased(KeyEvent e) {}
		@Override
		public void render(Graphics g) {
			// TODO Auto-generated method stub
		}
	}  // end of EditorPlaceMode inner class
	
	
	
	
/////////   INNER CLASS SPRITEEDITORMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

	private class SpriteEditorMode extends ModeAbstract {
		public SpriteEditorMode(){
			inputController = new InputController();
			this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());			
		
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			
		}
		@Override
		public void keyPressed(KeyEvent e) {
			
		}
		@Override
		public void keyReleased(KeyEvent e) {
			
		}
		@Override
		public void render(Graphics g) {
			
		}
		
	}
/////////   INNER CLASS BOUNDARYEDITORMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

	public class BoundaryEditorMode extends ModeAbstract {
		private ArrayList<EntityStatic> selectedEntitiesRef = getSelectedEntities();
		protected BufferedImage ghostVertexPic;
		private BoundaryVertexPlaceMode boundaryVertexPlaceMode;
		private BoundaryVertexSelectMode boundaryVertexSelectMode;
		private ModeAbstract boundaryMode;

		private ArrayList<Vertex> vertexList = new ArrayList<>();
		private ArrayList<Line2D.Double> surfaceLines = new ArrayList<>();
		public BoundaryEditorMode() {
			boundaryVertexPlaceMode = new BoundaryVertexPlaceMode();
			boundaryVertexSelectMode = new BoundaryVertexSelectMode();
			boundaryMode = boundaryVertexSelectMode;
			ghostVertexPic = (BufferedImage)Vertex.createVertexPic(0.5f);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			this.boundaryMode.mousePressed(e);
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			this.boundaryMode.mouseDragged(e);
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			this.boundaryMode.mouseMoved(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			this.boundaryMode.mouseReleased(e);
		}
		@Override
		public void keyPressed(KeyEvent e) {
			this.boundaryMode.keyPressed(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			this.boundaryMode.keyReleased(e);
		}
		@Override
		public void render(Graphics g) {
			this.boundaryMode.render(g);
		}
		public BoundaryVertexPlaceMode getVertexPlaceMode() {
			return this.boundaryVertexPlaceMode;
		}
		public BoundaryVertexSelectMode getVertexSelectMode() {
			return this.boundaryVertexSelectMode;
		}
		public void setMode(ModeAbstract newMode) {
			this.boundaryMode = newMode;
		}
		public Image getGhostVertexPic() {
			return ghostVertexPic;
		}
		public void refreshAllSurfaceLines() {
			surfaceLines.clear();
			for (int i = 0; i < vertexList.size()-1; i++) {
				Line2D.Double tempLine = new Line2D.Double(vertexList.get(i).getPoint(), vertexList.get(i+1).getPoint());
				surfaceLines.add(tempLine);
			}
		}
		public void addVertex(int x, int y) {
			//vertexList.add(new Vertex(this.camera.getLocalX(x), this.camera.getLocalY(y)));
			vertexList.add(new Vertex(x,y));
			if (vertexList.size() > 1) 
				//updateSurfaceLinesUponChange(vertexList.size()-2);
				refreshAllSurfaceLines();
		}
		public void removeVertex(SelectedVertices selectedVertices) {
			if (selectedVertices.size() == 1){
				for (Vertex verts: vertexList) {
					if (verts == selectedVertices.getVertices().get(0)) {
						vertexList.remove(verts);
						selectedVertices.clearSelectedVertices();
						break;
					}
				}
				refreshAllSurfaceLines();
			}
		}
		public void clearAllVerticesAndLines() {
			vertexList.clear();
			surfaceLines.clear();
		}
		public class BoundaryVertexSelectMode extends ModeAbstract {
			protected SelectedVertices selectedVertices;
			protected SelectionRectangleAbstract selectionRectangle;
			protected SelectionRectangleAbstract selectionRectangleState;
			protected SelectionRectangleAbstract nullSelectionRectangle;
			protected Point initClickPoint;

			public BoundaryVertexSelectMode() {
				initClickPoint = new Point();
				selectedVertices = new SelectedVertices(camera);
				nullSelectionRectangle = SelectionRectangleNull.getNullSelectionRectangle();
				selectionRectangle = new SelectionRectangle(Color.BLUE, Color.cyan, camera, initClickPoint);
				selectionRectangleState = nullSelectionRectangle;
				inputController = new InputController();
				this.inputController.createMouseBinding(MouseEvent.BUTTON1, new VertexSelectLClickEvent());
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON3, new CtrlVertexSelectLClickEvent());
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON2, new TranslateEvent());
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON1, new SelectionRectEvent());
				this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());			
			}

			@Override
			public void mousePressed(MouseEvent e) {
				inputController.mousePressed(e);			
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				inputController.mouseDragged(e);
			}
			public void mouseMoved(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				inputController.mouseReleased(e); }
			@Override
			public void keyPressed(KeyEvent e) { inputController.keyPressed(e);	}
			@Override
			public void keyReleased(KeyEvent e) {inputController.keyReleased(e); }

			@Override
			public void render(Graphics g) {
				Graphics2D g2 = (Graphics2D)g.create();			
				//old drawVertexPoints vvvvvvv
				for (Vertex vertex: vertexList) {
					//g2.drawImage(vertexPic, board.camera.getLocalX(point.x)-3, board.camera.getLocalY(point.y)-3, null);
					//camera.drawVertex(vertex, g);
					vertex.draw(g2, camera);
				}
				// old drawsurfacelines vvvvvv
				g2.setColor(Color.MAGENTA);
				for (int i = 0; i < vertexList.size()-1; i++) {
					Line2D.Double tempLine = new Line2D.Double(vertexList.get(i).getPoint(), vertexList.get(i+1).getPoint());
					// abstract world geometry surface lines later on
					camera.draw(tempLine);
				}
				
				// section to draw selected Vertex (if one is selected)
				g2.setColor(Color.GREEN);
				//currentSelectedVertex.drawClickableBox(g2, camera);
				selectedVertices.drawClickableBox(g2, camera);
				g2.setColor(Color.BLUE);
				// vvvv section to draw selection rectangle
				selectionRectangleState.draw(g2, camera);
				
			}
			public void checkForVertexShiftClick(Point click) {
				for (Vertex vertex: vertexList) {
					if (vertex.getClickableZone().contains(click)) {
						if (selectedVertices.contains(vertex)) 
							selectedVertices.removeSelectedVertex(vertex);
						else
							selectedVertices.addSelectedVertex(vertex);
					}
				}
			}
			public void checkForVertex(Point click) {
				//boolean atLeastOneVertexFound = false;
				//since this is the regular click method, would want to make sure any selected vertices are deselected first
				//TODO: DON'T USE THIS METHOD WITH THE SELECTION BOX PROCEDURE
				if (selectedVertices.size() > 0)
					selectedVertices.clearSelectedVertices();

				for (Vertex vertex: vertexList) {
					if (vertex.getClickableZone().contains(click)) 
					{
						if (selectedVertices.contains(vertex) == false) {
							//atLeastOneVertexFound = true;
							selectedVertices.addSelectedVertex(vertex);
							break;
						}
					}
				}
				/*if (atLeastOneVertexFound == false)
					selectedVertices.clearSelectedVertices();*/
			}
			public void checkForVertexInSelectionRect(Rectangle selectionRect) {
				for (Vertex vertex: vertexList) {
					if (selectionRect.intersects(vertex.getClickableZone())){
						if(selectedVertices.contains(vertex) == false) 
							selectedVertices.addSelectedVertex(vertex);
					}
				}
			}
		// ***** inner-inner classes for mouse behavior classes specific to vertex selecting
			public class VertexSelectLClickEvent implements MouseCommand{
				public void mousePressed() {
					// TODO Auto-generated method stub
					checkForVertex(camera.getLocalPosition(editorMousePos));
				}
				public void mouseDragged() {
					//currentSelectedVertex.translate(camera.getLocalPosition(editorMousePos));
				}
				public void mouseReleased() {}	
			} // end of VertexSelectLClickEvent inner class
			public class VertexSelectRClickEvent implements MouseCommand{
				public void mousePressed() {
					// TODO Auto-generated method stub
					//checkForVertex(camera.getLocalPosition(e.getPoint()));
					//checkForVertex(camera.getLocalPosition(editorMousePos));
				}
				public void mouseDragged() {
					// TODO Auto-generated method stub
					//currentSelectedVertex.translate(camera.getLocalPosition(editorMousePos));
				}
				public void mouseReleased() {
					// TODO Auto-generated method stub
				}	
			} // end of VertexSelectRClickEvent inner class
			public class CtrlVertexSelectLClickEvent implements MouseCommand{

				public void mousePressed() {
					// TODO Auto-generated method stub
					checkForVertexShiftClick(camera.getLocalPosition(editorMousePos));
				}
				public void mouseDragged() {
					// TODO Auto-generated method stub
					//currentSelectedVertex.translate(camera.getLocalPosition(editorMousePos));
				}
				public void mouseReleased() {
					// TODO Auto-generated method stub
				}

			} // end of ShiftVertexSelectLClickEvent inner class
			public class TranslateEvent implements MouseCommand{

				public void mousePressed() {
					// TODO Auto-generated method stub
					initClickPoint.setLocation(camera.getLocalPosition(editorMousePos));
					selectedVertices.updateOldVertexPositions();
				}
				public void mouseDragged() {
					selectedVertices.translate(initClickPoint, editorMousePos);
				}
				public void mouseReleased() {
					// TODO Auto-generated method stub
				}

			}
			public class SelectionRectEvent implements MouseCommand {

				@Override
				public void mousePressed() {
					// TODO Auto-generated method stub
					selectionRectangleState = selectionRectangle;
					initClickPoint.setLocation(camera.getLocalPosition(editorMousePos));
					selectionRectangleState.setInitialRectPoint();
				}

				@Override
				public void mouseDragged() {
					// TODO Auto-generated method stub
					selectionRectangleState.translateEndPoint(camera.getLocalPosition(editorMousePos));
				}

				@Override
				public void mouseReleased() {
					// TODO Auto-generated method stub
					//command to select vertices underneath box
					checkForVertexInSelectionRect(selectionRectangleState.getWrekt());
					selectionRectangleState.resetRect();
					selectionRectangleState = nullSelectionRectangle;
				}

			}
			public class EscapeEvent implements KeyCommand {
				@Override
				public void onPressed() {
					// TODO Auto-generated method stub
					selectedVertices.clearSelectedVertices();
				}
				public void onReleased(){} public void onHeld() {}
			}
			public class DeleteVerticesEvent implements KeyCommand {
				@Override
				public void onPressed() {
					// TODO Auto-generated method stub
					removeVertex(selectedVertices);
				}
				public void onReleased() {} public void onHeld() {}
			}
		} // end of boundaryVertexSelectMode
/////////   INNER CLASS BOUNDARYVERTEXPLACEMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
		public class BoundaryVertexPlaceMode extends ModeAbstract {
				public BoundaryVertexPlaceMode() {
					inputController = new InputController();
					this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());			
				}
				@Override
				public void mousePressed(MouseEvent e) {
				}
				@Override
				public void mouseDragged(MouseEvent e) {
				}
				@Override
				public void mouseMoved(MouseEvent e) {
				}
				@Override
				public void mouseReleased(MouseEvent e) {
				}
				@Override
				public void keyPressed(KeyEvent e) {
				}
				@Override
				public void keyReleased(KeyEvent e) {
				}
				@Override
				public void render(Graphics g) {
				}
		}
	} // end of BoundaryMode inner class 
		
		
		
		
//////////////////////////////////////////////////////////////////////	
	public class CameraPanEvent implements MouseCommand {
		public CameraPanEvent(){
		}
		@Override
		public void mousePressed() {
			//old version: vvvvvvv
			//else if (mode == EditorPanel.CAMERAPAN_MODE) {

			oldMousePanPos.setLocation(editorMousePos); // sets temporary old mouse position reference
			oldCameraPos.setLocation( camera.getFocus() );
			//Set start positions 


			//this.camera.setFocusForEditor(oldMousePanPos.getX(), oldMousePanPos.getY());
			mousePanDX = (editorMousePos.getX() - oldMousePanPos.getX());
			mousePanDY = (editorMousePos.getY() - oldMousePanPos.getY());
			//
		}
		@Override
		public void mouseDragged() {
			//old version: vvvvvvvvvvvvvvvv
			
			//else if (mode == EditorPanel.CAMERAPAN_MODE) {//keypressSHIFT == true: holding down shift key, ready to pan
				mousePanDX = editorMousePos.getX() - oldMousePanPos.getX();
				mousePanDY = editorMousePos.getY() - oldMousePanPos.getY();
				//camera.translate (-mousePanDX, -mousePanDY) or something
				// ^^^ must be negative because camera will pan in direction opposite the mouse drag
				mousePanDX = (editorMousePos.getX() - oldMousePanPos.getX());
				mousePanDY = (editorMousePos.getY() - oldMousePanPos.getY());
				camera.translate((float)mousePanDX, (float)mousePanDY);
				camera.setFocus(editorMousePos);
				camera.setFocusForEditor( oldCameraPos.getX() + ( oldMousePanPos.getX() - editorMousePos.getX() ), 
											    oldCameraPos.getY() + ( oldMousePanPos.getY() - editorMousePos.getY() )
											);// camera start pos   - (   distance dragged relative to screen         )
				
				// I changed this to screen relative positions (editorMousePos) since we want the distance between two points, 
				// which in this case is the same distance between world points because the camera is at x1 zoom. 
				
				//testing automatic mouse return
				//oldMousePanPos.setLocation( editorMousePos );
				//oldCameraPos.setLocation( this.camera.getFocus() );
				//automaticMouseReturn.mouseMove( 500 , 250 );
				
				//this.camera.setFocusForEditor(worldMousePos.x , worldMousePos.y );
				//oldMousePanPos.setLocation(e.getPoint());				
		//	}
		}
		@Override
		public void mouseReleased() {
			
		}
	} // end of CameraPanMode inner class
}
