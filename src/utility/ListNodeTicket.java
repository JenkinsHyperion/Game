package utility;

public class ListNodeTicket {

	DoubleLinkedList<?>.LinkedNodeElement<?> node;
	
	//protected ListNodeTicket( LinkedNodeElement<T> node ){
	//	this.node = node;
	//}
	protected ListNodeTicket(){
		
	}
	
	protected ListNodeTicket(DoubleLinkedList<?>.LinkedNodeElement<?> newElement) {
		this.node = newElement;
	}

	public void removeSelfFromList(){
		node.removeSelf();
	}
	
	public boolean isActive(){
		return true;
	}
	
}
