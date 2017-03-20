package sprites;

import java.awt.Graphics2D;
import java.util.ArrayList;

import engine.BoardAbstract;
import engine.Camera;
import engine.Overlay;
import engine.OverlayComposite;
import entityComposites.GraphicComposite;
import utility.*;

public class RenderingEngine {
	
	private Camera camera;
	
	private Graphics2D graphics;

	//private LinkedHead layer1Head;
	
	private DoubleLinkedList< GraphicComposite > spriteCompositeList = new DoubleLinkedList< GraphicComposite >();
	
	private ArrayList<Overlay> overlayList = new ArrayList<Overlay>();
	private ArrayList<OverlayComposite> visibleOverlayList = new ArrayList<OverlayComposite>();
	
	private ArrayList<Sprite> spriteListTEMPORARY_USEAGE_ONLY = new ArrayList<Sprite>();
	
	public RenderingEngine( BoardAbstract board){
		this.camera = new Camera( board , this.graphics , null  );
		init();
	}
	
	private void init(){
		//layer1Head = new LinkedHead();
	}

	public void draw( Graphics2D g2 ){ //ENTRY POINT OF RENDERING ENGINE FROM BOARD PAINTCOMPONENT(g)
		
		//Repainting
		this.graphics = g2;
		camera.repaint(g2); 
		
		//Layers
		//layer1Head.beginDraw(); 
		
		while ( spriteCompositeList.hasNext() ){
			spriteCompositeList.get().getSprite().drawSprite( camera );
		}
		
		//Overlays
		for ( OverlayComposite overlay : visibleOverlayList ){
			overlay.paintOverlay(g2,this.camera);
		}
		
	}
	
	public void redraw( Graphics2D g2){ 
		
	}
	
	public Ticket addSpriteComposite( GraphicComposite sprite ){
		
		try{
			//return layer1Head.addElement( (SpriteComposite) sprite );
			return spriteCompositeList.add( sprite );
		}
		
		catch( ClassCastException exc ){
			System.err.println("Attempted to add spriteless entity.");
			return null;
		}
			
	}
	
	public Camera getCamera(){
		return this.camera;
	}
	
	public void debugClearRenderer(){
		
		while ( spriteCompositeList.hasNext() ){
			spriteCompositeList.remove();
		}
		
	}
	
	//#### TESTING DOUBLE LINKED LIST #####
/*	
	private abstract class LinkedNode{ // make interface?
		
		LinkedNode nextNode;
		LinkedNode prevNode;
		
		protected abstract void draw();
		
		protected void setPreviousNode( LinkedNode prevNode ){ this.prevNode = prevNode; }
		protected void setNextNode( LinkedNode nextNode ){ this.nextNode = nextNode; }
		protected LinkedNode getPreviousNode(){ return this.prevNode; }
		protected LinkedNode getNextNode(){ return this.nextNode; }
		
	}
	
	private class LinkedHead extends LinkedNode{
		
		private LinkedTail tail; //useful having pointer to tail for end adding?
		
		public LinkedHead() {
			this.tail = new LinkedTail( this );
			this.setNextNode(tail);
			tail.setPreviousNode( LinkedHead.this);
		}
		
		public void beginDraw(){
			this.draw();
		}
		@Override
		public void draw(){
			this.getNextNode().draw();
		}
		
		public LinkedNodeElement addElement( SpriteComposite sprite ){
			LinkedNodeElement newNode = new LinkedNodeElement( sprite );
			
			newNode.setNextNode( this.nextNode ); 	// Head     Add --> Tail^      order of these is important

			newNode.setPreviousNode(this); 			// Head <-- Add     Tail
			
			this.nextNode.setPreviousNode(newNode); // Head     Add <-- Tail^
			
			this.nextNode = newNode;   				// Head --> Add     Tail**   change this.nextNode from tail to add LAST
			
			return newNode;
		}
		
	}
	
	private class LinkedTail extends LinkedNode{
		
		public LinkedTail( LinkedHead head ) {
			this.prevNode = head;
		}
		
		@Override
		public void draw(){
			//END OF LIST
		}
	}
	
	private class LinkedNodeElement extends LinkedNode{

		private SpriteComposite elementSprite;
		
		public LinkedNodeElement( SpriteComposite element ){
			this.elementSprite = element;
		}
		@Override
		protected void draw(){
			elementSprite.getSprite().drawSprite( camera );
			this.getNextNode().draw();
		}
		
		public void removeSelf(){
			this.prevNode.setNextNode( this.nextNode );
			this.nextNode.setPreviousNode( this.prevNode );
		}
		
	}*/
	
	public OverlayComposite addOverlay( Overlay overlay) {
		
		OverlayComposite newOverlayComp = new OverlayComposite( overlay );
		newOverlayComp.setHashID( 0 , this);
		return newOverlayComp;
		
	}
	
	public OverlayComposite quickAddOverlay( Overlay overlay) {
		
		OverlayComposite newOverlayComp = new OverlayComposite( overlay );
		newOverlayComp.setHashID( 0 , this);
		newOverlayComp.toggle();
		return newOverlayComp;
		
	}

	public OverlayComposite showOverlay( OverlayComposite overlay) {

		overlay.setHashID( visibleOverlayList.size() , this );
		
		visibleOverlayList.add( overlay );
		
		return overlay;
		
	}
	
	public void removeOverlay( int hashID ){
		
		for ( int i = hashID+1 ; i < visibleOverlayList.size() ; i++){
			visibleOverlayList.get(i).setHashID( i-1, this);
		}
		
		visibleOverlayList.remove(hashID);
		
	}

	public Graphics2D getGraphics() {
		return graphics;
	}
	
}
