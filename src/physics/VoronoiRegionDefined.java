package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


import engine.MovingCamera;
import entityComposites.EntityStatic;

public class VoronoiRegionDefined extends VoronoiRegion{
	
	private double slopeCW;
	private int yInterceptCW;
	
	private double slopeCCW;
	private int yInterceptCCW;
	
	private double slopeBase;
	private int yInterceptBase;
	private byte topBottom;

	private VoronoiRegionDefined( BoundaryFeature feature ){
		super(feature);
	}
	
	
	public static VoronoiRegionDefined getVoronoiRegion( Side feature ){
			
		VoronoiRegionDefined r = new VoronoiRegionDefined(feature);
			//if (X) //vertical line 
			if ( feature.getY1() - feature.getY2() == 0 ){ //horizontal side
				r.checkMath = r.new HorizontalSideCheck(feature);
				r.topBottom = (byte) Math.signum( feature.getX2()-feature.getX1() );
			}
			else if ( feature.getX1() - feature.getX2() == 0 ){
				r.checkMath = r.new VerticalSideCheck(feature);
				r.topBottom = (byte) Math.signum( feature.getY2()-feature.getY1() );
			}
			else {
		
				Line2D side = ((Side)feature).toLine();
				r.slopeCW = -(side.getX2()-side.getX1())/(side.getY2()-side.getY1());
				r.slopeCCW = r.slopeCW;
				
				r.yInterceptCW = (int) (side.getP1().getY() - ( r.slopeCW * side.getP1().getX() ));
				r.yInterceptCCW = (int) (side.getP2().getY() - ( r.slopeCCW * side.getP2().getX() ));
				
				r.slopeBase = (side.getY2()-side.getY1())/(side.getX2()-side.getX1());
				r.yInterceptBase = (int) (side.getY1() - (r.slopeBase * side.getX1()));
				r.topBottom = (byte) Math.signum( side.getX2()-side.getX1() );
				
				r.checkMath = r.new SideCheck(feature);
			}
			
			return r;
	}
	
	public static VoronoiRegionDefined getVoronoiRegion( BoundaryCorner corner ){
		
		VoronoiRegionDefined r = new VoronoiRegionDefined(corner);
			//if (X) //vertical line 
			Line2D sideCCW = corner.getStartingSide().toLine(); 
			Line2D sideCW = corner.getEndingSide().toLine();
			
			r.slopeCW = -(sideCW.getX2()-sideCW.getX1())/(sideCW.getY2()-sideCW.getY1()); //normals
			r.slopeCCW = -( sideCCW.getX2()-sideCCW.getX1() )/(sideCCW.getY2()-sideCCW.getY1());
			
			r.yInterceptCW = (int) (sideCW.getY1() - ( r.slopeCW * sideCW.getX1() )); //starting point of CW side is P1
			r.yInterceptCCW = (int) (sideCCW.getY2() - ( r.slopeCCW * sideCCW.getX2() )); //ending point of CCW side is P2
		
			r.slopeBase = r.slopeCCW;
			r.yInterceptBase = r.yInterceptCCW; //OPTIMIZE separate checks into vertex and side checks if worthy
			r.topBottom = (byte) Math.signum( sideCCW.getX2()-sideCCW.getX1() ); 
			
			r.checkMath = r.new CornerCheck(corner);
			
			return r;
	}
	
	//##############################################################################################################
	

	
	private class SideCheck implements RegionCheck{
		private Side ownerSide;
		public SideCheck(Side ownerSide){
			this.ownerSide = ownerSide;
		}
		
		@Override
		public boolean pointIsInRegion( Point point ) {
			
			double y =  (slopeCW * ( point.x ) + yInterceptCW);
			double x =  (( y - yInterceptCW ) / slopeCW) ;
			
			double y2 =  (slopeCCW * ( point.x ) + yInterceptCCW);
			double x2 =  (( y2 - yInterceptCCW ) / slopeCCW) ;
			

			boolean baseSide = topBottom * ( point.y - (slopeBase * point.x + yInterceptBase ) ) <= 0 ;
			
			double dist = Math.abs( point.y - y + point.y - y2 ) ; //OPTIMIZE into square check
			double range = Math.abs( y - y2  );
			
			if ( dist - range <= 2 && baseSide ){ //OPTIMIZE TEST IF FASTER THAN DOUBLE CONDITIONAL
				return true;
			}
			else{
				return false;
			}
		}
		
		public Line2D getSeparation( Point center ){
			return this.ownerSide.toLine();
		}
		
		public void debugDraw(MovingCamera cam , Graphics2D g2){
			//cam.drawDebugAxis(slopeCW, 0, g2);
			//cam.drawDebugAxis(slopeCCW, 0, g2);
			
			cam.debugDraw( ownerSide.toVector().normalRight().toLine(ownerSide.getP1()) ,g2);
			cam.debugDraw( ownerSide.toVector().normalRight().toLine(ownerSide.getP2()) ,g2);
		}
	}
	
	private class VerticalSideCheck implements RegionCheck{

		private Side side;
		public VerticalSideCheck(Side side){
			this.side = side;
		}
		
		@Override
		public boolean pointIsInRegion(Point point) {
			
			int dist = Math.abs( point.y - side.getY1() + point.y - side.getY2()  ) ;
			int range = Math.abs( side.getY1() - side.getY2()  ) ;
			if ( 
					dist < range &&
					topBottom*( point.x - side.getX1() ) >= 0
					){
				return true;
			}
			else{
				return false;
			}
		}

		@Override
		public Line2D getSeparation( Point center ) {
			return this.side.toLine();
		}

		@Override
		public void debugDraw(MovingCamera cam, Graphics2D g2) {
			cam.debugDraw( side.toVector().normalRight().toLine(side.getP1()) ,g2);
			cam.debugDraw( side.toVector().normalRight().toLine(side.getP2()) ,g2);
		}
		
	}
	
	private class HorizontalSideCheck implements RegionCheck{ //Special case horizontal side where CW and CCW sides will be vertical
		private Side side;
		public HorizontalSideCheck(Side side){
			this.side = side;
		}
		
		@Override
		public boolean pointIsInRegion( Point point ) {
			
			int dist = Math.abs( point.x - side.getX1() + point.x - side.getX2()  ) ;
			int range = Math.abs( side.getX1() - side.getX2()  ) ;
			if ( 
					dist < range &&
					topBottom*( point.y - side.getY1() ) <= 0
					){
				return true;
			}
			else{
				return false;
			}
		}

		@Override
		public Line2D getSeparation( Point center ) {
			return this.side.toLine();
		}

		@Override
		public void debugDraw(MovingCamera cam, Graphics2D g2) {
			cam.debugDraw( side.toVector().normalRight().toLine(side.getP1()) ,g2);
			cam.debugDraw( side.toVector().normalRight().toLine(side.getP2()) ,g2);
		}
		
	}
	
	private class CornerCheck implements RegionCheck{ //Corner Check uses normal vectors of adjacent sides
		private BoundaryCorner ownerCorner;
		public CornerCheck(BoundaryCorner ownerCorner){
			this.ownerCorner = ownerCorner;
		}
		
		@Override
		public boolean pointIsInRegion( Point point ) { 
			
			Vector e = new Vector( point.x - ((BoundaryCorner)ownerFeature).getX() , 
									point.y - ((BoundaryCorner)ownerFeature).getY()
					);
			Vector cw = ownerCorner.getStartingSide().toVector().normalLeft();
			Vector ccw = ownerCorner.getEndingSide().toVector().normalLeft();
			
			//if(AxB * AxC >= 0 && CxB * CxA >=0)
			if(	cw.crossProduct(e) * cw.crossProduct(ccw) <= 0   &&
				ccw.crossProduct(e) * ccw.crossProduct(cw) <=0
			){
				return true;
			}else
				return false;
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
			cam.debugDraw( ownerCorner.getStartingSide().toVector().normalRight().toLine(ownerCorner.toPoint()) ,g2);
			cam.debugDraw( ownerCorner.getEndingSide().toVector().normalRight().toLine(ownerCorner.toPoint()) ,g2);
		}
	}
	
}
