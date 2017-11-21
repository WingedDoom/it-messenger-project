package Coding;

import java.util.LinkedList;

import DSA.FixedSizeBitSet;
import DSA.HelperMethods;

/**
 * Created by markzaharov on 16.11.2017.
 */
public class Hamming implements Encoder, Decoder {

    public byte[] encode(byte[] byteArray) {
        FixedSizeBitSet bits = HelperMethods.bitSetfromByteArray(byteArray);
        int i = 0;
        int j = 1;
        while (i < bits.getSize()) {
            if (isPowerOfTwo(j)) {
                j++;
            } else {
                i++;
                j++;
            }
        }
        FixedSizeBitSet bitsP = new FixedSizeBitSet(j - 1);
        int k = 0;
        for (i = 0; i < j; i++) {
            if (!isPowerOfTwo(i + 1)) {
                if (bits.getBits().get(k) == true) {
                    bitsP.getBits().set(i);
                }
                k++;
            }
        }
        for (i = 0; i < j; i++) {
            if (isPowerOfTwo(i + 1)) {
                if (checkParity(i, i + 1, bitsP)) {
                    bitsP.getBits().set(i);
                }
            }
        }
        int numberOfTrailingZeros = (bitsP.getSize() + 3) % 8;
        numberOfTrailingZeros = (8 - numberOfTrailingZeros) % 8;
        FixedSizeBitSet bitsFinal = new FixedSizeBitSet(3 + bitsP.getSize() + numberOfTrailingZeros);
        int power = 2;
        while (power >= 0) {
            int powerOfTwo = (int)Math.pow(2, power);
            if (numberOfTrailingZeros / powerOfTwo == 1) {
                bitsFinal.getBits().set(2 - power);
                numberOfTrailingZeros = numberOfTrailingZeros % powerOfTwo;
            }
            power--;
        }
        for (i = 3; i < bitsP.getSize() + 3; i++) {
            if (bitsP.getBits().get(i - 3) == true) {
                bitsFinal.getBits().set(i);
            }
        }
        return HelperMethods.bitSetToByteArray(bitsFinal);
    }

    private static boolean checkParity(int index, int step, FixedSizeBitSet bits) {
        int sum = 0;
        int i = index;
        while (i < bits.getSize()) {
            for (int j = i; j < i + step && j < bits.getSize(); j++) {
                sum += bits.getBits().get(j) == true ? 1 : 0;
            }
            i += 2 * step;
        }
        return sum % 2 == 1;
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
        int numberOfTrailingZeros = 0;
        int power = 2;
        for (int i = 0; i < 3; i++) {
            numberOfTrailingZeros += (bits.getBits().get(i) == true ? 1 : 0) * (int)Math.pow(2, power);
            power--;
        }

        LinkedList<Integer> brokenParityBits = new LinkedList<>();
        for (int i = 3; i < bits.getSize() - numberOfTrailingZeros; i++) {
            if (isPowerOfTwo(i - 2)) {
                if (checkParity(i, i - 2, bits)) {
                    brokenParityBits.add(i);
                }
            }
        }

        if (!brokenParityBits.isEmpty()) {
            int sum = 0;
            for (int i : brokenParityBits) {
                sum += i;
            }
            bits.getBits().flip(sum);
        }

        int i = 1;
        int j = 0;
        while (i < bits.getSize() - numberOfTrailingZeros - 2) {
            if (isPowerOfTwo(i)) {
                j++;
                i++;
            } else {
                i++;
            }
        }
        int initialSize = bits.getSize() - numberOfTrailingZeros - 3 - j;
        FixedSizeBitSet bitsFinal = new FixedSizeBitSet(initialSize);
        j = 3;
        for (i = 0; i < initialSize; i++) {
            int k = 0;
            while (isPowerOfTwo(j + k - 2)) {
                k++;
            }
            if (bits.getBits().get(j + k) == true) {
                bitsFinal.getBits().set(i);
            }
            j = j + k + 1;
        }
        return HelperMethods.bitSetToByteArray(bitsFinal);
    }
}
