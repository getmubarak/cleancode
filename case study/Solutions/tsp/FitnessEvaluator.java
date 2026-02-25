/**
 * Strategy interface for fitness evaluation.
 * Open/Closed Principle: new fitness strategies can be added without modifying existing code.
 * Dependency Inversion: high-level GA depends on this abstraction, not a concrete evaluator.
 */
public interface FitnessEvaluator {
    /**
     * Returns a fitness score for the given route.
     * Higher is better.
     */
    double evaluate(Route route);
}
