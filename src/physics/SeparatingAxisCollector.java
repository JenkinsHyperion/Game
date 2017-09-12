package physics;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import engine.MovingCamera;
import entityComposites.EntityStatic;

public abstract class SeparatingAxisCollector {

	public static final AxesByPolygonFeatures poly = new AxesByPolygonFeatures();
	
	public static SeparatingAxisCollector polygonPolygon(){
		return poly;
	}
	
	public abstract Line2D[] getSeparatingAxes( Boundary b1 , Boundary b2, MovingCamera cam , Graphics2D g2 );
	
	public static class AxisByRawDistance extends SeparatingAxisCollector{
		EntityStatic e1;
		EntityStatic e2;
		public AxisByRawDistance(EntityStatic e1, EntityStatic e2){
			this.e1 = e1;
			this.e2 = e2;
		}
		public Line2D[] getSeparatingAxes( Boundary b1 , Boundary b2 , MovingCamera cam , Graphics2D g2){
		    return new Line2D[]{ new Line2D.Float( 0 , 0 , e1.getY()-e2.getY() , -e1.getX()+e2.getX()  ) };
		}
	}
	
	public static class AxesByPolygonFeatures extends SeparatingAxisCollector{
		
		public Line2D[] getSeparatingAxes( Boundary b1 , Boundary b2 , MovingCamera cam , Graphics2D g2){
		    return Boundary.getSeparatingSidesBetween( b1 , b2 );
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
				if ( region.pointIsInRegion( nonPolygon.getPosition() , polygon.getPosition() ) ){ 
					currentRegion = region;
					//System.out.println(" In "+region.ownerFeature.toString() );
				}else{
					//System.out.println(" Out "+region.ownerFeature.toString() );
				}
			}
		}
		
		public Line2D[] getSeparatingAxes( Boundary b1, Boundary b2 , MovingCamera cam , Graphics2D g2){

			VoronoiRegion changedRegion = currentRegion.getEscapedRegion(  polygon.getRelativeTranslationalPositionOf(nonPolygon) );
			
			if ( changedRegion == null ){
				return new Line2D[]{ currentRegion.constructDistanceLine( polygon.getRelativeTranslationalPositionOf(nonPolygon) ) };
			}
			else{
				currentRegion = changedRegion;
				return new Line2D[]{ currentRegion.constructDistanceLine( polygon.getRelativeTranslationalPositionOf(nonPolygon) ) };
			}
		}
		
		public Line2D[] getAndDrawSeparatingAxes( Boundary b1, Boundary b2 , MovingCamera cam , Graphics2D g2){
			
			currentRegion.debugDrawRegion(cam, g2);
			g2.drawString(" Region "+currentRegion.ownerFeature+" with "+ currentRegion.debugNumberOfBounds() +" bounds", 100, 100);
			
			VoronoiRegion changedRegion = currentRegion.getEscapedRegion(  polygon.getRelativeTranslationalPositionOf(nonPolygon) );
			
			if ( changedRegion == null ){
				return new Line2D[]{ currentRegion.constructDistanceLine( polygon.getRelativeTranslationalPositionOf(nonPolygon) ) };
			}
			else{
				currentRegion = changedRegion;
				return new Line2D[]{ currentRegion.constructDistanceLine( polygon.getRelativeTranslationalPositionOf(nonPolygon) ) };
			}
		}
	}
	
}
