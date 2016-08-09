package engine;

import java.awt.*;
import javax.swing.*;

public class ApplicationTest extends JFrame {

	private static final long serialVersionUID = 1L;
	
	//private SplitPane container;
	public ApplicationTest() 
	{
       
    }
    
    private void initUI() {
              
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