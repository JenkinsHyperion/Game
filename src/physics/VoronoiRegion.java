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
	
	@Deprecated
	public VoronoiRegion getEscapedRegion( Point point , Point localPos){
		return this.checkMath.pointIsOutsideRegion(point,localPos);
	}
	
	public VoronoiRegion getEscapedRegion( Point relativePos){
		return this.checkMath.pointIsOutsideRegion(relativePos);
	}
	
	public boolean pointIsInRegion( Point point , Point localPos){
		return this.checkMath.pointIsInRegion(point,localPos);
	}
	
	/** Constructs the separation line between this region's boundary feature, and the given point inside the region.
	 * 
	 * @param center
	 * @return
	 */
	@Deprecated
	public Line2D constructDistanceLine( Point center , Point boundaryEntityPosition){
		return this.checkMath.getSeparation(center , boundaryEntityPosition );
	}
	
	public Line2D constructDistanceLine( Point relativeCirclePosition){
		return this.checkMath.getSeparation( relativeCirclePosition );
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
		@Deprecated
		public VoronoiRegion pointIsOutsideRegion( Point point , Point localPos );
		public VoronoiRegion pointIsOutsideRegion( Point relativePos );
		public boolean pointIsInRegion(Point point, Point localPos);
		@Deprecated
		public Line2D getSeparation( Point center , Point boundaryEntityPosition );
		public Line2D getSeparation( Point relativeCirclePosition );
		public void debugDraw(MovingCamera cam , Graphics2D g2);
	}
	
	private class UndefinedCheck implements RegionCheck{
		@Override
		public VoronoiRegion pointIsOutsideRegion(Point point , Point localPos) {
			return null; 
		}
		@Override
		public VoronoiRegion pointIsOutsideRegion( Point relativePos) {
			return null; 
		}
		@Override
		public boolean pointIsInRegion(Point point , Point localPos){
			return true;
		}
		@Override
		public Line2D getSeparation( Point center , Point boundaryEntityPosition ) {
			return new Line2D.Double(
					-center.y,
					center.x,
					-ownerFeature.getP1().getY() - boundaryEntityPosition.y,
					ownerFeature.getP1().getX() - boundaryEntityPosition.x
			);
		}
		@Override
		public Line2D getSeparation( Point relativeCirclePosition ) {
			return new Line2D.Double(
					-relativeCirclePosition.y,
					relativeCirclePosition.x,
					-ownerFeature.getP1().getY(),
					ownerFeature.getP1().getX()
			);
		}
		@Override
		public void debugDraw(MovingCamera cam, Graphics2D g2) {
			
		}
		
	}

}
