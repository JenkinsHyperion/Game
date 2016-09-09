package physics;

public class CollidingPair {

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
