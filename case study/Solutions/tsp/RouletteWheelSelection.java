import java.util.List;
import java.util.Random;

/**
 * Fitness-proportional (roulette wheel) parent selection.
 * Single Responsibility: implements one selection algorithm only.
 */
public class RouletteWheelSelection implements SelectionStrategy {
    private final Random random;

    public RouletteWheelSelection(Random random) {
        this.random = random;
    }

    @Override
    public int select(List<Route> population, double[] fitnesses) {
        double totalFitness = 0;
        for (double f : fitnesses) totalFitness += f;

        double spin = random.nextDouble() * totalFitness;
        double cumulative = 0;

        for (int i = 0; i < population.size(); i++) {
            cumulative += fitnesses[i];
            if (spin <= cumulative) return i;
        }

        return population.size() - 1; // fallback
    }
}
