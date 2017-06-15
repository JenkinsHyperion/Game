package engine;

import java.awt.Graphics2D;

import sprites.RenderingEngine;

public class OverlayComposite {
	
	private RenderingEngine ownerEngine;
	private int hashID;
	
	private boolean visible = true;
	
	private Overlay component;
	
	public OverlayComposite( Overlay component ){
		this.component = component;
	}
	
	public void toggle(){
		if (visible){
			this.remove();
			visible = false;
		}
		else{
			ownerEngine.showOverlay(this);
			visible = true;
		}
	}
	
	public void setHashID( int ID , RenderingEngine ownerEngine ){
		this.hashID = ID;
		this.ownerEngine = ownerEngine;
	}
	
	public void remove(){
		try{
			ownerEngine.removeOverlay( this.hashID );
		}
		catch ( ClassCastException exc ){
			System.err.println("Overlay has not been assigned a rendering engine.");
		}
	}

	public void paintOverlay( Graphics2D g2 , MovingCamera cam ){
		this.component.paintOverlay(g2,cam);
	}
	
}
