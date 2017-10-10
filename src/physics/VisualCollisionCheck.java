package physics;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import engine.ReferenceFrame;
import engine.MovingCamera;
import entityComposites.*;
import sprites.RenderingEngine;

public abstract class VisualCollisionCheck extends CollisionCheck{

	protected SeparatingAxisCollector axisCollector;
	
	public abstract boolean check( Collider c1 , Collider c2 ) ;
	public abstract boolean check( Collider c1 , Collider c2 , MovingCamera cam , Graphics2D g2 ) ;
	
	private VisualCollisionCheck(){
	}

	private void setAxisCollector( SeparatingAxisCollector axisCollector ){
		this.axisCollector = axisCollector;
	}
	
	public SeparatingAxisCollector getCollector(){
		return this.axisCollector;
	}
	
	//CHECK MATH FACTORIES
	protected static VisualCollisionCheck polyPoly(){
		VisualCollisionCheck returnCheck = new VisualCollisionCheck.VisualSAT();
		returnCheck.setAxisCollector( SeparatingAxisCollector.polygonPolygon() );
		return returnCheck;
	}

	protected static VisualCollisionCheck circlePoly( EntityStatic circleEntity , EntityStatic polygonEntity , BoundaryPolygonal polygonBoundary ){
		VisualCollisionCheck returnCheck = new VisualCollisionCheck.VisualSAT();
		returnCheck.setAxisCollector( new SeparatingAxisCollector.AxisByRegion( polygonBoundary , polygonEntity , circleEntity) );
		return returnCheck;
	}
	
	protected static VisualCollisionCheck linePoly( EntityStatic lineEntity , EntityStatic polygonEntity , BoundaryLinear line, BoundaryPolygonal polygonBoundary ){
		VisualCollisionCheck returnCheck = new VisualCollisionCheck.VisualSAT();
		returnCheck.setAxisCollector( SeparatingAxisCollector.polygonPolygon() );
		return returnCheck;
	}
	
	protected static VisualCollisionCheck circleCircle( EntityStatic e1, EntityStatic e2 ){
		VisualCollisionCheck returnCheck = new VisualCollisionCheck.VisualSAT();
		returnCheck.setAxisCollector( new SeparatingAxisCollector.AxisByRawDistance(e1,e2) );
		return returnCheck;
	}
	
	// CHECK CLASSES
	
	protected static class VisualSAT extends VisualCollisionCheck{

		@Override
		public boolean check( Collider collidablePrimary , Collider collidableSecondary ) {
			boolean isColliding = true;

		    final Boundary statBoundsRel = collidableSecondary.getBoundary();
		    final Boundary playerBoundsRel = collidablePrimary.getBoundary();
		    
		    final EntityStatic player = collidablePrimary.getOwnerEntity();
		    final EntityStatic stat = collidableSecondary.getOwnerEntity();
	
		    Point2D playerCenter = new Point2D.Double(collidablePrimary.getOwnerEntity().getX(), collidablePrimary.getOwnerEntity().getY());
		    Point2D statCenter = new Point2D.Double(collidableSecondary.getOwnerEntity().getX(), collidableSecondary.getOwnerEntity().getY());
		    
		    final double deltaX = player.getTranslationComposite().getDeltaX(player) ;
		    final double deltaY = player.getTranslationComposite().getDeltaY(player) ;
		    final Point deltaPosition = new Point( (int)deltaX , (int)deltaY );
		    
			  // OPTIMIZATION pull axis collection into initialized field and notify changes rather than rapid calling
		    SeparatingAxisCollector.Axis[] separatingAxes = this.axisCollector.getSeparatingAxes( collidableSecondary, stat.getPosition(), statBoundsRel, collidablePrimary , deltaPosition, playerBoundsRel );

		    
		    for ( SeparatingAxisCollector.Axis separatingAxis : separatingAxes ){
		    	
		    	Line2D axis = separatingAxis.getAxisLine(); 

		    	Point2D[] outerPointsRel = 
		    			Boundary.getNearAndFarPointsBetween(collidablePrimary, collidableSecondary, axis);
		    	
		    	Point2D nearStatCorner = statBoundsRel.farthestLocalPointFromPoint(
		    			stat.getPosition(), outerPointsRel[1], axis
		    			);
		    	
		    	Point2D nearPlayerCorner = playerBoundsRel.farthestLocalPointFromPoint(
		    			deltaPosition, outerPointsRel[0], axis
		    			);
	
		    	Point2D centerStat = BoundaryCorner.getCenter(  nearStatCorner , outerPointsRel[1] );
		    	Point2D centerPlayer = BoundaryCorner.getCenter( nearPlayerCorner , outerPointsRel[0] );
	
		    	Line2D centerDistance = new Line2D.Double( centerPlayer , centerStat );
		    	Line2D centerProjection = Boundary.getProjectionLine(centerDistance, axis);
		    	
		    	// -----------------
	
		    	Line2D playerHalf = new Line2D.Float( 
		    			Boundary.getProjectionPoint(centerPlayer,axis) ,
		    			Boundary.getProjectionPoint(nearPlayerCorner,axis)
		    			);
		    	Line2D statHalf = new Line2D.Float( 
		    			Boundary.getProjectionPoint(centerStat,axis) ,
		    			Boundary.getProjectionPoint(nearStatCorner,axis) 
		    			);

		    	
		    	
		    	double centerDistanceX = (centerProjection.getX1() -  centerProjection.getX2()  );
		    	double centerDistanceY = (centerProjection.getY1() -  centerProjection.getY2()  );
	
		    	double playerProjectionX = (playerHalf.getX1() -  playerHalf.getX2());
		    	double playerProjectionY = (playerHalf.getY1() -  playerHalf.getY2());
	
		    	double statProjectionX = (statHalf.getX2() -  statHalf.getX1());
		    	double statProjectionY = (statHalf.getY2() -  statHalf.getY1()); 
	
		    	double penetrationX = 0;
		    	double penetrationY = 0;  
		    	
	
		    	if (centerDistanceX>0){
		    		//centerDistanceX -= 1;
		    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX +0;
		    	}
		    	else if (centerDistanceX<0){
		    		//centerDistanceX += 1;  //NEEDS HIGHER LEVEL SOLUTION
		    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX -0;
		    	}
		    	else
		    		penetrationX = Math.abs(playerProjectionX) + Math.abs(statProjectionX);
	
		    	//################
		    	
		    	if (centerDistanceY>0){

		    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY+0; 
		    	}
		    	else if (centerDistanceY<0){
		    		//centerDistanceY += 1; 
		    		
		    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY-0; 
		    		
		    	}
		    	else{
		    		penetrationY = Math.abs(playerProjectionY) + Math.abs(statProjectionY);
		    	}
	
		    	//#################
		    	
		    	if ( penetrationX * centerDistanceX < 0 ) //SIGNS ARE NOT THE SAME
					penetrationX = 0;
		    	if ( penetrationY * centerDistanceY < 0 )
					penetrationY = 0; 	
		    	
		    	if ( penetrationX == 0 && penetrationY == 0 ){ 
		    		//return false;
		    		isColliding = false;
		    	}else{
		    		
		    	}

		    	
		    	Line2D projCenter = new Line2D.Double( 
		    			playerHalf.getX1() , 
		    			playerHalf.getY1() , 
		    			playerCenter.getX() , 
		    			playerCenter.getY()
		    	);
		    	Line2D projOuter = new Line2D.Double( 
		    			playerHalf.getX2() , 
		    			playerHalf.getY2() , 
		    			nearPlayerCorner.getX() , 
		    			nearPlayerCorner.getY()
		    	);
		    	Line2D projCenterStat = new Line2D.Double( 
		    			statHalf.getX1() , 
		    			statHalf.getY1() , 
		    			centerStat.getX() , 
		    			centerStat.getY()
		    	);
		    	Line2D projOuterStat = new Line2D.Double( 
		    			statHalf.getX2() , 
		    			statHalf.getY2() , 
		    			nearStatCorner.getX() , 
		    			nearStatCorner.getY()
		    	);

		    }
		    
		    return isColliding;
		    //return false;
		    
		}

		
		
		@Override
		public boolean check( Collider collidablePrimary , Collider collidableSecondary , MovingCamera camera , Graphics2D g2 ) {
			boolean isColliding = true;

		    final Boundary statBoundsRel = collidableSecondary.getBoundary();
		    final Boundary playerBoundsRel = collidablePrimary.getBoundary();
		    
		    final EntityStatic player = collidablePrimary.getOwnerEntity();
		    final EntityStatic stat = collidableSecondary.getOwnerEntity();
	
		    Point2D playerCenter = new Point2D.Double(collidablePrimary.getOwnerEntity().getX(), collidablePrimary.getOwnerEntity().getY());
		    Point2D statCenter = new Point2D.Double(collidableSecondary.getOwnerEntity().getX(), collidableSecondary.getOwnerEntity().getY());
		    
		    final double deltaX = player.getTranslationComposite().getDeltaX(player) ;
		    final double deltaY = player.getTranslationComposite().getDeltaY(player) ;
		    final Point deltaPosition = new Point( (int)deltaX , (int)deltaY );
		    
			  // OPTIMIZATION pull axis collection into initialized field and notify changes rather than rapid calling
		    SeparatingAxisCollector.Axis[] separatingAxes = this.axisCollector.getSeparatingAxes( collidableSecondary, stat.getPosition(), statBoundsRel, collidablePrimary , deltaPosition, playerBoundsRel, camera, g2 );

		    //axisCollector.drawSeparation(camera, g2);
		    /*
		    g2.setColor(Color.WHITE);
		    this.axisCollector.drawSeparation(camera, g2);
		    
		    g2.setColor(Color.CYAN);
		    g2.drawString("Axes of Separations: "+separatingAxes.length, 300, 20);
			*/
		    
		    for ( SeparatingAxisCollector.Axis separatingAxis : separatingAxes ){
		    	
		    	Line2D axis = separatingAxis.getAxisLine(); 
		    	
		    	Point2D[] outerPointsRel = 
		    			//Boundary.getFarthestPointsBetween( player, playerBoundsRel, stat, statBoundsRel, axis );
		    			Boundary.getNearAndFarPointsBetween( collidablePrimary, collidableSecondary, axis);
		    	
		    	Point2D nearPlayerCorner	= outerPointsRel[2];
		    	Point2D nearStatCorner 		= outerPointsRel[3];
		    	/*Point2D nearStatCorner = statBoundsRel.farthestLocalPointFromPoint(
		    			stat.getPosition(), outerPointsRel[1], axis
		    			);
		    	
		    	Point2D nearPlayerCorner = playerBoundsRel.farthestLocalPointFromPoint(
		    			deltaPosition, outerPointsRel[0], axis
		    			);*/
	
		    	Point2D centerStat = BoundaryCorner.getCenter(  nearStatCorner , outerPointsRel[1] );
		    	Point2D centerPlayer = BoundaryCorner.getCenter( nearPlayerCorner , outerPointsRel[0] );
	
		    	Line2D centerDistance = new Line2D.Double( centerPlayer , centerStat );
		    	Line2D centerProjection = Boundary.getProjectionLine(centerDistance, axis);
		    	
		    	// -----------------
	
		    	Line2D playerHalf = new Line2D.Double( 
		    			Boundary.getProjectionPoint(centerPlayer,axis) ,
		    			Boundary.getProjectionPoint(nearPlayerCorner,axis)
		    			);
		    	Line2D statHalf = new Line2D.Double( 
		    			Boundary.getProjectionPoint(centerStat,axis) ,
		    			Boundary.getProjectionPoint(nearStatCorner,axis) 
		    			);
		    	
		    	/*g2.setColor(Color.GREEN);
		    	//Point2D drawPoint = collidableSecondary.absolutePositionOfRelativePoint( outerPointsRel[0] );
		    	camera.drawCrossInWorld( outerPointsRel[2] , g2 );
		    	g2.setColor(Color.RED);
		    	camera.drawCrossInWorld( outerPointsRel[3] , g2 );
		    	//camera.drawCrossInWorld( nearStatCorner, g2 );
	*/
		    	double centerDistanceX = (centerProjection.getX1() -  centerProjection.getX2()  );
		    	double centerDistanceY = (centerProjection.getY1() -  centerProjection.getY2()  );
	
		    	double playerProjectionX = (playerHalf.getX1() -  playerHalf.getX2());
		    	double playerProjectionY = (playerHalf.getY1() -  playerHalf.getY2());
	
		    	double statProjectionX = (statHalf.getX2() -  statHalf.getX1());
		    	double statProjectionY = (statHalf.getY2() -  statHalf.getY1()); 
	
		    	double penetrationX = 0;
		    	double penetrationY = 0;  
		    	
	
		    	if (centerDistanceX>0){
		    		//centerDistanceX -= 1;
		    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX +0;

		    	}
		    	else if (centerDistanceX<0){
		    		//centerDistanceX += 1;  //NEEDS HIGHER LEVEL SOLUTION
		    		penetrationX = playerProjectionX + statProjectionX - centerDistanceX -0;

		    	}
		    	else
		    		penetrationX = Math.abs(playerProjectionX) + Math.abs(statProjectionX);
	
		    	//################
		    	
		    	if (centerDistanceY>0){

		    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY+0; 
		    	}
		    	else if (centerDistanceY<0){
		    		//centerDistanceY += 1; 
		    		
		    		penetrationY = playerProjectionY + statProjectionY - centerDistanceY-0; 
		    		
		    	}
		    	else{
		    		penetrationY = Math.abs(playerProjectionY) + Math.abs(statProjectionY);
		    	}
	
		    	//#################
		    	
		    	//if ( penetrationX * ( playerHalf.getX1()-playerHalf.getX2() ) < 0 ) //If penetration is opposite sign as playerHalf
				//	penetrationX = 0;												//clamp to 0. Rather than haivng negative penetration
		    	
		    	//if ( penetrationY * ( playerHalf.getY1()-playerHalf.getY2() ) < 0 )
				//	penetrationY = 0; 	
		    	
		    	if ( penetrationX * centerDistanceX < 0 ) //If penetration is opposite sign as playerHalf
					penetrationX = 0;												//clamp to 0. Rather than haivng negative penetration
		    	
		    	if ( penetrationY * centerDistanceY < 0 )
					penetrationY = 0; 	
		    	
		    	if ( penetrationX == 0 && penetrationY == 0){ 
		    		//return false;
		    		isColliding = false;
			    	g2.setColor(Color.YELLOW);
		    	}else{
			    	g2.setColor(Color.CYAN);
		    	}
			    	
		    	
		    	g2.setColor(Color.DARK_GRAY);
		    	camera.drawDebugAxis(axis , g2 );   
		    	
		    	camera.drawDebugAxis(statHalf , g2 );
		 
		    	

		    	//camera.drawCrossInWorld(outerPoints[1] , g2);
		    	
		    	Line2D projCenter = new Line2D.Double( 
		    			playerHalf.getX1() , 
		    			playerHalf.getY1() , 
		    			playerCenter.getX() , 
		    			playerCenter.getY()
		    	);
		    	Line2D projOuter = new Line2D.Double( 
		    			playerHalf.getX2() , 
		    			playerHalf.getY2() , 
		    			nearPlayerCorner.getX() , 
		    			nearPlayerCorner.getY()
		    	);
		    	Line2D projCenterStat = new Line2D.Double( 
		    			statHalf.getX1() , 
		    			statHalf.getY1() , 
		    			centerStat.getX() , 
		    			centerStat.getY()
		    	);
		    	Line2D projOuterStat = new Line2D.Double( 
		    			statHalf.getX2() , 
		    			statHalf.getY2() , 
		    			nearStatCorner.getX() , 
		    			nearStatCorner.getY()
		    	);
		    	
		    	
		    	g2.setColor(Color.GREEN);
		    	camera.drawDebugAxis(playerHalf , g2 );
		    	
		    	camera.drawString( "   Depth: "+penetrationX+","+penetrationY , playerHalf.getP1() , g2);
		    	
		    	g2.setColor(Color.BLUE);
		    	camera.drawDebugAxis(statHalf , g2 );
		    	
		    	g2.setColor(Color.DARK_GRAY);
		    	camera.drawDebugAxis(projCenter, g2);
		    	camera.drawDebugAxis(projOuter, g2);
		    	camera.drawDebugAxis(projCenterStat, g2);
		    	camera.drawDebugAxis(projOuterStat, g2);
		
		    }
		    
		    return isColliding;
		    //return false;
		    
		}

	}

	public SeparatingAxisCollector getAxisCollector() {
		return this.axisCollector;
	}
}
