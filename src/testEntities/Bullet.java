package testEntities;

import animation.Animation;
import animation.LoadAnimation;
import entities.*;

public class Bullet extends EntityDynamic{
	
    private Animation flying = new Animation(LoadAnimation.buildAnimation(4, 0, 14, "bullet.png") , 4 ); 
	
    public Bullet(int x, int y, int dx, int dy) {
		super(x, y);
        initBulletTest();
        setDX(dx);
        setDY(dy);
        //setBoundingBox(0,0,12,12);
	}
    
    private void initBulletTest(){
    	
        loadAnimatedSprite(flying);
        getEntitySprite().getAnimatedSprite().start();
    	
    }
}
