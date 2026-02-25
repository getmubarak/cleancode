import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Generates an initial random population of routes.
 * Single Responsibility: handles only population initialization logic.
 */
public class PopulationInitializer {
    private final int totalCities;
    private final int startCity;
    private final Random random;

    public PopulationInitializer(int totalCities, int startCity, Random random) {
        this.totalCities = totalCities;
        this.startCity = startCity;
        this.random = random;
    }

    public List<Route> initialize(int populationSize) {
        List<Route> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            population.add(createRandomRoute());
        }
        return population;
    }

    private Route createRandomRoute() {
        int[] genes = new int[totalCities + 2];
        genes[0] = startCity;
        genes[totalCities + 1] = startCity;

        Set<Integer> visited = new HashSet<>();
        visited.add(startCity);

        for (int gene = 1; gene <= totalCities; gene++) {
            int city;
            do {
                city = random.nextInt(totalCities + 1);
            } while (visited.contains(city));
            genes[gene] = city;
            visited.add(city);
        }

        return new Route(genes);
    }
}
