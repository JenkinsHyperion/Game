package physics;

public class Vector {
		
		private int x;
		private int y;
		
		public Vector( int setX, int setY ){
			x = setX;
			y = setY;
		}
		
		public Vector( float setX, float setY ){
			x = (int) setX;
			y = (int) setY;
		}
		
		public Vector( double setX, double setY ){
			x = (int) setX;
			y = (int) setY;
		}
		
		public int getX(){ return x; }
		public int getY(){ return y; }

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
