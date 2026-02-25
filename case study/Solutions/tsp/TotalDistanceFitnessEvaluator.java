/**
 * Fitness = 1 / totalDistance.
 * Single Responsibility: computes tour fitness from a distance matrix.
 */
public class TotalDistanceFitnessEvaluator implements FitnessEvaluator {
    private final DistanceMatrix distanceMatrix;

    public TotalDistanceFitnessEvaluator(DistanceMatrix distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    @Override
    public double evaluate(Route route) {
        double totalDist = 0;
        for (int i = 0; i < route.length() - 1; i++) {
            totalDist += distanceMatrix.getDistance(route.getGene(i), route.getGene(i + 1));
        }
        return (totalDist == 0) ? 0 : 1.0 / totalDist;
    }

    public double totalDistance(Route route) {
        double totalDist = 0;
        for (int i = 0; i < route.length() - 1; i++) {
            totalDist += distanceMatrix.getDistance(route.getGene(i), route.getGene(i + 1));
        }
        return totalDist;
    }
}
