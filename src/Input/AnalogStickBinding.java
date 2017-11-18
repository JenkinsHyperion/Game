package Input;

public class AnalogStickBinding{

	protected float stickAngle;
	protected float stickMagnitude;
	
	
	
	public AnalogStickBinding(  ) {}
	
	protected void updatePosition( float stickAngle, float stickMagnitude ){
		this.stickAngle = stickAngle;
		this.stickMagnitude = stickMagnitude;
	}
	
	public void onMoved( float stickAngle, float stickMagnitude ){}
	public void onTilted( float stickAngle, float stickMagnitude ){}
	public void onReturned(){}
	
	protected void cutoffToZero(){
		stickAngle = 0;
		stickMagnitude = 0;
	}

}
