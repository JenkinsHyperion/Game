package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import physics.*;
import testEntities.PlantTwigSegment;
import utility.ListNodeTicket;

public class DynamicRotationComposite implements EntityComposite, UpdateableComposite{
	protected String compositeName;
	
	private int updateableIndex;
	
	private EntityStatic ownerEntity;
	
	//protected Boundary storedBounds; //So that rounding errors from rotation don't degrade the vertex locations
	protected double angularVelocity = 0;
	protected double angularAcc = 0;
	
	private static Null nullSingleton = new Null();
	
	protected static DynamicRotationComposite nullSingleton(){
		return nullSingleton;
	}
	
	public DynamicRotationComposite( EntityStatic owner ){
		this.ownerEntity = owner;
		compositeName = this.getClass().getSimpleName();
	}
	
	@Override
	public void updateComposite() {
    	
    	angularVelocity = angularVelocity + angularAcc;
		
	}
	
	@Override
	public void updateEntityWithComposite(EntityStatic entity) {

		AngularComposite angular = entity.getAngularComposite();
		angular.setAngleInDegrees( angular.getAngleInDegrees() + angularVelocity);
		
		angular.notifyAngleChange(angularVelocity); //FIXME Change to addAngle which automatically notifies rotateables in angular

	}
	
	public void setAngularVelocity( double angularVelocity ){
		this.angularVelocity = angularVelocity;
	}

	public void setAngularAcceleration( double angularAcc ){
		this.angularAcc = angularAcc;
	}

	public float getAngularVel(){ return (float)angularVelocity; }

	public float getAngularAcc(){ return (float)angularAcc; }
	
	@Override
	public void removeThisUpdateableComposite() {
		
		this.ownerEntity.removeUpdateableCompositeFromEntity(updateableIndex);
		this.updateableIndex = -1;
	}
	
	@Override
	public boolean addUpdateableCompositeTo(EntityStatic owner) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public void disableComposite() {
		removeThisUpdateableComposite();
		this.ownerEntity.nullifyRotationComposite();
		
		this.angularVelocity = 0;
		this.angularAcc = 0;
		
	}

	@Override
	public void setCompositeName(String newName) {
		this.compositeName = newName;
	}
	@Override
	public String getCompositeName() {
		return this.compositeName;		
	}
	public EntityStatic getOwnerEntity() {
		return this.ownerEntity;
	}
	@Override
	public String toString() {
		return this.compositeName;
	}

	
	
	public static class SineWave extends DynamicRotationComposite{

		int[] phaseCounter; //Array of [1] is java workaround to ensure this.phaseCounter receives a REFERENCE not a value
		//Integer phaseCounterInt; //Array of [1] is java workaround to ensure this.phaseCounter receives a REFERENCE not a value
		double bend = 0;
		
		/*public SineWave(EntityStatic owner , int[] phaseCounter) { 
			super(owner);
			this.phaseCounter = phaseCounter;
		}
		*/
		public SineWave( EntityStatic owner, int[] phaseCounterIntRef ) { 
			super(owner);
			this.phaseCounter = phaseCounterIntRef;
			this.compositeName = "DynamicRotation"+this.getClass().getSimpleName();
		}
		
		@Override
		public void updateComposite() {
	    	
			//double output = phaseCounter[0] / 10.0;
			double output = phaseCounter[0] / 10.0;
			
			//double output = internalCounter / 10.0;
			
			this.bend = ( (2*output)*(2*output)*(2*output)/100 - (8*output) ) / 3000 ; //Polynomial approximation of Sine function
			/*if ( internalCounter <= 100){
				internalCounter++;
			}else{
				internalCounter=-100;
			}*/
			
		}
		
		@Override
		public void updateEntityWithComposite(EntityStatic entity) {
			
			AngularComposite angular = entity.getAngularComposite();
			
			angular.setAngleInDegrees( angular.getAngleInDegrees() + bend);
			angular.notifyAngleChange(bend); //FIXME Change to addAngle which automatically notifies rotateables in angular
		}
	}
	
	
	private static class Null extends DynamicRotationComposite {

		public Null() {
			super(null);
			this.compositeName += this.getClass().getSimpleName();
		}
		@Override
		public void updateComposite() {}
		@Override
		public void updateEntityWithComposite(EntityStatic entity) {}
		public void setAngularVelocity( double angularVelocity ){
			
		}
		public void setAngularAcceleration( double angularAcc ){
			
		}
		public float getAngularVel(){ return (float)angularVelocity; }
		public float getAngularAcc(){ return (float)angularAcc; }
		public void removeThisUpdateableComposite() {
		}

		public boolean addUpdateableCompositeTo(EntityStatic owner) {
			return false;
		}
		@Override
		public boolean exists() {
			return false;
		}
		@Override
		public void disableComposite() {
			System.err.println("Warning: Attempted to disable null Rotation Composite");
		}
		@Override
		public String toString() {
			return this.compositeName;
		}
	}


	@Override
	public void setUpdateablesIndex(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decrementIndex() {
		this.updateableIndex--;
	}

}
