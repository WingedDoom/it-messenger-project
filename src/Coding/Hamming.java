package Coding;

import java.util.LinkedList;

import DSA.FixedSizeBitSet;
import DSA.HelperMethods;

/**
 * Created by markzaharov on 16.11.2017.
 */
public class Hamming implements Encoder, Decoder {

    public byte[] encode(byte[] byteArray) {
        int finalNumberOfBytes = (int)Math.ceil(byteArray.length * 1.75);
        FixedSizeBitSet bits = new FixedSizeBitSet(finalNumberOfBytes * 8);
        int index = 0;
        for (byte b : byteArray) {
            FixedSizeBitSet bitsFP = new FixedSizeBitSet(7);
            String s = HelperMethods.byteToString(b);
            for (int i = 0, k = 0; i < 7; i++) {
                if (!isPowerOfTwo(i + 1)) {
                    if (s.charAt(k) == '1') {
                        bitsFP.getBits().set(i);
                    }
                    k++;
                }
            }
            for (int i = 0; i < 7; i++) {
                if (isPowerOfTwo(i + 1)) {
                    if (!checkParity(i, i + 1, bitsFP)) {
                        bitsFP.getBits().set(i);
                    }
                }
            }
            FixedSizeBitSet bitsSP = new FixedSizeBitSet(7);
            for (int i = 0, k = 4; i < 7; i++) {
                if (!isPowerOfTwo(i + 1)) {
                    if (s.charAt(k) == '1') {
                        bitsSP.getBits().set(i);
                    }
                    k++;
                }
            }
            for (int i = 0; i < 7; i++) {
                if (isPowerOfTwo(i + 1)) {
                    if (!checkParity(i, i + 1, bitsSP)) {
                        bitsSP.getBits().set(i);
                    }
                }
            }
            for (int i = 0; i < 7; i++) {
                if (bitsFP.getBits().get(i) == true) {
                    bits.getBits().set(i + index);
                } else {
                }
            }
            for (int i = 0; i < 7; i++) {
                if (bitsSP.getBits().get(i) == true) {
                    bits.getBits().set(i + 7 + index);
                }
            }
            index += 14;
        }
        return HelperMethods.bitSetToByteArray(bits);
    }

    private static boolean checkParity(int index, int step, FixedSizeBitSet  bits) {
        int sum = 0;
        int i = index;
        while (i < bits.getSize()) {
            for (int j = i; j < i + step && j < bits.getSize(); j++) {
                sum += bits.getBits().get(j) ? 1 : 0;
            }
            i += 2 * step;
        }
        return sum % 2 == 0;
    }

    private static boolean checkParity(int index, int endIndex, int step, FixedSizeBitSet  bits) {
        int sum = 0;
        int i = index;
        while (i <= endIndex) {
            for (int j = i; j < i + step && j < bits.getSize(); j++) {
                sum += bits.getBits().get(j) == true ? 1 : 0;
            }
            i += 2 * step;
        }
        return sum % 2 == 0;
    }

    private static boolean isPowerOfTwo(int n) {
        while (n % 2 != 1) {
            n /= 2;
        }
        if (n == 1) {
            return true;
        } else {
            return false;
        }
    }

    public byte[] decode(byte[] byteArray) {
        FixedSizeBitSet bits = HelperMethods.bitSetfromByteArray(byteArray);
        FixedSizeBitSet decodedBits = new FixedSizeBitSet((int)Math.floor(byteArray.length / 1.75) * 8);

        for (int i = 0; i < bits.getSize() / 7; i++) {
            int startIndex = i * 7;
            int endIndex = i * 7 + 7;
            LinkedList<Integer> brokenParityBits = new LinkedList<>();
            for (int j = startIndex; j < endIndex; j++) {
                if (isPowerOfTwo(j - startIndex + 1)) {
                    if (!checkParity(j, endIndex, j - startIndex + 1, bits)) {
                        brokenParityBits.add(j - startIndex + 1);
                    }
                }
            }
            int paritySum = 0;
            for (int bit : brokenParityBits) {
                paritySum += bit;
            }
            if (paritySum != 0) {
                if (bits.getBits().get(startIndex + paritySum - 1)) {
                    bits.getBits().clear(startIndex + paritySum - 1);
                } else {
                    bits.getBits().set(startIndex + paritySum - 1);
                }
            }
            for (int j = startIndex, k = i * 4; j < endIndex; j++) {
                if (!isPowerOfTwo(j - startIndex + 1)) {
                    if (bits.getBits().get(j)) {
                        decodedBits.getBits().set(k);
                    }
                    k++;
                }
            }
        }

        return HelperMethods.bitSetToByteArray(decodedBits);
    }
}

