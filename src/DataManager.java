import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import DSA.FixedSizeBitSet;
import DSA.HelperMethods;

import Coding.*;
import Compression.*;

/**
 * Created by markzaharov on 17.11.2017.
 */
public class DataManager {
    Encoder encoder;
    Decoder decoder;
    Compressor compressor;
    Decompressor decompressor;

    public DataManager(Encoder encoder, Decoder decoder, Compressor compressor, Decompressor decompressor) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.compressor = compressor;
        this.decompressor = decompressor;
    }

    byte[] compressAndEncodeMessage(String message) {
        byte[] compressedMessage;
        try {
            compressedMessage = compressor.compress(message.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        byte[] encodedMessage = encoder.encode(compressedMessage);
        byte[] fullCompressedMessage = new byte[encodedMessage.length + 1];
        fullCompressedMessage[0] = 0;
        for (int i = 1; i < fullCompressedMessage.length; i++) {
            fullCompressedMessage[i] = encodedMessage[i - 1];
        }
        return fullCompressedMessage;
    }

    byte[] compressAndEncodeFileAtPath(String path) {
        File file = new File(path);
        String name = file.getName();
        int length = name.length();
        byte[] compressedFileData;
        try {
            compressedFileData = compressor.compress(Files.readAllBytes(file.toPath()));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        byte[] encodedFileData = encoder.encode(compressedFileData);

        byte[] nameData = name.getBytes();
        byte lengthByte = HelperMethods.parseByte(HelperMethods.unsignedIntToString(length));
        byte[] fullCompressedFile = new byte[1 + length + encodedFileData.length];
        fullCompressedFile[0] = lengthByte;
        for (int i = 1; i < length + 1; i++) {
            fullCompressedFile[i] = nameData[i - 1];
        }
        for (int i = length + 1; i < fullCompressedFile.length; i++) {
            fullCompressedFile[i] = encodedFileData[i - length - 1];
        }
        return fullCompressedFile;
    }

    String getMessage(byte[] encodedMessage) {
        if (encodedMessage[0] == 0) {
            byte[] bytes = new byte[encodedMessage.length - 1];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = encodedMessage[i + 1];
            }
            byte[] decodedMessage = decoder.decode(bytes);
            String decompressedString;
            try {
                decompressedString = decompressor.decompress(decodedMessage).toString();
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return decompressedString;
        } else {
            int length = 0;
            FixedSizeBitSet lengthBits = HelperMethods.bitSetfromByteArray(new byte[] { encodedMessage[0] });
            for (int i = 0, power = 7; i < 8; i++, power--) {
                length += (lengthBits.getBits().get(i) == true ? 1 : 0) * (int)Math.pow(2, power);
            }
            byte[] nameBytes = new byte[length];
            for (int i = 0; i < length; i++) {
                nameBytes[i] = encodedMessage[i + 1];
            }
            String name = nameBytes.toString();
            byte[] fileBytes = new byte[encodedMessage.length - length - 1];
            for (int i = 0; i < fileBytes.length; i++) {
                fileBytes[i] = encodedMessage[i + length + 1];
            }
            byte[] decodedFileBytes = decoder.decode(fileBytes);
            byte[] decompressedFileBytes;
            try {
                decompressedFileBytes = decompressor.decompress(decodedFileBytes);
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            try (FileOutputStream fos = new FileOutputStream(name)) {
                fos.write(decompressedFileBytes);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return("брат те картинка пришла вконтакте");
        }
    }
}
