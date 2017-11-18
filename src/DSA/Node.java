package DSA;

/**
 * Created by markzaharov on 15.11.17.
 */
public class Node implements Comparable<Node> {
    private int frequency;
    private byte b;
    private Node left;
    private Node right;

    public Node(byte b, int frequency, Node left, Node right) {
        this.frequency = frequency;
        this.b = b;
        this.left = left;
        this.right = right;
    }

    public int getFrequency() {
        return frequency;
    }

    public byte getByte() {
        return b;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public int compareTo(Node other) {
        return this.frequency - other.frequency;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }
}
