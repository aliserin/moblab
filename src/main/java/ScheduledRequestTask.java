import java.util.TimerTask;

public class ScheduledRequestTask extends TimerTask {

    @Override
    public void run() {
        Request.start();
    }
}