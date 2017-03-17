package utility;

public class LinkedNodeElement<T> extends LinkedNode<T>{

	private T element;
	
	protected LinkedNodeElement( T element ){
		this.element = element;
	}
	@Override
	protected void run(){
		// CODE TO RUN FOR EACH ELEMENT
		this.getNextNode().run();
	}
	
	protected T getElement(){
		return this.element;
	}
	
	protected boolean isNext(){
		return true;
	}
	
	protected void removeSelf(){
		this.prevNode.setNextNode( this.nextNode );
		this.nextNode.setPreviousNode( this.prevNode );
	}
	
}