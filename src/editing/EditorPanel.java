package editing;

import javax.swing.*;
import sprites.*;
import entities.*;
import entityComposites.Collidable;
import entityComposites.NonCollidable;
import physics.Boundary;
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
	public static final int CAMERAPAN_MODE = 3;
	//Mouse and positioning fields
	protected boolean mouseClick = false;
	private Point clickPosition;
	private Point editorMousePos;
	private Point oldMousePanPos; // the reference point of last click position for mouse camera panning
	// oldMousePanPos will only refresh when shift is held, and then mouse is dragged. Purely for the panning
	
	 // the distance from reference point and current point
	private double mousePanDX = 0f;
	private double mousePanDY = 0f;
	public int clickPositionXOffset;
	public int clickPositionYOffset;
	public int mode;
	public int modeBuffer; // to store most recent mode a quick-mode change happens (such as shift pressed for panning)
	private String newEntityPath;

	//Keyboard fields
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

	protected Board board;
	private WorldGeometry worldGeom;
	private Sprite ghostSprite; 

    protected EntityStatic currentSelectedEntity;
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
		this.board = boardInstance;
		oldMousePanPos = new Point();
		mode = EditorPanel.DEFAULT_MODE;
		worldGeom = new WorldGeometry(this, boardInstance);
		newEntityPath = "";
		selectedBox = new Rectangle();
		//entityPlacementMode = false;
		editorMousePos = new Point();
		ghostSprite = SpriteNull.getNullSprite();
        clickPosition = new Point(0,0);

		//set default selected entity so it's not null

		//setSelectedEntityThruEditor(board.getStaticEntities().get(0)); //NEEDS ZERO ARRAY SIZE CHECK

		setCurrentSelectedEntity(board.getStaticEntities().get(0)); 
		
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
		worldGeomButton = new JButton("World Geom");
		worldGeomButton.setFocusable(false);
		worldGeomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mode = WORLDGEOM_MODE;
			}
		});
		
		// inline panel for button
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBackground(Color.GRAY);
	    buttonPanel.setBorder(BorderFactory.createTitledBorder("buttonPanelTest"));
		//buttonPanel.setPreferredSize(new Dimension(190, 50));		
		buttonPanel.add(deleteEntButton);
		buttonPanel.add(worldGeomButton);

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
			//JComboBox cb = (JComboBox)e.getSource();
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
				setCurrentSelectedEntity(board.getStaticEntities().get(currentEntIndex));
				//board.
				createAndShowPropertiesPanel(board);
				setSelectedEntityNameLabel("Selected: " + currentSelectedEntity.name);
				setEntityCoordsLabel(String.format("Coords of Selected Entity: %s,%s", currentSelectedEntity.getX(), currentSelectedEntity.getY()));
				//sends code from here over to Board to let it draw this entity's selection box
				selectedBox.setSize(currentSelectedEntity.getEntitySprite().getImage().getWidth(null),
															currentSelectedEntity.getEntitySprite().getImage().getHeight(null) );
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
				if (currentSelectedEntity != null) {  	// there is entity under cursor
	  				/*selectedBox.setSize(currentSelectedEntity.getEntitySprite().getImage().getWidth(null),
	  									currentSelectedEntity.getEntitySprite().getImage().getHeight(null) ); */	  				
					//SidePanel.setSelectedEntityName("Selected: " + currentSelectedEntity.name);
					setSelectedEntityNameLabel("Selected: " + currentSelectedEntity.name);
					setEntityCoordsLabel("Coords. of selected entity: " + currentSelectedEntity.getX() + ", " + currentSelectedEntity.getY());
					//get offsets
					clickPositionXOffset = e.getX() - currentSelectedEntity.getX() ;
					clickPositionYOffset = e.getY() - currentSelectedEntity.getY() ;
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
			else if (mode == EditorPanel.CAMERAPAN_MODE) {
				oldMousePanPos.setLocation(e.getPoint()); // sets temporary old mouse position reference
				//board.camera.setFocusForEditor(oldMousePanPos.getX(), oldMousePanPos.getY());
				board.camera.setFocusForEditor(oldMousePanPos.getX(), oldMousePanPos.getY());
/*				mousePanDX = (e.getX() - oldMousePanPos.getX());
				mousePanDY = (e.getY() - oldMousePanPos.getY());*/
			}
		}
	}
	public void mouseDragged(MouseEvent e) {
		if (mode == EditorPanel.DEFAULT_MODE) {
			setMousePosLabel(String.format("Mouse Click: %s, %s", e.getX(), e.getY()));

			if (currentSelectedEntity != null) {
				currentSelectedEntity.setX(e.getX() - clickPositionXOffset);
				currentSelectedEntity.setY(e.getY() - clickPositionYOffset);
				setEntityCoordsLabel("Coords. of selected entity: " + currentSelectedEntity.getX() + ", " + currentSelectedEntity.getY());
			}
		}
		else if (mode == EditorPanel.CAMERAPAN_MODE) {//keypressSHIFT == true: holding down shift key, ready to pan
			//mousePanDX = e.getX() - oldMousePanPos.getX()
			//mousePanDY = e.getY() - oldMousePanPos.getY()
			//camera.translate (-mousePanDX, -mousePanDY) or something
			// ^^^ must be negative because camera will pan in direction opposite the mouse drag
			/*mousePanDX = (e.getX() - oldMousePanPos.getX());
			mousePanDY = (e.getY() - oldMousePanPos.getY());*/
			//board.camera.translate(mousePanDX, mousePanDY);
			//board.camera.setFocus(e.getPoint());
			board.camera.setFocusForEditor(oldMousePanPos.getX()-(e.getX()-oldMousePanPos.getX()), 
										oldMousePanPos.getY()-(e.getY()-oldMousePanPos.getY())
										);
		}
	}
	public void mouseMoved(MouseEvent e){
		setEditorMousePos(e.getX(), e.getY());
	}
	public void mouseReleased(MouseEvent e) {	
		
		if ( currentSelectedEntity == null) {
			deselectAllEntities();
		}
		if (mode == EditorPanel.ENTPLACEMENT_MODE) {
			mode = EditorPanel.DEFAULT_MODE;
		}
		else if (mode == EditorPanel.CAMERAPAN_MODE) {
			oldMousePanPos.setLocation(e.getPoint());
		}

		mouseClick = false;
	}
	
	// ############ KEY HANDLING SECTION ###########
	public void keyPressed(KeyEvent e) {

		int key = e.getKeyCode();
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
			modeBuffer = mode; // save the most recent mode, will switch back when shift is released
			mode = CAMERAPAN_MODE;
		}
		
		
		board.camera.translate(pan_dx, pan_dy);
	}	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
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
		board.camera.translate(pan_dx, pan_dy);
	}
    //END OF KEYHANDLING SECTION 
    
  	 public void drawEditorSelectedRectangle(EntityStatic stat, Graphics g) {
 	    if (currentSelectedEntity != null) {	
 	    	if (stat == currentSelectedEntity) {
 	    		int width = stat.getEntitySprite().getImage().getWidth(null);
 	        	int height = stat.getEntitySprite().getImage().getHeight(null);
 	    		Graphics2D g2 = (Graphics2D)g;
 	        	g2.setColor(Color.BLUE);
 	        	Stroke oldStroke = g2.getStroke();
 	        	float thickness = 2;
 	        	g2.setStroke(new BasicStroke(thickness));
 	    		g2.drawRect(stat.getXRelativeTo(board.camera) + stat.getSpriteOffsetX(), stat.getYRelativeTo(board.camera) + stat.getSpriteOffsetY(),width,height);
 	    		g2.setStroke(oldStroke);
 	    	}
 	    }
     }
   	public void checkForSelection(Point click) { //redundant
  		setCurrentSelectedEntity(clickedOnEntity(click));
  		//currentSelectedEntity = clickedOnEntity(click);

  		if (currentSelectedEntity != null)
  			board.currentDebugEntity = currentSelectedEntity;

  	}
  	public EntityStatic clickedOnEntity(Point click) {
  		int counter = 0;
  		for (EntityStatic entity : board.getStaticEntities()) 
  		{
  			
	 		if (entity.getEntitySprite().hasSprite()){ //if entity has sprite, select by using sprite dimensions
	  			selectedBox.setLocation(entity.getXRelativeTo(board.camera) + entity.getSpriteOffsetX(), entity.getYRelativeTo(board.camera) + entity.getSpriteOffsetY());

	  			selectedBox.setSize(entity.getEntitySprite().getImage().getWidth(null), entity.getEntitySprite().getImage().getHeight(null) );
	  			if (selectedBox.contains(click)) 
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
  		//nothing was found under cursor: 
  		enableEditPropertiesButton(false);
  		minimizePanels();
  		return null;
  	}
	public void deselectAllEntities() {
  		setCurrentSelectedEntity(null);
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
			
			//newEnt = new Platform(x, y, offsetX, offsetY, path);
			//
			newEnt = EntityFactory.buildPlatform( x , y , offsetX , offsetY , path );
		}
		else if (path.toLowerCase().contains("ground")) {
			newEnt = EntityFactory.buildStaticEntity(x,  y, EntityFactory.COLLIDABLE);
			Collidable collidable = new Collidable(newEnt, new Boundary.Box(446,100,-223,-50) );
			newEnt.setCollisionProperties( collidable );
			newEnt.loadSprite("ground_1.png" , -223 , -53 );
		}
		else if (path.toLowerCase().contains("grass")) {
			//newEnt = new Grass(x, y, offsetX, offsetY, path);
			//
			newEnt = EntityFactory.buildStaticEntity( x , y , EntityFactory.INTANGIBLE );
			newEnt.setCollisionProperties(NonCollidable.getNonCollidable());
			newEnt.loadSprite("grass01.png");
		}
		else {
			newEnt = new EntityStatic(x, y);
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
	public Rectangle getSelectedBox(){
		return selectedBox;
	}
	public void setCurrentSelectedEntity(EntityStatic newSelectedEntity){
		currentSelectedEntity = newSelectedEntity;
	}
	public EntityStatic getCurrentSelectedEntity() {
		return currentSelectedEntity; 
	}
	public WorldGeometry getWorldGeom() {
		return this.worldGeom;
	}
	

}
