package utility;

public class DoubleLinkedList<T> {

	private final LinkedHead head = new LinkedHead();
	private LinkedNodeElement<T> currentItteration = new LinkedTail(head);
	
	public DoubleLinkedList(){
		
	}

	public Ticket add( T element ){
		
		try{
			LinkedNodeElement<T> newElement = this.head.addElement( element );
			currentItteration = newElement ;
			return new Ticket(newElement);
		}
		
		catch( ClassCastException exc ){
			System.err.println("Attempted to add entity without Composite.");
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
	
}
