package editing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import Input.InputController;
import Input.KeyCommand;
import Input.MouseCommand;
import editing.worldGeom.EditorVertex;
import editing.worldGeom.SelectedVertices;
import editing.worldGeom.SelectionRectangle;
import editing.worldGeom.SelectionRectangleAbstract;
import editing.worldGeom.SelectionRectangleNull;
import editing.worldGeom.WorldGeometry;
import engine.*;
import entities.EntityDynamic;
import entities.EntityNull;
import entityComposites.Collider;
import entityComposites.CompositeFactory;
import entityComposites.Entity;
import entityComposites.EntityBehaviorScript;
import entityComposites.EntityComposite;
import entityComposites.EntityFactory;
import entityComposites.EntityStatic;
import entityComposites.TranslationComposite;
import physics.*;
import saving_loading.SavingLoading;
import sprites.Sprite;
import sprites.Sprite.Stillframe;
import testEntities.Asteroid;

//TASK LIST:
@SuppressWarnings("serial")
/**
 * @author Dave 
 */
public class EditorPanel extends JPanel implements MouseWheelListener{
	
//	##### MODES #####
	private static int SelectInstanceCount;
	private EntitySelectMode entitySelectMode;
	private EntityPlaceMode entityPlaceMode;
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
	//private JLabel mousePosLabel;
	private JLabel entityCoordsLabel;
	private JLabel selectedEntityNameLabel;
	//private JTree tree;
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
	protected JButton boundaryVertexSelectButton;
	protected JButton boundaryVertexPlaceButton;
	
//	Panels
	//private JPanel entitiesComboBoxPanel;
	//private JPanel treePanel;
	private BrowserTreePanel browserTreePanel;
	private CompositeEditorPanel compositeEditorPanel;
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
		/*try {
			this.camera = board.getCamera();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}*/
		this.camera = board.getCamera();
		oldMousePanPos = new Point();
		oldCameraPos = new Point();
		inputController = new InputController("Main panel controller");
		inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON3, new OpenNewEntityPopup());
		inputController.createKeyBinding(KeyEvent.VK_F5, new KeyCommand() {
			@Override
			public void onPressed() { setMode(getEntitySelectMode());	}
		});
		inputController.createKeyBinding(KeyEvent.VK_F6, new KeyCommand() {
			@Override
			public void onPressed() { setMode(getWorldGeomMode());	}
		});
		inputController.createKeyBinding(KeyEvent.VK_F7, new KeyCommand() {
			@Override
			public void onPressed() { 
				if (editorMode == worldGeomMode) 
					getWorldGeomMode().setMode(worldGeomMode.getVertexSelectMode());
				else if (editorMode == boundaryEditorMode) 
					getBoundaryEditorMode().setSubMode(boundaryEditorMode.getVertexSelectMode());
			}
		});
		inputController.createKeyBinding(KeyEvent.VK_F8, new KeyCommand() {
			@Override
			public void onPressed() { 
				/*debug section, line below */
				//refreshTree();
				if (editorMode == worldGeomMode) 
					getWorldGeomMode().setMode(worldGeomMode.getVertexPlaceMode());
				else if (editorMode == boundaryEditorMode) 
					getBoundaryEditorMode().setSubMode(boundaryEditorMode.getVertexPlaceMode());
			}
		});
		/*inputController.createKeyBinding(KeyEvent.VK_S, new KeyCommand() {
			@Override
			public void onPressed() { getEntitySelectMode().setSelectViaSprite(true);
									  selectViaSpriteRB.setSelected(true);}
			public void onReleased() {} public void onHeld() {}
		});*/

		inputController.createKeyBinding(KeyEvent.VK_B, new KeyCommand() {
			@Override
			public void onPressed() { 
				getEntitySelectMode().setSelectViaSprite(false);	
				selectViaBoundaryRB.setSelected(true);
			}
		});
		inputController.createKeyBinding( KeyEvent.SHIFT_MASK , KeyEvent.VK_DELETE, new KeyCommand(){ //DELETE SELECTED
		});
		
		inputController.createKeyBinding( KeyEvent.VK_ADD, new CameraZoomInEvent() );
		inputController.createKeyBinding( KeyEvent.VK_SUBTRACT, new CameraZoomOutEvent() );
		inputController.createKeyBinding( KeyEvent.VK_MULTIPLY, new CameraResetZoom() );
		
		//##### INITIALIZING BUTTONS   ############
		saveButton = new JButton("Save");
		loadButton = new JButton("Load");
		deleteEntButton = new JButton("Delete");
		entitySelectButton = new JButton("Entity Select");
		worldGeomButton = new JButton("World Geom");
		wgVertexPlaceModeButton = new JButton("VtxPlace");
		wgVertexSelectModeButton = new JButton("VtxSelect");
		spriteEditorButton = new JButton("SpriteEditor");
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
		
		//mousePosLabel = new JLabel("Mouse Click: ");
		entityCoordsLabel = new JLabel("Coords of selected entity: ");
		selectedEntityNameLabel = new JLabel("Nothing Selected");
		
		
	    iconBarForEntPlacement = new JPanel(new FlowLayout(FlowLayout.LEADING));
	    iconBarScrollPaneEntPlacement = new JScrollPane(iconBarForEntPlacement);
	    IconLoader iconLoaderForEntPlacement = new IconLoader(this, iconBarForEntPlacement);
		
	    iconBarForSpriteSwap = new JPanel(new FlowLayout(FlowLayout.LEADING)); 
		iconBarScrollPaneSpriteSwap = new JScrollPane(iconBarForSpriteSwap);
	    SpriteIconLoader iconLoaderForSpriteSwap = new SpriteIconLoader(this, iconBarForSpriteSwap);
	
	    entitySelectMode = new EntitySelectMode();
		entityPlaceMode = new EntityPlaceMode();
		cameraPanEvent = new CameraPanEvent();
		worldGeomMode = new WorldGeometry(this, board2);
		boundaryEditorMode = new BoundaryEditorMode();
		spriteEditorMode = new SpriteEditorMode();
		
		this.editorMode = getEntitySelectMode();

		
		selectViaSpriteRB = new JRadioButton("S");
		selectViaSpriteRB.setFocusable(false);
		selectViaSpriteRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				entitySelectMode.setSelectViaSprite(true);
			}
		});
		selectViaBoundaryRB = new JRadioButton("B");
		selectViaBoundaryRB.setFocusable(false);
		selectViaBoundaryRB.setSelected(true);
		selectViaBoundaryRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				entitySelectMode.setSelectViaSprite(false);
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
				setMode(getEntitySelectMode());
				getEntitySelectMode().selectedEntities.clearSelectedEntities();
				
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
				spriteEditorMode.setCurrentEntity(getEntitySelectMode().getSingleSelectedEntity());
				setMode(getSpriteEditorMode());
			}
		});
		boundaryVertexSelectButton.setFocusable(false);
		boundaryVertexSelectButton.setEnabled(false);
		boundaryVertexSelectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boundaryEditorMode.setCurrentEntity(getEntitySelectMode().getSingleSelectedEntity());
				boundaryEditorMode.setSubMode(boundaryEditorMode.getVertexSelectMode());
				boundaryEditorMode.getVertexSelectMode().setBoundarySubMode(boundaryEditorMode.getVertexSelectMode().getDefaultMode());
				setMode(getBoundaryEditorMode());
			}
		});
		boundaryVertexPlaceButton.setFocusable(false);
		boundaryVertexPlaceButton.setEnabled(false);
		boundaryVertexPlaceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boundaryEditorMode.setCurrentEntity(getEntitySelectMode().getSingleSelectedEntity());
				boundaryEditorMode.setSubMode(boundaryEditorMode.getVertexPlaceMode());
				setMode(getBoundaryEditorMode());
			}
		});
		// inline panel for button
		JSeparator[] separators = new JSeparator[10];
		for (int i = 0; i < separators.length; i++) {
			separators[i] = new JSeparator(SwingConstants.HORIZONTAL);
			separators[i].setPreferredSize(new Dimension(150,3));
		}
		/*try {            /// THIS WASN'T REALLY WORKING SUPER WELL
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }*/
		//separator.setPreferredSize(new Dimension(150,3));
		entitySelectMode.setSelectViaSprite(false);
		
		compositeEditorPanel = new CompositeEditorPanel();
		JScrollPane compositeEditorScrollPane = new JScrollPane( compositeEditorPanel  );
		compositeEditorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		compositeEditorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		compositeEditorScrollPane.setPreferredSize(new Dimension(220, 200));
		
		JToolBar filterToolBar = new JToolBar(JToolBar.HORIZONTAL); 
		
		browserTreePanel = new BrowserTreePanel(new BorderLayout(),this, board, compositeEditorPanel, filterToolBar);
//		browserTreePanel = new BrowserTreePanel(new FlowLayout(FlowLayout.LEFT),this,board);
		JScrollPane treeScrollPane = new JScrollPane(browserTreePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScrollPane.setFocusable(false);
		treeScrollPane.setPreferredSize(new Dimension(220,200));
		treeScrollPane.getVerticalScrollBar().setUnitIncrement(50);
		treeScrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		
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
		buttonPanel.add(boundaryVertexSelectButton);
		buttonPanel.add(boundaryVertexPlaceButton);
		

		// ## The drop down box for the list of all entities in board ###	
		/*allEntitiesComboBox = new JComboBox<>(staticEntityStringArr);
		allEntitiesComboBox.setPreferredSize(allEntitiesComboBoxDefSize);
		allEntitiesComboBox.setFocusable(false);
		//allEntitiesComboBox.setSelectedIndex(0); //give it a default value
		
		allEntitiesComboBox.addActionListener(new EntitiesComboBoxActionHandler());*/
		

		// Panel to contain allEntitiesComboBox drop down panel
		/*entitiesComboBoxPanel = new JPanel(new BorderLayout());
		entitiesComboBoxPanel.setBackground(Color.GRAY);
		entitiesComboBoxPanel.setPreferredSize(allEntitiesComboBox.getPreferredSize());
		entitiesComboBoxPanel.add(allEntitiesComboBox);*/
		
		// ###### adding the components to the Editor window		
		//inline panel for text messages
		labelsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelsPanel.setPreferredSize(new Dimension(215, 50));
		labelsPanel.setBackground(Color.GRAY);
		labelsPanel.setBorder(BorderFactory.createEtchedBorder());
		//labelsPanel.add(mousePosLabel);
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
		//iconBarScrollPaneSpriteSwap.setVisible(true);
		iconBarScrollPaneSpriteSwap.setVerticalScrollBarPolicy((JScrollPane.VERTICAL_SCROLLBAR_ALWAYS));
		
		
		// #### add everything to the editor's scroll pane
		//add(entitiesComboBoxPanel);
		add(saveButton);
		add(loadButton);
		add(labelsPanel);
		add(filterToolBar);
		add(treeScrollPane);
		add(compositeEditorScrollPane);
		add(buttonPanel);	
		//add(propertyPanelTest);
		add(new JLabel("        EntityPlacement         "));
		add(iconBarScrollPaneEntPlacement);
		spriteHotSwapLabel.setText("SpriteHotSwap: ");
		add(spriteHotSwapLabel);
		add(tempSpriteName);
		add(iconBarScrollPaneSpriteSwap);
		//iconBarScrollPaneSpriteSwap.setVisible(false);
		
	} // #### end of CONSTRUCTOR #### #####################################################################################
	 // #### end of CONSTRUCTOR #### #####################################################################################
	
	//Handler for the allEntitiesComboBox drop down panel
	// Out of date because I need to completely rework how this class handles multiple selections
	
	/** Takes care of GUI events that I want to happen when an entity is clicked.*/
	public void selectSingleEntityGUIHouseKeeping() {
		//TODO IMPORTANT make a "modeSwitchCleanUp()" method that is called every time a mode is switched, to avoid data leaks
		//editorMode.modeSwitchCleanUp();   //Might not be necessary though. States can probably just be stored in the mode's instance
		setMode(getEntitySelectMode());
		entitySelectMode.selectedEntities.clearSelectedEntities();
		deleteEntButton.setEnabled(true);
		spriteEditorButton.setEnabled(true);
		boundaryVertexSelectButton.setEnabled(true);
		boundaryVertexPlaceButton.setEnabled(true);
	}
	/*public class EntitiesComboBoxActionHandler implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setMode(getEntitySelectMode());
			//JComboBox cb = (JComboBox)e.getSource();
			//cb.getSelectedIndex());
			//restorePanels();    	#### IMPORTANT LINE, UNCOMMENT LATER
			currentEntIndex = allEntitiesComboBox.getSelectedIndex();
			try{					
				selectSingleEntityGUIHouseKeeping();
				//sets Board's current entity
				entitySelectMode.addSelectedEntity(board.listCurrentSceneEntities()[currentEntIndex]);
				//createAndShowPropertiesPanel(board);   ##### IMPORTANT LINE, UNCOMMENT LATER

				setEntityCoordsLabel(String.format("Coords of Selected Entity: %s,%s", entitySelectMode.getSingleSelectedEntity().getX(), entitySelectMode.getSingleSelectedEntity().getY()));
			}
			catch (NullPointerException exception){
				exception.printStackTrace();
				System.err.println("nullpointerexception, couldn't select an entity for some reason."); 
			}
		}	
	} //end of EntitiesComboBoxActionHandler inner class
*/	
	
	// ########## MOUSE HANDLING SECTION ##############
	public void mousePressed(MouseEvent e) {
		setEditorMousePos(e.getX(), e.getY());
		//clickPosition.setLocation(e.getX(),e.getY());
		this.inputController.mousePressed(e);
		this.editorMode.mousePressed(e);
		
	}
	public void mouseDragged(MouseEvent e) {
		setEditorMousePos(e.getX(), e.getY());
		this.inputController.mouseDragged(e);
		this.editorMode.mouseDragged(e);
	}
	public void mouseMoved(MouseEvent e){
		//setEditorMousePos(e.getX(), e.getY());
		this.editorMode.mouseMoved(e);
	}
	public void mouseReleased(MouseEvent e) {	
		setEditorMousePos(e.getX(), e.getY());
		this.inputController.mouseReleased(e);
		this.editorMode.mouseReleased(e);

	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//this.editorMode.mouseWheelScrolled(e);	
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
	//FIXME this method is fucked right now. Needs a lot of work.
	/**  This method needs complete rework
	 */
	public void addEntity(int x, int y, int offsetX, int offsetY, String path) {  //default one. Adds test entity
		//EntityStatic newEnt;
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
		}
		//deselectAllEntities();
		//board.getStaticEntities().add(newEnt);
		//addEntryToListOfPropLists(new PropertiesList(newEnt));
		//updateAllEntitiesComboBox();
	}
	//will refresh(create a new one of)staticEntityStringArr, remove old comboBox and then create & add a new updated one
	//PROBLEM AREA, still a problem. Thought was fixed but has an issue when deleting entities
	/*public void updateAllEntitiesComboBox() {
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
	}*/
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
		//mousePosLabel.setText(text);
	}
	public void setEntityCoordsLabel(String text){
		entityCoordsLabel.setText(text);
	}

	public void setSelectedEntityNameLabel(String text){
		selectedEntityNameLabel.setText(text);
	}
	/*public void setAllEntitiesComboBoxIndex(int index) {
		if (index >= 0 && index < allEntitiesComboBox.getItemCount())
			allEntitiesComboBox.setSelectedIndex(index);
		else
			allEntitiesComboBox.setSelectedIndex(0);
	}*/
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
		ghostSprite = new Sprite.Stillframe(System.getProperty("user.dir")+ File.separator + "Assets"+File.separator +path);
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
		Font originalFont = g.getFont();
		g.setFont(new Font("default", Font.BOLD, 16));
		g.drawString(this.editorMode.getModeName(), 800, 30);
		g.setFont(originalFont);
		//this.getGhostSprite().editorDraw(getEditorMousePos());
		//editorMode.inputController.debugPrintInputList(200, 100, g); //no idea why this wont fucking get the inputController instance

	}
	public void defaultRender(Graphics g) {
		//will contain a render procedure for modes that certainly don't need their own rendering implementation 
	}
	public void addSelectedEntity(EntityStatic entity) {
		this.entitySelectMode.addSelectedEntity(entity);
	}
	public void removeSelectedEntity(EntityStatic entity) {
		this.entitySelectMode.removeSelectedEntity(entity);
	}
	public void clearSelectedEntities() {
		this.getEntitySelectMode().selectedEntities.clearSelectedEntities();;
	}
	public ArrayList<EntityStatic> getSelectedEntities () {
		return this.getEntitySelectMode().getSelectedEntities();
	}
	public void setMode(ModeAbstract newMode) {
		this.editorMode = newMode;
	}
	public BrowserTreePanel getBrowserTreePanel() {
		return this.browserTreePanel;
	}
	public EntitySelectMode getEntitySelectMode() {
		this.entitySelectMode.setMode(entitySelectMode.getDefaultMode());
		worldGeomButton.setEnabled(true);
		iconBarScrollPaneSpriteSwap.setVisible(false);
		spriteEditorButton.setEnabled(false);
		wgVertexSelectModeButton.setEnabled(false);
		wgVertexPlaceModeButton.setEnabled(false);
		boundaryVertexPlaceButton.setEnabled(false);
		boundaryVertexSelectButton.setEnabled(false);
		revalidate();
		return this.entitySelectMode;
	}
	public EntityPlaceMode getEntityPlaceMode() {
		iconBarScrollPaneSpriteSwap.setVisible(false);
		return this.entityPlaceMode;
	}
	public WorldGeometry getWorldGeomMode() {
		iconBarScrollPaneSpriteSwap.setVisible(false);
		spriteEditorButton.setEnabled(false);
		wgVertexPlaceModeButton.setEnabled(true);
		wgVertexSelectModeButton.setEnabled(true);
		
		boundaryVertexPlaceButton.setEnabled(false);
		boundaryVertexSelectButton.setEnabled(false);
		revalidate();
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
		boundaryVertexPlaceButton.setEnabled(false);
		boundaryVertexSelectButton.setEnabled(false);
		revalidate();
		return this.spriteEditorMode;
	}
	public BoundaryEditorMode getBoundaryEditorMode() {
		iconBarScrollPaneSpriteSwap.setVisible(false);
		boundaryVertexPlaceButton.setEnabled(true);
		boundaryVertexSelectButton.setEnabled(true);
		wgVertexSelectModeButton.setEnabled(false);
		wgVertexPlaceModeButton.setEnabled(false);
		revalidate();
		return this.boundaryEditorMode;
	}
	// ######################################### INNER CLASS MODES #######################################
	// ######################################### INNER CLASS MODES #######################################
	
/////////   INNER CLASS ENTITYSELECTMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	public class EntitySelectMode extends ModeAbstract {
		protected SelectedEntities selectedEntities;
		protected SelectionRectangleAbstract selectionRectangle;
		protected SelectionRectangleAbstract nullSelectionRectangle;
		protected SelectionRectangleAbstract selectionRectangleState;
		
		protected ModeAbstract currentMode;
		protected DefaultMode defaultMode;
		protected RotateMode rotateMode;
		protected ScaleMode scaleMode;
		protected boolean oneEntitySelected;
		private boolean selectViaSprite;
		private Point initClickPoint;
		
		private double tempAngle;
		public EntitySelectMode() {
			tempAngle = 0.0;
			//mouseMovedKeyState = new MouseMovedKeyStateNull();
			selectedEntities = new SelectedEntities(camera);
			defaultMode = new DefaultMode();
			rotateMode = new RotateMode();
			scaleMode = new ScaleMode();
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
		public void mouseWheelScrolled(MouseWheelEvent e) {
			camera.setZoomLevel(0.5);
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
				modeName = "EntitySelectMode";
				this.inputController = new InputController("Default mode (Entity select) controller");
				
				this.inputController.createMouseBinding(MouseEvent.BUTTON1, new EntitySelectLClickEvent());
				this.inputController.createMouseBinding(MouseEvent.BUTTON3, new TranslateEvent());
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON1, new SelectionOrCTRLClickEvent()); 
				this.inputController.createKeyBinding( KeyEvent.VK_ESCAPE, new DeselectEntitiesEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_R, new SetRotateMode());
				this.inputController.createKeyBinding(KeyEvent.VK_S, new SetScaleMode());
				
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
				selectionRectangleState.draw(g, camera);
			}
		} // END OF DEFAULTMODE INNER CLASS  #####
		
		public class RotateMode extends ModeAbstract {
			//private boolean ctrlHeld;
			private Point origin;
			private Vector vector;
			private double currentAngle;
			private boolean mouseDown;
			//private MouseMovedKeyState defaultMouseMovedState;
			//private MouseMovedKeyStateNull mouseMovedKeyStateNull = new MouseMovedKeyStateNull();
			//private MouseMovedKeyState ctrlMouseMovedKeyState = new CtrlMouseMovedKeyState();
			public RotateMode() {
				currentAngle = 0.0;
				mouseDown = false;
				this.modeName = "RotateMode";
				this.vector = new Vector(0, 0);
				//ctrlHeld = false;
				this.inputController = new InputController("Rotate mode controller");	
				this.inputController.createMouseBinding(MouseEvent.BUTTON3, new RotateEvent());
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON3, new DegreeLockRotateEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_D, new SetDefaultMode());
				this.inputController.createKeyBinding(KeyEvent.VK_S, new SetScaleMode());
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
				Graphics2D g2 = (Graphics2D)g.create();
				g2.setColor(Color.GREEN);
				final float dash1[] = {10.0f};
			    final BasicStroke dashed =
			        new BasicStroke(1.0f,
			                        BasicStroke.CAP_BUTT,
			                        BasicStroke.JOIN_MITER,
			                        10.0f, dash1, 0.0f);
			    g2.setStroke(dashed);
				if (selectedEntities.size() == 1) {					
					g2.drawLine(camera.getRelativeX(getCurrentEntity().getX()), camera.getRelativeY(getCurrentEntity().getY()), editorMousePos.x,editorMousePos.y);
					if (mouseDown) {
						g2.drawString(String.format("Angle: %.2f", currentAngle), editorMousePos.x, editorMousePos.y - 8);
					}
				}
			}			
			
			public EntityStatic getCurrentEntity() {
				return selectedEntities.get(0);
			}
			// ######## INNER BEHAVIOR CLASSES #########
			public class RotateEvent extends MouseCommand {
				//the functionality for this will be very similar to how you designed the selection rectangle.
				/*
				 Steps: 
				 1) click, acknowledges origin and the clicked point
				 2) generate vector from these two points
				 3)	get angle from this vector
				 4) set rotation of selected entity by this angle
				 Considerations:
				 Everything should work except the blue shaded selection square around the entity. That won't rotate, will need to take care of that separately.
				 -There should be an ongoing vector in this mode
				 
				 */

				@Override
				public void mousePressed() {
					mouseDown = true;
					// gonna need to create vectore from initClickPoint and current mouse pos (editorMousePos?)
					initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
					double deltaX = editorMousePos.getX() - 
							camera.getRelativePoint(getCurrentEntity().getPosition()).getX();
					double deltaY = editorMousePos.getY() - 
							camera.getRelativePoint(getCurrentEntity().getPosition()).getY();
					if (editorMousePos.distance(
							camera.getRelativePoint(getCurrentEntity().getPosition())) > 20) {
						vector.setX(-deltaX);
						vector.setY(-deltaY);
						currentAngle = vector.angleFromVectorInDegrees();
						getCurrentEntity().getAngularComposite().setAngleInDegrees(currentAngle);
					}
				}

				@Override
				public void mouseDragged() {
					// ~~~### First way: using an initial click point ### /// 
					/*	double deltaX = camera.getLocalPosition(editorMousePos).getX() -
									initClickPoint.getX();
					double deltaY = camera.getLocalPosition(editorMousePos).getY() -
									initClickPoint.getY();*/
					// ~~~#### Second way: getting init point from entity's origin
					double deltaX = editorMousePos.getX() - 
									camera.getRelativePoint(getCurrentEntity().getPosition()).getX();
					double deltaY = editorMousePos.getY() - 
									camera.getRelativePoint(getCurrentEntity().getPosition()).getY();
					if (editorMousePos.distance(
							camera.getRelativePoint(getCurrentEntity().getPosition())) > 20) {
						vector.setX(-deltaX);
						vector.setY(-deltaY);
						currentAngle = vector.angleFromVectorInDegrees();
						getCurrentEntity().getAngularComposite().setAngleInDegrees(currentAngle);
					}
				}

				@Override
				public void mouseReleased() {
					mouseDown = false;
				}
			}
			public class DegreeLockRotateEvent extends MouseCommand {
				@Override
				public void mousePressed() {
					mouseDown = true;
					// gonna need to create vectore from initClickPoint and current mouse pos (editorMousePos?)
					initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
				}
				
				@Override
				public void mouseDragged() {
					// ~~~### First way: using an initial click point ### /// 
					/*	double deltaX = camera.getLocalPosition(editorMousePos).getX() -
									initClickPoint.getX();
					double deltaY = camera.getLocalPosition(editorMousePos).getY() -
									initClickPoint.getY();*/
					// ~~~#### Second way: getting init point from entity's origin
					double deltaX = editorMousePos.getX() - 
							camera.getRelativePoint(getCurrentEntity().getPosition()).getX();
					double deltaY = editorMousePos.getY() - 
							camera.getRelativePoint(getCurrentEntity().getPosition()).getY();
					if (editorMousePos.distance(
							camera.getRelativePoint(getCurrentEntity().getPosition())) > 20) {
						vector.setX((int)-deltaX);
						vector.setY((int)-deltaY);
						currentAngle = vector.angleFromVectorInDegrees();
						getCurrentEntity().getAngularComposite().setAngleInDegrees(15*(Math.round(currentAngle/15)));
					}
				}
				
				@Override
				public void mouseReleased() {
					mouseDown = false;
				}
			}
		} // END OF ROTATEMODE INNER CLASS  #####
		
		// ##################################### SCALE MODE ################################
		public class ScaleMode extends ModeAbstract {
			protected double sizeFactorRef;
			public ScaleMode() {
				modeName = "ScaleMode";
				sizeFactorRef = 0.0;
				this.inputController = new InputController("Scale mode controller");
				
				this.inputController.createMouseBinding(MouseEvent.BUTTON3, new ScaleEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_R, new SetRotateMode());
				this.inputController.createKeyBinding(KeyEvent.VK_D, new SetDefaultMode());
				
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
			public class ScaleEvent extends MouseCommand {

				@Override
				public void mousePressed() {
					// gonna need to create vectore from initClickPoint and current mouse pos (editorMousePos?)
					initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
					//if 
					sizeFactorRef = getSingleSelectedEntity().getGraphicComposite().getSprite().getSizeFactor();
				}

				@Override
				public void mouseDragged() {
					//double tempDistance = Math.abs(camera.getRelativePoint(getCurrentEntity().getPosition()).distance(editorMousePos));
					//double tempDistance = camera.getRelativePoint(initClickPoint).distance(editorMousePos);
					// vvv from the boundary scaling. Won't work as is.
					//   selectedVertices.scaleVertices(initClickPoint, editorMousePos, currentSelectedEntity.getPosition());
					double tempDistance = -(camera.getRelativeX(initClickPoint.getX()) - editorMousePos.getX());
					double width = getSingleSelectedEntity().getGraphicComposite().getSprite().getBufferedImage().getWidth();
					double height = getSingleSelectedEntity().getGraphicComposite().getSprite().getBufferedImage().getHeight();
					double hyp = Math.sqrt( (width*width/4) + (height*height/4));


					getSingleSelectedEntity().getGraphicComposite().getSprite().setSizeFactor(tempDistance/hyp + sizeFactorRef);
					/*if (editorMousePos.distance(
										camera.getRelativePoint(getCurrentEntity().getPosition())) > 20) {
									vector.setX(-deltaX);
									vector.setY(-deltaY);
									currentAngle = vector.angleFromVectorInDegrees();
									getCurrentEntity().getGraphicComposite().getSprite().setAngle(currentAngle);
								}*/
				}

				@Override
				public void mouseReleased() {
				}
			}
		} // END OF DEFAULTMODE INNER CLASS  #####
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
								removeSelectedEntity(entity);
							else {
								addSelectedEntity(entity);
							}
						}
					}
				}
			}
			else {
				for(EntityStatic entity: board.listCurrentSceneEntities()) {
					//polygonTest.
					//if (entity.getColliderComposite() instanceof ColliderNull ){
					if (entity.getColliderComposite().exists() ){
						
						Polygon polygonTest = entity.getColliderComposite().getBoundary().getPolygonBounds(entity);
						//Polygon polygonTest = new Polygon(xpoints, ypoints, bound.getCornersPoint().length);
						Rectangle rect = polygonTest.getBounds();
						if (polygonTest.contains(click)) {
							if (selectedEntities.contains(entity))
								removeSelectedEntity(entity);
							else
								addSelectedEntity(entity);
						}
					}
				}// end of for loop
			} //end of else
			if (selectedEntities.size() == 1) {
				spriteEditorButton.setEnabled(true);
				boundaryVertexSelectButton.setEnabled(true);
				boundaryVertexPlaceButton.setEnabled(true);
				
			}
			else {
				spriteEditorButton.setEnabled(false);
				boundaryVertexSelectButton.setEnabled(false);
				boundaryVertexPlaceButton.setEnabled(false);
			}
		}
		public void checkForEntity(Point click) {
			//boolean atLeastOneVertexFound = false;
			//since this is the regular click method, would want to make sure any selected entities are deselected first
			if (selectedEntities.size() > 0) {
				spriteEditorButton.setEnabled(false);
				boundaryVertexSelectButton.setEnabled(false);
				boundaryVertexPlaceButton.setEnabled(false);
				clearSelectedEntities();
			}
			if (selectViaSprite == true) {
				for (EntityStatic entity: board.listCurrentSceneEntities()) {
					if (entity.getGraphicComposite().exists()) {
						Rectangle clickableRect = new Rectangle();
						//clickableRect.setLocation(entity.getXRelativeTo(camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(camera) + entity.getSpriteOffsetY());
						Sprite graphic = entity.getGraphicComposite().getSprite();
						clickableRect.setLocation(entity.getX() + graphic.getOffsetX(), entity.getY() + graphic.getOffsetY());
						clickableRect.setSize(graphic.getImage().getWidth(null), graphic.getImage().getHeight(null) );
						if (clickableRect.contains(click))
						{
							if (selectedEntities.contains(entity) == false) {
								addSelectedEntity(entity);
								browserTreePanel.doNotifyEntitySelected(entity);
								spriteEditorButton.setEnabled(true);
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
						Boundary bound = entity.getColliderComposite().getBoundary();
						/*int[] xpoints;
						int[] ypoints;
						
						xpoints = new int[bound.getCornersPoint().length];
						ypoints = new int[bound.getCornersPoint().length];
						
						for (int i = 0; i < bound.getCornersPoint().length; i++ ) {
							xpoints[i] = (int)bound.getCornersPoint()[i].getX();
							ypoints[i] = (int)bound.getCornersPoint()[i].getY();
						}*/
						Polygon polygonTest = bound.getPolygonBounds(entity);
						//Polygon polygonTest = new Polygon(xpoints, ypoints, bound.getCornersPoint().length);
						
						//Rectangle rect = polygonTest.getBounds();
						if (polygonTest.contains(click)) {
							if (selectedEntities.contains(entity) == false) {
								addSelectedEntity(entity);
								browserTreePanel.doNotifyEntitySelected(entity);
								spriteEditorButton.setEnabled(true);
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
					if (entity.getGraphicComposite().exists()){
						Rectangle clickableRect = new Rectangle();
						Sprite graphic = entity.getGraphicComposite().getSprite();
						//clickableRect.setLocation(entity.getXRelativeTo(camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(camera) + entity.getSpriteOffsetY());
						clickableRect.setLocation(entity.getX() + graphic.getOffsetX(), entity.getY() + graphic.getOffsetY());
						clickableRect.setSize(graphic.getImage().getWidth(null), graphic.getImage().getHeight(null) );
						//if(selectionRect.contains(clickableRect)) {
						if(selectionRect.intersects(clickableRect)) {
							if(selectedEntities.contains(entity) == false) {
								addSelectedEntity(entity);
							}
						}
					}
				}
			}
			else {
				for(EntityStatic entity: board.listCurrentSceneEntities()) {
					//polygonTest.
					if (entity.getColliderComposite().exists()){
						Boundary bound = entity.getColliderComposite().getBoundary();
						
						Polygon polygonTest = bound.getPolygonBounds(entity);
						
						if (polygonTest.intersects(selectionRect)) {
							if (selectedEntities.contains(entity) == false) {
								addSelectedEntity(entity);
							}
						}
					}
				}// end of for loop
			}
			if (selectedEntities.size() == 1) {
				//TODO return to this once synced with matt's new Sprite composite system
				spriteEditorButton.setEnabled(true);
				boundaryVertexSelectButton.setEnabled(true);
				boundaryVertexPlaceButton.setEnabled(true);
			}
			else {
				spriteEditorButton.setEnabled(false);
				boundaryVertexSelectButton.setEnabled(false);
				boundaryVertexPlaceButton.setEnabled(false);
			}
		}
		
		public void addSelectedEntity(EntityStatic entity) {
			selectedEntities.addSelectedEntity(entity);
			setSelectedEntityNameLabel("Selected: " + entitySelectMode.selectedEntities.printSelectedEntitiesAsString());
		}
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
		public ScaleMode getScaleMode(){
			return scaleMode;
		}
		public void setMode(ModeAbstract newMode) {
			this.currentMode = newMode;
		}
		public void removeSelectedEntity(EntityStatic entity) {
			selectedEntities.removeSelectedEntity(entity);
			setSelectedEntityNameLabel("Selected: " + entitySelectMode.selectedEntities.printSelectedEntitiesAsString());
		}
		public void clearSelectedEntities() {
			this.selectedEntities.clearSelectedEntities();
			setSelectedEntityNameLabel("Selected: " + entitySelectMode.selectedEntities.printSelectedEntitiesAsString());
			browserTreePanel.doNotifyDeselectEntity();
		}
		public ArrayList<EntityStatic> getSelectedEntities () {
			return selectedEntities.getSelectedEntities();
		}
		public class EntitySelectLClickEvent extends MouseCommand {

			@Override
			public void mousePressed() {
				checkForEntity(camera.getWorldTranslationalPosition(editorMousePos));
				//selectedEntities.printSelectedEntities();
			}

			@Override
			public void mouseDragged() {}

			@Override
			public void mouseReleased() {} 	
		}
		
		public class TranslateEvent extends MouseCommand{

			public void mousePressed() {
				initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
				selectedEntities.updateOldEntityPositions();
			}
			public void mouseDragged() {
				selectedEntities.translate(initClickPoint, editorMousePos);
			}
			public void mouseReleased() {}

		}
		public class CtrlEntitySelectLClickEvent extends MouseCommand {

			@Override
			public void mousePressed() {}
			@Override
			public void mouseDragged() {}
			@Override
			public void mouseReleased() {
				checkForEntityCtrlClick(camera.getWorldTranslationalPosition(editorMousePos));
			}
		}
		public class SelectionRectEvent extends MouseCommand {
			@Override
			public void mousePressed() {
				selectionRectangleState = selectionRectangle;
				initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
				selectionRectangleState.setInitialRectPoint();
			}
			@Override
			public void mouseDragged() {
				selectionRectangleState.translateEndPoint(camera.getWorldTranslationalPosition(editorMousePos));
			}
			@Override
			public void mouseReleased() {
				//command to select vertices underneath box
				checkForEntityInSelectionRect(selectionRectangleState.getWrekt());
				selectionRectangleState.resetRect();
				selectionRectangleState = nullSelectionRectangle;
			}
		}
		public class SelectionOrCTRLClickEvent extends MouseCommand {
			private MouseCommand tempState;
			private SelectionRectEvent selectRectState;
			private CtrlEntitySelectLClickEvent ctrlState;
			public SelectionOrCTRLClickEvent() {
				SelectInstanceCount++;
				System.err.println("(editorpanel)selectRectInstanceCount: " + SelectInstanceCount);
				selectRectState = new SelectionRectEvent();
				ctrlState = new CtrlEntitySelectLClickEvent();
				tempState = ctrlState; //will assume we're in ctrlclick state until a drag occurs.
			}
			
			@Override
			public void mousePressed() {
				//set initial click point just in case drag happens; it'll need that data
				//actually I guess I could just run selectRectEvent's mousePressed(), it's lightweight anyway
				selectRectState.mousePressed();
			}
			@Override
			public void mouseDragged() {
				// in this body, change tempState to select Rect State.
				//	because if any drag at all happens, then has to be a selectionRect event
				tempState = selectRectState;
				tempState.mouseDragged();
			}
			@Override
			public void mouseReleased() {
				tempState.mouseReleased(); //will fire ctrlClick event if no drag occured. Otherwise will fire selectionRect event.
				//then set back to default state (ctrlEntitySelectLClickEvent)
				tempState = ctrlState;
			}
		}
		public class DeselectEntitiesEvent extends KeyCommand {
			@Override
			public void onPressed() {
				selectedEntities.clearSelectedEntities();
				spriteEditorButton.setEnabled(false);
				boundaryVertexSelectButton.setEnabled(false);
				boundaryVertexPlaceButton.setEnabled(false);
			}
			public void onReleased() {}
			public void onHeld() {}
		}
		public class SetRotateMode extends KeyCommand {
			@Override
			public void onPressed() {
				if (selectedEntities.size() == 1) {
					setMode(rotateMode);
				}
			}
			public void onReleased() {}
			public void onHeld() {}
		}
		public class SetDefaultMode extends KeyCommand {
			@Override
			public void onPressed() {
				setMode(defaultMode);
			}
			public void onReleased() {}
			public void onHeld() {}
		}
		public class SetScaleMode extends KeyCommand {
			@Override
			public void onPressed() {
				if (selectedEntities.size() == 1) {
					setMode(scaleMode);
				}
			}
			public void onReleased() {}
			public void onHeld() {}
		}
	}  // end of EntitySelectMode inner class
	
/////////   INNER CLASS ENTITYPLACEMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

	public class EntityPlaceMode extends ModeAbstract {
		public EntityPlaceMode() {
			modeName = "EntityPlaceMode";
			inputController = new InputController("Entity place mode controller");
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
			setMode(getEntitySelectMode());
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
	}  // end of EntityPlaceMode inner class
	
	
	
	
/////////   INNER CLASS SPRITEEDITORMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	public class SpriteEditorMode extends ModeAbstract {
		protected EntityStatic currentSelectedEntity;

		private ArrayList<EntityStatic> selectedEntitiesRef = getSelectedEntities();
		private String spritePath;
		
		protected ModeAbstract spriteEditorSubMode;
		//protected SpriteOffSetMode spriteOffsetMode;
		protected DefaultSpriteEditorMode defaultSpriteEditorMode;
		protected SpriteRotateMode spriteRotateMode;
		protected SpriteScaleMode spriteScaleMode;
		private Point initClickPoint;
		
		
		public SpriteEditorMode(){
			//modeName = "SpriteEditorMode";
			spritePath = "";
			initClickPoint = new Point();
			defaultSpriteEditorMode = new DefaultSpriteEditorMode();
			spriteRotateMode = new SpriteRotateMode();
			spriteScaleMode = new SpriteScaleMode();
			spriteEditorSubMode = defaultSpriteEditorMode;
			this.inputController = new InputController("Sprite editor mode controller");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			this.spriteEditorSubMode.mousePressed(e);
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			this.spriteEditorSubMode.mouseDragged(e);
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			this.spriteEditorSubMode.mouseMoved(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			this.spriteEditorSubMode.mouseReleased(e);
		}
		@Override
		public void keyPressed(KeyEvent e) {
			this.spriteEditorSubMode.keyPressed(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			this.spriteEditorSubMode.keyReleased(e);
		}
		@Override
		public void render(Graphics g) {
			this.spriteEditorSubMode.render(g);
		}
		public void setCurrentEntity(EntityStatic newEntity) {
			this.currentSelectedEntity = newEntity;
		}
		public EntityStatic getCurrentEntity(){
			return this.currentSelectedEntity;
		}
		@Override
		public String getModeName(){
			return this.spriteEditorSubMode.getModeName();
		}
		public void setSubMode(ModeAbstract newMode) {
			this.spriteEditorSubMode = newMode;
		}
		public void replaceAndFinalizeSprite(String path) {
/*			Boundary newBoundary = new Boundary(lines);
			this.currentSelectedEntity.getColliderComposite().setBoundary(newBoundary);
			getVertexSelectMode().selectedVertices.clearSelectedVertices();*/
			
			Sprite.Stillframe replacementSprite = new Sprite.Stillframe("SpriteHotSwap\\" + path);
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
		public class SetRotateMode extends KeyCommand {
			@Override
			public void onPressed() {
				setSubMode(spriteRotateMode);
			}
			public void onReleased() {}
			public void onHeld() {}
		}
		public class SetDefaultMode extends KeyCommand {
			@Override
			public void onPressed() {
				setSubMode(defaultSpriteEditorMode);
			}
			public void onReleased() {}
			public void onHeld() {}
		}
		public class SetScaleMode extends KeyCommand {
			@Override
			public void onPressed() {
				setSubMode(spriteScaleMode);
			}
			public void onReleased() {}
			public void onHeld() {}
		}
		public class DefaultSpriteEditorMode extends ModeAbstract {
			//Point initClick;
			//Point spriteInitialPosition;
			Point spriteOriginalOffset;
			
			public DefaultSpriteEditorMode() {
				modeName = "DefaultSpriteEditorMode";
				//spriteInitialPosition = new Point();
				spriteOriginalOffset = new Point();
				inputController = new InputController("Default sprite editor mode controller");
				this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());			
				this.inputController.createMouseBinding(MouseEvent.BUTTON3, new TranslateOffsetEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_R, new SetRotateMode());
				this.inputController.createKeyBinding(KeyEvent.VK_S, new SetScaleMode());
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
			public class TranslateOffsetEvent extends MouseCommand {
				@Override
				public void mousePressed() {
				/*	spriteOriginalOffset.setLocation(currentSelectedEntity.getGraphicComposite().getSprite().getOffsetX(),
							 currentSelectedEntity.getGraphicComposite().getSprite().getOffsetX());*/

					initClickPoint.setLocation(editorMousePos); // sets temporary old mouse position reference
					
					//sets temporary old Sprite offset
					spriteOriginalOffset = new Point( currentSelectedEntity.getGraphicComposite().getSprite().getOffsetPoint() );
					
					spriteOriginalOffset.setLocation(currentSelectedEntity.getGraphicComposite().getSprite().getOffsetPoint());;
				}

				@Override
				public void mouseDragged() {
					Sprite currentSprite = currentSelectedEntity.getGraphicComposite().getSprite();
					int mousePanDX = (camera.getLocalX(initClickPoint.x) - camera.getLocalX(editorMousePos.x));
					int mousePanDY = (camera.getLocalY(initClickPoint.y) - camera.getLocalY(editorMousePos.y));
					Vector originalVector = new Vector(mousePanDX, -mousePanDY);
					Vector newVector = currentSprite.getRelativePoint(originalVector);
					/*currentSelectedEntity.getGraphicComposite().getSprite().setOffset(
							(int)(spriteInitialPosition.x + mousePanDX),
							(int)(spriteInitialPosition.y + mousePanDY)
							);*/
				/*	currentSelectedEntity.getGraphicComposite().getSprite().setOffset(
							(int)(camera.getLocalX(spriteOriginalOffset.x - mousePanDX)),
							(int)(camera.getLocalY(spriteOriginalOffset.y - mousePanDY))
							);*/
					//currentSprite.setOffset(-(int)newVector.getX(),-(int)newVector.getY());

					Vector orientation = Vector.unitVectorFromAngle(Math.toRadians(currentSprite.getAngle()));

					//	Matt: Heres where I messed up the math, I was using the dot product projection method which returns the VECTOR
					//of the projection on the sprite x and y axis, RELATIVE TO BOARD. So when the sprite was diagonal, the dot product
					//returned diagonal coordnates when it really needed the straight x and y coordinates relative to the sprite.
					//		
					//	Like if you imagine a side of the tilted sprite, it's a diagonal vector relative to board, 
					// but it's just a straight line relative to the sprite.
					//
					//	What I needed was the cross product, which returns the projection DISTANCE over the sprite x and y axes.
					//that ends up with the x and y coordinates RELATIVE TO THE SPRITE
					double relativeDX = originalVector.crossProduct(orientation);
					double relativeDY = originalVector.crossProduct(orientation.normalRight()); //normal right is just the y axis relative
					//to the sprite.
					currentSprite.setOffset( (int)relativeDX + spriteOriginalOffset.x , (int)relativeDY + spriteOriginalOffset.y );
					
				}
				@Override
				public void mouseReleased() {}
			}
			public class SwapSpriteEvent extends KeyCommand {
				@Override
				public void onPressed() {
					replaceAndFinalizeSprite(spritePath);
				}
				@Override
				public void onReleased() {}
				@Override
				public void onHeld() {}
			}
			public class SetOffsetEvent extends KeyCommand {
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
		public class SpriteRotateMode extends ModeAbstract {
			//private boolean ctrlHeld;
			//private Point origin;
			private Vector vector;
			private double currentAngle;
			private boolean mouseDown;
			private double sizeFactorRef;
			//private MouseMovedKeyState defaultMouseMovedState;
			//private MouseMovedKeyStateNull mouseMovedKeyStateNull = new MouseMovedKeyStateNull();
			//private MouseMovedKeyState ctrlMouseMovedKeyState = new CtrlMouseMovedKeyState();
			public SpriteRotateMode() {
				sizeFactorRef = 0;
				currentAngle = 0.0;
				mouseDown = false;
				this.modeName = "SpriteRotateMode";
				this.vector = new Vector(0, 0);
				//ctrlHeld = false;
				this.inputController = new InputController("Rotate mode controller");	
				this.inputController.createMouseBinding(MouseEvent.BUTTON3, new RotateEvent());
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON3, new DegreeLockRotateEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_D, new SetDefaultMode());
				this.inputController.createKeyBinding(KeyEvent.VK_S, new SetScaleMode());
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
				Graphics2D g2 = (Graphics2D)g.create();
				g2.setColor(Color.GREEN);
				final float dash1[] = {10.0f};
			    final BasicStroke dashed =
			        new BasicStroke(1.0f,
			                        BasicStroke.CAP_BUTT,
			                        BasicStroke.JOIN_MITER,
			                        10.0f, dash1, 0.0f);
			    g2.setStroke(dashed);
				//g2.drawLine(camera.getRelativeX(getCurrentEntity().getX()), camera.getRelativeY(getCurrentEntity().getY()), editorMousePos.x,editorMousePos.y);
				if (mouseDown) {
					g2.drawString(String.format("Angle: %.2f", currentAngle), editorMousePos.x, editorMousePos.y - 8);
				}
			}			
			
			// ######## INNER BEHAVIOR CLASSES #########
			public class RotateEvent extends MouseCommand {
				//the functionality for this will be very similar to how you designed the selection rectangle.
				/*
				 Steps: 
				 1) click, acknowledges origin and the clicked point
				 2) generate vector from these two points
				 3)	get angle from this vector
				 4) set rotation of selected entity by this angle
				 Considerations:
				 Everything should work except the blue shaded selection square around the entity. That won't rotate, will need to take care of that separately.
				 -There should be an ongoing vector in this mode

				 */

				@Override
				public void mousePressed() {
					mouseDown = true;
					// gonna need to create vectore from initClickPoint and current mouse pos (editorMousePos?)
					initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
					sizeFactorRef = getCurrentEntity().getGraphicComposite().getSprite().getSizeFactor();
					double deltaX = editorMousePos.getX() - 
							camera.getRelativePoint(getCurrentEntity().getPosition()).getX();
					double deltaY = editorMousePos.getY() - 
							camera.getRelativePoint(getCurrentEntity().getPosition()).getY();

					if (editorMousePos.distance(
							camera.getRelativePoint(getCurrentEntity().getPosition())) > 20) {
						vector.setX(-deltaX);
						vector.setY(-deltaY);
						currentAngle = vector.angleFromVectorInDegrees();
						getCurrentEntity().getGraphicComposite().getSprite().setAngle(currentAngle);
					}
				}

				@Override
				public void mouseDragged() {
					// ~~~### First way: using an initial click point ### /// 
					/*	double deltaX = camera.getLocalPosition(editorMousePos).getX() -
									initClickPoint.getX();
					double deltaY = camera.getLocalPosition(editorMousePos).getY() -
									initClickPoint.getY();*/
					// ~~~#### Second way: getting init point from entity's origin
					double deltaX = editorMousePos.getX() - 
									camera.getRelativePoint(getCurrentEntity().getPosition()).getX();
					double deltaY = editorMousePos.getY() - 
									camera.getRelativePoint(getCurrentEntity().getPosition()).getY();
					
					if (editorMousePos.distance(
							camera.getRelativePoint(getCurrentEntity().getPosition())) > 20) {
						vector.setX(-deltaX);
						vector.setY(-deltaY);
						currentAngle = vector.angleFromVectorInDegrees();
						getCurrentEntity().getGraphicComposite().getSprite().setAngle(currentAngle);
					}
				}

				@Override
				public void mouseReleased() {
					mouseDown = false;
				}
			}
			public class DegreeLockRotateEvent extends MouseCommand {
				@Override
				public void mousePressed() {
					mouseDown = true;
					// gonna need to create vectore from initClickPoint and current mouse pos (editorMousePos?)
					initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
				}
				
				@Override
				public void mouseDragged() {
					// ~~~### First way: using an initial click point ### /// 
					/*	double deltaX = camera.getLocalPosition(editorMousePos).getX() -
									initClickPoint.getX();
					double deltaY = camera.getLocalPosition(editorMousePos).getY() -
									initClickPoint.getY();*/
					// ~~~#### Second way: getting init point from entity's origin
					double deltaX = editorMousePos.getX() - 
							camera.getRelativePoint(getCurrentEntity().getPosition()).getX();
					double deltaY = editorMousePos.getY() - 
							camera.getRelativePoint(getCurrentEntity().getPosition()).getY();
					if (editorMousePos.distance(
							camera.getRelativePoint(getCurrentEntity().getPosition())) > 20) {
						vector.setX((int)-deltaX);
						vector.setY((int)-deltaY);
						currentAngle = vector.angleFromVectorInDegrees();
						getCurrentEntity().getGraphicComposite().getSprite().setAngle(15*(Math.round(currentAngle/15)));
					}
				}
				
				@Override
				public void mouseReleased() {
					mouseDown = false;
				}
			}
		} // END OF ROTATEMODE INNER CLASS  #####
		
		// SPRITE SCALE MODE!
		@SuppressWarnings("unused")
		public class SpriteScaleMode extends ModeAbstract {
			//private boolean ctrlHeld;
			//private Point origin;

			private Vector vector;
			private double currentAngle;
			private boolean mouseDown;
			private double dragDistance;
			private double sizeFactorRef;
			//private MouseMovedKeyState defaultMouseMovedState;
			//private MouseMovedKeyStateNull mouseMovedKeyStateNull = new MouseMovedKeyStateNull();
			//private MouseMovedKeyState ctrlMouseMovedKeyState = new CtrlMouseMovedKeyState();
			public SpriteScaleMode() {
				sizeFactorRef = 0;
				dragDistance = 0.0;
				currentAngle = 0.0;
				mouseDown = false;
				this.modeName = "SpriteScaleMode";
				this.vector = new Vector(0, 0);
				//ctrlHeld = false;
				this.inputController = new InputController("Scale mode controller");	
				this.inputController.createMouseBinding(MouseEvent.BUTTON3, new ScaleEvent());
				this.inputController.createMouseBinding(MouseEvent.CTRL_MASK, MouseEvent.BUTTON3, new ScaleIncrementEvent());
				this.inputController.createKeyBinding(KeyEvent.VK_D, new SetDefaultMode());
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
				Graphics2D g2 = (Graphics2D)g.create();
				g2.setColor(Color.GREEN);
				final float dash1[] = {10.0f};
				final BasicStroke dashed =
						new BasicStroke(1.0f,
								BasicStroke.CAP_BUTT,
								BasicStroke.JOIN_MITER,
								10.0f, dash1, 0.0f);
				g2.setStroke(dashed);
				//g2.drawLine(camera.getRelativeX(getCurrentEntity().getX()), camera.getRelativeY(getCurrentEntity().getY()), editorMousePos.x,editorMousePos.y);
				if (mouseDown) {
					g2.drawString(String.format("Angle: %.2f", currentAngle), editorMousePos.x, editorMousePos.y - 8);
					//g2.drawString(String.format("DrawDistance: %.2f", dragDistance), editorMousePos.x, editorMousePos.y - 20);
				}
			}			
			
			// ######## INNER BEHAVIOR CLASSES #########
			public class ScaleEvent extends MouseCommand {
				
				@Override
				public void mousePressed() {
					mouseDown = true;
					// gonna need to create vectore from initClickPoint and current mouse pos (editorMousePos?)
					initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
					sizeFactorRef = getCurrentEntity().getGraphicComposite().getSprite().getSizeFactor();
				}
				
				@Override
				public void mouseDragged() {
					//double tempDistance = Math.abs(camera.getRelativePoint(getCurrentEntity().getPosition()).distance(editorMousePos));
					//double tempDistance = camera.getRelativePoint(initClickPoint).distance(editorMousePos);
					double tempDistance = -(camera.getRelativeX(initClickPoint.getX()) - editorMousePos.getX());
					dragDistance = tempDistance; //not an important field
					double width = getCurrentEntity().getGraphicComposite().getSprite().getBufferedImage().getWidth();
					double height = getCurrentEntity().getGraphicComposite().getSprite().getBufferedImage().getHeight();
					double hyp = Math.sqrt( (width*width/4) + (height*height/4));
					
					
					getCurrentEntity().getGraphicComposite().getSprite().setSizeFactor(tempDistance/hyp + sizeFactorRef);
					/*if (editorMousePos.distance(
							camera.getRelativePoint(getCurrentEntity().getPosition())) > 20) {
						vector.setX(-deltaX);
						vector.setY(-deltaY);
						currentAngle = vector.angleFromVectorInDegrees();
						getCurrentEntity().getGraphicComposite().getSprite().setAngle(currentAngle);
					}*/
				}
				
				@Override
				public void mouseReleased() {
					mouseDown = false;
				}
			}
			public class ScaleIncrementEvent extends MouseCommand {
				//not currently using this mode
			}
		} // END OF ROTATEMODE INNER CLASS  #####
	}
/////////   INNER CLASS BOUNDARYEDITORMODE   //////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	public class BoundaryEditorMode extends ModeAbstract {
		protected BufferedImage ghostVertexPic;
		private BoundaryVertexPlaceMode boundaryVertexPlaceMode;
		private BoundaryVertexSelectMode boundaryVertexSelectMode;
		private ModeAbstract boundarySubMode;
		protected boolean isClosedShape;

		protected EntityStatic currentSelectedEntity;
		protected EntityNull nullEntity = EntityNull.getNullEntity();
		private ArrayList<EditorVertex> vertexList = new ArrayList<>();
		private ArrayList<EditorVertex> oldVertexListForReset = new ArrayList<>(vertexList);
		//private ArrayList<EditorVertex> oldVertexListForScaling = new ArrayList<>();
		private ArrayList<Line2D.Double> surfaceLines = new ArrayList<>();
		private ArrayList<Line2D.Double> oldBoundaryLines = new ArrayList<>();
		
		public BoundaryEditorMode() {
			modeName = "BoundaryEditorMode";
			isClosedShape = false;
			//this.currentSelectedEntity = currentEntityRef;
			boundaryVertexPlaceMode = new BoundaryVertexPlaceMode();
			boundaryVertexSelectMode = new BoundaryVertexSelectMode();
			boundarySubMode = boundaryVertexSelectMode;
			ghostVertexPic = (BufferedImage)EditorVertex.createVertexPic(0.5f);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			this.boundarySubMode.mousePressed(e);
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			this.boundarySubMode.mouseDragged(e);
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			this.boundarySubMode.mouseMoved(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			this.boundarySubMode.mouseReleased(e);
		}
		@Override
		public void keyPressed(KeyEvent e) {
			this.boundarySubMode.keyPressed(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			this.boundarySubMode.keyReleased(e);
		}
		@Override
		public void render(Graphics g) {
			this.boundarySubMode.render(g);
		}
		public void defaultRender(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;			
			//old drawVertexPoints vvvvvvv
			for (EditorVertex editorVertex: vertexList) {
				editorVertex.draw(g2, camera);
			}
			// old drawsurfacelines vvvvvv
			g2.setColor(Color.DARK_GRAY);
			for (Line2D.Double lineToDraw: oldBoundaryLines) {
				camera.drawInBoard(lineToDraw,g2);
			}
			g2.setColor(Color.MAGENTA);
			/*for (int i = 0; i < vertexList.size()-1; i++) {
				Line2D.Double tempLine = new Line2D.Double(vertexList.get(i).getPoint(), vertexList.get(i+1).getPoint());
				camera.draw(tempLine);
			}*/
			for (Line2D.Double lineToDraw: surfaceLines) {
				camera.drawInBoard(lineToDraw,g2);
			}
		}
		@Override
		public String getModeName() {
			return this.boundarySubMode.getModeName();
		}
		public BoundaryVertexPlaceMode getVertexPlaceMode() {
			return this.boundaryVertexPlaceMode;
		}
		public BoundaryVertexSelectMode getVertexSelectMode() {
			return this.boundaryVertexSelectMode;
		}
		public void setSubMode(ModeAbstract newMode) {
			this.boundarySubMode = newMode;
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
			if ((checkIfClosedShape(lineArray) == false) && (lineArray.size() > 0)) {  //is an open shape, and array's not empty
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
			oldBoundaryLines.clear();
		}
		
		public void setCurrentEntity(EntityStatic newEntity) {
			this.currentSelectedEntity = newEntity;
			//debugTestForVerticesPosition();
			retrieveVertsFromBoundary(currentSelectedEntity.getColliderComposite());
			setUpBackUpVertsForReset();
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
		public void setUpBackUpVertsForReset() {
			this.oldVertexListForReset.clear();
			for (EditorVertex newVert: vertexList) 
				oldVertexListForReset.add(new EditorVertex((int)newVert.getPoint().getX(), (int)newVert.getPoint().getY()));
		}
		public void retrieveVertsFromBoundary(Collider sourceCollider){
			clearAllVerticesAndLines();
			//might not need either of these two lines vvvvv
			//ArrayList<Point2D> temporaryPointsList = new ArrayList<>();
			//Point2D[] temporarayPointsArray = sourceCollider.getBoundary().getCornersPoint();
			for (Point2D vertexToAdd: sourceCollider.getBoundary().getCornersPoint()){
				
				Point2D absoluteCorner = sourceCollider.absolutePositionOfRelativePoint(vertexToAdd);
				
				vertexList.add(new EditorVertex( (int)vertexToAdd.getX(),(int)vertexToAdd.getY()) );
			}
			refreshAllSurfaceLines(surfaceLines);
			refreshAllSurfaceLines(oldBoundaryLines);
			closeShape(surfaceLines);
			closeShape(oldBoundaryLines);
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
			refreshAllSurfaceLines(oldBoundaryLines);
			closeShape(surfaceLines);
			closeShape(oldBoundaryLines);
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
				this.currentSelectedEntity.getColliderComposite().setBoundary(newBoundary);
				//CompositeFactory.addColliderTo( this.currentSelectedEntity , newBoundary);
				clearAllVerticesAndLines();
				clearOldBoundary();
				isClosedShape = false;
				getVertexSelectMode().selectedVertices.clearSelectedVertices();
			}
		}
		
//  #################### INNER CLASS BOUNDARY VERTEX SELECT MODE ################################################### BOUNDARY VERTEX SELECT MODE
		public class BoundaryVertexSelectMode extends ModeAbstract {
			protected SelectedVertices selectedVertices;
			
			protected ModeAbstract boundaryVertexSelectSubMode;
			protected DefaultBoundarySubMode defaultBoundarySubMode;
			protected ScaleBoundarySubMode scaleBoundarySubMode;
			protected SelectionRectangleAbstract selectionRectangle;
			protected SelectionRectangleAbstract selectionRectangleState;
			protected SelectionRectangleAbstract nullSelectionRectangle;
			protected Point initClickPoint;
			
			//constructor
			public BoundaryVertexSelectMode() {
				//FIXME ** WILL NEED TO SPLIT THIS CLASS INTO TWO SUBMODES: DEFAULT AND SCALE MODE, MAYBE ROTATE LATER
				modeName = "BoundaryVertexSelectMode";
				defaultBoundarySubMode = new DefaultBoundarySubMode();
				scaleBoundarySubMode = new ScaleBoundarySubMode();
				this.boundaryVertexSelectSubMode = defaultBoundarySubMode;
				initClickPoint = new Point();
				selectedVertices = new SelectedVertices(camera);
				nullSelectionRectangle = SelectionRectangleNull.getNullSelectionRectangle();
				selectionRectangle = new SelectionRectangle(Color.BLUE, Color.cyan, camera, initClickPoint);
				selectionRectangleState = nullSelectionRectangle;
				
				inputController = new InputController("Boundary vertex select mode controller");
				
			}
			// Running polymorphic input commands
			@Override
			public void mousePressed(MouseEvent e) {
				boundaryVertexSelectSubMode.inputController.mousePressed(e); }
			@Override
			public void mouseDragged(MouseEvent e) {
				boundaryVertexSelectSubMode.inputController.mouseDragged(e); }
			@Override
			public void mouseReleased(MouseEvent e) {
				boundaryVertexSelectSubMode.inputController.mouseReleased(e); }
			@Override
			public void keyPressed(KeyEvent e) { 
				boundaryVertexSelectSubMode.inputController.keyPressed(e);	}
			@Override
			public void keyReleased(KeyEvent e) {
				boundaryVertexSelectSubMode.inputController.keyReleased(e); }
			@Override
			public void render(Graphics g) {
				boundaryVertexSelectSubMode.render(g);
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
			public ModeAbstract getSubMode() {
				return this.boundaryVertexSelectSubMode;
			}
			public DefaultBoundarySubMode getDefaultMode() {
				return this.defaultBoundarySubMode;
			}
			public ScaleBoundarySubMode getScaleMode() {
				return this.scaleBoundarySubMode;
			}
			@Override
			public String getModeName(){
				return this.boundaryVertexSelectSubMode.getModeName();
			}
			public void setBoundarySubMode(ModeAbstract newMode) {
				this.boundaryVertexSelectSubMode = newMode;
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
			
			public class SetDefaultMode extends KeyCommand {
				@Override
				public void onPressed() {
					setBoundarySubMode(defaultBoundarySubMode);
				}
				public void onReleased() {}
				public void onHeld() {}
			}
			public class SetScaleMode extends KeyCommand {
				@Override
				public void onPressed() {
					setBoundarySubMode(scaleBoundarySubMode);
				}
				public void onReleased() {}
				public void onHeld() {}
			}
			// ########################  INNER SUB MODE: DEFAULT MODE ########################					DEFAULT MODE, i.e. translating mode
			public class DefaultBoundarySubMode extends ModeAbstract {

				public DefaultBoundarySubMode() {
					this.modeName = "BoundaryVertexSelectDefaultMode";
					inputController = new InputController("Boundary vertex select mode Default controller");
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
					this.inputController.createKeyBinding(KeyEvent.VK_S, new SetScaleMode());
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
					// vvvv will be inside defaultmode's render
					Graphics2D g2 = (Graphics2D)g;	
					defaultRender(g2);
					// section to draw selected Vertex (if one is selected)
					g2.setColor(Color.GREEN);
					selectedVertices.drawClickableBox(g2, camera);
					g2.setColor(Color.BLUE);
					// vvvv section to draw selection rectangle
					selectionRectangleState.draw(g2, camera);
				}
				public class TranslateEvent extends MouseCommand{
					
					public void mousePressed() {
						
						initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
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
				public class AlignToXAxisEvent extends KeyCommand {
					@Override
					public void onPressed() {
						selectedVertices.alignToXAxis();
						refreshAllSurfaceLinesClosedShape(surfaceLines);
					}
					public void onReleased(){} public void onHeld() {}
				}
				public class CtrlVertexSelectLClickEvent extends MouseCommand{
					public void mousePressed() {
						checkForVertexShiftClick(camera.getWorldTranslationalPosition(editorMousePos));
					}
					public void mouseDragged() {
						//currentSelectedVertex.translate(camera.getLocalPosition(editorMousePos));
					}
					public void mouseReleased() {
					}
				
				} // end of ShiftVertexSelectLClickEvent inner class
				public class AlignToYAxisEvent extends KeyCommand {
					@Override
					public void onPressed() {
						selectedVertices.alignToYAxis();
						refreshAllSurfaceLinesClosedShape(surfaceLines);
					}
					public void onReleased(){} public void onHeld() {}
				}
				public class DeleteVerticesEvent extends KeyCommand {
					@Override
					public void onPressed() {
						
						removeVertex(selectedVertices);
					}
					public void onReleased() {} public void onHeld() {}
				}
				public class SelectionRectEvent extends MouseCommand {
					@Override
					public void mousePressed() {
						selectionRectangleState = selectionRectangle;
						initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
						selectionRectangleState.setInitialRectPoint();
					}
					@Override
					public void mouseDragged() {
						selectionRectangleState.translateEndPoint(camera.getWorldTranslationalPosition(editorMousePos));
					}
					@Override
					public void mouseReleased() {
						checkForVertexInSelectionRect(selectionRectangleState.getWrekt());
						selectionRectangleState.resetRect();
						selectionRectangleState = nullSelectionRectangle;
					}
				}
				public class SplitLineEvent extends KeyCommand {
					public void onPressed() {
						splitLine();
					}
					public void onReleased() {} public void onHeld() {}
				}
				// ****************** inner-inner classes for mouse behavior classes specific to vertex selecting
				// ****************** inner-inner classes for mouse behavior classes specific to vertex selecting
				public class VertexSelectLClickEvent extends MouseCommand{
					public void mousePressed() {

						checkForVertex(camera.getWorldTranslationalPosition(editorMousePos));
					}
					public void mouseDragged() {
						//currentSelectedVertex.translate(camera.getLocalPosition(editorMousePos));
					}
					public void mouseReleased() {}	
				} // end of VertexSelectLClickEvent inner class
			}	// end of Default Mode

			// ########################  INNER SUB MODE: SCALE MODE ########################					SCALE MODE
			public class ScaleBoundarySubMode extends ModeAbstract {
				
				
				public ScaleBoundarySubMode() {
					this.modeName = "Boundary Scale Mode";
					inputController = new InputController("Boundary vertex select Scale controller");
					this.inputController.createKeyBinding(KeyEvent.VK_D, new SetDefaultMode());
					this.inputController.createMouseBinding(MouseEvent.SHIFT_MASK, MouseEvent.BUTTON1, new CameraPanEvent());
					this.inputController.createMouseBinding(MouseEvent.BUTTON3, new ScaleEvent());
					this.inputController.createKeyBinding(KeyEvent.VK_ENTER, new ReplaceAndFinalizeBoundaryEvent());
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
					Graphics2D g2 = (Graphics2D)g;	
					defaultRender(g2);
					selectedVertices.drawClickableBox(g2, camera);
					g2.setColor(Color.BLUE);
				}
				
				public class ScaleEvent extends MouseCommand{

					public void mousePressed() {
						// update oldVertexListForScaling
						initClickPoint.setLocation((editorMousePos));
						selectedVertices.updateOldVertexPositions();
					}
					public void mouseDragged() {
						//double tempDistance = -(camera.getRelativeX(initClickPoint.getX()) - editorMousePos.getX());
						selectedVertices.scaleVertices(initClickPoint, editorMousePos, currentSelectedEntity.getPosition());

						refreshAllSurfaceLines(surfaceLines);
						closeShape(surfaceLines);
					}
					public void mouseReleased() {
						selectedVertices.updateOldVertexPositions();
					}
				}
			} // end of Scale mode
			
// ****************** inner-inner classes for mouse behavior classes specific to vertex selecting
// ****************** inner-inner classes for mouse behavior classes specific to vertex selecting
			

			public class VertexSelectLClickEvent extends MouseCommand{
				public void mousePressed() {
					
					checkForVertex(camera.getWorldTranslationalPosition(editorMousePos));
				}
				public void mouseDragged() {
					//currentSelectedVertex.translate(camera.getLocalPosition(editorMousePos));
				}
			} // end of VertexSelectLClickEvent inner class
			public class VertexSelectRClickEvent extends MouseCommand{
				public void mousePressed() {
					
					//checkForVertex(camera.getLocalPosition(e.getPoint()));
					//checkForVertex(camera.getLocalPosition(editorMousePos));
				}
				public void mouseDragged() {
					
					//currentSelectedVertex.translate(camera.getLocalPosition(editorMousePos));
				}
			} // end of VertexSelectRClickEvent inner class
			public class CtrlVertexSelectLClickEvent extends MouseCommand{

				public void mousePressed() {
					
					checkForVertexShiftClick(camera.getWorldTranslationalPosition(editorMousePos));
				}
				public void mouseDragged() {
					
					//currentSelectedVertex.translate(camera.getLocalPosition(editorMousePos));
				}

			} // end of ShiftVertexSelectLClickEvent inner class
			public class TranslateEvent extends MouseCommand{

				public void mousePressed() {
					
					initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
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
			}
			public class ScaleEvent extends MouseCommand{
				
				public void mousePressed() {
					// update oldVertexListForScaling
					initClickPoint.setLocation((editorMousePos));
					selectedVertices.updateOldVertexPositions();
				}
				public void mouseDragged() {
					//double tempDistance = -(camera.getRelativeX(initClickPoint.getX()) - editorMousePos.getX());
					selectedVertices.scaleVertices(initClickPoint, editorMousePos, currentSelectedEntity.getPosition());
					
					refreshAllSurfaceLines(surfaceLines);
					closeShape(surfaceLines);
				}
				public void mouseReleased() {
					selectedVertices.updateOldVertexPositions();
				}
			}
			public class SelectionRectEvent extends MouseCommand {

				@Override
				public void mousePressed() {
					
					selectionRectangleState = selectionRectangle;
					initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
					selectionRectangleState.setInitialRectPoint();
				}

				@Override
				public void mouseDragged() {
					
					selectionRectangleState.translateEndPoint(camera.getWorldTranslationalPosition(editorMousePos));
				}

				@Override
				public void mouseReleased() {
					checkForVertexInSelectionRect(selectionRectangleState.getWrekt());
					selectionRectangleState.resetRect();
					selectionRectangleState = nullSelectionRectangle;
				}

			}
			public class EscapeEvent extends KeyCommand {

				@Override
				public void onPressed() {
					selectedVertices.clearSelectedVertices();
				}
			}

			public class SplitLineEvent extends KeyCommand {
				public void onPressed() {
					splitLine();
				}
			}
			/*
			public class RetrieveVertsFromBoundaryEvent extends KeyCommand {
				@Override
				public void onPressed() {
					retrieveVertsFromBoundary(currentSelectedEntity.getColliderComposite());
				}
				public void onReleased(){} public void onHeld() {}
			}*/
			public class AlignToXAxisEvent extends KeyCommand {
				@Override
				public void onPressed() {
					selectedVertices.alignToXAxis();
					refreshAllSurfaceLinesClosedShape(surfaceLines);
				}
			}
			public class AlignToYAxisEvent extends KeyCommand {
				@Override
				public void onPressed() {
					selectedVertices.alignToYAxis();
					refreshAllSurfaceLinesClosedShape(surfaceLines);
				}
			}
			public class DeleteVerticesEvent extends KeyCommand {
				@Override
				public void onPressed() {
					
					removeVertex(selectedVertices);
				}
			}
			public class CloseShapeEvent extends KeyCommand {

				@Override
				public void onPressed() {
					
					refreshAllSurfaceLines(surfaceLines);
					closeShape(surfaceLines);
				}
			}
			public class ReplaceAndFinalizeBoundaryEvent extends KeyCommand {
				@Override
				public void onPressed() {
					
					replaceAndFinalizeBoundary();
				}
			}
			public class ResetBoundaryVerticesToDefaultEvent extends KeyCommand {
				@Override
				public void onPressed() {
					
					resetBoundaryVerticesToDefault();
				}
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
				inputController = new InputController("Boundary vertex place mode controller");
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
			public class RectangleBoundDrawEvent extends MouseCommand {

				@Override
				public void mousePressed() {
					
					tempRectBoundaryState = tempRectBoundary;
					initClickPoint.setLocation(camera.getWorldTranslationalPosition(editorMousePos));
					tempRectBoundaryState.setInitialRectPoint();
				}

				@Override
				public void mouseDragged() {
					
					tempRectBoundaryState.translateEndPoint(camera.getWorldTranslationalPosition(editorMousePos));
				}

				@Override
				public void mouseReleased() {
					
					//command to select vertices underneath box
					retrieveVertsFromRect(tempRectBoundary.getWrekt());
					tempRectBoundaryState.resetRect();
					tempRectBoundaryState = nullTempRectBoundary;
					setSubMode(getVertexSelectMode());
				}

			}
		}
	} // end of BoundaryMode inner class 


	private class NewEntityPopup extends JPopupMenu {
		private EntityStatic theEntity = null;
		private JMenu addEntityMenu = new JMenu("New Entity");
		private JMenuItem defaultItem = new JMenuItem("Default");
		private JMenuItem deleteComposite = new JMenuItem("Delete");
		private JPopupMenu popUp = new JPopupMenu();
		private NewEntityPopup() {
			super();
			// *** Also check if "Delete" menuItem should be enabled
			defaultItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// add circular follower
					createTestEntity();
				}
			});
			addEntityMenu.setEnabled(true);
		/*	deleteComposite.setEnabled(false);
			deleteComposite.addActionListener(new DeleteCompositeEvent());
			*/
			addEntityMenu.add(defaultItem);
			popUp.add(addEntityMenu);
			popUp.add(deleteComposite);
			
		}
		public void createTestEntity() {
			//section to create custom collision group
			board.collisionEngine.createColliderGroup("test");
			EntityStatic asteroid2 = new EntityStatic(camera.getLocalX(editorMousePos.x) , camera.getLocalY(editorMousePos.y));
			CompositeFactory.addGraphicTo(asteroid2, Sprite.Stillframe.missingSprite);
			Boundary bounds1 = new BoundaryCircular(40);
			CompositeFactory.addAngularComposite(asteroid2);
			CompositeFactory.addScriptTo(asteroid2, new EntityBehaviorScript.LinearFollowBehavior(asteroid2, ((TestBoard)board).player));
		//	asteroid2.addInitialColliderTo(bounds1);
			 
	        CompositeFactory.addRotationalColliderTo(
	        		asteroid2, 
	        		bounds1, 
	        		asteroid2.getAngularComposite()
	        		);
	        
			//CompositeFactory.addRigidbodyTo(asteroid2);
			CompositeFactory.addTranslationTo(asteroid2);
			/*Asteroid asteroid = new Asteroid( camera.getLocalX(editorMousePos.x) , camera.getLocalY(editorMousePos.y), 
					40, (TestBoard)board, 
					Asteroid.PRESET03);   */
			board.getCurrentScene().addEntity(asteroid2, "test");
			//((TestBoard)board).addFollowerToList(asteroid2);
		}
		 /** Just an overridden method from JPopup. Ignore */
		@Override
		public void show(Component invoker, int x, int y) {
			// TODO Auto-generated method stub
			popUp.show(invoker, x, y);
		}
	}
		
//////////////////////////////////////////////////////////////////////	
	public class OpenNewEntityPopup extends MouseCommand {
		public OpenNewEntityPopup() {
		}
		@Override 
		public void mousePressed() {
			NewEntityPopup newPopup = new NewEntityPopup();
			newPopup.show(board, editorMousePos.x, editorMousePos.y);
		}
	}
	public class CameraResetZoom extends KeyCommand {

		@Override
		public void onPressed() {
			camera.resetZoom();
		}	
	}
	
	public class CameraZoomInEvent extends KeyCommand {

		@Override
		public void onPressed() {
			//camera.addZoom(0.1);
			camera.quarterZoom();
		}
	}
	
	public class CameraZoomOutEvent extends KeyCommand {

		@Override
		public void onPressed() {
			//camera.addZoom(-0.1);
			camera.quadupleZoom();
		}
	}
	
	public class CameraPanEvent extends MouseCommand {
		public CameraPanEvent(){
		}
		@Override
		public void mousePressed() {
			//old version: vvvvvvv
			//else if (mode == EditorPanel.CAMERAPAN_MODE) {

			oldMousePanPos.setLocation( editorMousePos ); // sets temporary old mouse position reference
			oldCameraPos.setLocation( camera.getFocus() );
			//Set start positions 


			//this.camera.setFocusForEditor(oldMousePanPos.getX(), oldMousePanPos.getY());
			//mousePanDX = (editorMousePos.getX() - oldMousePanPos.getX());
			//mousePanDY = (editorMousePos.getY() - oldMousePanPos.getY());
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

				//camera.setFocus( editorMousePos );
				camera.setFocusForEditor( 
						oldCameraPos.getX() + camera.localDistance( oldMousePanPos.getX() - editorMousePos.getX() ), 
						oldCameraPos.getY() + camera.localDistance( oldMousePanPos.getY() - editorMousePos.getY()  )
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
		
		
	} // end of CameraPanMode inner class
	
	

}
