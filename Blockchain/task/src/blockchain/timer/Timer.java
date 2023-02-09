package blockchain.timer;

public class Timer {
    private static final long UNSTARTED_TIME = -1;
    private long start = UNSTARTED_TIME;

    public void start(){
        start = System.currentTimeMillis();
    }

    public long stop(){
        if(start == UNSTARTED_TIME) throw new RuntimeException("Stopping a timer before starting");
        long duration = System.currentTimeMillis() - start;
        start = UNSTARTED_TIME;
        return duration;
    }
}
