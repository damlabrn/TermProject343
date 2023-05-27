import java.util.Arrays;
import java.util.Random;

public class Main {

    // Define the maximum temperature and cooling rate for the annealing process
    private static final double MAX_TEMPERATURE = 10000;
    private static final double COOLING_RATE = 0.03;
    private static int[] values = { 68, 64, 47, 55, 72, 53, 81, 60, 72, 80, 62, 42, 48, 47, 68, 51, 48, 68, 83, 55, 48,
            44, 49, 68, 63, 71, 82, 55, 60, 63, 56, 75, 42, 76, 42, 60, 75, 68, 67, 42, 71, 58, 66, 72, 67, 78, 49, 50,
            51 };
    private static int[] weights = { 21, 11, 11, 10, 14, 12, 12, 14, 17, 13, 11, 13, 17, 14, 16, 10, 18, 10, 16, 17, 19,
            12, 12, 16, 16, 13, 17, 12, 16, 13, 21, 11, 11, 10, 14, 12, 12, 14, 17, 13, 11, 13, 17, 14, 16, 10, 18, 10,
            16 };

    private static int knapsackCapacity = 300;

    // Define the solution state variables
    private static boolean[] currentSolution; // solution 0-1'lerden oluşan
    private static boolean[] bestSolution;
    private static int currentValue; // current OFV
    private static int bestValue;
    private static boolean[] solutionSol;
    private static int solutionValue;;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Initialize the solution state variables
        currentSolution = new boolean[values.length];
        bestSolution = new boolean[values.length];
        solutionSol = new boolean[values.length];
        currentValue = 0;
        bestValue = 0; // new OFV gibi
        solutionValue = 0;
        // Initialize the random number generator
        Random random = new Random();

        // Start the simulated annealing process
        double currentTemperature = MAX_TEMPERATURE;
        // Randomly initialize the current solution
        for (int i = 0; i < currentSolution.length; i++) {
            currentSolution[i] = random.nextBoolean(); // Randomly assign true or false
        }
        currentValue = calculateValue(currentSolution);
        bestValue = currentValue;
        for (int i = 0; i < currentSolution.length; i++) {
            bestSolution[i] = currentSolution[i];
        }

        while (currentTemperature != 0) {

            for (int i = 0; i < currentSolution.length; i++) {

                if (random.nextDouble() < 0.5) { // Randomly decide whether to change the item's value or not
                    currentSolution[i] = !currentSolution[i]; // Flip the value of the item
                }
            }
            currentValue = calculateValue(currentSolution); // bu arrayin valuesu hesaplandı
            solutionValue = currentValue;
            for (int i = 0; i < currentSolution.length; i++) {
                solutionSol[i] = currentSolution[i];
            }

            double probability = calculateAcceptanceProbability(currentValue, solutionValue, currentTemperature);

            if (bestValue < currentValue) {
                if (probability == 1) {
                    bestValue = solutionValue; // new OFV

                    for (int i = 0; i < solutionSol.length; i++) {
                        bestSolution[i] = solutionSol[i];
                    }

                } else {
                    Double randomNumber = random.nextDouble();

                    if (probability > randomNumber) {
                        bestValue = solutionValue;

                        for (int i = 0; i < solutionSol.length; i++) {
                            bestSolution[i] = solutionSol[i];
                        }

                    } else {
                        bestValue = currentValue;
                        for (int i = 0; i < currentSolution.length; i++) {
                            bestSolution[i] = currentSolution[i];
                        }
                    }
                }
            }
            currentTemperature = currentTemperature * COOLING_RATE; // new temperature
        }

        // Print the best solution found
        System.out.println("Best Solution: " + Arrays.toString(bestSolution));
        System.out.println("Best Value: " + bestValue);
        System.out.println("Differences between temperatures: " + (MAX_TEMPERATURE - currentTemperature));
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Working time: " + executionTime + " ms");
    }

    // Helper method to calculate the fitness value of a solution
    private static int calculateValue(boolean[] solution) { // feasibility için solution'ın wightini toplar, uygunsa
                                                            // value döndürür
        int value = 0;
        int weight = 0;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i]) {
                value += values[i];
                weight += weights[i];
            }
        }
        if (weight > knapsackCapacity) {
            return 0;
        } else {
            return value;
        }
    }

    // Helper method to calculate the acceptance probability of a neighbor solution
    private static double calculateAcceptanceProbability(int currentValue, int neighborValue, double temperature) {
        if (neighborValue > currentValue) {
            return 1; // solution daha iyiyse 1 döndür
        } else {
            return Math.exp((neighborValue - currentValue) / temperature); // daha iyi değilse metropolis value
        } // neighbor=solution OFV, current value=current OFV
    }
}
