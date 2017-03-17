package utility;

public class Ticket {

	LinkedNodeElement<?> node;
	
	protected Ticket( LinkedNodeElement<?> node ){
		this.node = node;
	}
	
	public void removeSelf(){
		node.removeSelf();
	}
	
}
