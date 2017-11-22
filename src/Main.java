import Coding.CyclicRedundancyCheck;
import Compression.LZ77;

import javax.print.DocFlavor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import Compression.*;
import Networking.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter initial command:");
        String inputs = sc.nextLine();

        try {
            parseInputs(inputs);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    static void parseInputs(String str) throws IOException {
        String[] parts = str.split(" ");

        if (parts.length == 0) {
            return;
        }

        if (parts[0].equals("server")) {
            Server server = new Server(12345);
            new Thread(() -> server.run()).start();

            new Client("127.0.0.1", 12345).run();
        } else if (parts.length > 1
                && parts[0].equals("client")) {
            String host = parts[1];

            new Client(host, 12345).run();
        }
    }
}
