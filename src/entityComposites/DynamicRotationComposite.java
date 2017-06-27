package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import engine.BoardAbstract;
import physics.*;
import testEntities.PlantTwigSegment;
import utility.ListNodeTicket;

public class DynamicRotationComposite implements EntityComposite, UpdateableComposite{

	private ListNodeTicket updaterSlot;
	
	protected ArrayList<DynamicRotateableComposite> rotateableChildren = new ArrayList<DynamicRotateableComposite>();
	
	private EntityStatic ownerEntity;
	
	//protected Boundary storedBounds; //So that rounding errors from rotation don't degrade the vertex locations
	protected double angularVelocity = 0;
	protected double angularAcc = 0;
	
	public DynamicRotationComposite( EntityStatic owner ){
		this.ownerEntity = owner;
	}
	
	@Override
	public void updateComposite() {
    	
    	angularVelocity = angularVelocity + angularAcc;
		
	}
	
	@Override
	public void updateEntity(EntityStatic entity) {

		AngularComposite angular = entity.getAngularComposite();
		angular.setAngleInDegrees( angular.getAngle() + angularVelocity);
		
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

	public void removeUpdateable() {
		this.updaterSlot.removeSelf();
	}

	public boolean addCompositeToUpdater(BoardAbstract board) {
		if ( this.updaterSlot == null ){
    		this.updaterSlot = board.addCompositeToUpdater(this);
    		return true;
    	}
    	else{
    		return false;
    	}
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public void disable() {
		removeUpdateable();
		
		this.angularVelocity = 0;
		this.angularAcc = 0;
		
	}
	
	
	public static class SineWave extends DynamicRotationComposite{

		int[] phaseCounter; //Array of [1] is java workaround to ensure this.phaseCounter receives a REFERENCE not a value
		double bend = 0;
		int internalCounter = 0;
		
		public SineWave(EntityStatic owner , int[] phaseCounter) { 
			super(owner);
			this.phaseCounter = phaseCounter;
		}
		
		@Override
		public void updateComposite() {
	    	
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
		public void updateEntity(EntityStatic entity) {
			
			AngularComposite angular = entity.getAngularComposite();
			
			angular.setAngleInDegrees( angular.getAngle() + bend);
			angular.notifyAngleChange(bend); //FIXME Change to addAngle which automatically notifies rotateables in angular

		}
		
	}
	
}
