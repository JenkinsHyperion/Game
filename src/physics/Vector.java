package physics;

import java.io.Serializable;

public class Vector implements Serializable{
		
		private double x;
		private double y;
		
		public Vector( double setX, double setY ){
			x = setX;
			y = setY;
		}
		
		public double getX(){ return x; }
		public double getY(){ return y; }

		public boolean isShorterThan( Vector compare ){
			
			if ( 
					this.getX()*this.getX() +  this.getY()*this.getY() < 
					compare.getX()*compare.getX() +  compare.getY()*compare.getY()
			)
				return true;
			else
				return false;
			
		}
		
		public boolean isShorterThan( int length ){
			
			if ( 
					this.getX()*this.getX() +  this.getY()*this.getY() < 
					length*length
			)
				return true;
			else
				return false;
			
		}
		
	
	
}
