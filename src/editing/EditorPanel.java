package editing;

import javax.annotation.Generated;
import javax.swing.*;

import Input.*;
import editing.worldGeom.*;
import editing.worldGeom.WorldGeometry.VertexPlaceMode;
import editing.worldGeom.WorldGeometry.VertexSelectMode;
import editing.worldGeom.WorldGeometry.VertexSelectMode.AlignToXAxisEvent;
import editing.worldGeom.WorldGeometry.VertexSelectMode.AlignToYAxisEvent;
import editing.worldGeom.WorldGeometry.VertexSelectMode.CtrlVertexSelectLClickEvent;
import editing.worldGeom.WorldGeometry.VertexSelectMode.DeleteVerticesEvent;
import editing.worldGeom.WorldGeometry.VertexSelectMode.SelectionRectEvent;
import editing.worldGeom.WorldGeometry.VertexSelectMode.SplitLineEvent;
import editing.worldGeom.WorldGeometry.VertexSelectMode.TranslateEvent;
import editing.worldGeom.WorldGeometry.VertexSelectMode.VertexSelectLClickEvent;
import sprites.*;
import entities.*;
import entityComposites.*;
import physics.Boundary;
import physics.BoundaryPolygonal;
import physics.Vector;
import entityComposites.EntityFactory;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import saving_loading.SavingLoading;
import engine.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
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
	
//	##### MODES #####
	private EditorSelectMode editorSelectMode;
	private EditorPlaceMode editorPlaceMode;
	private WorldGeometry worldGeomMode;
	private CameraPanEvent cameraPanEvent;
	private SpriteEditorMode spriteEditorMode;
	private BoundaryEditorMode boundaryEditorMode;
	private ModeAbstract editorMode;
	private InputController inputController;
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
	//private Robot automaticMouseReturn;
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

	public final Dimension minimizedSize = new Dimension(200,20);
	public final Dimension propPanelDefaultSize = new Dimension(215,125);
	public final Dimension allEntitiesComboBoxDefSize = new Dimension(120,20);
	protected int currentEntIndex;

	protected BoardAbstract board;
	protected MovingCamera camera;
	private Sprite ghostSprite; 

    //protected EntityStatic currentSelectedEntity;
    public Rectangle selectedBox;
    	
	protected ArrayList<PropertiesList> listOfPropLists;
    private String[] staticEntityStringArr;
	
// ###### COMPONENTS
	private JLabel mousePosLabel;
	private JLabel entityCoordsLabel;
	private JLabel selectedEntityNameLabel;
	protected JLabel tempSpriteName = new JLabel("");
	protected JLabel spriteHotSwapLabel = new JLabel();
	protected JComboBox<String> allEntitiesComboBox;
	
	protected JButton loadButton;
	protected JButton saveButton;
	protected JButton deleteEntButton;
	protected JButton worldGeomButton;
	protected JButton entitySelectButton;
	protected JRadioButton selectViaSpriteRB;
	protected JRadioButton selectViaBoundaryRB;
	protected JButton wgVertexPlaceModeButton;
	protected JButton wgVertexSelectModeButton;
	//protected JButton entidorModeButton;
	protected JButton spriteEditorButton;
	protected JButton boundaryEditorButton;
	protected JButton boundaryVertexSelectButton;
	protected JButton boundaryVertexPlaceButton;
	
//	Panels
	private JPanel entitiesComboBoxPanel;
	private JPanel labelsPanel;
	private JPanel buttonPanel;
	private JPanel propertyPanelTest;
	private JPanel iconBarForEntPlacement;
	private JPanel iconBarForSpriteSwap;
	
	JScrollPane iconBarScrollPaneEntPlacement;
	JScrollPane iconBarScrollPaneSpriteSwap;
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
		inputController = new InputController();
		inputController.createKeyBinding(KeyEvent.VK_F5, new KeyCommand() {
			@Override
			public void onPressed() { setMode(getEditorSelectMode());	}
			public void onReleased() {} public void onHeld() {}
		});
		inputController.createKeyBinding(KeyEvent.VK_F6, new KeyCommand() {
			@Override
			public void onPressed() { setMode(getWorldGeomMode());	}
			public void onReleased() {} public void onHeld() {}
		});
		inputController.createKeyBinding(KeyEvent.VK_F7, new KeyCommand() {
			@Override
			public void onPressed() { 
				if (editorMode == worldGeomMode) 
					getWorldGeomMode().setMode(worldGeomMode.getVertexSelectMode());
				else if (editorMode == boundaryEditorMode) 
					getBoundaryEditorMode().setMode(boundaryEditorMode.getVertexSelectMode());
			}
			public void onReleased() {} public void onHeld() {}
		});
		inputController.createKeyBinding(KeyEvent.VK_F8, new KeyCommand() {
			@Override
			public void onPressed() { 
				if (editorMode == worldGeomMode) 
					getWorldGeomMode().setMode(worldGeomMode.getVertexPlaceMode());
				else if (editorMode == boundaryEditorMode) 
					getBoundaryEditorMode().setMode(boundaryEditorMode.getVertexPlaceMode());
			}
			public void onReleased() {} public void onHeld() {}
		});
		inputController.createKeyBinding(KeyEvent.VK_S, new KeyCommand() {
			@Override
			public void onPressed() { getEditorSelectMode().setSelectViaSprite(true);
									  selectViaSpriteRB.setSelected(true);}
			public void onReleased() {} public void onHeld() {}
		});
		
		inputController.createKeyBinding(KeyEvent.VK_B, new KeyCommand() {
			@Override
			public void onPressed() { getEditorSelectMode().setSelectViaSprite(false);	
									  selectViaBoundaryRB.setSelected(true);}
			public void onReleased() {} public void onHeld() {}
		});
		
		inputController.createKeyBinding( KeyEvent.SHIFT_MASK , KeyEvent.VK_DELETE, new KeyCommand(){ //DELETE SELECTED
			@Override
			public void onPressed() {
			}
			@Override
			public void onReleased() {}
			@Override
			public void onHeld() {}
			
		});
		
		//##### INITIALIZING BUTTONS   ############
		saveButton = new JButton("Save");
		loadButton = new JButton("Load");
		deleteEntButton = new JButton("Delete");
		entitySelectButton = new JButton("Entity Select");
		worldGeomButton = new JButton("World Geom");
		wgVertexPlaceModeButton = new JButton("VtxPlace");
		wgVertexSelectModeButton = new JButton("VtxSelect");
		spriteEditorButton = new JButton("SpriteEditor");
		boundaryEditorButton = new JButton("BoundEditor");
		boundaryVertexSelectButton = new JButton("BoundVertSelect");
		boundaryVertexPlaceButton = new JButton("BoundVertPlace");
		newEntityPath = "";
		selectedBox = new Rectangle();
		editorMousePos = new Point();
		ghostSprite = null; 				//FIXME
        //clickPosition = new Point(0,0);
		
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
		
		
	    iconBarForEntPlacement = new JPanel(new FlowLayout(FlowLayout.LEADING));
	    iconBarScrollPaneEntPlacement = new JScrollPane(iconBarForEntPlacement);
	    IconLoader iconLoaderForEntPlacement = new IconLoader(this, iconBarForEntPlacement);
		
	    iconBarForSpriteSwap = new JPanel(new FlowLayout(FlowLayout.LEADING)); 
		iconBarScrollPaneSpriteSwap = new JScrollPane(iconBarForSpriteSwap);
	    SpriteIconLoader iconLoaderForSpriteSwap = new SpriteIconLoader(this, iconBarForSpriteSwap);
	
	    editorSelectMode = new EditorSelectMode();
		editorPlaceMode = new EditorPlaceMode();
		cameraPanEvent = new CameraPanEvent();
		worldGeomMode = new WorldGeometry(this, board2);
		boundaryEditorMode = new BoundaryEditorMode();
		spriteEditorMode = new SpriteEditorMode();
		
		this.editorMode = getEditorSelectMode();

		
		selectViaSpriteRB = new JRadioButton("S");
		selectViaSpriteRB.setFocusable(false);
		selectViaSpriteRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				editorSelectMode.setSelectViaSprite(true);
			}
		});
		selectViaBoundaryRB = new JRadioButton("B");
		selectViaBoundaryRB.setFocusable(false);
		selectViaBoundaryRB.setSelected(true);
		selectViaBoundaryRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editorSelectMode.setSelectViaSprite(false);
			}
		});
		ButtonGroup rbgroup = new ButtonGroup();
		rbgroup.add(selectViaSpriteRB);
		rbgroup.add(selectViaBoundaryRB);
		
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

		deleteEntButton.setFocusable(false);
		deleteEntButton.setEnabled(false);
		deleteEntButton.setMnemonic(KeyEvent.VK_DELETE);
		deleteEntButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {						
				//deleteEntity(allEntitiesComboBox.getSelectedIndex());
			} 		
		});


		entitySelectButton.setFocusable(false);
		entitySelectButton.setEnabled(true);
		entitySelectButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {						
				setMode(getEditorSelectMode());
				getEditorSelectMode().selectedEntities.clearSelectedEntities();
				
			} 		
		});
		

		worldGeomButton.setFocusable(false);
		worldGeomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setMode(getWorldGeomMode());
			}
		});

		wgVertexPlaceModeButton.setFocusable(false);
		wgVertexPlaceModeButton.setEnabled(false);
		wgVertexPlaceModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				worldGeomMode.setMode(worldGeomMode.getVertexPlaceMode());
			}
		});

		wgVertexSelectModeButton.setFocusable(false);
		wgVertexSelectModeButton.setEnabled(false);
		wgVertexSelectModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				worldGeomMode.setMode(worldGeomMode.getVertexSelectMode());
			}
		});

		spriteEditorButton.setFocusable(false);
		spriteEditorButton.setEnabled(false);
		spriteEditorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spriteEditorMode.setCurrentEntity(getEditorSelectMode().getSingleSelectedEntity());
				setMode(getSpriteEditorMode());
				
			}
		});
		/////////// THIS BUTTON NOT BEING USED ANYMORE
		boundaryEditorButton.setFocusable(false);
		boundaryEditorButton.setEnabled(false);
		boundaryEditorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boundaryEditorMode.setCurrentEntity(getEditorSelectMode().getSingleSelectedEntity());
				setMode(getBoundaryEditorMode());
			}
		}); ///////////////////////////////

		boundaryVertexSelectButton.setFocusable(false);
		boundaryVertexSelectButton.setEnabled(false);
		boundaryVertexSelectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boundaryEditorMode.setCurrentEntity(getEditorSelectMode().getSingleSelectedEntity());
				boundaryEditorMode.setMode(boundaryEditorMode.getVertexSelectMode());
				setMode(getBoundaryEditorMode());
			}
		});

		boundaryVertexPlaceButton.setFocusable(false);
		boundaryVertexPlaceButton.setEnabled(false);
		boundaryVertexPlaceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boundaryEditorMode.setCurrentEntity(getEditorSelectMode().getSingleSelectedEntity());
				boundaryEditorMode.setMode(boundaryEditorMode.getVertexPlaceMode());
				setMode(getBoundaryEditorMode());
			}
		});
		// inline panel for button
		JSeparator[] separators = new JSeparator[10];
		for (int i = 0; i < separators.length; i++) {
			separators[i] = new JSeparator(SwingConstants.HORIZONTAL);
			separators[i].setPreferredSize(new Dimension(150,3));
		}
		//separator.setPreferredSize(new Dimension(150,3));
	
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBackground(Color.GRAY);
	    buttonPanel.setBorder(BorderFactory.createTitledBorder("buttonPanelTest"));
		buttonPanel.setPreferredSize(new Dimension(190, 340));		
		buttonPanel.add(deleteEntButton);
		buttonPanel.add(separators[0]); //divider
		buttonPanel.add(entitySelectButton);
		buttonPanel.add(selectViaSpriteRB, FlowLayout.RIGHT);
		buttonPanel.add(selectViaBoundaryRB, FlowLayout.RIGHT);
		buttonPanel.add(separators[2]); //divider
		buttonPanel.add(worldGeomButton);
		buttonPanel.add(separators[1]); //divider
		buttonPanel.add(wgVertexSelectModeButton);
		buttonPanel.add(wgVertexPlaceModeButton);
		buttonPanel.add(separators[3]); //divider
		buttonPanel.add(spriteEditorButton);
		buttonPanel.add(separators[4]); //divider
		//buttonPanel.add(boundaryEditorButton);
		buttonPanel.add(boundaryVertexSelectButton);
		buttonPanel.add(boundaryVertexPlaceButton);
		

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
		/*propertyPanelTest = new JPanel();
		propertyPanelTest.setPreferredSize(minimizedSize);
		propertyPanelTest.setBackground(Color.GRAY);
		propertyPanelTest.setBorder(BorderFactory.createTitledBorder("propertyPanelTest")); */
	   
	    iconBarForEntPlacement.setBackground(Color.GRAY);
	    iconBarForEntPlacement.setPreferredSize(new Dimension(195,200));
	    iconBarForEntPlacement.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		iconBarScrollPaneEntPlacement.setVerticalScrollBarPolicy((JScrollPane.VERTICAL_SCROLLBAR_ALWAYS));

		iconBarForSpriteSwap.setBackground(Color.GRAY);
		iconBarForSpriteSwap.setPreferredSize(new Dimension(195,200));
		iconBarForSpriteSwap.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		iconBarScrollPaneSpriteSwap.setVerticalScrollBarPolicy((JScrollPane.VERTICAL_SCROLLBAR_ALWAYS));
		iconBarScrollPaneSpriteSwap.setVisible(true);
		// #### add everything to the editor
		add(entitiesComboBoxPanel);
		add(saveButton);
		add(loadButton);
		add(labelsPanel);
		add(buttonPanel);	
		//add(propertyPanelTest);
		add(new JLabel("        EntityPlacement         "));
		add(iconBarScrollPaneEntPlacement);
		spriteHotSwapLabel.setText("SpriteHotSwap: ");
		add(spriteHotSwapLabel);
		add(tempSpriteName);
		add(iconBarScrollPaneSpriteSwap);
		//iconBarScrollPaneSpriteSwap.setVisible(false);
		
	} // #### end of constructor #### #####################################################################################
	 // #### end of constructor #### #####################################################################################
	
	//Handler for the allEntitiesComboBox drop down panel
	// Out of date because I need to completely rework how this class handles multiple selections

	public class EntitiesComboBoxActionHandler implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setMode(getEditorSelectMode());
			//JComboBox cb = (JComboBox)e.getSource();
			//cb.getSelectedIndex());
			
			//restorePanels();    	#### IMPORTANT LINE, UNCOMMENT LATER
			
			//String testString = (String)allEntitiesComboBox.getSelectedItem();
			//System.out.println(testString);
			//allEntitiesComboBox.addItem
			currentEntIndex = allEntitiesComboBox.getSelectedIndex();
			System.out.println(currentEntIndex);
			try{					
				getEditorSelectMode().selectedEntities.clearSelectedEntities();
				deleteEntButton.setEnabled(true);
				
				//sets Board's current entity
				//setCurrentSelectedEntity(board.getEntities().get(currentEntIndex));
				editorSelectMode.addSelectedEntity(board.listCurrentSceneEntities()[currentEntIndex]);
				spriteEditorButton.setEnabled(true);
				boundaryEditorButton.setEnabled(true);
				boundaryVertexSelectButton.setEnabled(true);
				boundaryVertexPlaceButton.setEnabled(true);
				//createAndShowPropertiesPanel(board);   ##### IMPORTANT LINE, UNCOMMENT LATER
				
				setSelectedEntityNameLabel("Selected: " + board.listCurrentSceneEntities()[currentEntIndex].name);
				setEntityCoordsLabel(String.format("Coords of Selected Entity: %s,%s", editorSelectMode.selectedEntities.get(0).getX(), editorSelectMode.getSelectedEntities().get(0).getY()));
			}
			catch (NullPointerException exception){
				exception.printStackTrace();
				System.err.println("nullpointerexception"); 
			}
		}	
	} //end of EntitiesComboBoxActionHandler inner class
	
	// TODO
	// ########## MOUSE HANDLING SECTION ##############
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
		//setEditorMousePos(e.getX(), e.getY());
		this.editorMode.mouseMoved(e);
	}
	public void mouseReleased(MouseEvent e) {	
		setEditorMousePos(e.getX(), e.getY());
		this.editorMode.mouseReleased(e);

	}
	
	// ############ KEY HANDLING SECTION ###########
	public void keyPressed(KeyEvent e) {
		this.inputController.keyPressed(e);
		this.editorMode.keyPressed(e);
		this.inputController.debugHeld();
	}	
	public void keyReleased(KeyEvent e) {
		this.editorMode.keyReleased(e);
		
		this.inputController.keyReleased(e);
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
		g.setFont(new Font("default", Font.BOLD, 16));
		g.drawString(this.editorMode.getModeName(), 800, 30);
		g.setFont(new Font("default", Font.PLAIN, 12));
		//this.getGhostSprite().editorDraw(getEditorMousePos());
		
		this.inputController.debugPrintInputList(100, 100, g);
		
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
		worldGeomButton.setEnabled(true);
		iconBarScrollPaneSpriteSwap.setVisible(false);
		spriteEditorButton.setEnabled(false);
		wgVertexSelectModeButton.setEnabled(false);
		wgVertexPlaceModeButton.setEnabled(false);
		boundaryEditorButton.setEnabled(false);
		boundaryVertexPlaceButton.setEnabled(false);
		boundaryVertexSelectButton.setEnabled(false);
		return this.editorSelectMode;
	}
	public EditorPlaceMode getEditorPlaceMode() {
		iconBarScrollPaneSpriteSwap.setVisible(false);
		return this.editorPlaceMode;
	}
	public WorldGeometry getWorldGeomMode() {
		iconBarScrollPaneSpriteSwap.setVisible(false);
		spriteEditorButton.setEnabled(false);
		wgVertexPlaceModeButton.setEnabled(true);
		wgVertexSelectModeButton.setEnabled(true);
		
		boundaryEditorButton.setEnabled(false);
		boundaryVertexPlaceButton.setEnabled(false);
		boundaryVertexSelectButton.setEnabled(false);
		return this.worldGeomMode;
	}
	public CameraPanEvent getCameraPanMode() {
		return this.cameraPanEvent;
	}
	public SpriteEditorMode getSpriteEditorMode() {
		spriteEditorButton.setEnabled(true);
		iconBarScrollPaneSpriteSwap.setVisible(true);
		wgVertexPlaceModeButton.setEnabled(false);
		wgVertexSelectModeButton.setEnabled(false);
		boundaryEditorButton.setEnabled(false);
		boundaryVertexPlaceButton.setEnabled(false);
		boundaryVertexSelectButton.setEnabled(false);
		return this.spriteEditorMode;
	}
	public BoundaryEditorMode getBoundaryEditorMode() {
		iconBarScrollPaneSpriteSwap.setVisible(false);
		boundaryVertexPlaceButton.setEnabled(true);
		boundaryVertexSelectButton.setEnabled(true);
		wgVertexSelectModeButton.setEnabled(false);
		wgVertexPlaceModeButton.setEnabled(false);
		return this.boundaryEditorMode;
	}
	// ######################################### INNER CLASS MODES #######################################
	// ######################################### INNER CLASS MODES #######################################
	
/////////   INNER CLASS EDITORSELECTMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	public class EditorSelectMode extends ModeAbstract {
		protected SelectedEntities selectedEntities;
		protected SelectionRectangleAbstract selectionRectangle;
		protected SelectionRectangleAbstract nullSelectionRectangle;
		protected SelectionRectangleAbstract selectionRectangleState;
		
		protected ModeAbstract currentMode;
		protected DefaultMode defaultMode;
		protected RotateMode rotateMode;
		protected boolean oneEntitySelected;
		private boolean selectViaSprite;
		private Point initClickPoint;
		
		private double tempAngle;
		public EditorSelectMode() {
			tempAngle = 0.0;
			//mouseMovedKeyState = new MouseMovedKeyStateNull();
			selectedEntities = new SelectedEntities(camera);
			defaultMode = new DefaultMode();
			rotateMode = new RotateMode();
			currentMode = defaultMode;
			selectViaSprite = true;
			oneEntitySelected = false;
			initClickPoint = new Point();
			
			nullSelectionRectangle = SelectionRectangleNull.getNullSelectionRectangle();
			selectionRectangle = new SelectionRectangle(Color.BLUE, Color.cyan, camera, initClickPoint);
			selectionRectangleState = nullSelectionRectangle;
		}
		@Override
		public void mousePressed(MouseEvent e) {
			currentMode.inputController.mousePressed(e);
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			currentMode.inputController.mouseDragged(e);
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			//currentMode.mouseMovedKeyState.mouseMoved(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			currentMode.inputController.mouseReleased(e);
		}
		@Override
		public void keyPressed(KeyEvent e) {
			currentMode.inputController.keyPressed(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			currentMode.inputController.keyReleased(e); 
		}
		public void defaultRender(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(Color.BLUE);
			selectedEntities.drawClickableBox(g2, camera);
			selectionRectangleState.draw(g2, camera);
		}
		@Override
		public void render(Graphics g) {
			//defaultRender(g);
			currentMode.render(g);
		}
		@Override
		public String getModeName() {
			return this.currentMode.getModeName();
		}
		// ################ INNER CLASS EditorSelect--->DEFAULT MODE ############################
		public class DefaultMode extends ModeAbstract {
			public DefaultMode() {
				modeName = "EditorSelectMode";
				
				
				inputController = new InputController();
				this.inputController.createMouseBinding(MouseEvent.BUTTON1, new EntitySelectLClickEvent());
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON3, new CtrlEntitySelectLClickEvent());
				this.inputController.createMouseBinding(MouseEvent.BUTTON3, new TranslateEvent());
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON1, new SelectionRectEvent());
				this.inputController.createKeyBinding( KeyEvent.VK_ESCAPE, new DeselectEntitiesEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_R, new SetRotateMode());
				
				this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());
			}
			@Override
			public void mousePressed(MouseEvent e) {
				this.inputController.mousePressed(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				this.inputController.mouseDragged(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				this.inputController.mouseReleased(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				this.inputController.keyPressed(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				this.inputController.keyReleased(e);
			}

			@Override
			public void render(Graphics g) {
				defaultRender(g);
			}
		} // END OF DEFAULTMODE INNER CLASS  #####
		
		public class RotateMode extends ModeAbstract {
			//private boolean ctrlHeld;
			private Point origin;
			private Vector vector;
			//private MouseMovedKeyState defaultMouseMovedState;
			//private MouseMovedKeyStateNull mouseMovedKeyStateNull = new MouseMovedKeyStateNull();
			//private MouseMovedKeyState ctrlMouseMovedKeyState = new CtrlMouseMovedKeyState();
			public RotateMode() {
				this.modeName = "RotateMode";
				//ctrlHeld = false;
				inputController = new InputController();	
				this.inputController.createMouseBinding(MouseEvent.BUTTON1, new RotateEvent());
				//this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON1, new DegreeLockRotateEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_R, new SetDefaultMode());
				this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());
			}
			@Override
			public void mousePressed(MouseEvent e) {
				this.inputController.mousePressed(e);
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				this.inputController.mouseDragged(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				this.inputController.mouseReleased(e);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				this.inputController.keyPressed(e);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				this.inputController.keyReleased(e);
			}
			
			@Override
			public void render(Graphics g) {
				defaultRender(g);
			}
			// ######## INNER BEHAVIOR CLASSES #########
			public class RotateEvent implements MouseCommand {
				//the functionality for this will be very similar to how you designed the selection rectangle.
				/*
				 Steps: 
				 1) click, acknowledges origin and the clicked point
				 2) generate vector from these two points
				 3)	get angle from this vector
				 4) set rotation of selected entity by this angle
				 Considerations:
				 Everything should work except the blue shaded selection square around the entity. That won't rotate, will need to take care of that separately.
				 
				 
				 */
				@Override
				public void mousePressed() {
					// 
				}

				@Override
				public void mouseDragged() {
					
				}

				@Override
				public void mouseReleased() {
					
				}
			}
		} // END OF ROTATEMODE INNER CLASS  #####
		
		
		/**Option of whether select mode will choose based on entity's sprite or boundary
		 * @param choice - If true, select based on sprite. If false, select based on boundary
		 */
		public void setSelectViaSprite(boolean choice){
			this.selectViaSprite = choice;
		}
		public void checkForEntityCtrlClick(Point click) {
			if (selectViaSprite == true) {
				for(EntityStatic entity: board.listCurrentSceneEntities()) {
					if (entity.getGraphicComposite().exists()) {
						Rectangle clickableRect = new Rectangle();
						Sprite graphic = entity.getGraphicComposite().getSprite();
						//clickableRect.setLocation(entity.getXRelativeTo(camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(camera) + entity.getSpriteOffsetY());
						clickableRect.setLocation(entity.getX() + graphic.getOffsetX(), entity.getY() + graphic.getOffsetY());
						clickableRect.setSize(graphic.getImage().getWidth(null), graphic.getImage().getHeight(null) );
						if (clickableRect.contains(click)) {
							if (selectedEntities.contains(entity))
								selectedEntities.removeSelectedEntity(entity);
							else
								selectedEntities.addSelectedEntity(entity);
						}
					}
				}
			}
			else {
				for(EntityStatic entity: board.listCurrentSceneEntities()) {
					//polygonTest.
					//if (entity.getColliderComposite() instanceof ColliderNull ){
					if (entity.getColliderComposite().exists() ){
						Boundary bound = entity.getColliderComposite().getBoundaryLocal();
						int[] xpoints;
						int[] ypoints;
						xpoints = new int[bound.getCornersPoint().length];
						ypoints = new int[bound.getCornersPoint().length];
						
						for (int i = 0; i < bound.getCornersPoint().length; i++ ) {
							xpoints[i] = (int)bound.getCornersPoint()[i].getX();
							ypoints[i] = (int)bound.getCornersPoint()[i].getY();
						}
						Polygon polygonTest = new Polygon(xpoints, ypoints, bound.getCornersPoint().length);
						Rectangle rect = polygonTest.getBounds();
						if (polygonTest.contains(click)) {
							if (selectedEntities.contains(entity))
								selectedEntities.removeSelectedEntity(entity);
							else
								selectedEntities.addSelectedEntity(entity);
						}
					}
				}// end of for loop
			} //end of else
			if (selectedEntities.size() == 1) {
				spriteEditorButton.setEnabled(true);
				boundaryEditorButton.setEnabled(true);
				boundaryVertexSelectButton.setEnabled(true);
				boundaryVertexPlaceButton.setEnabled(true);
				
			}
			else {
				spriteEditorButton.setEnabled(false);
				boundaryEditorButton.setEnabled(false);
				boundaryVertexSelectButton.setEnabled(false);
				boundaryVertexPlaceButton.setEnabled(false);
			}
		}
		public void checkForEntity(Point click) {
			//boolean atLeastOneVertexFound = false;
			//since this is the regular click method, would want to make sure any selected entities are deselected first
			if (selectedEntities.size() > 0) {
				spriteEditorButton.setEnabled(false);
				boundaryEditorButton.setEnabled(false);
				boundaryVertexSelectButton.setEnabled(false);
				boundaryVertexPlaceButton.setEnabled(false);
				selectedEntities.clearSelectedEntities();
			}
			if (selectViaSprite == true) {
				for (EntityStatic entity: board.listCurrentSceneEntities()) {
					System.err.println("Current entity: " + entity);
					if (entity.getGraphicComposite().exists()) {
						Rectangle clickableRect = new Rectangle();
						//clickableRect.setLocation(entity.getXRelativeTo(camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(camera) + entity.getSpriteOffsetY());
						Sprite graphic = entity.getGraphicComposite().getSprite();
						clickableRect.setLocation(entity.getX() + graphic.getOffsetX(), entity.getY() + graphic.getOffsetY());
						clickableRect.setSize(graphic.getImage().getWidth(null), graphic.getImage().getHeight(null) );
						if (clickableRect.contains(click))
						{
							if (selectedEntities.contains(entity) == false) {
								selectedEntities.addSelectedEntity(entity);
								//FIXME this is just a test
								//selectedEntities.get(0).getRotationComposite().setAngleInDegrees(selectedEntities.get(0).getRotationComposite().getAngle()+45);
								spriteEditorButton.setEnabled(true);
								boundaryEditorButton.setEnabled(true);
								boundaryVertexSelectButton.setEnabled(true);
								boundaryVertexPlaceButton.setEnabled(true);
								break;
							}
						}
					}
				}// end of for loop
			}// end of first if
			else {
				for(EntityStatic entity: board.listCurrentSceneEntities()) {
					//polygonTest.
					if (entity.getColliderComposite().exists()) {
						Boundary bound = entity.getColliderComposite().getBoundaryLocal();
						int[] xpoints;
						int[] ypoints;
						xpoints = new int[bound.getCornersPoint().length];
						ypoints = new int[bound.getCornersPoint().length];
						
						for (int i = 0; i < bound.getCornersPoint().length; i++ ) {
							xpoints[i] = (int)bound.getCornersPoint()[i].getX();
							ypoints[i] = (int)bound.getCornersPoint()[i].getY();
						}
						Polygon polygonTest = new Polygon(xpoints, ypoints, bound.getCornersPoint().length);
						//Rectangle rect = polygonTest.getBounds();
						if (polygonTest.contains(click)) {
							if (selectedEntities.contains(entity) == false) {
								selectedEntities.addSelectedEntity(entity);
								spriteEditorButton.setEnabled(true);
								boundaryEditorButton.setEnabled(true);
								boundaryVertexSelectButton.setEnabled(true);
								boundaryVertexPlaceButton.setEnabled(true);
								break;
							}
						}
					}
				}// end of for loop
			} // end of else
		}
		public void checkForEntityInSelectionRect(Rectangle selectionRect) {
			if (selectViaSprite == true) {
				
				for (EntityStatic entity: board.listCurrentSceneEntities()) {
					Rectangle clickableRect = new Rectangle();
					Sprite graphic = entity.getGraphicComposite().getSprite();
					//clickableRect.setLocation(entity.getXRelativeTo(camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(camera) + entity.getSpriteOffsetY());
					clickableRect.setLocation(entity.getX() + graphic.getOffsetX(), entity.getY() + graphic.getOffsetY());
					clickableRect.setSize(graphic.getImage().getWidth(null), graphic.getImage().getHeight(null) );
					//if(selectionRect.contains(clickableRect)) {
					if(selectionRect.intersects(clickableRect)) {
						if(selectedEntities.contains(entity) == false) {
							selectedEntities.addSelectedEntity(entity);
						}
					}
				}
			}
			else {
				for(EntityStatic entity: board.listCurrentSceneEntities()) {
					//polygonTest.
					if (entity.getColliderComposite() instanceof ColliderNull ){
					}
					else {
						Boundary bound = entity.getColliderComposite().getBoundaryLocal();
						int[] xpoints;
						int[] ypoints;
						xpoints = new int[bound.getCornersPoint().length];
						ypoints = new int[bound.getCornersPoint().length];
						
						for (int i = 0; i < bound.getCornersPoint().length; i++ ) {
							xpoints[i] = (int)bound.getCornersPoint()[i].getX();
							ypoints[i] = (int)bound.getCornersPoint()[i].getY();
						}
						Polygon polygonTest = new Polygon(xpoints, ypoints, bound.getCornersPoint().length);
						Rectangle rect = polygonTest.getBounds();
						if (polygonTest.intersects(selectionRect)) {
							if (selectedEntities.contains(entity) == false) {
								selectedEntities.addSelectedEntity(entity);
							}
						}
					}
				}// end of for loop
			}
			if (selectedEntities.size() == 1) {
				//TODO return to this once synced with matt's new Sprite composite system
				spriteEditorButton.setEnabled(true);
				boundaryEditorButton.setEnabled(true);
				boundaryVertexSelectButton.setEnabled(true);
				boundaryVertexPlaceButton.setEnabled(true);
			}
			else {
				spriteEditorButton.setEnabled(false);
				boundaryEditorButton.setEnabled(false);
				boundaryVertexSelectButton.setEnabled(false);
				boundaryVertexPlaceButton.setEnabled(false);
			}
		}
		
		public void addSelectedEntity(EntityStatic entity) {
			selectedEntities.addSelectedEntity(entity);
		}
		@Deprecated
		/**
		 * @deprecated
		 * @return
		 */
		public EntityStatic getSingleSelectedEntity() {
			if (selectedEntities.size() == 1)
				return selectedEntities.get(0);
			else
				return EntityNull.getNullEntity();
		}
		public DefaultMode getDefaultMode() {
			return defaultMode;
		}
		public RotateMode getRotateMode(){
			return rotateMode;
		}
		public void setMode(ModeAbstract newMode) {
			this.currentMode = newMode;
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
				//selectedEntities.printSelectedEntities();
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
		public class DeselectEntitiesEvent implements KeyCommand {
			@Override
			public void onPressed() {
				selectedEntities.clearSelectedEntities();
				spriteEditorButton.setEnabled(false);
				boundaryEditorButton.setEnabled(false);
			}
			public void onReleased() {}
			public void onHeld() {}
		}
		public class SetRotateMode implements KeyCommand {
			@Override
			public void onPressed() {
				//if (selectedEntities.size() == 1) {
					if (true) {
					setMode(rotateMode);
					System.out.println("Was able to reach rotatemode");
				}
			}
			public void onReleased() {}
			public void onHeld() {}
		}
		public class SetDefaultMode implements KeyCommand {
			@Override
			public void onPressed() {
				setMode(defaultMode);
				System.out.println("was able to reach defaultmode");
			}
			public void onReleased() {}
			public void onHeld() {}
		}
	}  // end of EditorSelectMode inner class
	
/////////   INNER CLASS EDITORPLACEMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

	public class EditorPlaceMode extends ModeAbstract {
		public EditorPlaceMode() {
			modeName = "EditorPlaceMode";
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
			
		}
	}  // end of EditorPlaceMode inner class
	
	
	
	
/////////   INNER CLASS SPRITEEDITORMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

	public class SpriteEditorMode extends ModeAbstract {
		protected EntityStatic currentSelectedEntity;
		private ArrayList<EntityStatic> selectedEntitiesRef = getSelectedEntities();
		private String spritePath;
		
		protected ModeAbstract spriteEditorMode;
		//protected SpriteOffSetMode spriteOffsetMode;
		protected DefaultSpriteEditorMode defaultSpriteEditorMode;
		
		
		public SpriteEditorMode(){
			modeName = "SpriteEditorMode";
			spritePath = "";
			defaultSpriteEditorMode = new DefaultSpriteEditorMode();
			spriteEditorMode = defaultSpriteEditorMode;
			
		}
		public void setCurrentEntity(EntityStatic newEntity) {
			this.currentSelectedEntity = newEntity;
		}
		public EntityStatic getCurrentEntity(){
			return this.currentSelectedEntity;
		}
		@Override
		public void mousePressed(MouseEvent e) {
			this.spriteEditorMode.mousePressed(e);
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			this.spriteEditorMode.mouseDragged(e);
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			this.spriteEditorMode.mouseMoved(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			this.spriteEditorMode.mouseReleased(e);
		}
		@Override
		public void keyPressed(KeyEvent e) {
			this.spriteEditorMode.keyPressed(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			this.spriteEditorMode.keyReleased(e);
		}
		@Override
		public void render(Graphics g) {
			this.spriteEditorMode.render(g);
		}
		public void replaceAndFinalizeSprite(String path) {
/*			Boundary newBoundary = new Boundary(lines);
			this.currentSelectedEntity.getColliderComposite().setBoundary(newBoundary);
			getVertexSelectMode().selectedVertices.clearSelectedVertices();*/
			
			SpriteStillframe replacementSprite = new SpriteStillframe("SpriteHotSwap\\" + path);
//			replacementSprite.setOffset(replacementSprite.getBufferedImage().getWidth() - (replacementSprite.getBufferedImage().getWidth() / 2), 
//										replacementSprite.getBufferedImage().getHeight() - (replacementSprite.getBufferedImage().getHeight() / 2));
			//replacementSprite.setOffset(replacementSprite.getBufferedImage().getWidth() / 2, replacementSprite.getBufferedImage().getHeight() / 2);
			replacementSprite.setOffset(-(replacementSprite.getBufferedImage().getWidth() / 2), -(replacementSprite.getBufferedImage().getHeight() / 2));
			this.currentSelectedEntity.getGraphicComposite().setSprite(replacementSprite);
		}
		public void setSpritePath(String newPath) {
			this.spritePath = newPath;
		}
		public String getSpritePath() {
			return this.spritePath;
		}
		public class DefaultSpriteEditorMode extends ModeAbstract {
			Point initClick;
			//Point spriteInitialPosition;
			Point spriteOriginalOffset;
			
			public DefaultSpriteEditorMode() {
				initClick = new Point();
				//spriteInitialPosition = new Point();
				spriteOriginalOffset = new Point();
				inputController = new InputController();
				this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());			
				this.inputController.createMouseBinding(MouseEvent.BUTTON3, new TranslateOffsetEvent());			
				this.inputController.createKeyBinding(KeyEvent.VK_ENTER, new SwapSpriteEvent());			
				//this.inputController.createKeyBinding(KeyEvent.VK_O, new SetOffsetEvent());			
			
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
			public void mouseReleased(MouseEvent e) {
				
				inputController.mouseReleased(e); }
			@Override
			public void keyPressed(KeyEvent e) {
				this.inputController.keyPressed(e);
			}
			@Override
			public void keyReleased(KeyEvent e) {
				this.inputController.keyReleased(e);
			}
			@Override
			public void render(Graphics g) {
			}
			public class TranslateOffsetEvent implements MouseCommand {
				@Override
				public void mousePressed() {
				/*	spriteOriginalOffset.setLocation(currentSelectedEntity.getGraphicComposite().getSprite().getOffsetX(),
							 currentSelectedEntity.getGraphicComposite().getSprite().getOffsetX());*/
					initClick.setLocation(camera.getLocalPosition(editorMousePos)); // sets temporary old mouse position reference
					spriteOriginalOffset.setLocation(currentSelectedEntity.getGraphicComposite().getSprite().getOffsetPoint());;
				}

				@Override
				public void mouseDragged() {
					int mousePanDX = (initClick.x - editorMousePos.x);
					int mousePanDY = (initClick.y - editorMousePos.y);
					/*currentSelectedEntity.getGraphicComposite().getSprite().setOffset(
							(int)(spriteInitialPosition.x + mousePanDX),
							(int)(spriteInitialPosition.y + mousePanDY)
							);*/
					currentSelectedEntity.getGraphicComposite().getSprite().setOffset(
							(int)(camera.getLocalX(spriteOriginalOffset.x - mousePanDX)),
							(int)(camera.getLocalY(spriteOriginalOffset.y - mousePanDY))
							);
				}
				@Override
				public void mouseReleased() {}
			}
			public class SwapSpriteEvent implements KeyCommand {
				@Override
				public void onPressed() {
					replaceAndFinalizeSprite(spritePath);
				}
				@Override
				public void onReleased() {}
				@Override
				public void onHeld() {}
			}
			public class SetOffsetEvent implements KeyCommand {
				@Override
				public void onPressed() {
					int x = Integer.parseInt(JOptionPane.showInputDialog("Enter x offset"));
					int y = Integer.parseInt(JOptionPane.showInputDialog("Enter y offset"));
					currentSelectedEntity.getGraphicComposite().getSprite().setOffset(x, y);
				}
				@Override
				public void onReleased() {}
				@Override
				public void onHeld() {}
			}
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
		protected boolean isClosedShape;

		protected EntityStatic currentSelectedEntity;
		protected EntityNull nullEntity = EntityNull.getNullEntity();
		private ArrayList<EditorVertex> vertexList = new ArrayList<>();
		private ArrayList<EditorVertex> oldVertexListForReset = new ArrayList<>(vertexList);
		private ArrayList<Line2D.Double> surfaceLines = new ArrayList<>();
		private ArrayList<Line2D.Double> oldBoundary = new ArrayList<>();
		
		public BoundaryEditorMode() {
			modeName = "BoundaryEditorMode";
			isClosedShape = false;
			//this.currentSelectedEntity = currentEntityRef;
			boundaryVertexPlaceMode = new BoundaryVertexPlaceMode();
			boundaryVertexSelectMode = new BoundaryVertexSelectMode();
			boundaryMode = boundaryVertexSelectMode;
			ghostVertexPic = (BufferedImage)EditorVertex.createVertexPic(0.5f);
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
		public void defaultRender(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;			
			//old drawVertexPoints vvvvvvv
			for (EditorVertex editorVertex: vertexList) {
				editorVertex.draw(g2, camera);
			}
			// old drawsurfacelines vvvvvv
			g2.setColor(Color.DARK_GRAY);
			for (Line2D.Double lineToDraw: oldBoundary) {
				camera.draw(lineToDraw);
			}
			g2.setColor(Color.MAGENTA);
			/*for (int i = 0; i < vertexList.size()-1; i++) {
				Line2D.Double tempLine = new Line2D.Double(vertexList.get(i).getPoint(), vertexList.get(i+1).getPoint());
				camera.draw(tempLine);
			}*/
			for (Line2D.Double lineToDraw: surfaceLines) {
				camera.draw(lineToDraw);
			}
		}
		@Override
		public String getModeName() {
			return this.boundaryMode.getModeName();
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
		public void refreshAllSurfaceLines(ArrayList<Line2D.Double> lineArray) {
			lineArray.clear();
			for (int i = 0; i < vertexList.size()-1; i++) {
				Line2D.Double tempLine = new Line2D.Double(vertexList.get(i).getPoint(), vertexList.get(i+1).getPoint());
				lineArray.add(tempLine);
			}
		}
		public void refreshAllSurfaceLinesClosedShape(ArrayList<Line2D.Double> lineArray) {
			lineArray.clear();
			for (int i = 0; i < vertexList.size()-1; i++) {
				Line2D.Double tempLine = new Line2D.Double(vertexList.get(i).getPoint(), vertexList.get(i+1).getPoint());
				lineArray.add(tempLine);
			}
			closeShape(lineArray);
		}
		public void closeShape(ArrayList<Line2D.Double> lineArray) {
			/*if (lineArray.size() > 2) {
				if (lineArray.get(0).getP1() != lineArray.get(lineArray.size()-1).getP2()) {
					lineArray.add(new Line2D.Double(lineArray.get(lineArray.size()-1).getP2(),
							lineArray.get(0).getP1()));
					System.out.println("Was able to close shape");
				}
			}*/
			if (checkIfClosedShape(lineArray) == false) {  //is an open shape
				lineArray.add(new Line2D.Double(lineArray.get(lineArray.size()-1).getP2(),
						lineArray.get(0).getP1()));
			}
			/*if (lineArray == surfaceLines)
					isClosedShape = true;*/
			//isClosedShape = true;
			//}
		}
		public boolean checkIfClosedShape(ArrayList<Line2D.Double> lineArray) {
			if (lineArray.size() > 2) {
				if (lineArray.get(0).getP1() == lineArray.get(lineArray.size()-1).getP2()) {
					return true;
				}
			}
			return false;
			/*if (lineArray == surfaceLines)
					isClosedShape = true;*/
			//isClosedShape = true;
			//}
		}
		public boolean checkIfVerticesAreAdjacent(EditorVertex vert1, EditorVertex vert2) {
			int indexOfVert1 = vertexList.indexOf(vert1);
			if (indexOfVert1 != vertexList.size()-1) {
				if (vert2 == vertexList.get(indexOfVert1+1)) {
					return true;
				}
			}
			return false;
		}
		
		public void addVertex(int x, int y) {
			if (isClosedShape == false) {
				//vertexList.add(new Vertex(this.camera.getLocalX(x), this.camera.getLocalY(y)));
				vertexList.add(new EditorVertex(x,y));
				if (vertexList.size() > 1) {
					//updateSurfaceLinesUponChange(vertexList.size()-2);
					refreshAllSurfaceLines(surfaceLines);
					//isClosedShape = false;
				}
			}
		}
		public void removeVertex(SelectedVertices selectedVertices) {
			if (selectedVertices.size() == 1){
				if ( vertexList.size() > 3 ) {
					for (EditorVertex verts: vertexList) {
						if (verts == selectedVertices.getVertices().get(0)) {
							vertexList.remove(verts);
							selectedVertices.clearSelectedVertices();
							break;
						}
					}
					refreshAllSurfaceLines(surfaceLines);
					closeShape(surfaceLines);
				}
			}
		}
		public void clearAllVerticesAndLines() {
			vertexList.clear();
			surfaceLines.clear();
		}
		public void clearOldBoundary() {
			oldBoundary.clear();
		}
		
		public void setCurrentEntity(EntityStatic newEntity) {
			this.currentSelectedEntity = newEntity;
			//debugTestForVerticesPosition();
			retrieveVertsFromBoundary(currentSelectedEntity.getColliderComposite());
			setUpBackUpVerts();
			getBoundaryEditorMode().getVertexSelectMode().selectedVertices.clearSelectedVertices();
		}
		public EntityStatic getCurrentEntity(){
			return this.currentSelectedEntity;
		}
		public void resetBoundaryVerticesToDefault() {
			this.vertexList.clear();
			for (EditorVertex newVert: oldVertexListForReset)
				vertexList.add(new EditorVertex((int)newVert.getPoint().getX(), (int)newVert.getPoint().getY()));
			closeShape(surfaceLines);
			refreshAllSurfaceLines(surfaceLines);
			
			getVertexSelectMode().selectedVertices.clearSelectedVertices();
		}
		public void setUpBackUpVerts() {
			this.oldVertexListForReset.clear();
			for (EditorVertex newVert: vertexList) 
				oldVertexListForReset.add(new EditorVertex((int)newVert.getPoint().getX(), (int)newVert.getPoint().getY()));
		}
		public void retrieveVertsFromBoundary(Collider sourceCollider){
			clearAllVerticesAndLines();
			//might not need either of these two lines vvvvv
			//ArrayList<Point2D> temporaryPointsList = new ArrayList<>();
			//Point2D[] temporarayPointsArray = sourceCollider.getBoundary().getCornersPoint();
			for (Point2D vertexToAdd: sourceCollider.getBoundaryLocal().getCornersPoint()){
				vertexList.add(new EditorVertex( (int)vertexToAdd.getX(),(int)vertexToAdd.getY()) );
			}
			refreshAllSurfaceLines(surfaceLines);
			refreshAllSurfaceLines(oldBoundary);
			closeShape(surfaceLines);
			closeShape(oldBoundary);
			/*surfaceLines.get(surfaceLines.size()-1).setLine(surfaceLines.get(surfaceLines.size()-1).getP2(),
																			 surfaceLines.get(0).getP1());*/
		}
		/*public void debugTestForVerticesPosition() {
			//going to try to duplicate the coordinates of the big slope testentity
			this.vertexList.clear();
			new Line2D.Double( -25 , -50 , 2000 , 500 ),
			new Line2D.Double( 2000 , 500 , -25 , 500 ),
			new Line2D.Double( -25 , 500 , -25 , -50 )
			
			vertexList.add(new EditorVertex(-25,-50));
			vertexList.add(new EditorVertex(2000,500));
			vertexList.add(new EditorVertex(275,500));
			refreshAllSurfaceLines(surfaceLines);
			refreshAllSurfaceLines(oldBoundary);
			closeShape(surfaceLines);
			closeShape(oldBoundary);
			
		}*/
		public void retrieveVertsFromRect(Rectangle rect) {
			this.vertexList.clear();
			Point2D p1 = new Point2D.Double(rect.getMinX(), rect.getMinY());
			Point2D p2 = new Point2D.Double(rect.getMaxX(), rect.getMinY());
			Point2D p3 = new Point2D.Double(rect.getMaxX(), rect.getMaxY());
			Point2D p4 = new Point2D.Double(rect.getMinX(), rect.getMaxY());
			/*vertexList.add(new EditorVertex((int)camera.getLocalX(p1.getX()), (int)camera.getLocalY(p1.getY())) );
			vertexList.add(new EditorVertex((int)camera.getLocalX(p2.getX()), (int)camera.getLocalY(p2.getY())) );
			vertexList.add(new EditorVertex((int)camera.getLocalX(p3.getX()), (int)camera.getLocalY(p3.getY())) );
			vertexList.add(new EditorVertex((int)camera.getLocalX(p4.getX()), (int)camera.getLocalY(p4.getY())) );*/
			vertexList.add(new EditorVertex((int)p1.getX(), (int)p1.getY()) );
			vertexList.add(new EditorVertex((int)p2.getX(), (int)p2.getY()) );
			vertexList.add(new EditorVertex((int)p3.getX(), (int)p3.getY()) );
			vertexList.add(new EditorVertex((int)p4.getX(), (int)p4.getY()) );
			refreshAllSurfaceLines(surfaceLines);
			refreshAllSurfaceLines(oldBoundary);
			closeShape(surfaceLines);
			closeShape(oldBoundary);
		}
		public void replaceAndFinalizeBoundary() {
			if (surfaceLines.size() > 0) {
				refreshAllSurfaceLines(surfaceLines);
				closeShape(surfaceLines);
				Line2D[] lines = new Line2D[surfaceLines.size()];
				for (int i = 0; i < surfaceLines.size(); i++) {
//					int offsetX = (int)surfaceLines.get(i).getX1() - currentSelectedEntity.getX();
//					int offsetY = (int)surfaceLines.get(i).getY1() - currentSelectedEntity.getY();
					lines[i] = surfaceLines.get(i);
					lines[i].setLine(surfaceLines.get(i).getX1()-currentSelectedEntity.getX(),
									 surfaceLines.get(i).getY1()-currentSelectedEntity.getY(),
									 surfaceLines.get(i).getX2()-currentSelectedEntity.getX(),
									 surfaceLines.get(i).getY2()-currentSelectedEntity.getY());
				}
				
				//lines[lines.length-1].setLine( lines[lines.length-1].getP1() , lines[0].getP1() );
				//Boundary newBoundary = new Boundary(lines, currentSelectedEntity.getColliderComposite());
				Boundary newBoundary = new BoundaryPolygonal(lines);
				//this.currentSelectedEntity.getColliderComposite().setBoundary(newBoundary);
				CompositeFactory.addColliderTo( this.currentSelectedEntity , newBoundary);
				clearAllVerticesAndLines();
				clearOldBoundary();
				isClosedShape = false;
				getVertexSelectMode().selectedVertices.clearSelectedVertices();
			}
		}
		// ############ INNER CLASS BOUNDARY VERTEX SELECT MODE ############
		public class BoundaryVertexSelectMode extends ModeAbstract {
			protected SelectedVertices selectedVertices;
			protected SelectionRectangleAbstract selectionRectangle;
			protected SelectionRectangleAbstract selectionRectangleState;
			protected SelectionRectangleAbstract nullSelectionRectangle;
			protected Point initClickPoint;
			
			//constructor
			public BoundaryVertexSelectMode() {
				modeName = "BoundaryVertexSelectMode";
				initClickPoint = new Point();
				selectedVertices = new SelectedVertices(camera);
				nullSelectionRectangle = SelectionRectangleNull.getNullSelectionRectangle();
				selectionRectangle = new SelectionRectangle(Color.BLUE, Color.cyan, camera, initClickPoint);
				selectionRectangleState = nullSelectionRectangle;
				
				inputController = new InputController();
				this.inputController.createMouseBinding(MouseEvent.BUTTON1, new VertexSelectLClickEvent());
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON3, new CtrlVertexSelectLClickEvent());
				this.inputController.createMouseBinding(MouseEvent.BUTTON3, new TranslateEvent());
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON1, new SelectionRectEvent());
				this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());
				//this.inputController.createKeyBinding(KeyEvent.VK_N, new RetrieveVertsFromBoundaryEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_ENTER, new ReplaceAndFinalizeBoundaryEvent());
				this.inputController.createKeyBinding(KeyEvent.CTRL_MASK, KeyEvent.VK_Z, new ResetBoundaryVerticesToDefaultEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_DELETE, new DeleteVerticesEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_C, new CloseShapeEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_X, new AlignToXAxisEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_Y, new AlignToYAxisEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_SLASH, new SplitLineEvent());
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
			public void mouseReleased(MouseEvent e) {
				
				inputController.mouseReleased(e); }
			@Override
			public void keyPressed(KeyEvent e) { inputController.keyPressed(e);	}
			@Override
			public void keyReleased(KeyEvent e) {inputController.keyReleased(e); }

			@Override
			public void render(Graphics g) {
				Graphics2D g2 = (Graphics2D)g;	
				defaultRender(g2);
				// section to draw selected Vertex (if one is selected)
				g2.setColor(Color.GREEN);
				selectedVertices.drawClickableBox(g2, camera);
				g2.setColor(Color.BLUE);
				// vvvv section to draw selection rectangle
				selectionRectangleState.draw(g2, camera);
			}
			public void checkForVertexShiftClick(Point click) {
				for (EditorVertex editorVertex: vertexList) {
					if (editorVertex.getClickableZone().contains(click)) {
						if (selectedVertices.contains(editorVertex)) 
							selectedVertices.removeSelectedVertex(editorVertex);
						else
							selectedVertices.addSelectedVertex(editorVertex);
					}
				}
			}
			public void checkForVertex(Point click) {
				//boolean atLeastOneVertexFound = false;
				//since this is the regular click method, would want to make sure any selected vertices are deselected first
				//TODO: DON'T USE THIS METHOD WITH THE SELECTION BOX PROCEDURE
				if (selectedVertices.size() > 0)
					selectedVertices.clearSelectedVertices();

				for (EditorVertex editorVertex: vertexList) {
					if (editorVertex.getClickableZone().contains(click)) 
					{
						if (selectedVertices.contains(editorVertex) == false) {
							//atLeastOneVertexFound = true;
							selectedVertices.addSelectedVertex(editorVertex);
							break;
						}
					}
				}
				/*if (atLeastOneVertexFound == false)
					selectedVertices.clearSelectedVertices();*/
			}
			public void checkForVertexInSelectionRect(Rectangle selectionRect) {
				for (EditorVertex editorVertex: vertexList) {
					if (selectionRect.intersects(editorVertex.getClickableZone())){
						if(selectedVertices.contains(editorVertex) == false) 
							selectedVertices.addSelectedVertex(editorVertex);
					}
				}
			}
			public void splitLine() {
				if (selectedVertices.size() == 2) {
					if ( checkIfVerticesAreAdjacent(selectedVertices.getVertices().get(0), selectedVertices.getVertices().get(1)) ){
						int x1 = selectedVertices.getVertices().get(0).getPoint().x;
						int x2 = selectedVertices.getVertices().get(1).getPoint().x;
						int y1 = selectedVertices.getVertices().get(0).getPoint().y;
						int y2 = selectedVertices.getVertices().get(1).getPoint().y;
						int averageX = (x1 + x2) / 2;
						int averageY = (y1 + y2) / 2;
						vertexList.add(vertexList.indexOf(selectedVertices.getVertices().get(1) ),
										new EditorVertex(averageX,averageY));
						refreshAllSurfaceLinesClosedShape(surfaceLines);
						selectedVertices.clearSelectedVertices();
					}
				}
			}
// ****************** inner-inner classes for mouse behavior classes specific to vertex selecting
// ****************** inner-inner classes for mouse behavior classes specific to vertex selecting
			
			public class VertexSelectLClickEvent implements MouseCommand{
				public void mousePressed() {
					
					checkForVertex(camera.getLocalPosition(editorMousePos));
				}
				public void mouseDragged() {
					//currentSelectedVertex.translate(camera.getLocalPosition(editorMousePos));
				}
				public void mouseReleased() {}	
			} // end of VertexSelectLClickEvent inner class
			public class VertexSelectRClickEvent implements MouseCommand{
				public void mousePressed() {
					
					//checkForVertex(camera.getLocalPosition(e.getPoint()));
					//checkForVertex(camera.getLocalPosition(editorMousePos));
				}
				public void mouseDragged() {
					
					//currentSelectedVertex.translate(camera.getLocalPosition(editorMousePos));
				}
				public void mouseReleased() {
					
				}	
			} // end of VertexSelectRClickEvent inner class
			public class CtrlVertexSelectLClickEvent implements MouseCommand{

				public void mousePressed() {
					
					checkForVertexShiftClick(camera.getLocalPosition(editorMousePos));
				}
				public void mouseDragged() {
					
					//currentSelectedVertex.translate(camera.getLocalPosition(editorMousePos));
				}
				public void mouseReleased() {
					
				}

			} // end of ShiftVertexSelectLClickEvent inner class
			public class TranslateEvent implements MouseCommand{

				public void mousePressed() {
					
					initClickPoint.setLocation(camera.getLocalPosition(editorMousePos));
					selectedVertices.updateOldVertexPositions();
				}
				public void mouseDragged() {
					selectedVertices.translate(initClickPoint, editorMousePos);
					/*if (isClosedShape)
						refreshAllSurfaceLinesClosedShape(surfaceLines);
					else
						refreshAllSurfaceLines(surfaceLines);*/
					refreshAllSurfaceLines(surfaceLines);
					closeShape(surfaceLines);
				}
				public void mouseReleased() {
					
				}

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
					checkForVertexInSelectionRect(selectionRectangleState.getWrekt());
					selectionRectangleState.resetRect();
					selectionRectangleState = nullSelectionRectangle;
				}

			}
			public class EscapeEvent implements KeyCommand {
				@Override
				public void onPressed() {
					selectedVertices.clearSelectedVertices();
				}
				public void onReleased(){} public void onHeld() {}
			}
			public class SplitLineEvent implements KeyCommand {
				public void onPressed() {
					splitLine();
				}
				public void onReleased() {} public void onHeld() {}
			}
			/*
			public class RetrieveVertsFromBoundaryEvent implements KeyCommand {
				@Override
				public void onPressed() {
					retrieveVertsFromBoundary(currentSelectedEntity.getColliderComposite());
				}
				public void onReleased(){} public void onHeld() {}
			}*/
			public class AlignToXAxisEvent implements KeyCommand {
				@Override
				public void onPressed() {
					selectedVertices.alignToXAxis();
					refreshAllSurfaceLinesClosedShape(surfaceLines);
				}
				public void onReleased(){} public void onHeld() {}
			}
			public class AlignToYAxisEvent implements KeyCommand {
				@Override
				public void onPressed() {
					selectedVertices.alignToYAxis();
					refreshAllSurfaceLinesClosedShape(surfaceLines);
				}
				public void onReleased(){} public void onHeld() {}
			}
			public class DeleteVerticesEvent implements KeyCommand {
				@Override
				public void onPressed() {
					
					removeVertex(selectedVertices);
				}
				public void onReleased() {} public void onHeld() {}
			}
			public class CloseShapeEvent implements KeyCommand {
				@Override
				public void onPressed() {
					
					refreshAllSurfaceLines(surfaceLines);
					closeShape(surfaceLines);
				}
				public void onReleased() {} public void onHeld() {}
			}
			public class ReplaceAndFinalizeBoundaryEvent implements KeyCommand {
				@Override
				public void onPressed() {
					
					replaceAndFinalizeBoundary();
				}
				public void onReleased() {} public void onHeld() {}
			}
			public class ResetBoundaryVerticesToDefaultEvent implements KeyCommand {
				@Override
				public void onPressed() {
					
					resetBoundaryVerticesToDefault();
				}
				public void onReleased() {} public void onHeld() {}
			}
		} // end of boundaryVertexSelectMode
/////////   INNER CLASS BOUNDARYVERTEXPLACEMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
		public class BoundaryVertexPlaceMode extends ModeAbstract {
			protected SelectionRectangleAbstract tempRectBoundary;
			protected SelectionRectangleAbstract tempRectBoundaryState;
			protected SelectionRectangleAbstract nullTempRectBoundary;
			protected Point initClickPoint;
			
			public BoundaryVertexPlaceMode() {
				modeName = "BoundaryVertexPlaceMode";
				initClickPoint = new Point();
				nullTempRectBoundary = SelectionRectangleNull.getNullSelectionRectangle();
				tempRectBoundary = new SelectionRectangle(Color.BLUE, Color.cyan, camera, initClickPoint);
				tempRectBoundaryState = nullTempRectBoundary;
				inputController = new InputController();
				this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());			
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON1, new RectangleBoundDrawEvent());
			}
			public void mousePressed(MouseEvent e) {
				inputController.mousePressed(e);
			}
			public void mouseDragged(MouseEvent e) {
				inputController.mouseDragged(e);
			}
			public void mouseMoved(MouseEvent e) {}
			
			public void mouseReleased(MouseEvent e) {
				inputController.mouseReleased(e);}
			@Override
			public void keyPressed(KeyEvent e) { 
				inputController.keyPressed(e);	}
			@Override
			public void keyReleased(KeyEvent e) {
				inputController.keyReleased(e); }
			
			@Override
			public void render(Graphics g) {
				Graphics2D g2 = (Graphics2D)g;
				defaultRender(g);
				g2.setColor(Color.BLUE);
				// vvvv section to draw selection rectangle
				tempRectBoundaryState.draw(g2, camera);
			}
			/////// INNER BEHAVIOR CLASSES
			public class RectangleBoundDrawEvent implements MouseCommand {

				@Override
				public void mousePressed() {
					
					tempRectBoundaryState = tempRectBoundary;
					initClickPoint.setLocation(camera.getLocalPosition(editorMousePos));
					tempRectBoundaryState.setInitialRectPoint();
				}

				@Override
				public void mouseDragged() {
					
					tempRectBoundaryState.translateEndPoint(camera.getLocalPosition(editorMousePos));
				}

				@Override
				public void mouseReleased() {
					
					//command to select vertices underneath box
					retrieveVertsFromRect(tempRectBoundary.getWrekt());
					tempRectBoundaryState.resetRect();
					tempRectBoundaryState = nullTempRectBoundary;
					setMode(getVertexSelectMode());
				}

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
				/*mousePanDX = editorMousePos.getX() - oldMousePanPos.getX();
				mousePanDY = editorMousePos.getY() - oldMousePanPos.getY();*/
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
