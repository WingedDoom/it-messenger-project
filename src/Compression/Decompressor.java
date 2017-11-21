package Compression;

import java.io.IOException;

/**
 * Created by Timkabor on 11/17/2017.
 */
public interface Decompressor {
    /**
     * Decompress previously compressed message.
     * @param message Compressed data.
     * @return Readable data.
     */
    public byte[] decompress(byte[] message) throws IOException;
}
