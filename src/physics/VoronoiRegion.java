package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

import engine.MovingCamera;

public abstract class VoronoiRegion {

	protected RegionCheck checkMath;
	
	protected VoronoiRegion(){ //super constructor

	}
	
	public static VoronoiRegion getUndefinedVoronoiRegion( BoundaryFeature feature ){ 
		
		VoronoiRegion returnRegion = new VoronoiRegion.Undefined(feature);
		returnRegion.checkMath = returnRegion.new UndefinedCheck();
		return returnRegion;
	}

	protected int debugNumberOfBounds(){ return 0; }
	
	public VoronoiRegion getEscapedRegion( Point relativePos){
		return this.checkMath.pointIsOutsideRegion(relativePos);
	}
	
	public boolean pointIsInRegion( Point relativePosition ){
		return this.checkMath.pointIsInRegion( relativePosition );
	}
	
	/** Constructs the separation line between this region's boundary feature, and the given point inside the region.
	 * 
	 * @param center
	 * @return
	 */
	public Line2D constructDistanceLine( Point relativePosition){
		return this.checkMath.getSeparation( relativePosition );
	}
	
	protected abstract Point getFeaturePoint(Point referencePoint);

	public abstract BoundaryFeature getFeature();
	
	public void debugDrawRegion( Point absPos, MovingCamera camera , Graphics2D g2 ){	
		this.checkMath.debugDraw( absPos, camera, g2);
	}

	//##########################################################################

	protected static class Undefined extends VoronoiRegion{

		private BoundaryFeature ownerFeature;
		
		protected Undefined(BoundaryFeature feature) {
			this.ownerFeature = feature;
		}

		@Override
		protected Point getFeaturePoint(Point referencePoint) {
			return new Point (
					(int) this.ownerFeature.getP1().getX(),
					(int) this.ownerFeature.getP1().getY()
					);
		}

		@Override
		public BoundaryFeature getFeature() {
			return ownerFeature;
		}

	}
	
	//##########################################################################

	protected interface RegionCheck{ // Interface for intersection math, depending on owner boundary feature (side, corner )
		/**
		 * Returns the adjacent VoronoiRegionDefined that input Point has escaped to. Returns null if input Point has not escaped. 
		 * @param point
		 * @return Adjacent region that point has escaped. Null if point is still within this region.
		 */
		public VoronoiRegion pointIsOutsideRegion( Point relativePos );
		public boolean pointIsInRegion(Point relativePosition );
		public Line2D getSeparation( Point relativeCirclePosition );
		public void debugDraw(Point absPos, MovingCamera cam , Graphics2D g2);
	}
	
	private class UndefinedCheck implements RegionCheck{

		@Override
		public VoronoiRegion pointIsOutsideRegion( Point relativePos) {
			return null; 
		}
		
		@Override
		public boolean pointIsInRegion( Point relativePosition ){
			return true;
		}

		@Override
		public Line2D getSeparation( Point relativeCirclePosition ) {
			return new Line2D.Double(
					-relativeCirclePosition.y,
					relativeCirclePosition.x,
					-getFeature().getP1().getY(),
					getFeature().getP1().getX()
			);
		}
		@Override
		public void debugDraw(Point absPos, MovingCamera cam, Graphics2D g2) {
			
		}
		
	}

}
