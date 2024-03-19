package classes;

public class Customer { // Customer's attributes declaration
    private final String firstName;
    private final String lastName;
    private final int burgersRequired;

    public Customer(String firstName, String lastName, int burgersRequired) {   // Initialize the customer with the provided attributes
        this.firstName = firstName;
        this.lastName = lastName;
        this.burgersRequired = burgersRequired;
    }

    public String getLastName() {
        return lastName;
    }

    public int getBurgersRequired() {
        return burgersRequired;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " - Burgers: " + burgersRequired;
    }
}
