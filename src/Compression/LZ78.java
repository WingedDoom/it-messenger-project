package Compression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for Lempel-Ziv compression and decompression algorithms.
 */

public class LZ78 implements Compressor, Decompressor {

    // Some necessary constant values for LZ78.
    private static final int BITS_IN_BYTE = 8;
    private static final int BYTES_OFFSET = 128;

    /**
     * Performs Lempel-Ziv compression.
     *
     * @param toBeCompressed the bytes sequence to be compressed.
     * @return the obtained compressed bytes sequence.
     */
    @Override
    public byte[] compress(final byte[] toBeCompressed) {
        int nodesAmount = 0;
        ArrayList<Byte> compressedBytes = new ArrayList<>();
        final long bitsAmount = toBeCompressed.length * BITS_IN_BYTE;
        long position = 0;
        LZ78Node root = new LZ78Node(nodesAmount++);
        ArrayList<Integer> compressedBits = new ArrayList<>();

        while (position < bitsAmount) {
            LZ78Node curLZ78Node = root;
            if (position == 0) {
                // First bit linking with the root
                compressedBits.add(getBit(toBeCompressed, position));
                curLZ78Node.addChild(getBit(toBeCompressed, position), nodesAmount++);
                position++;
            }
            // Looking for the longest bits sequence stored in the dictionary
            while (position < bitsAmount && curLZ78Node.hasChild(getBit(toBeCompressed, position))) {
                curLZ78Node = curLZ78Node.getChild(getBit(toBeCompressed, position));
                position++;
            }
            ArrayList<Integer> curNodeNumAsBits = intToBits(curLZ78Node.getNum());
            int numLen = (int) Math.ceil(Math.log(nodesAmount) / Math.log(2));

            while (curNodeNumAsBits.size() < numLen)
                curNodeNumAsBits.add(0, 0);

            // Adding the current word number (as bits) to compressedBits
            compressedBits.addAll(curNodeNumAsBits);
            if (position < bitsAmount) {
                compressedBits.add(getBit(toBeCompressed, position));
                curLZ78Node.addChild(getBit(toBeCompressed, position), nodesAmount++);
            }

            while (compressedBits.size() >= BITS_IN_BYTE) {
                // Adding the bytes that are full to compressedBytes
                compressedBytes.add(bitsToByte(compressedBits));
                for (int i = 0; i < BITS_IN_BYTE; i++)
                    // Removing the bytes that are full from compressedBits
                    compressedBits.remove(0);
            }

            position++;
        }

        int uselessBitsAmount = 0;

        if (compressedBits.size() > 0) {
            while (compressedBits.size() < BITS_IN_BYTE) {
                compressedBits.add(0);
                uselessBitsAmount++;
            }
            // Adding the last bits to compressedBytes
            compressedBytes.add(bitsToByte(compressedBits));
        }
        compressedBytes.add(0, (byte) (uselessBitsAmount - (BYTES_OFFSET - 1)));

        // ArrayList of bytes converting to the output bytes array
        byte[] compressed = new byte[compressedBytes.size()];
        for (int i = 0; i < compressedBytes.size(); i++)
            compressed[i] = compressedBytes.get(i);
        return compressed;
    }

    /**
     * Performs Lempel-Ziv decompression.
     *
     * @param toBeDecompressed the bytes sequence to be decompressed.
     * @return the obtainde decompressed bytes sequence.
     */
    @Override
    public byte[] decompress(byte[] toBeDecompressed) {
        int nodesAmount = 0;
        LZ78Node root = new LZ78Node(nodesAmount++);
        Map<Long, LZ78Node> numToNode = new HashMap<>();
        numToNode.put(root.getNum(), root);
        ArrayList<Integer> decodedBits = new ArrayList<>();
        ArrayList<Byte> decodedBytes = new ArrayList<>();
        // Beginning with 8th position, since the first 8 bits are the numbers of useless bits at the end
        long position = BITS_IN_BYTE;
        long bitsAmount = BITS_IN_BYTE * toBeDecompressed.length;

        int uselessBitsAmount = toBeDecompressed[0] + BYTES_OFFSET;

        while (position < bitsAmount - uselessBitsAmount) {
            // For k words in dictionary, the maximum bits for each word number coding is log2(k)
            int numLen = (int) Math.ceil(Math.log(nodesAmount) / Math.log(2));
            ArrayList<Integer> numAsBits = new ArrayList<>();
            for (int i = 0; i < numLen; i++) {
                numAsBits.add(getBit(toBeDecompressed, position));
                position++;
            }
            long nodeNum = bitsToInt(numAsBits);
            LZ78Node curLZ78Node = numToNode.get(nodeNum);
            decodedBits.addAll(curLZ78Node.getBits());
            if (position < bitsAmount - uselessBitsAmount) {
                decodedBits.add(getBit(toBeDecompressed, position));
                curLZ78Node.addChild(getBit(toBeDecompressed, position), nodesAmount++);
                curLZ78Node = curLZ78Node.getChild(getBit(toBeDecompressed, position));
                numToNode.put(curLZ78Node.getNum(), curLZ78Node);
            }

            while (decodedBits.size() >= BITS_IN_BYTE) {
                // Adding the bytes that are full to compressedBytes
                decodedBytes.add(bitsToByte(decodedBits));
                for (int i = 0; i < BITS_IN_BYTE; i++)
                    // Removing the bytes that are full from compressedBits
                    decodedBits.remove(0);
            }

            position++;
        }

        // ArrayList of bytes converting to the output bytes array
        byte[] decoded = new byte[decodedBytes.size()];
        for (int i = 0; i < decodedBytes.size(); i++)
            decoded[i] = decodedBytes.get(i);

        return decoded;
    }

    /**
     * Returns the required bit from the byte array.
     *
     * @param byteArray the given byte array
     * @param position  the bit position
     */
    private int getBit(byte[] byteArray, long position) {
        int value = byteArray[(int) (position / BITS_IN_BYTE)] + BYTES_OFFSET;
        return (value >> (BITS_IN_BYTE - 1 - position % BITS_IN_BYTE)) & 1;
    }

    /**
     * Converts the bits sequence to byte.
     *
     * @param bits the given bits sequence
     * @return the obtained byte value
     */
    private byte bitsToByte(ArrayList<Integer> bits) {
        int value = 0;
        for (int i = 0; i < BITS_IN_BYTE; i++)
            value |= bits.get(i) << (BITS_IN_BYTE - 1 - i);
        return (byte) (value - BYTES_OFFSET);
    }

    /**
     * Converts integer value to bits sequence.
     *
     * @param value the given integer value
     * @return the obtained bits
     */
    private ArrayList<Integer> intToBits(long value) {
        ArrayList<Integer> bits = new ArrayList<>();
        do {
            bits.add(0, (int) (value & 1));
            value = value >> 1;
        } while (value > 0);
        return bits;
    }

    /**
     * Converts the bits sequence to integer value.
     *
     * @param bits the given bits sequence
     */
    private long bitsToInt(ArrayList<Integer> bits) {
        long value = 0;
        for (int i = 0; i < bits.size(); i++)
            value += (bits.get(i) << (bits.size() - i - 1));
        return value;
    }
}