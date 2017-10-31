package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Vector implements Serializable{
		
		public final static Vector zeroVector = new Vector(0,0);
	
		private double x;
		private double y;
		
		public Vector( double setX, double setY ){
			x = setX;
			y = setY;
		}
		
		public Vector( Point2D p1 , Point2D p2 ){
			this.x = p2.getX() - p1.getX();
			this.y = p2.getY() - p1.getY();
		}
		
		public Vector( Line2D line ){
			this.x = line.getP2().getX() - line.getP1().getX();
			this.y = line.getP2().getY() - line.getP1().getY();
		}
		
		public double getX(){ return x; }
		public double getY(){ return y; }
		public void setX(double newX) {
			this.x = newX;
		}
		public void setY(double newY) {
			this.y = newY;
		}
		
		public void set(double newX, double newY){
			this.y = newY;
			this.x = newX;
		}
		
		public void set( Vector setVector ){
			this.y = setVector.x;
			this.x = setVector.y;
		}
		
		public void setAdd( Vector add ){
			this.y = this.y + add.y;
			this.x = this.x + add.x;
		}
		
		//############################################################################
		//						VECTOR MATH OPERATIONS
		//############################################################################
		
		public Vector unitVector(){  
			if ( x+y != 0 ){
				Point2D origin = new Point2D.Double(0,0);
				Point2D endpoint = new Point2D.Double(x,y);
				double unitX = x /  origin.distance( endpoint );
				double unitY = y / origin.distance( endpoint );
				return new Vector( unitX, unitY );
			}
			else{	
				return new Vector(0,0);
			}
		}
		
		/**
		 * Returns sign Vector with components -1 , 0 , or 1 that correspond to the input Vector's components. Vector equivalent of Math.signum()
		 */
		public Vector signumVector() {
			return new Vector( Math.signum(this.x) , Math.signum(this.y) );
		}
		
		public byte sign( Vector base ){

			  if ( this.y*base.getX() > this.x*base.getY() )
			  { 
			    return -1;
			  }
			  else  if ( this.y*base.getX() < this.x*base.getY() )
			  {
			    return 1;
			  }
			  else
				  return 0;
		}
		
		public boolean isShorterThan( Vector compare ){
			
			if ( 
					this.getX()*this.getX() +  this.getY()*this.getY() < 
					compare.getX()*compare.getX() +  compare.getY()*compare.getY()
			)
				return true;
			else
				return false;
			
		}
		
		public boolean isShorterThan( double length ){
			
			if ( 
					this.getX()*this.getX() +  this.getY()*this.getY() < 
					length*length
			)
				return true;
			else
				return false;
			
		}
		
		/**Returns the absolute value of this Vector, by taking the absolute value of all this Vector's components. 
		 * Note that the returned absolute Vector is always in Quadrant I
		 * @return
		 */
		public Vector abs(){
			double returnX = Math.abs(this.x);
			double returnY = Math.abs(this.y);
			return new Vector( returnX , returnY );
		}
		
		public Vector absX(){
			double returnX = Math.abs(this.x);
			return new Vector( returnX , this.y );
		}
		
		public Vector absY(){
			double returnY = Math.abs(this.y);
			return new Vector( this.x , returnY );
		}
		
		public Vector inverse(){
			double returnX = -this.x;
			double returnY = -this.y;
			return new Vector( returnX , returnY );
		}
		
		public Vector absSlope(){
			if ( this.x * this.y >= 0 ){ //both x and y are + or -
				return this.abs();
			}
			else if ( this.x < 0 ){
				return new Vector( -this.x , -this.y );
			}
			else{
				return new Vector(this.x , this.y);
			}
		}
		
		public Vector inverseX(){
			double returnX = -this.x;
			return new Vector( returnX , this.y );
		}
		
		public Vector inverseY(){
			double returnY = -this.y;
			return new Vector( this.x , returnY );
		}
		
		public Vector add( Vector input ){
			double returnX = this.x + input.getX();
			double returnY = this.y + input.getY();
			return new Vector( returnX , returnY );
		}
		

		public Vector subtract(Vector input) {
			double returnX = this.x - input.getX();
			double returnY = this.y - input.getY();
			return new Vector( returnX , returnY );
		}
		
		public Vector multiply( double input ){
			double returnX = this.x * input;
			double returnY = this.y * input;
			return new Vector( returnX , returnY );
		}
		
		public Vector multiply( Vector input ){
			double returnX = this.x * input.getX();
			double returnY = this.y * input.getY();
			return new Vector( returnX , returnY );
		}
		
		public Vector reciprocal() {
			double returnX = 1/this.x;
			double returnY = 1/this.y;
			return new Vector( returnX , returnY );
		}
		
		public double crossProduct( Vector input){
			
			return  input.getX()*this.getY() - input.getY()*this.getX() ;
		}
		
		public double dotProduct( Vector vector2){ //Returns the magnitude of the projection vector
			
			return (this.getX() ) * (vector2.getX()) + 
					(this.getY() ) * (vector2.getY() );
		}
		public double aDot( Vector vector2){ //Returns the magnitude of the projection vector
			
			return (-this.getY() ) * (vector2.getX()) + 
					(this.getX() ) * (vector2.getY() );
		}

		public Vector projectedOver( Vector base ){
			return base.unitVector().multiply( this.dotProduct(base.unitVector()) );
		}
		/**Returns the projection vector if it is in the same direction as the base vector, otherwise returns a vector of (0,0).
		 * 
		 * @param base
		 * @return
		 */
		public Vector projectedOverClamped( Vector base ){
			
			Vector projection = this.projectedOver(base);
			
			Vector signum2 = new Vector(
				Math.signum(base.x) +   Math.signum( projection.x ),
				Math.signum(base.y) +	Math.signum( projection.y )
			);
			
			return projection.abs().multiply(signum2).unitVector().multiply(projection.getMagnitude());
		}

		
		/**Returns new directional Unit Vector (magnitude of 1) from given Line2D
		 * Please note that all returned Vectors hold no position information
		 * @param line
		 * @return directional Unit Vector with magnitude of 1
		 */
		public Vector unitVectorFromLine2D( Line2D line ){
			return new Vector( line.getX2() - line.getX1() , line.getY2() - line.getY1() ).unitVector() ;
		}
		
		public static double angleBetweenVectors( Vector v1, Vector v2 ){
			
			double dotProduct = v1.dotProduct(v2);
			double aDotProduct = v1.aDot(v2);
			return Math.toDegrees( -Math.atan2( 
					aDotProduct,
					dotProduct
				));
		}
		
		public double angleFromVectorInRadians(){
				return -Math.PI/2 - Math.atan2( this.getX(), this.getY() );  

		}
		
		public double angleFromVectorInDegrees(){
				return Math.toDegrees( -Math.PI/2 - Math.atan2( this.getX(), this.getY() ) );

		}
		
		public double angleFromVector(){
			return Math.toDegrees( Math.PI/2 - Math.atan2( this.getX(), this.getY() ) );

		}
		
		public Vector clamp(){ // OPTIMIZE FIND POSSIBLE MATH SOLUTION FOT THIS
			if ( this.getX() > 0){
				return new Vector( this.x , this.y );
			}
			return new Vector( -this.x , -this.y );
		}
		
		public static Vector unitVectorFromAngle( double angle ){
			return new Vector( Math.sin(angle) , Math.cos(angle) );
		}
		
		public Vector normalRight(){
			double returnX = this.y;
			double returnY = -this.x;
			return new Vector( returnX , returnY );
		}
		
		public Vector normalLeft(){
			double returnX = -this.y;
			double returnY = this.x;
			return new Vector( returnX , returnY );
		}
		
		public Vector scaledBy( double factor ){
			double returnX = this.x*factor;
			double returnY = this.y*factor;
			return new Vector( returnX , returnY );
		}
		
		public Vector scaleYTo( double y ){
			if ( this.getY() != 0 )
				return this.scaledBy( y / this.getY() );
			else
				return this;
			
		}
		
		public Vector bisectingVector( Vector input ){
			return ( input.unitVector().add(this.unitVector() ) ) ;
		}
		
		public Line2D toLine(Point2D origin){
			return new Line2D.Double(
					origin.getX(),
					origin.getY(),
					origin.getX()+this.x,
					origin.getY()+this.y					
					);
		}
		
		@Override
		public String toString(){
			return "Vector("+ x + "," + y +")";
		}

		public int getIntegerMagnitude() {
			return (int)( Math.sqrt( x*x + y*y ) );
		}
		
		public double getMagnitude() {
			//return new Point2D.Double(0,0).distance( new Point2D.Double( this.x, this.y ) ) ;
			return  Math.sqrt( x*x + y*y ) ;
		}

		public static Line2D lineRotatedBy( Line2D line, double radians ){
			
			double returnX = line.getX2() - line.getX1();
			double returnY = line.getY2() - line.getY1(); 
			
			double cosineTheta = Math.cos( radians );
			double sineTheta = Math.sin( radians );
			
			Point2D returnPoint = new Point2D.Double(
					( returnX*cosineTheta - returnY*sineTheta ),
					( returnX*sineTheta + returnY*cosineTheta )
			);
			
			return new Line2D.Double(
					0,
					0,
					returnPoint.getX(),
					returnPoint.getY()
					);
			
		}

		public static Point2D relativePointRotatedBy( Point p, double radians ){
			
			double returnX = p.x;
			double returnY = p.y; 
			
			double cosineTheta = Math.cos( radians );
			double sineTheta = Math.sin( radians );
			
			Point2D returnPoint = new Point2D.Double(
					( returnX*cosineTheta - returnY*sineTheta ),
					( returnX*sineTheta + returnY*cosineTheta )
			);
			
			return returnPoint;
			
		}
		
		public static Point2D relativePointRotatedBy( Point2D p, double radians ){
			
			double returnX = p.getX();
			double returnY = p.getY(); 
			
			double cosineTheta = Math.cos( radians );
			double sineTheta = Math.sin( radians );
			
			Point2D returnPoint = new Point2D.Double(
					( returnX*cosineTheta - returnY*sineTheta ),
					( returnX*sineTheta + returnY*cosineTheta )
			);
			
			return returnPoint;
			
		}

	
}
