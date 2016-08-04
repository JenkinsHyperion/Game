package engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Application extends JFrame {

    /**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	private Board board;
	//private SplitPane container;

	public Application() 
	{
	    board = new Board();
        initUI();
       
    }
    
    private void initUI() {
    	
        add(board);
       /*
        test = new JPanel();
        test.setBackground(Color.WHITE);
        test.setSize(100, 300);
        test.setVisible(true);
        add(test, BorderLayout.EAST);
        //DisplayMode.REFRESH_RATE_UNKNOWN;
       // setSize(250, 200);
        */
        setSize(400, 335);
        setResizable(true);
        //setLayout(new FlowLayout());
        //pack();
        
        setTitle(System.getProperty("user.dir").replace( "\\", "//" ));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }

    public static void main(String[] args) {
    	
        EventQueue.invokeLater(new Runnable() {
    	///javax.swing.SwingUtilities.invokeLater(new Runnable() {   
            @Override
            public void run() {                
                JFrame ex = new Application();
                ex.setVisible(true);  
                
            }
        });
    }
}