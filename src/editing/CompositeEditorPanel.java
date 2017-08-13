package editing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;

import entityComposites.*;
import sprites.Sprite;

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
	private EntityComposite currentComposite;
	private Sprite currentSelectedSprite;
	private ArrayList<JComponent> listOfActiveComponents;
	//shared components
	private JLabel compositeName;
	private JCheckBox enabledCheckBox;
	private EnabledCheckBoxListener enabledCheckBoxListener;
	
	//variable components
	private JSlider numericSlider;
	private JComboBox extraComboBox;
	public CompositeEditorPanel()  {
		super();
		initializeGUI();
	}
	public void initializeGUI() {
		setPreferredSize(new Dimension(190, 200));
//		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBorder(BorderFactory.createTitledBorder("Composite Editor"));
		enabledCheckBoxListener = new EnabledCheckBoxListener();
		compositeName = new JLabel("");
		enabledCheckBox = new JCheckBox("Enabled");
		enabledCheckBox.setFocusable(false);
		enabledCheckBox.addItemListener(enabledCheckBoxListener);
	}
	public void setCurrentComposite(EntityComposite newCurrentComposite) {
		this.currentComposite = newCurrentComposite;
	}
	public EntityComposite getCurrentComposite() {
		return this.currentComposite;
	}
	
	public void runTemplate() {
		if (this.currentComposite == null) return;
		else {
			if (currentComposite instanceof GraphicComposite) {
				graphicsPanelTemplate();
				System.err.println("Setting graphics panel...");
			}
			else if(currentComposite instanceof AngularComposite) {
				angularCompositeTemplate();
				System.err.println("Setting angularcomposite panel...");
			}
			else if(currentComposite instanceof Collider) {
				colliderCompositeTemplate();
				System.err.println("Setting collider panel...");
			}
			else if(currentComposite instanceof TranslationComposite) {
				translationCompositeTemplate();
				System.err.println("Setting translation panel...");
			}
			else if(currentComposite instanceof DynamicRotationComposite) {
				rotateableCompositeTemplate();
				System.err.println("Setting dynamicrotation panel...");
			}
		}
	}
	//factories
	/**There will be 5 templates. Will need helper methods to remove all components*/
	public void graphicsPanelTemplate() {
		this.removeAll();
		createSharedComponents();
		
		//FIXME this whole part vvvvvv will change once multiple sprites are supported.
		currentSelectedSprite = ((GraphicComposite)currentComposite).getSprite();
		JComboBox<Sprite> spriteComboBox = new JComboBox<Sprite>(new Sprite[]{currentSelectedSprite});
		//spriteComboBox.addItemListener(new SpriteComboBoxHandler());
		spriteComboBox.addActionListener(new SpriteComboBoxHandler());
		//add slider
		double defaultTransparencyFloat = currentSelectedSprite.getAlpha() * 100;
		int defaultTransparency = (int)defaultTransparencyFloat;
		System.out.println("defaultTransparencyFloat: " + defaultTransparencyFloat);
		System.out.println("defaultTransparency: " + defaultTransparency);
		System.out.println("currentSelectedSprite.getAlpha() transparency: " + currentSelectedSprite.getAlpha());
		JSlider transparencySlider = new JSlider(JSlider.HORIZONTAL,0,100,defaultTransparency);
		transparencySlider.setMajorTickSpacing(25);
		transparencySlider.setMinorTickSpacing(5);
		transparencySlider.setPaintTicks(true);
		transparencySlider.setPaintLabels(true);
		transparencySlider.addChangeListener(new TransparencySliderHandler());
		transparencySlider.setBorder(BorderFactory.createEtchedBorder());
		transparencySlider.setFont(new Font("Serif",Font.PLAIN, 10));
		this.add(spriteComboBox);
		this.add(transparencySlider);
		this.repaint();
		//TEST AREA DELETE THIS SHIT
		//((GraphicComposite)this.currentComposite).getSprite().setAlpha(0.5);
		//this.revalidate();
	}
	
	public void angularCompositeTemplate() {
		this.removeAll();
		createSharedComponents();
		
		this.revalidate();
		this.repaint();
	}
	public void colliderCompositeTemplate() {
		this.removeAll();
		createSharedComponents();

		this.revalidate();
		this.repaint();
	}
	public void translationCompositeTemplate() {
		this.removeAll();
		createSharedComponents();
		
		this.revalidate();
		this.repaint();
	}
	public void rotateableCompositeTemplate() {
		this.removeAll();
		createSharedComponents();
		
		this.revalidate();
		this.repaint();
	}
	public void createSharedComponents(){
		//test area
		EnabledCheckBoxListener enabledCheckBoxListener = new EnabledCheckBoxListener();
		//shared components
		// VVVVVV won't work because not all composites have reference to owner entity
		//compositeName.setText(currentComposite.getCompositeName());
		//this.add(compositeName);
		//add back to this panel.
		this.add(enabledCheckBox);
		enabledCheckBox.setSelected(checkIfCompositeExists());
		this.revalidate();
	}
	public boolean checkIfCompositeExists(){
		if (currentComposite.exists() == true)
			return true;
		else
			return false;
	}
	private class EnabledCheckBoxListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED){
				//FIXME Add code to Enable the composite from nothing. Will need to refer to owner entity
				//currentComposite.disableComposite();
			}
			else if (e.getStateChange() == ItemEvent.DESELECTED){
				currentComposite.disableComposite();
			}
		}
	}
	//Might use later vvvvv instead of ActionListener
	/*private class SpriteComboBoxHandler implements ItemListener {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Sprite selectedSprite = (Sprite)e.getItem();
					System.err.println("Was able to select sprite: " +selectedSprite.getPathName());
				}
			}
		}*/
	//FIXME I'm only using ActionListener because ItemListener won't work with only one sprite.
	//When there are multiple sprites that one may be better. Although, ActionListener may not be the worst.
	//Keep in mind though ActionListener fires even when the already selected item is clicked again, while ItemListener doesn't.
	private class SpriteComboBoxHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox<?> receiver = (JComboBox<?>)e.getSource();
			Sprite selectedSprite = (Sprite)receiver.getSelectedItem();
			System.err.println("Was able to select sprite: " +selectedSprite.getPathName());
		}
	}

	private class TransparencySliderHandler implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider receiver = (JSlider)e.getSource();
			int transparencyInt = receiver.getValue();
			double newTransparency = (double)transparencyInt * .01;
			if (currentSelectedSprite != null) {
				currentSelectedSprite.setAlpha(newTransparency);
			}
/*			if (receiver.getValueIsAdjusting() == false) {
				int transparencyInt = receiver.getValue();
				double newTransparency = (double)transparencyInt * .01;
				if (currentSelectedSprite != null) {
					currentSelectedSprite.setAlpha(newTransparency);
				}
			}
*/		}
		
	}
}
