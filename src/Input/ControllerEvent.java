package Input;

public class ControllerEvent {

	private byte buttonCode;
	
	public static final byte VC_LEFT = 1;
	public static final byte VC_RIGHT = 2;
	public static final byte VC_UP = 3;
	public static final byte VC_DOWN = 4;
	
	public static final byte VC_A = 5;
	public static final byte VC_B = 6;
	public static final byte VC_X = 7;
	public static final byte VC_Y = 8;
	
	public ControllerEvent( byte code ) {
		this.buttonCode = code;
	}
	
	public byte getButton(){
		return buttonCode;
	}
	
}
