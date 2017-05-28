package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


import engine.MovingCamera;
import entityComposites.EntityStatic;

public class VoronoiRegionDefined extends VoronoiRegion{
	
	private RegionBoundary[] bounds = new RegionBoundary[0];
	
	public VoronoiRegionDefined( BoundaryFeature feature ){
		super(feature);
	}
	
	protected void addRegionBoundary( RegionBoundary addBound ){
		RegionBoundary[] newBounds = new RegionBoundary[ bounds.length +1 ];
		for ( int i = 0 ; i < bounds.length ; i++ ){
			newBounds[i] = bounds[i];
		}
		newBounds[ newBounds.length-1 ] = addBound;
		bounds = null;
		bounds = newBounds;
	}
	
	public static void addSideOuterBounds( VoronoiRegionDefined cornerCCW , VoronoiRegionDefined region, VoronoiRegionDefined cornerCW , Side side ){ //CONSTRCT ADJACENT CORNERS
		
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

		Side sideCCW = (Side) (regionCCW.getFeature()) ;
		Side sideCW = (Side) (regionCW.getFeature()) ;
		
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
		
		Side side1 = (Side) (sideRegion1.getFeature()) ;
		Side side2 = (Side) (sideRegion2.getFeature()) ;
		
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
	
	//##############################################################################################################
	// BOUNDARY CHECKS
	
	private class GeneralSideCheck implements RegionCheck{ //Special case horizontal side where CW and CCW sides will be vertical
		private Side side;
		public GeneralSideCheck(Side side){
			this.side = side;
		}
		
		@Override
		public VoronoiRegion pointIsOutsideRegion( Point point , Point localPos) {
			for ( RegionBoundary bound : bounds ){
				if ( !bound.pointIsWithinBound(point , localPos) ){ 
					return bound.adjacentRegion;
				}
			}
			return null ;
		}
		
		@Override
		public boolean pointIsInRegion(Point point , Point localPos){
			for ( RegionBoundary bound : bounds ){
				if ( !bound.pointIsWithinBound(point,localPos) ){
					return false;
				}
			}
			return true;
		}

		@Override
		public Line2D getSeparation( Point center ) {
			return this.side.toLine();
		}

		@Override
		public void debugDraw(MovingCamera cam, Graphics2D g2) {
			for( RegionBoundary bound : bounds ){
				bound.draw( cam , g2 , ownerFeature.getP2() );
			}
		}
		
	}
	
	private class CornerCheck implements RegionCheck{ //Corner Check uses normal vectors of adjacent sides
		private BoundaryCorner ownerCorner;
		public CornerCheck(BoundaryCorner ownerCorner){
			this.ownerCorner = ownerCorner;
		}
		
		@Override
		public VoronoiRegion pointIsOutsideRegion( Point point , Point localPos ) { 
			
			for ( RegionBoundary bound : bounds ){
				if ( !bound.pointIsWithinBound(point,localPos) ){
					return bound.adjacentRegion;
				}
			}
			return null;
		}
		
		@Override
		public boolean pointIsInRegion(Point point , Point localPos){
			for ( RegionBoundary bound : bounds ){
				if ( !bound.pointIsWithinBound(point , localPos) ){
					return false;
				}
			}
			return true;
		}
		
		public Line2D getSeparation( Point center ){
			return new Line2D.Double(
					-center.y,
					center.x,
					-this.ownerCorner.getY(),
					this.ownerCorner.getX()
			);
		}
		
		public void debugDraw(MovingCamera cam , Graphics2D g2){
			for( RegionBoundary bound : bounds ){
				bound.draw( cam , g2 , ownerFeature.getP2() );
			}
		}
	}
	
// REGION BOUNDARY CLASS
	
	private abstract class RegionBoundary{
		protected VoronoiRegion adjacentRegion;
		public abstract boolean pointIsWithinBound( Point point , Point localPos);
		public abstract void draw( MovingCamera cam , Graphics2D g2 , Point2D point );
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
		public boolean pointIsWithinBound( Point point , Point localPos) {
			return ( leftRight*(point.x - x) >= 0 );
		}
		
		@Override
		public void draw(MovingCamera cam, Graphics2D g2, Point2D point) {
			cam.drawVerticalLine( x , ""+ownerFeature , g2);
			g2.drawString(""+ownerFeature, cam.getRelativeX(point.getX()), cam.getRelativeY(point.getY()) );
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
		@Deprecated
		public RegionBoundarySlope( Side side , Side side2 ){

			Vector bisection = side.toVector().bisectingVector( side2.toVector().inverse() );
			slope = bisection.getY() / bisection.getX();
			if ( bisection.getX() >= 0){
				topBottom = -1;
			}else{
				topBottom = 1;
			}
			
			if (side.getX1() - side.getX2() != 0){ //side 1 is not vertical
				double m1 = (side.getY2() - side.getY1() )/( side.getX2() - side.getX1() );
				int b1 = (int) (side.getY1() - m1 * side.getX1());

				if (side2.getX1() - side2.getX2() != 0){ // side 1 and side 2 both arent vertical
					double m2 = side2.getY2() - side2.getY1() / side2.getX2() - side2.getX1() ;
					int b2 = (int) (side2.getY1() - m2 * side2.getX1());
					
					int intersectX = (int) (( b2 - b1 )/( m1 - m2 ));
					int interceptY = (int) (m1 * intersectX + b1);
					yIntercept = (int) (interceptY - (slope * intersectX));
				} // side 2 is vertical, side 1 isnt
				else{ 
					int interceptY = (int) (m1 * side2.getX1() + b1);
					yIntercept = (int) (interceptY - (slope * side2.getX1()) );
				}
			}
			else{ // side 1 is vertical
				
				if (side2.getX1() - side2.getX2() != 0){ // side 1 is vertical, side 2 isnt
					double m2 = (side2.getY2() - side2.getY1()) / (side2.getX2() - side2.getX1()) ;
					int b2 = (int) (side2.getY1() - m2 * side2.getX1());
					
					int interceptY = (int) ( m2 * side.getX1() + b2 );
					yIntercept = (int) (interceptY - (slope * side.getX1()) );
					
				} // both sides are vertical
				else{ 
					
				}
				
			}
			
		}
		@Override
		public boolean pointIsWithinBound( Point point , Point localPos){
			return ( topBottom * (slope*point.x + yIntercept - point.y) >= 0 );
		}
		
		@Override
		public void draw(MovingCamera cam, Graphics2D g2, Point2D p) {
			cam.drawDebugAxis( this.slope , this.yIntercept , g2);
		}
		
	}
	
	
}
