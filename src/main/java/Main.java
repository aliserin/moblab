import java.util.Timer;

public class Main {
    public static void main(String[] args) {

        Timer time = new Timer();
        ScheduledRequestTask st = new ScheduledRequestTask();
        time.schedule(st, 0, 60000);
        System.exit(-1);

//        Request.start();
    }
}
