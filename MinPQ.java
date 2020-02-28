import java.util.Random;

public class MinPQ<Key extends Comparable<Key>> {
    public static final int DEFAULT_CAPAC = 10;

    private int count;
    private Key[] items;

    public MinPQ() {
        this(DEFAULT_CAPAC);
    }

    public MinPQ(int capacity) {
        count = 0;
        items = (Key[]) new Comparable[capacity + 1];
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public void insert(Key newItem) {
        count++;
        items[count] = newItem;
        swim(count);
        if (count >= items.length - 1)
            resize(items.length * 2);
    }

    public Key remove() {
        Key ans = items[1];
        swap(1, count);
        count--;
        items[count + 1] = null;
        sink(1);

        if (count <= items.length / 4 && items.length / 2 > DEFAULT_CAPAC) 
            resize(items.length / 2);
        else if (items.length / 2 < DEFAULT_CAPAC)
            resize(DEFAULT_CAPAC);

        return ans;
    }

    private void resize(int newSize) {
        Key[] temp = (Key[]) new Comparable[newSize];
        for (int i = 1; i <= count; i++) {
            temp[i] = items[i];
        }

        items = temp;
    }

    private void swim(int index) {
        int k = index;
        while (k > 1 &&  less(items[k], items[k / 2])) {
            swap(k, k / 2);
            k = k / 2;
        }
    }

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

    private boolean less(Key a, Key b) {
        return a.compareTo(b) < 0;
    }

    private void swap(int j, int k) {
        Key temp = items[j];
        items[j] = items[k];
        items[k] = temp;
    }

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