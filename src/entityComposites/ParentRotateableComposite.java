package entityComposites;

import java.awt.Point;
import java.util.ArrayList;

public class ParentRotateableComposite extends ParentChildRelationship implements RotateableComposite{
	
	private EntityStatic ownerEntity;
	private ArrayList<ChildComposite> children = new ArrayList<ChildComposite>();
	
	public ParentRotateableComposite( EntityStatic owner ){
		this.ownerEntity = owner;
	}
	
	protected void addChild( EntityStatic child ){
		ChildComposite childComposite = new ChildComposite(child , children.size() , ownerEntity.getPosition() );
		child.addFamilyRole( childComposite );
		children.add( childComposite );
	}

	@Override
	public void setAngle(double angleRadians) {
		
		for ( ChildComposite child : children ){
			
			double x = child.zeroAnglePosition.getX();
			double y = child.zeroAnglePosition.getY();
			
			child.ownerChild.setPos( 
							ownerEntity.getX() + (int)( x*Math.cos(angleRadians) - y*Math.sin(angleRadians) ),
							ownerEntity.getY() + (int)( y*Math.cos(angleRadians) + x*Math.sin(angleRadians) )
					);			
		}
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
}
