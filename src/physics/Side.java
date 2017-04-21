package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import entityComposites.EntityStatic;
import misc.CollisionEvent;

public class Side extends BoundaryFeature{

	//private int ID;
	Line2D line;
	private BoundaryVertex startpoint;
	private BoundaryVertex endpoint;
	private int slopeX; 
	private int slopeY;

	//individual side properties here
	
	public Side( Line2D line , Boundary owner, int ID, CollisionEvent collisionEvent){
		this.line = line;
		this.owner = owner;
		this.ID = ID;
		calculateSlope(line);
		this.setCollisionEvent(collisionEvent);
	}
	//For cloning only
	protected Side( Line2D line , Point2D startpoint , Point2D endpoint , Boundary owner , int ID, CollisionEvent collisionEvent){
		this.line = line;
		this.owner = owner;
		this.ID = ID;
		calculateSlope(line);
		this.setCollisionEvent(collisionEvent);
	}
	
	private void calculateSlope(Line2D line){
		slopeX = (int)(line.getX2() - line.getX1());
		slopeY = (int)(line.getY2() - line.getY1());
	}
	
	//PROTECTED METHODS
	
	protected void setStartPoint( BoundaryVertex boundaryVertex ){ 
		this.startpoint = boundaryVertex; 
		line = new Line2D.Float( boundaryVertex.getX(), boundaryVertex.getY(), (int)line.getX2() , (int)line.getY2() );
		calculateSlope(line);
	} //assign this side to vertex 
	
	protected void setEndPoint( BoundaryVertex boundaryVertex ){ 
		this.endpoint = boundaryVertex; 
		line = new Line2D.Float( (int)line.getX1() , (int)line.getY1() ,boundaryVertex.getX(), boundaryVertex.getY() );
		calculateSlope(line);
	}
	
	protected void setLine( Line2D line ){ 
		this.line = line; 
		calculateSlope(line);
	}
	
	protected void setLine( Point2D p1 , Point2D p2){ 
		this.line = new Line2D.Float( p1, p2 ); 
		calculateSlope(line);
	}
	
	protected void setLine( Point p1 , Point p2){ 
		this.line = new Line2D.Float( p1, p2 ); 
		calculateSlope(line);
	}
	
	//PUBLIC
	
	public Vector unitVector(){
		double unitX = this.slopeX / this.line.getP1().distance( this.line.getP2() );
		double unitY = this.slopeY / this.line.getP1().distance( this.line.getP2() );
		return new Vector( unitX, unitY );
	}
	
	@Override 
	protected void onCollision(){
		
	}
	
	@Override
	public void collisionTrigger(){
		
	}

	
	public int getSlopeX(){ return slopeX; }  
	public int getSlopeY(){ return slopeY; }
	public Vector getSlopeVector(){ return new Vector(slopeX , slopeY); }
	
	public BoundaryVertex getStartPoint(){ return startpoint; }
	public BoundaryVertex getEndPoint(){ return endpoint; }
	
	public Line2D toLine(){ return line; }
	public Vector toVector(){ return new Vector(slopeX,slopeY); }
	
	@Override
	public Point2D getP1(){ return line.getP1(); }
	@Override
	public Point2D getP2(){ return line.getP2(); }
	
	public int getX1(){ return (int)line.getX1(); }
	public int getX2(){ return (int)line.getX2(); }
	public int getY1(){ return (int)line.getY1(); }
	public int getY2(){ return (int)line.getY2(); }
	public int getID(){ return ID; }
	
	@Override
	public String toString(){ return "Side"+this.ID ; }

	@Override
	public boolean debugIsVertex() {
		return false;
	}

	@Override
	public boolean debugIsSide() {
		return true;
	}
	
}
