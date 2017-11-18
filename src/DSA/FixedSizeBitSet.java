package DSA;

import java.util.BitSet;

/**
 * Created by markzaharov on 16.11.2017.
 */
public class FixedSizeBitSet {
    private BitSet bits = new BitSet();
    private int size;

    public FixedSizeBitSet(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public BitSet getBits() {
        return bits;
    }
}
