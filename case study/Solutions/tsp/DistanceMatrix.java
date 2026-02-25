/**
 * Holds and provides distances between cities.
 * Single Responsibility: distance data access only.
 */
public class DistanceMatrix {
    private final int[][] matrix;
    private final int cityCount;

    public DistanceMatrix(int cityCount) {
        this.cityCount = cityCount;
        this.matrix = new int[cityCount + 1][cityCount + 1];
    }

    public void setDistance(int from, int to, int distance) {
        matrix[from][to] = distance;
        matrix[to][from] = distance; // symmetric
    }

    public int getDistance(int from, int to) {
        return matrix[from][to];
    }

    public int getCityCount() {
        return cityCount;
    }

    /**
     * Factory method for the default 5-city dataset.
     */
    public static DistanceMatrix createDefault() {
        DistanceMatrix dm = new DistanceMatrix(4);
        dm.setDistance(0, 1, 179);
        dm.setDistance(0, 2, 129);
        dm.setDistance(1, 2, 79);
        dm.setDistance(0, 3, 157);
        dm.setDistance(1, 3, 141);
        dm.setDistance(2, 3, 131);
        dm.setDistance(0, 4, 146);
        dm.setDistance(1, 4, 33);
        dm.setDistance(2, 4, 43);
        dm.setDistance(3, 4, 116);
        return dm;
    }
}
