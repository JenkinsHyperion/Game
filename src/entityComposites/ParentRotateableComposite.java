package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import entityComposites.AngularComposite.AngleComposite;

public class ParentRotateableComposite extends ParentComposite implements RotateableComposite {
	protected String compositeName = "ParentRotateableComposite";
	private EntityStatic ownerEntity;
	
	public ParentRotateableComposite( EntityStatic owner ){
		this.ownerEntity = owner;
	}
	
	protected void addChild( EntityStatic child ){

		if ( ownerEntity.getAngularComposite().exists() ){
			AngleComposite angularParent = (AngleComposite) ownerEntity.getAngularComposite();
		
			ChildComposite childComposite = new ChildComposite(child , children.size() , ownerEntity.getPosition() , angularParent.getAngle() );
			child.childComposite = childComposite;
			children.add( childComposite );
		}else{
			System.err.println("TODO: Parent Rotateable Composite must be rotateable. Make non rotateable parent relationship ");
		}
		
	}
	
	@Override
	public void setAngle(double angleRadians) {
		manipulateChildren();
	}
	
	@Override
	public void manipulateChildren() { //METHOD IN CHARGE OF ROTATING ALL CHILDREN
		
		double angleRadians = Math.toRadians( ownerEntity.getAngularComposite().getAngle() );
		for ( ChildComposite child : children ){
			
			double relativeX = child.zeroAnglePosition.getX();
			double relativeY = child.zeroAnglePosition.getY();
			
			double x = ( relativeX*Math.cos(angleRadians) - relativeY*Math.sin(angleRadians) );
			double y = ( relativeX*Math.sin(angleRadians) + relativeY*Math.cos(angleRadians) );

			child.ownerChild.setCompositedPos( ownerEntity.x + x , ownerEntity.y + y );
			
			
		}
		
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	@Override
	public void setCompositeName(String newName) {
		this.compositeName = newName;
	}
	@Override
	public String getCompositeName() {
		return this.compositeName;		
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
