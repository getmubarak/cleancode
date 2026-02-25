/**
 * Represents a city in the Traveling Salesman Problem.
 * Single Responsibility: holds city identity only.
 */
public class City {
    private final int id;

    public City(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
