package utility;

public class ListNodeTicket {

	LinkedNodeElement<?> node;
	
	protected ListNodeTicket( LinkedNodeElement<?> node ){
		this.node = node;
	}
	
	public void removeSelf(){
		node.removeSelf();
	}
	
}
