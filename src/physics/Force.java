package physics;

import java.util.ArrayList;

//import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;

public class Force {

	protected int ID;
	
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
	
	public void removeFrom( ArrayList<Force> forceList ){
		forceList.remove( this.ID );
	}
	
	public void setVector( Vector input ){
		this.force = input;
	}
	
	public void setVector( double x , double y ){
		this.force = new Vector(x,y);
	}
	
	public void addVector( Vector input ){
		this.force = this.force.add( input );
	}
	
	public void indexShift(){
		this.ID = this.ID - 1;
	}
	
	public Vector getVector(){
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
