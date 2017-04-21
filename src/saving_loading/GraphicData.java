package saving_loading;

import java.awt.Point;
import java.io.Serializable;

public class GraphicData implements Serializable {

	private SpriteData spriteData;
	
	private GraphicData(){}
	
	protected static GraphicData createStillFrameData( String pathname , int xOffset, int yOffset ){
		GraphicData data = new GraphicData();
		data.setSrpiteData( data.new SpriteData( pathname, xOffset, yOffset ) );
		return data;
	}
	
	protected static GraphicData createAnimationData( String path, int xOffset, int yOffset, int frames, int row, int tileW, int tileH, int delay ){
		GraphicData data = new GraphicData();
		data.setSrpiteData( data.new AnimatedSprite( path, xOffset, yOffset , frames, row, tileW, tileH, delay ) );
		return data;
	}
	
	protected String getPathName(){
		return this.spriteData.pathname;
	}
	protected int getXOffset(){
		return this.spriteData.xOffset;
	}
	protected int getYOffset(){
		return this.spriteData.yOffset;
	}
	
	protected void setSrpiteData( SpriteData data ){
		this.spriteData = data;
	}
	
	public SpriteData getSpriteData(){
		return spriteData;
	}

	public class SpriteData implements Serializable{
		protected String pathname;
		protected int xOffset;
		protected int yOffset;
		public SpriteData(String pathname , int xOffset , int yOffset){
			this.pathname = pathname;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}
		public String getPath(){ return pathname; }
		public int getOffsetX(){ return xOffset; }
		public int getOffsetY(){ return yOffset; }
		
	}
	
	public class AnimatedSprite extends SpriteData implements Serializable{
		private int frameCount;
		private int row;
		private int tileWidth;
		private int tileHeight;
		private int delay;
		public AnimatedSprite( String path , int xOffset , int yOffset , int frameCount, int row, int tileWidth, int tileHeight, int delay){
			super(path , xOffset, yOffset);
			this.frameCount = frameCount;
			this.row = row;
			this.tileWidth = tileWidth;
			this.tileHeight = tileHeight;
		}
		public int getFrameCount(){ return frameCount; }
		public int getRow(){ return row; }
		public int getTileWidth(){ return tileWidth; }
		public int getTileHeight(){ return tileHeight; }
		public int getDelay() { return delay; }
	}
	
}
