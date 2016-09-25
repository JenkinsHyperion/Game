package physics;

import java.io.Serializable;

public class CollidingPair implements Serializable {

	Collision collision;
	boolean pairID;
	
	public CollidingPair(Collision invovledCollision , boolean pairIndex){
		
		collision = invovledCollision;
		pairID = pairIndex;	
	}
	
	public boolean pairID(){
		return pairID;
	}
	
	public boolean partnerID(){
		return !pairID;
	}
	
	public Collision collision(){
		return collision;
	}
	
}
