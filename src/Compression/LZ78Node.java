package Compression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing the Nodes for the Prefix Tree for Lempel-Ziv 78 algorithm.
 */

class LZ78Node {
    // for the current node children
    private Map<Integer, LZ78Node> map = new HashMap<>();
    private int bit;
    private LZ78Node parent;
    private long nodeNum = 0;

    /**
     * Creates a node with the stated parent node, bit and number.
     *
     * @param bit     the given bit
     * @param parent  the given parent
     * @param nodeNum the given number
     */
    private LZ78Node(LZ78Node parent, int bit, int nodeNum) {
        this.parent = parent;
        this.bit = bit;
        this.nodeNum = nodeNum;
    }

    /**
     * Creates a node with the stated number.
     *
     * @param num the given number
     */
    LZ78Node(int num) {
        this.parent = null;
        this.nodeNum = num;
    }

    /**
     * @return the sequence of bits that are situated on the path
     * from the root node to the current one.
     */
    ArrayList<Integer> getBits() {
        ArrayList<Integer> bits = new ArrayList<>();
        LZ78Node curLZ78Node = this;
        while (curLZ78Node.parent != null) {
            bits.add(0, curLZ78Node.getBit());
            curLZ78Node = curLZ78Node.parent;
        }
        return bits;
    }

    /**
     * Adds a child with stated bit and number to the current node
     *
     * @param b   the given bit
     * @param num the given number
     */
    void addChild(int b, int num) {
        map.put(b, new LZ78Node(this, b, num));
    }

    /**
     * Returns true if the current node has a child with the stated bit.
     * @param b the given bit
     */
    boolean hasChild(int b) {
        return map.containsKey(b);
    }

    long getNum() {
        return this.nodeNum;
    }

    private int getBit() {
        return this.bit;
    }

    LZ78Node getChild(int b) {
        return map.get(b);
    }

}