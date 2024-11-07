/**
 * @author K2425199
 */
class Doctor {
    private String name;
    private String specialization;

    public Doctor(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " - " + specialization;
    }
}
