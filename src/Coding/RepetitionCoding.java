package Coding;

import DSA.*;

public class RepetitionCoding implements Encoder, Decoder {

    public byte[] encode(byte[] byteArray) {
        FixedSizeBitSet bits = HelperMethods.bitSetfromByteArray(byteArray);
        FixedSizeBitSet repBits = new FixedSizeBitSet(bits.getSize() * 3);
        for (int i = 0; i < bits.getSize(); i++) {
            if (bits.getBits().get(i) == true) {
                repBits.getBits().set(i * 3);
                repBits.getBits().set(i * 3 + 1);
                repBits.getBits().set(i * 3 + 2);
            }
        }
        return HelperMethods.bitSetToByteArray(repBits);
    }

    public byte[] decode(byte[] byteArray) {
        FixedSizeBitSet repBits = HelperMethods.bitSetfromByteArray(byteArray);
        FixedSizeBitSet bits = new FixedSizeBitSet(repBits.getSize() / 3);
        for (int i = 0; i < bits.getSize(); i++) {
            int numberOfZeros = 0;
            for (int j = i * 3; j < (i + 1) * 3; j++) {
                if (repBits.getBits().get(j) == false) {
                    numberOfZeros++;
                }
            }
            if (numberOfZeros < 2) {
                bits.getBits().set(i);
            }
        }
        return HelperMethods.bitSetToByteArray(bits);
    }
}
