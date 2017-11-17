package Coding;

/**
 * Encodes raw message data using some encoding algorithm, so that it can be sent and decoded later.
 */
public interface Encoder {
    /**
     * Encodes given array of bytes, and returns encoded array of bytes.
     * @param message Raw data.
     * @return Encoded data.
     */
    public byte[] encode(byte[] message);
}
