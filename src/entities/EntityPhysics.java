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
    	
    	accY = 0.1f;
    	
    }
    
    @Override
    public void updatePosition(){
    	
    	dx=0.075f;
    	
    	x += dx;
    	y += dy;
    	
    	dx += accX;
    	dy += accY;
    }
	
}
