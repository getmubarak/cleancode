/**
 * Immutable configuration for the Genetic Algorithm.
 * Single Responsibility: holds GA parameters only.
 * Using a config object avoids long parameter lists (ISP / clean API).
 */
public class GeneticAlgorithmConfig {
    public final int maxGenerations;
    public final int populationSize;
    public final double mutationRate;
    public final double crossoverRate;
    public final boolean elitism;
    public final int startCity;

    private GeneticAlgorithmConfig(Builder b) {
        this.maxGenerations = b.maxGenerations;
        this.populationSize = b.populationSize;
        this.mutationRate   = b.mutationRate;
        this.crossoverRate  = b.crossoverRate;
        this.elitism        = b.elitism;
        this.startCity      = b.startCity;
    }

    public static class Builder {
        private int    maxGenerations = 100;
        private int    populationSize = 50;
        private double mutationRate   = 0.5;
        private double crossoverRate  = 0.5;
        private boolean elitism       = true;
        private int    startCity      = 0;

        public Builder maxGenerations(int v) { maxGenerations = v; return this; }
        public Builder populationSize(int v) { populationSize = v; return this; }
        public Builder mutationRate(double v) { mutationRate = v; return this; }
        public Builder crossoverRate(double v) { crossoverRate = v; return this; }
        public Builder elitism(boolean v) { elitism = v; return this; }
        public Builder startCity(int v) { startCity = v; return this; }

        public GeneticAlgorithmConfig build() { return new GeneticAlgorithmConfig(this); }
    }
}
