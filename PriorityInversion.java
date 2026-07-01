public class PriorityInversion {

    // Shared lock used to simulate access to a critical resource.
    private static final Object lock = new Object();

    // Tracks whether the medium-priority thread is currently running.
    private static volatile boolean mediumRunning = false;

    // Stores timing information for the high-priority request sequence.
    private static long highRequestTime;
    private static long highAcquireTime;
    private static long highReleaseTime;

    // Runs the priority inversion scenario and returns timing measurements.
    public static long[] run() {
        Thread low = new Thread(PriorityInversion::lowTask, "Logger(Low)");
        Thread high = new Thread(PriorityInversion::highTask, "SafetyMonitor(High)");
        Thread medium = new Thread(PriorityInversion::mediumTask, "MotionPlanner(Medium)");

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

    // Simulates low-priority thread holding the shared resource.
    private static void lowTask() {
        synchronized (lock) {
            System.out.println("Low priority thread acquired MotorController");
            int elapsed = 0;
            while (elapsed < 4000) {
                sleep(200);
                elapsed += 200;
                if (mediumRunning) {
                    System.out.println("Low priority thread delayed, medium priority thread is executing");
                    elapsed -= 100;
                }
            }
            System.out.println("Low priority thread released MotorController");
        }
    }

    // Simulates a high-priority thread waiting for the shared resource.
    private static void highTask() {
        System.out.println("High priority thread requesting MotorController");
        highRequestTime = System.currentTimeMillis();
        synchronized (lock) {
            highAcquireTime = System.currentTimeMillis();
            System.out.println("High priority thread acquired MotorController");
            sleep(100);
            System.out.println("High priority thread released MotorController");
        }
        highReleaseTime = System.currentTimeMillis();
    }

    // Simulates a medium-priority thread that runs while the low-priority task is still active.
    private static void mediumTask() {
        mediumRunning = true;
        System.out.println("Medium priority thread executing, does not need MotorController");
        sleep(3000);
        mediumRunning = false;
        System.out.println("Medium priority thread finished");
    }

    // Routes messages through standard output.
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
