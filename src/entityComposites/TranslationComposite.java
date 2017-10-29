package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import physics.Force;
import physics.PointForce;
import physics.Vector;
import utility.ListNodeTicket;

public class TranslationComposite implements EntityComposite, UpdateableComposite{
	
	protected String compositeName;
	private TranslationMath coreMath = new TranslationMath();
	private EntityStatic ownerEntity;
	private int ownerEntityIndex = -1;
	
	private Vector netForces = new Vector(0,0);
	
	private static final Null nullSingleton = new Null();

	public static TranslationComposite nullTranslationComposite(){
		return nullSingleton;
	}
	
	public TranslationComposite( EntityStatic ownerEntity ){
		this.ownerEntity = ownerEntity;
		this.compositeName = this.getClass().getSimpleName();
	}
	
	public void flyweightTranslation( TranslationComposite parentTranslation ){
		this.coreMath = null;
		this.coreMath = parentTranslation.coreMath;
	}
	
	public void halt(){
		coreMath.halt();
	}
	
	public int debugNumberVelocities(){
		return coreMath.debugNumberVelocities();
	}

	public double getDX() {
		return coreMath.getDX();
	}

	public double getDY() {
		return coreMath.getDY();
	}

	public Vector getVelocityVector(){
		return new Vector( coreMath.getDX() , coreMath.getDY() );
	}

	public void setDX(double setdx) {
		coreMath.setDX(setdx);
	}

	public void setDY(double setdy) {
		coreMath.setDY(setdy);
	}

	public void resetVelocityVector(){
		coreMath.setVelocityVector(Vector.zeroVector);
	}
	
	public void setVelocityVector( Vector vector){
		coreMath.setVelocityVector(vector);
	}

	public void addVelocity( Vector vector){
		coreMath.addVelocity(vector);
	}
	
	public void subtractVelocity( Vector vector){
		coreMath.subtractVelocity(vector);
	}

	public double getDeltaX( EntityStatic owner ){
		return coreMath.getDeltaX(owner);
	}

	public double getDeltaY( EntityStatic owner ){
		return coreMath.getDeltaY(owner);
	}

	public void clipDX(double clipDX) {
		coreMath.clipDX(clipDX);
	}

	public void clipDY(double clipDY) { 
		coreMath.clipDY(clipDY);
	}

	public void clampDX( int clamp ){
		coreMath.clampDX(clamp);
	}

	public void clampDY( int clamp ){
		coreMath.clampDY(clamp);
	}


	public void clipAccX(float clipAccX) {
		coreMath.clipAccX(clipAccX);
	}

	public void clipAccY(float clipAccY) { 
		coreMath.clipAccY(clipAccY);
	}


	public void setAccX(float setAX) {
		coreMath.setAccX(setAX);
	}

	public void setAccY(float setAY) {
		coreMath.setAccY(setAY);
	}

	public double getAccY() {
		return coreMath.getAccY();
	}

	public double getAccX() {
		return coreMath.getAccX();
	}

	public void setDampeningX(float decceleration) { 
		coreMath.setDampeningX(decceleration);
	}

	public void applyAccelerationX(float accX){
		coreMath.applyAccelerationX(accX);
	}

	public void applyAccelerationY(float accY){
		coreMath.applyAccelerationY(accY);
	}

	public VelocityVector registerVelocityVector( Vector vector ){
		return this.coreMath.addVelocityVector(vector);
	}

	public void removeVelocityVector( VelocityVector velocity ){
		this.coreMath.removeVelocityVector(velocity);
	}
	
	public Vector sumOfVelocityVectors(){
		return this.coreMath.sumOfVelocityVectors();
	}
	
	/** Creates new Force on this Collidable out of input Vector, and returns the Force that was added
	 * @param vector
	 * @return
	 */
	public Force addForce( Vector vector ){
		return coreMath.addForce(vector);
	}

	public void unregisterForce(Force force){ 
		coreMath.unregisterForce(force);
	}
	
	
	public Force registerGravityForce(){
		return coreMath.registerGravityForce();
	}
	public void unregisterGravityForce(Force force){ 
		coreMath.unregisterGravityForce(force);
	}
	public Vector getNetGravityVector(){
		return coreMath.sumOfGravityVectors();
	}
	
	
	public Force registerNormalForce( Vector vector ){
		return coreMath.addNormalForce(vector);
	}

	public void removeNormalForce(Force index){ 
		this.coreMath.removeNormalForce(index);
	}

	//MOVE TO ROTATIONAL BODY
	public PointForce addPointForce( Vector vector , Point point ){
		return coreMath.addPointForce(vector, point);
	}

	public void removePointForce(int index){ 
		coreMath.removePointForce(index);
	}
	
	public Vector getNetForces(){
		return this.netForces;
	}

	public Vector[] debugForceArrows(){
		return coreMath.debugForceArrows();
	}

	/** Attempts to add this Composite to Board updater thread.
	 * 
	 * @return True if this composite was added to updater thread. False if this composite is already in updater thread
	 */

	public boolean addUpdateableCompositeTo( EntityStatic owner){ 
		if ( ownerEntityIndex == -1 ){
			ownerEntityIndex = owner.addUpdateableCompositeToEntity(this);
			return true;
		}else{
			return false;
		}
	}
	@Override
	public void removeThisUpdateableComposite(){

		System.out.println("Removing "+TranslationComposite.this+" from ["+ownerEntity+"] updateables");
		
		ownerEntity.removeUpdateableCompositeFromEntity(ownerEntityIndex);
		ownerEntityIndex = -1;
	}
	@Override
	public boolean exists(){
		return true;
	}

	@Override
	public void disableComposite() { //DISABLING OF CONCRETE TRANSLATION COMPOSITE Consider condensing into one utility method

		this.removeThisUpdateableComposite(); //Remove math calculations from updater thread
		
		if ( this.ownerEntityIndex != -1 )
			this.ownerEntity.removeUpdateableCompositeFromEntity( this.ownerEntityIndex ); //Remove this composite's calculations from owner
		
		this.ownerEntity.getColliderComposite().changeColliderToStaticInEngine(); 
		/*		This translation being removed from ownerEntity means that ownerEntity will now be static. If ownerEntity
		 *  has a collider, tell it to notify the Collision Engine of this change. Collision Engine will then remove any
		 *	collision pairs with other statics, since static owner Entity will never collide with other statics.
		 */ 
		
		this.ownerEntity.nullifyTranslationComposite(); //Call owner entity to rereference its null translation singleton
		
		
	}
	@Override
	public void decrementIndex(){
		this.ownerEntityIndex--;
	}
	
	@Override
	public void setUpdateablesIndex(int index) {
		this.ownerEntityIndex = index;
	}


	@Override
	public EntityStatic getOwnerEntity(){
		return this.ownerEntity;
	}
	@Override
	public void updateEntityWithComposite(EntityStatic entity) {
		coreMath.updateEntityWithMath(entity);
	}

	@Override
	public void updateComposite() {
		coreMath.update();
	}
	@Override
	public String toString() {
		return this.compositeName;
	}
	
	
	private class TranslationMath{

			protected double dx=0;
			protected double dy=0;
			protected double accY=0;
			protected double accX=0;
			protected double dxT=0;
			protected double dyT=0;
			protected double accYT=0;
			protected double accXT=0;
		
			protected ArrayList<VelocityVector> velocityVectors = new ArrayList<>();
			protected ArrayList<Force> forces = new ArrayList<>();
			protected ArrayList<PointForce> pointForces = new ArrayList<>();
			protected ArrayList<Force> normalForces = new ArrayList<>();
			protected ArrayList<Force> gravityForces = new ArrayList<>();
		
			public void update() {

				dx += accX; 
				dy += accY;
		
				Vector netForces = this.sumOfForces();
				accX = netForces.getX();
				accY = netForces.getY();
		
		
			}  
			public int debugNumberVelocities() {
				return velocityVectors.size();
			}

			public void updateEntityWithMath( EntityStatic entity ) {
				
				Vector sumVelocities = this.sumOfVelocityVectors();
				
				entity.setX( entity.x + this.dx + sumVelocities.getX() ) ; 
				entity.setY( entity.y + this.dy + sumVelocities.getY() ) ;
				
			}  
			
			public void halt(){
				dx=0;
				dy=0;
				accX=0;
				accY=0;
			}
		
			public double getDX() {
				return dx;
			}
		
			public double getDY() {
				return dy;
			}
		
			public Vector getVelocityVector(){
				return new Vector( dx , dy );
			}
		
			public void setDX(double setdx) {
				dx = setdx;
			}
		
			public void setDY(double setdy) {
				dy = setdy;
			}
		
			public void setVelocityVector( Vector vector){
				dx = vector.getX();
				dy = vector.getY();
			}
		
			public void addVelocity( Vector vector){
				dx += vector.getX();
				dy += vector.getY();
			}
			
			public void subtractVelocity( Vector vector ){
				dx -= vector.getX();
				dy -= vector.getY();
			}
		
			public double getDeltaX( EntityStatic owner ){
				return (owner.x + dx + accX);
			}
		
			public double getDeltaY( EntityStatic owner ){
				return (owner.y + dy + accY);
			}
		
			public void clipDX(double clipDX) {
		
				double dxTemp = dx;
		
				if ( dx > 0 ){
					if ( clipDX > 0){
		
					}
					else if ( dx + clipDX < 0 ){
						dx = 0;
					}else{
						dx = dx + clipDX;
					}
				}
				else if ( dx < 0 ){
					if ( clipDX < 0){
		
					}
					else if ( dx + clipDX > 0 ){ 
						dx = 0;
					}else{
						dx = dx + clipDX;
					}
				}else{
					dx = 0;
				}
				System.out.println(dxTemp+" ->> "+clipDX+" ->> "+dx);
		
			}
		
			public void clipDY(double clipDY) { 
				double dyTemp = dy;
				if ( dy > 0 ){
					if ( clipDY > 0){
		
					}
					else if ( dy + clipDY < 0 ){
						dy = 0;
					}else{
						dy = dy + clipDY;
					}
				}
				else if ( dy < 0 ){
					if ( clipDY < 0){
		
					}
					else if ( dy + clipDY > 0 ){ 
						dy = 0;
					}else{
						dy = dy + clipDY;
					}
				}else{
					dy = 0;
				}
				System.out.println(dyTemp+" ->> "+clipDY+" ->> "+dy);
			}
		
			public void clampDX( int clamp ){
				if ( dx > 0 ){
					if (clamp < 0){
						dx = 0;
					}
				}
				else if (dx < 0){
					if (clamp > 0){
						dx=0;
					}
				}
			}
		
			public void clampDY( int clamp ){
				if ( dy > 0 ){
					if (clamp < 0){
						dy = 0;
					}
				}
				else if (dy < 0){
					if (clamp > 0){
						dy=0;
					}
				}
			}
		
		
			public void clipAccX(float clipAccX) {
				if ( accX > 0 ) {
		
					if ( clipAccX < 0 ){ 
						if ( clipAccX + accX > 0)
							accX = (accX + clipAccX);
						else
							accX = 0;
					}
				}
				else if ( accX < 0 ) {
		
					if ( clipAccX > 0 ){ 
						if ( clipAccX + accX < 0)
							accX = accX + clipAccX;
						else
							accX = 0;
					}
				}
			}
		
			public void clipAccY(float clipAccY) { 
				if ( accY > 0 ) {
		
					if ( clipAccY < 0 ){ 
						if ( clipAccY + accY > 0)
							accY = accY + clipAccY;
						else
							accY = 0;
					}
				}
				else if ( accY < 0 ) {
		
					if ( clipAccY > 0 ){ 
						if ( clipAccY + accY < 0)
							accY = (accY + clipAccY);
						else
							accY = 0;
					}
				}
			}
		
		
			public void setAccX(float setAX) {
				accXT = setAX;
			}
		
			public void setAccY(float setAY) {
				accYT = setAY;
			}
		
			public double getAccY() {
				return accY;
			}
		
			public double getAccX() {
				return accX;
			}
		
			public void setDampeningX(float decceleration) { 
				if (dx > (0.1))
				{
					applyAccelerationX( -decceleration );
				}
				else if (dx < (-0.1))
				{
					applyAccelerationX( decceleration );
				}
				else
				{
					accX=0;
					dx=0;
				}
			}
		
			public void applyAccelerationX(float accX){
				accX =+ accX;
			}
		
			public void applyAccelerationY(float accY){
				accY =+ accY;
			}
		
			
			
			public VelocityVector addVelocityVector( Vector vector ){
     	
				VelocityVector newVelocity = new VelocityVector( vector );
				newVelocity.addToList( velocityVectors );
				return newVelocity;
			}
		
			public void removeVelocityVector( VelocityVector velocity ){ 
				
				velocity.removeFromList( velocityVectors );
			}
			
			public Vector sumOfVelocityVectors(){
				
				Vector returnVector = new Vector(0,0);
				
				for ( VelocityVector velocity : velocityVectors ){
					returnVector = returnVector.add( velocity.getVector() );
				}
				return returnVector;
			}
			
			
			public Force registerGravityForce(){
				
				int indexID = gravityForces.size();     	
				Force newForce = new Force( new Vector(0,0) , indexID );
				gravityForces.add( newForce ) ;
				//System.out.print("Adding Force "+ indexID+" ... ");
				return newForce;
			}
		
			public void unregisterGravityForce(Force force){ 
				
				int index = force.getID();
				
				for ( int i = index+1 ; i < gravityForces.size() ; i++) {
					gravityForces.get(i).decrementIndex();
				} 
				gravityForces.remove(index); 
		
			}
			
			public Vector sumOfGravityVectors(){
				
				Vector returnSum = new Vector(0,0);
				for( Force gravity : gravityForces ){
					returnSum.setAdd(gravity.toVector());
				}
				return returnSum;
			}
			
			/** Creates new Force on this Collidable out of input Vector, and returns the Force that was added
			 * 
			 * @param vector
			 * @return
			 */
			public Force addForce( Vector vector ){
		
				int indexID = forces.size();     	
				Force newForce = new Force( vector , indexID );
				forces.add( newForce ) ;
				//System.out.print("Adding Force "+ indexID+" ... ");
				return newForce;
			}
		
			public void unregisterForce(Force force){ 
				
				int index = force.getID();
				
				for ( int i = index+1 ; i < forces.size() ; i++) {
					forces.get(i).decrementIndex();
				} 
				forces.remove(index); 
		
			}
			
			public Force addNormalForce( Vector vector ){
				
				int indexID = normalForces.size();     	
				Force newForce = new Force( vector , indexID );
				normalForces.add( newForce ) ;
				//System.out.print("Adding Force "+ indexID+" ... "+normalForces.size());
				return newForce;
			}
			
			public void removeNormalForce( Force force){ 
				//System.out.print("Removing Force "+ force.getID()+" ... "+normalForces.size());
				if ( force.getID() != -1 ){
				
					for ( int i = force.getID()+1 ; i < normalForces.size() ; i++) {
						normalForces.get(i).decrementIndex();
					} 
					normalForces.remove( force.getID() ); 
					force.resetID();
				}
				else{
					
				}
		
			}
		
			//MOVE TO ROTATIONAL BODY
			public PointForce addPointForce( Vector vector , Point point ){
		
				int indexID = pointForces.size();     	
				PointForce newForce = new PointForce( vector, point , indexID );
				pointForces.add( newForce ) ;
				return newForce;
			}
		
			public void removePointForce(int index){ 

				pointForces.remove(index); 
				for ( int i = index ; i < pointForces.size() ; i++) {
					pointForces.get(i).decrementIndex();
				} 
			}
		
		
			public Vector[] debugForceArrows(){
				final Vector[] returnVectors = new Vector[ forces.size() + gravityForces.size() + normalForces.size() ];
				
				for ( int i = 0 ; i < forces.size() ; ++i ){
					returnVectors[i] = forces.get(i).toVector() ;
				}
				
				for ( int j = forces.size() ; j < forces.size()+normalForces.size() ; ++j ){
					returnVectors[j] = normalForces.get(j - forces.size() ).toVector() ;
				}
				
				for ( int k = forces.size()+normalForces.size() ; k < returnVectors.length ; ++k ){
					returnVectors[k] = gravityForces.get(k -forces.size() -normalForces.size() ).toVector() ;
				}
				
				return returnVectors;
			}
			
			public Vector sumOfForces(){
		
				Vector returnVector = new Vector(0,0);
				for ( Force force : forces ){
					returnVector.setAdd( force.toVector() );
				}
				for ( Force gravity : gravityForces ){
					returnVector.setAdd( gravity.toVector() );
				}
				
				netForces = returnVector;
				for ( Force normal : normalForces ){

					returnVector = returnVector.add( normal.toVector() );
				}
		
				return returnVector;
			}
	
	} // END INNER MATH CLASS
	
	
	//NULL SINGLETON CLASS ###############################################################################
	private static class Null extends TranslationComposite{
		protected Null(){
			super(null);
			this.compositeName += "Null";
		}
	    
	    protected boolean isColliding;
	    
	    public void halt(){
	    	System.err.println("Attempted to halt static");
	    }
	
	    public double getDX() {
	    	return 0;
	    }
	    
	    public double getDY() {
	    	return 0;
	    }
	    
	    public void setDX(double setdx) {
	    	System.err.println("Attempted to set y velocity of static");
	    }
	    
	    public void setDY(double setdy) {
	    	System.err.println("Attempted to set x velocity of static");
	    }
	    
	    public void setVelocityVector( Vector vector){
	    	System.err.println("Attempted to set velocity of static");
	    }
	    
	    public void addVelocity( Vector vector){
	    	System.err.println("Attempted to add velocity of static");
	    }
	    
	    public double getDeltaX( EntityStatic owner ){
	    	return (owner.x);
	    }
	
	    public double getDeltaY( EntityStatic owner ){
	    	return (owner.y);
	    }
	    
	    public void clipDX(double clipDX) {
	    	System.err.println("Attempted to clip x velocity of static");
	    }
	    
	    public void clipDY(double clipDY) { 
	    	System.err.println("Attempted to clip y velocity of static");
	    }
	    
	    
	    public void clipAccX(float clipAccX) {
	    	System.err.println("Attempted to clip x acceleration of static");
	    }
	    
	    public void clipAccY(float clipAccY) { 
	    	System.err.println("Attempted to clip y acceleration of static");
	    }
	    
	    
	    public void setAccX(float setAX) {
	    	System.err.println("Attempted to set x acceleration of static");
	    }
	    
	    public void setAccY(float setAY) {
	    	System.err.println("Attempted to set y acceleration of static");
	    }
	    
	    public double getAccY() {
	    	return 0;
	    }
	    
	    public double getAccX() {
	    	return 0;
	    }
	    
	    public void setDampeningX(float decceleration) { 
	    	System.err.println("Attempted to apply x dampening to static");
	    }
	    
	    public void applyAccelerationX(float accX){
	    	System.err.println("Attempted to apply x acceleration to static");
	    }
	    
	    public void applyAccelerationY(float accY){
	    	System.err.println("Attempted to apply y acceleration to static");
	    }
	
	    
	    public boolean isColliding(){ return isColliding; }
	    public void setColliding( boolean state ){ isColliding = state;}
	    
	
	    
	    @Override
	    public VelocityVector registerVelocityVector(Vector vector) {
	    	System.err.println("Attempted to add velocity vector to static");
	    	return new VelocityVector( vector );
	    }
	    
	    @Override
	    public void removeVelocityVector(VelocityVector velocity) {
	    	System.err.println("Attempted to remove velocity vector to static");
	    }
	    
	    @Override
	    public Vector sumOfVelocityVectors() {
	    	return Vector.zeroVector;
	    }
	    
		/** Creates new Force on this Collidable out of input Vector, and returns the Force that was added
		 * 
		 * @param vector
		 * @return
		 */
	    public Force addForce( Vector vector ){
	    	System.err.println("Attempted to add force to static");
	    	return null;
	    }
	    
	    public void unregisterForce(int index){ 
	    	System.err.println("Attempted to remove force from static");
	    }
	    
	    //MOVE TO ROTATIONAL BODY
	    public PointForce addPointForce( Vector vector , Point point ){
	    	System.err.println("Attempted to add force to static");
	    	return null;
	    }
	    
	    public void removePointForce(int index){ 
	    	System.err.println("Attempted to remove force on static");
	    }
	    
	    
	    public Vector[] debugForceArrows(){
	    	System.err.println("Attempted to get forces of static");
	    	return new Vector[0];
	    }
	
		public Vector sumOfForces() {
			System.err.println("Attempted to sum forces to static");
			return new Vector(0,0);
		}
		
		public void removeThisUpdateableComposite(){
			System.err.println("Attempted to remove null Translation from updater");
		}
		
		@Override
		public boolean exists(){
			return false;
		}
	
		@Override
		public void disableComposite() {
			System.err.println("Attempted to disable null Translation");
		}

		@Override
		public String toString() {
			return this.compositeName;
		}
	
		public Vector getVelocityVector() {
			return new Vector(0,0);
		}
	}
	
	
	
	
	public class VelocityVector{
		
		private Vector vector;
		private int indexID;
		
		public VelocityVector(Vector vector) {
			this.vector = vector;
		}
		
		public Vector getVector(){
			return this.vector;
		}
		
		public void setVector(Vector vector){
			this.vector = vector;
		}

		private void decIndex(){
			this.indexID--;
		}
		
		protected void addToList( ArrayList<VelocityVector> list ){
			this.indexID = list.size();
			list.add(this);
		}
		
		protected void removeFromList( ArrayList<VelocityVector> list ){
			list.remove(indexID);
			for( int i = indexID ; i < list.size() ; i++ ){
				list.get(i).decIndex();
			}
		}
	}
	
}