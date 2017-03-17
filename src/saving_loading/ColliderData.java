package saving_loading;

import java.awt.Point;
import java.io.Serializable;

import physics.*;

public class ColliderData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int[] xCornerList;
	private int[] yCornerList;
	
	//Add events possibly?
	
	protected ColliderData( Vertex[] cornerList ){
		
		xCornerList = new int[cornerList.length];
		yCornerList = new int[cornerList.length];
		
		for ( int i = 0 ; i < cornerList.length ; i++ ){
			this.xCornerList[i] = (int)cornerList[i].toPoint().getX();
			this.yCornerList[i] = (int)cornerList[i].toPoint().getY();
		}
	}
	
	public Point[] getCornerPositions(){
		Point[] returnCorners = new Point[ xCornerList.length ];
		for ( int i = 0 ; i < returnCorners.length ; i++ ){
			returnCorners[i] = new Point( xCornerList[i], yCornerList[i] );
		}
		return returnCorners;
	}
	
}
