package editing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import entityComposites.EntityStatic;

@SuppressWarnings("serial")
public class MultipleSelectionsPopup extends JPopupMenu {

	private JPopupMenu popUp;
	//private ArrayList<JMenuItem> menuItemsList;
	private ArrayList<EntityStatic> entitiesUnderCursor;
	private EditorPanel editorPanelRef;
	private boolean ctrlModifier;
	
	public MultipleSelectionsPopup(ArrayList<EntityStatic> entitiesUnderCursorRef, EditorPanel editorPanelRef, boolean ctrlModifier) {
		super();
		//menuItemsList = new ArrayList<>();
		entitiesUnderCursor = entitiesUnderCursorRef;
		this.editorPanelRef = editorPanelRef;
		this.ctrlModifier = ctrlModifier;
		popUp = new JPopupMenu();
		populateMenuItems();
	
	}
	
	public void populateMenuItems() {
		for (EntityStatic ent : entitiesUnderCursor) {
			//menuItemsList.add(new JMenuItem(new MyAction(currentEntity)));  will be something like this
			JMenuItem itemToAdd = new JMenuItem(new MenuItemActionListener(ent));
			itemToAdd.setText(ent.name);
			//menuItemsList.add(itemToAdd);
			popUp.add(itemToAdd);
		}
	}
	
	 /** Just an overridden method from JPopup. Ignore */
	@Override
	public void show(Component invoker, int x, int y) {
		// TODO Auto-generated method stub
		popUp.show(invoker, x, y);
	}
	//my actionlistener
	@SuppressWarnings("serial")
	private class MenuItemActionListener extends AbstractAction {
		private EntityStatic currentEntity;
		private MenuItemActionListener(EntityStatic currentEntity) {
			this.currentEntity = currentEntity;
		}
		public void actionPerformed(ActionEvent e) {
			editorPanelRef.getEntitySelectMode().addSelectedEntity(currentEntity);
		}
		
	}
}
