public class MotorController {

    // Controls whether the priority ceiling feature is active.
    private boolean ceilingEnabled = false;

    // Stores the highest priority allowed by the ceiling rule.
    private int ceilingPriority = Thread.MAX_PRIORITY;

    // Enables or disables the priority ceiling behavior.
    public void setCeilingEnabled(boolean enabled) {
        this.ceilingEnabled = enabled;
    }

    // Sets the ceiling priority level.
    public void setCeilingPriority(int priority) {
        this.ceilingPriority = priority;
    }

    // Manages access to the shared motor controller resource.
    public synchronized void access(String threadName, long holdTimeMs) {
        Thread current = Thread.currentThread();
        int originalPriority = current.getPriority();

        if (ceilingEnabled && originalPriority < ceilingPriority) {
            current.setPriority(ceilingPriority);
            System.out.println(threadName + " priority raised to ceiling (" + ceilingPriority + ")");
        }

        System.out.println(threadName + " acquired MotorController");

        try {
            Thread.sleep(holdTimeMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println(threadName + " released MotorController");

        if (ceilingEnabled && current.getPriority() != originalPriority) {
            current.setPriority(originalPriority);
            System.out.println(threadName + " priority restored to " + originalPriority);
        }
    }
}
