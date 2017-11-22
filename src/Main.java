import Coding.CyclicRedundancyCheck;
import Compression.LZ77;

import javax.print.DocFlavor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import Compression.*;
import Networking.*;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(12345);
        new Thread(() -> server.run()).start();

        new Client("127.0.0.1", 12345).run();
    }
}
