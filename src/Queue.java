public class Queue<T>{
	private T[] queue;
	private int front=0;
	private int rear=0;
	//modulus variable, just equal to the size
	private int mod;
	//variable to track empty/full queue
	private int capacity=0;
	
	//constructor, gives warning for generic data type but is suppressed
	@SuppressWarnings("unchecked")
	public Queue(int size){
		queue= (T[]) new Object[size];
		mod= size;
	}
	
	//adds a variable to the queue after making sure its not full, and increments rear
	public void enque(T item) {
		if(!isFull()) {
			queue[rear]=item;
			capacity++;
			//System.out.println("Enqueing: " + item);
			rear=(rear+1)%mod;
		}else {
			System.out.println("Queue full.");
		}
	}
	
	//removes variable after making sure its not empty, and increments front
	public T deque(){
		if(!isEmpty()) {
			T first=front();
			queue[front]=null;
			front=(front+1)%mod;
			capacity--;
			//System.out.println("Dequeing: " + first);
			return first;
		}else {
			System.out.println("Queue is empty.");
			return null;
		}
	}
	
	//returns the next variable that needs to be dequeued, null if queue is empty
	public T front() {
		if(!isEmpty()) {
			return queue[front];
		} else {
			System.out.println("Queue is empty.");
			return null;
		}
	}
	
	//checks if the queue is empty
	public boolean isEmpty() {
		if(capacity==0) {
			return true;
		}
		return false;
	}
	
	//checks if the queue is full
	private boolean isFull() {
		if(capacity==mod) {
			return true;
		}
		return false;
	}
	
	public boolean contains(String str) {
		for(int i=0; i<queue.length; i++) {
			if(str.equals(queue[i])) {
				return true;
			}
		}
		return false;
	}
}