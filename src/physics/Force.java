package physics;

import java.util.ArrayList;

public class Force {

	private int ID;
	
	private Vector force;
	
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
	
	public void indexShift(){
		this.ID--;
	}
	
	public Vector getVector(){
		return force;
	}
	
}
