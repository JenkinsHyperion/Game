package misc;

import java.awt.Point;

import engine.BoardAbstract;
import entities.EntityDynamic;
import entityComposites.EntityStatic;
import physics.Vector;

public class SlidingMessageBehavior extends SlidingMessageBehaviorAbstract {
	
	protected SlidingMessageBehaviorAbstract currentBehavior;
	protected LinearSlidingMessageBehavior linearBehavior;
	protected NullSlidingMessageBehavior nullBehavior;

	public SlidingMessageBehavior(EntityStatic ownerRef, Point targetRef){

		this.linearBehavior = new LinearSlidingMessageBehavior(ownerRef, targetRef);
		this.nullBehavior = new NullSlidingMessageBehavior();
		this.currentBehavior = linearBehavior;
	}
	@Override
	public void updateAIPosition() {
		currentBehavior.updateAIPosition();
	}
	@Override
	public Vector calculateVector() {
		return currentBehavior.calculateVector();
	}
	public void setTargetPoint(Point target) {
		this.currentBehavior.setTargetPoint(target);
	}
	
	public void setBehavior(SlidingMessageBehaviorAbstract newBehavior) {
		this.currentBehavior = newBehavior;
	}
	
	
	
	public class LinearSlidingMessageBehavior extends SlidingMessageBehaviorAbstract {

		private long startTime;
		private long currentTime;
		
		public LinearSlidingMessageBehavior(EntityStatic ownerRef, Point targetRef){
			this.owner = ownerRef;
			this.target = targetRef;
			startTime = System.currentTimeMillis();
		}
		public Vector activeUpdateAIPosition() {
			currentTime = System.currentTimeMillis();
			if (currentTime - startTime > 1500) {
				setTargetPoint(new Point(BoardAbstract.B_WIDTH, owner.getY()));
				if (currentTime - startTime > 1800) {
					System.err.println("(message popup) Removing self...");
					setBehavior(nullBehavior);
					((SlidingMessagePopup)this.owner).removeSelf();
				}
			}
			if ( Math.abs(owner.getX() - target.getX()) > 5 ) {
				//this.owner.getTranslationComposite().setDX( (float)( this.target.getX() - this.owner.getX() ) /7 );
				//this.owner.getTranslationComposite().setDY( (float)( this.target.getY() - this.owner.getY() ) /7 );
				return new Vector(
						( (float)( this.target.getX() - this.owner.getX() ) /7 ),
						( (float)( this.target.getY() - this.owner.getY() ) /7 )
						);
			}
			else {
				//this.owner.getTranslationComposite().setDX(0f);
				//this.owner.getTranslationComposite().setDY(0f);
				return Vector.zeroVector;
			}

		}
		@Override
		public void updateAIPosition() {
			activeUpdateAIPosition();
		}
		@Override
		public Vector calculateVector() {
			return activeUpdateAIPosition();
		}
		@Override
		public void setTargetPoint(Point target) {
			this.target = target;
		}
		@Override
		public void setBehavior(SlidingMessageBehaviorAbstract newBehavior) {
			
		}
	}
	
	
	
	
	public class NullSlidingMessageBehavior extends SlidingMessageBehaviorAbstract {

		public NullSlidingMessageBehavior() {
		}
		@Override
		public void updateAIPosition() {}
		@Override
		public Vector calculateVector() {
			return Vector.zeroVector;
		}
		@Override
		public void setTargetPoint(Point target) {
		}
		@Override
		public void setBehavior(SlidingMessageBehaviorAbstract newBehavior) {
			
		}
	}
}
