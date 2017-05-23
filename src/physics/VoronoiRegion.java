package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

import engine.MovingCamera;

public class VoronoiRegion {

	protected BoundaryFeature ownerFeature;
	protected RegionCheck checkMath;
	
	protected VoronoiRegion( BoundaryFeature feature){ //super constructor
		this.ownerFeature = feature;
	}
	
	public static VoronoiRegion getUndefinedVoronoiRegion( BoundaryFeature feature ){ 
		
		VoronoiRegion returnRegion = new VoronoiRegion(feature);
		returnRegion.checkMath = returnRegion.new UndefinedCheck();
		return returnRegion;
	}
	
	
	public VoronoiRegion getEscapedRegion( Point point ){
		return this.checkMath.pointIsOutsideRegion(point);
	}
	
	public boolean pointIsInRegion( Point point ){
		return this.checkMath.pointIsInRegion(point);
	}
	
	/** Constructs the separation line between this region's boundary feature, and the given point inside the region.
	 * 
	 * @param center
	 * @return
	 */
	public Line2D constructDistanceLine( Point center){
		return this.checkMath.getSeparation(center);
	}
	
	public Point getFeaturePoint(){
		return new Point(
				(int)this.ownerFeature.getP1().getX(),
				(int)this.ownerFeature.getP1().getY()
				);
	}
	
	public BoundaryFeature getFeature(){
		return ownerFeature;
	}
	
	public void debugDrawRegion( MovingCamera camera , Graphics2D g2 ){	
		this.checkMath.debugDraw(camera, g2);
	}
	
	//##########################################################################

	protected interface RegionCheck{ // Interface for intersection math, depending on owner boundary feature (side, corner )
		/**
		 * Returns the adjacent region that input Point has escaped. 
		 * @param point
		 * @return Adjacent region that point has escaped. Null if point is still within this region.
		 */
		public VoronoiRegion pointIsOutsideRegion( Point point );
		public boolean pointIsInRegion(Point point);
		public Line2D getSeparation( Point center );
		public void debugDraw(MovingCamera cam , Graphics2D g2);
	}
	
	private class UndefinedCheck implements RegionCheck{
		@Override
		public VoronoiRegion pointIsOutsideRegion(Point point ) {
			return null; 
		}
		@Override
		public boolean pointIsInRegion(Point point){
			return true;
		}
		@Override
		public Line2D getSeparation(Point center) {
			return new Line2D.Double(
					-center.y,
					center.x,
					-ownerFeature.getP1().getY(),
					ownerFeature.getP1().getX()
			);
		}
		@Override
		public void debugDraw(MovingCamera cam, Graphics2D g2) {
			
		}
		
	}

}
