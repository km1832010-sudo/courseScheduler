//stack class
public class Stack{
	
	private String[] stack;
	//tracks the top of the stack
	private int top;
	
	//constructor
	public Stack(int size) {
		stack= new String[size];
		top=-1;
	}
	
	//checks is the user can go previous, helper method
	public boolean isEmpty() {
		if(top==-1) {
			return true;
		}
		return false;
	}
	
	//this is a peek function, it allows the user to access the top of the stack without altering anything
	public String peek() {
		if (!isEmpty()){
			return stack[top];
		}
		return "Stack is empty.";
	}
	
	//adds a String to the top of the stack, returns true if successful
	public boolean push(String x) {
		if (top<stack.length-1 ) {
			top++;
			stack[top]=x;
			//System.out.println("XXXXX: "+x+" added to arr");
			return true;
		} else {
			System.out.println("Stack is full");
			return false;
		}
	}
	
	//removes the first value from the stack 
	public void pop() {
		if(!isEmpty()) {
			//System.out.println("XXXXX: "+stack[top]+" removed from  arr");
			stack[top]=null;
			top--;
		} else {
			System.out.println("Stack is empty.");
		}
	}
	
	public boolean contains(String str) {
		for(int i=0; i<stack.length; i++) {
			if(str.equals(stack[i])) {
				return true;
			}
		}
		return false;
	}
}