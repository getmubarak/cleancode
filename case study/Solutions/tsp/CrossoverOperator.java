/**
 * Strategy interface for crossover operators.
 * Open/Closed Principle: new crossover types (PMX, OX, etc.) can be added without modifying the GA.
 */
public interface CrossoverOperator {
    /**
     * Produces a child route from two parents.
     *
     * @param parentA   first parent route
     * @param parentB   second parent route
     * @param startCity the fixed start/end city
     * @return a new child Route
     */
    Route crossover(Route parentA, Route parentB, int startCity);
}
