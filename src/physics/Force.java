package physics;

import java.awt.Point;
import java.util.ArrayList;

//import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;

public class Force {

	protected int ID = -1;
	
	protected Vector force;
	
	public Force( Vector force , int ID ){
		this.force = force;
		this.ID = ID;
	}
	
	public Force( double x , double y , int ID ){
		this.force = new Vector( x , y );
		this.ID = ID;
	}
	
	public int getID(){
		return this.ID;
	}
	
	public void resetID(){
		this.ID = -1;
	}
	
	public void removeFrom( ArrayList<Force> forceList ){
		forceList.remove( this.ID );
	}
	
	public void setVector( Vector input ){
		this.force = input;
	}
	
	public void setVector( Point input ){
		this.force.setX(input.x);
		this.force.setY(input.y);
	}
	
	public void setVector( double x , double y ){
		this.force.setX(x);
		this.force.setY(y);
	}
	
	public void addVector( Vector input ){
		this.force = this.force.add( input );
	}
	
	public void decrementIndex(){
		this.ID--;
	}
	
	public Vector toVector(){
		return force;
	}

	public Vector getLinearForce() {
		return new Vector ( force.getX() , force.getY() );
	}
	
	@Override
	public String toString() {
		return "FORCE";
	}
	
	private static class StaticNormal extends Force{
		
		public StaticNormal( Vector force , int ID ){
			super( force , ID );
		}
		
	}
	
}
