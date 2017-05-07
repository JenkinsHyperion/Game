package utility;

public class DoubleLinkedList<T> {

	private final LinkedHead head = new LinkedHead();
	private LinkedNodeElement<T> currentItteration = new LinkedTail(head);
	private int size;
	
	public DoubleLinkedList(){
		this.size = 0;
	}

	public ListNodeTicket add( T element ){
		
		try{
			LinkedNodeElement<T> newElement = this.head.addElement( element );
			currentItteration = newElement ;
			size++;
			return new ListNodeTicket(newElement);
		}
		
		catch( ClassCastException exc ){
			System.err.println("Attempted to add entity without Composite DoubleLinkedList.");
			return null;
		}
		
	}
	
	public void executeThrough(  ){
		
	}
	
	public T get(){
		LinkedNodeElement<T> currentNode = this.currentItteration;
		
		this.currentItteration = this.currentItteration.getNextNode();
		
		return currentNode.getElement();
	}
	
	public boolean hasNext(){
		return currentItteration.isNext();
	}
	
	public void remove(){
		
		LinkedNodeElement<T> currentNode = this.currentItteration;
		
		currentNode.removeSelf();
		
		this.currentItteration = this.currentItteration.getNextNode();
		
		this.size--;
	}
	
	public int size(){
		return this.size;
	}
	
	private class LinkedHead extends LinkedNode<T>{
		
		private LinkedTail tail; //useful having pointer to tail for end adding?
		
		protected LinkedHead() {
			this.tail = new LinkedTail( this );
			this.setNextNode(tail);
			tail.setPreviousNode( LinkedHead.this);
		}
		
		protected void beginRun(){
			this.run();
		}
		@Override
		protected void run(){
			this.getNextNode().run();
		}
		
		protected LinkedNodeElement<T> addElement( T element ){
			LinkedNodeElement<T> newNode = new LinkedNodeElement<T>( element );
			
			newNode.setNextNode( this.nextNode ); 	// Head     Add --> Tail^      order of these is important

			newNode.setPreviousNode(this); 			// Head <-- Add     Tail
			
			this.nextNode.setPreviousNode(newNode); // Head     Add <-- Tail^
			
			this.nextNode = newNode;   				// Head --> Add     Tail**   change this.nextNode from tail to add LAST
			
			return newNode;
		}
		
	}
	
	private class LinkedTail extends LinkedNodeElement<T>{
		
		protected LinkedTail( LinkedHead head ) {
			super(null);
			this.prevNode = head;
		}
		
		@Override
		protected boolean isNext() {
			currentItteration = head.getNextNode(); //RESET ITTERATOR BACK TO HEAD
			return false; //TERMINATE WHILE LOOP ITTERATING
		}
		
		@Override
		protected void run(){
			currentItteration = head.getNextNode();//RESET ITTERATOR BACK TO HEAD
			//END OF RUNTHROUGH CHAIN
		}
		
	}
	
	
	abstract class LinkedNode<T>{ // make interface?
		
		protected LinkedNodeElement<T> nextNode;
		protected LinkedNode<T> prevNode;
		
		protected abstract void run();
		
		protected void setPreviousNode( LinkedNode<T> prevNode ){ this.prevNode = prevNode; }
		protected void setNextNode( LinkedNodeElement<T> nextNode ){ this.nextNode = nextNode; }
		protected LinkedNode<T> getPreviousNode(){ return this.prevNode; }
		protected LinkedNodeElement<T> getNextNode( ){ return this.nextNode; }
		
	}
	
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
			size--;
		}
		
	}
	
	
}
