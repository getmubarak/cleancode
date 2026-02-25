/**
 * Strategy interface for mutation operators.
 * Open/Closed Principle: swap, inversion, scramble mutations can be plugged in freely.
 */
public interface MutationOperator {
    /**
     * Optionally mutates the given route in-place.
     *
     * @param route the route to (possibly) mutate
     */
    void mutate(Route route);
}
