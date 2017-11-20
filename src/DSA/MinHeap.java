package DSA;

import java.util.Arrays;

class MinHeap {

    private Node[] heap;
    private int size;

    public MinHeap() {
        heap = new Node[16];
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Inserts process at the end of the heap and puts it into right place.
    public void insert(Node node) {
        if (heap.length == size) {
            heap = Arrays.copyOf(heap, heap.length * 2);
        }
        heap[size] = node;
        int parentIndex = (size - 1) / 2;
        int nodeIndex = size;
        while (heap[nodeIndex].compareTo(heap[parentIndex]) < 0 && parentIndex >= 0) {
            swap(parentIndex, nodeIndex);
            parentIndex = (parentIndex - 1) / 2;
            nodeIndex = (nodeIndex - 1) / 2;
        }
        size++;
    }

    private int leftChildIndex(int parentIndex) {
        return parentIndex * 2 + 1;
    }

    private int rightChildIndex(int parentIndex) {
        return parentIndex * 2 + 2;
    }

    // Method for balancing the heap.
    private void minHeapify(int pos) {
        int left = leftChildIndex(pos);
        int right = rightChildIndex(pos);
        int lowest = pos;

        if(left < size && heap[left].compareTo(heap[lowest]) < 0)
            lowest = left;
        if(right < size && heap[right].compareTo(heap[lowest]) < 0)
            lowest = right;
        if(lowest != pos) {
            Node temp = heap[pos];
            heap[pos] = heap[lowest];
            heap[lowest] = temp;
            minHeapify(lowest);
        }
    }

    // Deletes and returns topmost element in the heap and rebalances it using minHeapify method.
    public Node delete() {
        Node top = heap[0];
        heap[0] = heap[size - 1];
        size--;
        minHeapify(0);
        return top;
    }

    private void swap(int fPos,int sPos) {
        Node temp;
        temp = heap[fPos];
        heap[fPos] = heap[sPos];
        heap[sPos] = temp;
    }

    public int getSize() {
        return size;
    }
}