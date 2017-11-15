package editing;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.NetworkChannel;
import java.util.Enumeration;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.UnsupportedDataTypeException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import engine.BoardAbstract;
import entityComposites.AngularComposite;
import entityComposites.Collider;
import entityComposites.CompositeFactory;
import entityComposites.DynamicRotationComposite;
import entityComposites.Entity;
import entityComposites.EntityComposite;
import entityComposites.EntityStatic;
import entityComposites.GraphicComposite;
import entityComposites.TranslationComposite;
import misc.Client;
import misc.Server;
import physics.BoundarySingular;
import sprites.Sprite;

/** Task area. Consult every time opening this file. 
 * Last check: 2:22pm 9/9/17
 * 
 * Last thing I was working on was handling deleting composites from all root folders, as well as
 * handling what happens when you try to add a new composite. Currently only made progress on Graphics. But the framework
 * is mostly there for the others. 
 * TODO: Create a checker to check if the entity already has the composite, and don't let it create new node if true.
 * 
 * 
 * 
 * 
 * 
 * 
 * @author Dave
 *
 */
public class BrowserTreePanel extends JPanel {
	private static final Logger myLogger = Logger.getLogger( BrowserTreePanel.class.getName() );
	public static int count;
	private CompositeEditorPanel compositeEditorPanelRef;
	private JTree tree;
	private JToolBar filterToolBar;
	private DefaultTreeModel defaultModel;
	private EditorPanel editorPanelRef;
	protected BoardAbstract board;
	protected DefaultMutableTreeNode sceneRoot;
	protected DefaultMutableTreeNode entitiesRoot;
	//Root nodes for filtering composites
	protected DefaultMutableTreeNode graphicsRootNode = new DefaultMutableTreeNode("Graphics");
	protected DefaultMutableTreeNode colliderRootNode = new DefaultMutableTreeNode("Colliders");
	protected DefaultMutableTreeNode translationRootNode = new DefaultMutableTreeNode("Translations");
	protected DefaultMutableTreeNode angularRootNode = new DefaultMutableTreeNode("Angulars");
	protected DefaultMutableTreeNode dynamicRotationRootNode = new DefaultMutableTreeNode("DynamicRotations");
	
	public BrowserTreePanel(LayoutManager layout, EditorPanel editorPanelRef, BoardAbstract boardRef, 
			CompositeEditorPanel compositeEditorPanelArg, JToolBar filterToolBar) {
		super(layout);
		this.compositeEditorPanelRef = compositeEditorPanelArg;
		this.editorPanelRef = editorPanelRef;
		this.board = boardRef;
		this.filterToolBar = filterToolBar;
		count++;
		sceneRoot = new DefaultMutableTreeNode("Current Scene");
		entitiesRoot = new DefaultMutableTreeNode("Entities");
		sceneRoot.add(entitiesRoot);
		defaultModel = new DefaultTreeModel(sceneRoot);
		initializeFilterToolBar();
		tree = new JTree(defaultModel);
		populateEntityFolder(entitiesRoot, board.listCurrentSceneEntities());
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        tree.setCellRenderer(renderer);
		//tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionEventHandler());
		tree.setFocusable(false);
		tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)){
					int row = tree.getRowForLocation(e.getX(), e.getY());
					tree.setSelectionRow(row);
					if (row != -1) {
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
						//create new popup upon right click that contains reference to the selected Node.
						MyPopup newPopup = new MyPopup(currentNode);
						newPopup.show(tree, e.getX(), e.getY());
					}
				}
			}
		});
		
		BasicTreeUI basicTreeUI = (BasicTreeUI) tree.getUI();
		basicTreeUI.setRightChildIndent(5); 
		basicTreeUI.setLeftChildIndent(5);

		//FIXME Test area for server
		/*System.err.println("instance count: " + count);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Server server = new Server();
				server.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				server.startRunning();
			}

		});
		thread.start();
		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
		executor.schedule(new Runnable() {
			@Override
			public void run() {
				Client client = new Client("127.0.0.1");
				client.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				client.startRunning();
			}
		}, 5, TimeUnit.SECONDS);*/
		this.add(tree);
		TreeNode[] entitiesPath = defaultModel.getPathToRoot(entitiesRoot);
		tree.expandPath(new TreePath(entitiesPath));
		this.setFocusable(false);
	}
	
	@SuppressWarnings("serial")
	private class MyPopup extends JPopupMenu {
		private DefaultMutableTreeNode clickedNode;
		private EntityStatic theEntity = null;
		private JMenu addCompositeMenu = new JMenu("New Composite");
		private JMenuItem graphics = new JMenuItem("Graphics");
		private JMenuItem collider = new JMenuItem("Collider");
		private JMenuItem angular = new JMenuItem("Angular");
		private JMenuItem dynamicRotation = new JMenuItem("DynamicRotation");
		private JMenuItem translation = new JMenuItem("Translation");
		private JMenuItem deleteComposite = new JMenuItem("Delete");
		private JPopupMenu popUp = new JPopupMenu();
		private MyPopup(DefaultMutableTreeNode clickedNode) {
			super();
			this.clickedNode = clickedNode;
			/*TASKS*/
			// *** Check if "new composite" menuItem should be enabled
			// *** Also check if "Delete" menuItem should be enabled
			
			addCompositeMenu.setEnabled(false);
			deleteComposite.setEnabled(false);
			deleteComposite.addActionListener(new DeleteCompositeEvent());
			checkIfDeleteButtonValid();
			
			checkIfAddCompositeMenuValid();
			addCompositeMenu.add(graphics);
			addCompositeMenu.add(collider);
			addCompositeMenu.add(angular);
			addCompositeMenu.add(dynamicRotation);
			addCompositeMenu.add(translation);
			popUp.add(addCompositeMenu);
			popUp.add(deleteComposite);
			
		}

	
		/** FIXME: NEEDS WORK! FINISH FIRST! 
		 * The event that is called from {@link MyPopup.AddGraphicsCompositeEvent} and the others.
		 * */
	    protected void addCompositeToEntity(JMenuItem selectedOption) {
	    	if (theEntity != null) {
	    		String menuOptionName = selectedOption.getText();
	    		System.err.println("e.getSource() for JMenuItem: " + menuOptionName);
	    		if (menuOptionName.equalsIgnoreCase("Graphics")) 
	    		{
	    			System.err.println("IN BROWSERTREE: REACHED 'GRAPHICS' CHECK, for entity:" + theEntity.name);
	    			//if (checkIfCompositeIsActive(theEntity.getGraphicComposite()) == false)
	    			if (theEntity.getGraphicComposite().exists() == false)
	    			{
		    			System.err.println("Success"); 		
		    			//theEntity now has new graphicsComposite, so retreive it.
		    			GraphicComposite newGraphicsComposite = CompositeFactory.addGraphicTo(theEntity, Sprite.missingSprite, false);
		    			
		    			BrowserTreePanel.this.board.notifyGraphicsAddition(newGraphicsComposite);
		    			
		    			insertCompositeIntoRespectiveFolder(newGraphicsComposite); //inserts into respective filter
		    			try {  //inserts into actual root tree
		    				DefaultMutableTreeNode entityNode = containsEntity(theEntity, sceneRoot);
		    				addNewNodeOfAnyType( (DefaultMutableTreeNode)entityNode.getFirstChild(), newGraphicsComposite);
		    			}catch (UnsupportedDataTypeException e) { e.printStackTrace(); }
	    			}
	    		}
	    		else if (menuOptionName.equalsIgnoreCase("Collider")) {
	    			if (theEntity.getColliderComposite().exists() == false)
	    			{
		    			Collider newColliderComposite = CompositeFactory.addColliderTo(theEntity, new BoundarySingular(), board);
		    			
		    			insertCompositeIntoRespectiveFolder( newColliderComposite ); //inserts into respective filter
		    			try {  //inserts into actual root tree
		    				DefaultMutableTreeNode entityNode = containsEntity(theEntity, sceneRoot);
		    				addNewNodeOfAnyType( (DefaultMutableTreeNode)entityNode.getFirstChild(), newColliderComposite);
		    			}catch (UnsupportedDataTypeException e) { e.printStackTrace(); }
	    			}
	    		}else if (menuOptionName.equalsIgnoreCase("Translation")) {
	    			if (theEntity.getTranslationComposite().exists() == false)
	    			{
	    				
		    			//Collider newColliderComposite = CompositeFactory.addColliderTo(theEntity, new BoundarySingular());
		    			TranslationComposite newTranslationComposite = CompositeFactory.addTranslationTo(theEntity);
		    			insertCompositeIntoRespectiveFolder( newTranslationComposite ); //inserts into respective filter
		    			try {  //inserts into actual root tree
		    				DefaultMutableTreeNode entityNode = containsEntity(theEntity, sceneRoot);
		    				addNewNodeOfAnyType( (DefaultMutableTreeNode)entityNode.getFirstChild(), newTranslationComposite);
		    			}catch (UnsupportedDataTypeException e) { e.printStackTrace(); }
	    			}
	    		}
	    	}
	    }
	  
		/** Remove the currently selected node. Valid only for removing composites.
		 */
	    public void removeAndDeactivateCurrentNode() {
	        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
	        if (currentNode != null) {
	        	if (currentNode.getUserObject() instanceof EntityComposite) {
		            DefaultMutableTreeNode parent = (DefaultMutableTreeNode)(currentNode.getParent());
		            if (parent != null) {
		                defaultModel.removeNodeFromParent(currentNode); //deletes selected node
		                DefaultMutableTreeNode theRoot = (DefaultMutableTreeNode)defaultModel.getRoot();
		                if ( theRoot != sceneRoot) {
		                	defaultModel.removeNodeFromParent(parent);
		                }
		                // TODO SECTION TO DEACTIVATE COMPOSITE
		                // ________________
		               ((EntityComposite)currentNode.getUserObject()).disableComposite();
		                searchAndRemoveCompositeFromAllRoots(currentNode);
		            }
	        	}
	        } 
	    }
	    public void searchAndRemoveCompositeFromAllRoots(DefaultMutableTreeNode nodeToSearchFor){

	    	EntityComposite currentComposite = (EntityComposite)nodeToSearchFor.getUserObject();
	    	if (nodeToSearchFor == null) return;
			else {
				if ( (DefaultMutableTreeNode)defaultModel.getRoot() == sceneRoot) {
					if (nodeToSearchFor.getUserObject() instanceof GraphicComposite) {
						searchAndDeleteParentNodes( nodeToSearchFor , graphicsRootNode,  currentComposite, false );
					}
					else if(nodeToSearchFor.getUserObject() instanceof AngularComposite) {
						searchAndDeleteParentNodes( nodeToSearchFor , angularRootNode, currentComposite, false );
					}
					else if(nodeToSearchFor.getUserObject() instanceof Collider) {
						searchAndDeleteParentNodes( nodeToSearchFor , colliderRootNode, currentComposite, false );
					}
					else if(nodeToSearchFor.getUserObject() instanceof TranslationComposite) {
						searchAndDeleteParentNodes( nodeToSearchFor , translationRootNode, currentComposite, false );
					}
					else if(nodeToSearchFor.getUserObject() instanceof DynamicRotationComposite) {
						searchAndDeleteParentNodes( nodeToSearchFor , dynamicRotationRootNode, currentComposite, false );
					}
				}
				else { //meaning the user is deleting composite node from within a filter mode
					//must delete from Current Scene root as well.
					searchAndDeleteParentNodes(nodeToSearchFor, sceneRoot, currentComposite, true);

				}
					/*else {
						searchAndDeleteParentNodes(sceneRoot, sceneRoot, currentComposite);
					}*/
			}
	    	
/*//			Enumeration e = ((DefaultMutableTreeNode)entitiesRoot).breadthFirstEnumeration();
			Enumeration e = ((DefaultMutableTreeNode)nodeToSearch).breadthFirstEnumeration();
			if (e.hasMoreElements())
				e.nextElement();
			long timeOld = System.currentTimeMillis();
			long timeCurrent = System.currentTimeMillis();
			while (e.hasMoreElements()) {
				possibleNodeContainingEnt = (DefaultMutableTreeNode)e.nextElement();
				if (possibleNodeContainingEnt.getUserObject() == ent) {
					timeCurrent = System.currentTimeMillis();
					int difference = (int)(timeCurrent - timeOld);
					System.err.println("Took: " + difference + "ms to run search.");
					return possibleNodeContainingEnt;
				}
			}
			return null;*/
		}
	    private void searchAndDeleteParentNodes( 
	    		DefaultMutableTreeNode nodeToSearchFor, 
	    		DefaultMutableTreeNode rootToRemoveFrom,
	    		EntityComposite comp,
	    		boolean isSceneRoot
	    		){

		    	DefaultMutableTreeNode possibleNodeContainingEnt;
				Enumeration e = ((DefaultMutableTreeNode)rootToRemoveFrom).breadthFirstEnumeration();
				while (e.hasMoreElements()){
					possibleNodeContainingEnt = (DefaultMutableTreeNode)e.nextElement();
					if (possibleNodeContainingEnt.getUserObject() == comp) {
						DefaultMutableTreeNode itsParent = (DefaultMutableTreeNode)possibleNodeContainingEnt.getParent();
						System.err.println("IN BROWSERTREE: was able to remove " + possibleNodeContainingEnt.toString() + "from parent: " + itsParent.toString());
						if (isSceneRoot) 
							defaultModel.removeNodeFromParent(possibleNodeContainingEnt);
						else
							defaultModel.removeNodeFromParent(itsParent);
						//comp.disableComposite();
						break;
					}
				}
	    }
	    public void checkIfDeleteButtonValid() {
	    	if (clickedNode.getUserObject() instanceof EntityComposite)
	    		deleteComposite.setEnabled(true);
	    }
	    /** Careful, this method's kind of brittle. */
	    public void checkIfAddCompositeMenuValid() {
	    	
	    	//once node is right clicked, gonna have to check if it's any possible node that can redirect to
			//the owning entity.
			
			//1) if "Entities" clicked: not valid
			//2) if "[entityname] clicked: valid, might need reference variable to it
			//3) if "Composites" clicked: valid, need it to point to owner entity
			//4) if one of the composite names clicked: get parent's parent, get userobject
	    	DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
	    	if (currentNode != null   && 
	    			(DefaultMutableTreeNode)defaultModel.getRoot() == sceneRoot ) {
	    		
	    		if (currentNode.getUserObject() instanceof String) {
	    			if ( ((String)currentNode.getUserObject()).equals("Composites")) {
	    				theEntity = (EntityStatic)((DefaultMutableTreeNode)currentNode.getParent()).getUserObject();
	    				addCompositeMenu.setEnabled(true);
	    				addListenersToAddCompositeMenu();
	    			}	
	    		}
	    		else if (currentNode.getUserObject() instanceof EntityComposite) {
	    			//assume there would never be a composite without an entity owning it
	    			theEntity = ((EntityComposite)currentNode.getUserObject()).getOwnerEntity();
	    			addCompositeMenu.setEnabled(true);
	    			addListenersToAddCompositeMenu();
	    		}
	    		else if (currentNode.getUserObject() instanceof EntityStatic) {
	    			theEntity = ((EntityStatic)currentNode.getUserObject());
	    			addCompositeMenu.setEnabled(true);
	    			addListenersToAddCompositeMenu();
	    		}
	    	}
	    }
	    public void addListenersToAddCompositeMenu(){
	    	graphics.addActionListener(new AddGraphicsCompositeEvent());
			collider.addActionListener(new AddColliderCompositeEvent());
			angular.addActionListener(new AddAngularCompositeEvent());
			dynamicRotation.addActionListener(new AddDynamicRotationCompositeEvent());
			translation.addActionListener(new AddTranslationCompositeEvent());
	    }
	    /** Just an overridden method from JPopup. Ignore */
		@Override
		public void show(Component invoker, int x, int y) {
			// TODO Auto-generated method stub
			popUp.show(invoker, x, y);
		}
		// INNER NESTED CLASSES
		private class AddColliderCompositeEvent implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				System.err.println("Adding collider composite...");
				addCompositeToEntity( (JMenuItem)e.getSource());
			}
		}
		private class AddGraphicsCompositeEvent implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				addCompositeToEntity( (JMenuItem)e.getSource());
			}
		}
		private class AddAngularCompositeEvent implements ActionListener{
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.err.println("Adding angular composite...");
				addCompositeToEntity( (JMenuItem)e.getSource());
			}
		}
		private class AddDynamicRotationCompositeEvent implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				System.err.println("Adding dynamic rotation composite...");
				addCompositeToEntity( (JMenuItem)e.getSource());
			}
		}
		private class AddTranslationCompositeEvent implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				System.err.println("Adding translation composite...");
				addCompositeToEntity( (JMenuItem)e.getSource());
			}
		}
		private class DeleteCompositeEvent implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAndDeactivateCurrentNode();
			}
			
		}
	}
	
	public void initializeFilterToolBar() {
		JComboBox<DefaultMutableTreeNode> filterComboBox = new JComboBox<DefaultMutableTreeNode>(new DefaultMutableTreeNode[]{ 
				sceneRoot,graphicsRootNode, colliderRootNode, translationRootNode,
				angularRootNode, dynamicRotationRootNode
		});
		filterComboBox.setFocusable(false);
		filterComboBox.addItemListener(new FilterComboBoxHandler());
		filterComboBox.setFocusable(false);
		filterToolBar.add(new JLabel("Filter by: "));
		filterToolBar.add(filterComboBox);
		filterToolBar.setFloatable(false);
		filterToolBar.setRollover(true);
		//filterToolBar.revalidate();
		
	}
	//TREE SECTION
	public synchronized void notifyTreeAddedEntity(EntityStatic newEnt) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DefaultMutableTreeNode newEntityNode = createSingleEntityNodeFolder(newEnt);
				defaultModel.insertNodeInto(newEntityNode, entitiesRoot, entitiesRoot.getChildCount());
				//System.err.println("From notifyTreeAddedEntity()--- added "+newEnt.name+" to tree.");
			}
		});
	/*	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							System.err.println("in entity added, current thread: \n" + Thread.currentThread());
							DefaultMutableTreeNode newEntityNode = createSingleEntityNodeFolder(newEnt);
							defaultModel.insertNodeInto(newEntityNode, entitiesRoot, entitiesRoot.getChildCount());
							//System.err.println("From notifyTreeAddedEntity()--- added "+newEnt.name+" to tree.");
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}).start();*/
		
	}

	/**Tells the browser tree that relationship has changed, and updates the node display accordingly.
	 * Will be the connection that outside methods have when an actual parent change happens on entities.
	 * This will likely be called exclusively by CompositeFactory.makeChildOfParent()  */
	public boolean checkIfCompositeIsActive(EntityComposite compToCheck) {
		if (compToCheck.exists()) 
			return true;
		else
			return false;
	}
	public synchronized void notifyParentChildRelationshipChanged(EntityStatic child, EntityStatic parent) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DefaultMutableTreeNode parentNode, childNode;
				parentNode = containsEntity(parent, entitiesRoot);
				childNode = containsEntity(child, entitiesRoot);
				if (parentNode == null || childNode == null) {
					System.err.println("BrowserTree was unable to modify parents for child "+child.name+" and parent "+parent.name);
					return;  //if either of these nodes can't be found, there's no way to update the tree so break out.
				}
				defaultModel.removeNodeFromParent(childNode);
				//System.err.println("removed "+childNode.getUserObject().toString()+"from parent.");
				defaultModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
				//System.err.println("inserted "+childNode.getUserObject().toString()+"to parent "+parentNode.getUserObject().toString());
			}
		});
		/*new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							System.err.println("in notify parent changed, current thread: \n" + Thread.currentThread());
							DefaultMutableTreeNode parentNode, childNode;
							parentNode = containsEntity(parent, entitiesRoot);
							childNode = containsEntity(child, entitiesRoot);
							if (parentNode == null || childNode == null) {
								System.err.println("BrowserTree was unable to modify parents for child "+child.name+" and parent "+parent.name);
								return;  //if either of these nodes can't be found, there's no way to update the tree so break out.
							}
							defaultModel.removeNodeFromParent(childNode);
							//System.err.println("removed "+childNode.getUserObject().toString()+"from parent.");
							defaultModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
							//System.err.println("inserted "+childNode.getUserObject().toString()+"to parent "+parentNode.getUserObject().toString());
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}).start();*/
		
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
	private void insertCompositeIntoRespectiveFolder(EntityComposite currentComposite) {
		if (currentComposite == null) return;
		else {
			DefaultMutableTreeNode rootNodeToInsertInto = null;
			DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode(new String(currentComposite.getOwnerEntity().name));
			if (currentComposite instanceof GraphicComposite) {
				rootNodeToInsertInto = graphicsRootNode;
			}
			else if(currentComposite instanceof AngularComposite) {
				rootNodeToInsertInto = angularRootNode;
			}
			else if(currentComposite instanceof Collider) {
				rootNodeToInsertInto = colliderRootNode;
			}
			else if(currentComposite instanceof TranslationComposite) {
				rootNodeToInsertInto = translationRootNode;
			}
			else if(currentComposite instanceof DynamicRotationComposite) {
				rootNodeToInsertInto = dynamicRotationRootNode;
			}
			//area to do the actual hard lifting
			if(rootNodeToInsertInto != null) {
				this.defaultModel.insertNodeInto(entityNode, rootNodeToInsertInto, rootNodeToInsertInto.getChildCount());
				this.defaultModel.insertNodeInto(new DefaultMutableTreeNode(currentComposite), entityNode, entityNode.getChildCount());
			}
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
			if (checkIfCompositeIsActive(entityCompositeArray[i]) == true){
				newCompositesFolder.add(new DefaultMutableTreeNode(entityCompositeArray[i]));
				insertCompositeIntoRespectiveFolder(entityCompositeArray[i]);
			}
			/*else {
				myLogger.finest("Attempt);
				insertCompositeIntoRespectiveFolder(entityCompositeArray[i]);
				//now set composite's name to its owner entity, then add to respective composite folder
				// vvv test vvv
				//System.err.println(entityCompositeArray[i].getOwnerEntity());
				// ^^^ test ^^^ 
				//entityCompositeArray[i].setCompositeName(entityCompositeArray[i].getOwnerEntity().name);
				//TASK: Populate all the other Composite root nodes used in filtering tree
				
				//entityCompositeArray[i].setCompositeName(oldCompositeName);
			}*/
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
	public <T>DefaultMutableTreeNode addNewNodeOfAnyType(DefaultMutableTreeNode parentNode, T userType ) throws UnsupportedDataTypeException{
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(userType);
		if (parentNode != null){
			defaultModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
			return newNode;
		}
		else
			throw new UnsupportedDataTypeException("parentNode was null");
	}
	public class TreeSelectionEventHandler implements TreeSelectionListener{
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			//DefaultMutableTreeNode testNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
			//this is still in raw Object format. Must be cast
			if (currentNode == null) return;
			Object objectInsideNode = currentNode.getUserObject();
			if (defaultModel.getRoot() == sceneRoot) {
				if (objectInsideNode instanceof EntityStatic) {
					EntityStatic nodeIsEntity = (EntityStatic)objectInsideNode;
					System.out.println("*** IS AN ENTITY ***");
					editorPanelRef.setMode(editorPanelRef.getEntitySelectMode());
					editorPanelRef.selectSingleEntityGUIHouseKeeping();
					//sets Board's current entity
					editorPanelRef.getEntitySelectMode().addSelectedEntity(nodeIsEntity);
				}
			}
			//else if (objectInsideNode instanceof EntityComposite) {
			else  {//else condition: a composite root node is the active root.
				//task: select entity, and also create composite editorpanel based on the given composite
				EntityComposite nodeIsComposite = null;
				EntityStatic entityFromNode = null;
				if (objectInsideNode instanceof EntityComposite) {
					//it's a composite: get composite's owner entity, then select it.
					nodeIsComposite = (EntityComposite)objectInsideNode;
					entityFromNode = nodeIsComposite.getOwnerEntity();
				}
				else if (objectInsideNode instanceof String) {
					//the node that is the name of the entity is a string, I need it's first child. That will be the composite
					DefaultMutableTreeNode nodeIwantToWorkWith = (DefaultMutableTreeNode)currentNode.getFirstChild();
					nodeIsComposite = (EntityComposite)nodeIwantToWorkWith.getUserObject(); //will return EntityComposite type.
					entityFromNode = nodeIsComposite.getOwnerEntity();
				}
				//sets Board's current entity
				editorPanelRef.setMode(editorPanelRef.getEntitySelectMode());
				editorPanelRef.selectSingleEntityGUIHouseKeeping();
				editorPanelRef.getEntitySelectMode().addSelectedEntity(entityFromNode);
				//set composite editor panel's composite
				BrowserTreePanel.this.compositeEditorPanelRef.setCurrentComposite(nodeIsComposite);
				BrowserTreePanel.this.compositeEditorPanelRef.runTemplate();
				BrowserTreePanel.this.compositeEditorPanelRef.revalidate();
			}

		}	
	} //end of TreeSelectionEventHandler class
	public void setNewRoot(DefaultMutableTreeNode newRoot) {
		defaultModel.setRoot(newRoot);

		TreeNode[] nodes;
		if (defaultModel.getRoot() == sceneRoot) {
			DefaultMutableTreeNode tempChild = (DefaultMutableTreeNode)newRoot.getFirstChild();
			nodes = defaultModel.getPathToRoot(tempChild);
		}
		else {
			nodes = defaultModel.getPathToRoot(newRoot);
		}
		this.tree.expandPath(new TreePath(nodes));
		//this.tree.expandRow(1);
//		this.tree.collapsePath(new TreePath(this.entitiesRoot));
		//this.tree.collapsePath(new TreePath(this.entitiesRoot));
	}
	/** Will search entire tree for a node that contains this entity */
	public DefaultMutableTreeNode containsEntity(EntityStatic ent, DefaultMutableTreeNode nodeToSearch){
		DefaultMutableTreeNode possibleNodeContainingEnt;
//		Enumeration e = ((DefaultMutableTreeNode)entitiesRoot).breadthFirstEnumeration();
		Enumeration e = ((DefaultMutableTreeNode)nodeToSearch).breadthFirstEnumeration();
		if (e.hasMoreElements())
			e.nextElement();
		long timeOld = System.currentTimeMillis();
		long timeCurrent = System.currentTimeMillis();
		while (e.hasMoreElements()) {
			possibleNodeContainingEnt = (DefaultMutableTreeNode)e.nextElement();
			if (possibleNodeContainingEnt.getUserObject() == ent) {
				return possibleNodeContainingEnt;
			}
		}
		return null;
	}
	public DefaultMutableTreeNode getGraphicsRootNode() {
		return this.graphicsRootNode;
	}
	public DefaultMutableTreeNode getColliderRootNode() {
		return this.colliderRootNode;
	}
	public DefaultMutableTreeNode getTranslationRootNode() {
		return this.translationRootNode;
	}
	public DefaultMutableTreeNode getAngularRootNode() {
		return this.angularRootNode;
	}
	public DefaultMutableTreeNode getDynamicRotationRootNode() {
		return this.dynamicRotationRootNode;
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
	private class FilterComboBoxHandler implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				DefaultMutableTreeNode receiver = (DefaultMutableTreeNode)e.getItem();
				BrowserTreePanel.this.setNewRoot(receiver);
			}
			
		}
	}

	public void doNotifyEntitySelected(EntityStatic entity) {
		DefaultMutableTreeNode selectedEntityAsNode;
		selectedEntityAsNode = containsEntity(entity, sceneRoot);
		
		if (selectedEntityAsNode != null) {
			//TreePath pathToEntity = new TreePath(selectedEntityAsNode.getParent().getParent());
			TreeNode[] nodes = defaultModel.getPathToRoot(selectedEntityAsNode);
			tree.setSelectionPath(new TreePath(nodes));
		}
		
	}
	public void doNotifyDeselectEntity() {
		tree.setSelectionPath(null);
		
	}
}
