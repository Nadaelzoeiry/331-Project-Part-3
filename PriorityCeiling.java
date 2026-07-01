public class PriorityCeiling {

    // Shared lock used to simulate access to a protected resource.
    private static final Object lock = new Object();

    // Highest priority level allowed by the ceiling protocol.
    private static final int CEILING_PRIORITY = Thread.MAX_PRIORITY;

    // Stores timing information for the high-priority request sequence.
    private static long highRequestTime;
    private static long highAcquireTime;
    private static long highReleaseTime;

    // Runs the priority ceiling scenario and returns timing measurements.
    public static long[] run() {
        Thread low = new Thread(PriorityCeiling::runLowTask, "Logger(Low)");
        Thread high = new Thread(PriorityCeiling::runHighTask, "SafetyMonitor(High)");
        Thread medium = new Thread(PriorityCeiling::runMediumTask, "MotionPlanner(Medium)");

        low.setPriority(Thread.MIN_PRIORITY);
        medium.setPriority(Thread.NORM_PRIORITY);
        high.setPriority(Thread.MAX_PRIORITY);

        low.start();
        sleep(300);
        high.start();
        sleep(100);
        medium.start();

        join(low);
        join(high);
        join(medium);

        long waitTime = highAcquireTime - highRequestTime;
        long responseTime = highReleaseTime - highRequestTime;

        System.out.println("High priority thread waiting time: " + waitTime + " ms");
        System.out.println("High priority thread response time: " + responseTime + " ms");

        return new long[] { waitTime, responseTime };
    }

    //  low-priority thread holding the shared resource.
    private static void runLowTask() {
        Thread self = Thread.currentThread();
        int originalPriority = self.getPriority();

        synchronized (lock) {
            self.setPriority(CEILING_PRIORITY);
            System.out.println("Low priority thread raised to the ceiling priority (" + CEILING_PRIORITY + ")");
            System.out.println("Low priority thread acquired the MotorController");

            sleep(4000);

            System.out.println("Low priority thread released the MotorController");
            self.setPriority(originalPriority);
            System.out.println("Low priority thread priority restored to " + originalPriority);
        }
    }

    // Simulates a high-priority thread requesting access to the shared resource.
    private static void runHighTask() {
        System.out.println("High priority thread requesting the MotorController");
        highRequestTime = System.currentTimeMillis();

        synchronized (lock) {
            highAcquireTime = System.currentTimeMillis();
            System.out.println("High priority thread acquired the MotorController");
            sleep(100);
            System.out.println("High priority thread released the MotorController");
        }
        highReleaseTime = System.currentTimeMillis();
    }

    // Simulates a medium-priority thread that runs while the resource is in use.
    private static void runMediumTask() {
        System.out.println("Medium priority thread executing, does not need the MotorController");
        sleep(3000);
        System.out.println("Medium priority thread finished");
    }

    // Sends status messages to standard output.
    private static void log(String msg) {
        System.out.println(msg);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void join(Thread t) {
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
