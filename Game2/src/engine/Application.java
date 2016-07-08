package engine;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Application extends JFrame {

    /**
	 *  
	 */
	private static final long serialVersionUID = 1L;

	public Application() {

        initUI();
    }
    
    private void initUI() {
        
        add(new Board());
        setSize(250, 200);
        setResizable(true);
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