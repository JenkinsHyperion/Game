package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Vector implements Serializable{
		
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
		
		public double getX(){ return x; }
		public double getY(){ return y; }

		public Vector unitVector(){  
			if ( x+y != 0 ){
				Point2D origin = new Point2D.Double(0,0);
				Point2D endpoint = new Point2D.Double(x,y);
				double unitX = x /  origin.distance( endpoint );
				double unitY = y / origin.distance( endpoint );
				return new Vector( unitX, unitY );
			}
			else	
				return new Vector(0,0);
		}
		
		/**
		 * Returns sign Vector with components -1 , 0 , or 1 that correspond to the input Vector's components. Vector equivalent of Math.signum()
		 */
		public Vector signVector() {
			return new Vector( Math.signum(this.x) , Math.signum(this.y) );
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
		
		public double crossProduct( Vector input){
			
			return  input.getX()*this.getY() - input.getY()*this.getX() ;
		}
		
		public double dotProduct( Vector vector2){ //Returns the magnitude of the projection vector
			
			return (this.getX() ) * (vector2.getX()) + 
					(this.getY() ) * (vector2.getY() );
		}
		
		public Vector projectedOver( Vector base ){
			return base.unitVector().multiply( this.dotProduct(base.unitVector()) );
		}
		
		/**Returns new directional Unit Vector (magnitude of 1) from given Line2D
		 * Please note that all returned Vectors hold no position information
		 * @param line
		 * @return directional Unit Vector with magnitude of 1
		 */
		public Vector unitVectorFromLine2D( Line2D line ){
			return new Vector( line.getX2() - line.getX1() , line.getY2() - line.getY1() ).unitVector() ;
		}
		
		public double calculateAngleFromVector(){
			if ( this.getX() > 0 )
				return Math.PI/2 - Math.atan2( this.getX(), this.getY() );
			else //if ( vector.getX() < 0 )
				return -Math.PI/2 - Math.atan2( this.getX(), this.getY() );  

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

		public double getLength() {
			return new Point2D.Double(0,0).distance( new Point2D.Double( this.x, this.y ) ) ;
		}





	
}
