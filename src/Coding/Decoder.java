package Coding;

/**
 * Decodes the encoded data, so that it can be read by the user.
 */
public interface Decoder {
    /**
     * Decodes previously encoded message.
     * @param encodedMessage Encoded data.
     * @return Readable data.
     */
    public byte[] decode(byte[] encodedMessage);
}
