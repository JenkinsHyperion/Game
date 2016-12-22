package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import entities.EntityStatic;

public class Side extends BoundaryFeature{

	//private int ID;
	Line2D line;
	private Vertex startpoint;
	private Vertex endpoint;
	private int slopeX;
	private int slopeY;

	//individual side properties here
	
	public Side( Line2D line , Boundary owner, int ID){
		this.line = line;
		this.owner = owner;
		this.ID = ID;
		calculateSlope(line);
	}
	
	public Side( Line2D line , Point2D startpoint , Point2D endpoint , Boundary owner , int ID){
		this.line = line;
		this.owner = owner;
		this.ID = ID;
		calculateSlope(line);
	}
	
	private void calculateSlope(Line2D line){
		slopeX = (int)(line.getX2() - line.getX1());
		slopeY = (int)(line.getY2() - line.getY1());
	}
	
	//PROTECTED METHODS
	
	protected void setStartPoint( Vertex vertex ){ 
		this.startpoint = vertex; 
		line = new Line2D.Float( vertex.getX(), vertex.getY(), (int)line.getX2() , (int)line.getY2() );
	} //assign this side to vertex 
	
	protected void setEndPoint( Vertex vertex ){ 
		this.endpoint = vertex; 
		line = new Line2D.Float( (int)line.getX1() , (int)line.getY1() ,vertex.getX(), vertex.getY() );
	}
	
	protected void setLine( Point P1 , Point P2){ this.line = new Line2D.Float( P1, P2 ); }
	
	//PUBLIC
	
	@Override 
	protected void onCollision(){
		
	}
	
	@Override
	public Boundary getOwnerBoundary(){
		return this.owner;
	}
	
	@Override
	public EntityStatic getOwnerEntity(){
		return this.owner.getOwnerCollidable().getOwnerEntity();
	}

	
	public int getSlopeX(){ return slopeX; }
	public int getSlopeY(){ return slopeY; }
	public Vector getSlopeVector(){ return new Vector(slopeX , slopeY); }
	
	public Vertex getStartPoint(){ return startpoint; }
	public Vertex getEndPoint(){ return endpoint; }
	
	public Line2D toLine(){ return line; }
	
	public Point2D getP1(){ return line.getP1(); }
	public Point2D getP2(){ return line.getP2(); }
	
	public int getX1(){ return (int)line.getX1(); }
	public int getX2(){ return (int)line.getX2(); }
	public int getY1(){ return (int)line.getY1(); }
	public int getY2(){ return (int)line.getY2(); }
	public int getID(){ return ID; }
	
	@Override
	public String toString(){ return "Side"+this.ID ; }
	
}
