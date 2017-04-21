
package editing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.border.BevelBorder;
import javax.swing.*;

@SuppressWarnings("serial")
public class SpriteIconLoader extends JFrame {
   
  
    private String iconsDir = System.getProperty("user.dir") + "\\Assets\\Icons\\";
    private String realImagesDir = System.getProperty("user.dir") + "\\Assets\\";
    private JPanel iconBarRef;
    private EditorPanel editorPanelRef;
    private MissingIcon placeholderIcon = new MissingIcon();
    /*private String[] iconFileNames = { 
    		"boxIcon.png" , 
    		"bulletIcon.png", 
    		"grass01Icon.png",
    		"ground01Icon.png",
    		"ground_1Icon.png", 
    		"platformIcon.png",
    		"platform02Icon.png" }; */
    //private String[] realImageFileNames = new String[iconFileNames.length];
    private String[] realImageFileNames;
//    private String[] realImageFileNames = { "platform.png" , "platform02.png", "ground01.png",
//    		"grass01.png", "box.png" }; 
    /**
     * Helper class to load buttons into preview Icon display for creating new entities.
     * @param editorPanelRef Reference to EditorPanel to use its methods and fields
     * @param ibRef Reference to the IconBar panel inside of EditorPanel
     */
    public SpriteIconLoader(JPanel edPanRef, JPanel ibRef) {
    	editorPanelRef = (EditorPanel)edPanRef;
    	iconBarRef = ibRef;  
    	populateRealFileNamesArray("SpriteHotSwap");
    	//populateRealFileNamesArray("Icons", iconFileNames);
    	//iconBarRef.add(Box.createGlue());
    	// iconBarRef.add(Box.createGlue());   

    	// start the image loading SwingWorker in a background thread
    	loadimages.execute();
    }
    private void populateRealFileNamesArray(String path) {
    	File fileArray[] = null;
//    	FileInputStream fileIn = null;
    	if (new File(realImagesDir + File.separator + path).exists()) {
    		try {
    			fileArray = new File(realImagesDir + File.separator + path + File.separator).listFiles();
    			realImageFileNames = new String[fileArray.length];
    			System.out.println("fileArray's length: " + fileArray.length);
    			for (int i = 0; i < fileArray.length; i++) {
    				/*fileIn = new FileInputStream(realImagesDir + File.separator + "SpriteHotSwap" + File.separator +
    						fileArray[i].getName());
    				fileIn.close();*/
    				realImageFileNames[i] = fileArray[i].getName();
    				System.out.println(fileArray[i].getName());
    			}
    		}
    		 catch (Exception f) {
 				f.printStackTrace();
 			}
    		System.out.println("Realimagefilenames[]: \n");
    		 for (int i = 0; i < realImageFileNames.length; i++) {
    			 System.out.println(realImageFileNames[i]);
    		 }
    	}
    	else {
			JOptionPane.showMessageDialog(null, "No files to load");
			realImageFileNames = new String[0];
    	}
    }

    /**
     * SwingWorker class that loads the images a background thread and calls publish
     * when a new one is ready to be displayed.
     *
     * We use Void as the first SwingWroker param as we do not need to return
     * anything from doInBackground().
     */
    private SwingWorker<Void, ThumbnailAction> loadimages = new SwingWorker<Void, ThumbnailAction>() {
        /**
         * Creates full size and thumbnail versions of the target image files.
         */
        @Override
        protected Void doInBackground() throws Exception {
            for (int i = 0; i < realImageFileNames.length; i++) {
                ImageIcon icon; //picture that will be set on top of each button
               // String iconPath = iconsDir + iconFileNames[i];
                String iconPath = realImagesDir + realImageFileNames[i];
                //String realImagePath = realImagesDir + realImageFileNames[i];
                icon = createImageIcon(iconPath);
                //put logic for resizing image for the icon here:
                
                ThumbnailAction thumbAction;
                if(icon != null){
                    
                    thumbAction = new ThumbnailAction(icon, iconPath, realImageFileNames[i]);
                    
                }else{
                    // the image failed to load for some reason
                    // so load a placeholder instead
                    thumbAction = new ThumbnailAction(placeholderIcon, null, null);
                }
                //publish(List<V> dataChunks)  ----> sends argument to process
                publish(thumbAction);
            }
            // unfortunately we must return something, and only null is valid to
            // return when the return type is void.
            return null;
        }
        
        /**This method is run when publish() is called. ^^^
         * Process all loaded images.
         */
        @Override
        protected void process(List<ThumbnailAction> chunks) {
            for (ThumbnailAction thumbAction : chunks) {
                JButton thumbButton = new JButton(thumbAction);
                thumbButton.setFocusable(false);
                thumbButton.setPreferredSize(new Dimension(40,40));
                thumbButton.setBackground(Color.BLACK);
                thumbButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.LIGHT_GRAY, Color.DARK_GRAY));
                iconBarRef.add(thumbButton);
            }
        }
    };
    
    /**
     * Creates an ImageIcon if the path is valid.
     * @param String - resource path
     * @param String - description of the file
     */
    protected ImageIcon createImageIcon(String path) {
    	File newFile = new File(path);
        if ( newFile.exists() ) {
        	System.out.println("This path exists: " + path);
            return new ImageIcon(path);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    
    /**
     * Action class that shows the image specified in it's constructor.
     */
    private class ThumbnailAction extends AbstractAction{
        
        /**
         * @param Icon - The full size photo to show in the button.
         * @param Icon - The thumbnail to show in the button.
         * @param String - The description of the icon.
         */
        public ThumbnailAction(Icon photo, String path, String realImagePath){

            // The LARGE_ICON_KEY is the key for setting the
            // icon when an Action is applied to a button.
            putValue(LARGE_ICON_KEY, photo);
            putValue(ACTION_COMMAND_KEY, realImagePath);
        }
        
        /**
         * Shows the full image in the main area and sets the application title.
         */
        public void actionPerformed(ActionEvent e) {
           	//editorPanelRef.setMode(editorPanelRef.getSpriteEditorMode());
        	String path = e.getActionCommand();
        	editorPanelRef.getSpriteEditorMode().setSpritePath(path);
        	editorPanelRef.tempSpriteName.setText(path);
        	//editorPanelRef.setGhostSprite(path);
        	//editorPanelRef.setNewEntityPath(path);
        	//editorPanelRef.mode = EditorPanel.ENTPLACEMENT_MODE;
 
           //System.out.println(e.getActionCommand().toString());
        }
    }
}