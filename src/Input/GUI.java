package Input;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class GUI {
	
	
	private interface Input{
		void mouseClicked( MouseEvent e );
		void mouseReleased( MouseEvent e );
	}
	
	private class Container{
		
		private Point screenPosition;
		private Dimension dimensions;
		private Component[] componentsList;
		
		protected void mouseMoved( Point screenPosition ){
			
		}
		
	}
	
	private abstract class Component{
		
		
		
	}

}
