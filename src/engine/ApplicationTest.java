package engine;

import java.awt.*;
import javax.swing.*;

public class ApplicationTest extends JFrame {

	private static final long serialVersionUID = 1L;
	
	//private SplitPane container;
	public ApplicationTest() 
	{
        //initUI();
       
    }
    
    private void initUI() {
    	
    	//container = new SplitPane();
       // setSize(400, 335);
              
    }

    public static void main(String[] args) {
    	
        EventQueue.invokeLater(new Runnable() {
    	//javax.swing.SwingUtilities.invokeLater(new Runnable() {   
            @Override
            public void run() {                
                SplitPane.createAndShowGUI();                
            }
        });
    }
}