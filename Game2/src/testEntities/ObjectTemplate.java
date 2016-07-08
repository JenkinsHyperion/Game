package testEntities;

import animation.*;
import entities.*;

//When complete, go to Board, initBoard() and add your object to either staticObjects or dynamicObjects array.

public class ObjectTemplate extends EntityStatic{ // Can extend either EntityStatic or EntityDynamic
	
	//if sprite for this object is animated, declare animation and loadAnimatedSPrite in initialization. 
	//See getAnimation() documentation.
    //private Animation defaultAnimation = new Animation(LoadAnimation.getAnimation(4, 0, 14, "bullet") , 4 ); 
	
    public ObjectTemplate(int x, int y) { // Can add more construction arguments for spawn 
		super(x, y);
		
        initialize();
        
        //if entity is Dynamic, set initial velocities and accelerations
        //setDX(dx);
        //setDY(dy);
        //setAccX(accX);
        //setAccY(accY);
	}
    
    private void initialize(){
    	
    	//if sprite for this entity is still image, loadSprite(name)
    	loadSprite("bullet");
    	
    	//if sprite for this entity is animated, loadAnimatedSprite(Animation) instead  
        //loadAnimatedSprite(defaultAnimation);	
        //getObjectGraphic().getAnimatedSprite().start();
    	
    	//currently all entities require a bounding box until I separate collidables into their own class
		setBoundingBox(0,0,32,32); 
    	
    }
    


}
