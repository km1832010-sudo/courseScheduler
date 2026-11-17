import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class courseScheduler{

    private class Course{
    	private String name;
    	private String CourseID;
    	private double creditHours;
    	private double difficulty;
		private String[] prereqs;
        private String department;
		public boolean completed=false;
        public Boolean canTake=null;

		
		public Course(String name, String CourseID, double creditHours, double difficulty, String[] prereqs, String department){
			this.name=name;
			this.CourseID=CourseID;
			this.creditHours=creditHours;
			this.difficulty=difficulty;
			this.prereqs=prereqs;
            this.department=department;
		}

        public String getName() {
            return name;
        }
		public String getCourseID() {
			return CourseID;
		}

		@Override
		public String toString(){
			return name + " (" + CourseID + ") - " + creditHours + " credit hours, difficulty: " + difficulty;
		}
    }
        

    private Graph[] graphs;
    private String[] graphNames;
    private ArrayList<Course> courseList;

    public courseScheduler(String fileName) {
        courseList = new ArrayList<Course>();
        graphs = processFile(fileName);
    }

    //Made by Jon Te'ab
    public Graph[] processFile(String filename){
        Scanner scanner;
        try {
            FileReader fileReader = new FileReader(filename);
            scanner = new Scanner(fileReader);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return null;
        }

        // Read every data row first so the arrays can be sized exactly
        //reads every line from the file and compiles it into the arraylists, which are then sent into the graphmaker
        //helper method to create the graph
        scanner.nextLine();
        String[] words;
        String currentGraphName="";
        String departments="";
        while(scanner.hasNextLine()){
            words = scanner.nextLine().split(",");
            if(words.length < 2){
                currentGraphName=words[0].substring(0,words[0].length()-1);
                departments+=currentGraphName + " ";
            }else{
                String name=words[0];
                String id =words[1];
                String hours =words[2]; 
                String difficulty =words[3];
                // each course's list of prerequisite targets
                String[] prereqs=new String[words.length-4];
                for (int j = 0; j < prereqs.length; j++){
                    prereqs[j]=stripQuotes(words[j+4]);
                }
                courseList.add(new Course(name, id, Double.parseDouble(hours), Double.parseDouble(difficulty), prereqs, currentGraphName));
            }
        }
        scanner.close();
        graphNames = departments.split(" ");
        return graphMaker();
    }

    public String traversal(String v1, String v2){
        return graphs[0].DFSdirected(v1, v2);
    }

    public String topologicalSort(){
        return graphs[0].topologicalSort();
    }

    public String getShortestPathValue(String v1, String v2, int weightIndex){
        return graphs[0].getShortestPathValue(v1, v2, weightIndex);
    }

    public String CycleDetection(){
        return graphs[0].CycleDetection();
    }

    // Removes a quote mark from the start and end of a piece, if there is one
    private String stripQuotes(String piece)
    {
        String result = piece;
        if (result.charAt(0) == '"')
        {
            result = result.substring(1);
        }
        if (result.charAt(result.length() - 1) == '"')
        {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private Graph[] graphMaker(){
        
        Graph[] g = new Graph[graphNames.length];
        for (int i = 0; i < graphNames.length; i++){
            ArrayList<String> ids = new ArrayList<String>();
            for (int j = 0; j < courseList.size(); j++){
                if(courseList.get(j).department.equals(graphNames[i])){
                    ids.add(courseList.get(j).getCourseID() + "");
                } else if (!courseList.get(j).department.equals(graphNames[i]) && ids.size() > 0){
                    break;
                }
            }
            g[i] = new Graph(ids.toArray(new String[ids.size()]), ids.size());
        }

        for(int i = 0; i < g.length; i++){
            boolean found = false;
            for (int j = 0; j < courseList.size(); j++){
                if(courseList.get(j).department.equals(graphNames[i])){
                    found = true;
                    String[] targets = courseList.get(j).prereqs;
                    for (int k = 0; k < targets.length; k++)
                    {
                        if(targets[k] != null){
                            // Weight = the course the edge starts from: {credits, difficulty}
                            // A new array per edge, so edges never share one
                            double[] weight = {courseList.get(j).creditHours, courseList.get(j).difficulty};
                            g[i].addEdge(courseList.get(j).getCourseID() + "", targets[k], weight);
                        }
                    }
                } else if (!courseList.get(j).department.equals(graphNames[i]) && found){
                    break;
                }
            }   
        }
        
        return g;
    }

    public void milestoneTwo(ArrayList<String> done, String target){
        // edge case 1: cycle
        if(graphs[0].CycleDetection().equals("CYCLE-DETECTED")){
            System.out.println("Error: prerequisite data contains a cycle.");
            return;
        }
        // edge case 2: invalid target ID
        if(indexOfID(target) == -1){
            System.out.println("Error: unknown course ID '" + target + "'.");
            return;
        }

        // mark completed courses by their index in courseIds
        for (int i = 0; i < done.size(); i++){
            int idx = indexOfID(done.get(i));
            if(idx == -1){
                System.out.println("Error: unknown course ID '" + done.get(i) + "'.");
                return;
            }else {
                courseList.get(idx).completed = true;
            }
        }

        // highest completed course that the target has reached, and if it can reach the target course
        // (topologicalSort lists the deepest course first, so the earliest index is the highest)
        String topo = topologicalSort();
        String highest = null;
        for (int i = 0; i < courseList.size(); i++){
            if(courseList.get(i).completed){
                String id = courseList.get(i).getCourseID() + "";
                if(getShortestPathValue(id, target, 0).startsWith("Shortest")
&& (highest == null || topo.indexOf(id) < topo.indexOf(highest))){
                    highest = id;
                }
            }
        }
        // edge case 3: unreachable target
        if(highest == null){
            System.out.println("Error: " + target + " is unreachable from your completed courses.");
            return;
        }
        System.out.println("Highest usable completed course: " + highest);
        System.out.println("Fastest (credits):    " + getShortestPathValue(highest, target, 0));
        System.out.println("Easiest (difficulty): " + getShortestPathValue(highest, target, 1));

        // semester plan: a course goes in once one of its prerequisites is completed or in an earlier semester
        String currentCourse=highest;
		int semester = 1;
		while(!currentCourse.equals(target) && semester<=8){
			String[] path =graphs[0].getShortestPath(currentCourse, target, 0).split(", ");
			System.out.println("Semester " + semester + ": " + path[0]);
			currentCourse=path[0];
			semester++;
		}
    }

	private int indexOfID(String id){
		for(int i=0; i<courseList.size(); i++){
			if(courseList.get(i).getCourseID().equals(id)){
				return i;
			}
		}
		return -1;
	}

    private int indexOfName(String name){
		for(int i=0; i<courseList.size(); i++){
			if(courseList.get(i).getName().equals(name)){
				return i;
			}
		}
		return -1;
	}
    //Class to make graphics for the course scheduler
    public void CompletedCourses(){
        //Page one, selecting completed courses:
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        JPanel panel=new JPanel(new GridLayout((graphs.length/2)+1,2));

        for(int j=0; j<graphs.length; j++){
            Object[][] data = new Object[(graphs[j].topologicalSort().split(" ").length)/2][2];
            int row = 0;
            int i=0;
            while(row < ((graphs[j].topologicalSort().split(" ").length)/2)){
                if(courseList.get(i).department.equals(graphNames[j])){
                    data[row][0]=courseList.get(i).getName();
                    data[row][1]=Boolean.FALSE;
                    row++;
                }
                i++;
            }
            //makes the first column uneditable, and the second column a checkbox   
            DefaultTableModel model = new DefaultTableModel(data, new String[]{graphNames[j] + " Courses", "Completed"}) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Return false for column 0 (making it uneditable), true for all other columns
                    return column != 0; 
                }
            };
            JTable table= new JTable(model);
            //adds checkbox to second column, and makes it editable
            table.getColumnModel().getColumn(1).setCellRenderer(
                    table.getDefaultRenderer(Boolean.class)
            );
            table.getColumnModel().getColumn(1).setCellEditor(
                (javax.swing.table.TableCellEditor) table.getDefaultEditor(Boolean.class)
            );
            panel.add(new JScrollPane(table));
        }

        JButton button = new JButton("What can I take next?");
        button.addActionListener(e -> {
            for(int j=0; j<graphs.length; j++){
                JTable table = (JTable) ((JScrollPane) panel.getComponent(j)).getViewport().getView();
                for (int i = 0; i < table.getRowCount(); i++) {
                    if(table.getValueAt(i, 1).equals(Boolean.TRUE)){
                        courseList.get(indexOfName((String) table.getValueAt(i, 0))).completed = true;
                    }
                }
            }
            availableCourses();
            JFrame courses = new JFrame();
            courses.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            courses.setSize(600, 600);
            JPanel panelCourses=new JPanel(new GridLayout((graphs.length/2),2));
            for(int j=0; j<graphs.length; j++){
                ArrayList<String> rows = new ArrayList<String>();
                for(int k=0; k<courseList.size(); k++){
                    if(courseList.get(k).department.equals(graphNames[j]) && courseList.get(k).canTake==true){
                        rows.add(courseList.get(k).getName());
                    }
                }
                Object[][] data = new Object[rows.size()][1];
                for(int i=0; i<rows.size(); i++){
                    data[i][0]=rows.get(i);
                }
            JTable table= new JTable(data, new String[]{graphNames[j]});
            panelCourses.add(new JScrollPane(table));
        }
        courses.add(panelCourses);
        courses.setVisible(true);
        });
        panel.add(button);

        JButton button2 = new JButton("Shortest path to target course");
        button2.addActionListener(e -> {
            JFrame shortestPath = new JFrame();
            shortestPath.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JPanel shortestPanel= new JPanel();
            shortestPanel.setLayout(null);
            for(int j=0; j<graphs.length; j++){
                JTable table = (JTable) ((JScrollPane) panel.getComponent(j)).getViewport().getView();
                for (int i = 0; i < table.getRowCount(); i++) {
                    if(table.getValueAt(i, 1).equals(Boolean.TRUE)){
                        courseList.get(indexOfName((String) table.getValueAt(i, 0))).completed = true;
                    }
                }
            }
            JTextField inputBox = new JTextField();
            inputBox.setBounds(50, 30, 150, 20);
            JLabel label = new JLabel("Enter target course ID:");
            label.setBounds(50, 10, 200, 20);
            JButton submitButton = new JButton("Calculate Shortest Path");
            submitButton.setBounds(50, 60, 200, 30);
            submitButton.addActionListener(e2 -> {
                availableCourses();
                int graphIndex=0;
                for(int j=0; j<graphNames.length; j++){
                    if(graphNames[j].equals(courseList.get(indexOfID(inputBox.getText())).department)){
                        graphIndex = j;
                        break;
                    }
                }
                String shortestPathResult="";
                int shortestPathInt=Integer.MAX_VALUE;
                boolean enteredDepartment=false;
                for (int i = 0; i < courseList.size(); i++){
                    if(courseList.get(i).department.equals(graphNames[graphIndex]) && courseList.get(i).canTake){
                        int shortestPathValue=graphs[graphIndex].getShortestPathValue(courseList.get(i).getCourseID(), inputBox.getText(), 0).split(",").length;
                        if(shortestPathValue!=1 &&shortestPathValue<shortestPathInt){
                            shortestPathInt = shortestPathValue;
                            shortestPathResult = graphs[graphIndex].getShortestPathValue(courseList.get(i).getCourseID(), inputBox.getText(), 0);
                        }
                        enteredDepartment=true;
                    }else if(enteredDepartment==true && !courseList.get(i).department.equals(graphNames[graphIndex])){
                        break;
                    }
                }

                if(shortestPathInt<2){
                    JLabel boxedLabel = new JLabel("Course doesn't exist or is unreachable");
                    boxedLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    boxedLabel.setOpaque(true);
                    boxedLabel.setBackground(new Color(243, 242, 255));
                    boxedLabel.setBounds(200, 200, 100, 50); 
                    shortestPanel.add(boxedLabel);
                    shortestPanel.repaint();
                }else{
                    for(int i=1; i<shortestPathInt; i++){
                        System.out.println(shortestPathResult);
                        String text=shortestPathResult.substring(0,shortestPathResult.indexOf(","));
                        shortestPathResult=shortestPathResult.substring(shortestPathResult.indexOf(",")+1);
                        JLabel boxedLabel = new JLabel(text);
                        boxedLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        boxedLabel.setOpaque(true);
                        boxedLabel.setBackground(new Color(243, 242, 255));
                        boxedLabel.setBounds(-100+150*i, 200, 100, 50); 
                        if(i>=2){
                            JLabel line= new JLabel();
                            line.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                            line.setOpaque(true);
                            line.setBounds(-150+150*i, 225, 50, 1); 
                            shortestPanel.add(line);
                        }
                        shortestPanel.add(boxedLabel);
                    }
                    shortestPanel.repaint();
                }
                shortestPath.setVisible(true);
            });
            shortestPanel.add(inputBox);
            shortestPanel.add(label);
            shortestPanel.add(submitButton);

            shortestPath.add(shortestPanel);
            shortestPath.setSize(800, 400);
            shortestPath.setVisible(true);

        });
        panel.add(button2);

        frame.add(panel);
        frame.setVisible(true);
    }

    public Panel courseSchedulerPanel(){
        return new Panel();
    }
    private void availableCourses(){
        for(int j=0; j<courseList.size(); j++){
            courseList.get(j).canTake=null;
        }
        //this code works, but idk why
        for(int i=0; i<courseList.size(); i++){
            if(courseList.get(i).completed){
                courseList.get(i).canTake=false;
                String prereqs[] = courseList.get(i).prereqs;
                for(int j=0; j<prereqs.length; j++){
                    courseList.get(indexOfID(prereqs[j])).canTake=true;
                }
            }else if(courseList.get(i).canTake==Boolean.TRUE || courseList.get(i).canTake==Boolean.FALSE){
                String prereqs[] = courseList.get(i).prereqs;
                for(int j=0; j<prereqs.length; j++){
                    courseList.get(indexOfID(prereqs[j])).canTake=false;
                }
            }else if(courseList.get(i).canTake==null){
                courseList.get(i).canTake=true;
            }
            if(courseList.get(i).completed){
                courseList.get(i).canTake=false;
                String prereqs[] = courseList.get(i).prereqs;
                for(int j=0; j<prereqs.length; j++){
                    courseList.get(indexOfID(prereqs[j])).canTake=true;
                }
            }else if(courseList.get(i).canTake==Boolean.TRUE || courseList.get(i).canTake==Boolean.FALSE){
                String prereqs[] = courseList.get(i).prereqs;
                for(int j=0; j<prereqs.length; j++){
                    courseList.get(indexOfID(prereqs[j])).canTake=false;
                }
            }
        }
    }
}
