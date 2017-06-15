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

	public void removeSelf(){
		node.removeSelf();
	}
	
	public boolean isActive(){
		return true;
	}
	
}
