package physics;

import java.awt.Point;
import java.awt.geom.Point2D;

import misc.CollisionEvent;

public class Vertex extends BoundaryFeature{

	//private int ID;
	private Point position;
	private Side endingSide; //Side ending on this vertex (Side whose P2 is this vertex)
	private Side startingSide; // Side starting from this vertex (Side whose P1 is this vertex)
	
	private Vertex( Point position , int ID , CollisionEvent collisionEvent ){
		this.position = position;
		this.ID = ID;
		this.setCollisionEvent( collisionEvent );
	}
	
	public Vertex( Point2D position , int ID , CollisionEvent collisionEvent ){
		this.position = new Point( (int)position.getX(), (int)position.getY() );
		this.ID = ID;
		this.setCollisionEvent( collisionEvent );
	}
	
	public Vertex( Point2D position , Side CW_side , Side CCW_side , int ID , CollisionEvent collisionEvent ){
		this.position = new Point( (int)position.getX(), (int)position.getY() );
		this.startingSide = CW_side;
		this.endingSide = CCW_side;
		this.ID = ID;
		this.setCollisionEvent( collisionEvent );
	}
	
	public Point toPoint(){
		return position;
	}
	
	public Side getSharedSide( Vertex vertex2 ){ //LOOK FOR OPTIMIZATION

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
	
	public Side getEndingSide(){ return endingSide; }
	public Side getStartingSide(){ return startingSide; }
	
	protected void setStartingSide( Side side ){ this.startingSide = side; }
	protected void setEndingSide( Side side ){ this.endingSide = side; }
	
	public int getX() { return (int) this.position.getX(); }
	public int getY() { return (int) this.position.getY(); }
	
	protected void setX(int x) { this.position.x = x; }
	protected void setY(int y) { this.position.y = y; }
	protected void setPos(Point point) { this.position = point; }

	public int getID() { return this.ID; }
	
	@Override
	public String toString(){ return "Vertex"+this.ID ; }

	@Override
	public Point2D getP1() {
		return position;
	}

	@Override
	public Point2D getP2() {
		return position;
	}
	
	public Point2D getCenter(Vertex vertex){ 
		return new Point2D.Double( (this.getX() + vertex.getX())/2 , (this.getY() + vertex.getY())/2 );
		
	}

	@Override
	public boolean debugIsVertex() {
		return true;
	}

	@Override
	public boolean debugIsSide() {
		return false;
	}

}
