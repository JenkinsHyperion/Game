package physics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Side {

	Line2D line;
	//individual side properties here
	
	public Side(){
		
	}
	
	public Side( Line2D line){
		this.line = line;
	}
	
	public Line2D toLine(){ return line; }
	
	public Point2D getP1(){ return line.getP1(); }
	public Point2D getP2(){ return line.getP2(); }
	
	public int getX1(){ return (int)line.getX1(); }
	public int getX2(){ return (int)line.getX2(); }
	public int getY1(){ return (int)line.getY1(); }
	public int getY2(){ return (int)line.getY2(); }
	
}
