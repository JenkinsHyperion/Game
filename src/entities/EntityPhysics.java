package entities;

public class EntityPhysics extends EntityDynamic{

    public EntityPhysics(int x, int y, String path) {
    	super(x,y);

		initPhysics(x,y,path);
		setBoundingBox(0,0,24,24);
		name = "Box"+count;
    	
    }
    
    private void initPhysics(int x, int y, String path){
    	
    	loadSprite(path);
    	
    	//accY = 0.1f;
    	
    	//dy = -3;
    	
    }
    
    @Override
    public void updatePosition(){ 	
    	
    	
    	
    	
    	x += dx;
    	y += dy;
    	
    	dx += accX;
    	dy += accY;
    	
    	//accY = -(y-210)/100  ; 
    	
    	dy = dy - (y-150)/50 ;
    	dx = dx - (x-320)/50 ;
    	
    	if (dy>0){
    		dy = dy - 0.02f;
    	} 
    	else if (dy<0) {
    		dy = dy + 0.02f;
    	}
    	
    	if (dx>0){
    		dx = dx - 0.02f;
    	} 
    	else if (dx<0) {
    		dx = dx + 0.02f;
    	}

    }
	
}
