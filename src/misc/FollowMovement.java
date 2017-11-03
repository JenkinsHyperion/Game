package misc;

import java.awt.Point;
import java.awt.geom.Point2D;

import entities.EntityDynamic;
import entityComposites.EntityStatic;
import entityComposites.TranslationComposite;
import physics.Vector;

public abstract class FollowMovement extends MovementBehavior{
	
	protected Point target;
	
	public static class Linear extends FollowMovement{
		
		public Linear(EntityStatic owner, EntityStatic target){
			this.owner = owner;
			this.target = target.getPositionReference();
		}
		public Linear(EntityStatic owner, Point target){
			this.owner = owner;
			this.target = target;
		}
		
		@Override
		public void updateAIPosition() {

			TranslationComposite trans = this.owner.getTranslationComposite();
			
			trans.setDX( ( this.target.getX() - this.owner.getX() ) /30.0 );
			trans.setDY( ( this.target.getY() - this.owner.getY() ) /30.0 );

			Vector velocity = trans.getVelocityVector();
			
			if ( velocity.getMagnitude() > 2){
				trans.setVelocityVector( velocity.unitVector().multiply(2) );
			}
			
			Vector separation = this.owner.getSeparationVector( new Point(0 , 600));
			
			if ( separation.getIntegerMagnitude() < 550 ){ //prevent follower from running into asteroid
				
				trans.subtractVelocity(trans.getVelocityVector().projectedOverClamped(separation));
			}

		}
	}
	
	
	public static class Directional extends FollowMovement{
		
		private int angleDegrees = 0;
		
		public Directional(EntityStatic owner, EntityStatic target){
			this.owner = owner;
			this.target = target.getPositionReference();
		}
		public Directional(EntityStatic owner, Point target){
			this.owner = owner;
			this.target = target;
		}
		
		@Override
		public void updateAIPosition() {

			Vector separation = this.owner.getSeparationUnitVector(target);
			
			double targetAngle = separation.angleFromVectorInDegrees();
			
			this.owner.getTranslationComposite().setVelocityVector(separation);

		}
	}
	
	
	public static class Rational extends FollowMovement{
		
		public Rational(EntityStatic owner, EntityStatic target){
			this.owner = owner;
			this.target = target.getPositionReference();
		}
		public Rational(EntityStatic owner, Point target){
			this.owner = owner;
			this.target = target;
		}
		
		@Override
		public void updateAIPosition() {

			this.owner.getTranslationComposite().setDX( 2 - ( 1/(( this.target.getX() - this.owner.getX() ) +0.5 )) );
			this.owner.getTranslationComposite().setDY( 2 - ( 1/(( this.target.getY() - this.owner.getY() ) +0.5 )) );

		}

	}

}
