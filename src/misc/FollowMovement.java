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
		private double speed;
		public Linear(EntityStatic owner, EntityStatic target){
			this.owner = owner;
			this.target = target.getPositionReference();
			speed = 2;
		}
		public Linear(EntityStatic owner, Point target){
			this.owner = owner;
			this.target = target;
			speed = 2;
		}
		public Linear(EntityStatic owner, Point target, double speed) {
			this.owner = owner;
			this.target = target;
			this.speed = speed;
		}
		public Linear(EntityStatic owner, EntityStatic target, double speed) {
			this.owner = owner;
			this.target = target.getPositionReference();
			this.speed = speed;
		}
		@Override
		public void updateAIPosition() {

			TranslationComposite trans = this.owner.getTranslationComposite();
			
			trans.setDX( ( this.target.getX() - this.owner.getX() ) /30 );
			trans.setDY( ( this.target.getY() - this.owner.getY() ) /30 );

			Vector velocity = trans.getVelocityVector();
			
			if ( velocity.getMagnitude() > 2){
				trans.setVelocityVector( velocity.unitVector().multiply(speed) );
			}
			
			Vector separation = this.owner.getSeparationVector( new Point(0 , 600));
			
			if ( separation.getIntegerMagnitude() < 550 ){ //prevent follower from running into asteroid
				
				trans.subtractVelocity(trans.getVelocityVector().projectedOverClamped(separation));
			}

		}
		
		@Override
		public Vector calculateVector() {
			
			Vector returnVector = new Vector(0,0);
			
			returnVector.set(
				( this.target.getX() - this.owner.getX() ) /30 ,
				( this.target.getY() - this.owner.getY() ) /30
			);
			
			if ( returnVector.getMagnitude() > 2){
				
				returnVector = returnVector.unitVector().multiply(speed) ;
			}
			
			Vector separation = this.owner.getSeparationVector( new Point(0 , 600));
			
			if ( separation.getIntegerMagnitude() < 550 ){ //prevent follower from running into asteroid
				
				returnVector = returnVector.subtract(returnVector.projectedOverClamped(separation) );
			}
			return returnVector;
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
		
		@Override
		public Vector calculateVector() {
			return null;
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
		
		@Override
		public Vector calculateVector() {
			return null;
		}

	}

}
