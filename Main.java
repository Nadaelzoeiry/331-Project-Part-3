import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Creates a scanner for reading the user's menu selection.
        Scanner scanner = new Scanner(System.in);

        // Displays the available simulation and evaluation options.
        System.out.println("Real-Time Robotic Arm Controller, Nada's Project");
        System.out.println("1. Basic Multi-threaded Simulation (Task 1 and 2)");
        System.out.println("2. Priority Inversion Demonstration (Task 3)");
        System.out.println("3. Priority Inheritance Protocol (Task 4)");
        System.out.println("4. Priority Ceiling Protocol (Task 5)");
        System.out.println("5. Performance Evaluation (Task 6)");
        System.out.print("Select an option: ");

        // Reads the user's selected option.
        int choice = scanner.nextInt();

        // Routes the program to the selected simulation or evaluation.
        switch (choice) {
            case 1:
                BasicSimulation.main(args);
                break;
            case 2:
                PriorityInversion.run();
                break;
            case 3:
                PriorityInheritance.run();
                break;
            case 4:
                PriorityCeiling.run();
                break;
            case 5:
                PerformanceEvaluation.main(args);
                break;
            default:
                System.out.println("Invalid option");
        }

        // Closes the input scanner.
        scanner.close();
    }
}
