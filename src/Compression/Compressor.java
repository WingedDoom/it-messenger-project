package Compression;

import java.io.IOException;

/**
 * Created by Timkabor on 11/17/2017.
 */
public interface Compressor {
    /**
     * Compress given array of bytes, and returns compresses array of bytes.
     * @param message Raw data.
     * @return Encoded data.
     */
    public byte[] compress(byte[] message) throws IOException;
}
