package testEntities;

import entities.*;

//NOTE: somewhat outdated

//When complete, go to Board, initBoard() and add your object to either staticObjects or dynamicObjects array.

public class ObjectTemplate extends EntityStatic{ // Can extend either EntityStatic or EntityDynamic
	
	//if sprite for this object is animated, declare animation and loadAnimatedSPrite in initialization. 
	//See getAnimation() documentation.
    //private Animation defaultAnimation = new Animation(LoadAnimation.getAnimation(4, 0, 14, "bullet") , 4 ); 
	
	
    public ObjectTemplate(int x, int y) { // Can add more construction arguments for spawn 
		super(x, y);
		this.offsetX = 0;
		this.offsetY = 0;
    	loadSprite("box.png");
    	setBoundingBox(0,0,32,32); 
        //if entity is Dynamic, set initial velocities and accelerations
        //setDX(dx);
        //setDY(dy);
        //setAccX(accX);
        //setAccY(accY);
	}
    public ObjectTemplate(int x, int y, int offsetX, int offsetY) { // Can add more construction arguments for spawn 
		super(x, y);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
    	loadSprite("box.png");
    	setBoundingBox(0,0,32,32); 
        //if entity is Dynamic, set initial velocities and accelerations
        //setDX(dx);
        //setDY(dy);
        //setAccX(accX);
        //setAccY(accY);
	}
    private void initialize(){
    	
    	//if sprite for this entity is still image, loadSprite(name)

    	
    	//if sprite for this entity is animated, loadAnimatedSprite(Animation) instead  
        //loadAnimatedSprite(defaultAnimation);	
        //getObjectGraphic().getAnimatedSprite().start();
    	
    	//currently all entities require a bounding box until I separate collidables into their own class
		
    	
    }
    


}
