/**
 *  Author:         Khoa Nam Pham
 *  Date modified:  02/03/2020
 *  Purpose:        Implementation of the Minimum Priority Queue Abstract Data Type.
 *                  This class provides useful method to retrieve the minimum item in 
 *                  a collection of objects with O(logN) time complexity.
 *                  The client code does not need to care about the capacity since it 
 *                  can grow or shrink. 
 */

import java.util.Random;

public class MinPQ<Key extends Comparable<Key>> {
    public static final int DEFAULT_CAPAC = 10;  // Default capacity of the queue

    private int count;  // The current number of items in the queue 
    private Key[] items;  // All the items stored in the queue so far

    /**
     *  Initialize a queue with the DEFAULT_CAPACITY which is 10.
     */
    public MinPQ() {
        this(DEFAULT_CAPAC);
    }

    /**
     *  Initialize a queue with the given capacity
     * 
     *  @param capacity The desired capacity of the queue
     *  @throws IllegalArgumentException when the capacity is negative
     */
    public MinPQ(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("Capacity cannot be negative");
        
            count = 0;
        items = (Key[]) new Comparable[capacity + 1];
    }

    /**
     * Returns the size of the queue
     * @return The current number of items stored in the queue
     */
    public int size() {
        return count;
    }

    /**
     * Checks if the queue is empty or not.
     * @return true if the queue is empty or false otherwise.
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Insert a new item to the queue
     * @param newItem The new item to be inserted
     * @throws IllegalArgumentException when the given item is null
     */
    public void insert(Key newItem) {
        if (newItem == null)
            throw new IllegalArgumentException("Calling insert() with a null object");

        count++;
        items[count] = newItem;
        swim(count);
        if (count >= items.length - 1)
            resize(items.length * 2);
    }

    /**
     * Remove the item on the top (the smallest item) of the queue
     * 
     * @return the smallest item
     */
    public Key remove() {
        Key ans = items[1];
        swap(1, count);
        count--;
        items[count + 1] = null;
        sink(1);

        // Resize the queue to fit the number of items
        if (count <= items.length / 4 && items.length / 2 > DEFAULT_CAPAC) 
            resize(items.length / 2);
        else if (items.length / 2 < DEFAULT_CAPAC)
            resize(DEFAULT_CAPAC);

        return ans;
    }

    /**
     * Resize the queue to a new size.
     * @param newSize The new size of the queue
     */
    private void resize(int newSize) {
        Key[] temp = (Key[]) new Comparable[newSize];
        for (int i = 1; i <= count; i++) {
            temp[i] = items[i];
        }

        items = temp;
    }

    /**
     * Bring an item up the queue until it stays in the correct position
     * @param index The starting index
     */
    private void swim(int index) {
        int k = index;
        while (k > 1 &&  less(items[k], items[k / 2])) {
            swap(k, k / 2);
            k = k / 2;
        }
    }

    /**
     * Bring an item down the queue until it stays in the correct position 
     * @param index The starting index
     */
    private void sink(int index) {
        int k = index;
        while (2 * k <= count) {
            int j = 2 * k;
            if (j < count && less(items[j + 1], items[j])) 
                j++;

            if (less(items[j], items[k])) {
                swap(j, k);
                k = j;
            }
            else break;
        }
    }

    /**
     * Checks if item a is less than item b or not 
     * @param a Item a
     * @param b Item b
     * @return true if a is less than b otherwise false
     */
    private boolean less(Key a, Key b) {
        return a.compareTo(b) < 0;
    }

    /**
     * Swap 2 items in the queue at the given positions
     * @param j the position of the first item
     * @param k the position of the second item
     */
    private void swap(int j, int k) {
        Key temp = items[j];
        items[j] = items[k];
        items[k] = temp;
    }

    // Test client
    public static void main(String[] args) {
        Random rand = new Random();
        MinPQ<Integer> pq = new MinPQ<>();

        System.out.println("Inserting 20 random integers:");
        for (int i = 0; i < 20; i++) {
            int j = rand.nextInt(100);
            System.out.println("Inserting: " + j);
            pq.insert(j);
        }

        System.out.println("Keep popping out from the queue: ");
        while (!pq.isEmpty()) {
            System.out.print(pq.remove() + " ");
        }
        System.out.println();
    }
}