package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

import engine.MovingCamera;

public abstract class VoronoiRegion {

	protected BoundaryFeature ownerFeature;
	protected RegionCheck checkMath;
	
	protected VoronoiRegion( BoundaryFeature feature){ //super constructor
		this.ownerFeature = feature;
	}
	
	public static VoronoiRegion getUndefinedVoronoiRegion( BoundaryFeature feature ){ 
		
		VoronoiRegion returnRegion = new VoronoiRegion.Undefined(feature);
		returnRegion.checkMath = returnRegion.new UndefinedCheck();
		return returnRegion;
	}
	
	protected abstract void notifySetAngle( double setAngle );

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
	
	public Point getFeaturePoint(){
		return new Point(
				(int)this.ownerFeature.getP1().getX(),
				(int)this.ownerFeature.getP1().getY()
				);
	}
	
	public BoundaryFeature getFeature(){
		return ownerFeature;
	}
	
	public void debugDrawRegion( Point absPos, MovingCamera camera , Graphics2D g2 ){	
		this.checkMath.debugDraw( absPos, camera, g2);
	}
	
	public abstract void rotateRegion( double angle );
	
	//##########################################################################

	protected static class Undefined extends VoronoiRegion{

		protected Undefined(BoundaryFeature feature) {
			super(feature);
		}

		@Override
		protected void notifySetAngle(double setAngle) {
			// DO NOTHING
		}

		@Override
		public void rotateRegion(double angle) {
			System.err.println("Warning: attempting to rotate VoronoiRegion.Undefined");
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
					-ownerFeature.getP1().getY(),
					ownerFeature.getP1().getX()
			);
		}
		@Override
		public void debugDraw(Point absPos, MovingCamera cam, Graphics2D g2) {
			
		}
		
	}

}
