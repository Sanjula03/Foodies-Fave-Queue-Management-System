package classes;

import java.util.Arrays;

public class FoodQueue {    // Class variables declaration
    private final Customer[] customers;
    private int front;
    private int rear;
    private final int capacity;
    private final int queueNumber;
    private final Customer[] waitingList;
    private int waitingListFront;
    private int waitingListRear;
    private int waitingListSize;

    public FoodQueue(int capacity, int queueNumber) {   // Initialize the queue and waiting list with the given capacity and queue number
        this.customers = new Customer[capacity];
        this.front = 0;
        this.rear = -1;
        this.capacity = capacity;
        this.queueNumber = queueNumber;
        waitingList = new Customer[capacity];
        waitingListFront = 0;
        waitingListRear = -1;
        waitingListSize = 0;
    }

    public boolean isEmpty() {
        return rear == -1;
    }

    public boolean isFull() {
        return rear == capacity - 1;
    }

    public int getSize() {
        if (isEmpty()) {
            return 0;
        }
        return rear - front + 1;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getQueueNumber() {
        return queueNumber;
    }

    public int getAvailableBurgers() {
        int occupiedBurgers = 0;
        for (int i = front; i <= rear; i++) {
            occupiedBurgers += customers[i].getBurgersRequired();
        }
        return capacity - occupiedBurgers;
    }

    public void addCustomer(Customer customer) {
        if (isFull()) {
            System.out.println("Queue " + queueNumber + " is full. Cannot add customer.");
            return;
        }

        rear++;
        customers[rear] = customer;
    }

    public boolean enqueue(Customer customer) { // Add a customer to the queue, returning true if successful
        if (isFull()) {
            return false;
        }

        rear++;
        customers[rear] = customer;
        return true;
    }

    public boolean dequeue() {  // Remove a customer from the queue, returning true if successful
        if (isEmpty()) {
            return false;
        }

        customers[front] = null;
        front++;
        return true;
    }

    public Customer[] getAllCustomers() {
        return Arrays.copyOfRange(customers, front, rear + 1);
    }

    public void clearQueue() {
        Arrays.fill(customers, null);
        front = 0;
        rear = -1;
    }

    public void addBurgersToStock() {
    }

    public int getTotalBurgersRequired() {
        return 0;
    }


    public boolean isWaitingListEmpty() {
        return waitingListSize == 0;
    }

    public boolean isWaitingListFull() {
        return waitingListSize == capacity;
    }

    public void addToWaitingList(Customer customer) {   // Add a customer to the waiting list
        if (isWaitingListFull()) {
            System.out.println("Waiting list is full. Cannot add customer.");
            return;
        }

        waitingListRear = (waitingListRear + 1) % capacity;
        waitingList[waitingListRear] = customer;
        waitingListSize++;
    }

    public Customer removeFromWaitingList() {   // Remove a customer from the waiting list, returning the removed customer
        if (isWaitingListEmpty()) {
            return null;
        }

        Customer removedCustomer = waitingList[waitingListFront];
        waitingList[waitingListFront] = null;
        waitingListFront = (waitingListFront + 1) % capacity;
        waitingListSize--;

        return removedCustomer;
    }
    public int getWaitingListSize() {
        return waitingListSize;
    }

}
