package physics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import engine.MovingCamera;
import entityComposites.AngularComposite;
import entityComposites.EntityStatic;
import entityComposites.TranslationComposite;

public abstract class SeparatingAxisCollector {

	public static final AxesByPolygonFeatures poly = new AxesByPolygonFeatures();
	
	public abstract Axis[] getSeparatingAxes( EntityStatic e1, Point pos1 , Boundary b1, EntityStatic e2, Point pos2 , Boundary b2 );
	
	public abstract Axis[] getSeparatingAxes( EntityStatic e1, Point pos1 , Boundary b1, EntityStatic e2, Point pos2 , Boundary b2, MovingCamera cam, Graphics2D g2 );
	
	public abstract void drawSeparation( MovingCamera cam , Graphics2D g2 );
	
	public static SeparatingAxisCollector polygonPolygon(){
		return poly;
	}
	
	private static Line2D sideToSeparation( Line2D side ){
		if ( side.getP1().getX() == side.getP2().getX() ) { //line is vertical
			return new Line2D.Double( 0 , side.getY1() , 100 , side.getY1() ); //return normal line which is horizontal with slope 0
		}else{
			//return normal line, whose slope is inverse reciprocal of line.   -(1/slope)
			return new Line2D.Double( 0 , 0 , (side.getY2() - side.getY1() ), -(side.getX2() - side.getX1() ) );
		}
	}
	
	public static class AxisByRawDistance extends SeparatingAxisCollector{
		EntityStatic e1;
		EntityStatic e2;
		public AxisByRawDistance(EntityStatic e1, EntityStatic e2){
			this.e1 = e1;
			this.e2 = e2;
		}
		
		@Override
		public Axis[] getSeparatingAxes( EntityStatic e1, Point pos1 , Boundary b1, EntityStatic e2, Point pos2, Boundary b2, MovingCamera cam, Graphics2D g2) {
			return constructNewAxes(e1, e2, b1, b2, pos1, pos2);
		}
		
		@Override	
		public Axis[] getSeparatingAxes( EntityStatic e1, Point pos1 , Boundary b1, EntityStatic e2, Point pos2, Boundary b2) {
			return constructNewAxes(e1, e2, b1, b2, pos1, pos2);
		}
		
		private Axis[] constructNewAxes( EntityStatic e1, EntityStatic e2, Boundary b1, Boundary b2, Point pos1, Point pos2 ){
			
			final Line2D axisLine = new Line2D.Double( 0 , 0 ,pos2.getX()-pos1.getX() , pos2.getY()-pos1.getY()  );
					
			final Point2D[] outerPointsRel = Boundary.getFarthestPointsBetween( e1, b1, e2, b2, axisLine );
			
			final Point2D nearPrimaryPoint = b1.farthestLocalPointFromPoint(
	    			pos1, outerPointsRel[0], axisLine
	    			);

			final Point2D nearSecondaryPoint = b2.farthestLocalPointFromPoint(
	    			pos2, outerPointsRel[1], axisLine
	    			);
			
			
			BoundaryFeature[] nearStatVertex = b2.farthestFeatureFromPoint( 
					e2.getPosition(), e1.getPosition(), outerPointsRel[1] , axisLine 
		    		); 
	    	BoundaryFeature[] nearPlayerVertex = b1.farthestFeatureFromPoint( 
	    			e1.getPosition(), e2.getPosition(), outerPointsRel[0] , axisLine
		    		);
			
			return new Axis[]{ 		
				new Axis( axisLine, nearPrimaryPoint, nearSecondaryPoint, nearPlayerVertex[0] )
			};
		}
		
		@Override
		public void drawSeparation(MovingCamera cam, Graphics2D g2) {
			// TODO Auto-generated method stub
		}
	}
	
	public static class AxesByPolygonFeatures extends SeparatingAxisCollector{

		@Override
		public Axis[] getSeparatingAxes( EntityStatic e1, Point pos1 , Boundary b1, EntityStatic e2, Point pos2, Boundary b2, MovingCamera cam, Graphics2D g2) {
			return getSeparatingAxes(e1, pos1, b1, e2, pos2, b2);
		}
		
		@Override
		public Axis[] getSeparatingAxes( EntityStatic e1, Point pos1 , Boundary b1, EntityStatic e2, Point pos2, Boundary b2) {
			Line2D[] sides = Boundary.getSeparatingSidesBetween( b1 , b2 );
			Axis[] returnAxes = new Axis[ sides.length ];
			for ( int i = 0 ; i < sides.length ; i++ ){
			
				final Line2D axisLine = sideToSeparation( sides[i] ); 
				
				final Point2D[] outerPointsRel = Boundary.getFarthestPointsBetween( e1, b1, e2, b2, sideToSeparation( sides[i] ) );
				
				final Point2D nearStatCorner = b2.farthestLocalPointFromPoint(
		    			pos2, outerPointsRel[1], axisLine
		    			);
		    	
				final Point2D nearPlayerCorner = b1.farthestLocalPointFromPoint(
		    			pos1, outerPointsRel[0], axisLine
		    			);
				
				
				BoundaryFeature[] nearCircleVertex = b2.farthestFeatureFromPoint( 
						e2.getPosition(), e1.getPosition(), outerPointsRel[1] , axisLine 
			    		); 
		    	BoundaryFeature[] nearPolygonVertices = b1.farthestFeatureFromPoint( 
		    			e1.getPosition(), e2.getPosition(), outerPointsRel[0] , axisLine
			    		);

		    	BoundaryFeature featurePolygon = null;
		    	BoundaryFeature featureCircular = null;
		    	
		    	
		    	featureCircular = nearCircleVertex[0];
		    	if ( nearCircleVertex.length > 1 ){ 
		    		featureCircular = ((BoundaryCorner)nearCircleVertex[0]).getSharedSide( ((BoundaryCorner)nearCircleVertex[1]) );
		    	}
				

		    	featurePolygon = nearPolygonVertices[0];

		    	if ( nearPolygonVertices.length > 1 ){ //two corners of side are equal distance
		    		featurePolygon = ((BoundaryCorner)nearPolygonVertices[0]).getSharedSide( ((BoundaryCorner)nearPolygonVertices[1]) );
		    	}
		    	else{ //only one corner is closest
		    		featurePolygon = nearPolygonVertices[0];
		    	}
				
					
				returnAxes[i] = new Axis( axisLine, nearPlayerCorner, nearStatCorner, featurePolygon );
				
			}
			return returnAxes;
		}
		
		@Override
		public void drawSeparation(MovingCamera cam, Graphics2D g2) {
			// TODO Auto-generated method stub
		}
	}
	
	public static class AxisByRegion extends SeparatingAxisCollector{
		
		Boundary regionBoundary;
		VoronoiRegion currentRegion;
		EntityStatic nonPolygon;
		EntityStatic polygon;
		
		protected AxisByRegion( Boundary region1 , EntityStatic polygon , EntityStatic nonPolygon){
			this.regionBoundary = region1;
			regionBoundary.constructVoronoiRegions();
			this.nonPolygon = nonPolygon;
			this.polygon = polygon;

			for ( VoronoiRegion region : regionBoundary.getVoronoiRegions() ){
				if ( region.pointIsInRegion( polygon.getRelativeTranslationalPositionOf(nonPolygon) ) ){ 
					currentRegion = region;
				}
			}
		}
		
		@Override
		public Axis[] getSeparatingAxes( EntityStatic e1, Point pos1 , Boundary b1, EntityStatic e2, Point pos2, Boundary b2, MovingCamera cam, Graphics2D g2) {

			//currentRegion.debugDrawRegion(EntityStatic.origin, cam, g2);
			//cam.drawCrossInWorld(relativePoint , g2);
			
			return getSeparatingAxes(e1, pos1, b1, e2, pos2, b2);
		}
		
		@Override
		public Axis[] getSeparatingAxes( EntityStatic e1, Point pos1 , Boundary b1, EntityStatic e2, Point pos2, Boundary b2) {

			Point relativePoint = polygon.getRelativePositionOf(nonPolygon);
			
			VoronoiRegion changedRegion = currentRegion.getEscapedRegion( relativePoint );
			
			if ( changedRegion == null ){
				
				return constructNewAxes(e1, e2, b1, b2, pos1, pos2);
			}
			else{
				currentRegion = changedRegion;
				return constructNewAxes(e1, e2, b1, b2, pos1, pos2);
			}				
		}
		
		private Axis[] constructNewAxes( EntityStatic e1, EntityStatic e2, Boundary b1, Boundary b2, Point pos1, Point pos2 ){
			
			final Line2D axisLine = sideToSeparation( currentRegion.constructDistanceLine( polygon.getRelativeTranslationalPositionOf(nonPolygon)));

			final Point2D[] outerPointsRel = Boundary.getFarthestPointsBetween( e1, b1, e2, b2, axisLine );
			
			//final Point2D nearPrimaryPoint = b1.farthestLocalPointFromPoint(
	    	//		pos1, outerPointsRel[0], axisLine
	    	//		);
			
			Point2D relative = currentRegion.getFeaturePoint(  polygon.getRelativeTranslationalPositionOf(nonPolygon)  ); //closest polygon feature is the owner of the region
			
			final Point2D nearPrimaryPoint = b2.farthestLocalPointFromPoint(
	    			pos2, outerPointsRel[1], axisLine
	    			);
			
			final Point2D nearSecondaryPoint = Boundary.shiftPoint(relative, pos2);
			
			return new Axis[]{ 		
				new Axis( axisLine, nearPrimaryPoint, nearSecondaryPoint, currentRegion.getFeature() )
			};
		}
		
		@Override
		public void drawSeparation(MovingCamera cam, Graphics2D g2) {
			currentRegion.debugDrawRegion( polygon.getPosition(), cam, g2);
			g2.drawString(" Region "+currentRegion.getFeature()+" with "+ currentRegion.debugNumberOfBounds() +" bounds", 100, 100);
			
		}
	}
	
	public class Axis {
		
		private Line2D axis; //Convert to slope intercept doubles
		private double slope;
		private double yIntercept;
		private Point2D nearPrimary;
		private Point2D nearSecondary;
		private BoundaryFeature nearPrimaryFeature;
		private BoundaryFeature nearSecondaryFeature;
		
		protected Axis( Line2D axis, Point2D nearPrimary , Point2D nearSecondary, BoundaryFeature nearSecondaryFeature ){
			this.axis = axis;
			this.nearPrimary = nearPrimary;
			this.nearSecondary = nearSecondary;
			this.nearSecondaryFeature = nearSecondaryFeature;
			//this.nearPrimaryFeature = nearPrimaryFeature
		}
		
		public Line2D getAxisLine(){
			return this.axis;
		}
		
		public Point2D getNearPointPrimary() {
			return this.nearPrimary;
		}
		public BoundaryFeature getNearFeatureSecondary() {
			return this.nearSecondaryFeature;
		}
		public Point2D getNearPointSecondary() {
			return this.nearSecondary;
		}
		public BoundaryFeature getNearFeaturePrimary() {
			return this.nearPrimaryFeature;
		}
	}
	
}
