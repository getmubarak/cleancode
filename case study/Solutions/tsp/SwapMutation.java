import java.util.Random;

/**
 * Swap mutation: with probability mutationRate, swaps two adjacent interior genes.
 * Single Responsibility: implements one mutation algorithm only.
 */
public class SwapMutation implements MutationOperator {
    private final double mutationRate;
    private final int totalCities;
    private final Random random;

    public SwapMutation(double mutationRate, int totalCities, Random random) {
        this.mutationRate = mutationRate;
        this.totalCities = totalCities;
        this.random = random;
    }

    @Override
    public void mutate(Route route) {
        if (random.nextDouble() > mutationRate) return;

        // Pick a random interior gene position (1 to totalCities-1) and swap with next
        int pos = 1 + random.nextInt(totalCities - 1);
        int tmp = route.getGene(pos);
        route.setGene(pos, route.getGene(pos + 1));
        route.setGene(pos + 1, tmp);
    }
}
