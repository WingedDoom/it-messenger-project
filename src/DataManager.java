import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import DSA.FixedSizeBitSet;
import DSA.HelperMethods;

/**
 * Created by markzaharov on 17.11.2017.
 */
public class DataManager {
    Encoder encoder;
    Decoder decoder;

    public DataManager(Encoder encoder, Decoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    byte[] encodeMessage(String message) {
        byte[] encodedMessage = encoder.encode(message.getBytes());
        byte[] fullEncodedMessage = new byte[encodedMessage.length + 1];
        fullEncodedMessage[0] = 0;
        for (int i = 1; i < fullEncodedMessage.length; i++) {
            fullEncodedMessage[i] = encodedMessage[i - 1];
        }
        return fullEncodedMessage;
    }

    byte[] encodeFileAtPath(String path) {
        File file = new File(path);
        String name = file.getName();
        int length = name.length();
        byte[] encodedFileData;
        try {
            encodedFileData = encoder.encode(Files.readAllBytes(file.toPath()));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        byte[] nameData = name.getBytes();
        byte lengthByte = HelperMethods.parseByte(HelperMethods.unsignedIntToString(length));
        byte[] fullEncodedFile = new byte[1 + length + encodedFileData.length];
        fullEncodedFile[0] = lengthByte;
        for (int i = 1; i < length + 1; i++) {
            fullEncodedFile[i] = nameData[i - 1];
        }
        for (int i = length + 1; i < fullEncodedFile.length; i++) {
            fullEncodedFile[i] = encodedFileData[i - length - 1];
        }
        return fullEncodedFile;
    }

    String getMessage(byte[] encodedMessage) {
        if (encodedMessage[0] == 0) {
            byte[] bytes = new byte[encodedMessage.length - 1];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = encodedMessage[i + 1];
            }
            return bytes.toString();
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
            try (FileOutputStream fos = new FileOutputStream(name)) {
                fos.write(fileBytes);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return("брат те картинка пришла вконтакте");
        }
    }
}
