package editing;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import engine.BoardAbstract;
import entityComposites.Entity;
import entityComposites.EntityComposite;
import entityComposites.EntityStatic;
import entityComposites.TranslationComposite;

public class BrowserTreePanel extends JPanel {
	
	private JTree tree;
	private DefaultTreeModel defaultModel;
	private EditorPanel editorPanelRef;
	protected BoardAbstract board;
	protected DefaultMutableTreeNode sceneRoot;
	protected DefaultMutableTreeNode entitiesRoot;
	public BrowserTreePanel(LayoutManager layout, EditorPanel editorPanelRef, BoardAbstract boardRef) {
		super(layout);
		this.editorPanelRef = editorPanelRef;
		this.board = boardRef;
		sceneRoot = new DefaultMutableTreeNode("Current Scene");
		entitiesRoot = new DefaultMutableTreeNode("Entities");
		sceneRoot.add(entitiesRoot);
		defaultModel = new DefaultTreeModel(sceneRoot);
		tree = new JTree(defaultModel);
		//refreshTree();
		populateEntityFolder(entitiesRoot, board.listCurrentSceneEntities());
		
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionEventHandler());
		tree.setFocusable(false);
		
		BasicTreeUI basicTreeUI = (BasicTreeUI) tree.getUI();
		basicTreeUI.setRightChildIndent(5); 
		basicTreeUI.setLeftChildIndent(1);
		
		this.add(tree);
		this.setFocusable(false);
		
	}
	
	//TREE SECTION
		public void notifyTreeAddedEntity(EntityStatic newEnt) {
			DefaultMutableTreeNode newEntityNode = createSingleEntityNodeFolder(newEnt);
			defaultModel.insertNodeInto(newEntityNode, entitiesRoot, entitiesRoot.getChildCount());
			System.err.println("From notifyTreeAddedEntity()--- added "+newEnt.name+" to tree.");
		}
		
		/**Tells the browser tree that relationship has changed, and updates the node display accordingly.
		 * Will be the connection that outside methods have when an actual parent change happens on entities.
		 * This will likely be called exclusively by CompositeFactory.makeChildOfParent()  */
		public void notifyParentChildRelationshipChanged(EntityStatic child, EntityStatic parent) {
			DefaultMutableTreeNode parentNode, childNode;
			parentNode = containsEntity(parent);
			childNode = containsEntity(child);
			if (parentNode == null || childNode == null) {
				System.err.println("BrowserTree was unable to modify parents for child "+child.name+" and parent "+parent.name);
				return;  //if either of these nodes can't be found, there's no way to update the tree so break out.
			}
			defaultModel.removeNodeFromParent(childNode);
			System.err.println("removed "+childNode.getUserObject().toString()+"from parent.");
			defaultModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
			System.err.println("inserted "+childNode.getUserObject().toString()+"to parent "+parentNode.getUserObject().toString());
		}
		/**
		 * Note** Only creates the current entity node. Doesn't create the main "Entities" folder in the same way that {@link #createCompositesNodeFolder(EntityStatic)} does.
		 * @param entity The entity that will be stored in this corresponding node.
		 * @return The branch node for the current entity. Also contains "Composites" and may contain more branches for its children*/
		private DefaultMutableTreeNode createSingleEntityNodeFolder(EntityStatic entity) {
			DefaultMutableTreeNode currentEntityNode = new DefaultMutableTreeNode(entity);
			//append Composites folder to Entities folder
			//currentEntityNode.add(createCompositesNodeFolder(entity));
			defaultModel.insertNodeInto(createCompositesNodeFolder(entity), currentEntityNode, currentEntityNode.getChildCount());
			return currentEntityNode;
			
			// vvvvv Broken stuff delete later vvvvv
			//FIXME EntityStatic.isParent() doesn't work
			/*if (entity.isParent()){
				EntityStatic[] children = entity.getChildrenEntities();
				for (EntityStatic thisChildEntity: children) {
					currentEntityNode.add(createSingleEntityNodeFolder(thisChildEntity));
				}
			}
			// FIXME ask Matt to make some utility functions for easily navigating parent/child relationship. */
		}
		private void populateEntityFolder(DefaultMutableTreeNode newRoot, EntityStatic[] entityListRef) {
			//DefaultMutableTreeNode entireEntityFolder = new DefaultMutableTreeNode("Entities");
			//will make as many folders(nodes) as there are Entities in the list parameter
			int size = entityListRef.length;
			DefaultMutableTreeNode currentFolder;
			for (int i = 0; i < size; i++) {
				currentFolder = createSingleEntityNodeFolder(entityListRef[i]);
				//newRoot.add(currentFolder);
				defaultModel.insertNodeInto(currentFolder, newRoot, newRoot.getChildCount());
			}
		}
		
		/** @param entity The entity that contains the composites.
		 * @return The branch node "Composites" that will contain the entity's composites */
		private DefaultMutableTreeNode createCompositesNodeFolder(EntityStatic entity) {
			DefaultMutableTreeNode newCompositesFolder = new DefaultMutableTreeNode("Composites");
			EntityComposite[] entityCompositeArray = new EntityComposite[]{entity.getAngularComposite(), entity.getColliderComposite(),
					entity.getGraphicComposite(), entity.getRotationComposite(), entity.getTranslationComposite() };
			//create the leaf nodes from the array
			for (int i = 0; i < entityCompositeArray.length; i++) {
				newCompositesFolder.add(new DefaultMutableTreeNode(entityCompositeArray[i]));
			}
			return newCompositesFolder;
		}
		
		/**--WARNING--Not sure if I should use this method */
		@Deprecated
		public void refreshTree() {
			try {
				populateEntityFolder(entitiesRoot, board.listCurrentSceneEntities());
				//sceneRoot.removeAllChildren(); //this shouldn't be how it's done
				//defaultModel.insertNodeInto(entitiesRootFolder, sceneRoot, sceneRoot.getChildCount());
				//sceneRoot.add(entitiesRootFolder);
//				tree.expandPath(new TreePath(entitiesRootFolder.getPath()));
				tree.scrollPathToVisible(new TreePath(entitiesRoot.getPath()));
				//tree.scrollPathToVisible(new TreePath(new Object[]{"Current Scene", "Entities"}));
				defaultModel.reload();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	public class TreeSelectionEventHandler implements TreeSelectionListener{
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			//DefaultMutableTreeNode testNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
			//this is still in raw Object format. Must be cast
			if (currentNode == null) return;
			Object objectInsideNode = currentNode.getUserObject();
			if (objectInsideNode instanceof Entity) {
				EntityStatic nodeIsEntity = (EntityStatic)objectInsideNode;
				System.out.println("*** IS AN ENTITY ***");
				/** TEST AREA **/
				editorPanelRef.setMode(editorPanelRef.getEntitySelectMode());
				editorPanelRef.selectSingleEntityGUIHouseKeeping();
				//sets Board's current entity
				editorPanelRef.getEntitySelectMode().addSelectedEntity(nodeIsEntity);
			}
			else if (objectInsideNode instanceof EntityComposite) {
				System.out.println("*** IS A COMPOSITE ***");
				if (objectInsideNode instanceof TranslationComposite) {
					System.out.println("*** IS TRANSLATEABLECOMPOSITE ***");
				}
			}

		}	
	} //end of TreeSelectionEventHandler class
	public void setNewRoot(DefaultMutableTreeNode newRoot) {
		defaultModel.setRoot(newRoot);
	}
	/** Will search entire tree for a node that contains this entity */
	public DefaultMutableTreeNode containsEntity(EntityStatic ent){
		DefaultMutableTreeNode possibleNodeContainingEnt;
		Enumeration e = ((DefaultMutableTreeNode)entitiesRoot).breadthFirstEnumeration();
		if (e.hasMoreElements())
			e.nextElement();
		while (e.hasMoreElements()) {
			possibleNodeContainingEnt = (DefaultMutableTreeNode)e.nextElement();
			if (possibleNodeContainingEnt.getUserObject() == ent)
				return possibleNodeContainingEnt;
		}
		return null;
	}
	/*
	 * //from website:
	public DefaultMutableTreeNode searchNode(String nodeStr) {
    DefaultMutableTreeNode node = null;
    Enumeration e = m_rootNode.breadthFirstEnumeration();
    while (e.hasMoreElements()) {
      node = (DefaultMutableTreeNode) e.nextElement();
      if (nodeStr.equals(node.getUserObject().toString())) {
        return node;
      }
    }
    return null;
}

	 */
	
}
