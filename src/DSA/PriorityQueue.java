package DSA;

public class PriorityQueue {
    MinHeap heap;

    public PriorityQueue() {
        heap = new MinHeap();
    }

    public void enqueue(Node node) {
        heap.insert(node);
    }

    public Node dequeue() {
        return heap.delete();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int getSize() {
        return heap.getSize();
    }
}