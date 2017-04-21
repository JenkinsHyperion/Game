package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.sun.org.apache.xalan.internal.utils.FeatureManager.Feature;

import engine.MovingCamera;
import entityComposites.EntityStatic;

public class VoronoiRegion {

	private BoundaryFeature ownerFeature;
	private RegionCheck checkMath;
	
	private double slopeCW;
	private int yInterceptCW;
	
	private double slopeCCW;
	private int yInterceptCCW;
	
	private double slopeBase;
	private int yInterceptBase;
	private byte topBottom;

	private VoronoiRegion( BoundaryFeature feature ){
		this.ownerFeature = feature;
	}
	
	
	public static VoronoiRegion getVoronoiRegion( Side feature ){
			
		VoronoiRegion r = new VoronoiRegion(feature);
			//if (X) //vertical line 
			Line2D side = ((Side)feature).toLine();
			r.slopeCW = -(side.getX2()-side.getX1())/(side.getY2()-side.getY1());
			r.slopeCCW = r.slopeCW;
			
			r.yInterceptCW = (int) (side.getP1().getY() - ( r.slopeCW * side.getP1().getX() ));
			r.yInterceptCCW = (int) (side.getP2().getY() - ( r.slopeCCW * side.getP2().getX() ));
			
			r.slopeBase = (side.getY2()-side.getY1())/(side.getX2()-side.getX1());
			r.yInterceptBase = (int) (side.getY1() - (r.slopeBase * side.getX1()));
			r.topBottom = (byte) Math.signum( side.getX2()-side.getX1() );
			
			r.checkMath = r.new SideCheck(feature);
			
			return r;
	}
	
	public static VoronoiRegion getVoronoiRegion( BoundaryCorner corner ){
		
		VoronoiRegion r = new VoronoiRegion(corner);
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
	
	public boolean entityIsInRegion( EntityStatic entity ){
		return this.checkMath.entityIsInRegion(entity);
	}
	
	public Line2D getSeparationSide(EntityStatic entity){
		return this.checkMath.getSeparation(entity);
	}
	
	//##############################################################################################################
	
	private interface RegionCheck{
		
		public boolean entityIsInRegion( EntityStatic entity );
		public Line2D getSeparation(EntityStatic entity);
		public void debugDraw(MovingCamera cam , Graphics2D g2);
	}
	
	private class SideCheck implements RegionCheck{
		private Side ownerSide;
		public SideCheck(Side ownerSide){
			this.ownerSide = ownerSide;
		}
		@Override
		public boolean entityIsInRegion(EntityStatic entity) {
			Point entityOnFrame = new Point( 
					 entity.getX() ,
					 entity.getY() 
					);
			
			double y =  (slopeCW * ( entity.getX() ) + yInterceptCW);
			double x =  (( y - yInterceptCW ) / slopeCW) ;
			Point point = new Point( (int)x , (int)y);
			
			double y2 =  (slopeCCW * ( entity.getX() ) + yInterceptCCW);
			double x2 =  (( y2 - yInterceptCCW ) / slopeCCW) ;
			Point point2 = new Point( (int)x2 , (int)y2);
			

			boolean baseSide = topBottom * ( entityOnFrame.y - (slopeBase * entityOnFrame.x + yInterceptBase ) ) < 0 ;
			
			int dist = Math.abs( entityOnFrame.y - point.y + entityOnFrame.y - point2.y  ) ; //OPTIMIZE into square check
			int range = Math.abs( point.y - point2.y  ) ;

			if ( dist - range < 0 && baseSide ){ //OPTIMIZE TEST IF FASTER THAN DOUBLE CONDITIONAL
				return true;
			}
			else
				return false;
		}
		
		public Line2D getSeparation(EntityStatic entity){
			return this.ownerSide.toLine();
		}
		
		public void debugDraw(MovingCamera cam , Graphics2D g2){
			cam.debugDraw( ownerSide.toVector().normalRight().toLine(ownerSide.getP1()) ,g2);
			cam.debugDraw( ownerSide.toVector().normalRight().toLine(ownerSide.getP2()) ,g2);
		}
	}
	
	private class CornerCheck implements RegionCheck{ //Corner Check uses normal vectors of adjacent sides
		private BoundaryCorner ownerCorner;
		public CornerCheck(BoundaryCorner ownerCorner){
			this.ownerCorner = ownerCorner;
		}
		@Override
		public boolean entityIsInRegion(EntityStatic entity) { 
			
			Vector e = new Vector( entity.getX() - ((BoundaryCorner)ownerFeature).getX() , 
									entity.getY() - ((BoundaryCorner)ownerFeature).getY()
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
		
		public Line2D getSeparation( EntityStatic entity ){
			return new Line2D.Double(
					-entity.getY(),
					entity.getX(),
					-this.ownerCorner.getY(),
					this.ownerCorner.getX()
			);
		}
		
		public void debugDraw(MovingCamera cam , Graphics2D g2){
			cam.debugDraw( ownerCorner.getStartingSide().toVector().normalRight().toLine(ownerCorner.toPoint()) ,g2);
			cam.debugDraw( ownerCorner.getEndingSide().toVector().normalRight().toLine(ownerCorner.toPoint()) ,g2);
		}
	}
	
	public Point getFeature(){
		return new Point(
				(int)this.ownerFeature.getP1().getX(),
				(int)this.ownerFeature.getP1().getY()
				);
	}
	
	public void debugDrawRegion( MovingCamera camera , Graphics2D g2 ){	
		
		this.checkMath.debugDraw(camera, g2);
	}
	
}
