package engine;

import java.awt.Graphics2D;

public interface Overlay {
	
	public abstract void paintOverlay( Graphics2D g2 , Camera cam );
	
}
