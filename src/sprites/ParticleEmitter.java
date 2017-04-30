package sprites;

import java.util.Random;

import entityComposites.*;

public class ParticleEmitter extends EntityStatic implements UpdateableComposite{

	private EntityStatic[] particles;
	
	private int counter = 0;
	private int particleIndex = 0;
	private int particleCount;
	private int particleDelay;
	
	public ParticleEmitter(int x, int y) {
		super(x, y);
		this.name = "emitter";
		
		this.particleCount = 8;
		this.particleDelay = 2;
		
		initParticles();
	}
	
	private void initParticles(){
		particles = new EntityStatic[particleCount];
		
		particles[0] = new EntityStatic( this.name+"_particle0" , this.getX() , this.getY() );
		CompositeFactory.addTranslationTo(particles[0]);
		CompositeFactory.addRotationTo(particles[0]);
		CompositeFactory.addGraphicTo(particles[0], new SpriteStillframe("particle_test.png",-3,-3) ); //FLYWEIGHT SPRITES
		//particles[0].getTranslationComposite().setDX(2);
		particles[0].getRotationComposite().setAngularVelocity(2);
		
		updateables.add(particles[0]);
		
		for ( int i = 1 ; i < particles.length ; i++ ){

			particles[i] = new EntityStatic( this.name+"_particle"+i, this.getX() , this.getY());
			
			Random rand = new Random(); 
			int value = rand.nextInt(180); 
			double angleRadians = (value * ((Math.PI)/180) ) ;
			
			CompositeFactory.flyweightTranslation( particles[0] , particles[i] );
			
			CompositeFactory.addRotationTo(particles[i]);
			particles[i].getRotationComposite().setAngularVelocity(2);
			//CompositeFactory.flyweightRotation( particles[0] , particles[i] );
			particles[i].getRotationComposite().setAngleInRadians(angleRadians);
			
			CompositeFactory.addGraphicTo(particles[i], new SpriteStillframe("particle_test.png",-3,-3) ); 
			updateables.add(particles[i]);
		}
	}
	
	private void trigger(){
		
		particles[particleIndex].setPos( this.getPosition() );
		particleIndex++;
		if ( particleIndex >= particleCount ){
			particleIndex=0;
		}
	}
	
	@Override
	public void updateComposite() {
		super.updateComposite();
		if ( counter < particleDelay ){
			counter++;
		}else{
			trigger();
			counter = 0;
		}
	}
	
	@Override
	public void updateEntity(EntityStatic entity) {
	}

	
}
