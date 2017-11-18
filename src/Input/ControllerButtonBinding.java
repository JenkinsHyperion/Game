package Input;

public class ControllerButtonBinding extends InputBinding{

	private KeyCommand command;
	
	public ControllerButtonBinding(int inputCode, KeyCommand command) {
		super(inputCode);
		this.command = command;
	}
	
	protected boolean keyMatch( ControllerEvent e ){
		return ( inputCode == e.getButton() );
	}
	
	public void onPressed(){ command.onPressed(); }  //POSSIBLE 
	public void onReleased(){ command.onReleased(); }
	public void onHeld(){ command.onHeld(); }
	
}
