package entities;

import entityComposites.*;
import physics.BoundaryPolygonal;

public class EntityPhysics extends EntityDynamic{
	
	int yReturn;

    public EntityPhysics(int x, int y, String path) {
    	super(x,y);

		initPhysics(x,y,path);
		//setBoundingBox(0,0,24,24);
		name = "Box"+count;
    	
    }
    
    private void initPhysics(int x, int y, String path){
    	
    	loadSprite(path);
    	
    	//Collider collidable = new Collider(this);
    	//collidable.setBoundary( new BoundaryPolygonal.Box(24, 24, 0, 0) );   	
    	//this.setCollisionComposite( collidable );
    	
    	CompositeFactory.addColliderTo( this, new BoundaryPolygonal.Box(24, 24, 0, 0) );
    	
    	yReturn = y + 12 ;
    	//accY = 0.1f;
    	
    	//dy = -3;

    }
    
    @Override
    public void setX(int setx) {
        x = setx;
    }

    @Override
    public void setY(int sety) {
        y = sety;
        yReturn = sety ;
    }
    
    @Override
    public void updatePosition(){ 	
    	
    	
    	
    	
    	x += dx;
    	y += dy;
    	
    	dx += accX;
    	dy += accY;
    	
    	//accY = -(y-210)/100  ; 
    	
    	
    	

    	dy = dy - (y-yReturn)/50 ;
    	//dx = dx - (x-120)/50 ;
    	
    	if (dy>0){
    		dy = dy - 0.02f;
    	} 
    	else if (dy<0) {
    		dy = dy + 0.02f;
    	}
    	
    	/*if (dx>0){
    		dx = dx - 0.02f;
    	} 
    	else if (dx<0) {
    		dx = dx + 0.02f;
    	}*/

    }
	
}
