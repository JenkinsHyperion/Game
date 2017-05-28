package physics;

import java.awt.Point;
import java.awt.geom.Point2D;

import misc.CollisionEvent;

public class BoundaryCorner extends BoundaryVertex{

	private Side endingSide; //Side ending on this vertex (Side whose P2 is this vertex)
	private Side startingSide; // Side starting from this vertex (Side whose P1 is this vertex)
	
	protected BoundaryCorner( Point2D position , Boundary owner ,int ID , CollisionEvent collisionEvent ){
		super(position);
		this.ID = ID;
		this.owner = owner;
		this.setCollisionEvent( collisionEvent );
	}
	
	protected BoundaryCorner( Point2D position , Side CW_side , Side CCW_side , Boundary owner , int ID , CollisionEvent collisionEvent ){
		super(position);
		this.startingSide = CW_side;
		this.endingSide = CCW_side;
		this.ID = ID;
		this.owner = owner;
		this.setCollisionEvent( collisionEvent );
	}
	@Deprecated
	private BoundaryCorner( Point position , Side CW_side , Side CCW_side , Boundary owner , int ID , CollisionEvent collisionEvent ){
		super(position);
		this.startingSide = CW_side;
		this.endingSide = CCW_side;
		this.ID = ID;
		this.owner = owner;
		this.setCollisionEvent( collisionEvent );
	}
	
	public Side getSharedSide( BoundaryCorner vertex2 ){ //LOOK FOR OPTIMIZATION

		if ( this.startingSide.getID() == vertex2.startingSide.getID() ) {
			return this.startingSide;
		}
		else if ( this.startingSide.getID() == vertex2.endingSide.getID() ) {
			return this.startingSide;
		}
		else if ( this.endingSide.getID() == vertex2.startingSide.getID() ) {
			return this.endingSide;
		}
		else if ( this.endingSide.getID() == vertex2.endingSide.getID() ) {
			return this.endingSide;
		}
		else {
			return null;		
		}

	}
	
	@Override
	public void collisionTrigger(){
		//TO DO
	}
	
	public Side getCWSide(){ return endingSide; }
	public Side getCCWSide(){ return startingSide; }
	
	protected void setStartingSide( Side side ){ this.startingSide = side; }
	protected void setEndingSide( Side side ){ this.endingSide = side; }
	
	@Override
	public String toString(){ return "Vertex"+this.ID ; }

	public Point2D getCenter(BoundaryVertex boundaryVertex){ 
		return new Point2D.Double( (this.getX() + boundaryVertex.getX())/2 , (this.getY() + boundaryVertex.getY())/2 );
		
	}
	
	public static Point2D getCenter( Point2D p1 , Point2D p2 ){ 
		return new Point2D.Double( (p1.getX() + p2.getX())/2 , (p1.getY() + p2.getY())/2 );
	}
	
	public static Point2D getCenter( BoundaryVertex p1 , Point2D p2 ){ 
		return new Point2D.Double( (p1.getX() + p2.getX())/2 , (p1.getY() + p2.getY())/2 );
	}
	
	/*@Override
	public Vector getNormal() {
		
	}*/

	@Override
	public boolean debugIsVertex() {
		return true;
	}

	@Override
	public boolean debugIsSide() {
		return false;
	}

}
