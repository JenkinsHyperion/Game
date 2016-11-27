package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Side {

	private int ID;
	Line2D line;
	private Vertex startpoint;
	private Vertex endpoint;
	private double slope;

	//individual side properties here
	
	public Side( Line2D line , int ID){
		this.line = line;
		this.ID = ID;
	}
	
	public Side( Line2D line , Point2D startpoint , Point2D endpoint , int ID){
		this.line = line;
		this.ID = ID;
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
	public String toString(){ return "Side"+ID ; }
	
}
