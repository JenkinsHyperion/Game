package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


import engine.MovingCamera;
import entityComposites.EntityStatic;

public abstract class VoronoiRegionDefined extends VoronoiRegion{
	
	private RegionBoundary[] boundsList = new RegionBoundary[0];

	protected void addRegionBoundary( RegionBoundary addBound ){
		RegionBoundary[] newBounds = new RegionBoundary[ boundsList.length +1 ];
		for ( int i = 0 ; i < boundsList.length ; i++ ){
			newBounds[i] = boundsList[i];
		}
		newBounds[ newBounds.length-1 ] = addBound;
		boundsList = null;
		boundsList = newBounds;
	}
	
	@Override
	protected int debugNumberOfBounds(){ return this.boundsList.length; }

	
	public static void addSideOuterBounds( VoronoiRegionDefined cornerCCW , VoronoiRegionDefined region, VoronoiRegionDefined cornerCW , BoundarySide side ){ //CONSTRCT ADJACENT CORNERS
		
		//System.err.println(cornerCCW.getFeature()+" "+region.getFeature()+" "+cornerCW.getFeature());
		
		final Vector normal = side.toVector().normalLeft();
		byte topBottom;
		byte bottomTop;
		
		if (normal.getX() > 0){
			topBottom = 1;
			bottomTop = -1;
		}else{
			topBottom = -1;
			bottomTop = 1;
		}
		RegionBoundary boundCornerCW;
		RegionBoundary boundSideCW;
		RegionBoundary boundSideCCW;
		RegionBoundary boundCornerCCW;
		
		if ( normal.getX() !=0 ){
			double slope = normal.getY() / normal.getX();
			int yIntercept = (int) (side.getY1() - slope * side.getX1());
			boundSideCCW = region.new RegionBoundarySlope( slope , yIntercept , topBottom , cornerCCW);
			boundCornerCW = cornerCCW.new RegionBoundarySlope( slope , yIntercept , bottomTop , region);
			
			int yInterceptCW = (int) (side.getY2() - slope * side.getX2());
			boundSideCW = region.new RegionBoundarySlope( slope , yInterceptCW , bottomTop , cornerCW);
			boundCornerCCW = cornerCW.new RegionBoundarySlope( slope , yInterceptCW , topBottom , region);
		}
		else{ //normal is vertical
			byte CCW;
			byte CW;
			if (normal.getY() > 0){
				CW = 1;
				CCW = -1;
			}else{
				CW= -1;
				CCW = 1;
			}
			boundCornerCCW = cornerCW.new RegionBoundaryVertical( CW, side.getX2() , region );
			
			boundSideCCW = region.new RegionBoundaryVertical( CCW, side.getX2() , cornerCW);		
			boundSideCW = region.new RegionBoundaryVertical( CW, side.getX1() , cornerCCW);
			
			boundCornerCW = cornerCCW.new RegionBoundaryVertical( CCW, side.getX1() , region );
		}
		cornerCCW.checkMath = cornerCCW.new CornerCheck( side.getStartPoint() );
		region.checkMath = region.new GeneralSideCheck(side);
		cornerCW.checkMath = cornerCW.new CornerCheck( side.getEndPoint() );

		cornerCCW.addRegionBoundary(boundCornerCW);
		region.addRegionBoundary( boundSideCW );
		region.addRegionBoundary( boundSideCCW );
		cornerCW.addRegionBoundary(boundCornerCCW);
		
	}
	
	public static void splitAdjacentSideRegions( VoronoiRegionDefined regionCCW , VoronoiRegionDefined regionCW ){

		BoundarySide sideCCW = (BoundarySide) (regionCCW.getFeature()) ;
		BoundarySide sideCW = (BoundarySide) (regionCW.getFeature()) ;
		
		Vector split = sideCCW.toVector().bisectingVector( sideCW.toVector().inverse() );
		
		RegionBoundary boundSideCCW;
		RegionBoundary boundSideCW;
		
		if ( split.getX() != 0 ){ //region divider is not vertical
			
			byte topBottom;
			byte bottomTop;
			
			if (split.getX() > 0){
				topBottom = 1;
				bottomTop = -1;
			}else{
				topBottom = -1;
				bottomTop = 1;
			}
			
			double slope = split.getY() / split.getX();
			int yIntercept = (int) (sideCW.getY1() - slope * sideCW.getX1());
			boundSideCCW = regionCCW.new RegionBoundarySlope( slope , yIntercept , topBottom , regionCW );
			boundSideCW = regionCW.new RegionBoundarySlope( slope , yIntercept , bottomTop , regionCCW );
		}
		else{
			
			byte CCW; 
			byte CW;
			if (split.getY() > 0){
				CW = 1;
				CCW = -1;
			}else{
				CW= -1;
				CCW = 1;
			}	
			
			boundSideCW = regionCW.new RegionBoundaryVertical( CW, sideCW.getX1() , regionCCW );
			boundSideCCW = regionCCW.new RegionBoundaryVertical( CCW, sideCW.getX1() , regionCW );
		}
		
		regionCW.checkMath = regionCW.new GeneralSideCheck( sideCW );
		regionCCW.checkMath = regionCCW.new GeneralSideCheck( sideCCW );

		regionCW.addRegionBoundary( boundSideCW );
		regionCCW.addRegionBoundary( boundSideCCW );
		
	}
	
	public static void splitOpposingSides( VoronoiRegionDefined sideRegion1 , VoronoiRegionDefined sideRegion2 ){
		
		BoundarySide side1 = (BoundarySide) (sideRegion1.getFeature()) ;
		BoundarySide side2 = (BoundarySide) (sideRegion2.getFeature()) ;
		
		Vector split = side1.toVector().bisectingVector( side2.toVector().inverse() );
		
		RegionBoundary boundSide1;
		RegionBoundary boundSide2;
		
		if ( split.getX() != 0 ){ //region divider is not vertical
			
			byte topBottom;
			byte bottomTop;
			
			if (split.getX() > 0){
				topBottom = 1;
				bottomTop = -1;
			}else{
				topBottom = -1;
				bottomTop = 1;
			}
			
			double slope = split.getY() / split.getX();
			
			Point mid = new Point( (side1.getX1() + side2.getX1())/2 , (side1.getY1() + side2.getY1())/2 ); 
			
			int yIntercept = (int) ( mid.y - slope * mid.x );
			boundSide1 = sideRegion1.new RegionBoundarySlope( slope , yIntercept , topBottom , sideRegion2);
			boundSide2 = sideRegion2.new RegionBoundarySlope( slope , yIntercept , bottomTop , sideRegion1 );
		}
		else{
			
			byte CCW; 
			byte CW;
			if (split.getY() > 0){
				CW = 1;
				CCW = -1;
			}else{
				CW= -1;
				CCW = 1;
			}	
			
			boundSide1 = sideRegion1.new RegionBoundaryVertical( CW, side1.getX1() , sideRegion2 );
			boundSide2 = sideRegion2.new RegionBoundaryVertical( CCW, side2.getX1() , sideRegion1 );
		}
		
		sideRegion1.checkMath = sideRegion1.new GeneralSideCheck( side1 );
		sideRegion2.checkMath = sideRegion2.new GeneralSideCheck( side2 );

		sideRegion1.addRegionBoundary( boundSide1 );
		sideRegion2.addRegionBoundary( boundSide2 );
		
	}

	public static class Side extends VoronoiRegionDefined{

		private BoundarySide ownerSide;
		
		public Side(BoundarySide ownerSide) {
			this.ownerSide = ownerSide;
		}
		/**VoronoiRegionDefined.Side.getFeaturePoint(Point p) returns the Point on the line of the Side.
		 * 
		 */
		@Override
		protected Point getFeaturePoint(Point referencePoint) {
			
			Point2D p = Boundary.getProjectionPoint( referencePoint, this.ownerSide.toLine() ); //OPTIMIZE this is overkill floating point accuracy
			
			final int returnX = (int)p.getX();
			final int returnY = (int)p.getY();
			
			return new Point(returnX,returnY);
		}
		@Override
		public BoundaryFeature getFeature() {
			return this.ownerSide;
		}
	}
	
	public static class Corner extends VoronoiRegionDefined{

		private BoundaryCorner ownerCorner;
		
		public Corner(BoundaryCorner ownerCorner) {
			this.ownerCorner = ownerCorner;
		}
		/**VoronoiRegionDefined.Corner.getFeaturePoint(Point p) returns the position of the corner.
		 * 
		 */
		@Override
		protected Point getFeaturePoint(Point referencePoint) { 

			return new Point(
				(int)this.ownerCorner.getP1().getX(),
				(int)this.ownerCorner.getP1().getY()
				);
		}
		@Override
		public BoundaryFeature getFeature() {
			return this.ownerCorner;
		}
	}
	
	//##############################################################################################################
	// BOUNDARY CHECKS
	//OPTIMIZE : MOVE THESE INTO THEIR RESPECTIVE REGION SUBCLASSES and delete redundant variables of side and corners
	
	private class GeneralSideCheck implements RegionCheck{ //Special case horizontal side where CW and CCW sides will be vertical
		private BoundarySide side;
		protected GeneralSideCheck(BoundarySide side){
			this.side = side;
		}
		@Override
		public VoronoiRegion pointIsOutsideRegion( Point relativePos ) {
			for ( RegionBoundary bound : boundsList ){
				if ( !bound.pointIsWithinBound(relativePos) ){ 
					return bound.adjacentRegion;
				}
			}
			return null ;
		}
		
		@Override
			public boolean pointIsInRegion(Point relativePosition) {
			for ( RegionBoundary bound : boundsList ){
				if ( bound.pointIsWithinBound(relativePosition) ){
					return true;
				}
			}
			return false;
			}
		
		public Line2D getSeparation( Point relativeCirclePosition ){
			return this.side.toLine();
		}

		@Override
		public void debugDraw(Point absPos, MovingCamera cam, Graphics2D g2) {
			for( RegionBoundary bound : boundsList ){
				bound.draw( absPos, cam , g2 , side.getP2() );
			}
		}
		
	}
	
	private class CornerCheck implements RegionCheck{ //Corner Check uses normal vectors of adjacent sides
		private BoundaryCorner ownerCorner;
		public CornerCheck(BoundaryCorner ownerCorner){
			this.ownerCorner = ownerCorner;
		}
		
		@Override
		public VoronoiRegion pointIsOutsideRegion( Point relativePos ) { 
			
			for ( RegionBoundary bound : boundsList ){
				if ( !bound.pointIsWithinBound(relativePos) ){
					return bound.adjacentRegion;
				}
			}
			return null;
		}
		
		@Override
		public boolean pointIsInRegion(Point relativePosition) {
			for ( RegionBoundary bound : boundsList ){
				if ( !bound.pointIsWithinBound(relativePosition) ){
					return false;
				}
			}
			return true;
		}
		
		public Line2D getSeparation( Point relativeCirclePosition ){
			return new Line2D.Double(
					-relativeCirclePosition.y,
					relativeCirclePosition.x,
					-this.ownerCorner.getY(),
					this.ownerCorner.getX()
			);
		}
		
		public void debugDraw(Point absPos, MovingCamera cam , Graphics2D g2){
			for( RegionBoundary bound : boundsList ){
				bound.draw( absPos, cam , g2 , getFeature().getP2() );
			}
		}
	}
	
// REGION BOUNDARY CLASS
	
	private abstract class RegionBoundary{
		protected VoronoiRegion adjacentRegion;
		public abstract boolean pointIsWithinBound( Point relativePos);
		public abstract void notifySetAngle( double radians );
		public abstract void draw( Point absPos, MovingCamera cam , Graphics2D g2 , Point2D point );
	}
	
	private class RegionBoundaryVertical extends RegionBoundary{
		
		private int x;
		private byte leftRight;
		
		public RegionBoundaryVertical( byte leftRight , int x , VoronoiRegionDefined adjacent){
			this.x = x;
			this.leftRight = leftRight;
			this.adjacentRegion = adjacent;
		}

		@Override
		public boolean pointIsWithinBound( Point relativePos) {
			return ( leftRight*( relativePos.x - x ) >= 0 );
		}
		@Override
		public void notifySetAngle(double radians) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void draw(Point absPos ,MovingCamera cam, Graphics2D g2, Point2D point) {
			cam.drawVerticalLine( x + absPos.x , ""+getFeature() , g2);
			g2.drawString(""+getFeature(), cam.getRelativeX(point.getX()), cam.getRelativeY(point.getY()) );
		}
		
	}
	
	private class RegionBoundarySlope extends RegionBoundary{
		
		private double slope;
		private int yIntercept;
		private byte topBottom;
		
		public RegionBoundarySlope( double slope , int yIntercept, byte topBottom,  VoronoiRegionDefined adjacent ){
			this.slope = slope;
			this.yIntercept = yIntercept;
			this.topBottom = topBottom;
			this.adjacentRegion = adjacent;
		}

		@Override
		public boolean pointIsWithinBound( Point relativePos) {
			return ( topBottom * (slope*( relativePos.x ) + yIntercept - relativePos.y) >= 0 );
		}
		@Override
		public void notifySetAngle(double radians) {
			
			
			
		}
		@Override
		public void draw(Point absPos,MovingCamera cam, Graphics2D g2, Point2D p) {
			cam.drawDebugAxis( this.slope , this.yIntercept + absPos.y , g2);
		}
		
	}
	
	
}
