package testEntities;

import entities.EntityDynamic;
import entities.EntityStatic;

public class Crosshair extends EntityDynamic{
	
	private EntityStatic target;
	private EntityStatic parent;
	
    public Crosshair(int x, int y, EntityStatic parent, EntityStatic target) {
		super(x, y);
		
		this.target = target;
		this.parent = parent;
		
        initBulletTest();
        setBoundingBox(-1,-1,2,2);
		name = "Crosshair"+count;
	}
    
    private void initBulletTest(){
    	
        //loadAnimatedSprite(flying);
        //getObjectGraphic().getAnimatedSprite().start();

    	loadSprite("particle_test");
        
    }
    
    @Override 
    public void updatePosition() {
    	
    	x += dx;
    	y += dy;
    	
    	dx += accX;
    	dy += accY;
    	
    	//MOVE TO AI / BEHAVIOR CLASS ratehr than boilerplating it
    	
    	y = y - (y-target.getY())/30 ;

    	
    	
    	x = x - (x-target.getX())/30 ;
    }
    

    
}