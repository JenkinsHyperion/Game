package sprites;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Random;

import engine.ReferenceFrame;
import entityComposites.*;
import sprites.Sprite.Stillframe;
import utility.Probability;

public class ParticleEmitter extends EntityStatic{

	private int particleCount;
	private int delay;
	private ParticleNonEntity[] particlesList;
	
	public ParticleEmitter(int x, int y) {
		super(x, y);
		this.name = "emitter";
		
		this.particleCount = 4;
		
		initParticles();
	}
	
	private void initParticles(){


		//updateablesList.add(particles[0]);

			delay = 100;
		
			particlesList = new ParticleNonEntity[particleCount];	
			
			final Sprite.Stillframe glow03 = new Sprite.Stillframe("ParticleFX/glowTest_03.png",Sprite.CENTERED);
			final Sprite.Stillframe lightray1 = new Sprite.Stillframe("ParticleFX/rayTest_01.png",Sprite.CENTERED_TOP);
		
			for( int i = 0 ; i < particlesList.length ; ++i ){
				
				particlesList[i] = new ParticleNonEntity( glow03, 0.05 , 0.8f , false);
			}
			
			/*final Sprite.Stillframe glow03 = new Sprite.Stillframe("particle_test.png",Sprite.CENTERED);
			
			for( int i = 0 ; i < particlesList.length ; ++i ){
				
				particlesList[i] = new ParticleBug( glow03, 0.05 , 0.8f , false);
			}*/
			
			/*
			 * 
			 */
			CompositeFactory.addAnonymousGraphicTo(this, new GraphicComposite.Rotateable(this){
				
				@Override
				public void draw(ReferenceFrame camera) {
					
					for ( ParticleNonEntity particle : particlesList ){
						AffineTransform entityTransform = new AffineTransform();
						
						entityTransform.rotate(Math.toRadians(particle.getAngle()));
						entityTransform.scale( particle.getSize(),particle.getSize()  );
						entityTransform.translate(particle.getSprite().getOffsetX(),particle.getSprite().getOffsetY());
						
						camera.draw( particle.getSprite().getImage() , particle.getPosition(), entityTransform, particle.getAlpha() );
						
						/*camera.draw( 
								particleSprite.getImage() , 
								ParticleEmitter.this.getAbsolutePositionOf(particle.getPosition()), 
								entityTransform, 
								particle.getAlpha() 
								);*/
					}
				}
			});
			
			CompositeFactory.addScriptTo(this, new FadeInAndOut() );
		
	}
	
	public void setState( boolean onOff ){ //TODO pause emitter script from clocking while off
		if (onOff){
			this.getGraphicComposite().activateGraphic();
		}else{
			this.getGraphicComposite().deactivateGraphic();
		}
	}
	
	private class FadeInAndOut extends EntityBehaviorScript{
		
		byte counter = 0;		//general counter int
		
		byte indexFadeOut = 0;	// index of particle fading out, this value will technically increment through all values 
								// of byte but after modulus will represent range from 0 to number of particles. This is just
								// an operational alternative to checking if index < particleCount each time,
								// at the expense of a probably unnoticable inaccuracy when value wraps around 128 to -127 
		
		/*unsigned*/byte duriation = 3;	// number of cycles each particle lasts
										// Should not be any multiples of particleCount, else fade in and fade out 
										// will clash
		
		@Override
		protected void updateOwnerEntity(EntityStatic ownerEntity) {

			for ( ParticleNonEntity particle : particlesList ){	//update position of all particles
				particle.updatePosition();
			}

			if ( counter < delay ){		//counter loops from 0 to delay
				
				counter++;
				
				particlesList[(indexFadeOut+128)%particleCount].setAlpha(((float)delay-counter)/delay);
					//decrememnt alpha on particle fading out
				particlesList[(indexFadeOut+duriation+128)%particleCount].setAlpha((counter)/(float)delay);
					//increment alpha on particle fading in
			}else{
				particlesList[(indexFadeOut+128)%particleCount].reset();
				++indexFadeOut;
				counter = 0;
			}
		}
	}
	
	private class RotatingParticles extends EntityBehaviorScript{
		
		short angleCounter = 0;	//   +/-  32,767
		
		byte indexFadeOut = 0;
		
		byte counter = 0;
		
		byte duriation = 3;
		
		@Override
		protected void updateOwnerEntity(EntityStatic entity) {
			
			angleCounter += 20;
			
			for ( int i = 0 ; i < particlesList.length ; ++i){
				short a = angleCounter;
				a = (short)(a + (i*17000));
				particlesList[i].angle =  Math.abs(a/300)/4f;
			}
			
			if ( counter < delay ){		//counter loops from 0 to delay
				
				counter++;
				
				particlesList[(indexFadeOut+128)%particleCount].setAlpha(((float)delay-counter)/delay);
					//decrememnt alpha on particle fading out
				particlesList[(indexFadeOut+duriation+128)%particleCount].setAlpha((counter)/(float)delay);
					//increment alpha on particle fading in
			}else{
				particlesList[(indexFadeOut+128)%particleCount].reset();
				++indexFadeOut;
				counter = 0;
			}
			
		}
	}
	
	private class ParticleTrail extends EntityBehaviorScript{

		byte angleCounter = 0;
		byte angle = 0;
		byte counter = 0;
		byte indexFadeOut = 0;
		byte speed = 3;
		
		double x = 0;
		double y = 0;
		
		@Override
		protected void updateOwnerEntity(EntityStatic entity) {
			
			x = (60.0*Math.cos(Math.toRadians(angle*1.406)));
			y = (30.0*Math.sin(Math.toRadians((angle*1.406))*2));
			
			++angle;
			
			if ( counter < speed ){
				++counter;
				particlesList[(indexFadeOut+128)%particleCount].setAlpha((float)counter/speed);
				
			}
			else{
				ParticleNonEntity resetParticle = particlesList[(indexFadeOut+128)%particleCount];
				resetParticle.resetSize();
				resetParticle.setPosition(x, y);
				++indexFadeOut;
				counter = 0 ;
			}
			
		}

	}

	private class ParticleNonEntity{
		
		final Sprite.Stillframe sprite;
		
		double x;
		double y;
		double dx;
		double dy;
		float angle;
		float angularVelocity;
		
		double size=1.0;
		float alpha = 1.0f;
		
		protected ParticleNonEntity( Sprite.Stillframe sprite , double velocity , float anglularVelocity, boolean particlesAreAttatched ){
			
			int velocityAngle = Probability.randomInt(0, 360);
			
			this.dx = Math.cos(velocityAngle)*velocity;
			this.dy = Math.sin(velocityAngle)*velocity;

			this.x =  ParticleEmitter.this.getX() ;
			this.y =  ParticleEmitter.this.getY() ;
			
			this.angularVelocity = anglularVelocity; 
			this.angle = velocityAngle;
			
			this.sprite = sprite;
		}
		
		protected void reset(){
			size = 1.0;
			x = ParticleEmitter.this.getX() ;
			y = ParticleEmitter.this.getY() ;
		}
		
		protected void resetSize(){
			size = 1.0;
		}
		
		protected double getSize(){
			return this.size;
		}
		
		protected Sprite.Stillframe getSprite(){
			return this.sprite;
		}
		
		protected float getAlpha(){
			return this.alpha;
		}
		
		protected void setAlpha(float alpha){
			this.alpha = alpha;
		}
		
		protected void setSize( double size ){
			this.size = size;
		}
		
		protected float getAngle(){
			return this.angle;
		}
		
		protected void setPosition( double x, double y){
			this.x = x;
			this.y = y;
		}
		
		protected Point getPosition(){
			return new Point((int)x, (int)y);
		}
		
		protected void updatePosition(){
			x += dx;
			y += dy;
			
			this.angle += angularVelocity;
		}
		
	}
	
	
	private class ParticleBug extends ParticleNonEntity{

		protected ParticleBug(Stillframe sprite, double velocity, float anglularVelocity,
				boolean particlesAreAttatched) {
			super(sprite, velocity, anglularVelocity, particlesAreAttatched);
		}
		
		@Override
		protected void updatePosition() {
			
		}

	}
	
}
