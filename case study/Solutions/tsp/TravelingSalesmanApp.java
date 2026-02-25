import java.util.Random;

/**
 * Application entry point.
 * Responsible only for wiring (composition root) — all real logic lives in dedicated classes.
 */
public class TravelingSalesmanApp {

    public static void main(String[] args) {

        // --- Configuration ---
        GeneticAlgorithmConfig config = new GeneticAlgorithmConfig.Builder()
                .maxGenerations(100)
                .populationSize(50)
                .mutationRate(0.5)
                .crossoverRate(0.5)
                .elitism(true)
                .startCity(0)
                .build();

        // --- Infrastructure ---
        Random random = new Random();
        DistanceMatrix distances = DistanceMatrix.createDefault();

        // --- Build collaborators (depend on abstractions) ---
        PopulationInitializer  initializer  = new PopulationInitializer(distances.getCityCount(), config.startCity, random);
        FitnessEvaluator       evaluator    = new TotalDistanceFitnessEvaluator(distances);
        SelectionStrategy      selection    = new RouletteWheelSelection(random);
        CrossoverOperator      crossover    = new UniformCrossover(config.crossoverRate, distances.getCityCount(), random);
        MutationOperator       mutation     = new SwapMutation(config.mutationRate, distances.getCityCount(), random);

        // --- Run ---
        GeneticAlgorithm ga = new GeneticAlgorithm(config, initializer, evaluator, selection, crossover, mutation);
        Route best = ga.run();

        // --- Report ---
        TotalDistanceFitnessEvaluator concreteEvaluator = (TotalDistanceFitnessEvaluator) evaluator;
        System.out.println("\n=== FINAL RESULT ===");
        System.out.println("Best route : " + best);
        System.out.printf("Distance   : %.2f%n", concreteEvaluator.totalDistance(best));
        System.out.printf("Fitness    : %.6f%n", evaluator.evaluate(best));
    }
}
