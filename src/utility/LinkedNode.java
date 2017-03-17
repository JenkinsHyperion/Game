package utility;

abstract class LinkedNode<T>{ // make interface?
	
	protected LinkedNodeElement<T> nextNode;
	protected LinkedNode<T> prevNode;
	
	protected abstract void run();
	
	protected void setPreviousNode( LinkedNode<T> prevNode ){ this.prevNode = prevNode; }
	protected void setNextNode( LinkedNodeElement<T> nextNode ){ this.nextNode = nextNode; }
	protected LinkedNode<T> getPreviousNode(){ return this.prevNode; }
	protected LinkedNodeElement<T> getNextNode( ){ return this.nextNode; }
	
}