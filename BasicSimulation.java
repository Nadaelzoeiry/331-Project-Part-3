public class BasicSimulation {

    public static void main(String[] args) {
        MotorController controller = new MotorController();

        // Starts a safety monitoring thread for emergency checks.
        Thread safetyMonitor = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Safety monitor is checking for emergency conditions.");
                controller.access("SafetyMonitor(High)", 300);
                sleep(200);
            }
        }, "SafetyMonitor");

        // Starts a motion planning thread for movement commands.
        Thread motionPlanner = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Motion planner is sending movement commands.");
                controller.access("MotionPlanner(Medium)", 500);
                sleep(200);
            }
        }, "MotionPlanner");

        // Starts a logging thread to record system activity.
        Thread logger = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Logger is recording system activity.");
                controller.access("Logger(Low)", 700);
                sleep(200);
            }
        }, "Logger");

        // Assigns thread priorities so critical tasks are handled first.
        safetyMonitor.setPriority(Thread.MAX_PRIORITY);
        motionPlanner.setPriority(Thread.NORM_PRIORITY);
        logger.setPriority(Thread.MIN_PRIORITY);

        // Begins execution of all threads.
        safetyMonitor.start();
        motionPlanner.start();
        logger.start();

        // Waits for each thread to finish before continuing.
        joinThread(safetyMonitor);
        joinThread(motionPlanner);
        joinThread(logger);

        // Signals that the simulation has completed successfully.
        System.out.println("Simulation completed successfully.");
    }

    // Pauses execution for the specified number of milliseconds.
    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Waits for a thread to finish its execution.
    private static void joinThread(Thread t) {
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
