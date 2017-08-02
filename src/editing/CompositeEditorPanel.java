package editing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

/***
 * ROUGH OUTLINE OF TASKS
 * 
 * -Be able to "reconstruct" this panel. To do this, must decide whether I want to reconstruct the actual components,
 *    or just reassign their fields and actions. Depends on which is more efficient and feasible.
 * -Make factories for each component, shared and variable. Then make a Command pattern that does these in chunks.
 * -Make helper methods to access the current composite
 * >> (Should there be more than one composite reference stored in this class? Since there are 5 composites editable per entity,
 * >> (might be worth it to store each of the composites. Assuming I want to follow the flyweight way of doing it)
 * -
 * Question: will it work fine to initialize all this stuff right when clicking on the composite node? If I have it created 
 * 	and then stored, it won't have to reconstruct everything. It will flyweight it, and reload this panel when it's needed. 
 * 	This will also create some memory overhead though as it's storing a panel that might not be used again.
 */
@SuppressWarnings({ "serial", "unused" })
public class CompositeEditorPanel extends JPanel {
	
	//shared components
	private JLabel entityName;
	private JLabel compositeName;
	private JCheckBox enabledCheckBox;
	private JComboBox compositeState;
	
	//variable components
	private JSlider numericSlider;
	private JComboBox extraComboBox;
	public CompositeEditorPanel()  {
		super();
		initializeGUI();
	}
	public void initializeGUI() {
		setPreferredSize(new Dimension(190, 200));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(new JLabel("EntityName, CompositeName"));
		add(new JCheckBox("checkbox",true));
		add(new JLabel("Composite State:"));
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>() {
			@Override
			public void addElement(String anObject) {
				super.addElement(anObject);
				System.out.println("Overrode addElement");
		    }
		});
		((DefaultComboBoxModel<String>)comboBox.getModel()).addElement("String1");
		((DefaultComboBoxModel<String>)comboBox.getModel()).addElement("String2");
		((DefaultComboBoxModel<String>)comboBox.getModel()).addElement("String3");
		add(comboBox);
		setBorder(BorderFactory.createTitledBorder("Composite Editor"));
		// TODO Auto-generated constructor stub
		
		/** LITTLE DUMB TEST AREA, DELETE AFTER **/
		JComboBox<String> comboTest = new JComboBox<String>(new String[]{"TEST", "ASDF"});
		add(comboTest);
		comboTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox receiver = (JComboBox)e.getSource();
				String string = (String)receiver.getSelectedItem();
				System.err.println(string);
				
			}
		});
		comboTest.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
		
		
		
		
		
	}
}
