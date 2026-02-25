import java.util.List;

/**
 * Strategy interface for parent selection.
 * Open/Closed Principle: swap in tournament, rank, or other selection without touching GA logic.
 */
public interface SelectionStrategy {
    /**
     * Selects and returns the index of a parent from the population.
     *
     * @param population list of routes in the current generation
     * @param fitnesses  corresponding fitness scores
     * @return index of the selected parent
     */
    int select(List<Route> population, double[] fitnesses);
}
