package classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {     // Constants and variables declaration
    private static final List<Integer> QUEUE_CAPACITIES = Arrays.asList(2, 3, 5);
    private static List<FoodQueue> queues;
    private static FoodQueue waitingList;
    private static final int BURGER_PRICE = 650;
    private static int stock = 50;

    public static void main(String[] args) {    // Initialization of queues, waiting list, and scanner
        queues = new ArrayList<>();
        queues.add(new FoodQueue(QUEUE_CAPACITIES.get(0), 1));
        queues.add(new FoodQueue(QUEUE_CAPACITIES.get(1), 2));
        queues.add(new FoodQueue(QUEUE_CAPACITIES.get(2), 3));
        waitingList = new FoodQueue(QUEUE_CAPACITIES.get(2), -1); // Use the same capacity as the food queues

        Scanner scanner = new Scanner(System.in);
        String option;

        do {    // Display the menu and get user's choice
            displayMenu();
            option = scanner.nextLine();

            switch (option) {   // Handle different menu options
                case "100", "VAQ" -> viewAllQueues();
                case "101", "VEQ" -> viewAllEmptyQueues();
                case "102", "ACQ" -> addCustomerToQueue(scanner);
                case "103", "RCQ" -> removeCustomerFromQueue(scanner);
                case "104", "RSC" -> removeServedCustomer(scanner);
                case "105", "VSO" -> viewCustomersSortedInAlphabeticalOrder();
                case "106", "SPD" -> storeProgramDataIntoFile();
                case "107", "LPD" -> loadProgramDataFromFile();
                case "108", "VRS" -> viewRemainingBurgersStock();
                case "109", "ABA" -> addBurgersToStock();
                case "110", "IFQ" -> printQueueIncome();
                case "111", "PWL" -> printWaitingList();
                case "999", "EXT" -> System.out.println("Exiting the program. Goodbye!");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (!option.equals("999") && !option.equals("EXT"));

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("*****************");
        System.out.println("*   Cashiers   *");
        System.out.println("*****************");
        System.out.println("O O O");
        System.out.println("O X X");
        System.out.println("X X");
        System.out.println("X");
        System.out.println("X");
        System.out.println("('X' - Not Occupied / 'O' - Occupied)");
        System.out.println("---------------------------------------------------------------");
        System.out.println("\n-Menu-");
        System.out.println("100 or VAQ: View all Queues");
        System.out.println("101 or VEQ: View all Empty Queues");
        System.out.println("102 or ACQ: Add customer to a Queue");
        System.out.println("103 or RCQ: Remove a customer from a Queue");
        System.out.println("104 or RSC: Remove a served customer");
        System.out.println("105 or VSO: View Customers Sorted in alphabetical order");
        System.out.println("106 or SPD: Store Program Data into file");
        System.out.println("107 or LPD: Load Program Data from file");
        System.out.println("108 or VRS: View Remaining burgers Stock");
        System.out.println("109 or ABA: Add burgers to Stock");
        System.out.println("110 or IFQ: Print Queue Income");
        System.out.println("111 or PWL: Print Waiting List");
        System.out.println("999 or EXT: Exit the Program");
        System.out.print("Enter your choice: ");
    }

    private static void viewAllQueues() {   // Display the state of all queues
        System.out.println("\n*****************");
        System.out.println("*   Cashiers   *");
        System.out.println("*****************");

        for (int i = 0; i < queues.size(); i++) {
            FoodQueue queue = queues.get(i);

            System.out.print("Queue " + (i + 1) + ": ");

            for (int j = 0; j < queue.getSize(); j++) {
                System.out.print("O ");
            }

            for (int j = queue.getSize(); j < queue.getCapacity(); j++) {
                System.out.print("X ");
            }

            System.out.println();
        }
    }

    private static void viewAllEmptyQueues() {  // Display the status of each queue (empty or occupied)
        boolean allQueuesEmpty = true;

        System.out.println("\n*****************");
        System.out.println("*   Cashiers   *");
        System.out.println("*****************");

        for (int i = 0; i < queues.size(); i++) {
            FoodQueue queue = queues.get(i);
            String status = queue.isEmpty() ? "Empty" : queue.getSize() + "/" + queue.getCapacity() + " customers";

            System.out.println("Queue " + (i + 1) + ": " + status);

            if (!queue.isEmpty()) {
                allQueuesEmpty = false;
            }
        }

        if (allQueuesEmpty) {
            System.out.println("\nAll queues are empty.");
        } else {
            System.out.println("\nQueues are not all empty.");
        }
    }

    private static void addCustomerToQueue(Scanner scanner) {   // Prompt for customer's information and add them to a queue or waiting list
        System.out.print("Enter the customer's first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter the customer's last name: ");
        String lastName = scanner.nextLine();

        int burgersRequired;
        while (true) {
            System.out.print("Enter the number of burgers required: ");
            String input = scanner.nextLine();
            try {
                burgersRequired = Integer.parseInt(input);
                if (burgersRequired <= stock && burgersRequired > 0) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a value between 1 and " + stock + " for the number of burgers.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer value for the number of burgers.");
            }
        }

        // Check if there are enough burgers in stock
        int totalBurgersRequired = getTotalBurgersRequired();
        int remainingStock = stock - totalBurgersRequired;

        if (remainingStock >= 0) {
            stock = remainingStock;

            // Find the queue with the minimum length
            int minIndex = 0;
            int minLength = queues.get(0).getSize();
            for (int i = 1; i < queues.size(); i++) {
                int currentLength = queues.get(i).getSize();
                if (currentLength < minLength) {
                    minIndex = i;
                    minLength = currentLength;
                }
            }

            Customer customer = new Customer(firstName, lastName, burgersRequired);
            boolean added = queues.get(minIndex).enqueue(customer);
            if (added) {
                System.out.println("Customer added to Queue " + (minIndex + 1) + " successfully.");
            } else {
                boolean addedToOtherQueue = false;
                for (int i = 0; i < queues.size(); i++) {
                    if (i != minIndex && queues.get(i).enqueue(customer)) {
                        System.out.println("Customer added to Queue " + (i + 1) + " successfully.");
                        addedToOtherQueue = true;
                        break;
                    }
                }

                if (!addedToOtherQueue) {
                    waitingList.addToWaitingList(customer);
                    System.out.println("Customer added to the waiting list.");
                }
            }
        } else {
            System.out.println("Insufficient burgers in stock. Unable to add customer to a queue.");
        }
    }

    private static int getTotalBurgersRequired() {
        int totalBurgersRequired = 0;
        for (FoodQueue queue : queues) {
            totalBurgersRequired += queue.getTotalBurgersRequired();
        }
        return totalBurgersRequired;
    }

    private static void removeCustomerFromQueue(Scanner scanner) {  // Prompt for queue number and customer index, and remove the customer if valid
        System.out.print("Enter queue number (1, 2, or 3): ");
        if (scanner.hasNextInt()) {
            int queueNumber = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            if (queueNumber >= 1 && queueNumber <= queues.size()) {
                int queueIndex = queueNumber - 1;
                FoodQueue queue = queues.get(queueIndex);

                if (!queue.isEmpty()) {
                    System.out.print("Enter customer index (starting from 1): ");
                    if (scanner.hasNextInt()) {
                        int customerIndex = scanner.nextInt();
                        scanner.nextLine();

                        if (customerIndex >= 1 && customerIndex <= queue.getSize()) {
                            boolean removedCustomer = queue.dequeue();
                            System.out.println("Customer removed from queue " + queueNumber + ": " + removedCustomer);
                            moveCustomerFromWaitingList(); // Move a customer from the waiting list to the queues
                        } else {
                            System.out.println("Invalid customer index.");
                        }
                    } else {
                        System.out.println("Invalid customer index. Please enter a valid integer value.");
                        scanner.nextLine(); // Consume invalid input
                    }
                } else {
                    System.out.println("Queue " + queueNumber + " is empty.");
                }
            } else {
                System.out.println("Invalid queue number.");
            }
        } else {
            System.out.println("Invalid queue number. Please enter a valid integer value.");
            scanner.nextLine();
        }
    }

    private static void removeServedCustomer(Scanner scanner) { // Prompt for queue number and remove the next served customer if valid
        System.out.print("Enter queue number (1, 2, or 3): ");
        if (scanner.hasNextInt()) {
            int queueNumber = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            if (queueNumber >= 1 && queueNumber <= queues.size()) {
                int queueIndex = queueNumber - 1;
                FoodQueue queue = queues.get(queueIndex);

                if (!queue.isEmpty()) {
                    boolean removedCustomer = queue.dequeue();
                    System.out.println("Served customer removed from queue " + queueNumber + ": " + removedCustomer);
                    moveCustomerFromWaitingList(); // Move a customer from the waiting list to the queues
                } else {
                    System.out.println("Queue " + queueNumber + " is empty.");
                }
            } else {
                System.out.println("Invalid queue number.");
            }
        } else {
            System.out.println("Invalid queue number. Please enter a valid integer value.");
            scanner.nextLine();
        }
    }

    private static void moveCustomerFromWaitingList() {  // Move a customer from the waiting list to an appropriate queue
        for (FoodQueue queue : queues) {
            if (!waitingList.isWaitingListEmpty() && !queue.isFull()) {
                Customer nextCustomer = waitingList.removeFromWaitingList();
                boolean added = queue.enqueue(nextCustomer);
                if (added) {
                    System.out.println("Next customer from the waiting list added to Queue " + queue.getQueueNumber() + ".");
                } else {
                    System.out.println("Failed to add next customer from the waiting list. Queue " + queue.getQueueNumber() + " is full.");
                    waitingList.addToWaitingList(nextCustomer);
                }
            }
        }
    }

    private static void viewCustomersSortedInAlphabeticalOrder() {
        // Check if there are any customers in the queues
        boolean hasCustomers = false;
        for (FoodQueue queue : queues) {
            if (queue.getSize() > 0) {
                hasCustomers = true;
                break;
            }
        }

        if (!hasCustomers) {
            System.out.println("There are no customers in the queues.");
            return;
        }

        // Collect all customers from the queues, sort them by last name, and display the sorted list
        List<Customer> allCustomers = new ArrayList<>();
        for (FoodQueue queue : queues) {
            Customer[] queueCustomers = queue.getAllCustomers();
            allCustomers.addAll(Arrays.asList(queueCustomers));
        }

        bubbleSortCustomersByLastName(allCustomers);

        System.out.println("Customers sorted in alphabetical order:");
        for (Customer customer : allCustomers) {
            System.out.println(customer);
        }
    }

    private static void bubbleSortCustomersByLastName(List<Customer> customers) {   // Implement bubble sort to sort customers by last name
        int n = customers.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                String lastName1 = customers.get(j).getLastName();
                String lastName2 = customers.get(j + 1).getLastName();
                if (lastName1.compareTo(lastName2) > 0) {
                    Customer temp = customers.get(j);
                    customers.set(j, customers.get(j + 1));
                    customers.set(j + 1, temp);
                }
            }
        }
    }

    private static void storeProgramDataIntoFile() {    // Write the output of option 100 (viewAllQueues) to the file
        try {
            FileWriter writer = new FileWriter("Customer_Data.txt");

            // Write the output of option 100 (viewAllQueues) to the file
            writer.write(getAllQueuesOutput());

            writer.close();
            System.out.println("Program data stored in the file successfully.");
        } catch (IOException e) {
            System.out.println("Error occurred while storing program data.");
        }
    }

    private static String getAllQueuesOutput() {    // Generate the output string for viewAllQueues method
        StringBuilder output = new StringBuilder();

        output.append("*****************\n");
        output.append("*   Cashiers   *\n");
        output.append("*****************\n");
        for (FoodQueue queue : queues) {
            output.append("Cashier ").append(queue.getQueueNumber()).append(": ");
            output.append("O ".repeat(Math.max(0, queue.getSize())));
            output.append("X ".repeat(Math.max(0, queue.getCapacity() - queue.getSize())));
            output.append("\n");
        }

        return output.toString();
    }

    private static void loadProgramDataFromFile() { // Read the program data from the file and populate queues and stock
        try (BufferedReader reader = new BufferedReader(new FileReader("Customer_Data.txt"))) {
            // Skip the header lines
            reader.readLine();
            reader.readLine();
            reader.readLine();

            for (FoodQueue queue : queues) {
                queue.clearQueue(); // Clear the existing queue
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        break; // End of queue
                    }
                    String[] parts = line.split(", ");
                    if (parts.length == 2) {  // Check if the line contains both first name and burgers required
                        String[] names = parts[0].split(" ");
                        String firstName = names[0];
                        String lastName = names[1];
                        int burgersRequired = Integer.parseInt(parts[1]);

                        Customer customer = new Customer(firstName, lastName, burgersRequired);
                        queue.addCustomer(customer);
                    } else {
                        System.out.println(line);
                    }
                }
            }

            // Read the remaining burgers stock
            String line = reader.readLine();
            if (line != null && line.startsWith("Remaining burgers stock:")) {
                String[] tokens = line.split(":");
                if (tokens.length == 2) {
                    stock = Integer.parseInt(tokens[1].trim());
                }
            }

            System.out.println("Program data loaded from file successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Program data file not found. Loading data failed.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
    }

    private static void viewRemainingBurgersStock() {   // Calculate and display the remaining burgers in stock
        int totalBurgersStock = stock;
        for (FoodQueue queue : queues) {
            totalBurgersStock -= (queue.getCapacity() - queue.getAvailableBurgers());
        }

        System.out.println("Remaining burgers stock: " + totalBurgersStock);
    }

    private static void printQueueIncome() {    // Calculate and print the income for each queue
        for (int i = 0; i < queues.size(); i++) {
            int queueIncome = queues.get(i).getSize() * BURGER_PRICE;
            System.out.println("Queue " + (i + 1) + " income: " + queueIncome);
        }
    }

    private static void addBurgersToStock() {   // Prompt for the number of burgers to add, adjust the stock, and check if waiting list customers can be added
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of burgers to add: ");
        int burgersToAdd = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        stock += burgersToAdd;
        for (FoodQueue queue : queues) {
            queue.addBurgersToStock();
        }

        System.out.println(burgersToAdd + " burgers added to stock.");

        // Check if any customers in the waiting list can be added to the queues
        for (FoodQueue queue : queues) {
            while (!waitingList.isWaitingListEmpty() && !queue.isFull()) {
                Customer nextCustomer = waitingList.removeFromWaitingList();
                boolean added = queue.enqueue(nextCustomer);
                if (added) {
                    System.out.println("Next customer from the waiting list added to queue " + queue.getQueueNumber() + ".");
                } else {
                    System.out.println("Failed to add next customer from the waiting list. Queue " + queue.getQueueNumber() + " is full.");
                    waitingList.addToWaitingList(nextCustomer);
                }
            }
        }
    }

    private static void printWaitingList() {    // Print the number of customers in the waiting list and their details
        int waitingListSize;
        waitingListSize = waitingList.getWaitingListSize();
        if (waitingListSize == 0) {
            System.out.println("The waiting list is empty.");
        } else {
            System.out.println("Number of customers in the waiting list: " + waitingListSize);
            Customer[] customers = waitingList.getAllCustomers();
            for (Customer customer : customers) {
                System.out.println(customer);
            }
        }
    }

}
