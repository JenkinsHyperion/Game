package testEntities;

import animation.Animation;
import animation.LoadAnimation;
import engine.Board.BoardAccess;
import entities.EntityDynamic;

public class Particle extends EntityDynamic{
	
	private short lifespan = 240; //    lifespan/60 = seconds
	
    public Particle(int x, int y, float dx, float dy) {
		super(x, y);
		
        initBulletTest();
        setDY(dy);
        setDX(dx);
        setAccY(0.1f);
        setBoundingBox(0,0,2,2);
	}
    
    private void initBulletTest(){
    	
        //loadAnimatedSprite(flying);
        //getObjectGraphic().getAnimatedSprite().start();

    	loadSprite("particle_test");
        
    }
    
    @Override // Kill particle when off screen. Once particles have collision (i.e. never leave screen), make aliveCounter
    			//variable that decrements in updatePosition() below and declares alive false when counted down to 0.
    public void updatePosition() {
    	
    	//Destroy particle if it goes out of bounds
    	if (y > BoardAccess.getBoardHeight()-3){ 
    		selfDestruct();
    	}
    	
    	x += dx;
    	y += dy;
    	
    	dx += accX;
    	dy += accY;
    	
    	if (lifespan > 0){
    		lifespan-- ;
    	}
    	else{
    		selfDestruct();
    	}
    	
    }
}