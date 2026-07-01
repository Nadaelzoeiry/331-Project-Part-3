import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PerformanceEvaluation {

    public static void main(String[] args) {
        // Number of times each scenario is executed for averaging.
        int runs = 5;

        // Stores measured wait and response times for each scenario.
        List<Long> baselineWait = new ArrayList<>();
        List<Long> baselineResp = new ArrayList<>();
        List<Long> inheritWait = new ArrayList<>();
        List<Long> inheritResp = new ArrayList<>();
        List<Long> ceilingWait = new ArrayList<>();
        List<Long> ceilingResp = new ArrayList<>();

        // Runs the baseline scenario and collects its performance data.
        for (int i = 0; i < runs; i++) {
            System.out.println("\nBaseline Run " + (i + 1));
            long[] result = PriorityInversion.run();
            baselineWait.add(result[0]);
            baselineResp.add(result[1]);
            pause();
        }

        // Runs the priority inheritance scenario and collects its results.
        for (int i = 0; i < runs; i++) {
            System.out.println("\n Priority Inheritance Run " + (i + 1));
            long[] result = PriorityInheritance.run();
            inheritWait.add(result[0]);
            inheritResp.add(result[1]);
            pause();
        }

        // Runs the priority ceiling scenario and collects its results.
        for (int i = 0; i < runs; i++) {
            System.out.println("\n Priority Ceiling Run " + (i + 1) );
            long[] result = PriorityCeiling.run();
            ceilingWait.add(result[0]);
            ceilingResp.add(result[1]);
            pause();
        }

        // Computes the average performance values for each scenario.
        double avgBaselineWait = average(baselineWait);
        double avgBaselineResp = average(baselineResp);
        double avgInheritWait = average(inheritWait);
        double avgInheritResp = average(inheritResp);
        double avgCeilingWait = average(ceilingWait);
        double avgCeilingResp = average(ceilingResp);

        System.out.println("\nPERFORMANCE SUMMARY");
        System.out.printf("%-15s %-20s %-20s%n", "Scenario", "Avg Wait (ms)", "Avg Response (ms)");
        System.out.printf("%-15s %-20.2f %-20.2f%n", "Baseline", avgBaselineWait, avgBaselineResp);
        System.out.printf("%-15s %-20.2f %-20.2f%n", "Inheritance", avgInheritWait, avgInheritResp);
        System.out.printf("%-15s %-20.2f %-20.2f%n", "Ceiling", avgCeilingWait, avgCeilingResp);

        // Opens a visual chart comparing the average wait times.
        showChart(avgBaselineWait, avgInheritWait, avgCeilingWait);
    }

    // Calculates the average of a list of timing values.
    private static double average(List<Long> values) {
        long sum = 0;
        for (long v : values) {
            sum += v;
        }
        return (double) sum / values.size();
    }

    // Pauses briefly between runs to keep the output readable.
    private static void pause() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Creates and displays the comparison chart window.
    private static void showChart(double baseline, double inherit, double ceiling) {
        JFrame frame = new JFrame("Average Waiting Time Comparison");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(baseline, inherit, ceiling));
        frame.setVisible(true);
    }
}

class ChartPanel extends JPanel {

    private final double baseline;
    private final double inherit;
    private final double ceiling;

    public ChartPanel(double baseline, double inherit, double ceiling) {
        this.baseline = baseline;
        this.inherit = inherit;
        this.ceiling = ceiling;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        int baseY = height - 50;

        double max = Math.max(baseline, Math.max(inherit, ceiling));
        if (max == 0) {
            max = 1;
        }

        int barWidth = 80;
        int gap = 60;
        int startX = 50;

        drawBar(g, startX, baseY, barWidth, height, baseline, max, "Baseline", new Color(255, 182, 193));
        drawBar(g, startX + barWidth + gap, baseY, barWidth, height, inherit, max, "Inheritance", new Color(173, 216, 230));
        drawBar(g, startX + 2 * (barWidth + gap), baseY, barWidth, height, ceiling, max, "Ceiling", new Color(221, 160, 221));
    }

    private void drawBar(Graphics g, int x, int baseY, int width, int panelHeight,
                          double value, double max, String label, Color color) {
        int maxBarHeight = panelHeight - 100;
        int barHeight = (int) ((value / max) * maxBarHeight);

        g.setColor(color);
        g.fillRect(x, baseY - barHeight, width, barHeight);
        g.setColor(Color.BLACK);
        g.drawRect(x, baseY - barHeight, width, barHeight);
        g.drawString(label, x, baseY + 20);
        g.drawString(String.format("%.1f ms", value), x, baseY - barHeight - 10);
    }
}
