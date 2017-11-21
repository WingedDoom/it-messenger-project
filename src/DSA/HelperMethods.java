package DSA;

import java.util.BitSet;

/**
 * Created by markzaharov on 16.11.2017.
 */
public class HelperMethods {
    static public FixedSizeBitSet bitSetfromByteArray(byte[] bytes) {
        FixedSizeBitSet bits = new FixedSizeBitSet(bytes.length * 8);
        for (int i = 0; i < bytes.length; i++) {
            String stringRepresentation = Integer.toBinaryString((bytes[i] & 0xFF) + 0x100).substring(1);
            for (int j = i * 8; j < (i + 1) * 8; j++) {
                if (stringRepresentation.charAt(j % 8) == '1') {
                    bits.getBits().set(j);
                }
            }
        }
        return bits;
    }

    static public byte[] bitSetToByteArray(FixedSizeBitSet tuple) {
        byte[] bytes = new byte[tuple.getSize() / 8];
        for (int i = 0; i < bytes.length; i++) {
            String stringRepresentation = "";
            for (int j = i * 8; j < (i + 1) * 8; j++) {
                stringRepresentation += tuple.getBits().get(j) == true ? "1" : "0";
            }
            bytes[i] = parseByte(stringRepresentation);
        }
        return bytes;
    }

    static public byte parseByte(String s) {
        if (s.charAt(0) == '1') {
            int num = 0;
            int power = 6;
            for (int i = 1; i < 8; i++) {
                num += (s.charAt(i) == '1' ? 1 : 0) * (int)Math.pow(2, power);
                power--;
            }
            num = num - 128;
            return (byte)num;
        } else {
            return Byte.parseByte(s, 2);
        }
    }

    static public String unsignedIntToString(int n) {
        String s = "";
        int power = 7;
        while (power >= 0) {
            int powerOfTwo = (int) Math.pow(2, power);
            s += n / powerOfTwo;
            n %= powerOfTwo;
            power--;
        }
        return s;
    }

    static public String byteToString(byte b) {
        String s = "";
        int power = 6;
        if (b < 0) {
            s += "1";
            b += 128;
        } else {
            s += "0";
        }
        while (power >= 0) {
            int powerOfTwo = (int)Math.pow(2, power);
            s += b / powerOfTwo;
            b %= powerOfTwo;
            power--;
        }
        return s;
    }
}
