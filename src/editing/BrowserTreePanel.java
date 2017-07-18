package editing;

import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
	public BrowserTreePanel(LayoutManager layout, EditorPanel editorPanelRef, BoardAbstract boardRef) {
		super(layout);
		this.editorPanelRef = editorPanelRef;
		this.board = boardRef;
		sceneRoot = new DefaultMutableTreeNode("Current Scene");
		defaultModel = new DefaultTreeModel(sceneRoot);
		tree = new JTree(defaultModel);
		refreshTree();
		
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionEventHandler());
		tree.setFocusable(false);
		this.add(tree);
		this.setFocusable(false);
		
	}
	
	//TREE SECTION
		/**
		 * Note** Only creates the current entity node. Doesn't create the main "Entities" folder in the same way that {@link #createCompositesNodeFolder(EntityStatic)} does.
		 * @param entity The entity that will be stored in this corresponding node.
		 * @return The branch node for the current entity. Also contains "Composites" and may contain more branches for its children*/
		private DefaultMutableTreeNode createSingleEntityNodeFolder(EntityStatic entity) {
			DefaultMutableTreeNode currentEntityNode = new DefaultMutableTreeNode(entity);
			//append Composites folder to Entities folder
			currentEntityNode.add(createCompositesNodeFolder(entity));
			//append Children folder to Entities folder
			// FIXME ask Matt to make some utility functions for easily navigating parent/child relationship.
			currentEntityNode.add(createChildrenNodeFolder(entity));
			return currentEntityNode;
		}
		private DefaultMutableTreeNode createEntireEntityNodeFolder(EntityStatic[] entityListRef) {
			DefaultMutableTreeNode entireEntityFolder = new DefaultMutableTreeNode("Entities");
			//will make as many folders(nodes) as there are Entities in the list parameter
			int size = entityListRef.length;
			DefaultMutableTreeNode currentFolder;
			for (int i = 0; i < size; i++) {
				currentFolder = createSingleEntityNodeFolder(entityListRef[i]);
				entireEntityFolder.add(currentFolder);
			}
			return entireEntityFolder;
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
		
		private DefaultMutableTreeNode createChildrenNodeFolder(EntityStatic entity) {
			DefaultMutableTreeNode newChildrenFolder = new DefaultMutableTreeNode("Children");
			// TODO lots of work to do here
			return newChildrenFolder;
		}
		public void refreshTree() {
			try {
				DefaultMutableTreeNode entitiesRootFolder = createEntireEntityNodeFolder(board.listCurrentSceneEntities());
				sceneRoot.removeAllChildren();
				defaultModel.insertNodeInto(entitiesRootFolder, sceneRoot, 0);
				//sceneRoot.add(entitiesRootFolder);
				//tree.expandPath(new TreePath(defaultModel.getPathToRoot(entitiesRootFolder)));
				tree.expandPath(new TreePath(sceneRoot.getLastChild()));
				System.err.println("Path to entitiesRootFolder: " + entitiesRootFolder.toString());
				defaultModel.reload();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	public class TreeSelectionEventHandler implements TreeSelectionListener{
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			DefaultMutableTreeNode testNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
			//this is still in raw Object format. Must be cast
			if (currentNode == null) return;
			Object objectInsideNode = currentNode.getUserObject();
			if (objectInsideNode instanceof Entity) {
				EntityStatic nodeIsEntity = (EntityStatic)objectInsideNode;
				System.out.println("*** IS AN ENTITY ***");
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
			System.err.println("Name of object in mode using its toString() method: " + objectInsideNode);

		}	
	} //end of TreeSelectionEventHandler class
	
}
