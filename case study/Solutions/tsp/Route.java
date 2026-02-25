import java.util.Arrays;

/**
 * Represents a single route (chromosome) — an ordered tour of all cities.
 * Single Responsibility: encapsulates one solution candidate.
 */
public class Route {
    private final int[] genes; // genes[0] = genes[last] = startCity

    public Route(int[] genes) {
        this.genes = Arrays.copyOf(genes, genes.length);
    }

    public int getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, int value) {
        genes[index] = value;
    }

    public int length() {
        return genes.length;
    }

    public int[] getGenes() {
        return Arrays.copyOf(genes, genes.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int g : genes) sb.append(g).append(" ");
        return sb.toString().trim();
    }
}
