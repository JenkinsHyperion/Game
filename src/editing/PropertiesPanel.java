package editing;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import engine.*;



//HUGE NOTE:
//I WILL BE CHANGING THIS INTO A PANEL, MAKING IT EXTEND EDITORPANEL.JAVA TO OBTAIN ALL ITS FIELDS,
//AND PLACING THIS PANEL AT THE BOTTOM OF EDITORPANEL. IT WILL BE COLLAPSED WHEN NOTHING IS SELECTED.
public class PropertiesPanel extends JPanel implements ActionListener,ItemListener{
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
	EditorPanel ep;
	BoardAbstract board;
	
	public PropertiesPanel(EditorPanel editorPanelRef, BoardAbstract board2) { 		 //constructor 
		this.ep = editorPanelRef;
		this.board = board2;
		//setPreferredSize(ep.propPanelDefaultSize);
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
		//setBackground(Color.GRAY);
		add(createMainPropertiesPanel());
	}
	//this panel will act as the content pane for PropertiesFrame
	protected JPanel createMainPropertiesPanel() {
		
		//int thisPropertyType;   // will be of type Property.STATE, Property.POS, or Property.TEXT, etc.
		//mainPropertiesPanel will be the main panel inside of PropertiesFrame
		JPanel mainPropertiesPanel = new JPanel();

		//initialize drop down box for list of Properties
		propertiesListComboBox = new JComboBox<>(ep.listOfPropLists.get(ep.currentEntIndex).getPropertiesAsString());
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
		mainPropertiesPanel.add(comboBoxPanel, BorderLayout.PAGE_START);
		mainPropertiesPanel.add(cardsPanel, BorderLayout.CENTER);
		mainPropertiesPanel.setPreferredSize(new Dimension(200,125));
		//mainPropertiesPanel.setBackground(Color.GRAY);
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
		/*String command = e.getActionCommand();
		//for the radio button panels
		if (command.equals("COL_TRUE")) {
			ep.getThisProperty(Property.COL_STATE).setEntityCollidableState(ep.getCurrentSelectedEntity(), true);
			updateCurrentStatusLabel(Property.COL_STATE);
		}
		else if(command.equals("COL_FALSE")) {
			ep.getThisProperty(Property.COL_STATE).setEntityCollidableState(ep.getCurrentSelectedEntity(), false);
			updateCurrentStatusLabel(Property.COL_STATE);
		}
		else if(e.getSource() == xPosTextField){
			
			 * Here, set the xPosition field of the current propertyList's xPos property 
			ep.getThisProperty(Property.XPOS).setEntityXpos(ep.getCurrentSelectedEntity(), Integer.parseInt(command));
			updateCurrentStatusLabel(Property.XPOS);
		}
		else if(e.getSource() == yPosTextField){
			
			 * Here, set the yPosition field of the current propertyList's yPos property 
			ep.getThisProperty(Property.YPOS).setEntityYpos(ep.getCurrentSelectedEntity(), Integer.parseInt(command));
			updateCurrentStatusLabel(Property.YPOS);
		}
		else if(e.getSource() == entNameTextField){
			
			 * Here, set the name field of the current propertyList's yPos property 
			//INPUT VALIDATION: IF DUPLICATE NAME FOUND, DON'T TAKE INPUT
			if (checkDuplicateName(ep.listOfPropLists, command) == true) {  //found duplicate name
				JOptionPane.showMessageDialog(null, "Entity name already taken. Try again.");				
			} 
			else {
				ep.getThisProperty(Property.ENTNAME).setEntityName(ep.getCurrentSelectedEntity(), command);
				updateCurrentStatusLabel(Property.ENTNAME);
				ep.updateAllEntitiesComboBox();				
			}
		}
		revalidate();
		repaint(); */
	}
	private boolean checkDuplicateName(ArrayList<PropertiesList> propList, String nameCheck) {
		int duplicatesFound = 0;
		int index = 0;
		for (PropertiesList prop : propList) {
			System.out.println(prop.getProperty(Property.ENTNAME).getEntityName(board.getStaticEntities().get(index)));
			if (prop.getProperty(Property.ENTNAME).getEntityName(board.getStaticEntities().get(index)).equals(nameCheck)){
				duplicatesFound++;
				System.out.println("In checkDuplicateName(): found duplicate " + duplicatesFound + " times");
			}
			index++;
		}
		if (duplicatesFound > 0)
			return true;
		else
			return false;
	}
	public void updateCurrentStatusLabel(int propType) {
		/*if (propType == Property.COL_STATE) {
			collisionStatusLabel.setText(String.valueOf(ep.getThisProperty(propType).getEntityCollidableState(ep.getCurrentSelectedEntity())) );
		}
		else if (propType == Property.XPOS)
			xPosStatusLabel.setText(Integer.toString(ep.getThisProperty(propType).getEntityXpos(ep.getCurrentSelectedEntity())) );
		else if (propType == Property.YPOS)
			yPosStatusLabel.setText(Integer.toString(ep.getThisProperty(propType).getEntityYpos(ep.getCurrentSelectedEntity())) );
		else if (propType == Property.ENTNAME)
			entNameStatusLabel.setText(ep.getThisProperty(propType).getEntityName(ep.getCurrentSelectedEntity()) );*/
	}
// ###### Panel creation section  ###########
	// will create true/false (radiobutton), text field, and others here:
	protected JPanel createRadioPanel(){
		JPanel radioPanel = new JPanel(new BorderLayout());
		/*ButtonGroup buttonGroup = new ButtonGroup();			
		JRadioButton rbTrue = new JRadioButton("True");
		JRadioButton rbFalse = new JRadioButton("False");
		buttonGroup.add(rbTrue);
		buttonGroup.add(rbFalse);
		rbTrue.setActionCommand("COL_TRUE");
		rbTrue.addActionListener(this);		
		rbFalse.setActionCommand("COL_FALSE");
		rbFalse.addActionListener(this);	
		//initial check to correctly choose true or false for property
		if (ep.getThisProperty(Property.COL_STATE).getEntityCollidableState(ep.getCurrentSelectedEntity()) == true) {
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
		radioPanel.add(labelPanel, BorderLayout.PAGE_END); */
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
		/*collisionStatusLabelcursed = new JLabel();
		String entTypeString = ep.getThisProperty(Property.ENTTYPE).getEntityType(ep.getCurrentSelectedEntity());
		entTypePanel.add(new JLabel(entTypeString), BorderLayout.PAGE_END);*/
		return entTypePanel;
	}
} // end of PropertiesPanel class
