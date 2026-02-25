import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the Genetic Algorithm.
 * Depends on abstractions (FitnessEvaluator, SelectionStrategy, etc.) — Dependency Inversion.
 * Single Responsibility: runs generations; delegates all sub-tasks to collaborators.
 */
public class GeneticAlgorithm {

    private final GeneticAlgorithmConfig config;
    private final PopulationInitializer  initializer;
    private final FitnessEvaluator       fitnessEvaluator;
    private final SelectionStrategy      selectionStrategy;
    private final CrossoverOperator      crossoverOperator;
    private final MutationOperator       mutationOperator;

    public GeneticAlgorithm(
            GeneticAlgorithmConfig config,
            PopulationInitializer  initializer,
            FitnessEvaluator       fitnessEvaluator,
            SelectionStrategy      selectionStrategy,
            CrossoverOperator      crossoverOperator,
            MutationOperator       mutationOperator) {
        this.config            = config;
        this.initializer       = initializer;
        this.fitnessEvaluator  = fitnessEvaluator;
        this.selectionStrategy = selectionStrategy;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator  = mutationOperator;
    }

    /**
     * Runs the full GA and returns the best route found.
     */
    public Route run() {
        List<Route> population = initializer.initialize(config.populationSize);

        Route bestRoute    = null;
        double bestFitness = -1;

        for (int gen = 0; gen < config.maxGenerations; gen++) {
            double[] fitnesses = evaluateFitnesses(population);

            // Track best
            for (int i = 0; i < population.size(); i++) {
                if (fitnesses[i] > bestFitness) {
                    bestFitness = fitnesses[i];
                    bestRoute   = population.get(i);
                }
            }

            printGenerationSummary(gen, population, fitnesses);

            if (gen < config.maxGenerations - 1) {
                population = evolve(population, fitnesses);
            }
        }

        return bestRoute;
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------

    private double[] evaluateFitnesses(List<Route> population) {
        double[] fitnesses = new double[population.size()];
        for (int i = 0; i < population.size(); i++) {
            fitnesses[i] = fitnessEvaluator.evaluate(population.get(i));
        }
        return fitnesses;
    }

    private List<Route> evolve(List<Route> old, double[] fitnesses) {
        List<Route> next = new ArrayList<>(config.populationSize);

        // Elitism: carry the best individual forward unchanged
        if (config.elitism) {
            next.add(bestIndividual(old, fitnesses));
        }

        while (next.size() < config.populationSize) {
            int idxA = selectionStrategy.select(old, fitnesses);
            int idxB;
            do { idxB = selectionStrategy.select(old, fitnesses); } while (idxB == idxA);

            Route child = crossoverOperator.crossover(
                    old.get(idxA), old.get(idxB), config.startCity);
            mutationOperator.mutate(child);
            next.add(child);
        }

        return next;
    }

    private Route bestIndividual(List<Route> population, double[] fitnesses) {
        int best = 0;
        for (int i = 1; i < fitnesses.length; i++) {
            if (fitnesses[i] > fitnesses[best]) best = i;
        }
        return population.get(best);
    }

    private void printGenerationSummary(int gen, List<Route> population, double[] fitnesses) {
        double best = -1;
        int bestIdx = 0;
        for (int i = 0; i < fitnesses.length; i++) {
            if (fitnesses[i] > best) { best = fitnesses[i]; bestIdx = i; }
        }
        System.out.printf("Gen %3d | Best: chromosome %d | Fitness: %.6f | Route: %s%n",
                gen, bestIdx, best, population.get(bestIdx));
    }
}
