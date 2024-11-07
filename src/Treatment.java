/**
 * @author K2425199
 */
class Treatment {
    private String name;
    private double cost;

    public Treatment(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return name + " - Rs. " + cost;
    }
}
