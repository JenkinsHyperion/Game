package engine;

import java.awt.DisplayMode;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTextField;


public class Application extends JFrame {

    /**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	private Board board;
	public Application() 
	{
	    board = new Board();
        initUI();
       
    }
    
    private void initUI() {
        add(board);
        //DisplayMode.REFRESH_RATE_UNKNOWN;
        setSize(250, 200);
        setResizable(true);
        //setLayout(new FlowLayout());
        pack();
        
        setTitle(System.getProperty("user.dir").replace( "\\", "//" ));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }

    public static void main(String[] args) {
    	
        EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {                
                JFrame ex = new Application();
                ex.setVisible(true);  
                
            }
        });
    }
}