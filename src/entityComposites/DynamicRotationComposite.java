package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import engine.BoardAbstract;
import physics.*;
import utility.ListNodeTicket;

public class DynamicRotationComposite implements EntityComposite, UpdateableComposite{

	private ListNodeTicket updaterSlot;
	
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
		angular.setAngle( angular.getAngle() + angularVelocity);
		/*for ( RotateableComposite rotateable : rotateableCompositeList ){

			rotateable.setAngle(angleRadians);
		}*/
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
}
