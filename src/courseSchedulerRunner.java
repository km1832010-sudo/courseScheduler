public class courseSchedulerRunner {

    public static void main(String[] args) {
        //run();
        courseScheduler scheduler = new courseScheduler("src/classes.csv");
        scheduler.CompletedCourses();
    }
}