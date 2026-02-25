import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Uniform crossover: each gene is chosen from parent A or B with probability crossoverRate.
 * Duplicate genes are replaced with a random unvisited city.
 * Single Responsibility: implements one crossover algorithm only.
 */
public class UniformCrossover implements CrossoverOperator {
    private final double crossoverRate;
    private final int totalCities;
    private final Random random;

    public UniformCrossover(double crossoverRate, int totalCities, Random random) {
        this.crossoverRate = crossoverRate;
        this.totalCities = totalCities;
        this.random = random;
    }

    @Override
    public Route crossover(Route parentA, Route parentB, int startCity) {
        int[] childGenes = new int[totalCities + 2];
        childGenes[0] = startCity;
        childGenes[totalCities + 1] = startCity;

        Set<Integer> used = new HashSet<>();
        used.add(startCity);

        for (int gene = 1; gene <= totalCities; gene++) {
            int candidate = (random.nextDouble() > crossoverRate)
                    ? parentA.getGene(gene)
                    : parentB.getGene(gene);

            // Resolve duplicates
            while (used.contains(candidate)) {
                candidate = random.nextInt(totalCities + 1);
            }

            childGenes[gene] = candidate;
            used.add(candidate);
        }

        return new Route(childGenes);
    }
}
