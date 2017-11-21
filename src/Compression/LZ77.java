package Compression;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by Timkabor on 11/20/2017.
 */
public class LZ77 implements Compressor, Decompressor {

    // 12 bits to store maximum offset distance.
    public static final int MAX_WINDOW_SIZE = (1 << 12) - 1;

    // 4 bits to store length of the match.
    public static final int LOOK_AHEAD_BUFFER_SIZE = (1 << 4) - 1;

    // sliding window size
    private int windowSize = MAX_WINDOW_SIZE;

    public LZ77(int windowSize) {
        this.windowSize = Math.min(windowSize, MAX_WINDOW_SIZE);
    }


    public byte[] compress(byte[] data) throws IOException {
        BitOutputStream out = null;
        try {
            out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream("compressed")));
            for (int i = 0; i < data.length;) {
                Match match = findMatchInSlidingWindow(data, i);
                if (match != null) {
                    out.write(Boolean.TRUE);
                    out.write((byte) (match.getDistance() >> 4));
                    out.write((byte) (((match.getDistance() & 0x0F) << 4) | match.getLength()));
                    //System.out.println("<1," + match.getDistance() + ", " + match.getLength() + ">");
                    i = i + match.getLength();
                } else {
                    out.write(Boolean.FALSE);
                    out.write(data[i]);
                    i = i + 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
        File file = new File("compressed");
        byte[] b = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(b);
        return b;
    }


    public byte[] decompress(byte[] data) throws IOException {
        String inputFileName = "compressedData";
        FileOutputStream fos = new FileOutputStream(inputFileName);
        fos.write(data);
        fos.close();
        String outputFileName = "temp";
        BitInputStream inputFileStream = null;
        FileChannel outputChannel = null;
        RandomAccessFile outputFileStream = null;
        try {
            inputFileStream = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFileName)));
            outputFileStream = new RandomAccessFile(outputFileName, "rw");
            outputChannel = outputFileStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1);
            try {
                while (true) {// when end of file reached, inputStream throws End Of file Exception
                    int flag = inputFileStream.read();
                    if (flag == 0) {
                        buffer.clear();
                        buffer.put(inputFileStream.readByte());
                        buffer.flip();
                        outputChannel.write(buffer, outputChannel.size());
                        outputChannel.position(outputChannel.size());
                    } else {
                        int byte1 = inputFileStream.read(8);
                        int byte2 = inputFileStream.read(8);
                        int distance = (byte1 << 4) | (byte2 >> 4);
                        int length = (byte2 & 0x0f);
                        for (int i = 0; i < length; i++) {
                            buffer.clear();
                            outputChannel.read(buffer, outputChannel.position() - distance);
                            buffer.flip();
                            outputChannel.write(buffer, outputChannel.size());
                            outputChannel.position(outputChannel.size());
                        }
                    }
                }
            } catch (EOFException e) {
                // ignore. means we reached the end of the file. and we are done.
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputFileStream.close();
            outputChannel.close();
            inputFileStream.close();
        }
        byte[] b = new byte[(int) outputFileStream.length()];
        outputFileStream.readFully(b);
        return b;

    }

    private Match findMatchInSlidingWindow(byte[] data, int currentIndex) {
        Match match = new Match();
        int end = Math.min(currentIndex + LOOK_AHEAD_BUFFER_SIZE, data.length + 1);
        for (int j = currentIndex + 2; j < end; j++) {
            int startIndex = Math.max(0, currentIndex - windowSize);
            byte[] bytesToMatch = Arrays.copyOfRange(data, currentIndex, j);
            for (int i = startIndex; i < currentIndex; i++) {
                int repeat = bytesToMatch.length / (currentIndex - i);
                int remaining = bytesToMatch.length % (currentIndex - i);

                byte[] tempArray = new byte[(currentIndex - i) * repeat + (i + remaining - i)];
                int m = 0;
                for (; m < repeat; m++) {
                    int destPos = m * (currentIndex - i);
                    System.arraycopy(data, i, tempArray, destPos, currentIndex - i);
                }
                int destPos = m * (currentIndex - i);
                System.arraycopy(data, i, tempArray, destPos, remaining);
                if (Arrays.equals(tempArray, bytesToMatch) && bytesToMatch.length > match.getLength()) {
                    match.setLength(bytesToMatch.length);
                    match.setDistance(currentIndex - i);
                }
            }
        }
        if (match.getLength() > 0 && match.getDistance() > 0)
            return match;
        return null;
    }


}
