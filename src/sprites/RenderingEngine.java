package sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Input.MouseCommand;
import engine.Board;
import engine.BoardAbstract;
import engine.ReferenceFrame;
import engine.MovingCamera;
import engine.Overlay;
import engine.OverlayComposite;
import entityComposites.GraphicComposite;
import utility.*;

public class RenderingEngine {
	
	private MovingCamera camera;
	private BoardAbstract ownerBoard;
	
	private Graphics2D graphics;

	//private LinkedHead layer1Head;
	
	private DoubleLinkedList< ActiveGraphic > activeSpriteCompositeList = new DoubleLinkedList< ActiveGraphic >();
	
	public RenderingLayer[] layersList;
	
	private BufferedImage image = new BufferedImage( ownerBoard.B_WIDTH, ownerBoard.B_HEIGHT , BufferedImage.TYPE_INT_ARGB );
	
	private ArrayList<OverlayComposite> visibleOverlayList = new ArrayList<OverlayComposite>();
	
	private ArrayList<Sprite> spriteListTEMPORARY_USEAGE_ONLY = new ArrayList<Sprite>();
	
	public RenderingEngine( BoardAbstract board){
		this.camera = new MovingCamera( board , this.graphics , null  ); 
		this.ownerBoard = board;
		init();
	}
	
	private void init(){

		layersList = new RenderingLayer[]{
			new RenderingLayer(1,1,1,camera),
			new RenderingLayer(1.1,1.1 ,0.98 ,camera), //nearest
			new RenderingLayer(1.6,1.4 , 0.9 ,camera),
			new RenderingLayer(2.0, 1.6, 0.4 ,camera),
			new RenderingLayer(3.0, 3.0, 0.05 ,camera ),
			new RenderingLayer(5, 5 ,0.1,camera),
			new RenderingLayer(10, 10 ,0.01,camera),
			new RenderingLayer(100, 100 ,0,camera) //farthest
		};
		
	}
	
	public Graphics2D debugGetOverlayGraphics(){
		image = new BufferedImage( ownerBoard.B_WIDTH, ownerBoard.B_HEIGHT , BufferedImage.TYPE_INT_ARGB );
		//image.
		return (Graphics2D) image.getGraphics();
	}
	
	public ReferenceFrame debugGetOverlayCamera(){
		return camera;
	}

	public void render( Graphics2D g2 ){ //ENTRY POINT OF RENDERING ENGINE FROM BOARD PAINTCOMPONENT(g)
		
		//Repainting
		this.graphics = g2;
		camera.repaint(g2); 
		
		for ( int i = layersList.length-1 ; i > -1  ; i-- ){
			layersList[i].renderLayer(camera);
		}	
		
		int spriteNumber = 0;
		
		while ( activeSpriteCompositeList.hasNext() ){
			ActiveGraphic graphic = activeSpriteCompositeList.get();
			graphic.composite.draw(camera);
			spriteNumber++;
		}
		g2.setColor(Color.CYAN);
		g2.drawString( "Rendering Engine: Sprites: "+ spriteNumber , 20, 300);
		
		//Overlays
		
		g2.drawImage(image,0,0,ownerBoard );
		
		for ( OverlayComposite overlay : visibleOverlayList ){
			overlay.paintOverlay(camera.getOverlayGraphics(), camera);
		}
		
	}
	
	public ActiveGraphic addGraphicsCompositeToRenderer( GraphicComposite sprite ){
		
		try{
			ActiveGraphic newActiveGraphic = new ActiveGraphic(sprite);
			newActiveGraphic.listPosition = activeSpriteCompositeList.add(newActiveGraphic);
			return newActiveGraphic;
		}
		
		catch( ClassCastException exc ){
			System.err.println("Rendering Engine attempted to add spriteless entity.");
			return null;
		}
			
	}
	
	public MovingCamera getCamera(){
		return this.camera;
	}
	
	public void debugClearRenderer(){
		
		while ( activeSpriteCompositeList.hasNext() ){
			activeSpriteCompositeList.remove();
		}
		
	}
	

	
	public OverlayComposite addOverlay( Overlay overlay) {
		OverlayComposite newOverlayComp = new OverlayComposite( overlay );
		newOverlayComp.setHashID( this.visibleOverlayList.size() , this);
		this.visibleOverlayList.add( newOverlayComp );
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

	public class ActiveGraphic{
		
		protected GraphicComposite composite;
		protected ListNodeTicket listPosition;
		
		protected ActiveGraphic( GraphicComposite composite ){
			this.composite = composite;
		}
		
		public void activateInRenderingEngine(){
			if (listPosition == null){
				listPosition = activeSpriteCompositeList.add(this);
			}
			else{	
				System.err.println("Graphic is already active");
			}
		}
		
		public void deactivateInRenderingEngine(){
			if (listPosition != null){
				listPosition.removeSelfFromList();
				listPosition = null;
			}
			else{
				System.err.println("Graphic is already inactive");
			}
		}
		
		public void notifyRenderingEngineOfDisabledGraphic(){
			composite = null;
			listPosition.removeSelfFromList();
			listPosition = null;
		}
	}
	
}
