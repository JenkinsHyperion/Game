package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

import entityComposites.AngularComposite.AngleComposite;

public class ParentRotateableComposite extends ParentChildRelationship implements RotateableComposite{
	
	private EntityStatic ownerEntity;
	private ArrayList<ChildComposite> children = new ArrayList<ChildComposite>();
	
	public ParentRotateableComposite( EntityStatic owner ){
		this.ownerEntity = owner;
	}
	
	protected void addChild( EntityStatic child ){

		if ( ownerEntity.getAngularComposite().exists() ){
			AngleComposite angularChild = (AngleComposite) ownerEntity.getAngularComposite();
		
			ChildComposite childComposite = new ChildComposite(child , children.size() , ownerEntity.getPosition() , angularChild.getAngle() );
			child.addFamilyRole( childComposite );
			children.add( childComposite );
		}else{
			System.err.println("TODO: Parent Rotateable Composite must be rotateable. Make non rotateable parent relationship ");
		}
		
	}

	@Override
	public void setAngle(double angleDegrees) { //METHOD IN CHARGE OF ROTATING ALL CHILDREN
		
		double angleRadians = Math.toRadians(angleDegrees);
		
		for ( ChildComposite child : children ){
			
			double x = child.zeroAnglePosition.getX();
			double y = child.zeroAnglePosition.getY();
			
			child.ownerChild.setPos( 
							(int) (ownerEntity.getX() + x*Math.cos(angleRadians) - y*Math.sin(angleRadians)) ,
							(int) (ownerEntity.getY() + x*Math.sin(angleRadians) + y*Math.cos(angleRadians)) 
					);	
			
			
			
		}
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
}
