package testEntities;

import entities.*;
import entityComposites.EntityStatic;
import misc.*;

public class Crosshair extends EntityDynamic{
	
	private EntityStatic target;
	private EntityStatic parent;

	private MovementBehavior idleBehavior; //TO DO make static singleton
	private MovementBehavior activeBehavior; //AI can also be a static singleton since its a behavior
	private MovementBehavior behaviorCurrent;
	
    public Crosshair(int x, int y, EntityStatic parent, EntityStatic target) {
		super(x, y);
		
		this.target = target;
		this.parent = parent;
		
		idleBehavior = new InactiveBehavior(); //TO DO make static singleton
		activeBehavior = new LinearFollow( this , target ); //AI can also be a static singleton since its a behavior
		behaviorCurrent = activeBehavior;
		
        initBulletTest();
        setBoundingBox(-1,-1,2,2);
		name = "Crosshair"+count;
	}
    
    private void initBulletTest(){
    	
        //loadAnimatedSprite(flying);
        //getObjectGraphic().getAnimatedSprite().start();

    	loadSprite("particle_test.png");
        
    }
    
    public void activate(){ behaviorCurrent = activeBehavior; }
    
    public void deactivate(){ 
    	behaviorCurrent = idleBehavior; 
    	this.setPos(parent.getPos());
    	this.setDX(0);
    	this.setDY(0);
    }
    
    @Override 
    public void updatePosition() {
    	

    	super.updatePosition();
    	behaviorCurrent.updateAIPosition(); 	

    	
    }
    

    
}