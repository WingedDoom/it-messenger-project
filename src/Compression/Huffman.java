package Compression;

import DSA.*;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by markzaharov on 15.11.17.
 */
public class Huffman implements Compressor, Decompressor {

    public byte[] compress(byte[] byteArray) {
        int[] frequencies = new int[256];
        for (byte b : byteArray) {
            frequencies[b + 128]++;
        }
        Node root = constructTree(frequencies);
        String[] code = new String[256];
        buildCode(code, root, "");
        FixedSizeBitSet bits = replaceBytes(code, byteArray);
        String encodedBytes = "";
        int numberOfBytesEncoded = 0;
        for (int i = 0; i < 256; i++) {
            if (code[i] != null) {
                numberOfBytesEncoded++;
            }
        }

        // finding number of bytes actually encoded and adding it to the string
        int n = numberOfBytesEncoded;
        int power = 7;
        while (power >= 0) {
            int powerOfTwo = (int)Math.pow(2, power);
            encodedBytes += n / powerOfTwo + "";
            n = n % powerOfTwo;
            power--;
        }

        int count = 0;
        for (int i = 0; i < 256; i++) {
            if (code[i] != null) {
                count++;
                encodedBytes += Integer.toBinaryString(((byte)(i - 128) & 0xFF) + 0x100).substring(1);
                int encodedLength = code[i].length();
                String encodedLengthString = "";
                power = 7;
                while (power >= 0) {
                    int powerOfTwo = (int)Math.pow(2, power);
                    encodedLengthString += encodedLength / powerOfTwo + "";
                    encodedLength = encodedLength % powerOfTwo;
                    power--;
                }
                encodedBytes += encodedLengthString;
                encodedBytes += code[i];
            }
        }

        int numberOfZeros = (encodedBytes.length() + bits.getSize() + 3) % 8;
        numberOfZeros = (8 - numberOfZeros) % 8;
        String numberOfZerosString = "";
        power = 2;
        n = numberOfZeros;
        while (power >= 0) {
            int powerOfTwo = (int)Math.pow(2, power);
            numberOfZerosString += String.valueOf(n / powerOfTwo);
            n = n % powerOfTwo;
            power--;
        }
        encodedBytes = numberOfZerosString + encodedBytes;
        FixedSizeBitSet finalBits = new FixedSizeBitSet(encodedBytes.length() + bits.getSize() + numberOfZeros);
        char[] chars = encodedBytes.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '1') {
                finalBits.getBits().set(i);
            }
        }
        for (int i = chars.length; i < finalBits.getSize() - numberOfZeros; i++) {
            if (bits.getBits().get(i - chars.length) == true) {
                finalBits.getBits().set(i);
            }
        }
        return HelperMethods.bitSetToByteArray(finalBits);
    }

    private static Node constructTree(int[] frequencies) {
        PriorityQueue pq = new PriorityQueue();

        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                pq.enqueue(new Node((byte)(i - 128), frequencies[i], null, null));
            }
        }

        while (pq.getSize() > 1) {
            Node left = pq.dequeue();
            Node right = pq.dequeue();
            Node parent = new Node((byte)0, left.getFrequency() + right.getFrequency(), left, right);
            pq.enqueue(parent);
        }

        return pq.dequeue();
    }

    private static void buildCode(String[] code, Node node, String s) {
        if (!node.isLeaf()) {
            buildCode(code, node.getLeft(), s + "0");
            buildCode(code, node.getRight(), s + "1");
        }
        else {
            code[(int)node.getByte() + 128] = s;
        }
    }

    private static FixedSizeBitSet replaceBytes(String[] codes, byte[] byteArray) {
        BitSet bits = new BitSet();
        int length = 0;
        for (byte b : byteArray) {
            String code = codes[(int)b + 128];
            for (int i = 0; i < code.length(); i++) {
                if (code.charAt(i) == '1') {
                    bits.set(length + i);
                }
            }
            length += code.length();
        }
        FixedSizeBitSet bitSet = new FixedSizeBitSet(length);
        for (int i = 0; i < length; i++) {
            if (bits.get(i) == true) {
                bitSet.getBits().set(i);
            }
        }
        return bitSet;
    }

    public byte[] decompress(byte[] byteArray) {
        final FixedSizeBitSet bits = HelperMethods.bitSetfromByteArray(byteArray);
        final BitSet bitSet = bits.getBits();

        //getting number of trailing zeros and bytes encoded
        int numberOfTrailingZeros = 0;
        int power = 2;
        for (int i = 0; i < 3; i++) {
            numberOfTrailingZeros += (bitSet.get(i) == true ? 1 : 0) * (int)Math.pow(2, power);
            power--;
        }
        int numberOfBytesEncoded = 0;
        power = 7;
        for (int i = 3; i < 11; i++) {
            numberOfBytesEncoded += (bitSet.get(i) == true ? 1 : 0) * (int)Math.pow(2, power);
            power--;
        }

        // retrieving encodings
        int index = 11;
        HashMap<String, Byte> encodings = new HashMap<>();
        for (int i = 0; i < numberOfBytesEncoded; i++) {
            String s = "";
            for (int j = index; j < index + 8; j++) {
                s += bitSet.get(j) == true ? "1" : "0";
            }
            byte b = HelperMethods.parseByte(s);
            int numberOfBits = 0;
            power = 7;
            for (int j = index + 8; j < index + 16; j++) {
                numberOfBits += (bitSet.get(j) == true ? 1 : 0) * (int)Math.pow(2, power);
                power--;
            }
            s = "";
            for (int j = index + 16; j < index + 16 + numberOfBits; j++) {
                s += bitSet.get(j) == true ? "1" : "0";
            }
            encodings.put(s, b);
            index += 16 + numberOfBits;
        }

        // decoding
        LinkedList<Byte> bytes = new LinkedList<>();

        String s = "";
        for (int i = index; i < bits.getSize() - numberOfTrailingZeros; i++) {
            s += bitSet.get(i) == true ? "1" : "0";
            if (encodings.containsKey(s)) {
                bytes.add(encodings.get(s));
                s = "";
            }
        }
        byte[] decoded = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            decoded[i] = bytes.get(i);
        }
        return decoded;
    }
}
