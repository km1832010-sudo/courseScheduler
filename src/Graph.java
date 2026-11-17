import java.util.ArrayList;

public class Graph{
	//Vertex object if values or other variables need to be added in the future
	public class Vertex{
		private String name;
		
		public Vertex(String n){
			name=n;
		}
		
		
		public String getName() {
			return name;
		}
	}
	
	//Object edges between vertices
	public class Edge{
		private Vertex source;
		private Vertex destination;
		private double[] weight;
		
		public Edge(Vertex v1, Vertex v2, double[] w) {
			source=v1;
			destination=v2;
			weight=w;
		}
		
		//helps with adding weight when its not constructed
		public void setWeight(double w, int i) {
			weight[i]=w;
		}
		
		public double getWeight(int i) {
			return weight[i];
		}
		
		public Vertex getSource() {
			return source;
		}
		public Vertex getDestination() {
			return destination;
		}
		
		@Override
		public String toString() {
			return "(" + source.getName() + "," + destination.getName()+")";
		}
	}
	
	//Adjacency Matrix represents edges between Vertices. vertices stores those vertices and their indexes
	private Edge[][] adjacencyMatrix;
	private ArrayList<Vertex> vertices;
	private static final int max = Integer.MAX_VALUE;
	   
	//Overloaded constructor, user can simply send an array for all vertexes they want to create.
	public Graph(String[] a, int vertexCount) {
		vertices=new ArrayList<Vertex>();
		adjacencyMatrix= new Edge[vertexCount][vertexCount];
		for(int i=0; i<a.length; i++) {
			vertices.add(new Vertex(a[i]));
		}
	}
	
	public Graph(int vertexCount) {
		vertices=new ArrayList<Vertex>();
		adjacencyMatrix= new Edge[vertexCount][vertexCount];
	}
	
	//Method to add vertex, might be updated if vertex class has more than a name attribute
	public void addVertex(String str) {
		vertices.add(new Vertex(str));
	}
	
	//Overloaded add Edge method to account for or ignore weight, 
	//all edges are one way, but the user can simply create two in the adjacency matrix
	public void addEdge(String v1, String v2, double[] weight) {
		if(searchVertex(v1)!=-1 && searchVertex(v2)!=-1) {
			Edge edge= new Edge(getVertex(v1),getVertex(v2),weight);
			adjacencyMatrix[searchVertex(v1)][searchVertex(v2)]= edge;
		}else {
			System.out.println("Vertex not found.");
		}
	}
	
	public void addEdge(String v1, String v2) {
		if(searchVertex(v1)!=-1 && searchVertex(v2)!=-1) {
			Edge edge= new Edge(getVertex(v1),getVertex(v2),new double[]{0.0});
			adjacencyMatrix[searchVertex(v1)][searchVertex(v2)]= edge;
		}else {
			System.out.println("Vertex not found.");
		}
	}
	
	//To add weight for an edge, also accounts for direction
	public void addWeight(String v1, String v2, double weight, int index) {
		if(searchVertex(v1)!=-1 && searchVertex(v2)!=-1) {
			int i=searchVertex(v1);
			int j=searchVertex(v2);
			if(adjacencyMatrix[i][j]!=null) {
				adjacencyMatrix[i][j].setWeight(weight, index);
			}
		}
	}
	
	//Checks if two Vertices are connected by edges in the adjacency matrix
	public boolean isConnected(String v1, String v2) {
		if((searchVertex(v1)!=-1 && searchVertex(v2)!=-1)
				&& (adjacencyMatrix[searchVertex(v1)][searchVertex(v2)]!=null 
				&& adjacencyMatrix[searchVertex(v2)][searchVertex(v1)]!=null)) {
			return true;
		}
		return false;
	}
	
	//Prints all non-null edges
	public void printAdjacencyMatrix() {
		for(int i=0; i<vertices.size(); i++) {
			for(int j=i+1; j<vertices.size(); j++) {
				if(adjacencyMatrix[i][j]!=null)
					System.out.println(adjacencyMatrix[i][j]);
			}
		}
	}
	
	//Removes vertex from vertices, 
	//then shifts adjacency Matrix once to the left and once upwards starting at index
	public void removeVertex(String v1) {
		if(searchVertex(v1)!=-1) {
			int index=searchVertex(v1);
			vertices.remove(index);
			for(int i=0; i<adjacencyMatrix.length;i++) {
				for(int j=index+1; j<adjacencyMatrix.length; j++) {
					adjacencyMatrix[j-1][i]=adjacencyMatrix[j][i];
					adjacencyMatrix[j][i]=null;
					
					adjacencyMatrix[i][j-1]=adjacencyMatrix[j][i];
					adjacencyMatrix[i][j]=null;
				}
			}
		}else {
			System.out.println("Vertex not found.");
		}
	}
	
	
	//Self-implemented Dijkstra class, returns table in a 2d ArrayList
	public String[][] dijkstra(String v1, int weightPos) {
		//Creating table and visited and unvisited stacks(they aren't actually 
		//stacks because then I wouldn't be able to go in order of shortest distance)
	    String[][] table = new String[vertices.size()][3];
	    ArrayList<String> visited= new ArrayList<String>();
	    ArrayList<String> unvisited= new ArrayList<String>();
	    
	    //Adds the names of the vertices to the table and the unvisited stack, 
	    //also establishes initial distance as max-1 to all vertices in the table
	    //If distance was set to max that could cause an infinite loop error later
	    for(int i=0; i<vertices.size(); i++) {
	    	unvisited.add(vertices.get(i).getName());
	    	table[i][0]=vertices.get(i).getName();
	    	table[i][1]=(max-1)+"";
	    }
	    //declares a source as the parameter before running while loop
	    String source=getVertex(v1).getName();
	    table[searchVertex(source)][1]=0+"";
	    //continues until the unvisited list is empty
	    while(unvisited.size()!=0) {
	    	int index=searchVertex(source);
	    	//for every Edge leading from the source, if the destination isn't visited, and the distance
	    	//for the path to source + the distance to the destination, is less than the recorded least 
	    	//distance to that vertex, then the previous node and shortest distance are updated for the destination
	    	for(int i=0; i<adjacencyMatrix.length; i++) {
	    		if(adjacencyMatrix[index][i]!=null && 
	    				visited.indexOf(adjacencyMatrix[index][i].getDestination().getName())==-1 &&
	    				adjacencyMatrix[index][i].getWeight(weightPos)+Double.parseDouble(table[index][1])
	    				<Double.parseDouble(table[i][1])) {
	    			table[i][1]=(adjacencyMatrix[index][i].getWeight(weightPos)+Double.parseDouble(table[index][1]))+"";
	    			table[i][2]=source;
	    		}
	    	}
	    	//Stacks are updated
	    	visited.add(source);
	    	unvisited.remove(unvisited.indexOf(source));
	    	
	    	//Minimum algorithm to find nextSource
	    	double minDist=max;
	    	String nextSource=null;
	    	for(int i=0; i<table.length; i++) {
	    		if(Double.parseDouble(table[i][1])<minDist && visited.indexOf(table[i][0])==-1) {
	    			minDist=Double.parseDouble(table[i][1]);
	    			nextSource=table[i][0];
	    		}
	    	}
	    	if (nextSource == null) {
	    	    break;
	    	}
	    	source = nextSource;
	    }
	    //table returned after while loop is complete
	    return table;
	}
	
	//calls Dijkstra and prints everything in the table
	public void dijkstraPrint(String v1, int index) {
		if(searchVertex(v1)!=-1) {
			String[][] table=dijkstra(v1, index);
			
		    for(int i=0; i<table.length; i++) {
		    	System.out.println(table[i][0]+":  "+table[i][1]+"  "+table[i][2]);
		    }
		}else {
			System.out.println("Vertex does not exist.");
		}
	}
	
	//calls Dijkstra and finds the shortest path for two vertexes, if path doesn't exist -1 is returned
	public String getShortestPath(String v1, String v2, int index) {
		if(searchVertex(v1)!=-1 && searchVertex(v2)!=-1) {
			String table[][]=dijkstra(v1, index);
			if(Double.parseDouble(table[searchVertex(v2)][1])!=max-1) {
				return "Shortest path from "+v1+" to "+v2+" is: "+Double.parseDouble(table[searchVertex(v2)][1]);
			}else {
				return "No path found from "+v1+" to "+v2;
			}
		}else {
			return "One or two of the given Vertecis don't exist.";
		}
	}

	public String getShortestPathValue(String v1, String v2, int index) {
		if(searchVertex(v1)!=-1 && searchVertex(v2)!=-1) {
			String table[][]=dijkstra(v1, index);
			if(Double.parseDouble(table[searchVertex(v2)][1])!=max-1) {
				int node=searchVertex(v2);
				String Path="";
				while(node!=searchVertex(v1)){
					Path=table[node][0]+", "+Path;
					node=searchVertex(table[node][2]);
				}
				return v1+", "+Path;
			}else {
				return "No path found from "+v1+" to "+v2;
			}
		}else {
			return "One or two of the given Vertecis don't exist.";
		}
	}
	
	//Recursive DFS function
	public String DFS(String v1, String v2) {
		if(searchVertex(v1)!=-1 && searchVertex(v2)!=-1) {
			//creates stack for DFS and adds source to it
			Stack visited = new Stack(100);
			visited.push(v1);
			//calls the helper with the destination and stack
			String path =DFShelper(v2,visited);
			if(path.equals("")) {
				return "Path not found";
			}else{
				return "("+path;
			}
		}else {
			return "Vertices not found.";
		}
	}
	
	private String DFShelper(String v2, Stack visited) {
		int i=searchVertex(visited.peek());
		//updates stack for end condition if not path is found
		visited.pop();
		//for every vertex adjacent to the source, if its equal to the destination then the path is returned
		//if its not the destination and not null and not already visited then its added to the visited stack
		for(int j=0; j<vertices.size(); j++) {
			if(adjacencyMatrix[i][j]!=null && adjacencyMatrix[i][j].getDestination().getName().equals(v2)) {
				return vertices.get(i).getName()+", "+vertices.get(j).getName()+")";
			}else if(adjacencyMatrix[i][j]!=null) {
				if(!visited.contains(adjacencyMatrix[i][j].getDestination().getName())) {
					visited.push(adjacencyMatrix[i][j].getDestination().getName());
				}
			}
		}
		
		//recursive call, if the visited stack is not empty yet then the path is returned + a recursive call
		//when the destination is found this will create a correct path
		if(!visited.peek().equals("Stack is empty.")) {
			return vertices.get(i).getName()+", "+DFShelper(v2, visited);
		}else {
			return "";
		}
	}
	
	//DFS for directed graphs, also detects cycles, if a cycle is detected it will return "CYCLE-DETECTED"
	public String DFSdirected(String v1, String v2) {
		if(searchVertex(v1)!=-1 && searchVertex(v2)!=-1) {
			//creates stack for DFS and adds source to it
			Stack visited = new Stack(100);
			visited.push(v1);
			//calls the helper with the destination and stack
			String path =DFShelperDirected(v2,visited);
			if(path.equals("")) {
				return "Path not found";
			}else if(path.equals("CYCLE-DETECTED")){
				return "CYCLE-DETECTED";
			}else {
				return "("+path;
			}
		}else {
			return "Vertices not found.";
		}
	}
	
	private String DFShelperDirected(String v2, Stack visited) {
		int i=searchVertex(visited.peek());
		//for every vertex adjacent to the source, if its equal to the destination then the path is returned
		//if its not the destination and not null and not already visited then its added to the visited stack
		//else a cycle is detected and the function returns "CYCLE-DETECTED"
		for(int j=0; j<vertices.size(); j++) {
			if(adjacencyMatrix[i][j]!=null && adjacencyMatrix[i][j].getDestination().getName().equals(v2)) {
				return vertices.get(i).getName()+", "+vertices.get(j).getName()+")";
			}else if(adjacencyMatrix[i][j]!=null) {
				if(!visited.contains(adjacencyMatrix[i][j].getDestination().getName())) {
					visited.push(adjacencyMatrix[i][j].getDestination().getName());
					String x= DFShelperDirected(v2, visited);
					if(x.equals("CYCLE-DETECTED")) {
						return "CYCLE-DETECTED";
					}else if(!x.equals("")) {
						return vertices.get(i).getName()+", "+x;
					}
					visited.pop();
				}else {
					return "CYCLE-DETECTED";
				}
			}
		}
		return "";
	}

	//Detects cycles 
	public String CycleDetection() {
			//path stack is used to keep track of the current path in the DFS traversal, 
			//while the visited stack keeps track of all the vertices that have been fully explored. If a vertex is encountered that is already in the path stack, a cycle is detected.
			Stack path = new Stack(100);
			Stack visited = new Stack(100);
			//loops through all vertices, if a vertex has not been visited it calls the helper method
			for(int i=0; i<vertices.size(); i++){
				if(!visited.contains(vertices.get(i).getName())){
					String result =CycleDetectionHelper(i,path,visited);
					if(result.equals("CYCLE-DETECTED")){
						return "CYCLE-DETECTED";
					}
				}
			}
			return "CLEAR";
		}
		
	//Recursive helper method for cycle detection, uses DFS traversal to explore the graph and check for cycles.
	private String CycleDetectionHelper(int i, Stack path, Stack visited) {
		//creates current path
		path.push(vertices.get(i).getName());
		for(int j=0; j<vertices.size(); j++) {
			//if the destination vertex is already in the current path, a cycle is detected and the method returns "CYCLE-DETECTED"
			if(adjacencyMatrix[i][j]!=null && path.contains(adjacencyMatrix[i][j].getDestination().getName())) {
				return "CYCLE-DETECTED";
			//if the destination vertex has not been visited, the method recursively calls itself to explore that vertex's neighbors
			}else if(adjacencyMatrix[i][j]!=null && !visited.contains(adjacencyMatrix[i][j].getDestination().getName())){
				if(CycleDetectionHelper(j,path,visited).equals("CYCLE-DETECTED")) {
					return "CYCLE-DETECTED";
				}
			}
		}
		//Marks vertex as fully explored and updates path
		visited.push(vertices.get(i).getName());
		path.pop();
		return "CLEAR";
	}

	public String topologicalSort() {
		String result="";
		if(!CycleDetection().equals("CYCLE-DETECTED")) {
			Stack visited = new Stack(100);
			//for all vertices, if it has not been visited, the helper method is called to perform a depth-first traversal 
			// and add the vertex to the result string in topological order.
			for(int i=0; i<vertices.size(); i++){
				if(!visited.contains(vertices.get(i).getName())){
					result+=topologicalSortHelper(i,visited)+" ";
				}
			}
		}
		return result;
	}

	private String topologicalSortHelper(int i, Stack visited) {
		//marks vertex as visited and adds it to the result string
		visited.push(vertices.get(i).getName());
		String output="";
		//for all vertices adjacent to the current vertex, if it has not been visited,
		//the helper method is called recursively to perform a depth-first traversal and add the vertex to the result string in topological order.
		for(int j=0; j<vertices.size(); j++) {
			if(adjacencyMatrix[i][j]!=null && !visited.contains(adjacencyMatrix[i][j].getDestination().getName())) {
				output+=topologicalSortHelper(j,visited)+" ";
			}
		}
		return output+vertices.get(i).getName();
	}
	
	//recursive BFS method, exactly the same as DFS, but uses queue
	public String BFS(String v1, String v2) {
		if(searchVertex(v1)!=-1 && searchVertex(v2)!=-1) {
			Queue<String> visited = new Queue<String>(100);
			visited.enque(v1);;
			String path =BFShelper(v2,visited);
			if(path.equals("")) {
				return "Path not found";
			}else{
				return "("+ path;
			}
		}else {
			return "Vertices not found.";
		}
	}
	
	private String BFShelper(String v2, Queue<String> visited) {	
		if(!visited.front().equals("Stack is empty.")) {
			return "";
		}
		int i=searchVertex(visited.deque());
		for(int j=0; j<vertices.size(); j++) {
			if(adjacencyMatrix[i][j]!=null && adjacencyMatrix[i][j].getDestination().getName().equals(v2)) {
				return vertices.get(i).getName()+", "+vertices.get(j).getName()+")";
			}else if(adjacencyMatrix[i][j]!=null) {
				if(!visited.contains(adjacencyMatrix[i][j].getDestination().getName())) {
					visited.enque(adjacencyMatrix[i][j].getDestination().getName());
				}
			}
		}
		
		if(!visited.front().equals("Stack is empty.")) {
			return vertices.get(i).getName()+", "+BFShelper(v2, visited);
		}else {
			return "";
		}
	}
	
	//private helper method that turns a String into the index of it in the vertices ArrayList,
	//if not present it returns -1
	private int searchVertex(String v1) {
		for(int i=0; i<vertices.size(); i++) {
			if(vertices.get(i).getName().compareTo(v1)==0) {
				return i;
			}
		}
		return -1;
	}
	
	//private helper method, finds the vertex of a string in the vertices ArrayList,
	//can be replaced completely by search vertex, but I'm too lazy
	private Vertex getVertex(String v1) {
		for(int i=0; i<vertices.size(); i++) {
			if(vertices.get(i).getName().compareTo(v1)==0) {
				return vertices.get(i);
			}
		}
		return null;
	}
}